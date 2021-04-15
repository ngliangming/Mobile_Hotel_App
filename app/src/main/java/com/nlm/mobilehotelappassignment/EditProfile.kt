package com.nlm.mobilehotelappassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.nlm.mobilehotelappassignment.databinding.ActivityEditProfileBinding

class EditProfile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Zenith Hotel - Forgot Password"

        binding.changeBtn.setOnClickListener { update() }

//        Enable action bar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggleUi(true)
    }

    //    Assign back button function
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun update() {

    }

    private fun toggleUi(switch: Boolean) {
        if (switch) {
            Log.d("Toggle", "On")
            binding.progressBar.visibility = View.GONE
            binding.username.isEnabled = true
            binding.passwordInput.isEnabled = true
            binding.passwordRepeatInput.isEnabled = true
            binding.changeBtn.isEnabled = true
            binding.editConstraint.foreground = null
        } else {
            Log.d("Toggle", "Off")
            binding.progressBar.visibility = View.VISIBLE
            binding.username.isEnabled = true
            binding.passwordInput.isEnabled = true
            binding.passwordRepeatInput.isEnabled = true
            binding.changeBtn.isEnabled = true
            binding.editConstraint.foreground = getDrawable(R.color.semi_transparent)
        }

    }
}