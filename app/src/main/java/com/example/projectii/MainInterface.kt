package com.example.projectii

import android.content.Intent
import com.example.projectii.R
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.projectii.R.*
import com.example.projectii.databinding.ActivityMainInterfaceBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainInterface : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main_interface)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Xử lý sự kiện chọn item trên bottom navigation
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(home()) //hiển thị fragment tương ứng
                    true
                }
                R.id.profile -> {
                    replaceFragment(profile())  //hiển thị fragment tương ứng
                    true
                }
                R.id.setting -> {
                    replaceFragment(setting())  //hiển thị fragment tương ứng
                    true
                }
                else -> false
            }
        }

        // Mặc định hiển thị fragment Home
        replaceFragment(home())
    }

    //hàm hiển thị fragment
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
    }


}
