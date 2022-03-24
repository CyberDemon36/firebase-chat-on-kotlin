package com.example.firebasetest.message

import com.example.firebasetest.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.firebasetest.views.LatestMessageRow
import com.example.firebasetest.RegistrationActivity
import com.example.firebasetest.databinding.ActivityLatestMessagesBinding
import com.example.firebasetest.message.NewMessageActivity.Companion.USER_KEY
import com.example.firebasetest.models.ChatMessage
import com.example.firebasetest.models.User
import com.example.firebasetest.representation.LatestMessageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class LatestMessagesActivity : AppCompatActivity() {
    private lateinit var vm: LatestMessageViewModel
    private val adapter = GroupAdapter<ViewHolder>()
    lateinit var binding: ActivityLatestMessagesBinding

    companion object {
        var currentUser: User? = null
        val TAG = LatestMessagesActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatestMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this).get(LatestMessageViewModel::class.java)
        vm.latestMessagesMap.observe(this, Observer {
            adapter.clear()
            it.values.forEach {
                adapter.add(LatestMessageRow(it, this))
            }
            binding.swiperefresh.isRefreshing = false
        })
        vm.savedCurrentUser.observe(this, Observer {
            currentUser = it
        })

        verifyUserIsLoggedIn()

        binding.recyclerviewLatestMessages.adapter = adapter

        binding.swiperefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.teal_200))

        vm.fetchCurrentUser()
        listenForLatestMessages()

        adapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }


        binding.newMessageFab.setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        binding.swiperefresh.setOnRefreshListener {
            verifyUserIsLoggedIn()
            vm.fetchCurrentUser()
            listenForLatestMessages()
        }
    }

    private fun listenForLatestMessages() {
        binding.swiperefresh.isRefreshing = true
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase
            .getInstance("https://fir-test-9d07c-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/latest-messages/$fromId")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(LatestMessagesActivity.TAG, "database error: " + databaseError.message)
            }


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(LatestMessagesActivity.TAG, "has children: " + dataSnapshot.hasChildren())
                if (!dataSnapshot.hasChildren()) {
                    binding.swiperefresh.isRefreshing = false
                }
            }

        })
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    vm.addHashSet(dataSnapshot.key!!, it)
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    vm.addHashSet(dataSnapshot.key!!, it)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {

            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegistrationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}