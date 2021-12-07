package com.heigvd.sym.lab3_environment

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.heigvd.sym.lab3_environment.Utils.ForegroundNFC
import com.heigvd.sym.lab3_environment.Utils.manageNFC
import java.util.*

class NFCConnected : AppCompatActivity() {

    private lateinit var btnMax: Button
    private lateinit var btnMedium: Button
    private lateinit var btnLow: Button
    private lateinit var progressBar: ProgressBar

    private var mNfcAdapter: NfcAdapter? = null

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

        //NFC code
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }else if (!mNfcAdapter!!.isEnabled()) {
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NFC is enabled", Toast.LENGTH_LONG).show();
            Log.d("Handle : ", "beginning")
            handleIntent(intent);
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

    inner class ManageNFCImpl : manageNFC() {
        @Override
        override fun onPostExecute(result: String){
            refreshAuthStatus()
        }
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        Log.e("handleIntent : ", "begin2")

        /*
        Intent to start an activity when a tag with NDEF payload is discovered.
         */
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            Log.e(TAG, "begin3")
            val type = intent.type
            if (MIME_TEXT_PLAIN == type) {
                val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                Log.e(TAG, "handleIntent")
                this.ManageNFCImpl().execute(tag)
            } else {
                Log.e(TAG, "Wrong mime type: $type")
            }
            //Intent to start an activity when a tag is discovered.
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED == action) {

            // In case we would still use the Tech Discovered Intent
            Log.e(TAG, "action-tech-discovers")
            Toast.makeText(this, "Taction-tech-discovers", Toast.LENGTH_LONG).show();
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val techList: Array<String> = tag?.getTechList() as Array<String>
            val searchedTech = Ndef::class.java.name
            for (tech in techList) {
                if (searchedTech == tech) {
                    manageNFC().execute(tag)
                    break
                }
            }
        }else{
            Log.e(TAG, "Nothing")
        }
    }

    override fun onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        ForegroundNFC().stopForegroundDispatch(this, mNfcAdapter!!)
        super.onPause()
    }

    /*
    Nécessaire pour pouvoir lire plusieurs fois le tag
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent!!)
    }

    override fun onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        mNfcAdapter?.let {  ForegroundNFC().setupForegroundDispatch(this, it) };
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

        private const val MIME_TEXT_PLAIN = "text/plain"
    }
}