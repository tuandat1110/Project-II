package com.example.projectii

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.projectii.data.UserDAO

class MainActivity : AppCompatActivity() {

    private lateinit var registerText: TextView
    private lateinit var loginButton: Button
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ View
        val userDao = UserDAO(this)
        loginButton = findViewById(R.id.login_btn)
        registerText = findViewById(R.id.RegisterTextView)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)

        // Xử lý sự kiện nhấn vào "Đăng ký"
        registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Xử lý sự kiện nhấn vào nút "Đăng nhập"
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                if(userDao.checkLogin(username,password)){
                    Toast.makeText(this,"Login successful!",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainInterface::class.java))
                }
                else{
                    Toast.makeText(this,"Wrong username or password!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
