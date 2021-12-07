package com.heigvd.sym.lab3_environment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class Password : AppCompatActivity() {

    private lateinit var user: EditText
    private lateinit var password: EditText
    private lateinit var cancelButton: Button
    private lateinit var validateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        user = findViewById(R.id.pw_user)
        password = findViewById(R.id.pw_pw)
        cancelButton = findViewById(R.id.pw_cancel)
        validateButton = findViewById(R.id.pw_validate)

        cancelButton.setOnClickListener {
            //on va vider les champs de la page de login lors du clique sur le bouton Cancel
            clearInput(user, password)
        }

        validateButton.setOnClickListener {
            //on réinitialise les messages d'erreur
            user.error = null
            password.error = null

            //on récupère le contenu de deux champs dans des variables de type String
            val userInput = user.text?.toString()
            val passwordInput = password.text?.toString()

            if (userInput.isNullOrEmpty() or passwordInput.isNullOrEmpty()) {
                // on affiche un message dans les logs de l'application
                Log.d(TAG, "Au moins un des deux champs est vide")
                if (userInput.isNullOrEmpty())
                    user.error = "user empty"
                if (passwordInput.isNullOrEmpty())
                    password.error = "password empty"
                // Pour les fonctions lambda, on doit préciser à quelle fonction l'appel à return
                // doit être appliqué
                return@setOnClickListener
            }

            // Vérif connexion
            if (credentials.find { it == Pair(userInput, passwordInput) } != null) {
                Log.d(TAG, "Connecté")


                // Lance l'activité
                val intent = Intent(this, NFC::class.java)
                startActivity(intent)

            } else {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.apply {
                    setTitle("Error")
                    setMessage("Invalid credentials")
                }.create()
                alertDialog.show()
                Log.d(TAG, "Erreur de connexion")
                return@setOnClickListener
            }
        }

        val extras = intent.extras
        if (extras != null) {
            val email = extras.getString("email")
            val passWord = extras.getString("password")
            credentials.add(Pair(email.toString(), passWord.toString()))
        }

    }

    // En Kotlin, les variables static ne sont pas tout à fait comme en Java
    // pour des raison de lisibilité du code, les variables et méthodes static
    // d'une classe doivent être regroupées dans un bloc à part: le companion object
    // cela permet d'avoir un aperçu plus rapide de tous les éléments static d'une classe
    // sans devoir trouver le modifieur dans la définition de ceux-ci, qui peuvent être mélangé
    // avec les autres éléments non-static de la classe
    companion object {
        private const val TAG: String = "MainActivity"


        /**
         * Vider les champs passés en paramètre
         * Annule les éventuels messages d'erreurs présents
         */
        fun clearInput(email: EditText, password: EditText) {
            email.text?.clear()
            password.text?.clear()
            email.error = null
            password.error = null
        }


        val credentials: MutableList<Pair<String, String>> = mutableListOf(
            Pair("user1", "1234"),
            Pair("user2", "abcd")
        )

    }
}