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

  // In HelloArView.kt, near the top inside the class body (or in an init block):
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
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$emailAddress"))
        activity.startActivity(intent)
      }
    }



  val educationButton = root.findViewById<Button>(R.id.education_button)
  val educationText = root.findViewById<TextView>(R.id.education_text)

  init {
    educationButton.setOnClickListener {
      educationText.text = educationTextFetch
      educationText.visibility = View.VISIBLE
      educationText.postDelayed({
        educationText.visibility = View.GONE
      }, 5000)
      // Trigger TTS for Education:
      activity.lifecycleScope.launch(Dispatchers.IO) {
        com.example.dummy_database.tts.synthesizeAndPlay(
          activity,
          educationTextFetch,
          // Use the voice preference fetched from the Intent extra
          activity.intent.getStringExtra("voicePreference") ?: "FEMALE"
        )
      }
    }
  }

  val experienceButton = root.findViewById<Button>(R.id.experience_button)
  val experienceText = root.findViewById<TextView>(R.id.experience_text)

  init {
    experienceButton.setOnClickListener {
      experienceText.text = experienceTextFetch
      experienceText.visibility = View.VISIBLE
      experienceText.postDelayed({
        experienceText.visibility = View.GONE
      }, 5000)
      // Trigger TTS for Experience:
      activity.lifecycleScope.launch(Dispatchers.IO) {
        com.example.dummy_database.tts.synthesizeAndPlay(
          activity,
          experienceTextFetch,
          activity.intent.getStringExtra("voicePreference") ?: "FEMALE"
        )
      }
    }
  }

  val hobbiesButton = root.findViewById<Button>(R.id.hobbies_button)
  val hobbiesText = root.findViewById<TextView>(R.id.hobbies_text)

  init {
    hobbiesButton.setOnClickListener {
      hobbiesText.text = hobbiesTextFetch
      hobbiesText.visibility = View.VISIBLE
      // Hide the text after 3 seconds (3000 milliseconds)
      hobbiesText.postDelayed({
        hobbiesText.visibility = View.GONE
      }, 5000)
      // Trigger TTS for Hobbies:
      activity.lifecycleScope.launch(Dispatchers.IO) {
        com.example.dummy_database.tts.synthesizeAndPlay(
          activity,
          hobbiesTextFetch,
          activity.intent.getStringExtra("voicePreference") ?: "FEMALE"
        )
      }
    }
  }

  fun showButtons() {
    linkedInButton.visibility = View.VISIBLE
    githubButton.visibility = View.VISIBLE
    emailButton.visibility = View.VISIBLE
    educationButton.visibility = View.VISIBLE
    experienceButton.visibility = View.VISIBLE
    hobbiesButton.visibility = View.VISIBLE
  }

  val session
    get() = activity.arCoreSessionHelper.session

  val snackbarHelper = SnackbarHelper()
  val tapHelper = TapHelper(activity).also { surfaceView.setOnTouchListener(it) }

  override fun onResume(owner: LifecycleOwner) {
    surfaceView.onResume()
  }

  override fun onPause(owner: LifecycleOwner) {
    surfaceView.onPause()
  }

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
