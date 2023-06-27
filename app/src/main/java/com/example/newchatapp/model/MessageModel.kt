package com.example.newchatapp.model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newchatapp.repository.MessageRepository

class MessageModel : ViewModel() {
    private val repository: MessageRepository = MessageRepository.getInstance()
    private val _allMessages = MutableLiveData<List<Message>>()
    val allMessages: LiveData<List<Message>> = _allMessages

    fun loadMessages(senderId: String, receiverId: String) {
        repository.loadMessages(senderId, receiverId, _allMessages)

    }

    fun sendMessage(senderId: String, receiverId: String, message: String) {
        repository.sendMessage(senderId, receiverId, message)
    }

    fun sendPhoto(senderId: String,receiverId: String,imageUrl: Uri){
        repository.sendImageSender(senderId,receiverId,imageUrl)
    }

    fun deleteForEveryone(senderId: String, receiverId: String, messageId:String){
        repository.deleteMessageForEveryone(senderId,receiverId,messageId)
    }

    fun deleteForMe(senderId: String, receiverId: String, messageId:String){
        repository.deleteMessageForMe(senderId,receiverId,messageId)
    }

    fun deleteImageForEveryOne(sender: String, receiver: String, messageId: String){
        repository.deleteImageForEveryOne(sender,receiver,messageId)
    }

    fun deleteImageForMe(sender: String, receiver: String, messageId: String){
        repository.deleteImageForMe(sender, receiver, messageId)
    }


}