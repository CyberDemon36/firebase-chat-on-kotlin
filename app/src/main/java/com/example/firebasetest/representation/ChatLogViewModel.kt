package com.example.firebasetest.representation

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatLogViewModel(): ViewModel() {

    fun customiseSenderReference(fromId: String, toId: String): DatabaseReference {
        return FirebaseDatabase
            .getInstance("https://fir-test-9d07c-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/user-messages/$fromId/$toId").push()
    }

    fun customiseReceiverReference(fromId: String, toId: String): DatabaseReference {
        return FirebaseDatabase
            .getInstance("https://fir-test-9d07c-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/user-messages/$toId/$fromId").push()
    }

    fun customiseLastSenderMessageReference(fromId: String, toId: String): DatabaseReference {
        return FirebaseDatabase
            .getInstance("https://fir-test-9d07c-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/latest-messages/$fromId/$toId")
    }

    fun customiseLastReceiverMessageReference(fromId: String, toId: String): DatabaseReference {
        return FirebaseDatabase
            .getInstance("https://fir-test-9d07c-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/latest-messages/$toId/$fromId")
    }
}