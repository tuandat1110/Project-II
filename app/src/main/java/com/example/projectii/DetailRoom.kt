package com.example.projectii

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class DetailRoom : AppCompatActivity() {

    lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_room)

        backButton = findViewById(R.id.buttonBack)

        backButton.setOnClickListener {
            finish()
        }
    }
}