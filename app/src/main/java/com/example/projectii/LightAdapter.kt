package com.example.projectii

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.example.projectii.RoomAdapter.ViewHolder
import com.example.projectii.data.UserDAO
import java.net.HttpURLConnection
import java.net.URL

class LightAdapter(private val context: Context, private val lights: MutableList<LightItem>,private val tenPhong:String) : BaseAdapter() {

    override fun getCount(): Int = lights.size

    override fun getItem(position: Int): LightItem = lights[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val item: View

        if (convertView == null) {
            item = LayoutInflater.from(parent?.context).inflate(R.layout.custom_light_control, parent, false)
            viewHolder = ViewHolder().apply {
                icon = item.findViewById(R.id.icon)
                switchLight = item.findViewById(R.id.switchLight)
//                seekBarBrightness = item.findViewById(R.id.seekBarBrightness)
                nameLight = item.findViewById(R.id.nameLight)
                status = item.findViewById(R.id.status)
            }
            item.tag = viewHolder
        } else {
            item = convertView
            viewHolder = item.tag as ViewHolder
        }

        // Cập nhật dữ liệu cho viewHolder
        viewHolder.nameLight.text = lights[position].name
        viewHolder.switchLight.isChecked = lights[position].status
        viewHolder.status.text = if (lights[position].status) "ON" else "OFF"

        viewHolder.switchLight.setOnCheckedChangeListener(null)
      //  viewHolder.seekBarBrightness.setOnSeekBarChangeListener(null)

        viewHolder.switchLight.setOnCheckedChangeListener { _, isChecked ->
            lights[position].status = isChecked
            viewHolder.status.text = if (isChecked) "ON" else "OFF"
            if (isChecked) {
                // Switch đang ON: gửi lệnh bật đèn
                sendCommand(lights[position].pin,"on")
            } else {
                // Switch đang OFF: gửi lệnh tắt đèn
                sendCommand(lights[position].pin,"off")
            }
        }

//        viewHolder.seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if (fromUser) lights[position].brightness = progress
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
//        })


//        viewHolder.icon.setOnClickListener {
//            AlertDialog.Builder(context)
//                .setTitle("Delete light")
//                .setMessage("Are you sure that you want to delete '${lights[position].name}'?")
//                .setPositiveButton("OK") { dialog, which ->
//                    // Xóa phòng khỏi cơ sở dữ liệu và danh sách
//                    if (UserDAO(context).deleteLight(tenPhong, lights[position].name)) {
//                        // Xóa phòng khỏi danh sách
//                        lights.removeAt(position)
//                        notifyDataSetChanged()
//                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(context, "Failed to delete!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                .setNegativeButton("Cancel", null)
//                .show()
//        }

        return item
    }

    fun sendCommand(pin: String, state: String) {
        val url = URL("http://192.168.1.100/control?pin=$pin&state=$state")

        Thread {
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"
                    inputStream.bufferedReader().use {
                        val response = it.readText()
                        Log.d("ESP_RESPONSE", response)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    class ViewHolder {
        lateinit var switchLight: Switch
        //lateinit var seekBarBrightness: SeekBar
        lateinit var nameLight: TextView
        lateinit var status: TextView
        lateinit var icon: ImageView
    }
}


