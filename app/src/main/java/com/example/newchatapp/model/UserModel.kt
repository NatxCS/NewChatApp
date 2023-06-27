package com.example.newchatapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newchatapp.repository.UserRepository

class UserModel : ViewModel() {

    private val repository : UserRepository = UserRepository().getInstance()
    private val _allUsers = MutableLiveData<List<User>>()
    val allUsers : LiveData<List<User>> = _allUsers

    init {
        repository.loadUsers(_allUsers)
    }

}