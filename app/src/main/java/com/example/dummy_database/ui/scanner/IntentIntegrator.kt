package com.example.dummy_database.ui.scanner

/**
 * Provides components for integrating the ZXing barcode scanner into the application
 * using the modern Activity Result API and a custom capture activity for specific
 * layout or orientation requirements.
 *
 */


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import com.example.dummy_database.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity



/**
 * A custom ActivityResultContract to launch the ZXing scanner.
 * Encapsulates creating the scan Intent and parsing its result, returning
 * the scanned contents (or null if canceled/failed).
 */
class ZxingScannerContract : ActivityResultContract<Unit, String?>() {

    // builds the intent to launch the scanner
    override fun createIntent(context: Context, input: Unit): Intent {
        // ensures context is an Activity
        val activity = context as? Activity
            ?: throw IllegalStateException("Context must be an Activity")

        // Initialize ZXing IntentIntegrator with custom PortraitCaptureActivity
        val integrator = IntentIntegrator(activity)
        integrator.setCaptureActivity(PortraitCaptureActivity::class.java)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)   //only qr code
        integrator.setPrompt("Scan a QR code")      //prompt text shown at top
        integrator.setCameraId(0)               // Uses default(rear) camera
        integrator.setBeepEnabled(false)
        integrator.setOrientationLocked(false)

        // return the Intent that will start the scanning Activity
        return integrator.createScanIntent()
    }

    // parse the result from the scanning Activity
    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode != Activity.RESULT_OK) return null
        val result: IntentResult? = IntentIntegrator.parseActivityResult(resultCode, intent)
        return result?.contents     // returns the scanned text (doc ID), or null if canceled
    }
}

/**
 * Custom CaptureActivity subclass to use a tailored layout for scanning.
 * (with info button)
 */
class PortraitCaptureActivity : CaptureActivity() {
    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.custom_capture_layout)

        // Set up the info button
        val infoBtn = findViewById<ImageButton>(R.id.info_button)
        infoBtn.setOnClickListener {
            Toast.makeText(this, "Scan the QR code shown on a business card to view details in AR.", Toast.LENGTH_LONG).show()
        }
    }
}
