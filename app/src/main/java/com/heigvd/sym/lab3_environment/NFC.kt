package com.heigvd.sym.lab3_environment

import android.content.Intent
import android.os.Bundle
import android.nfc.NfcAdapter

import android.nfc.Tag

import android.nfc.tech.Ndef
import android.util.Log
import android.widget.*
import com.heigvd.sym.lab3_environment.utils.ForegroundNFC
import com.heigvd.sym.lab3_environment.utils.ManageNFC
import androidx.appcompat.app.AlertDialog
import com.heigvd.sym.lab3_environment.utils.NFCActivity
import kotlinx.coroutines.*


class NFC : NFCActivity() {
    val MIME_TEXT_PLAIN = "text/plain"
    val TAG = "Log - NfcDemo : "

    protected var mTextView: TextView? = null
    private var mNfcAdapter: NfcAdapter? = null

    private lateinit var user: EditText
    private lateinit var password: EditText
    private lateinit var validateButton: Button

    private var passwordCheck = false;
    private var NFCContent = "frtest";
    private val activity = this;

    inner class ManageNFCImpl : ManageNFC() {
        @Override
        override fun onPostExecute(result: String){
            if (result != null) {
                Log.e("setText : ", "read")
                Log.e("setText pc : ", passwordCheck.toString())
                Log.e("setText : ", result)
                if(passwordCheck && result.equals(NFCContent) ){
                    Log.e("setText : ", "startActivity")
                    val intent = Intent(activity.applicationContext, NFCConnected::class.java)
                    startActivity(intent)
                }else{
                    //mTextView?.text = "Read content: $result"
                }

            }else{
                Log.e("setText : ", "null")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        Log.d(TAG, "onCreate")
        mTextView = findViewById(R.id.textView_explanation);

        user = findViewById(R.id.pw_user)
        password = findViewById(R.id.pw_pw)
        validateButton = findViewById(R.id.btn_connect)

        //NFC code
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            //return;

        }else if (!mNfcAdapter!!.isEnabled()) {
            //mTextView.setText("NFC is disabled.");
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "NFC is enabled", Toast.LENGTH_LONG).show();
            Log.d("Handle : ", "beginning")
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
                if (NFC.credentials.find { it == Pair(userInput, passwordInput) } != null) {
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
            //mTextView.setText(R.string.explanation);
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
                //NdefReaderTask().execute(tag)
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
                    this.ManageNFCImpl().execute(tag)
                    break
                }
            }
        }else{
            Log.e(TAG, "Noting")
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
        val credentials: MutableList<Pair<String, String>> = mutableListOf(
            Pair("user1", "1234"),
            Pair("user2", "abcd")
        )
    }

}



