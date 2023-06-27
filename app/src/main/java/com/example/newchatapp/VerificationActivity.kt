package com.example.newchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.newchatapp.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVerificationBinding
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //If the user is already logIn, open the MainActivity
        if(auth.currentUser != null){
            val intent = Intent(this, ProfileSetUpActivity::class.java)
            startActivity(intent)
            finish()
        }
        //The user is not logIn, starts the process to login
        binding.PhoneNumberOfUser.requestFocus()
        binding.btnOfVerification.setOnClickListener {
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("phoneNumber",binding.PhoneNumberOfUser.text.toString())
            startActivity(intent)
            finish()
        }
    }
}