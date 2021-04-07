package com.nlm.mobilehotelappassignment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nlm.mobilehotelappassignment.databinding.ActivityCustomerHomeBinding


class customerHome : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name").toString()
        val adminLevel = intent.getIntExtra("adminLevel", -1)

        val text = "$name, user level $adminLevel"
        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
        toast.show()
    }

    fun logout(view: View) {
        var result = true;
        val returnIntent = Intent()
        returnIntent.putExtra("result", result)
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}