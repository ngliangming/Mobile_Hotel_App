package com.nlm.mobilehotelappassignment

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivityBookRoomDetailsBinding
import java.sql.Timestamp
import java.util.*

class bookRoomDetails : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: ActivityBookRoomDetailsBinding
    private val timestamp = TimestampConverter()

    private lateinit var userId: String
    private lateinit var roomType: String
    private var roomPrice: Int = -1
    private lateinit var roomImg: String
    private var totalPrice: Int = -1
    private var minRoomNumber: Int = -1
    private var maxRoomNumber: Int = -1

    private var startDate: Int = -1
    private var endDate: Int = -1

    val cal = Calendar.getInstance()
    private var sDay = -1
    private var sMonth = -1
    private var sYear = -1
    private lateinit var sDate: String

    private var eDay = -1
    private var eMonth = -1
    private var eYear = -1
    private lateinit var eDate: String

    //Current Date (as limiter)
    private var cDay = -1
    private var cMonth = -1
    private var cYear = -1
    private lateinit var cDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookRoomDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Change action bar title
        title = "Zenith Hotel - Room Booking";

        //Enable action bar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Get binding
        userId = intent.getStringExtra("userId").toString()
        roomType = intent.getStringExtra("roomType").toString()
        roomPrice = intent.getIntExtra("roomPrice", -1)
        roomImg = intent.getStringExtra("roomImg").toString()
        minRoomNumber = intent.getIntExtra("minRoomNumber", -1)
        maxRoomNumber = intent.getIntExtra("maxRoomNumber", -1)

        //Get current date
        sDay = cal.get(Calendar.DAY_OF_MONTH)
        sMonth = cal.get(Calendar.MONTH) + 1
        sYear = cal.get(Calendar.YEAR)

        eDay = cal.get(Calendar.DAY_OF_MONTH) + 1
        eMonth = cal.get(Calendar.MONTH) + 1
        eYear = cal.get(Calendar.YEAR)

        //Record current date
        cDay = sDay
        cMonth = sMonth
        cYear = sYear

        //Convert to timestamp
        cDate = timestamp.intToTimestamp(sDay, sMonth, sYear)
        sDate = timestamp.intToTimestamp(sDay, sMonth, sYear)
        eDate = timestamp.intToTimestamp(eDay, eMonth, eYear)

        binding.startDate.text = "$sDay/$sMonth/$sYear"
        binding.endDate.text = "$eDay/$eMonth/$eYear"

        //Update UI info
        //=----------------------------
        val context: Context = binding.roomImg.context
        val id: Int = context.resources
            .getIdentifier(roomImg, "drawable", context.packageName)
        binding.roomImg.setImageResource(id)

        binding.roomName.text = roomType
        binding.roomPrice.text = roomPrice.toString()
        binding.totalPrice.text = roomPrice.toString()

        binding.startDate.setOnClickListener { getStartDate(binding.startDate) }
        binding.endDate.setOnClickListener { getEndDate(binding.endDate) }

        binding.bookRoomBtn.setOnClickListener { book() }

        toggleUi(true)
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

    private fun getStartDate(dateView: TextView) {
        val dpd =
            DatePickerDialog(
                this,
                { view, year, month, day ->
                    sYear = year
                    sMonth = month + 1
                    sDay = day
                    dateView.text = "$sDay/$sMonth/$sYear"

                    sDate = timestamp.intToTimestamp(sDay, sMonth, sYear)
                    if (checkDate()) {
                        var totalDays = timestamp.daysBetween(eDate, sDate)
                        totalPrice = totalDays * roomPrice
                        binding.totalPrice.text = totalPrice.toString()
                    } else {
                        totalPrice = 0
                        binding.totalPrice.text = totalPrice.toString()
                    }
                }, sYear, sMonth - 1, sDay
            )

        dpd.show()
    }

    private fun getEndDate(dateView: TextView) {
        val dpd =
            DatePickerDialog(
                this,
                { view, year, month, day ->
                    eYear = year
                    eMonth = month + 1
                    eDay = day
                    dateView.text = "$eDay/$eMonth/$eYear"

                    eDate = timestamp.intToTimestamp(eDay, eMonth, eYear)
                    if (checkDate()) {
                        var totalDays = timestamp.daysBetween(eDate, sDate)
                        totalPrice = totalDays * roomPrice
                        binding.totalPrice.text = totalPrice.toString()
                    } else {
                        totalPrice = 0
                        binding.totalPrice.text = totalPrice.toString()
                    }
                }, eYear, eMonth - 1, eDay
            )

        dpd.show()
    }

    private fun checkDate(): Boolean {
        if (cDate.toInt() > sDate.toInt()) {
            val toast = Toast.makeText(
                applicationContext,
                "Check-in date can not be in the past!",
                Toast.LENGTH_LONG
            )
            toast.show()
            return false
        } else if (sDate.toInt() >= eDate.toInt()) {
            val toast = Toast.makeText(
                applicationContext,
                "Check-out date can not be on or before check-in date!",
                Toast.LENGTH_LONG
            )
            toast.show()
            return false
        } else {
//            val toast = Toast.makeText(applicationContext, "Valid Date", Toast.LENGTH_LONG)
//            toast.show()
            return true
        }
    }

    private fun book() {
        if (checkDate()) {
            toggleUi(false)
            var roomNumber = minRoomNumber
            val bookedRooms = mutableListOf<Room>()
            var booked = true
            var match = false

            db.collection("booking")
                .get()
                .addOnSuccessListener { rooms ->

                    for (room in rooms) {
                        //roomNumber: Int, val type: String, val startDate: Int, val endDate
                        val bookedNumber = room.data.getValue("roomNumber").toString().toInt()
                        val bookedStartDate = room.data.getValue("startDate").toString().toInt()
                        val bookedEndDate = room.data.getValue("endDate").toString().toInt()

                        Log.d(
                            "Current room",
                            "number: ${bookedNumber.toString()}|start: ${bookedStartDate.toString()}|end: ${bookedEndDate.toString()}"
                        )
                        if (bookedStartDate in sDate.toInt()..eDate.toInt() || bookedEndDate in sDate.toInt()..eDate.toInt()) {
                            //=====------=====------=====------=====------=====------=====------=====------=====------=====------=====------=====------=====------
                            Log.d(
                                "Booked room found",
                                "number: ${bookedNumber.toString()}|start: ${bookedStartDate.toString()}|end: ${bookedEndDate.toString()}"
                            )

                            bookedRooms += Room(
                                bookedNumber,
                                bookedStartDate,
                                bookedEndDate
                            )
                        }

                    }

                    roomNumber = minRoomNumber;
                    do {
                        Log.d("Generated new room number", roomNumber.toString())

                        if (bookedRooms.count() > 0) {
                            bookedRooms.forEach {
                                if (it.roomNumber == roomNumber) {
                                    Log.d("Match!", it.roomNumber.toString())
                                    booked = true
                                    match = true
                                    roomNumber++
                                    return@forEach
                                }
                            }

                            if (!match)
                                booked = false
                            else
                                match = false
                        } else {
                            booked = false
                        }
                    } while (booked && roomNumber <= maxRoomNumber)

                    if (booked) {
                        Log.d("Room Status", "No rooms available")
                        val text = "No available rooms, try another room/time"
                        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                        toast.show()
                    } else {

                        val room = hashMapOf(
                            "roomNumber" to roomNumber,
                            "roomType" to roomType,
                            "userId" to userId,
                            "cost" to roomPrice,
                            "startDate" to sDate,
                            "endDate" to eDate,
                            "status" to "pending"
                        )

                        val tag = "Room Status"

                        db.collection("booking")
                            .add(room)
                            .addOnSuccessListener { document ->
                                Log.d(tag, "Room added with ID: ${document.id}")

                                val text = "Room has been booked"
                                val toast =
                                    Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                                toast.show()

                                var result = true;
                                val returnIntent = Intent()
                                returnIntent.putExtra("result", result)
                                setResult(RESULT_OK, returnIntent)

                                toggleUi(false)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.d(tag, "Room failed to be added")

                                val text = "Failed to book room, please try again"
                                val toast =
                                    Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                                toast.show()

                                toggleUi(false)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    val text = "Error Getting Rooms"
                    val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                    toast.show()
                }
        }
    }

    class Room(val roomNumber: Int, val startDate: Int, val endDate: Int)

    private fun toggleUi(switch: Boolean) {
        if (switch) {
            Log.d("Toggle", "On")
            binding.progressBarBooking.visibility = View.GONE
            binding.startDate.setOnClickListener { getStartDate(binding.startDate) }
            binding.endDate.setOnClickListener { getEndDate(binding.endDate) }
            binding.bookConstraint.foreground = null
        } else {
            Log.d("Toggle", "Off")
            binding.progressBarBooking.visibility = View.VISIBLE
            binding.startDate.setOnClickListener { }
            binding.endDate.setOnClickListener { }
            binding.bookConstraint.foreground = getDrawable(R.color.semi_transparent)
        }

    }
}