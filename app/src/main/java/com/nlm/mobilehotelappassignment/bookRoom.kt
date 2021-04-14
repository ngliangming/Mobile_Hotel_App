package com.nlm.mobilehotelappassignment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nlm.mobilehotelappassignment.databinding.ActivityBookRoomBinding

class bookRoom : AppCompatActivity() {
    private lateinit var binding: ActivityBookRoomBinding
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Change action bar title
        title = "Zenith Hotel - Room Booking"

        //Enable action bar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        email = intent.getStringExtra("email").toString()

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

    private fun initiateView() {
        //Create List of rooms
        val roomList = RoomList()

        //Populate List with rooms
        roomList.add(Room("Single", 250, "single_room", 100, 199))
        roomList.add(Room("Double", 450, "double_room", 200, 299))
        roomList.add(Room("Suite", 800, "suite_room", 300, 399))

        binding.recyclerViewRoomType.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRoomType.adapter = ViewAdapter(roomList, ::startBooking)
    }

    class ViewAdapter(
        private val roomList: RoomList,
        val passedFunc: (
            roomType: String, roomPrice: Int, roomImg: String, minRoomNum: Int, maxRoomNum: Int
        ) -> (Unit)
    ) : RecyclerView.Adapter<CustomerViewHolder>() {
        //number of items
        override fun getItemCount(): Int {
            return roomList.rooms.count()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
            //Create view
            val layoutInflater = LayoutInflater.from(parent.context)
            val cellForRow = layoutInflater.inflate(R.layout.book_room_row, parent, false)
            return CustomerViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
            //Get invididual room
            val room = roomList.rooms[position]

            //Get UI controller
            val roomTypeView = holder.view.findViewById<TextView>(R.id.roomType)
            val roomPriceView = holder.view.findViewById<TextView>(R.id.roomPrice)
            val bookBtn = holder.view.findViewById<TextView>(R.id.bookRoomBtn)

            //Change UI display
            roomTypeView.text = room.type
            roomPriceView.text = "RM${room.price} / Day"

            //Add onClick Listener
            bookBtn.setOnClickListener {
                //Run accepted function (From constructor)
                passedFunc(room.type, room.price, room.roomImg, room.minRoomNum, room.maxRoomNum)
            }

        }

    }

    class CustomerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }

    private fun startBooking(
        roomType: String,
        roomPrice: Int,
        roomImg: String,
        minRoomNum: Int,
        maxRoomNum: Int
    ) {
        startForResult.launch(
            Intent(this, bookRoomDetails::class.java)
                .putExtra("email", email)
                .putExtra("roomType", roomType)
                .putExtra("roomPrice", roomPrice)
                .putExtra("roomImg", roomImg)
                .putExtra("minRoomNumber", minRoomNum)
                .putExtra("maxRoomNumber", maxRoomNum)
        )
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //Booked
                finish()
            }
        }


    class RoomList(var rooms: List<Room> = listOf()) {
        fun add(room: Room) {
            rooms += room
        }
    }

    class Room(
        val type: String,
        val price: Int,
        val roomImg: String,
        val minRoomNum: Int,
        val maxRoomNum: Int
    )

}