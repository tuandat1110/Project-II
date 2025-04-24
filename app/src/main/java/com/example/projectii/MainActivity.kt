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
import com.example.projectii.data.DatabaseHandler
import com.example.projectii.data.UserDAO


class MainActivity : AppCompatActivity() {

    private lateinit var registerText: TextView
    private lateinit var loginButton: Button
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var forgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ View
        val userDao = UserDAO(this)
        loginButton = findViewById(R.id.login_btn)
        registerText = findViewById(R.id.RegisterTextView)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        forgotPassword = findViewById(R.id.forgot_password)

        // Xử lý sự kiện nhấn vào "Đăng ký"
        registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        // Xử lý sự kiện nhấn vào nút "Đăng nhập"
        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                if(userDao.checkLogin(username,password)){
                    val dao = UserDAO(this)
                    val user = dao.getAccountByUsername(username)
                    if (user != null) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainInterface::class.java)
                        intent.putExtra("email", user.email)  // truyền dữ liệu sang MainInterface
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "User not found in database!", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this,"Wrong username or password!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all the information!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
