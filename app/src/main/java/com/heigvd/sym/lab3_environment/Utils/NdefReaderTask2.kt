package com.heigvd.sym.lab3_environment.Utils

import android.nfc.NdefRecord

import android.nfc.NdefMessage
import android.nfc.Tag

import android.nfc.tech.Ndef

import android.os.AsyncTask
import android.util.Log
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.experimental.and

/*
Source : https://stackoverflow.com/questions/61654867/convert-to-kotlin-using-and
 */

/**
 * Background task for reading the data. Do not block the UI thread while reading.
 *
 * @author Ralf Wondratschek
 */
class NdefReaderTask : AsyncTask<Tag?, Void?, String?>() {
    override fun doInBackground(vararg p0: Tag?): String? {
        val tag: Tag? = p0[0]
        val ndef = Ndef.get(tag)
            ?: // NDEF is not supported by this Tag.
            return null
        val ndefMessage = ndef.cachedNdefMessage
        val records = ndefMessage.records
        for (ndefRecord in records) {
            if (ndefRecord.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(
                    ndefRecord.type,
                    NdefRecord.RTD_TEXT
                )
            ) {
                try {
                    return readText(ndefRecord)
                } catch (e: UnsupportedEncodingException) {
                    Log.e(tag.toString(), "Unsupported Encoding", e)
                }
            }
        }
        return null
    }

    @Throws(UnsupportedEncodingException::class)
    private fun readText(record: NdefRecord): String {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */
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

    fun onPostExecute(result: String?) {
        if (result != null) {
            //mTextView.setText("Read content: $result")
        }
    }
