package com.cannondev.messaging.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cannondev.messaging.R
import com.cannondev.messaging.models.ProfileModel
import com.cannondev.messaging.models.UserModel
import com.google.gson.Gson

private const val ARG_PARAM1 = "param1"

class ContactFragment : Fragment() {
    private lateinit var contact: UserModel
    private var contactJson: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactJson = it.getString(ARG_PARAM1)
            contact = Gson().fromJson(contactJson, UserModel::class.java)
            Log.d(this::class.java.simpleName, "created fragment: ${contact.toString()}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    companion object {
        fun newInstance(param1: String) =
            ContactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}