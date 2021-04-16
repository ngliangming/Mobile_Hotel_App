package com.nlm.mobilehotelappassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivityAddAdminBinding
import java.util.regex.Pattern

class AddAdmin : AppCompatActivity() {
    //for spinner
    lateinit var adminSpinner : Spinner
    lateinit var storeLevel : String

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAddAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Zenith Hotel - Add Admin"

        auth = FirebaseAuth.getInstance()

        adminLevelSpinner()

        toggleUi(true)

        binding.createadminButton.setOnClickListener { createAdmin() }

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

    fun adminLevelSpinner(){
        adminSpinner = findViewById<Spinner>(R.id.adminLevel)

        //initialise the array
        val options = arrayListOf<Int>(1,2)

        //use built in layout and replace with "options" val array
        adminSpinner.adapter = ArrayAdapter<Int>(this, android.R.layout.simple_list_item_1,options)

        //functions of the spinner
        adminSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                storeLevel = "Please select something"

                Log.d("debug", "storelevel is $storeLevel")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                storeLevel = options.get(position).toString()

                Log.d("debug", "storelevel is $storeLevel")
            }
        }
    }

    //email function to check email input from user
    private fun checkEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    private fun createAdmin() {
        toggleUi(false)
        val tag = "Firestore Out"

        var valid = true
        val emailInput = binding.emailInput.text.toString()
        val nameInput = binding.nameSignup.text.toString()
        val passwordInput = binding.passwordSignup.text.toString()
        val level = storeLevel.toInt()

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
                                    this@AddAdmin,
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
            binding.createadminButton.isEnabled = true
            binding.signupConstraint.foreground = null
        } else {
            Log.d("Toggle", "Off")
            binding.progressBar.visibility = View.VISIBLE
            binding.createadminButton.isEnabled = false
            binding.emailInput.isEnabled = false
            binding.nameSignup.isEnabled = false
            binding.passwordSignup.isEnabled = false
            binding.signupConstraint.foreground = getDrawable(R.color.semi_transparent)
        }

    }
}