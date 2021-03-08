package com.nlm.mobilehotelappassignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nlm.mobilehotelappassignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.text1.text = "Remote Github test 2"
    }
}
