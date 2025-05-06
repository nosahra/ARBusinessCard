package com.example.dummy_database.utils

/**
 * Utility functions for generating QR codes using the zxing library.
 *
 * Sole Contributor: Newton
 */


import android.graphics.Bitmap          // for creating and manipulating bitmaps
import com.google.zxing.BarcodeFormat   // enumeration fo barcode formats
import com.google.zxing.EncodeHintType      //hints for encoder(qr code)
import com.google.zxing.qrcode.QRCodeWriter     //Qr code encoder implementation

fun generateQrCode(content: String, size: Int = 512): Bitmap? {
    return try {

        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")      // specifying encoding hinst
        val writer = QRCodeWriter()
        // encode the content into a bitmatrx(2d array of bits)
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

        // creates a mutable bitmap to draw the qr code pixels
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

        //iterate over every pixel coordinate in the matrix
        for (x in 0 until size) {
            for (y in 0 until size) {
                // if bitmatrix returns true at (x,y), set the pixel to black else white
                bmp.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bmp  //returns the generated bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
