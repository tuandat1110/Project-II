package com.example.projectii

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectii.data.UserDAO

class DetailRoom : AppCompatActivity() {

    lateinit var backButton: Button
    lateinit var addButton: Button
    lateinit var lightItems: MutableList<LightItem>
    lateinit var adapter: LightAdapter
    lateinit var toggleButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_room)
        var userdao = UserDAO(this)

        var lightListView = findViewById<ListView>(R.id.listView)
        backButton = findViewById(R.id.buttonBack)
        addButton = findViewById(R.id.add)
        toggleButton = findViewById(R.id.toggle)

        val name = intent.getStringExtra("roomName")
        val count = intent.getIntExtra("lightCount", 0)
        var lightsCount = 0

        backButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("lightCount", lightsCount)
            setResult(Activity.RESULT_OK, resultIntent)
            //Toast.makeText(this,"${lightsCount}",Toast.LENGTH_LONG).show()
            finish()
        }

        addButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_light, null)

            val edtTen = dialogView.findViewById<EditText>(R.id.edtTenDen)
            val edtPin = dialogView.findViewById<EditText>(R.id.edtPin)
            val edtIP = dialogView.findViewById<EditText>(R.id.edtIP)
            val edtStatus = dialogView.findViewById<EditText>(R.id.edtStatus)
            val btnAdd = dialogView.findViewById<Button>(R.id.button_add_light)

            val dialog = AlertDialog.Builder(this) // nếu đang dùng Fragment
                .setTitle("Add light")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create()

            dialog.setOnShowListener {
                btnAdd.setOnClickListener {
                    val ten = edtTen.text.toString().trim()
                    val pin = edtPin.text.toString().trim()
                    val ip = edtIP.text.toString().trim()
                    val statusText = edtStatus.text.toString().trim()

                    val status = statusText.toIntOrNull()

                    // Kiểm tra chuỗi IP có đúng định dạng x.x.x.x không
                    fun isValidIP(ip: String): Boolean {
                        val parts = ip.split(".")
                        if (parts.size != 4) return false
                        return parts.all {
                            it.toIntOrNull()?.let { num -> num in 0..255 } ?: false
                        }
                    }

                    // Kiểm tra đầu vào
                    if (ten.isEmpty() || pin.isEmpty() || ip.isEmpty() || status == null || status !in 0..1) {
                        Toast.makeText(
                            this,
                            "Please enter all fields correctly!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    if (!pin.all { it.isDigit() }) {
                        Toast.makeText(this, "PIN must be a number!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (!isValidIP(ip)) {
                        Toast.makeText(this, "Invalid IP address!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val light = LightItem(ten, pin, false,ip, status == 1)

                    if (userdao.insertLight(name.toString(), light)) {
                        Toast.makeText(this, "Light added successfully!", Toast.LENGTH_SHORT).show()
                        lightsCount = userdao.countLights(name.toString())
                        lightItems.clear()
                        lightItems.addAll(userdao.getLightByNameRoom(name.toString()))
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this,
                            "Light name already exists in this room!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
        }

        lightListView.setOnItemClickListener { _, _, position, _ ->
            Log.d("TEST_CLICK", "Clicked item at position $position")
            val dialogView = layoutInflater.inflate(R.layout.dialog_update_light, null)
            val edtTen = dialogView.findViewById<TextView>(R.id.edtTenDen)
            val edtPin = dialogView.findViewById<EditText>(R.id.edtPin)
            val edtIP = dialogView.findViewById<EditText>(R.id.edtIP)
            //val checkBox = dialogView.findViewById<CheckBox>(R.id.checkBox)
            val btnUpdate = dialogView.findViewById<Button>(R.id.button_update_light)
            val btnDelete = dialogView.findViewById<Button>(R.id.button_delete_light)

            val selectedLight = lightItems[position]
            val lightName = selectedLight.name
            val pin = selectedLight.pin
            val ip = selectedLight.ip

            edtTen.text = lightName
            edtPin.setText(pin)
            edtIP.setText(ip)

            val dialog = AlertDialog.Builder(this) // nếu đang dùng Fragment
                .setTitle("Update light")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create()

            dialog.setOnShowListener {
                btnUpdate.setOnClickListener {
                    val newPin = edtPin.text.toString().trim()
                    val newIP = edtIP.text.toString().trim()

                    // Kiểm tra PIN là số
                    if (!newPin.all { it.isDigit() }) {
                        Toast.makeText(this, "PIN must be a number!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Kiểm tra định dạng IP
                    fun isValidIP(ip: String): Boolean {
                        val parts = ip.split(".")
                        if (parts.size != 4) return false
                        return parts.all {
                            it.toIntOrNull()?.let { num -> num in 0..255 } ?: false
                        }
                    }

                    if (!isValidIP(newIP)) {
                        Toast.makeText(this, "Invalid IP address!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Chỉ update nếu có sự thay đổi
                    if ((newPin != pin || newIP != ip) && userdao.updateLight(lightName, newPin, newIP)) {
                        Toast.makeText(this, "Update successfully!", Toast.LENGTH_SHORT).show()
                        lightItems.clear()
                        lightItems.addAll(userdao.getLightByNameRoom(name.toString()))
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "No changes or update failed!", Toast.LENGTH_SHORT).show()
                    }
                }


                btnDelete.setOnClickListener {
                    // Xử lý xóa đèn tại đây, ví dụ:
                    if (userdao.deleteLight(name.toString(),lightName)) {
                        Toast.makeText(this, "Light deleted successfully!", Toast.LENGTH_SHORT).show()
                        lightItems.clear()
                        lightItems.addAll(userdao.getLightByNameRoom(name.toString()))
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Failed to delete light", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            dialog.show()
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(Color.BLACK)
        }

        lightItems = mutableListOf()
        lightItems.clear()
        lightItems.addAll(userdao.getLightByNameRoom(name.toString()))

        adapter = LightAdapter(this,lightItems,name.toString())
        lightListView.adapter = adapter
        toggleButton.setOnClickListener {
            adapter.toggleAllSwitches()
        }
    }
}