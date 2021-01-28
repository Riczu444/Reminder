package com.example.reminder

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainPage : AppCompatActivity() {
    var list : ArrayList<String> = ArrayList()
    var auth = FirebaseAuth.getInstance().currentUser
    var time = ""
    var date = ""
    var remainder_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val tvUserName : TextView = findViewById(R.id.textView)
        val btnDropDown : ImageButton = findViewById(R.id.btnDropDown)
        val listView : ListView = findViewById(R.id.listViewReminder)
        val addButton : Button = findViewById(R.id.btnAdd)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        list.add("Test 30.1.2021 8.05")
        list.add("Test 4.7.2021 22.45")
        arrayAdapter.notifyDataSetChanged()
        listView.adapter = arrayAdapter

        btnDropDown.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this,btnDropDown)

            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.settings ->
                        Toast.makeText(this, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    R.id.log_out ->
                        signOut()
                }
                true
            })
            popupMenu.show()
        }





        if(auth != null) {
            tvUserName.text = auth!!.displayName
        }

        addButton.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Give a name for reminder")
            val view = layoutInflater.inflate(R.layout.name_reminder, null)
            builder.setView(view)
            val etNameReminder: EditText = view.findViewById(R.id.etNameReminder)

            builder.setPositiveButton("Ok", DialogInterface.OnClickListener{dialog, which ->
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
                        list.add(remainder_name.plus(" ").plus(date).plus(" ").plus(time))
                        arrayAdapter.notifyDataSetChanged()
                        listView.adapter = arrayAdapter
                    }
                    TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

                }
                DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            })

            builder.setNeutralButton("Cancel", DialogInterface.OnClickListener{dialog, which ->
                dialog.dismiss()
            })

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()

        }
    }



    fun signOut(){
        FirebaseAuth.getInstance().signOut()
        val logOut = Intent( this, MainActivity::class.java)
        startActivity(logOut)
    }
}