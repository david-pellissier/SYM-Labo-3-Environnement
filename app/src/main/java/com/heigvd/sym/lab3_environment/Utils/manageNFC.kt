package com.heigvd.sym.lab3_environment.Utils

import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import kotlinx.coroutines.*
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.and

class manageNFC : CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job // to run code in Main(UI) Thread

    // call this method to cancel a coroutine when you don't need it anymore,
    // e.g. when user closes the screen
    fun cancel() {
        job.cancel()
    }

    fun execute(tag: Tag?) = launch {
        onPreExecute()
        val result = doInBackground(tag) // runs in background thread without blocking the Main Thread
        onPostExecute(result)
    }

    private suspend fun doInBackground(vararg p0: Tag?): String = withContext(Dispatchers.IO) { // to run code in Background Thread
        // do async work
        val tag: Tag? = p0[0]
        Log.e(tag.toString(), "doInBackground")
        val ndef = Ndef.get(tag)
            ?: // NDEF is not supported by this Tag.
            return@withContext null.toString()

        val ndefMessage = ndef.cachedNdefMessage
        val records = ndefMessage.records
        for (ndefRecord in records) {
            if (ndefRecord.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(
                    ndefRecord.type,
                    NdefRecord.RTD_TEXT
                )
            ) {
                try {
                    return@withContext readText(ndefRecord)
                } catch (e: UnsupportedEncodingException) {
                    Log.e(tag.toString(), "Unsupported Encoding", e)
                }
            }
        }
        return@withContext "SomeResult"
    }

    // Runs on the Main(UI) Thread
    private fun onPreExecute() {
        // show progress
    }

    // Runs on the Main(UI) Thread
    private fun onPostExecute(result: String) {
        // hide progress
        if (result != null) {
            Log.e("setText : ", "read")
            //mTextView?.text = "Read content: $result"
        }else{
            Log.e("setText : ", "null")
        }
    }

    @Throws(UnsupportedEncodingException::class)
    public fun readText(record: NdefRecord): String {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */
        Log.e("readText : ", "read")

        val payload = record.payload

        // Get the Text Encoding
        var textEncoding = charset("UTF-8")
        if(payload[0] and 128.toByte() == 0.toByte()) {
            textEncoding = charset("UTF-8")
        }else{
            textEncoding = charset("UTF-16")
        }
        // Get the Language Code
        val languageCodeLength: Int = (payload[0] and 52.toByte()).toInt()

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            textEncoding
        )
    }
}