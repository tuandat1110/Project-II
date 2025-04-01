package com.example.projectii

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.zip.Inflater

class RoomAdapter(private val context: Context, private val rooms: List<RoomItem>) : BaseAdapter() {

    override fun getCount(): Int = rooms.size

    override fun getItem(position: Int): RoomItem = rooms[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val item: View

        if (convertView == null) {
            item = LayoutInflater.from(parent?.context).inflate(R.layout.room, parent, false)
            viewHolder = ViewHolder().apply {
                icon = item.findViewById(R.id.icon)
                name = item.findViewById(R.id.nameRoom)
                numberOfLights = item.findViewById(R.id.number_of_lights)
            }
            item.tag = viewHolder
        } else {
            item = convertView
            viewHolder = item.tag as ViewHolder
        }

        // Cập nhật dữ liệu cho viewHolder
        viewHolder.name.text = rooms[position].name
        viewHolder.numberOfLights.text = "Number of lights: ${rooms[position].numberOfLights}"

        return item
    }

    class ViewHolder {
        lateinit var icon: ImageView
        lateinit var name: TextView
        lateinit var numberOfLights: TextView
    }
}
