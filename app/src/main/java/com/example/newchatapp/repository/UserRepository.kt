package com.example.newchatapp.repository

import androidx.lifecycle.MutableLiveData
import com.example.newchatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class UserRepository {

    private var databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("users")



    @Volatile private var INSTANCE : UserRepository ?= null

    fun getInstance() : UserRepository{
        return INSTANCE ?: synchronized(this){
            val instance = UserRepository()
            INSTANCE = instance
            instance
        }
    }

    fun loadUsers(userList: MutableLiveData<List<User>>) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val dbUserList: MutableList<User> = mutableListOf()

                    for (dataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue(User::class.java)!!

                        getUserProfileImageDownloadUrl(user.uid) { downloadUrl ->

                            user.profileImage = downloadUrl
                            dbUserList.add(user)


                            if (dbUserList.size == snapshot.childrenCount.toInt()) {
                                userList.postValue(dbUserList)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Handle exceptions if any
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })
    }

    private fun getUserProfileImageDownloadUrl(uid: String?, callback: (String?) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val profileImageRef = storageReference.child("Profile/$uid")
        profileImageRef.downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri.toString())
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null)
            }
    }
}