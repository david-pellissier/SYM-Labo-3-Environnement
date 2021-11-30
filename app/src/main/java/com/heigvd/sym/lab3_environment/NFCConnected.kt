package com.heigvd.sym.lab3_environment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import java.util.*

class NFCConnected : AppCompatActivity() {

    private lateinit var btnMax: Button
    private lateinit var btnMedium: Button
    private lateinit var btnLow: Button
    private lateinit var progressBar: ProgressBar

    private var authStatus = AUTHENTICATE_INIT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_connected)
        btnMax = findViewById(R.id.btn_max_security)
        btnMedium = findViewById(R.id.btn_medium_security)
        btnLow = findViewById(R.id.btn_min_security)
        progressBar = findViewById(R.id.connected_progress_bar)

        progressBar.progress = AUTHENTICATE_INIT;
        progressBar.max = AUTHENTICATE_MAX

        btnMax.setOnClickListener {
            checkAuthLevel(AUTHENTICATE_MAX)
            return@setOnClickListener
        }
        btnMedium.setOnClickListener {
            checkAuthLevel(AUTHENTICATE_MEDIUM)
            return@setOnClickListener
        }
        btnLow.setOnClickListener {
            checkAuthLevel(AUTHENTICATE_LOW)
            refreshAuthStatus()
            return@setOnClickListener
        }

        val handler = Handler(Looper.getMainLooper())

        // Decrease authentication level periodically
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if(authStatus > 0){
                    handler.post {
                        authStatus -= AUTHENTICATE_DECREASE
                        progressBar.setProgress(authStatus, true)
                    }
                }
            }
        }, DELAY_MS, DELAY_MS)
    }

    private fun checkAuthLevel(level: Int) {
        val message = if(authStatus > level) AUTH_SUFFICIENT else AUTH_INSUFFICIENT
        Toast.makeText(this, "$message ($authStatus/$level)", Toast.LENGTH_SHORT).show();
    }

    private fun refreshAuthStatus() {
        authStatus = AUTHENTICATE_INIT
        progressBar.progress = authStatus
    }

    companion object {
        private const val AUTHENTICATE_INIT = 15
        private const val AUTHENTICATE_MAX = 10
        private const val AUTHENTICATE_MEDIUM = 5
        private const val AUTHENTICATE_LOW = 1
        private const val AUTH_SUFFICIENT = "Sufficient authentication"
        private const val AUTH_INSUFFICIENT = "Insufficient authentication"

        private const val AUTHENTICATE_DECREASE = 1
        private const val DELAY_MS : Long = 1000
        private const val TAG = "NFC/Connected"
    }
}