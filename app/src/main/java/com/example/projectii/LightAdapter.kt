package com.example.projectii

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.example.projectii.data.UserDAO
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class LightAdapter(private val context: Context, private val lights: MutableList<LightItem>, private val tenPhong: String) : BaseAdapter() {

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
                nameLight = item.findViewById(R.id.nameLight)
                status = item.findViewById(R.id.status)
                checkBox = item.findViewById(R.id.checkBox)
            }
            item.tag = viewHolder
        } else {
            item = convertView
            viewHolder = item.tag as ViewHolder
        }

        val light = lights[position]

        viewHolder.nameLight.text = light.name
        viewHolder.switchLight.setOnCheckedChangeListener(null)
        viewHolder.switchLight.isChecked = light.status
        viewHolder.checkBox.isChecked = light.isMarked
        viewHolder.switchLight.jumpDrawablesToCurrentState() // Optional cho hiệu ứng mượt
        viewHolder.status.text = if (light.status) "ON" else "OFF"

        viewHolder.switchLight.setOnCheckedChangeListener { _, isChecked ->
            if (light.status != isChecked) {
                light.status = isChecked
                viewHolder.status.text = if (isChecked) "ON" else "OFF"
                sendCommand(light.ip, light.pin, if (isChecked) "on" else "off",null,1)
                UserDAO(context).updateState(light.name, isChecked)
            }
        }

        viewHolder.checkBox.setOnClickListener {
            val isChecked = viewHolder.checkBox.isChecked
            light.isMarked = isChecked

            UserDAO(context).updateMark(light.name,isChecked)
        }
        return item
    }

    fun toggleAllSwitches() {
        for (light in lights) {
            if(light.isMarked){
                light.status = !light.status // đảo trạng thái
                sendCommand(light.ip, light.pin, if (light.status) "on" else "off", null, 1)
                UserDAO(context).updateState(light.name, light.status)
            }

        }
        notifyDataSetChanged()
    }


    private fun sendCommand(ip: String, pin: String, state: String, time: String?, mode: Int) {
        if (ip.isBlank()) return
        val encodedTime = time?.let { URLEncoder.encode(it, "UTF-8") } ?: ""
        val url = if (mode == 1) {
            URL("http://$ip/control?pin=$pin&state=$state&mode=$mode")
        } else {
            URL("http://$ip/control?pin=$pin&state=$state&time=$encodedTime&mode=$mode")
        }

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
                Log.e("ESP_ERROR", "Failed to send command: ${e.message}")
            }
        }.start()
    }

    class ViewHolder {
        lateinit var switchLight: Switch
        lateinit var nameLight: TextView
        lateinit var status: TextView
        lateinit var icon: ImageView
        lateinit var checkBox: CheckBox
    }
}
