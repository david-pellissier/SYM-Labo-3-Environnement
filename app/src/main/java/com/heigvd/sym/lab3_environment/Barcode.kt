package com.heigvd.sym.lab3_environment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.journeyapps.barcodescanner.*
import android.graphics.Color

import android.widget.ImageView
import com.google.zxing.ResultPoint

import com.journeyapps.barcodescanner.BarcodeResult

import com.journeyapps.barcodescanner.BarcodeCallback

import com.journeyapps.barcodescanner.DefaultDecoderFactory

import com.google.zxing.BarcodeFormat

import java.util.Arrays
import android.view.KeyEvent

import android.view.View
import android.widget.TextView

class Barcode : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var resultTextView: TextView
    private var lastText: String = ""

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                // Prevent duplicate scans
                return
            }
            lastText = result.text
            resultTextView.text = result.text

            //Added preview of scanned barcode
            val imageView = findViewById<ImageView>(R.id.barcode_preview)
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW))
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        barcodeView = findViewById(R.id.barcode_scanner)
        barcodeView.setStatusText("")
        resultTextView = findViewById(R.id.barcode_result)
        val formats: Collection<BarcodeFormat> =
            Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.initializeFromIntent(intent)
        barcodeView.decodeContinuous(callback)
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    fun pause(view: View?) {
        barcodeView.pause()
    }

    fun resume(view: View?) {
        barcodeView.resume()
    }

    fun triggerScan(view: View?) {
        barcodeView.decodeSingle(callback)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
}