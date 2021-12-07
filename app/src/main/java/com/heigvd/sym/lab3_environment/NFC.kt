package com.heigvd.sym.lab3_environment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.heigvd.sym.lab3_environment.utils.ManageNFC
import androidx.appcompat.app.AlertDialog
import com.heigvd.sym.lab3_environment.utils.NFCActivity
import kotlinx.coroutines.*


class NFC : NFCActivity() {

    private var mTextView: TextView? = null

    private lateinit var user: EditText
    private lateinit var password: EditText
    private lateinit var validateButton: Button

    private var passwordCheck = false;
    private val activity = this;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        Log.d(TAG, "onCreate")
        mTextView = findViewById(R.id.textView_explanation);

        user = findViewById(R.id.pw_user)
        password = findViewById(R.id.pw_pw)
        validateButton = findViewById(R.id.btn_connect)

        nfcPostExecute = object : ManageNFC() {
            @Override
            override fun onPostExecute(result: String){
                super.onPostExecute(result) // basic check is performed in there

                Log.e("setText : ", "read")
                Log.e("setText pc : ", passwordCheck.toString())
                Log.e("setText : ", result)

                if(passwordCheck){
                    Log.e("setText : ", "startActivity")
                    val intent = Intent(activity.applicationContext, NFCConnected::class.java)
                    startActivity(intent)
                }
            }
        }

        validateButton.setOnClickListener {
            //on réinitialise les messages d'erreur
            user.error = null
            password.error = null

            val userInput = user.text?.toString()
            val passwordInput = password.text?.toString()

            if (userInput.isNullOrEmpty() or passwordInput.isNullOrEmpty()) {
                if (userInput.isNullOrEmpty())
                    user.error = "user empty"
                if (passwordInput.isNullOrEmpty())
                    password.error = "password empty"
                return@setOnClickListener
            }
            if (credentials.find { it == Pair(userInput, passwordInput) } != null) {
                // Lance handle
                passwordCheck = true;
                handleIntent(intent);
            } else {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.apply {
                    setTitle("Error")
                    setMessage("Invalid credentials")
                }.create()
                alertDialog.show()
                return@setOnClickListener
            }
        }
    }


    companion object {
        val credentials: MutableList<Pair<String, String>> = mutableListOf(
            Pair("user1", "1234"),
            Pair("user2", "abcd")
        )
        const val TAG = "Log - NfcDemo : "
    }

}



