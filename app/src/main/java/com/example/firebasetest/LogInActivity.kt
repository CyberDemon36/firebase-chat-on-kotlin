package com.example.firebasetest

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebasetest.databinding.LogInActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.sign

class LogInActivity : AppCompatActivity() {
    lateinit var binding: LogInActivityBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LogInActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    override fun onStart() {
        super.onStart()
        binding.btnLogIn.setOnClickListener{
            // Проверка email адреса на соответствие паттерну
            var email = binding.emailInput.text.toString()
            var password = binding.passwordInput.text.toString()

            auth = Firebase.auth
            logIn(email, password)
        }

        binding.btnSignIn.setOnClickListener{
            var intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed. Check email or password!",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }
}