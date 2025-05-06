/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dummy_database.ar

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.opengl.GLSurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.dummy_database.R
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.Config
import com.example.dummy_database.ar.helpers.SnackbarHelper
import com.example.dummy_database.ar.helpers.TapHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.example.dummy_database.ui.network.NetworkConnectivityObserver
import com.example.dummy_database.ui.network.ConnectivityStatus
import kotlinx.coroutines.flow.collect

/**
 * Manages the Android View-based UI elements for the AR business card screen.
 * Finds UI elements from the layout, sets up event listeners for buttons (social links, TTS playback),
 * displays subtitles, manages an offline connectivity banner. Interacts with the owning Activity
 * and external utilities like TTSUtil and NetworkConnectivityObserver.
 * This class acts as a presenter for the view layer. Based on Google ARCore HelloAR example code's view management pattern.
 *
 *Contributions=>
 *
 *Sahra:
 *
 *Newton: Playing TTS for introduction, experience and other buttons. adding stop button functionality.
 *
 */






/** Contains UI elements for Hello AR. */
class HelloArView(val activity: HelloArActivity) : DefaultLifecycleObserver {
  val root = View.inflate(activity, R.layout.activity_main, null)
  val surfaceView = root.findViewById<GLSurfaceView>(R.id.surfaceview)
  val settingsButton =
    root.findViewById<ImageButton>(R.id.settings_button).apply {
      setOnClickListener { v ->
        PopupMenu(activity, v).apply {
          setOnMenuItemClickListener { item ->
            when (item.itemId) {
              R.id.depth_settings -> launchDepthSettingsMenuDialog()
              R.id.instant_placement_settings -> launchInstantPlacementSettingsMenuDialog()
              else -> null
            } != null
          }
          inflate(R.menu.settings_menu)
          show()
        }
      }
    }

  // Lazy-fetch text fields passed via Intent extras
  private val educationTextFetch: String by lazy {
    activity.intent.getStringExtra("education") ?: "No Education Data"
  }
  private val experienceTextFetch: String by lazy {
    activity.intent.getStringExtra("experience") ?: "No Experience Data"
  }
  private val hobbiesTextFetch: String by lazy {
    activity.intent.getStringExtra("hobbies") ?: "No Hobbies Data"
  }
  private val introductionText: String by lazy {
    activity.intent.getStringExtra("introduction") ?: "No Introduction Data"
  }
  private val avatarId: String by lazy {
    activity.intent.getStringExtra("avatar_id") ?: "default"
  }
  // tracks the current speaking section
  private var currentSpeakingSection: String? = null

  fun hideButtons() {
    linkedInButton.visibility = View.GONE
    githubButton.visibility = View.GONE
    emailButton.visibility = View.GONE
    introButton.visibility = View.GONE
    educationButton.visibility = View.GONE
    experienceButton.visibility = View.GONE
    hobbiesButton.visibility = View.GONE
    stopButton.visibility = View.GONE
  }

  /** True if any of the TTS subtitle TextViews are currently visible. */
  val areSubtitlesShowing: Boolean
    get() = listOf(educationText, experienceText, hobbiesText)
      .any { it.visibility == View.VISIBLE }

  // Stops any currently playing TTS audio and hides all subtitle TextViews
  fun stopTTSAndHideAllSubtitles() {
    com.example.dummy_database.tts.TTSUtil.stop()
    currentSpeakingSection = null
    educationText.visibility = View.GONE
    experienceText.visibility = View.GONE
    hobbiesText.visibility = View.GONE
  }

  private fun showAndHideSubtitle(textView: TextView) {
    activity.runOnUiThread {
      textView.visibility = View.VISIBLE
      textView.removeCallbacks(null) // Remove any previously posted hide tasks
      textView.postDelayed({
        textView.visibility = View.GONE
      }, 5000)
    }
  }

  private fun normalizeUrl(raw: String): String {
    val trimmed = raw.trim()
    return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
      trimmed
    } else {
      "https://$trimmed"
    }
  }

  private val linkedInLink: String by lazy {
    val rawLink = activity.intent.getStringExtra("linkedInUrl") ?: "No LinkedIn URL"
    if (rawLink == "No LinkedIn URL") rawLink else normalizeUrl(rawLink)
  }

  private val githubLink: String by lazy {
    val rawLink = activity.intent.getStringExtra("githubUrl") ?: "No GitHub URL"
    if (rawLink == "No GitHub URL") rawLink else normalizeUrl(rawLink)
  }

  private val emailAddress: String by lazy {
    activity.intent.getStringExtra("email") ?: "No Email Address"
  }

  private val voicePreference: String by lazy {
    activity.intent.getStringExtra("voicePreference") ?: "No Voice Preference"
  }

  //Find the TextView used as the offline banner
  private val offlineBanner: TextView =
    root.findViewById(R.id.offline_banner)

  // Observes network and toggles offline banner visibility
  private val connectivityObserver = NetworkConnectivityObserver(activity)

  override fun onResume(owner: LifecycleOwner) {
    super.onResume(owner)
    surfaceView.onResume()

    // start observing connectivity
    activity.lifecycleScope.launchWhenResumed {
      connectivityObserver.observe().collect { status ->
        if (status == ConnectivityStatus.Available) {
          // hide banner
          offlineBanner.visibility = View.GONE
        } else {
          // show banner with custom text
          offlineBanner.text =
            "You are offline! Please, connect to the internet."
          offlineBanner.visibility = View.VISIBLE
        }
      }
    }
  }

  override fun onPause(owner: LifecycleOwner) {
    super.onPause(owner)
    surfaceView.onPause()
  }

  //buttons to launch external links: initialized below
  val linkedInButton =
    root.findViewById<ImageButton>(R.id.linkedin_button).apply {
      setOnClickListener {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedInLink))
        val chooser = Intent.createChooser(intent, "Open link with…")
        try {
          activity.startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
          Toast.makeText(
            activity,
            "No app installed can open:\n$linkedInLink",
            Toast.LENGTH_LONG
          ).show()
        }
      }
    }

  val githubButton =
    root.findViewById<ImageButton>(R.id.github_button).apply {
      setOnClickListener {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubLink))
        val chooser = Intent.createChooser(intent, "Open link with…")
        try {
          activity.startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
          Toast.makeText(
            activity,
            "No app installed can open:\n$githubLink",
            Toast.LENGTH_LONG
          ).show()
        }
      }
    }

  val emailButton =
    root.findViewById<ImageButton>(R.id.email_button).apply {
      setOnClickListener {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
          data = Uri.parse("mailto:$emailAddress")
        }
        try {
          activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
          Toast.makeText(activity, "No email app found.", Toast.LENGTH_LONG).show()
        }

      }
    }

  // sets up stop button for TTS playback
  private val stopButton: ImageButton = root.findViewById<ImageButton>(R.id.stop_button).apply {
    visibility = View.GONE
    setOnClickListener {
      stopTTSAndHideAllSubtitles()
      visibility = View.GONE
    }
  }

  val introButton = root.findViewById<Button>(R.id.intro_button)
  val introText = root.findViewById<TextView>(R.id.intro_text)

  // Initialization block for the Intro button's click listener
  init {
    // Set up the TTS button click behavior
    introButton.setOnClickListener {
      // If already speaking this section, ignore repeat clicks
      if (currentSpeakingSection == "intro") return@setOnClickListener

      // Stop any ongoing TTS playback and hide previous subtitles
      stopTTSAndHideAllSubtitles()

      // Mark this section as currently speaking
      currentSpeakingSection = "intro"
      introText.text = introductionText //generate subtitle text

      // Launch a coroutine to perform TTS synthesis
      activity.lifecycleScope.launch(Dispatchers.IO) {
        com.example.dummy_database.tts.TTSUtil.synthesizeAndPlay(
          activity,
          introductionText,
          activity.intent.getStringExtra("voicePreference") ?: "FEMALE",
          // display the stop button and subtitle
          onStart = {
            stopButton.visibility = View.VISIBLE
            showAndHideSubtitle(introText)
          },
          // upon completion, hide the stop button
          onComplete = {
            stopButton.visibility = View.GONE
            currentSpeakingSection = null
          }
        )
      }
    }
  }


  val educationButton = root.findViewById<Button>(R.id.education_button)
  val educationText = root.findViewById<TextView>(R.id.education_text)

  // Initialization block for the Education button's click listener
  init {
    educationButton.setOnClickListener {
      if (currentSpeakingSection == "education") return@setOnClickListener

      //Stop any ongoing TTS playback and hide previous subtitles
      stopTTSAndHideAllSubtitles()

      currentSpeakingSection = "education"  // Mark this section as currently speaking
      educationText.text = educationTextFetch   //generate subtitle text

      // Launch a coroutine to perform TTS synthesis
      activity.lifecycleScope.launch(Dispatchers.IO) {
        com.example.dummy_database.tts.TTSUtil.synthesizeAndPlay(
          activity,
          educationTextFetch,
          activity.intent.getStringExtra("voicePreference") ?: "FEMALE",
          onStart = {
            // upon start, display the stop button and subtitle
            stopButton.visibility = View.VISIBLE
            showAndHideSubtitle(educationText)
          },
          onComplete = {
            // upon completion, hide the stop button
            stopButton.visibility = View.GONE
            currentSpeakingSection = null
          }
        )
      }
    }
  }

  val experienceButton = root.findViewById<Button>(R.id.experience_button)
  val experienceText = root.findViewById<TextView>(R.id.experience_text)

  // Initialization block for the Experience button's click listener
  init {
    experienceButton.setOnClickListener {
      if (currentSpeakingSection == "experience") return@setOnClickListener

      stopTTSAndHideAllSubtitles()

      currentSpeakingSection = "experience"
      experienceText.text = experienceTextFetch

      activity.lifecycleScope.launch(Dispatchers.IO) {
        com.example.dummy_database.tts.TTSUtil.synthesizeAndPlay(
          activity,
          experienceTextFetch,
          activity.intent.getStringExtra("voicePreference") ?: "FEMALE",
          onStart = {
            stopButton.visibility = View.VISIBLE
            showAndHideSubtitle(experienceText)
          },
          onComplete = {
            stopButton.visibility = View.GONE
            currentSpeakingSection = null
          }
        )
      }
    }
  }

  val hobbiesButton = root.findViewById<Button>(R.id.hobbies_button)
  val hobbiesText = root.findViewById<TextView>(R.id.hobbies_text)

  // Initialization block for the Hobbies button's click listener
  init {
    hobbiesButton.setOnClickListener {
      if (currentSpeakingSection == "hobbies") return@setOnClickListener

      stopTTSAndHideAllSubtitles()

      currentSpeakingSection = "hobbies"
      hobbiesText.text = hobbiesTextFetch

      activity.lifecycleScope.launch(Dispatchers.IO) {
        com.example.dummy_database.tts.TTSUtil.synthesizeAndPlay(
          activity,
          hobbiesTextFetch,
          activity.intent.getStringExtra("voicePreference") ?: "FEMALE",
          onStart = {
            stopButton.visibility = View.VISIBLE
            showAndHideSubtitle(hobbiesText)
          },
          onComplete = {
            stopButton.visibility = View.GONE
            currentSpeakingSection = null
          }
        )
      }
    }
  }

  fun showButtons() {
    linkedInButton.visibility = View.VISIBLE
    githubButton.visibility = View.VISIBLE
    emailButton.visibility = View.VISIBLE
    introButton.visibility = View.VISIBLE
    educationButton.visibility = View.VISIBLE
    experienceButton.visibility = View.VISIBLE
    hobbiesButton.visibility = View.VISIBLE
    stopButton.visibility = View.GONE
  }

  val session
    get() = activity.arCoreSessionHelper.session

  val snackbarHelper = SnackbarHelper()
  val tapHelper = TapHelper(activity).also { surfaceView.setOnTouchListener(it) }


  /**
   * Shows a pop-up dialog on the first tap in HelloARRenderer, determining whether the user wants
   * to enable depth-based occlusion. The result of this dialog can be retrieved with
   * DepthSettings.useDepthForOcclusion().
   */
  fun showOcclusionDialogIfNeeded() {
    val session = session ?: return
    val isDepthSupported = session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)
    if (!activity.depthSettings.shouldShowDepthEnableDialog() || !isDepthSupported) {
      return // Don't need to show dialog.
    }

    // Asks the user whether they want to use depth-based occlusion.
    AlertDialog.Builder(activity)
      .setTitle(R.string.options_title_with_depth)
      .setMessage(R.string.depth_use_explanation)
      .setPositiveButton(R.string.button_text_enable_depth) { _, _ ->
        activity.depthSettings.setUseDepthForOcclusion(true)
      }
      .setNegativeButton(R.string.button_text_disable_depth) { _, _ ->
        activity.depthSettings.setUseDepthForOcclusion(false)
      }
      .show()
  }

  private fun launchInstantPlacementSettingsMenuDialog() {
    val resources = activity.resources
    val strings = resources.getStringArray(R.array.instant_placement_options_array)
    val checked = booleanArrayOf(activity.instantPlacementSettings.isInstantPlacementEnabled)
    AlertDialog.Builder(activity)
      .setTitle(R.string.options_title_instant_placement)
      .setMultiChoiceItems(strings, checked) { _, which, isChecked -> checked[which] = isChecked }
      .setPositiveButton(R.string.done) { _, _ ->
        val session = session ?: return@setPositiveButton
        activity.instantPlacementSettings.isInstantPlacementEnabled = checked[0]
        activity.configureSession(session)
      }
      .show()
  }

  /** Shows checkboxes to the user to facilitate toggling of depth-based effects. */
  private fun launchDepthSettingsMenuDialog() {
    val session = session ?: return

    // Shows the dialog to the user.
    val resources: Resources = activity.resources
    val checkboxes =
      booleanArrayOf(
        activity.depthSettings.useDepthForOcclusion(),
        activity.depthSettings.depthColorVisualizationEnabled()
      )
    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
      // With depth support, the user can select visualization options.
      val stringArray = resources.getStringArray(R.array.depth_options_array)
      AlertDialog.Builder(activity)
        .setTitle(R.string.options_title_with_depth)
        .setMultiChoiceItems(stringArray, checkboxes) { _, which, isChecked ->
          checkboxes[which] = isChecked
        }
        .setPositiveButton(R.string.done) { _, _ ->
          activity.depthSettings.setUseDepthForOcclusion(checkboxes[0])
          activity.depthSettings.setDepthColorVisualizationEnabled(checkboxes[1])
        }
        .show()
    } else {
      // Without depth support, no settings are available.
      AlertDialog.Builder(activity)
        .setTitle(R.string.options_title_without_depth)
        .setPositiveButton(R.string.done) { _, _ -> /* No settings to apply. */ }
        .show()
    }
  }

}
