package com.example.newchatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.newchatapp.adapter.MessageAdapter
import com.example.newchatapp.databinding.ActivityChatBinding
import com.example.newchatapp.model.MessageModel
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: MessageModel
    private lateinit var adapter: MessageAdapter
    private var auth = FirebaseAuth.getInstance()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectedImageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //profile name-profile photo
        binding.chatUserName.text = intent.getStringExtra("name")
        val profilePhoto = intent.getStringExtra("profileImage")
        Glide.with(this).load(profilePhoto).placeholder(R.drawable.user_image)
            .into(binding.chatImageItemUser)

        //back arrow function
        binding.chatBackArrow.setOnClickListener { finish() }


        // Get the sender and receiver IDs from the intent or any other source
        val senderId = auth.uid.toString()
        val receiverId = intent.getStringExtra("uid")


        viewModel = ViewModelProvider(this)[MessageModel::class.java]


        binding.chatRecycler.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(this@ChatActivity, senderId, viewModel)
        binding.chatRecycler.adapter = adapter

        // Observe the list of messages
        viewModel.allMessages.observe(this) { messages ->
            adapter.updateMessageList(messages)
            // Scroll to the last message
            binding.chatRecycler.scrollToPosition(adapter.itemCount - 1)
        }


        viewModel.loadMessages(senderId, receiverId!!)


        binding.chatSendButton.setOnClickListener {
            val message = binding.editMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(senderId, receiverId, message)
                binding.editMessage.text.clear()
            }
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    viewModel.sendPhoto(senderId, receiverId, selectedImageUri)
                }
            }
        }

        binding.chatAttach.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }
    }
}