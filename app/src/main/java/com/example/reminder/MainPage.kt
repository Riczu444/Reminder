package com.example.reminder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.work.*
import java.util.concurrent.TimeUnit


class MainPage : AppCompatActivity(), ReminderRowListener {

    private var auth = FirebaseAuth.getInstance().currentUser
    private var time = ""
    private var date = ""
    private var remainderName = ""

    private lateinit var databaseReference: DatabaseReference
    private var reminderList: MutableList<Reminder>? = null
    private lateinit var dueReminderAdapter: DueReminderAdapter
    private var listViewItems: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val tvUserName : TextView = findViewById(R.id.textView)
        val btnDropDown : ImageButton = findViewById(R.id.btnDropDown)
        val addButton : Button = findViewById(R.id.btnAdd)

        this.listViewItems = findViewById<View>(R.id.listViewReminder) as ListView
        this.databaseReference = FirebaseDatabase.getInstance().reference
        this.reminderList = mutableListOf()
        this.dueReminderAdapter = DueReminderAdapter(this, this.reminderList!!)
        this.listViewItems!!.adapter = this.dueReminderAdapter
        this.databaseReference.orderByKey().addListenerForSingleValueEvent(itemListener)

        // Drop list /PopUp menu for Settings and logout choices
        btnDropDown.setOnClickListener {
            val popupMenu = PopupMenu(this,btnDropDown)
            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.settings ->
                        // Toast.makeText(this, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                        goToSettings()
                    R.id.log_out ->
                        signOut()
                }
                true
            }
            popupMenu.show()
        }

        // If there exist instance of current user, his/her username is set to screen
        if(auth != null) {
            tvUserName.text = auth!!.displayName
        }

        // Add new reminder, set title, date and time and the add new item to listView
        addButton.setOnClickListener {
            val editReminder = Intent( this, EditReminder::class.java)
            startActivity(editReminder)
            //val mapReminder = Intent( this, MapsActivity::class.java)
            //startActivity(mapReminder)
            //this.setReminder()
        }

    }


    // It does not need anymore in the main page side, but the main page utilises
    // the same rowlistener as in edit reminder activity, so the function is the only placeholder
    override fun modifyReminder(itemObjectId: String) {

    }

    //delete reminder from database
    override fun onItemDelete(itemObjectId: String) {
        // get child reference in database using the ObjectID
        val itemReference = this.getDatabaseReference(itemObjectId)
        itemReference.removeValue()
    }


    // Log out the current user
    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        val logOut = Intent( this, MainActivity::class.java)
        startActivity(logOut)
    }

    // Go to Settings Activity
    private fun goToSettings(){
        val intent = Intent( this, SettingActivity::class.java)
        startActivity(intent)
    }

    // Item listener to update reminder list
    private var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            updateReminderList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    // Update reminder list
    private fun updateReminderList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        // Check Check that database has collections
        if (items.hasNext()) {
            this.reminderList!!.clear()
            val reminderIndex = items.next()
            val itemsIterator = reminderIndex.children.iterator()

            // Check collections has any item
            while (itemsIterator.hasNext()) {
                // Get current item
                val currentItem = itemsIterator.next()
                val reminder = Reminder.create()

                // Get current data in a map
                val map = currentItem.value as HashMap<*, *>

                reminder.object_id = currentItem.key
                reminder.message = map["message"] as String?
                reminder.reminder_time = map["reminder_time"] as String?
                reminder.creator_id = map["creator_id"] as String?
                reminder.reminder_seen = map["reminder_seen"] as Boolean?
                reminder.creation_time = map["creation_time"] as String?
                //reminder.location_x = map["location_x"] as Double
                //reminder.location_y = map["location_y"] as Double

                // Return only the items which belongs to the current user and reminder time due
                if (reminder.creator_id == auth!!.uid && reminder.reminder_seen == true) {
                    reminderList!!.add(reminder)
                }

            }
        }
        else {
            this.reminderList!!.clear()
        }

        // Alert adapter that data has changed
        this.dueReminderAdapter.notifyDataSetChanged()
    }

    private fun getDatabaseReference(itemObjectId: String): DatabaseReference {
        // Add listener for items changed or removed after reminder list is created
        this.databaseReference.orderByKey().addListenerForSingleValueEvent(this.itemListener)
        return this.databaseReference.child(Constants.FIREBASE_ITEM).child(itemObjectId)
    }


    // Destroy item from ListView
    override fun onDestroy() {
        this.databaseReference.removeEventListener(this.itemListener)
        super.onDestroy()
    }

    private fun calculateDelay( reminderTime: Calendar): Long {
        val diff = reminderTime.timeInMillis - Calendar.getInstance().timeInMillis
        val diffMinutes = diff / (60 * 1000)
        return diffMinutes
    }





}

