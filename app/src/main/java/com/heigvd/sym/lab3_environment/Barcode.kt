package com.heigvd.sym.lab3_environment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.*
import com.google.zxing.BarcodeFormat

class Barcode : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var resultTextView: TextView
    private lateinit var resultImageView: ImageView

    private var lastText: String = ""

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                // Prevent duplicate scans
                return
            }

            lastText = result.text
            resultTextView.text = result.text

            //Preview of scanned barcode
            resultImageView.setImageBitmap(result.getBitmapWithResultPoints(DOT_COLOR))
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        resultTextView = findViewById(R.id.barcode_result)
        resultImageView = findViewById(R.id.barcode_preview)

        barcodeView = findViewById(R.id.barcode_scanner)
        barcodeView.setStatusText("")
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(FORMATS)
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

    companion object {
        private val FORMATS = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        private const val DOT_COLOR = Color.YELLOW
    }
}