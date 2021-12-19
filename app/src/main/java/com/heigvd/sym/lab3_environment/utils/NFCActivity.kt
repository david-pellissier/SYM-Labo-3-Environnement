/**
 * Groupe : Pellissier David, Ruckstuhl Michael, Sauge Ryan
 * Description : Classe abstraite servant à définir le comportement pour les activités reposant sur
 *               le scan NFC.
 * Sources :
 * https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
 *
 */

package com.heigvd.sym.lab3_environment.utils

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class NFCActivity() : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null
    protected var nfcPostExecute: ManageNFC? =
        null // Override this to define the code to execute after scanning

    fun handleIntent(intent: Intent) {
        val action = intent.action

        /*
        Intent to start an activity when a tag with NDEF payload is discovered.
         */
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val type = intent.type
            if (MIME_TEXT_PLAIN == type) {
                //Intent to start an activity when a tag is discovered.
                val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                nfcPostExecute?.execute(tag)
            } else {
                Log.e(TAG, "Wrong MIME type: $type")
            }
        } else {
            Log.d(TAG, "No tags found")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize NFC adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show()
            finish()
            return
        } else if (!mNfcAdapter!!.isEnabled) {
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show()
            finish()
            return
        } else {
            Toast.makeText(this, "NFC is enabled", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        mNfcAdapter!!.disableForegroundDispatch(this)
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent!!)
    }

    override fun onResume() {
        super.onResume()

        mNfcAdapter?.let {
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
        }
    }

    companion object {
        const val MIME_TEXT_PLAIN = "text/plain"
        const val TAG = "Log - NfcDemo : "
        const val NFCContent = "frtest"
    }


}