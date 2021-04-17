package com.nlm.mobilehotelappassignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nlm.mobilehotelappassignment.databinding.ActivityAdminHomeBinding


class adminHome : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAdminHomeBinding

    private lateinit var name: String
    private var adminLevel = -1
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        name = intent.getStringExtra("name").toString()
        adminLevel = intent.getIntExtra("adminLevel",-1)

        val user = auth.currentUser
        email = user?.email.toString()

        val welcomeText = "Welcome back, $name"
        binding.welcomeTxt.text = welcomeText

        //Assign button functions
        binding.checkinBtn.setOnClickListener { checkIn() }
        binding.checkoutBtn.setOnClickListener { checkOut() }
        binding.logoutBtn.setOnClickListener { logout() }
        binding.editAdminProfile.setOnClickListener { editProfile() }

        if (adminLevel == 1) {
            binding.addadminBtn.visibility = View.GONE
        } else {
            binding.addadminBtn.setOnClickListener { addAdmin() }
        }
    }

    private fun checkIn() {
        val intent = Intent(this, checkIn::class.java)
            .putExtra("email", email)

        startActivity(intent)
    }

    private fun checkOut() {
        val intent = Intent(this, checkOut::class.java)
            .putExtra("email", email)

        startActivity(intent)
    }

    private fun addAdmin() {
        val intent = Intent(this, AddAdmin::class.java)
        //.putExtra("email", email)

        startActivity(intent)
    }

    private fun logout() {
        auth.signOut()

        val result = true
        val returnIntent = Intent()
        returnIntent.putExtra("result", result)
        setResult(RESULT_OK, returnIntent)
        finish()
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
}