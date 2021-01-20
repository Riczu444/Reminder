package com.example.reminder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var  auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val email: EditText = findViewById(R.id.etEmail)
        val password: EditText = findViewById(R.id.etPassword)
        val tvRegister: TextView = findViewById(R.id.tvRegister)

        btnLogin.setOnClickListener{

            if(email.text.trim().toString().isNotEmpty() || password.text.trim().toString().isNotEmpty()){
                logInUser(email.text.trim().toString(), password.text.trim().toString())
            }
            else{
                Toast.makeText(this, "Input required", Toast.LENGTH_LONG).show()
            }
        }

        tvRegister.setOnClickListener{
            val intent = Intent( this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
    private fun logInUser(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->

                if (task.isSuccessful) {
                    Toast.makeText(this, "\n" +
                            "Login was successful!", Toast.LENGTH_LONG).show()
                    val intent = Intent( this, MainPage::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this, "\n" +
                            "Authentication failed! Email or Password did not found."
                        , Toast.LENGTH_LONG).show()
                }

            }

    }
}
