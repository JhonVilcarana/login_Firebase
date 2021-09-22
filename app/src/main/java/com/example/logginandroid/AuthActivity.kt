package com.example.logginandroid


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

import android.widget.Toast
class AuthActivity : AppCompatActivity() {
    private lateinit var remindCheckBox: CheckBox
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //Analytics Event
        setContentView(R.layout.activity_auth)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        loginButton = findViewById(R.id.loginButton)
        remindCheckBox = findViewById(R.id.remindCheckBox)
        val loaderPreferences = getSharedPreferences("credentials", Context.MODE_PRIVATE)
        if (loaderPreferences.getBoolean("remindCheckBox",false)){
            startActivity(Intent(applicationContext,MainActivity::class.java).apply {
                putExtra("email", loaderPreferences.getString("email",""))
                putExtra("password", loaderPreferences.getString("password",""))
            })
        }
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integration of Firebase complet")
        analytics.logEvent("InitScreen", bundle)

        // Setup
        setup()

    }

    private fun setup() {
        title = "Autentication"
        signUpButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            saveSession(applicationContext)
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            showAlert("Sea producido en un error autenticando al usuario")

                        }
                    }

            }
            else{
                showAlert("Campos requeridos vacios")
            }
        }
        loginButton.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            saveSession(applicationContext)
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            showAlert("Sea producido en un error autenticando al usuario")
                        }
                    }
            }
            else{
                showAlert("Campos requeridos vacios")
            }
            }


    }

    private fun saveSession(context:Context){
        val edit = context.getSharedPreferences("credentials",Context.MODE_PRIVATE).edit()
        edit.putString("email", emailEditText.text.toString())
        edit.putString("password", passwordEditText.text.toString())
        edit.putBoolean("remindCheckBox", remindCheckBox.isChecked)
        edit.apply()
        Toast.makeText(
            applicationContext,
            "Credentials successfully saved!",
            Toast.LENGTH_SHORT
        )
            .show()
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }

    private fun showAlert(message:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("$message")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, password: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("password", password.name)
        }
        startActivity(homeIntent)
    }

}
