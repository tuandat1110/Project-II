package com.example.projectii

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import com.example.projectii.data.UserDAO
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 * Use the [setting.newInstance] factory method to
 * create an instance of this fragment.
 */
class setting : Fragment() {
    lateinit var selectRoom: Spinner
    lateinit var selectLight: Spinner
    lateinit var timePicker: TimePicker
    lateinit var buttonSetTime: Button
    lateinit var email:String  //email lay dc tu khi dang nhap
    lateinit var onOffSpinner: Spinner
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lúc này arguments đã có dữ liệu
        email = arguments?.getString("email") ?: ""
        Log.d("DEBUG", "onCreate - Email: $email")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectRoom = view.findViewById(R.id.selectRoom)
        selectLight = view.findViewById(R.id.selectLight)
        timePicker = view.findViewById(R.id.timePicker)
        buttonSetTime = view.findViewById(R.id.btnSelectDateTime)
        onOffSpinner = view.findViewById(R.id.selectState)

        loadRoomsToSpinner()  // Gọi hàm tách riêng

        val state = listOf<String>("On","Off")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, state)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        onOffSpinner.adapter = adapter


        selectRoom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Bạn chọn: $selectedItem", Toast.LENGTH_SHORT).show()
                loadLightsToSpinner(selectedItem)
                // Hoặc xử lý logic gì đó với selectedItem ở đây
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Trường hợp không chọn gì
            }
        }

        buttonSetTime.setOnClickListener {
            val roomSelected = selectRoom.selectedItem?.toString()
            val lightSelected = selectLight.selectedItem?.toString()
            val hour = timePicker.hour
            val minute = timePicker.minute
            val onOff = onOffSpinner.selectedItem?.toString()
            var dao = UserDAO(requireContext())
            username = dao.getUsernameByEmail(email).toString()

            val lightItem: LightItem? = dao.getLightByNameLight(lightSelected.toString())

            if (roomSelected.isNullOrEmpty() || lightSelected.isNullOrEmpty() || onOff == null) {
                Toast.makeText(requireContext(), "Please select all the information!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            val setTime = calendar.time // thời gian hiện tại
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val activeTime = calendar.time // thời điểm đèn sẽ được bật/tắt

            val state = onOff.equals("On", ignoreCase = true) // true = bật, false = tắt

            val success = dao.addSetTimer(
                username = username,
                nameRoom = lightSelected,
                setTime = setTime,
                activeTime = activeTime,
                state = state
            )

            if (success) {
                sendCommand(lightItem!!.ip,lightItem.pin, if (state) "on" else "off", activeTime.toString(),2)
                Toast.makeText(requireContext(), "Set timer successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to set timer!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun loadRoomsToSpinner() {
        val dao = UserDAO(requireContext())
        val username = dao.getUsernameByEmail(email).toString()
        val rooms = dao.getRoomsByUsername(username)
        val nameRoom = rooms.map { it.name }  // dùng map cho gọn

        Log.d("DEBUG", "Rooms: $rooms")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nameRoom)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectRoom.adapter = adapter
    }

    private fun loadLightsToSpinner(room: String) {
        val dao = UserDAO(requireContext())
        val lights = dao.getLightByNameRoom(room)
        val nameLight = lights.map { it.name }  // dùng map cho gọn

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nameLight)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectLight.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        loadRoomsToSpinner()
    }

    private fun sendCommand(ip: String, pin: String, state: String, time: String?, mode: Int) {
        if (ip.isBlank()) return
        val encodedTime = time?.let { URLEncoder.encode(it, "UTF-8") } ?: ""
        val url = if (mode == 1) {
            URL("http://$ip/control?pin=$pin&state=$state&mode=now")
        } else {
            URL("http://$ip/control?pin=$pin&state=$state&time=$encodedTime&mode=schedule")
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

}