package com.example.projectii

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projectii.data.UserDAO
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

fun generateOtp(): String {
    val random = (100000..999999).random()
    return random.toString()
}

fun sendEmailOTP(toEmail: String, otp: String) {
    // Đảm bảo luồng chính không chặn (tạm thời cho phép để test)
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)

    val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
    }

    val session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication("nguyentuandat1110@gmail.com", "wccu hzrt jutu etny") // App password
        }
    })

    try {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress("nguyentuandat1110@gmail.com"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
            subject = "Mã xác thực của bạn"
            setContent(
                """
                <html>
                    <body style="font-family:sans-serif; padding:10px;">
                        <h2 style="color:#2E86C1;">Xin chào,</h2>
                        <p>Bạn vừa yêu cầu khôi phục mật khẩu tài khoản của mình.</p>
                        <p>Mã OTP của bạn là:</p>
                        <h1 style="color:#E74C3C;">$otp</h1>
                        <p>Vui lòng không chia sẻ mã này với bất kỳ ai.</p>
                        <p>Mã có hiệu lực trong vòng <strong>5 phút</strong>.</p>
                        <br>
                        <p style="font-size: 12px; color: gray;">Nếu bạn không yêu cầu điều này, hãy bỏ qua email này.</p>
                        <p style="margin-top:30px;">Trân trọng,<br>NTD</p>
                    </body>
                </html>
                """.trimIndent(),
                "text/html; charset=utf-8"
            )

        }

        // Gửi email trong luồng mới (tránh lỗi NetworkOnMainThread)
        Thread {
            try {
                Transport.send(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

class ForgotPassword : AppCompatActivity() {

    lateinit var btnBack: TextView
    lateinit var btnSendOTP: Button
    lateinit var edtOTP: EditText
    lateinit var edtUsername: EditText
    lateinit var btnVerify: Button

    lateinit var userDao: UserDAO
    var currentOtp: String? = null  // Lưu OTP để verify sau

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        userDao = UserDAO(this)

        btnBack = findViewById(R.id.TextViewBack)
        btnSendOTP = findViewById(R.id.otp_btn)
        edtOTP = findViewById(R.id.otp_input)
        edtUsername = findViewById(R.id.username_input)
        btnVerify = findViewById(R.id.verify_btn)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnSendOTP.setOnClickListener {
            val username = edtUsername.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên người dùng", Toast.LENGTH_SHORT).show()
            } else {
                val email = userDao.getEmailByUsername(username)

                if (email != null) {
                    currentOtp = generateOtp()
                    sendEmailOTP(email, currentOtp!!)
                    Toast.makeText(this, "Đã gửi mã OTP đến email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Không tìm thấy tài khoản với tên người dùng này", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnVerify.setOnClickListener {
            val inputOtp = edtOTP.text.toString()
            if (inputOtp == currentOtp) {
                Toast.makeText(this, "Xác minh thành công. Đổi mật khẩu...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ChangePassword::class.java)
                intent.putExtra("username", edtUsername.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Mã OTP không đúng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
