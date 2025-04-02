package com.example.projectii

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.projectii.databinding.ActivityMainInterfaceBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainInterface : AppCompatActivity() {
    private lateinit var binding: ActivityMainInterfaceBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainInterfaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        bottomNavigationView = binding.bottomNavigationView

        // Thiết lập ViewPager2 với FragmentStateAdapter
        val pagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Đồng bộ ViewPager2 với BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.profile -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.setting -> {
                    viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }

        // Khi ViewPager2 thay đổi trang, cập nhật BottomNavigationView
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        // Mặc định hiển thị tab Home
        viewPager.currentItem = 0
    }
}

// Adapter cho ViewPager2
class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // Số lượng tab

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> home()
            1 -> profile()
            2 -> setting()
            else -> home()
        }
    }
}