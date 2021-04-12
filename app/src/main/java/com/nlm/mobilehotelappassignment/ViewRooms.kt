package com.nlm.mobilehotelappassignment

import android.app.Activity
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nlm.mobilehotelappassignment.databinding.ActivityViewRoomsBinding

class ViewRooms : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: ActivityViewRoomsBinding
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Change action bar title
        title = "Zenith Hotel - Room Status";

        //Enable action bar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userId = intent.getStringExtra("userId").toString()

        initiateView()
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

    fun initiateView() {
        toggleUi(false)

        //Create List of rooms
        var roomList = RoomList()
        //Populate List with rooms based on userId
        db.collection("booking")
            .whereEqualTo("userId", userId)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val bookingId = document.id
                    val roomNumber = document.data.getValue("roomNumber").toString().toInt()
                    val type = document.data.getValue("roomType").toString()
                    val price = document.data.getValue("cost").toString().toInt()
                    var img: String = ""

                    //TODO Add code to determine image
                    when (type) {
                        "Single" -> img = "single_room"
                        "Double" -> img = "double_room"
                        "Suite" -> img = "suite_room"
                    }

                    roomList.add(Room(bookingId, roomNumber, type, price, img))
                }

                binding.recyclerViewRoomType.layoutManager = LinearLayoutManager(this)
                binding.recyclerViewRoomType.adapter = ViewAdapter(roomList, ::viewRoom)
                toggleUi(true)
            }
            .addOnFailureListener { e ->
                val text = "Error Getting Rooms"
                val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG)
                toast.show()

                Log.d("FIRESTORE", e.toString())
                finish()
            }

    }

    class ViewAdapter(
        val roomList: RoomList,
        val passedFunc: (bookingId: String, roomNumber: Int, roomType: String, roomPrice: Int, roomImg: String) -> (Unit)
    ) : RecyclerView.Adapter<CustomerViewHolder>() {
        //number of items
        override fun getItemCount(): Int {
            return roomList.rooms.count()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
            //Create view
            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellForRow = layoutInflater.inflate(R.layout.view_room_rows, parent, false)
            return CustomerViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
            //Get invididual room
            val room = roomList.rooms.get(position)

            //Get UI controller
            val roomTypeView = holder.view.findViewById<TextView>(R.id.roomType)
            val roomPriceView = holder.view.findViewById<TextView>(R.id.roomPrice)
            val roomNumber = holder.view.findViewById<TextView>(R.id.roomNumber)
            val viewBtn = holder.view.findViewById<TextView>(R.id.viewRoomBtn)

            //Change UI display
            roomTypeView.text = room.type
            roomPriceView.text = "RM${room.price} / Day"
            roomNumber.text = room.roomNumber.toString()

            //Add onClick Listener
            viewBtn.setOnClickListener {
                //Run accepted function (From constructor)
                passedFunc(room.bookingId, room.roomNumber, room.type, room.price, room.roomImg)
            }
        }

    }

    class CustomerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }

    public fun viewRoom(
        bookingId: String,
        roomNumber: Int,
        roomType: String,
        roomPrice: Int,
        roomImg: String
    ) {
        //TODO ADD NEW ROOM ACTIVITY
//        val intent = Intent(this, ViewRoomDetails::class.java)
//            .putExtra("userId", userId)
//            .putExtra("roomType", roomType)
//            .putExtra("roomPrice", roomPrice)
//            .putExtra("roomImg", roomImg)
//            .putExtra("minRoomNumber", roomNumber)
//
//        startActivity(intent)
    }

    class RoomList(var rooms: List<Room> = listOf<Room>()) {
        fun add(room: Room) {
            rooms += room
        }
    }

    class Room(
        val bookingId: String,
        val roomNumber: Int,
        val type: String,
        val price: Int,
        val roomImg: String
    )

    private fun toggleUi(switch: Boolean) {
        if (switch) {
            Log.d("Toggle", "On")
            binding.progressBarViewing.visibility = View.GONE
            binding.viewRoomConstraint.foreground = null
        } else {
            Log.d("Toggle", "Off")
            binding.progressBarViewing.visibility = View.VISIBLE
            binding.viewRoomConstraint.foreground = getDrawable(R.color.semi_transparent)
        }

    }
}