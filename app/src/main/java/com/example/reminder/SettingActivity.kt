package com.example.reminder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SettingActivity : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val tvUsername : TextView = findViewById(R.id.twCurrentUsername)
        val tvEmail : TextView = findViewById(R.id.twCurrentEmail)
        val edUsername : EditText = findViewById(R.id.edNewUsername)
        val edEmail : EditText = findViewById(R.id.edNewEmail)
        val edNewPassword : EditText = findViewById(R.id.edNewPassword)
        val edConfirmNewPassword : EditText = findViewById(R.id.edConfirmNewPassword)
        val btnConfirm : Button = findViewById(R.id.btnConfirm)
        val btnCancel : Button = findViewById(R.id.btnCancel)

        if(auth != null) {
            tvEmail.text = auth!!.email
            tvUsername.text = auth!!.displayName
        }

        // Confirm changes
        btnConfirm.setOnClickListener {

            if (edUsername.text.trim().toString().isNotEmpty()) {
                val profileUpdates =
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(edUsername.text.trim().toString())
                        .build()
                auth!!.updateProfile(profileUpdates)
                    .addOnCompleteListener {
                    }
            }

            if (edEmail.text.trim().toString().isNotEmpty()) {
                auth!!.updateEmail(edEmail.text.trim().toString())
                .addOnCompleteListener {
                }
            }

            if(edNewPassword.text.trim().toString().isNotEmpty() && edConfirmNewPassword.text.trim().toString().isNotEmpty() ){
                if (edNewPassword.text.trim().toString() == edConfirmNewPassword.text.trim().toString()) {
                    auth!!.updatePassword(edNewPassword.text.trim().toString())
                        .addOnCompleteListener {
                        }
                }
                else{
                    Toast.makeText(this, "Confirm Password does not match with Password! Password did not change", Toast.LENGTH_LONG).show()
                }
            }

            Toast.makeText(this, "Userprofile is saved!", Toast.LENGTH_LONG).show()
        }

        btnCancel.setOnClickListener{
            val intent = Intent( this, MainPage::class.java)
            startActivity(intent)
        }

    }
}