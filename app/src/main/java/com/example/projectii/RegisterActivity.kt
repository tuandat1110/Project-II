package com.example.projectii

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectii.data.User
import com.example.projectii.data.UserDAO
import com.example.projectii.data.UserData

class RegisterActivity : AppCompatActivity() {
    lateinit var TextViewLogin: TextView
    lateinit var regiterButton: Button
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var confirmPasswod: EditText
    lateinit var email: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val userDao = UserDAO(this)

        TextViewLogin = findViewById(R.id.TextViewLogin)
        regiterButton = findViewById(R.id.regiter_btn)
        username = findViewById(R.id.username_input)
        password = findViewById(R.id.password_input)
        confirmPasswod = findViewById(R.id.confirm_password_input)
        email = findViewById(R.id.email)

        TextViewLogin.setOnClickListener{
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        regiterButton.setOnClickListener {
            var usernameString:String = username.text.toString()
            var passwordString:String = password.text.toString()
            var confirmPasswordString:String = confirmPasswod.text.toString()
            var emailString:String = email.text.toString()
            if(usernameString == "" || passwordString == "" || confirmPasswordString == "" || emailString == ""){
                Toast.makeText(this,"Please fill fully information!",Toast.LENGTH_SHORT).show()
            }
            else if(passwordString != confirmPasswordString){
                    Toast.makeText(this,"Confirmation password does not match!", Toast.LENGTH_SHORT).show()
            }
            else{
                if(userDao.findUser(emailString)){
                    if (userDao.insertUser(UserData(usernameString, passwordString,emailString))) {
                        Toast.makeText(this, "Register succesfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    if(!userDao.findAccount(usernameString)){
                        userDao.insertUser(UserData(usernameString, passwordString,emailString))
                        userDao.insertUser1(User("",emailString,"",""))
                        Toast.makeText(this, "Register succesfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    else {
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

    }
}
