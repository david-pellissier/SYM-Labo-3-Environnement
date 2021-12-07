package com.heigvd.sym.lab3_environment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.heigvd.sym.lab3_environment.utils.ManageNFC
import com.heigvd.sym.lab3_environment.utils.NFCActivity
import java.util.*

class NFCConnected : NFCActivity() {

    private lateinit var btnMax: Button
    private lateinit var btnMedium: Button
    private lateinit var btnLow: Button
    private lateinit var progressBar: ProgressBar

    private var authStatus = AUTHENTICATE_INIT
    private val handler = Handler(Looper.getMainLooper())
    private var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_connected)
        btnMax = findViewById(R.id.btn_max_security)
        btnMedium = findViewById(R.id.btn_medium_security)
        btnLow = findViewById(R.id.btn_min_security)
        progressBar = findViewById(R.id.connected_progress_bar)

        // Decrease authentication level periodically
        progressBar.max = AUTHENTICATE_MAX
        progressBar.progress = AUTHENTICATE_MAX
        timer.schedule(object: TimerTask() {
            override fun run() {
                if(authStatus > 0){
                    handler.post {
                        authStatus -= AUTHENTICATE_DECREASE
                        progressBar.setProgress(authStatus, true)
                    }
                }
            }
        }, DELAY_MS, DELAY_MS)

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
            return@setOnClickListener
        }

        //Start NFC scan
        handleIntent(intent);

        nfcPostExecute = object : ManageNFC() {
            @Override
            override fun onPostExecute(result: String){
                super.onPostExecute(result) // basic check is performed in there
                refreshAuthStatus()
            }
        }
    }

    private fun checkAuthLevel(level: Int) {
        val message = if(authStatus > level) AUTH_SUFFICIENT else AUTH_INSUFFICIENT
        Toast.makeText(this, "$message ($authStatus/$level)", Toast.LENGTH_SHORT).show()
    }

    private fun refreshAuthStatus() {
        authStatus = AUTHENTICATE_INIT
        progressBar.setProgress(AUTHENTICATE_MAX, true)
        Toast.makeText(this, "Authentication refreshed !", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val AUTHENTICATE_INIT = 120
        private const val AUTHENTICATE_MAX = 100
        private const val AUTHENTICATE_MEDIUM = 50
        private const val AUTHENTICATE_LOW = 10
        private const val AUTH_SUFFICIENT = "Sufficient authentication"
        private const val AUTH_INSUFFICIENT = "Insufficient authentication"

        private const val AUTHENTICATE_DECREASE = 1
        private const val DELAY_MS : Long = 150
        private const val TAG = "NFC/Connected"
    }
}