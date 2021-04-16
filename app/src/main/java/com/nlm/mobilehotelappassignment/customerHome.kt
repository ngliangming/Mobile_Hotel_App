package com.nlm.mobilehotelappassignment

import android.R.attr.data
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nlm.mobilehotelappassignment.databinding.ActivityCustomerHomeBinding


class customerHome : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCustomerHomeBinding

    private lateinit var name: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        name = intent.getStringExtra("name").toString()

        val user = auth.currentUser
        email = user?.email.toString()

        val welcomeText = "Welcome back, $name"
        binding.welcomeTxt.text = welcomeText

        //Change action bar title
        title = "Zenith Hotel"

        //Assign button functions
        binding.bookBtn.setOnClickListener { bookRoom() }
        binding.viewBtn.setOnClickListener { viewRooms() }
        binding.profileBtn.setOnClickListener { editProfile() }
        binding.logoutBtn.setOnClickListener { logout() }

//        Enable action bar back button
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

//    Assign back button function
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun bookRoom() {
        val intent = Intent(this, bookRoom::class.java)
            .putExtra("email", email)

        startActivity(intent)
    }

    private fun viewRooms() {
        val intent = Intent(this, ViewRooms::class.java)
            .putExtra("email", email)

        startActivity(intent)
    }

    private fun editProfile() {
        startForResult.launch(
            Intent(this, EditProfile::class.java)
                .putExtra("name", name)
                .putExtra("email", email)
        )
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                logout()
            }

            val data: Intent? = result.data
            name = data?.getStringExtra("name").toString()
            if (name != "") {
                val welcomeText = "Welcome back, $name"
                binding.welcomeTxt.text = welcomeText
            }
        }

    private fun logout() {
        auth.signOut()

        val result = true
        val returnIntent = Intent()
        returnIntent.putExtra("result", result)
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}