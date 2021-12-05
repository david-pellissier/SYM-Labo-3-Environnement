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
import android.content.IntentFilter.MalformedMimeTypeException

import android.content.IntentFilter

import android.app.PendingIntent

import android.app.Activity
import android.nfc.NdefRecord
import android.nfc.Tag
import java.lang.RuntimeException
import com.heigvd.sym.lab3_environment.Utils.NdefReaderTask

import android.nfc.tech.Ndef
import android.os.AsyncTask
import android.util.Log
import java.io.UnsupportedEncodingException
import kotlin.experimental.and
import android.nfc.NdefMessage





class NFC : AppCompatActivity() {
    private var AUTHENTICATE_MAX = 10
    private lateinit var progressBar: ProgressBar
    val MIME_TEXT_PLAIN = "text/plain"
    val TAG = "Log - NfcDemo : "

    protected var mTextView: TextView? = null
    private var mNfcAdapter: NfcAdapter? = null


    fun reduceBarre() {

    }
    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     */
    inner class NdefReaderTask : AsyncTask<Tag?, Void?, String?>() {
        override fun doInBackground(vararg p0: Tag?): String? {

            val tag: Tag? = p0[0]
            Log.e(tag.toString(), "doInBackground")
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




        /*
        Runs on the UI thread after doInBackground(Params...). The specified result is the value returned by doInBackground(Params...).
         */
        override fun onPostExecute(result: String?) {
            if (result != null) {
                Log.e("setText : ", "read")
                mTextView?.text = "Read content: $result"
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


        //NFC code

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
        Log.d("Handle : ", "beginning")
        handleIntent(intent);
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
                NdefReaderTask().execute(tag)
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
                    NdefReaderTask().execute(tag)
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
        stopForegroundDispatch(this, mNfcAdapter!!)
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
        mNfcAdapter?.let { setupForegroundDispatch(this, it) };
    }

    /**
     * @param activity The corresponding [Activity] requesting the foreground dispatch.
     * @param adapter The [NfcAdapter] used for the foreground dispatch.
     */
    private fun setupForegroundDispatch(activity: Activity, adapter: NfcAdapter) {
        Log.d(TAG, "setupForegroundDispatch")
        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, 0)
        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()

        // Notice that this is the same filter as in our manifest.
        filters[0] = IntentFilter()
        filters[0]!!.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
        filters[0]!!.addCategory(Intent.CATEGORY_DEFAULT)
        try {
            filters[0]!!.addDataType(MIME_TEXT_PLAIN)
        } catch (e: MalformedMimeTypeException) {
            throw RuntimeException("Check your mime type.")
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList)
    }

    /**
     * @param activity The corresponding [BaseActivity] requesting to stop the foreground dispatch.
     * @param adapter The [NfcAdapter] used for the foreground dispatch.
     */
    private fun stopForegroundDispatch(activity: Activity?, adapter: NfcAdapter) {
        adapter.disableForegroundDispatch(activity)
    }




}



