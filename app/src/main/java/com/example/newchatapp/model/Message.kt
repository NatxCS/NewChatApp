package com.example.newchatapp.model



data class Message(
    var messageId:String? = null,
    var message:String? = null,
    var senderId:String? = null,
    var receiverId: String? = null,
    var timeStamp:Long? = null,
    var imageUrl:String? = null
)
