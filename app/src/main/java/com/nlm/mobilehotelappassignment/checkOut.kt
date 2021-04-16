package com.nlm.mobilehotelappassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivityCheckOutBinding

class checkOut : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: ActivityCheckOutBinding
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Change action bar title
        title = "Zenith Hotel - Check out";

        //Enable action bar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        userEmail = intent.getStringExtra("email").toString()

        initiateView()
    }

    private fun initiateView() {
        //Create List of rooms
        var roomList = CheckOutList()

        //Populate List with rooms based on userEmail

        db.collection("booking").whereEqualTo("status","Checked in")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val bookingId = document.id
                    val userEmail = document.data.getValue("userEmail").toString()
                    val roomNumber = document.data.getValue("roomNumber").toString().toInt()
                    val roomStatus = document.data.getValue("status").toString()
                    val type = document.data.getValue("roomType").toString()
                    val price = document.data.getValue("cost").toString().toInt()
                    val startDate = document.data.getValue("startDate").toString()
                    val endDate = document.data.getValue("endDate").toString()

                    var img: String = ""

                    //TODO Add code to determine image
                    when (type) {
                        "Single" -> img = "single_room"
                        "Double" -> img = "double_room"
                        "Suite" -> img = "suite_room"
                    }

                    roomList.add(
                        ViewRooms.Room(
                            userEmail,
                            bookingId,
                            roomNumber,
                            roomStatus,
                            type,
                            price,
                            startDate,
                            endDate,
                            img
                        )
                    )
                }

                binding.recyclerViewCheckOut.layoutManager = LinearLayoutManager(this)
                binding.recyclerViewCheckOut.adapter = ViewAdapter(roomList, ::viewCheckoutRoom)
            }

            .addOnFailureListener { e ->
                val text = "Error Getting Rooms"
                val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                toast.show()

                Log.d("FIRESTORE", e.toString())
                finish()
            }
    }


    class adminCoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }

    //for adapter
    class ViewAdapter(
        val checkoutList: CheckOutList,
        val passedFunc: (bookingId: String, roomNumber: Int, roomStatus: String, roomType: String, roomPrice: Int, startDate: String, endDate: String, roomImg: String, userEmail: String) -> (Unit)
    ) : RecyclerView.Adapter<adminCoViewHolder>() {

        //number of items (room)
        override fun getItemCount(): Int {
            return checkoutList.rooms.count()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adminCoViewHolder {
            //create view
            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellForRow = layoutInflater.inflate(R.layout.check_out_rows, parent, false)
            return adminCoViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: adminCoViewHolder, position: Int) {
            //Get invididual room
            val room = checkoutList.rooms.get(position)

            //Get UI controller
            val roomTypeView = holder.view.findViewById<TextView>(R.id.roomTypeCo)
            val roomNumber = holder.view.findViewById<TextView>(R.id.roomNumberCo)
            val clientEmailView = holder.view.findViewById<TextView>(R.id.clientEmailCo)
            val checkoutCoBtn = holder.view.findViewById<TextView>(R.id.checkoutCoBtn)

            //Change UI display
            roomTypeView.text = room.type
            roomNumber.text = room.roomNumber.toString()
            clientEmailView.text = room.userEmail

            //Add onClick Listener
            checkoutCoBtn.setOnClickListener {
                //Run accepted function (From constructor)
                passedFunc(
                    room.bookingId,
                    room.roomNumber,
                    room.roomStatus,
                    room.type,
                    room.price,
                    room.startDate,
                    room.endDate,
                    room.roomImg,
                    room.userEmail
                )
            }
        }

    }


    fun viewCheckoutRoom(
        bookingId: String,
        roomNumber: Int,
        roomStatus: String,
        roomType: String,
        roomPrice: Int,
        startDate: String,
        endDate: String,
        roomImg: String,
        userEmail: String
    ){
        val intent = Intent(this, checkoutDetails::class.java)
            .putExtra("bookingId", bookingId)
            .putExtra("roomNumber", roomNumber)
            .putExtra("roomStatus", roomStatus)
            .putExtra("roomType", roomType)
            .putExtra("roomPrice", roomPrice)
            .putExtra("startDate", startDate)
            .putExtra("endDate", endDate)
            .putExtra("roomImg", roomImg)
            .putExtra("userEmail",userEmail)

        startActivity(intent)
    }

    class CheckOutList(var rooms: List<ViewRooms.Room> = listOf<ViewRooms.Room>()) {
        fun add(room: ViewRooms.Room) {
            rooms += room
        }
    }

    class Room(
        val userEmail: String,
        val bookingId: String,
        val roomNumber: Int,
        val roomStatus: String,
        val type: String,
        val price: Int,
        val startDate: String,
        val endDate: String,
        val roomImg: String,
    )

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


//    private fun toggleUi(switch: Boolean) {
//        if (switch) {
//            Log.d("Toggle", "On")
//            binding.progressBarViewing.visibility = View.GONE
//            binding.viewRoomConstraint.foreground = null
//        } else {
//            Log.d("Toggle", "Off")
//            binding.progressBarViewing.visibility = View.VISIBLE
//            binding.viewRoomConstraint.foreground = getDrawable(R.color.semi_transparent)
//        }
//
//    }
}
