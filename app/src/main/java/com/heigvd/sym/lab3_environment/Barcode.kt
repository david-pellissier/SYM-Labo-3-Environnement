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
import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.widget.Toast.*

class Barcode : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var resultTextView: TextView
    private lateinit var resultImageView: ImageView

    private var lastText: String = ""

    private var isCameraPermissionGranted = false

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

    private fun initBarcode(){
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(FORMATS)
        barcodeView.initializeFromIntent(intent)
        barcodeView.decodeContinuous(callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        resultTextView = findViewById(R.id.barcode_result)
        resultImageView = findViewById(R.id.barcode_preview)

        barcodeView = findViewById(R.id.barcode_scanner)
        barcodeView.setStatusText("")

        isCameraPermissionGranted = (this.checkSelfPermission(CAMERA_PERM) == PERMISSION_GRANTED)

        if(isCameraPermissionGranted){
            initBarcode()
        }
        else {
            this.requestPermissions(arrayOf(CAMERA_PERM), REQUEST_CODE_CAMERA)
        }
    }

    override fun onResume() {
        super.onResume()

        if(isCameraPermissionGranted){
            barcodeView.resume()
        }
    }

    override fun onPause() {
        super.onPause()

        if(isCameraPermissionGranted) {
            barcodeView.pause()
        }
    }

    fun pause(view: View?) {
        if(isCameraPermissionGranted){
            barcodeView.pause()
        }

    }

    fun resume(view: View?) {
        if(isCameraPermissionGranted){
            barcodeView.resume()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                isCameraPermissionGranted = true
                initBarcode()
            } else {
                makeText(this, "Camera permission denied. The scanner will not work.", LENGTH_LONG).show()
                resultTextView.text = "Camera disabled"
            }
        }
    }


    companion object {
        private val FORMATS = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        private const val DOT_COLOR = Color.YELLOW
        private const val CAMERA_PERM = Manifest.permission.CAMERA
        private const val REQUEST_CODE_CAMERA = 101
    }
}