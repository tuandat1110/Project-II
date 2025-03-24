package com.example.projectii

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class LightControlView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val switchLight: Switch
    private val seekBarBrightness: SeekBar
    private val nameLight: TextView
    private val status: TextView  // Đã khởi tạo tránh lỗi

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_light_control, this, true)

        switchLight = findViewById(R.id.switchLight)
        seekBarBrightness = findViewById(R.id.seekBarBrightness)
        nameLight = findViewById(R.id.nameLight)
        status = findViewById(R.id.status)  //  Thêm vào tránh lỗi UninitializedPropertyAccessException
    }

    //  Cập nhật dữ liệu từ LightItem
    fun setLightData(lightItem: LightItem) {
        nameLight.text = lightItem.name
        switchLight.isChecked = lightItem.isOn
        seekBarBrightness.progress = lightItem.brightness
        status.text = if (lightItem.isOn) "ON" else "OFF"

        //  Ngăn chặn vòng lặp khi cập nhật UI
        switchLight.setOnCheckedChangeListener(null)
        seekBarBrightness.setOnSeekBarChangeListener(null)

        switchLight.setOnCheckedChangeListener { _, isChecked ->
            lightItem.isOn = isChecked
            status.text = if (isChecked) "ON" else "OFF"
        }

        seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) lightItem.brightness = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
