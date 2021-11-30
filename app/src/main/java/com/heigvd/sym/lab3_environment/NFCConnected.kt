package com.heigvd.sym.lab3_environment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NFCConnected : AppCompatActivity() {


    private var authenticateValue = AUTHENTICATE_MAX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfcconnected)




    }

    fun updateAuthStatus() {



    }



    companion object {
        private val AUTHENTICATE_MAX = 10
    }
}