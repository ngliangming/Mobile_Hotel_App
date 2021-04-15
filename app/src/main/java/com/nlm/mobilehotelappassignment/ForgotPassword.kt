package com.nlm.mobilehotelappassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.nlm.mobilehotelappassignment.databinding.ActivityForgotPasswordBinding

class ForgotPassword : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Zenith Hotel - Forgot Password"

        binding.resetBtn.setOnClickListener{ resetPassword() }

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

    private fun resetPassword(){
        toggleUi(false)

        val emailInput = binding.emailInput
        val email = emailInput.text.toString()
        if(emailInput.toString().isEmpty()){
            emailInput.error = "Please enter your email"
            emailInput.requestFocus()

            toggleUi(true)
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.error = "Email input is invalid"
            emailInput.requestFocus()

            toggleUi(true)
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"Password reset email sent", Toast.LENGTH_SHORT).show()
                finish()
                toggleUi(true)
            }else{
                Toast.makeText(this,"${it.exception?.message}", Toast.LENGTH_SHORT).show()

                toggleUi(true)
            }
        }
    }

    private fun toggleUi(switch: Boolean) {
        if (switch) {
            Log.d("Toggle", "On")
            binding.progressBar.visibility = View.GONE
            binding.emailInput.isEnabled = true
            binding.resetBtn.isEnabled = true
            binding.resetConstraint.foreground = null
        } else {
            Log.d("Toggle", "Off")
            binding.progressBar.visibility = View.VISIBLE
            binding.emailInput.isEnabled = true
            binding.resetBtn.isEnabled = true
            binding.resetConstraint.foreground = getDrawable(R.color.semi_transparent)
        }

    }
}