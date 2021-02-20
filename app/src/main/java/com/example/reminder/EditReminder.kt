package com.example.reminder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EditReminder : AppCompatActivity(),  ReminderRowListener {
    private var auth = FirebaseAuth.getInstance().currentUser
    private var time = ""
    private var date = ""
    private var remainderName = ""

    private lateinit var databaseReference: DatabaseReference
    private var reminderList: MutableList<Reminder>? = null
    private lateinit var reminderAdapter: ReminderAdapter
    private var listViewItems: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_reminder)

        val addButton : Button = findViewById(R.id.btnAdd)
        val backButton : Button = findViewById(R.id.btnBack)

        this.listViewItems = findViewById<View>(R.id.listViewReminder) as ListView
        this.databaseReference = FirebaseDatabase.getInstance().reference
        this.reminderList = mutableListOf()
        this.reminderAdapter = ReminderAdapter(this, this.reminderList!!)
        this.listViewItems!!.adapter = this.reminderAdapter
        this.databaseReference.orderByKey().addListenerForSingleValueEvent(itemListener)

        // Add new reminder, set title, date and time and the add new item to listView
        addButton.setOnClickListener {
            this.setReminder()
        }

        // Go back to main activity
        backButton.setOnClickListener {
            val mainPage = Intent( this, MainPage::class.java)
            startActivity(mainPage)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setReminder(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Give a name for reminder")
        val view = layoutInflater.inflate(R.layout.name_reminder, null)
        builder.setView(view)
        val etNameReminder: EditText = view.findViewById(R.id.etNameReminder)

        builder.setPositiveButton("Ok") { _, _ ->
            remainderName = etNameReminder.text.trim().toString()
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                date = SimpleDateFormat("dd.MM.yyyy", Locale.US).format(cal.time).toString()

                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    time = SimpleDateFormat("HH:mm", Locale.US).format(cal.time).toString()

                    // Create Reminder and add attribute information
                    val reminderItem = Reminder.create()
                    reminderItem.message = remainderName
                    reminderItem.reminder_time = date.plus(" ").plus(time)
                    reminderItem.creator_id = auth!!.uid
                    val sdf = SimpleDateFormat("dd.M.yyyy hh:mm")
                    val currentDate = sdf.format(Date())
                    reminderItem.creation_time = currentDate
                    val newItem = this.databaseReference.child(Constants.FIREBASE_ITEM).push()
                    reminderItem.object_id = newItem.key
                    newItem.setValue(reminderItem)
                    this.databaseReference.orderByKey().addListenerForSingleValueEvent(this.itemListener)
                    val reminderData: Data = workDataOf(
                            "title" to getString(R.string.app_name),
                            "description" to remainderName,
                            "reminder_object_id" to reminderItem.object_id)
                    val request = OneTimeWorkRequestBuilder<NotifyWorker>().setInitialDelay(calculateDelay(cal), TimeUnit.MINUTES).setInputData(reminderData).build()
                    WorkManager.getInstance(this).enqueue(request)
                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

    }

    // Modify reminder and update modification to firebase and user view
    override fun modifyReminder(itemObjectId: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Give a name for reminder")
        val view = layoutInflater.inflate(R.layout.name_reminder, null)
        builder.setView(view)
        val etNameReminder: EditText = view.findViewById(R.id.etNameReminder)

        builder.setPositiveButton("Ok") { _, _ ->
            remainderName = etNameReminder.text.trim().toString()
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                date = SimpleDateFormat("dd.MM.yyyy", Locale.US).format(cal.time).toString()

                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    time = SimpleDateFormat("HH:mm", Locale.US).format(cal.time).toString()

                    // Update reminder and add attribute information
                    val itemReference = this.getDatabaseReference(itemObjectId)
                    itemReference.child("message").setValue(remainderName)
                    itemReference.child("reminder_time").setValue(date.plus(" ").plus(time))
                }
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

    }

    //delete reminder from database
    override fun onItemDelete(itemObjectId: String) {
        // get child reference in database using the ObjectID
        val itemReference = this.getDatabaseReference(itemObjectId)
        itemReference.removeValue()
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
                reminder.location_x = map["location_x"] as String?
                reminder.location_y = map["location_y"] as String?

                // Return only the items which belongs to the current user
                if (reminder.creator_id == auth!!.uid) {
                    reminderList!!.add(reminder)
                }

            }
        }
        else {
            this.reminderList!!.clear()
        }

        // Alert adapter that data has changed
        this.reminderAdapter.notifyDataSetChanged()
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