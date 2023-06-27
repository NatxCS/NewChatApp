package com.example.newchatapp.model

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.newchatapp.MainActivity
import com.example.newchatapp.repository.ProfileSetUpRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileSetUpViewModel : ViewModel() {
    private val repository = ProfileSetUpRepository()

    fun checkProfileSetUp(context: Context) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            repository.checkProfileSetUp(uid) { isProfileSetUp ->
                if (isProfileSetUp) {
                    navigateToMainActivity(context)
                }
            }
        }
    }

    fun setSelectedImageUri(uri: Uri) {
        repository.setSelectedImageUri(uri)
    }

    fun uploadUserProfile(userName: String, dialog: Dialog, onComplete: () -> Unit) {
        val selectedImageUri = repository.getSelectedImageUri()
        repository.uploadUserProfile(userName, selectedImageUri, dialog) {
            onComplete.invoke()
        }
    }

    private fun navigateToMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}