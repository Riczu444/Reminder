package com.example.reminder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var  auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()


        val btnRegister: Button = findViewById(R.id.btnLogin)
        val username: EditText = findViewById(R.id.etUsername)
        val password: EditText = findViewById(R.id.etPassword)
        val email: EditText = findViewById(R.id.etEmail)
        val password2: EditText = findViewById(R.id.etPassword2)
        val tvRegister: TextView = findViewById(R.id.tvRegister)

        btnRegister.setOnClickListener {

            if (username.text.trim().toString().isNotEmpty() && password.text.trim().toString().isNotEmpty()
                && email.text.trim().toString().isNotEmpty() && password2.text.trim().toString().isNotEmpty()) {

                if (password.text.trim().toString() == password2.text.trim().toString()) {
                    createUser(email.text.trim().toString(), password.text.trim().toString())
                }
                else{
                    Toast.makeText(this, "Confirm Password does not match with Password!", Toast.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(this, "Input required", Toast.LENGTH_LONG).show()
            }
        }
        tvRegister.setOnClickListener{
            val intent = Intent( this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUser(email:String, password:String){

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->

                if (task.isSuccessful) {
                    Toast.makeText(this, "\n" +
                            "Registration was successful!", Toast.LENGTH_LONG).show()
                    val intent = Intent( this, MainPage::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this, "\n" +
                            "Authentication failed! Make sure the email is valid" +
                            " or the password is at least 6 characters long"
                            , Toast.LENGTH_LONG).show()
                }
            }
    }
}