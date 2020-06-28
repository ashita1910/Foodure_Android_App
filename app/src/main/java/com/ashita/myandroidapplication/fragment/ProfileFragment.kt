package com.ashita.myandroidapplication.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ashita.myandroidapplication.R


class ProfileFragment : Fragment() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtName: TextView
    lateinit var txtEmailAddress: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var txtDeliveryAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        txtName = view.findViewById(R.id.txtName)
        txtEmailAddress = view.findViewById(R.id.txtEmailAddress)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtDeliveryAddress = view.findViewById(R.id.txtDeliveryAddress)

        txtName.text = sharedPreferences.getString("Name", "Default Name")

        txtEmailAddress.text = sharedPreferences.getString("Email Address", "Default Email Address")

        txtMobileNumber.text = "+91-" + sharedPreferences.getString("Mobile Number", "Default Mobile Number")

        txtDeliveryAddress.text = sharedPreferences.getString("Delivery Address", "Default Delivery Address")

        return view
        }
    }
