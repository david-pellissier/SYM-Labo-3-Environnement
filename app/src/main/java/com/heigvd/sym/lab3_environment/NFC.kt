package com.heigvd.sym.lab3_environment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import java.util.*
import android.nfc.NfcAdapter

import android.widget.TextView




class NFC : AppCompatActivity() {
    private var AUTHENTICATE_MAX = 10
    private lateinit var progressBar: ProgressBar

    val TAG = "NfcDemo"

    private val mTextView: TextView? = null
    private var mNfcAdapter: NfcAdapter? = null


    fun reduceBarre(){

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        progressBar = findViewById(R.id.progressBar)

        progressBar.setProgress(100);
        /*https://stackoverflow.com/questions/43348623/how-to-call-a-function-after-delay-in-kotlin

         */
        Timer().schedule(object : TimerTask() {
            override fun run() {
                progressBar.incrementProgressBy(-10)
            }
        }, 2000, 2000)


        //Pop-up
        val text = "Durée de vie"
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter!!.isEnabled()) {
            //mTextView.setText("NFC is disabled.");
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "NFC is enabled", Toast.LENGTH_LONG).show();
            //mTextView.setText(R.string.explanation);
        }

        handleIntent(intent);
    }

    private fun  handleIntent(intent : Intent )  {

    }


}