package com.nlm.mobilehotelappassignment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Try to load user & login with shared pref
        loadUser()
    }

    fun testFire(view: View) {
        val tag = "Firestore Out"

        val level = 0
        val email = "demo1@gmail.com"
        val name = "demo guy 1"
        val password = "abc123"

        // Create a new user with a first and last name
        val user = hashMapOf(
            "adminLevel" to level,
            "email" to email,
            "name" to name,
            "password" to password
        )

        // Add a new document with a generated ID
        toggleUi(false)
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "DocumentSnapshot added with ID: ${documentReference.id}")

                val text = "Dummy Account Added"
                val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                toast.show()

                toggleUi(true)
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error adding document", e)

                val text = "Error Adding"
                val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                toast.show()

                toggleUi(true)
            }
    }

    fun loginBtn(view: View) {
        val emailInput = binding.emailInput.text.toString()
        val passwordInput = binding.passwordInput.text.toString()

        val tag = "login";
        var loginStatus = false;

        toggleUi(false)
        db.collection("users")
            .whereEqualTo("email", emailInput)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val email = document.data.getValue("email").toString()
                    val password = document.data.getValue("password").toString()
                    val adminLevel = document.data.getValue("adminLevel").toString().toInt()
                    val name = document.data.getValue("name").toString()

                    Log.d(
                        tag,
                        "$email => $password | $adminLevel"
                    )

                    if (document.data.getValue("password") == passwordInput) {
                        loginStatus = true;

                        saveUser(name, password, email, adminLevel)
                        login(name, adminLevel)
                    }

                }

                if (!loginStatus) {
                    Log.d(tag, "login fail")

                    val text = "Invalid Credentials"
                    val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                    toast.show()

                    toggleUi(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting documents: ", exception)

                toggleUi(true)
            }
    }

    private fun login(name: String, adminLevel: Int) {
        val data1 = "name"
        val data2 = "adminLevel"

        if (adminLevel == 0) {
            Log.d("Login", "client")
            startForResult.launch(
                Intent(this, customerHome::class.java)
//                .putExtra(data1, name))
                    .putExtra(data1, name)
                    .putExtra(data2, adminLevel)
            )
        } else {
            Log.d("Login", "admin")
//            startForResult.launch(
//                Intent(this, adminHome::class.java)
//                    .putExtra(data1, name)
//                    .putExtra(data2, adminLevel)
//            )
        }
    }

    private fun saveUser(name: String, password: String, email: String, adminLevel: Int) {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("name", name)
            putString("password", password)
            putString("email", email)
            putInt("adminLevel", adminLevel)
        }.apply()
    }

    private fun loadUser() {
        toggleUi(false)

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val name: String = sharedPreferences.getString("name", null).toString()
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        val adminLevel = sharedPreferences.getInt("adminLevel", -1)

        val tag = "login";
        var loginStatus = false;

        if (email != null && password != null) {
            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->

                    for (document in documents) {

                        Log.d(
                            tag,
                            "$email => $password | $adminLevel"
                        )

                        if (document.data.getValue("password") == password) {
                            loginStatus = true;

                            login(name, adminLevel)
                        }

                    }

                    if (!loginStatus) {
                        Log.d(tag, "login fail")

                        val text = "Credentials Changed"
                        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                        toast.show()

                        clearData()

                    }

                    toggleUi(true)
                }
                .addOnFailureListener { exception ->
                    Log.w(tag, "Error getting documents: ", exception)

                    toggleUi(true)
                }
        }else {
            toggleUi(true)
        }
    }

    fun clearData() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("name", null)
            putString("password", null)
            putString("email", null)
            putInt("adminLevel", -1)
        }.apply()
    }

    private fun toggleUi(switch: Boolean) {
        if (switch) {
            Log.d("Toggle", "On")
            binding.progressBar.visibility = View.GONE
            binding.emailInput.isEnabled = true
            binding.passwordInput.isEnabled = true
            binding.loginButton.isEnabled = true
            binding.signupButton.isEnabled = true
        } else {
            Log.d("Toggle", "Off")
            binding.progressBar.visibility = View.VISIBLE
            binding.emailInput.isEnabled = false
            binding.passwordInput.isEnabled = false
            binding.loginButton.isEnabled = false
            binding.signupButton.isEnabled = false
        }

    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode != Activity.RESULT_OK) {
                finish()
            } else {
                binding.emailInput.text.clear()
                binding.passwordInput.text.clear()
                clearData()
                toggleUi(true)
            }
        }


}
