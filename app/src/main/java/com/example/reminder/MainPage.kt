package com.example.reminder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast

class MainPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val btnDropDown : ImageButton = findViewById(R.id.btnDropDown)

        btnDropDown.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this,btnDropDown)
            val logOut = Intent( this, MainActivity::class.java)
            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.settings ->
                        Toast.makeText(this, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    R.id.log_out ->
                        startActivity(logOut)
                }
                true
            })
            popupMenu.show()
        }
    }
}