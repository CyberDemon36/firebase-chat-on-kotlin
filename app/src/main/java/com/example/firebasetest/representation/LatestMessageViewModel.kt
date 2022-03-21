package com.example.firebasetest.representation

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebasetest.message.LatestMessagesActivity
import com.example.firebasetest.models.ChatMessage
import com.example.firebasetest.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.core.view.View
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class LatestMessageViewModel(): ViewModel() {
    public var liveDataLatestMessagesMap = MutableLiveData<HashMap<String, ChatMessage>>()
    public var latestMessagesMap = HashMap<String, ChatMessage>()
    public val savedCurrentUser = MutableLiveData<User>()
  /*  public var hashMap: HashMap <String, ChatMessage> = hashMapOf()
    public fun addHashSetToLiveData(){
        latestMessagesMap.value = hashMap
    }*/
    public fun addHashSet(str: String, cm: ChatMessage){
      latestMessagesMap[str] = cm
      liveDataLatestMessagesMap.value = latestMessagesMap
    }
   public fun fetchCurrentUser() {
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