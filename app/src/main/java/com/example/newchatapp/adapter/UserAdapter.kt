package com.example.newchatapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newchatapp.ChatActivity
import com.example.newchatapp.R
import com.example.newchatapp.databinding.ItemProfileBinding
import com.example.newchatapp.model.User


class UserAdapter(private var context : Context): RecyclerView.Adapter<UserAdapter.UserViewHolder>()  {

    private val userList = ArrayList<User>()
    inner class UserViewHolder(var binding : ItemProfileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemProfileBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserList(userList : List<User>){
        this.userList.clear()
        this.userList.addAll(userList)
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.binding.nameItemUser.text = currentItem.name

        Glide.with(context).load(currentItem.profileImage).placeholder(R.drawable.user_image).into(holder.binding.imageItemUser)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", currentItem.name)
            intent.putExtra("profileImage", currentItem.profileImage)
            intent.putExtra("uid", currentItem.uid)

            context.startActivity(intent)
        }
    }


}