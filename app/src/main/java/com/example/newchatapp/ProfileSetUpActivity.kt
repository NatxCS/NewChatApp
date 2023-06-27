package com.example.newchatapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.newchatapp.databinding.ActivityProfileSetUpBinding
import com.example.newchatapp.model.ProfileSetUpViewModel

class ProfileSetUpActivity : AppCompatActivity() {

private lateinit var binding: ActivityProfileSetUpBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var dialog: Dialog
    private lateinit var viewModel: ProfileSetUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this@ProfileSetUpActivity, R.style.AlertDialogTheme)
        dialog.setContentView(R.layout.dialog_progress_profile)
        dialog.setCanceledOnTouchOutside(false)

        viewModel = ViewModelProvider(this)[ProfileSetUpViewModel::class.java]

        //Checks if the user already has a profile picture and name, to send it to the main activity
        viewModel.checkProfileSetUp(this@ProfileSetUpActivity)

        //This gives the actions to the imageBox
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.setSelectedImageUri(uri)
                    binding.imageProfile.setImageURI(uri)
                }
            }
        }
        binding.imageProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }


        binding.btnOfProfileSetUp.setOnClickListener {
            val userName = binding.NameOfUser.text.toString()

            if (userName.isEmpty()) {
                binding.NameOfUser.error = "Type your Name"
            }

            dialog.show()
            viewModel.uploadUserProfile(userName, dialog) {
                navigateToMainActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
