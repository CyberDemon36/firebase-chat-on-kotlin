package com.example.firebasetest.representation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebasetest.models.ChatMessage
import com.example.firebasetest.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LatestMessageViewModel(): ViewModel() {
    private var mutableDataLatestMessagesMap = MutableLiveData<HashMap<String, ChatMessage>>()
    private var mapsForFinalResult: HashMap<String, ChatMessage> = hashMapOf()
    var latestMessagesMap: LiveData<HashMap<String, ChatMessage>> = mutableDataLatestMessagesMap
    val savedCurrentUser = MutableLiveData<User>()
  /*  public var hashMap: HashMap <String, ChatMessage> = hashMapOf()
    public fun addHashSetToLiveData(){
        latestMessagesMap.value = hashMap
    }*/
    fun addHashSet(str: String, cm: ChatMessage){
      mapsForFinalResult[str] = cm
      mutableDataLatestMessagesMap.value = mapsForFinalResult
    }
   fun fetchCurrentUser() {
       val uid = FirebaseAuth.getInstance().uid ?: return
       val ref = FirebaseDatabase
           .getInstance("https://fir-test-9d07c-default-rtdb.europe-west1.firebasedatabase.app")
           .getReference("/users/$uid")
       ref.addListenerForSingleValueEvent(object : ValueEventListener {
           override fun onCancelled(databaseError: DatabaseError) {
           }

           override fun onDataChange(dataSnapshot: DataSnapshot) {
               savedCurrentUser.value = dataSnapshot.getValue(User::class.java)
           }

       })
   }
}