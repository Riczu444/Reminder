package com.example.reminder

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
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

class MainPage : AppCompatActivity(), ReminderRowListener {
    //var list : ArrayList<String> = ArrayList()
    var auth = FirebaseAuth.getInstance().currentUser
    var time = ""
    var date = ""
    var remainder_name = ""

    private lateinit var databaseReference: DatabaseReference
    private var reminderList: MutableList<Reminder>? = null
    private lateinit var reminderAdapter: ReminderAdapter
    private var listViewItems: ListView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val tvUserName : TextView = findViewById(R.id.textView)
        val btnDropDown : ImageButton = findViewById(R.id.btnDropDown)
        //val listView : ListView = findViewById(R.id.listViewReminder)
        val addButton : Button = findViewById(R.id.btnAdd)
        //val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        this.listViewItems = findViewById<View>(R.id.listViewReminder) as ListView

        this.databaseReference = FirebaseDatabase.getInstance().reference
        this.reminderList = mutableListOf()
        this.reminderAdapter = ReminderAdapter(this, this.reminderList!!)
        this.listViewItems!!.adapter = this.reminderAdapter

        this.databaseReference.orderByKey().addListenerForSingleValueEvent(itemListener)



        // Initialise a few examples of reminders to ListView
        //list.add("Test 30.1.2021 8.05")
        //list.add("Test 4.7.2021 22.45")
        //arrayAdapter.notifyDataSetChanged()
        //listView.adapter = arrayAdapter


        // Droplist/PopUp menu for Settings and logout choices
        btnDropDown.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this,btnDropDown)

            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.settings ->
                        // Toast.makeText(this, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                        goToSettings()
                    R.id.log_out ->
                        signOut()
                }
                true
            })
            popupMenu.show()
        }

        // If there exist instance of current user, who he/she name set the username to screen
        if(auth != null) {
            tvUserName.text = auth!!.displayName
        }

        // Add new reminder, set title, date and time and the add new item to listView
        addButton.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Give a name for reminder")
            val view = layoutInflater.inflate(R.layout.name_reminder, null)
            builder.setView(view)
            val etNameReminder: EditText = view.findViewById(R.id.etNameReminder)

            builder.setPositiveButton("Ok", DialogInterface.OnClickListener{dialog, which->
                remainder_name = etNameReminder.text.trim().toString()
                val cal = Calendar.getInstance()
                val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    date = SimpleDateFormat("dd.MM.yyyy", Locale.US).format(cal.time).toString()

                    val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        time = SimpleDateFormat("HH:mm", Locale.US).format(cal.time).toString()

                        val reminderItem = Reminder.create()
                        reminderItem.message = remainder_name
                        reminderItem.reminder_time = date.plus(" ").plus(time)
                        val newItem = this.databaseReference.child(Constants.FIREBASE_ITEM).push()
                        reminderItem.creator_id= newItem.key
                        newItem.setValue(reminderItem)
                        this.databaseReference.orderByKey().addListenerForSingleValueEvent(this.itemListener)


                        //list.add(remainder_name.plus(" ").plus(date).plus(" ").plus(time))
                        //arrayAdapter.notifyDataSetChanged()
                        //listView.adapter = arrayAdapter
                        //setNewReminder()
                    }
                    TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

                }
                DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            })

            builder.setNeutralButton("Cancel", DialogInterface.OnClickListener{ dialog, which ->
                dialog.dismiss()
            })

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()

        }

    }

    //delete an item
    override fun onItemDelete(itemObjectId: String) {
        //get child reference in database via the ObjectID
        val itemReference = this.databaseReference.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        //deletion can be done via removeValue() method
        itemReference.removeValue()
    }

    private fun addReminderToList(dataSnapshot: DataSnapshot){
        //val ref = FirebaseDatabase.getInstance().getReference("reminders")
        //val userId = ref.push().key.toString()
        //val reminder = Reminder(remainder_name, date.plus(" ").plus(time), "test" , 0.0, 0.0)
        //ref.child(userId).setValue(reminder).addOnCompleteListener {  }


        """"
                    val ref = FirebaseDatabase.getInstance().getReference("users")
                    val userId = ref.push().key.toString()
                    val user = User(userId, username, email, password)
                    ref.child(userId).setValue(user).addOnCompleteListener {  }"""
    }


    // Log out the current user
    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
        val logOut = Intent( this, MainActivity::class.java)
        startActivity(logOut)
    }

    private fun goToSettings(){
        val intent = Intent( this, SettingActivity::class.java)
        startActivity(intent)
    }

    var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            addDataToList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }
    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        //Check if current database contains any collection
        if (items.hasNext()) {
            this.reminderList!!.clear()
            val reminderindex = items.next()
            val itemsIterator = reminderindex.children.iterator()

            //check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {
                //get current item
                val currentItem = itemsIterator.next()
                val reminder = Reminder.create()
                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>
                //key will return Firebase ID
                reminder.creator_id = currentItem.key
                reminder.message = map.get("message") as String?
                reminder.reminder_time = map.get("reminder_time") as String?
                //reminder.reminder_seen = map.get("reminder_seen") as String?
                //reminder.creation_time = map.get("creation_time") as String?
                //reminder.location_x = map.get("location_x") as Float?
                //reminder.location_y = map.get("location_y") as Float?
                reminderList!!.add(reminder)
            }
        }
        //alert adapter that has changed
        this.reminderAdapter.notifyDataSetChanged()
    }

    private fun getDatabaseReference(itemObjectId: String): DatabaseReference {
        // add listener for items changed or removed after to do list is created
        this.databaseReference.orderByKey().addListenerForSingleValueEvent(this.itemListener)
        return this.databaseReference.child(Constants.FIREBASE_ITEM).child(itemObjectId)
    }



}

