/**
 * Groupe : Pellissier David, Ruckstuhl Michael, Sauge Ryan
 * Description : Page d'authentification pour la partie NFC (labo partie 2).
 *               L'utilisateur se connecte en entrant ses credentials et scannant le tag NFC en
 *               appuyant sur le bouton de connexion.
 *               Pour se connecter : user1:1234 et user2:abcd
 */

package com.heigvd.sym.lab3_environment

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.heigvd.sym.lab3_environment.utils.ManageNFC
import com.heigvd.sym.lab3_environment.utils.NFCActivity
import kotlinx.coroutines.*


class NFCLogin : NFCActivity() {

    private lateinit var user: EditText
    private lateinit var password: EditText
    private lateinit var validateButton: Button

    private var passwordCheck = false;
    private val activity = this;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        user = findViewById(R.id.pw_user)
        password = findViewById(R.id.pw_pw)
        validateButton = findViewById(R.id.btn_connect)

        nfcPostExecute = object : ManageNFC() {
            override fun onPostExecute(result: String) {
                super.onPostExecute(result) // basic check is performed in there


                if (passwordCheck) {
                    // User is now connected. We can launch the next activity
                    val intent = Intent(activity.applicationContext, NFCConnected::class.java)
                    startActivity(intent)
                }
            }
        }

        validateButton.setOnClickListener {
            // Reset error message
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
                // Launch NFC scan
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



