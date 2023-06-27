package com.example.newchatapp.repository


import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.newchatapp.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MessageRepository {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("messages")
    private val storage: StorageReference = FirebaseStorage.getInstance().reference.child("ChatImages")

    companion object {
        @Volatile
        private var INSTANCE: MessageRepository? = null

        fun getInstance(): MessageRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MessageRepository()
                INSTANCE = instance
                instance
            }
        }
    }

    fun loadMessages(
        senderId: String,
        receiverId: String,
        messageList: MutableLiveData<List<Message>>,
    ) {
        databaseReference.child(senderId).child(receiverId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages: MutableList<Message> = mutableListOf()
                    for (dataSnapshot in snapshot.children) {
                        val message = dataSnapshot.getValue(Message::class.java)
                        message?.let {
                            messages.add(it)
                        }
                    }
                    messageList.postValue(messages)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled event if needed
                }
            })
    }

    fun sendMessage(senderId: String, receiverId: String, message: String) {
        val timestamp = System.currentTimeMillis()
        val messageId = databaseReference.push().key ?: ""
        val imageUrl = "no image"
        val messageObj = Message(messageId, message, senderId, receiverId, timestamp, imageUrl)
        databaseReference.child(senderId).child(receiverId).child(messageId).setValue(messageObj)
        databaseReference.child(receiverId).child(senderId).child(messageId).setValue(messageObj)
    }

    fun sendImageSender(senderId: String, receiverId: String, imageUrl: Uri) {
        val message = "photo"
        val timestamp = System.currentTimeMillis()
        val messageId = databaseReference.push().key ?: ""

        if (imageUrl != null) {
            val reference = storage.child(senderId).child(messageId)
            reference.putFile(imageUrl).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the download URL
                    reference.downloadUrl.addOnSuccessListener { uri ->
                        val image = uri.toString()
                        val messageObj = Message(messageId, message, senderId, receiverId, timestamp, image)

                        databaseReference.child(senderId).child(receiverId).child(messageId)
                            .setValue(messageObj)
                        databaseReference.child(receiverId).child(senderId).child(messageId)
                            .setValue(messageObj)
                    }.addOnFailureListener { exception ->
                        Log.e("error saving image",exception.message.toString())
                    }
                }
            }
        }
    }

    fun deleteMessageForEveryone(sender: String, receiver: String, messageId: String) {
        val timestamp = System.currentTimeMillis()
        val imageUrl = "no image"
        val newMessage =
            Message(messageId, "Message Deleted", sender, receiver, timestamp, imageUrl)
        databaseReference.child(sender).child(receiver).child(messageId).setValue(newMessage)
        databaseReference.child(receiver).child(sender).child(messageId).setValue(newMessage)
    }

    fun deleteMessageForMe(sender: String, receiver: String, messageId: String) {
        val imageUrl = "no image"
        val timestamp = System.currentTimeMillis()
        val newMessage =
            Message(messageId, "Message Deleted", receiver, sender, timestamp, imageUrl)
        databaseReference.child(sender).child(receiver).child(messageId).setValue(newMessage)

    }

    fun deleteImageForEveryOne(sender: String, receiver: String, messageId: String) {
        val timestamp = System.currentTimeMillis()
        val reference = storage.child(sender).child(messageId)
        reference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                    val message = "Message Deleted"
                    val image = "no image"
                    val messageObj = Message(messageId, message, sender, receiver, timestamp, image)
                    databaseReference.child(sender).child(receiver).child(messageId).setValue(messageObj)
                    databaseReference.child(receiver).child(sender).child(messageId).setValue(messageObj)
            }
        }
    }

    fun deleteImageForMe(sender: String, receiver: String, messageId: String){
        val timestamp = System.currentTimeMillis()
        val reference = storage.child(sender).child(messageId)
        reference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newMessage = "Message Deleted"
                val image = "no image"
                val messageObj = Message(messageId, newMessage, receiver, sender, timestamp, image)
                databaseReference.child(sender).child(receiver).child(messageId).setValue(messageObj)
            }
        }
    }


}
