package com.example.newchatapp.adapter

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newchatapp.R
import com.example.newchatapp.databinding.DeleteLayoutBinding
import com.example.newchatapp.databinding.DeleteMessageReceiverBinding
import com.example.newchatapp.databinding.ReceiveMsgBinding
import com.example.newchatapp.databinding.SendMsgBinding
import com.example.newchatapp.model.Message
import com.example.newchatapp.model.MessageModel


class MessageAdapter(private val context : Context, private val senderId: String, private val model : MessageModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messageList = ArrayList<Message>()
    private val dialog = Dialog(context, R.style.AlertDialogTheme)


    companion object {
        private const val VIEW_TYPE_SENDER = 1
        private const val VIEW_TYPE_RECEIVER = 2
    }

    inner class SenderViewHolder(val binding: SendMsgBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ReceiverViewHolder(val binding: ReceiveMsgBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                val binding =  SendMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SenderViewHolder(binding)
            }
            VIEW_TYPE_RECEIVER -> {
                val binding = ReceiveMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ReceiverViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        val messageId = message.messageId
        val receiver = message.receiverId
        val sender = message.senderId



        when (holder.itemViewType) {
            VIEW_TYPE_SENDER -> {
                val senderHolder = holder as SenderViewHolder
                senderHolder.binding.messageTextView.visibility = View.VISIBLE
                senderHolder.binding.imageView.visibility = View.GONE
                if (message.imageUrl.equals("no image")) {
                    senderHolder.binding.messageTextView.text = message.message
                } else if (message.message.equals("photo")) {
                    senderHolder.binding.imageView.visibility = View.VISIBLE
                    senderHolder.binding.messageTextView.visibility = View.GONE
                    Glide.with(context).load(message.imageUrl).placeholder(R.drawable.placeholder)
                        .into(senderHolder.binding.imageView)
                }
            }
            VIEW_TYPE_RECEIVER -> {
                val receiverHolder = holder as ReceiverViewHolder
                receiverHolder.binding.messageTextView2.visibility = View.VISIBLE
                receiverHolder.binding.imageView2.visibility = View.GONE

                if (message.imageUrl.equals("no image")) {
                    receiverHolder.binding.messageTextView2.text = message.message
                } else if (message.message.equals("photo")) {
                    receiverHolder.binding.imageView2.visibility = View.VISIBLE
                    receiverHolder.binding.messageTextView2.visibility = View.GONE
                    Glide.with(context).load(message.imageUrl).placeholder(R.drawable.placeholder)
                        .into(receiverHolder.binding.imageView2)
                }
            }
        }

        //Function of delete messages and photos
        holder.itemView.setOnLongClickListener {
                if(holder.itemViewType == VIEW_TYPE_SENDER){
                    dialog.setContentView(R.layout.delete_layout)
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                    val binding = DeleteLayoutBinding.bind(dialog.findViewById(R.id.delete_layout))

                    binding.deleteEveryone.setOnClickListener {
                        model.deleteForEveryone(senderId,receiver!!,messageId!!)
                        dialog.hide()
                    }

                    binding.deleteMe.setOnClickListener {
                        model.deleteForMe(senderId,receiver!!,messageId!!)

                        dialog.hide()
                    }

                    binding.optionCancel.setOnClickListener {
                        dialog.hide()
                    }


                }else if(holder.itemViewType == VIEW_TYPE_RECEIVER){
                    dialog.setContentView(R.layout.delete_message_receiver)
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                    val binding2 = DeleteMessageReceiverBinding.bind(dialog.findViewById(R.id.delete_layout_receiver))

                    binding2.deleteMeReceiver.setOnClickListener {
                        model.deleteForMe(senderId,sender!!,messageId!!)
                        dialog.hide()
                    }

                    binding2.optionCancelReceiver.setOnClickListener {
                        dialog.hide()
                    }
                }else if(holder.itemViewType == VIEW_TYPE_SENDER && message.imageUrl != "no image"){
                    dialog.setContentView(R.layout.delete_layout)
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                    val senderHolder = holder as SenderViewHolder
                    val binding = DeleteLayoutBinding.bind(dialog.findViewById(R.id.delete_layout))

                    binding.deleteEveryone.setOnClickListener {
                        model.deleteImageForEveryOne(senderId,receiver!!,messageId!!)
                        senderHolder.binding.imageView.visibility = View.GONE
                        senderHolder.binding.messageTextView.visibility = View.VISIBLE
                        dialog.hide()
                    }

                    binding.deleteMe.setOnClickListener {
                        model.deleteImageForMe(senderId,receiver!!,messageId!!)
                        senderHolder.binding.imageView.visibility = View.GONE
                        senderHolder.binding.messageTextView.visibility = View.VISIBLE
                        dialog.hide()
                    }

                    binding.optionCancel.setOnClickListener {
                        dialog.hide()
                    }
                }else if(holder.itemViewType == VIEW_TYPE_RECEIVER && message.imageUrl != "no image"){
                    dialog.setContentView(R.layout.delete_message_receiver)
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                    val senderHolder = holder as SenderViewHolder
                    val binding = DeleteLayoutBinding.bind(dialog.findViewById(R.id.delete_layout_receiver))

                    binding.deleteMe.setOnClickListener {
                        model.deleteImageForMe(senderId,sender!!,messageId!!)
                        senderHolder.binding.imageView.visibility = View.GONE
                        senderHolder.binding.messageTextView.visibility = View.VISIBLE
                        dialog.hide()
                    }

                    binding.optionCancel.setOnClickListener {
                        dialog.hide()
                    }
                }


                false
            }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    //Function to get the type of message
    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        // Determine the view type based on the senderId
        return if (message.senderId == senderId) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }

    fun updateMessageList(messages: List<Message>) {
        messageList.clear()
        messageList.addAll(messages)
        notifyDataSetChanged()
    }
}