package com.example.projectii

import android.app.AlertDialog
import android.content.Context
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
                seekBarBrightness = item.findViewById(R.id.seekBarBrightness)
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
        viewHolder.seekBarBrightness.progress = lights[position].brightness
        viewHolder.status.text = if (lights[position].status) "ON" else "OFF"

        viewHolder.switchLight.setOnCheckedChangeListener(null)
        viewHolder.seekBarBrightness.setOnSeekBarChangeListener(null)

        viewHolder.switchLight.setOnCheckedChangeListener { _, isChecked ->
            lights[position].status = isChecked
            viewHolder.status.text = if (isChecked) "ON" else "OFF"
        }

        viewHolder.seekBarBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) lights[position].brightness = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        viewHolder.icon.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Xóa den")
                .setMessage("Bạn có chắc chắn muốn xóa den '${lights[position].name}' không?")
                .setPositiveButton("OK") { dialog, which ->
                    // Xóa phòng khỏi cơ sở dữ liệu và danh sách
                    if (UserDAO(context).deleteLight(tenPhong, lights[position].name)) {
                        // Xóa phòng khỏi danh sách
                        lights.removeAt(position)
                        notifyDataSetChanged()
                        Toast.makeText(context, "Đã xóa den!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        return item
    }

    class ViewHolder {
        lateinit var switchLight: Switch
        lateinit var seekBarBrightness: SeekBar
        lateinit var nameLight: TextView
        lateinit var status: TextView
        lateinit var icon: ImageView
    }
}


