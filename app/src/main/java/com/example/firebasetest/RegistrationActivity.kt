package com.example.firebasetest

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.firebasetest.databinding.RegistrationActivityLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationActivity: AppCompatActivity() {
    lateinit var binding: RegistrationActivityLayoutBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        binding.btnSignIn.setOnClickListener{
            // Проверка email адреса на соответствие паттерну
            var email = binding.emailRegistrationInput.text.toString()
            val emailRegexPattern = Regex("[\\w~!@#$%^&*+-]+@[a-z]+\\.[a-z]{2,}")
            var emailCheck = emailRegexPattern.matches(email)
            // Также, пароль должен содержать как минимум 6 символов
            var password = binding.passwordRegistrationInput.text.toString()
            val passwordRegexPattern = Regex("[\\w~!@#$%^&*+-]{6,}")
            var passwordCheck = passwordRegexPattern.matches(password)
            // Поле для повторения пароля а также проверка с исходным
            val passwordConfirmation = binding.repeatPasswordRegistrationInput.text.toString()
            var comparison = password.equals(passwordConfirmation)
            // Проверяем корректность введенных пользователем данных
            if (password == passwordConfirmation){
                Log.d(TAG, "statement is work")
            } else {
                Log.d(TAG, "not work")
            }

            if (emailCheck && passwordCheck && comparison){
                // Sign in
                auth = Firebase.auth
                signIn(email, password)
            } else {
                Toast
                    .makeText(this, "Email or password is invalid!", Toast.LENGTH_SHORT)
                    .show()
            }
            if (password == passwordConfirmation){

            }
        }
    }
    private fun signIn(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }
}