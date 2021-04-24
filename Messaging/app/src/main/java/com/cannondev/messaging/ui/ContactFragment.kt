package com.cannondev.messaging.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.cannondev.messaging.R
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.ImageHandler
import com.google.gson.Gson

private const val ARG_PARAM1 = "param1"

class ContactFragment : Fragment() {
    private lateinit var contact: UserModel
    private var contactJson: String? = null
    lateinit var username: TextView
    lateinit var details: TextView
    lateinit var photo: ImageView

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
        val root = inflater.inflate(R.layout.fragment_contact, container, false)
        username = root.findViewById(R.id.contact_username)
        details = root.findViewById(R.id.contact_details)
        photo = root.findViewById(R.id.contact_avatar)
        username.text = contact.profile?.username
        details.text = contact.profile?.details
        ImageHandler.loadPhoto(requireContext(), contact.profile?.photo, photo)

        return root
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