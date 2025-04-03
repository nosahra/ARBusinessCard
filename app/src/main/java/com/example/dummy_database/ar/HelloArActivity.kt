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

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dummy_database.R
import com.google.ar.core.Config
import com.google.ar.core.Config.InstantPlacementMode
import com.google.ar.core.Session
import com.example.dummy_database.ar.helpers.CameraPermissionHelper
import com.example.dummy_database.ar.helpers.DepthSettings
import com.example.dummy_database.ar.helpers.FullScreenHelper
import com.example.dummy_database.ar.helpers.InstantPlacementSettings
import com.example.dummy_database.ar.helpers.samplerender.SampleRender
import com.example.dummy_database.ar.helpers.ARCoreSessionLifecycleHelper
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.dummy_database.tts.synthesizeAndPlay

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3D model.
 */
class HelloArActivity : AppCompatActivity() {
  companion object {
    private const val TAG = "HelloArActivity"
  }

  lateinit var arCoreSessionHelper: ARCoreSessionLifecycleHelper
  lateinit var view: HelloArView
  lateinit var renderer: HelloArRenderer

  val instantPlacementSettings = InstantPlacementSettings()
  val depthSettings = DepthSettings()

  // Flag to ensure TTS is triggered only once.
  var ttsTriggered = false

  // Retrieve extras once.
  val introductionText: String by lazy {
    intent.getStringExtra("introduction") ?: ""
  }
  val voicePreference: String by lazy {
    intent.getStringExtra("voicePreference") ?: ""
  }

  // Call this method to trigger TTS for the introduction.
  fun triggerTTSForIntroduction() {
    if (!ttsTriggered && introductionText.isNotEmpty()) {
      ttsTriggered = true
      // Use a coroutine to call your suspend function.
      // It’s better to use lifecycleScope instead of GlobalScope.
      lifecycleScope.launch(Dispatchers.IO) {
        synthesizeAndPlay(this@HelloArActivity, introductionText, voicePreference)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Determine if the device is a phone or a device that should support multiple orientations.
    // For example, you might check for a feature or screen size.
    if (isPhoneDevice()) {
      requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    } else {
      // Allow flexible orientation (fullSensor) for devices like tablets or Chrome OS.
      requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    setContentView(R.layout.activity_main)

    // Setup ARCore session lifecycle helper and configuration.
    arCoreSessionHelper = ARCoreSessionLifecycleHelper(this)
    // If Session creation or Session.resume() fails, display a message and log detailed
    // information.
    arCoreSessionHelper.exceptionCallback =
      { exception ->
        val message =
          when (exception) {
            is UnavailableUserDeclinedInstallationException ->
              "Please install Google Play Services for AR"
            is UnavailableApkTooOldException -> "Please update ARCore"
            is UnavailableSdkTooOldException -> "Please update this app"
            is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
            is CameraNotAvailableException -> "Camera not available. Try restarting the app."
            else -> "Failed to create AR session: $exception"
          }
        Log.e(TAG, "ARCore threw an exception", exception)
        view.snackbarHelper.showError(this, message)
      }

    // Configure session features, including: Lighting Estimation, Depth mode, Instant Placement.
    arCoreSessionHelper.beforeSessionResume = ::configureSession
    lifecycle.addObserver(arCoreSessionHelper)

    // Set up the Hello AR renderer.
    renderer = HelloArRenderer(this)
    lifecycle.addObserver(renderer)

    // Set up Hello AR UI.
    view = HelloArView(this)
    lifecycle.addObserver(view)
    setContentView(view.root)

    // Sets up an example renderer using our HelloARRenderer.
    SampleRender(view.surfaceView, renderer, assets)

    depthSettings.onCreate(this)
    instantPlacementSettings.onCreate(this)
  }

  // Configure the session, using Lighting Estimation, and Depth mode.
  fun configureSession(session: Session) {
    session.configure(
      session.config.apply {
        lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR

        // Depth API is used if it is configured in Hello AR's settings.
        depthMode =
          if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            Config.DepthMode.AUTOMATIC
          } else {
            Config.DepthMode.DISABLED
          }

        // Instant Placement is used if it is configured in Hello AR's settings.
        instantPlacementMode =
          if (instantPlacementSettings.isInstantPlacementEnabled) {
            InstantPlacementMode.LOCAL_Y_UP
          } else {
            InstantPlacementMode.DISABLED
          }
      }
    )
  }

  // You can implement a simple check based on screen size or other criteria.
  private fun isPhoneDevice(): Boolean {
    // Example check using screen size: phones typically have a smaller screen.
    val screenLayout = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
    return screenLayout == Configuration.SCREENLAYOUT_SIZE_SMALL ||
            screenLayout == Configuration.SCREENLAYOUT_SIZE_NORMAL
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    results: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, results)
    if (!CameraPermissionHelper.hasCameraPermission(this)) {
      // Use toast instead of snackbar here since the activity will exit.
      Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
        .show()
      if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        CameraPermissionHelper.launchPermissionSettings(this)
      }
      finish()
    }
  }

//  override fun onTouchEvent(event: MotionEvent): Boolean {
//    // Assume you have a way to get the current frame from your ARCore session.
//    val frame = arCoreSessionHelper.session?.update() ?: return super.onTouchEvent(event)
//    renderer.handleTap(event, frame)
//    return super.onTouchEvent(event)
//  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
  }
}
