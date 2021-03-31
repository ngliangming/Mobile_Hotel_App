package com.nlm.mobilehotelappassignment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
//        setContentView(R.layout.activity_main)
    }

    fun testFire(view: View) {
        val tag = "Firestore Out"
        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815
        )
        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error adding document", e)
            }
    }

    fun login(view: View) {
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()

        val tag = "login";
        var adminLevel = 0;
        var loginStatus = false;

        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(
                        tag,
                        "${document.data.getValue("email")} => ${document.data.getValue("password")}"
                    )
                    adminLevel = document.data.getValue("adminLevel").toString().toInt()

                    if (document.data.getValue("password") == password) {
                        loginStatus = true;

                        if (adminLevel == 0) {
                            Log.d(tag, "client")
                            startForResult.launch(Intent(this, customerHome::class.java))
                        } else if (adminLevel > 0) {
                            var highLevelAdmin = false;
                            Log.d(tag, "admin")

                            if (adminLevel == 2) {
                                highLevelAdmin = true;
                                Log.d(tag, "high level")
                            }
                        }
                    }

                }

                if (loginStatus == false) {
                    Log.d(tag, "login fail")

                }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting documents: ", exception)

            }
    }

    fun signup(view: View) {

    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode != Activity.RESULT_OK) {
                finish()
            }
        }


}
