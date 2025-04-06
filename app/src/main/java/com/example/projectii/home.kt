package com.example.projectii

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
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

        val lightListView = view.findViewById<ListView>(R.id.listView)
        val addButton = view.findViewById<Button>(R.id.add)


        addButton.setOnClickListener {
            var userdao = UserDAO(requireContext())
            userdao.insertSampleRooms()
            Toast.makeText(requireContext(),"Add room successfully!",Toast.LENGTH_SHORT).show()
        }

        // Danh sách các đèn
        val lightItems = listOf(
            LightItem("Kitchen", false, 80, "OFF"),
            LightItem("Living Room", false, 40, "OFF"),
            LightItem("Bedroom", false, 60, "OFF"),
            LightItem("Garage", false, 70, "OFF"),
            LightItem("Bathroom", false, 50, "OFF"),
            LightItem("Hallway", false, 30, "OFF"),
            LightItem("Garden", false, 90, "OFF"),
            LightItem("Balcony", false, 20, "OFF"),
            LightItem("Office", false, 75, "OFF"),
            LightItem("Guest Room", false, 55, "OFF"),
            LightItem("Dining Room", false, 65, "OFF"),
            LightItem("Laundry Room", false, 35, "OFF"),
            LightItem("Storage Room", false, 45, "OFF"),
        )

        val roomItems = listOf(
            RoomItem("Living Room", 3),
            RoomItem("Bedroom", 2),
            RoomItem("Kitchen", 4),
            RoomItem("Bathroom", 1),
            RoomItem("Garage", 2)
        )

        // Kết nối ListView với Adapter
        val adapter = RoomAdapter(requireContext(), roomItems) //  Sửa lỗi: `this` -> `requireContext()`
        lightListView.adapter = adapter
    }
}