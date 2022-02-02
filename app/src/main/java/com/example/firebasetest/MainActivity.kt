package com.example.firebasetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       /* val database = Firebase.database("https://fir-test-9d07c-default-rtdb.europe-west1.firebasedatabase.app")
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")*/

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

    }
}