package com.example.newchatapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.newchatapp.adapter.UserAdapter
import com.example.newchatapp.databinding.ActivityMainBinding
import com.example.newchatapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : UserModel
    private lateinit var adapter: UserAdapter
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        adapter = UserAdapter(this@MainActivity)
        binding.recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[UserModel::class.java]

        viewModel.allUsers.observe(this) {
            adapter.updateUserList(it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.logout ->{
                auth.signOut()
                val intent = Intent(this@MainActivity, VerificationActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}





