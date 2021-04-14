package com.nlm.mobilehotelappassignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        toggleUi(true)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            login()
        }

        //Change action bar title
        title = "Zenith Hotel - Login";

        //Assign buttons
        binding.loginButton.setOnClickListener {

            toggleUi(false)

            val text = "Invalid Credentials"
            val invalidMsg = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)

            val emailInput = binding.emailInput.text.toString()
            val passwordInput = binding.passwordInput.text.toString()

            val tag = "login"
            var loginStatus = false

            if (TextUtils.isEmpty(emailInput)) {
                binding.passwordInput.error = "Please enter email"
                invalidMsg.show()
                toggleUi(true)
                return@setOnClickListener
            } else if (TextUtils.isEmpty(passwordInput)) {
                binding.emailInput.error = "Please enter password"
                invalidMsg.show()
                toggleUi(true)
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        login()

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Login failed, please try again! ",
                            Toast.LENGTH_LONG
                        ).show()
                        toggleUi(true)
                    }
                }
        }

        binding.signupButton.setOnClickListener { signupButton() }

        //Try to load user & login with shared pref
//        loadUser()
    }

    private fun signupButton() {
        val button = binding.signupButton
        button.setOnClickListener {
            val intent = Intent(this, signUp::class.java)
            startActivity(intent)
        }
    }


    private fun login() {
        toggleUi(false)

        val user = auth.currentUser
        val email:String = user?.email.toString()

        Log.d("DEBUG", user.email)

        var username: String = ""
        var adminLevel: Int = -1

        db.collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { userData ->
                if (userData.exists()) {

                    username = userData.get("username").toString()
                    adminLevel = userData.get("adminLevel").toString().toInt()

                        loginIntent(email, username, adminLevel)

                }
            }
            .addOnFailureListener { exception ->
                Log.w("LOGIN", "Error getting documents: ", exception)

                toggleUi(true)
            }
    }

    fun loginIntent(email: String, username: String, adminLevel: Int) {
        if (adminLevel == 0) {
            Log.d("Login", "client")
            startForResult.launch(
                Intent(this, customerHome::class.java)
                    .putExtra("email", email)
                    .putExtra("name", username)
            )
        } else {
            Log.d("Login", "admin")
//            startForResult.launch(
//                Intent(this, adminHome::class.java)
//                    .putExtra("email", email)
//                    .putExtra("name", username)
//                    .putExtra(data2, adminLevel)
//            )
        }
    }
//
//    private fun saveUser(
//        userId: String,
//        name: String,
//        password: String,
//        email: String,
//        adminLevel: Int
//    ) {
//        val sharedPreferences: SharedPreferences =
//            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = sharedPreferences.edit()
//        editor.apply {
//            putString("userId", userId)
//            putString("name", name)
//            putString("password", password)
//            putString("email", email)
//            putInt("adminLevel", adminLevel)
//        }.apply()
//    }
//
//    private fun loadUser() {
//        toggleUi(false)
//
//        val sharedPreferences: SharedPreferences =
//            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
//        val userId = sharedPreferences.getString("userId", null).toString()
//        val name = sharedPreferences.getString("name", null).toString()
//        val email = sharedPreferences.getString("email", null)
//        val password = sharedPreferences.getString("password", null)
//        val adminLevel = sharedPreferences.getInt("adminLevel", -1)
//
//        val tag = "login"
//        var loginStatus = false
//
//        if (email != null && password != null) {
//            db.collection("users")
//                .whereEqualTo("email", email)
//                .get()
//                .addOnSuccessListener { documents ->
//
//                    for (document in documents) {
//
//                        Log.d(
//                            tag,
//                            "$email => $password | $adminLevel"
//                        )
//
//                        if (document.data.getValue("password") == password) {
//                            loginStatus = true
//
//                            login(userId, name, password, email, adminLevel)
//                        }
//
//                    }
//
//                    if (!loginStatus) {
//                        Log.d(tag, "login fail")
//
//                        val text = "Credentials Changed"
//                        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
//                        toast.show()
//
//                        clearData()
//
//                    }
//
//                    toggleUi(true)
//                }
//                .addOnFailureListener { exception ->
//                    Log.w(tag, "Error getting documents: ", exception)
//
//                    toggleUi(true)
//                }
//        } else {
//            toggleUi(true)
//        }
//    }
//
//    private fun clearData() {
//        val sharedPreferences: SharedPreferences =
//            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = sharedPreferences.edit()
//        editor.apply {
//            putString("name", null)
//            putString("password", null)
//            putString("email", null)
//            putInt("adminLevel", -1)
//        }.apply()
//    }

    private fun toggleUi(switch: Boolean) {
        if (switch) {
            Log.d("Toggle", "On")
            binding.progressBar.visibility = View.GONE
            binding.emailInput.isEnabled = true
            binding.passwordInput.isEnabled = true
            binding.loginButton.isEnabled = true
            binding.signupButton.isEnabled = true
            binding.loginConstraint.foreground = null
        } else {
            Log.d("Toggle", "Off")
            binding.progressBar.visibility = View.VISIBLE
            binding.emailInput.isEnabled = false
            binding.passwordInput.isEnabled = false
            binding.loginButton.isEnabled = false
            binding.signupButton.isEnabled = false
            binding.loginConstraint.foreground = getDrawable(R.color.semi_transparent)
        }

    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode != Activity.RESULT_OK) {
                finish()
            } else {
                binding.emailInput.text.clear()
                binding.passwordInput.text.clear()
//                clearData()
                toggleUi(true)
            }
        }


}
