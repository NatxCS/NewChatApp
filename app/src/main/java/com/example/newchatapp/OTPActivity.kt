package com.example.newchatapp

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.newchatapp.databinding.ActivityOtpBinding
import com.example.newchatapp.repository.OTPRepository
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


class OTPActivity : AppCompatActivity() {

private lateinit var binding: ActivityOtpBinding
    private lateinit var dialog: Dialog
    private lateinit var verificationId: String
    private val otpRepository = OTPRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOtpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        dialog = Dialog(this, R.style.AlertDialogTheme)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        val phoneNumber = intent.getStringExtra("phoneNumber")
        binding.phoneNumberToVerify.text = phoneNumber

        otpRepository.verifyPhoneNumber(
            phoneNumber!!,
            { credential -> signInWithPhoneAuthCredential(credential) },
            { e -> Toast.makeText(this@OTPActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show() },
            { verificationId ->
                this@OTPActivity.verificationId = verificationId
                dialog.hide()
                Toast.makeText(this@OTPActivity, "Verification code sent", Toast.LENGTH_SHORT).show()
            },
            this@OTPActivity
        )

        binding.OtpToEntercode.setOtpCompletionListener { otp ->
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            otpRepository.signInWithPhoneAuthCredential(
                credential,
                {
                    val intent = Intent(this@OTPActivity, ProfileSetUpActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                { Toast.makeText(this@OTPActivity, "Failed", Toast.LENGTH_SHORT).show() }
            )
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        otpRepository.signInWithPhoneAuthCredential(
            credential,
            {
                val intent = Intent(this@OTPActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            },
            { Toast.makeText(this@OTPActivity, "Failed", Toast.LENGTH_SHORT).show() }
        )
    }
}