package com.nlm.mobilehotelappassignment

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivityViewRoomsDetailsBinding
import java.util.*

class ViewRoomsDetails : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: ActivityViewRoomsDetailsBinding
    private val timestamp = TimestampConverter()

    private lateinit var userId: String
    private lateinit var bookingId: String
    private var roomNumber: Int = -1
    private lateinit var roomStatus: String
    private lateinit var roomType: String
    private var roomPrice: Int = -1
    private lateinit var roomImg: String

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
        binding = ActivityViewRoomsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Change action bar title
        title = "Zenith Hotel - Room Status";

        //Enable action bar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Get binding
        userId = intent.getStringExtra("userId").toString()
        bookingId = intent.getStringExtra("bookingId").toString()
        roomNumber = intent.getIntExtra("roomNumber", -1)
        roomStatus = intent.getStringExtra("roomStatus").toString()
        roomType = intent.getStringExtra("roomType").toString()
        roomPrice = intent.getIntExtra("roomPrice", -1)
        sDate = intent.getStringExtra("startDate").toString()
        eDate = intent.getStringExtra("endDate").toString()
        roomImg = intent.getStringExtra("roomImg").toString()

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

        binding.roomNumber.text = roomNumber.toString()
        binding.roomName.text = roomType
        binding.roomPrice.text = roomPrice.toString()

        binding.startDate.text = "$sDay/$sMonth/$sYear"
        binding.endDate.text = "$eDay/$eMonth/$eYear"

        binding.roomStatus.text = roomStatus

        val totalDays = timestamp.daysBetween(eDate, sDate)
        binding.totalPrice.text = (totalDays * roomPrice).toString()
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
}