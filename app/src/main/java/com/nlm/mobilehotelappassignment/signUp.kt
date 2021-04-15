package com.nlm.mobilehotelappassignment

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivitySignUpBinding
import java.util.regex.Pattern

class signUp : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Zenith Hotel - Signup"

        auth = FirebaseAuth.getInstance()

        binding.createuserButton.setOnClickListener { createUserBtn() }

        toggleUi(true)

//        Enable action bar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    //check for email pattern
    private val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    //email function to check email input from user
    private fun checkEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    private fun createUserBtn() {
        toggleUi(false)
        val tag = "Firestore Out"

        var valid = true
        val emailInput = binding.emailInput.text.toString()
        val nameInput = binding.nameSignup.text.toString()
        val passwordInput = binding.passwordSignup.text.toString()
        val level = 0

        val userProf = hashMapOf(
            "username" to nameInput,
            "adminLevel" to level
        )

        if (nameInput.isEmpty()) { //if name is  null
            binding.nameSignup.error = "Please enter a name"
            valid = false
        }

        if (passwordInput.length < 6) { //if password too short
            binding.passwordSignup.error = "Password too short!"
            valid = false
        }

        if (emailInput.isNotEmpty()) { // if not empty
            if (checkEmail(emailInput)) { // is correct email format ?
                //If Valid
                if (valid) {

                    //Create User in Auth
                    auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener {

                            //Created
                            if (it.isSuccessful) {

                                //Add info to database
                                db.collection("users")
                                    .document(emailInput)
                                    .set(userProf)
                                    .addOnSuccessListener {

                                        Log.d(tag, "USER Info added with ID: $emailInput")

                                        //Send Email Verify
                                        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                                            ?.addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    Toast.makeText(
                                                        baseContext,
                                                        "Email verification sent.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    auth.signOut()
                                                    finish()
                                                } else {
                                                    Toast.makeText(
                                                        baseContext,
                                                        "Register failed.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    toggleUi(true)
                                                }
                                            }

                                    }

                            } else {
                                Toast.makeText(
                                    this@signUp,
                                    "Error Registering, Account may already exist",
                                    Toast.LENGTH_LONG
                                ).show()
                                toggleUi(true)
                            }
                        }.addOnFailureListener { e ->
                            Log.w(tag, "Error contacting Firebase", e)

                            val text = "Error Registering"
                            val toast =
                                Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                            toast.show()
                            toggleUi(true)
                        }
                } else {
                    Log.d(tag, "register fail")

                    val text =
                        "Invalid credentials! Please check all the fields can try again."
                    val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                    toast.show()
                    toggleUi(true)
                }
            } else {
                binding.emailInput.error = "Please enter a correct email format"
                valid = false
                toggleUi(true)
            }
        } else {
            binding.emailInput.error = "Please enter email"
            valid = false
            toggleUi(true)
        }
    }


    private fun toggleUi(switch: Boolean) {
        if (switch) {
            Log.d("Toggle", "On")
            binding.progressBar.visibility = View.GONE
            binding.emailInput.isEnabled = true
            binding.nameSignup.isEnabled = true
            binding.passwordSignup.isEnabled = true
            binding.createuserButton.isEnabled = true
            binding.signupConstraint.foreground = null
        } else {
            Log.d("Toggle", "Off")
            binding.progressBar.visibility = View.VISIBLE
            binding.createuserButton.isEnabled = false
            binding.emailInput.isEnabled = false
            binding.nameSignup.isEnabled = false
            binding.passwordSignup.isEnabled = false
            binding.createuserButton.isEnabled = false
            binding.signupConstraint.foreground = getDrawable(R.color.semi_transparent)
        }

    }

}