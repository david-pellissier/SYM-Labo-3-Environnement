package com.heigvd.sym.lab3_environment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG: String = "MainActivity"

    private lateinit var btnBarcode: Button
    private lateinit var btnNFC: Button
    private lateinit var btniBeacon: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBarcode = findViewById(R.id.main_btn_barcode)
        btniBeacon = findViewById(R.id.main_btn_ibeacon)
        btnNFC = findViewById(R.id.main_btn_nfc)

        btnBarcode.setOnClickListener {
            val intent = Intent(this, Barcode::class.java)
            startActivity(intent)

            return@setOnClickListener
        }

        btniBeacon.setOnClickListener {
            val intent = Intent(this, iBeacon::class.java)
            startActivity(intent)

            return@setOnClickListener
        }

        btnNFC.setOnClickListener {
            val intent = Intent(this, NFCLogin::class.java)
            startActivity(intent)

            return@setOnClickListener
        }


    }
}