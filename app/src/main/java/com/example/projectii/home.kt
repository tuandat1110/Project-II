package com.example.projectii

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projectii.data.UserDAO

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [home.newInstance] factory method to
 * create an instance of this fragment.
 */
class home : Fragment() {

    private lateinit var adapter: RoomAdapter
    private val roomItems = mutableListOf<RoomItem>()
    private lateinit var detailRoomLauncher: ActivityResultLauncher<Intent>
    private var selectedItem: RoomItem? = null  // Khai báo selectedItem toàn cục

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roomListView = view.findViewById<ListView>(R.id.listView)
        val addButton = view.findViewById<Button>(R.id.add)
        val username = arguments?.getString("username") ?: ""
        val email1 = arguments?.getString("email") ?: ""  // email lấy từ khi đăng nhập
        var userdao = UserDAO(requireContext())
        //val username: String = userdao.getUsernameByEmail(email1).toString()

        detailRoomLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val returnedLightCount = data?.getIntExtra("lightCount", 0) ?: 0

                // Cập nhật lại danh sách room từ database
                val updatedRooms = userdao.getRoomsByUsername(username)
                roomItems.clear()
                roomItems.addAll(updatedRooms)
                adapter.notifyDataSetChanged()  // Cập nhật giao diện
            }
        }

        addButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_room, null)
            val edtTen = dialogView.findViewById<EditText>(R.id.edtTenPhong)
            val btnAdd = dialogView.findViewById<Button>(R.id.button_add_room)
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Add room")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create()

            dialog.setOnShowListener {
                btnAdd.setOnClickListener {
                    val ten = edtTen.text.toString().trim()

                    if (ten.isBlank()) {
                        Toast.makeText(requireContext(), "Please enter the correct information!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Thêm vào DB trong background thread
                        if(userdao.insertRoom(username, RoomItem(ten))){
                            Toast.makeText(requireContext(),"Add room successfully!",Toast.LENGTH_SHORT).show()
                            val updatedList = userdao.getRoomsByUsername(username)
                            roomItems.clear()
                            roomItems.addAll(updatedList)
                            adapter.notifyDataSetChanged() //  Cập nhật lại giao diện
                            dialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Room name existed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            dialog.show()

            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(Color.BLACK)
        }

        roomListView.setOnItemClickListener { _, _, position, _ ->
            selectedItem = roomListView.adapter.getItem(position) as? RoomItem
            if (selectedItem != null) {
                val intent = Intent(requireContext(), DetailRoom::class.java)
                intent.putExtra("roomName", selectedItem?.name)
                detailRoomLauncher.launch(intent)  // Sử dụng launcher để mở DetailRoom Activity
            } else {
                Log.e("Error", "Selected room item is null")
            }
        }

        roomItems.clear()
        roomItems.addAll(userdao.getRoomsByUsername(username))

        // Kết nối ListView với Adapter
        adapter = RoomAdapter(requireContext(), roomItems, username)
        roomListView.adapter = adapter
    }
}
