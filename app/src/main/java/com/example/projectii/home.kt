package com.example.projectii

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.BinderThread
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment home.
         */
        // TODO: Rename and change types and number of parameters
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

//        val btnClick = view.findViewById<Button>(R.id.btn)
//        btnClick.setOnClickListener {
//            Toast.makeText(requireContext(), "Button Clicked!", Toast.LENGTH_SHORT).show()
//        }


        val roomListView = view.findViewById<ListView>(R.id.listView)
        val addButton = view.findViewById<Button>(R.id.add)
        val email1 = arguments?.getString("email") ?: ""  //email lay dc tu khi dang nhap
        var userdao = UserDAO(requireContext())
        val username: String = userdao.getUsernameByEmail(email1).toString()
        var lightCount = 0

        detailRoomLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                lightCount = data?.getIntExtra("lightCount", 0) ?: 0
            }
        }

        addButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_room, null)
            val edtTen = dialogView.findViewById<EditText>(R.id.edtTenPhong)
            val edtSoLuong = dialogView.findViewById<EditText>(R.id.edtSoLuongBong)
            val btnAdd = dialogView.findViewById<Button>(R.id.button_add_room)
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Add room")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create()
            dialog.setOnShowListener {
                btnAdd.setOnClickListener {
                    val ten = edtTen.text.toString().trim()
                    val soLuong = edtSoLuong.text.toString().toIntOrNull()

                    if (ten.isBlank() || soLuong == null || soLuong <= 0) {
                        Toast.makeText(requireContext(), "Please enter the correct information!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Thêm vào DB trong background thread
                        if(userdao.insertRoom(username, RoomItem(ten,soLuong))){
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
            val selectedItem = roomListView.adapter.getItem(position) as? RoomItem
            if (selectedItem != null) {
                val intent = Intent(requireContext(), DetailRoom::class.java)
                intent.putExtra("roomName", selectedItem.name)
                intent.putExtra("lightCount", selectedItem.numberOfLights)
                detailRoomLauncher.launch(intent)  //  sử dụng launcher
            }
        }

        roomItems.clear()
        roomItems.addAll(userdao.getRoomsByUsername(username))

        // Kết nối ListView với Adapter
        adapter = RoomAdapter(requireContext(), roomItems,username,lightCount)
        roomListView.adapter = adapter

    }


}
