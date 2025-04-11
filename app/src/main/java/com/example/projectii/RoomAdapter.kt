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

class RoomAdapter(private val context: Context, private val rooms: MutableList<RoomItem>, private val username: String ) : BaseAdapter() {

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
                .setTitle("Xóa phòng")
                .setMessage("Bạn có chắc chắn muốn xóa phòng '${rooms[position].name}' không?")
                .setPositiveButton("OK") { dialog, which ->
                    // Xóa phòng khỏi cơ sở dữ liệu và danh sách
                    if (UserDAO(context).deleteRoom(username, rooms[position].name)) {
                        // Xóa phòng khỏi danh sách
                        rooms.removeAt(position)
                        notifyDataSetChanged()
                        Toast.makeText(context, "Đã xóa phòng!", Toast.LENGTH_SHORT).show()
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
        lateinit var icon: ImageView
        lateinit var name: TextView
        lateinit var numberOfLights: TextView
        lateinit var deleteBtn: TextView
    }
}
