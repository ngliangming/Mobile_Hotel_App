package com.nlm.mobilehotelappassignment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nlm.mobilehotelappassignment.databinding.ActivityAdminHomeBinding


class adminHome : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAdminHomeBinding

    private lateinit var name: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        name = intent.getStringExtra("name").toString()

        val user = auth.currentUser
        email = user?.email.toString()

        val welcomeText = "Welcome back, $name"
        binding.welcomeTxt.text = welcomeText

        //Assign button functions
        binding.checkinBtn.setOnClickListener { checkIn() }
        binding.checkoutBtn.setOnClickListener { checkOut() }
        binding.addadminBtn.setOnClickListener { addAdmin() }
        binding.logoutBtn.setOnClickListener { logout() }
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

}