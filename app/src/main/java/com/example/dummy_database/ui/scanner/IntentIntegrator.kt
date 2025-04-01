package com.example.dummy_database.ui.scanner


import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult


/**
 * A custom ActivityResultContract to launch the ZXing scanner.
 */
class ZxingScannerContract : ActivityResultContract<Unit, String?>() {

    // 1) Use the exact signature: createIntent(context: Context, input: Unit)
    override fun createIntent(context: Context, input: Unit): Intent {
        // 2) Cast context to Activity. If context is not an activity,
        //    throw an error or handle gracefully.
        val activity = context as? Activity
            ?: throw IllegalStateException("Context must be an Activity")

        // 3) Use IntentIntegrator with the actual Activity
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setCameraId(0) // Use a specific camera if needed
        integrator.setBeepEnabled(false)
        integrator.setOrientationLocked(false)
        // ... any other settings you want

        // Create the Intent to launch the scanning Activity
        return integrator.createScanIntent()
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode != Activity.RESULT_OK) return null
        val result: IntentResult? = IntentIntegrator.parseActivityResult(resultCode, intent)
        return result?.contents // The scanned text (doc ID), or null if canceled
    }
}