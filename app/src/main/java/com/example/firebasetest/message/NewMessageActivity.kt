package com.example.firebasetest.message

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebasetest.R
import com.example.firebasetest.models.User
import com.example.firebasetest.views.BigImageDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firebasetest.representation.NewMessageActivityViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    private val adapter = GroupAdapter<ViewHolder>()
    private lateinit var vm: ViewModel
    private val ref = FirebaseDatabase
        .getInstance("https://fir-test-9d07c-default-rtdb.europe-west1.firebasedatabase.app")
        .getReference("/users")

    companion object {
        const val USER_KEY = "USER_KEY"
        private val TAG = NewMessageActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        vm = ViewModelProvider(this).get(NewMessageActivityViewModel::class.java)
        (vm as NewMessageActivityViewModel).searchValueToLiveData.observe(this, Observer {
            val filterOption = (vm as NewMessageActivityViewModel).searchValueToLiveData.value
            fetchUsers(filterOption!!)
        })

        swiperefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))

        supportActionBar?.title = "Select User"
        (vm as NewMessageActivityViewModel).setValueToSearchBarLiveData("")
        //Todo - Add more users and messages for screenshots

        swiperefresh.setOnRefreshListener {
            (vm as NewMessageActivityViewModel).setValueToSearchBarLiveData("")
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.setOnItemClickListener { item, view ->
            val userItem = item as UserItem
            val intent = Intent(view.context, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, userItem.user)
            startActivity(intent)
            finish()
        }
    }
   private fun fetchUsers(filterOption: String) {
       swiperefresh.isRefreshing = true

       ref.addListenerForSingleValueEvent(object : ValueEventListener {
           override fun onCancelled(databaseError: DatabaseError) {

           }

           override fun onDataChange(dataSnapshot: DataSnapshot) {
               adapter.clear()

               dataSnapshot.children.forEach {
                   Log.d(TAG, it.toString())
                   if (filterOption.isNotEmpty()) {
                       it.getValue(User::class.java)?.let {
                           val userFilter = Regex("$filterOption.*")
                           if (it.uid != FirebaseAuth.getInstance().uid && userFilter.matches(it.name)) {
                               adapter.add(UserItem(it, this@NewMessageActivity))
                           }
                       }
                   } else {
                       it.getValue(User::class.java)?.let {
                           if (it.uid != FirebaseAuth.getInstance().uid) {
                               adapter.add(UserItem(it, this@NewMessageActivity))
                           }
                       }
                   }
               }
               recyclerview_newmessage.adapter = adapter
               swiperefresh.isRefreshing = false
           }
       })
   }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_bar_menu, menu)

        val search = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Search users"

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                (vm as NewMessageActivityViewModel).setValueToSearchBarLiveData(p0!!)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}

class UserItem(val user: User, val context: Context) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.name

        if (!user.profileImageUrl!!.isEmpty()) {
            val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)


            Glide.with(viewHolder.itemView.imageview_new_message.context)
                .load(user.profileImageUrl)
                .apply(requestOptions)
                .into(viewHolder.itemView.imageview_new_message)

            viewHolder.itemView.imageview_new_message.setOnClickListener {
                BigImageDialog.newInstance(user?.profileImageUrl!!).show((context as Activity).fragmentManager
                    , "")
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}
