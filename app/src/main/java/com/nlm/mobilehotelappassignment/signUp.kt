package com.nlm.mobilehotelappassignment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseError
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivitySignUpBinding
import java.util.regex.Pattern

class signUp : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createuserButton.setOnClickListener { createuserBtn() }
    }

    //check for email pattern
    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
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

    private fun createuserBtn() {
        val tag = "Firestore Out"

        var valid = true
        val emailInput = binding.emailSignup.text.toString()
        val nameInput = binding.nameSignup.text.toString()
        val passwordInput = binding.passwordSignup.text.toString()
        val level = 0

        val user = hashMapOf(
            "adminLevel" to level,
            "email" to emailInput,
            "name" to nameInput,
            "password" to passwordInput
        )

        if (nameInput.isNullOrEmpty()) { //if name is  null
            binding.nameSignup.setError("Please enter a name")
            valid = false
        }

        if (passwordInput.length < 6) { //if password too short
            binding.passwordSignup.setError("Password too short!")
            valid = false
        }

        if (!emailInput.isNullOrEmpty()) { // if not empty
            if (checkEmail(emailInput)) { // is correct email format ?

                db.collection("users")
                    .whereEqualTo("email", emailInput)
                    .get()
                    .addOnSuccessListener { users ->
                        for (user in users) {
                            val checkExistEmail = user.data.getValue("email").toString()
                            binding.emailSignup.setError("Email existed! Please enter another email.")
                            valid = false
                        }

                        if (valid) {
                            db.collection("users")
                                .add(user)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(
                                        tag,
                                        "DocumentSnapshot added with ID: ${documentReference.id}"
                                    )

                                    val text = "Account Successfully Added"
                                    val toast =
                                        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                                    toast.show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(tag, "Error adding document", e)

                                    val text = "Error Adding"
                                    val toast =
                                        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                                    toast.show()
                                }
                        } else {
                            Log.d(tag, "register fail")

                            val text =
                                "Invalid credentials! Please check all the fields can try again."
                            val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                            toast.show()
                        }
                    }
            } else {
                binding.emailSignup.setError("Please enter a correct email format")
                valid = false
            }
        }else{
            binding.emailSignup.setError("Please enter email")
            valid = false
        }
    }
}

