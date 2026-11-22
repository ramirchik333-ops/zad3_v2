package com.example.madventuretv.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.madventuretv.R

/** Экран авторизации: проверка полей на пустоту и переход к каналам. */
class SignInActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        findViewById<Button>(R.id.login).setOnClickListener {
            signIn(email.text.toString(), password.text.toString())
        }

        email.requestFocus()
    }

    private fun signIn(emailValue: String, passwordValue: String) {
        if (emailValue.isBlank() || passwordValue.isBlank()) {
            AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_empty_fields)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show()
            return
        }

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
