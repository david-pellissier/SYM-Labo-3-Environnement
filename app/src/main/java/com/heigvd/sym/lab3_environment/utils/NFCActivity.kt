package com.heigvd.sym.lab3_environment.utils

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.heigvd.sym.lab3_environment.R
import java.lang.RuntimeException

open class NFCActivity : AppCompatActivity(){

    private var mNfcAdapter: NfcAdapter? = null
    protected var nfcPostExecute : ManageNFC? = null

    fun handleIntent(intent: Intent) {
        val action = intent.action
        Log.e("handleIntent : ", "begin2")

        /*
        Intent to start an activity when a tag with NDEF payload is discovered.
         */
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) { // TODO: check if necessary (included in second)
            Log.e(TAG, "begin3")
            val type = intent.type
            if (MIME_TEXT_PLAIN == type) {
                val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                Log.e(TAG, "handleIntent")
                nfcPostExecute?.execute(tag)
            } else {
                Log.e(TAG, "Wrong MIME type: $type")
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
                    nfcPostExecute?.execute(tag)
                    break
                }
            }
        } else {
            Log.d(TAG, "Nothing")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //NFC code
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish()
            return
        } else if (!mNfcAdapter!!.isEnabled()) {
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
            finish()
            return
        } else {
            Toast.makeText(this, "NFC is enabled", Toast.LENGTH_LONG).show();
        }
    }

    override fun onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        mNfcAdapter!!.disableForegroundDispatch(this)
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
        mNfcAdapter?.let {
            Log.d(TAG, "setupForegroundDispatch")
            val intent = Intent(applicationContext, javaClass)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
            val filters = arrayOfNulls<IntentFilter>(1)
            val techList = arrayOf<Array<String>>()

            // Notice that this is the same filter as in our manifest.
            filters[0] = IntentFilter()
            filters[0]!!.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            filters[0]!!.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                filters[0]!!.addDataType(MIME_TEXT_PLAIN)
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("Check your mime type.")
            }
            it.enableForegroundDispatch(this, pendingIntent, filters, techList)
        };
    }

    companion object {
        const val MIME_TEXT_PLAIN = "text/plain"
        const val TAG = "Log - NfcDemo : "
        const val NFCContent = "frtest";
    }


}