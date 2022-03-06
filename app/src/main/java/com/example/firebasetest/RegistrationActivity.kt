package com.example.firebasetest

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.firebasetest.databinding.RegistrationActivityLayoutBinding
import com.example.firebasetest.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class RegistrationActivity: AppCompatActivity() {
    lateinit var binding: RegistrationActivityLayoutBinding
    lateinit var auth: FirebaseAuth
    private var selectedPhotoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    override fun onStart() {
        super.onStart()
        binding.btnAvatarSelecter.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
        binding.btnSignIn.setOnClickListener{
            performRegistration()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data ?: return
            Log.d(TAG, "Photo was selected" + selectedPhotoUri.toString())
            // Get and resize profile image
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            contentResolver.query(selectedPhotoUri!!, filePathColumn, null, null, null)?.use {
                it.moveToFirst()
                val columnIndex = it.getColumnIndex(filePathColumn[0])
                val picturePath = it.getString(columnIndex)
                // If picture chosen from camera rotate by 270 degrees else
                if (picturePath.contains("DCIM")) {
                    Picasso.get().load(selectedPhotoUri).rotate(270f).into(binding.avatarImageview)
                } else {
                    Picasso.get().load(selectedPhotoUri).into(binding.avatarImageview)
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private fun performRegistration(){
        // Проверка email адреса на соответствие паттерну
        val email = binding.emailRegistrationInput.text.toString()
        val emailRegexPattern = Regex("[\\w~!@#$%^&*+-]+@[a-z]+\\.[a-z]{2,}")
        val emailCheck = emailRegexPattern.matches(email)
        // Также, пароль должен содержать как минимум 6 символов
        val password = binding.passwordRegistrationInput.text.toString()
        val passwordRegexPattern = Regex("[\\w~!@#$%^&*+-]{6,}")
        val passwordCheck = passwordRegexPattern.matches(password)
        // Поле для повторения пароля а также проверка с исходным
        val passwordConfirmation = binding.repeatPasswordRegistrationInput.text.toString()
        val comparison = password.equals(passwordConfirmation)
        // Проверяем корректность введенных пользователем данных
        if (password == passwordConfirmation){
            Log.d(TAG, "statement is work")
        } else {
            Log.d(TAG, "not work")
        }
        // Если все проверки прошли успешно - пользователь зарегистрирован
        if (emailCheck && passwordCheck && comparison){
            auth = Firebase.auth
            signIn(email, password)
        } else {
            // В противном случае выводим соответсвующее сообщение
            Toast
                .makeText(this, "Email or password is invalid!", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun signIn(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    uploadAvatarToFirestore()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }
    private fun uploadAvatarToFirestore(){
        if (selectedPhotoUri == null) {
            // save user without photo
            saveUserToFirebaseDatabase(null)
        } else {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")
                    @Suppress("NestedLambdaShadowedImplicitParameter")
                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File Location: $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to upload image to storage: ${it.message}")
                    /*loading_view.visibility = View.GONE
                    already_have_account_text_view.visibility = View.VISIBLE*/
                }
        }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String?) {
        val uid = FirebaseAuth.getInstance().uid ?: return
        Log.d(TAG, "Firebase unique id -> $uid")
        val ref = FirebaseDatabase
            .getInstance("https://fir-test-9d07c-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("/users/$uid")
        Log.d(TAG, "Firebase database reference -> $ref")

        val user = if (profileImageUrl == null) {
            User(uid, binding.nicknameRegistrationInput.text.toString(), null)
        } else {
            User(uid, binding.nicknameRegistrationInput.text.toString(), profileImageUrl)
        }
        Log.d(TAG, "User instance -> $user")

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                //val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //overridePendingTransition(R.anim.enter, R.anim.exit)
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
                /*loading_view.visibility = View.GONE
                already_have_account_text_view.visibility = View.VISIBLE*/
            }

    }
}