package com.example.projectii

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectii.data.UserDAO

class ChangePassword : AppCompatActivity() {

    lateinit var btnBack: TextView
    lateinit var edtPassword: EditText
    lateinit var edtConfirmPassword: EditText
    lateinit var btnChange: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val username = intent.getStringExtra("username") ?: ""

        btnBack = findViewById(R.id.TextViewBack)
        btnChange = findViewById(R.id.change_btn)
        edtPassword = findViewById(R.id.password_input)
        edtConfirmPassword = findViewById(R.id.confirm_password_input)

        btnBack.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        btnChange.setOnClickListener {
            var password = edtPassword.text.toString()
            var confirmPass = edtConfirmPassword.text.toString()
            if(password == "" || confirmPass == ""){
                Toast.makeText(this,"Please fill fully information!",Toast.LENGTH_SHORT).show()
            }
            else if(password != confirmPass){
                Toast.makeText(this,"Confirmation password does not match!", Toast.LENGTH_SHORT).show()
            }else{
               val isSuccess = UserDAO(this).updatePassword(username,password)
                if (isSuccess){
                    Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java)) // Quay về login
                    finish()
                }else{
                    Toast.makeText(this, "Password update failed!", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
}


