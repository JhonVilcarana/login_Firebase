package com.example.logginandroid

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.logginandroid.R.*
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC
}

class MainActivity : AppCompatActivity() {
    private lateinit var emailEdit: TextView
    private lateinit var passwordEdit: TextView
    private lateinit var logOut: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Analytics Event
        setContentView(layout.activity_main)
        emailEdit = findViewById(R.id.emailEdit)
        passwordEdit = findViewById(R.id.passwordEdit)
        logOut = findViewById(R.id.logOut)


        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val password = bundle?.getString("password")
        setup(email ?: "", password ?: "")
    }

    private fun setup(email: String, password: String) {
        title = "Inicio"
        emailEdit.text = email
        passwordEdit.text = password

        logOut.setOnClickListener {
            val clean = getSharedPreferences("credentials",Context.MODE_PRIVATE)
                .edit()
            clean.remove("email")
            clean.remove("password")
            clean.remove("remindCheckBox")
            clean.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}