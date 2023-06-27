package com.example.newchatapp.repository

import android.app.Dialog
import android.net.Uri
import com.example.newchatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileSetUpRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var selectedImageUri: Uri? = null

    fun checkProfileSetUp(uid: String, onComplete: (Boolean) -> Unit) {
        val databaseReference = database.reference.child("users").child(uid)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val isProfileSetUp = user != null && isProfileSetUp(user)
                onComplete.invoke(isProfileSetUp)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onComplete.invoke(false)
            }
        })
    }

    fun setSelectedImageUri(uri: Uri) {
        selectedImageUri = uri
    }

    fun getSelectedImageUri(): Uri? {
        return selectedImageUri
    }

    fun uploadUserProfile(userName: String, selectedImageUri: Uri?, dialog: Dialog, onComplete: () -> Unit) {
        if (selectedImageUri != null) {
            val reference = storage.reference.child("Profile").child(auth.uid!!)
            reference.putFile(selectedImageUri).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reference.downloadUrl.addOnCompleteListener { uri ->
                        val imageUrl = uri.toString()
                        val uid = auth.uid
                        val phone = auth.currentUser!!.phoneNumber
                        val user = User(uid, userName, phone, imageUrl)
                        database.reference.child("users").child(uid!!).setValue(user).addOnCompleteListener {
                            dialog.dismiss()
                            onComplete.invoke()
                        }
                    }
                } else {
                    val uid = auth.uid
                    val phone = auth.currentUser!!.phoneNumber
                    val user = User(uid, userName, phone, "No image")
                    database.reference.child("users").child(uid!!).setValue(user).addOnCompleteListener {
                        dialog.dismiss()
                        onComplete.invoke()
                    }
                }
            }
        }
    }

    private fun isProfileSetUp(user: User): Boolean {
        // Check if the user's name and profile image are set
        return !user.name.isNullOrEmpty() && !user.profileImage.isNullOrEmpty()
    }
}