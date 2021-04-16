package com.nlm.mobilehotelappassignment

import android.R
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivityCheckinDetailsBinding
import com.nlm.mobilehotelappassignment.databinding.ActivityCheckoutDetailsBinding

class checkoutDetails : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var binding: ActivityCheckoutDetailsBinding
    private val timestamp = TimestampConverter()

    private lateinit var email: String
    private lateinit var bookingId: String
    private var roomNumber: Int = -1
    private lateinit var roomStatus: String
    private lateinit var roomType: String
    private var roomPrice: Int = -1
    private lateinit var roomImg: String
    private lateinit var username: String

    private var sDay = -1
    private var sMonth = -1
    private var sYear = -1
    private lateinit var sDate: String

    private var eDay = -1
    private var eMonth = -1
    private var eYear = -1
    private lateinit var eDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Enable action bar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Get binding
        email = intent.getStringExtra("email").toString()
        bookingId = intent.getStringExtra("bookingId").toString()
        roomNumber = intent.getIntExtra("roomNumber", -1)
        roomStatus = intent.getStringExtra("roomStatus").toString()
        roomType = intent.getStringExtra("roomType").toString()
        roomPrice = intent.getIntExtra("roomPrice", -1)
        sDate = intent.getStringExtra("startDate").toString()
        eDate = intent.getStringExtra("endDate").toString()
        roomImg = intent.getStringExtra("roomImg").toString()
        username = intent.getStringExtra("username").toString()

        //Change action bar title
        title = "Room $roomNumber";

        //Convert to Int
        sDay = timestamp.toDay(sDate)
        sMonth = timestamp.toMonth(sDate)
        sYear = timestamp.toYear(sDate)

        eDay = timestamp.toDay(eDate)
        eMonth = timestamp.toMonth(eDate)
        eYear = timestamp.toYear(eDate)

        //Update UI info
        //=----------------------------
        val context: Context = binding.roomImg.context
        val id: Int = context.resources
            .getIdentifier(roomImg, "drawable", context.packageName)
        binding.roomImg.setImageResource(id)

        binding.username.text = username
        binding.roomName.text = roomType
        binding.roomPrice.text = roomPrice.toString()

        binding.startDate.text = "$sDay/$sMonth/$sYear"
        binding.endDate.text = "$eDay/$eMonth/$eYear"

        val totalDays = timestamp.daysBetween(eDate, sDate)
        binding.totalPrice.text = (totalDays * roomPrice).toString()

        binding.adminCheckOutBtn.setOnClickListener {
            db.collection("booking").document(bookingId).update("status","Checked out")
                .addOnSuccessListener { rooms ->
                    Log.d("Success","Successfully checked in")

                    val text = "Room $roomNumber has successfully checked out"
                    val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                    toast.show()

                    finish()
                }
        }

    }

    //    Assign back button function
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}