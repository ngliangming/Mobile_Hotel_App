package com.nlm.mobilehotelappassignment

import android.os.Bundle
import android.util.Log
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

        auth = FirebaseAuth.getInstance()

        binding.createuserButton.setOnClickListener { createUserBtn() }
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

                db.collection("users")
                    .document(emailInput)
                    .get()
                    .addOnSuccessListener {

                        if (valid) {
                            db.collection("users").document(emailInput)
                                .set(userProf)
                                .addOnSuccessListener {
                                    Log.d(
                                        tag,
                                        "DocumentSnapshot added with ID: $emailInput"
                                    )

                                    val text = "Account Successfully Added"
                                    val toast =
                                        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                                    toast.show()

                                    auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Toast.makeText(
                                                    this@signUp,
                                                    "Registration Success. ",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                finish()

                                            } else {
                                                Toast.makeText(
                                                    this@signUp,
                                                    "Registration failed, please try again! ",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
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
                binding.emailInput.error = "Please enter a correct email format"
                valid = false
            }
        } else {
            binding.emailInput.error = "Please enter email"
            valid = false
        }
    }
}

