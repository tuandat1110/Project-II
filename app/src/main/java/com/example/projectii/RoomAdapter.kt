package com.example.projectii

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.projectii.data.UserDAO
import java.util.zip.Inflater

class RoomAdapter(private val context: Context, private val rooms: MutableList<RoomItem>, private val username: String,private val count:Int) : BaseAdapter() {

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
                deleteBtn = item.findViewById(R.id.deleteText)
            }
            item.tag = viewHolder
        } else {
            item = convertView
            viewHolder = item.tag as ViewHolder
        }

        // Cập nhật dữ liệu cho viewHolder
        viewHolder.name.text = rooms[position].name
        viewHolder.numberOfLights.text = "Number of lights: ${rooms[position].numberOfLights}"
        //Xu ly doan xoa phong nay sau
        viewHolder.deleteBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete room")
                .setMessage("Are you sure that you want to delete '${rooms[position].name}'?")
                .setPositiveButton("OK") { dialog, which ->
                    // Xóa phòng khỏi cơ sở dữ liệu và danh sách
                    if (UserDAO(context).deleteRoom(username, rooms[position].name)) {
                        // Xóa phòng khỏi danh sách
                        rooms.removeAt(position)
                        notifyDataSetChanged()
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to delete!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return item
    }

    class ViewHolder {
        lateinit var icon: ImageView
        lateinit var name: TextView
        lateinit var numberOfLights: TextView
        lateinit var deleteBtn: TextView
    }
}
