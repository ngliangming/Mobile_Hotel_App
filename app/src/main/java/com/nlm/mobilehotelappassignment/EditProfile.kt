package com.nlm.mobilehotelappassignment

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivityEditProfileBinding

class EditProfile : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var name: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Zenith Hotel - Forgot Password"

        name = intent.getStringExtra("name").toString()
        email = intent.getStringExtra("email").toString()

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        email = user?.email.toString()

        Log.d("name", name)
        binding.username.setText(name)

        binding.updateUsernameBtn.setOnClickListener { updateUsername() }
        binding.updatePasswordBtn.setOnClickListener { updatePassword() }
        binding.deleteBtn.setOnClickListener { deleteAlert() }

//        Enable action bar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggleUi(true)

        val result = true
        val returnIntent = Intent()
            .putExtra("result", result)
            .putExtra("name", name)
        setResult(RESULT_CANCELED, returnIntent)
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

    fun updateUsername() {
        toggleUi(false)

        val usernameField = binding.username
        val name = usernameField.text.toString()

        if (!usernameField.text.toString().isEmpty()) {
            db.collection("users")
                .document(email)
                .update("username", name)

            Toast.makeText(
                applicationContext,
                "Username changed",
                Toast.LENGTH_SHORT
            ).show()

            val result = true
            val returnIntent = Intent()
                .putExtra("result", result)
                .putExtra("name", name)
            setResult(RESULT_CANCELED, returnIntent)

            toggleUi(true)
        } else {
            usernameField.error = "Please enter a name"
            toggleUi(true)
        }
    }

    private fun updatePassword() {
        toggleUi(false)

        val oldPasswordField = binding.oldPasswordInput
        val passwordField = binding.passwordInput
        val passwordRepeatField = binding.passwordRepeatInput

        var valid = true

        if (oldPasswordField.text.toString().length < 6) {
            oldPasswordField.error = "Please enter password"
            valid = false
        } else {
            oldPasswordField.error = null
        }

        if (passwordField.text.toString().length < 6) {
            passwordField.error = "Password too short!"
            valid = false
        } else {
            passwordField.error = null
        }

        if (passwordRepeatField.text.toString() != passwordField.text.toString()) {
            passwordRepeatField.error = "Password does not match!"
            valid = false
        } else {
            passwordRepeatField.error = null
        }

        if (valid) {
            val user = auth.currentUser
            val oldPassword = oldPasswordField.text.toString()
            val newPassword = passwordField.text.toString()

            val credential = EmailAuthProvider.getCredential(
                email,
                oldPassword
            )

            user?.reauthenticate(credential)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Auth", "Authenticated successfully")

                    user.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("Auth", "User password updated.")
                                Toast.makeText(
                                    applicationContext,
                                    "Password changed",
                                    Toast.LENGTH_SHORT
                                ).show()

                                toggleUi(true)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                baseContext,
                                "An error has occurred, please try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                            toggleUi(true)
                        }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Current password is incorrect",
                        Toast.LENGTH_SHORT
                    ).show()

                    toggleUi(true)
                    oldPasswordField.requestFocus()
                }


            }
        } else {
            toggleUi(true)
        }
    }

    private fun deleteAlert() {
        toggleUi(false)
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.delete_dialog, null)
        val password = dialogLayout.findViewById<EditText>(R.id.passwordInput)
        with(builder) {
            builder.setTitle("Account Deletion")
            builder.setMessage(
                "You are about to delete your Zenith Hotel account, this action can not be undone." +
                        "\n\nAre you sure you want to delete your account?"
            )
            setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                deleteAccount(
                    password.text.toString()
                )
            }
            setNegativeButton("No") { dialogInterface: DialogInterface, i: Int -> toggleUi(true) }
            setView(dialogLayout)
        }

        builder.show()
    }

    private fun deleteAccount(password: String) {
        toggleUi(false)

        if (password != "") {
            val user = auth.currentUser!!
            val credential = EmailAuthProvider.getCredential(
                email,
                password
            )

            user.reauthenticate(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Auth", "Authenticated successfully")
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("Auth Delete", "User account deleted.")
                                db.collection("users")
                                    .document(email)
                                    .delete()

                                db.collection("booking")
                                    .whereEqualTo("userEmail", email)
                                    .get()
                                    .addOnSuccessListener { bookings ->
                                        for (booking in bookings) {
                                            db.collection("booking")
                                                .document(booking.id)
                                                .delete()
                                        }
                                    }
                            }

                            auth.signOut()

                            val result = true
                            val returnIntent = Intent()
                            returnIntent.putExtra("result", result)
                            setResult(RESULT_OK, returnIntent)
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(
                                applicationContext,
                                "An error has occurred! Please try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                            toggleUi(true)
                        }

                } else {
                    Toast.makeText(
                        baseContext,
                        "Password is incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                    toggleUi(true)
                }


            }.addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "An error has occurred, please try again later",
                    Toast.LENGTH_SHORT
                ).show()
                toggleUi(true)
            }
        }else{
            Toast.makeText(
                baseContext,
                "No password entered, please try again",
                Toast.LENGTH_SHORT
            ).show()
            toggleUi(true)
        }
    }

    private fun toggleUi(switch: Boolean) {
        if (switch) {
            Log.d("Toggle", "On")
            binding.progressBar.visibility = View.GONE
            binding.username.isEnabled = true
            binding.passwordInput.isEnabled = true
            binding.passwordRepeatInput.isEnabled = true
            binding.updateUsernameBtn.isEnabled = true
            binding.updatePasswordBtn.isEnabled = true
            binding.deleteBtn.isEnabled = true
            binding.editConstraint.foreground = null
        } else {
            Log.d("Toggle", "Off")
            binding.progressBar.visibility = View.VISIBLE
            binding.username.isEnabled = false
            binding.passwordInput.isEnabled = false
            binding.passwordRepeatInput.isEnabled = false
            binding.updateUsernameBtn.isEnabled = false
            binding.updatePasswordBtn.isEnabled = false
            binding.deleteBtn.isEnabled = false
            binding.editConstraint.foreground = getDrawable(R.color.semi_transparent)
        }

    }
}