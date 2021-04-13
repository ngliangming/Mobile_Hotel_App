package com.nlm.mobilehotelappassignment

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.nlm.mobilehotelappassignment.databinding.ActivityCustomerHomeBinding


class customerHome : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerHomeBinding

    private lateinit var userId: String
    private lateinit var name: String
    private lateinit var password: String
    private lateinit var email: String
    private var adminLevel: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId").toString()
        name = intent.getStringExtra("name").toString()
        password = intent.getStringExtra("password").toString()
        email = intent.getStringExtra("email").toString()
        adminLevel = intent.getIntExtra("adminLevel", -1)

        val welcomeText = "Welcome back, $name"
        binding.welcomeTxt.text = welcomeText

        //Change action bar title
        title = "Zenith Hotel";

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
            .putExtra("userId", userId)
            .putExtra("email", email)

        startActivity(intent)
    }

    private fun viewRooms() {
        val intent = Intent(this, ViewRooms::class.java)
            .putExtra("userId", userId)

        startActivity(intent)
    }
    private fun editProfile(){
//        TODO add activity into Intent
//        val intent = Intent(this, ???????::class.java)
//            .putExtra("userId", userId)
//
//        startActivity(intent)
    }

    private fun logout() {
        var result = true;
        val returnIntent = Intent()
        returnIntent.putExtra("result", result)
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}