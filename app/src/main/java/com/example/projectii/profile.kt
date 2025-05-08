package com.example.projectii

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.projectii.data.User
import com.example.projectii.data.UserDAO

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var logout_btn:Button
    private lateinit var save_btn: Button
    private lateinit var textViewName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var fulLName: EditText
    private lateinit var email: TextView
    private lateinit var phone: EditText
    private lateinit var address: EditText

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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var dao = UserDAO(requireContext())
        val email1 = arguments?.getString("email") ?: ""  //email lay dc tu khi dang nhap
        logout_btn = view.findViewById(R.id.logout_btn)
        save_btn = view.findViewById(R.id.save_btn)
        textViewName = view.findViewById(R.id.your_name)
        textViewEmail = view.findViewById(R.id.your_email)
        fulLName = view.findViewById(R.id.full_name)
        email = view.findViewById(R.id.email)
        phone = view.findViewById(R.id.phone)
        address = view.findViewById(R.id.address)
        logout_btn.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        val user = dao.getUserByEmail(email1)
        if(user != null){
            if(user.fullName.isNotEmpty()){
                textViewName.setText(user.fullName)
            }
            fulLName.setText(user.fullName)
            email.setText(email1)
            textViewEmail.setText(email1)
            phone.setText(user.phone)
            address.setText(user.address)
        }

        save_btn.setOnClickListener {
            var nameString:String = fulLName.text.toString()
            var phoneString:String = phone.text.toString()
            var addressString:String = address.text.toString()

            if(dao.updateUser(User(nameString,email1,phoneString,addressString),email1)){
                Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
            }
            textViewName.setText(nameString)
        }
    }
        companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
