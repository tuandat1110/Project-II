package com.example.projectii

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectii.data.UserDAO


class DetailRoom : AppCompatActivity() {

    lateinit var backButton: Button
    lateinit var addButton: Button
    lateinit var lightItems: MutableList<LightItem>
    lateinit var adapter: LightAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_room)
        var userdao = UserDAO(this)

        var lightListView = findViewById<ListView>(R.id.listView)
        backButton = findViewById(R.id.buttonBack)
        addButton = findViewById(R.id.add)

        val name = intent.getStringExtra("roomName")
        val count = intent.getIntExtra("lightCount", 0)


        backButton.setOnClickListener {
            finish()
        }

        addButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_light, null)

            val edtTen = dialogView.findViewById<EditText>(R.id.edtTenDen)
            val edtPin = dialogView.findViewById<EditText>(R.id.edtPin)
            val edtBrightness = dialogView.findViewById<EditText>(R.id.edtDoSang)
            val edtStatus = dialogView.findViewById<EditText>(R.id.edtStatus)
            val btnAdd = dialogView.findViewById<Button>(R.id.button_add_light)

            val dialog = AlertDialog.Builder(this) // nếu đang dùng Fragment
                .setTitle("Thêm đèn")
                .setView(dialogView)
                .setNegativeButton("Hủy", null)
                .create()

            dialog.setOnShowListener {
                btnAdd.setOnClickListener {
                    val ten = edtTen.text.toString().trim()
                    val pin = edtPin.text.toString().trim()
                    val brightnessText = edtBrightness.text.toString().trim()
                    val statusText = edtStatus.text.toString().trim()

                    val brightness = brightnessText.toIntOrNull()
                    val status = statusText.toIntOrNull()

                    if (ten.isBlank() || pin.isBlank() || brightness == null || status == null || brightness !in 0..1 || status !in 0..1) {
                        Toast.makeText(this, "Vui lòng nhập đúng thông tin (brightness/status = 0 hoặc 1)", Toast.LENGTH_SHORT).show()
                    } else {
                        val light = LightItem(ten, pin, brightness, status == 1)

                        // Gọi insertLight truyền vào phòng nào đó (name), ví dụ:
                        if (userdao.insertLight(name.toString(), light)) {
                            Toast.makeText(this, "Thêm đèn thành công!", Toast.LENGTH_SHORT).show()
                            lightItems.clear()
                            lightItems.addAll(userdao.getLightByNameRoom(name.toString()))
                            adapter.notifyDataSetChanged()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(this, "Thêm đèn thất bại!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            dialog.show()
        }


        lightItems = mutableListOf()
        lightItems.clear()
        lightItems.addAll(userdao.getLightByNameRoom(name.toString()))


        adapter = LightAdapter(this,lightItems,name.toString())
        lightListView.adapter = adapter

    }
}