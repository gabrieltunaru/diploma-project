package com.cannondev.messaging.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.os.bundleOf
import com.cannondev.messaging.R
import com.cannondev.messaging.http.ContactsHttp
import com.cannondev.messaging.models.ConnectionModel
import com.cannondev.messaging.models.ProfileModel
import com.cannondev.messaging.models.UserModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.reflect.typeOf

class ContactsFragment : Fragment() {
    lateinit var contactPseudoId: EditText
    lateinit var searchContactsButton: AppCompatImageButton
    lateinit var connections: List<ConnectionModel>

    fun getContacts(savedInstanceState: Bundle?) {
        ContactsHttp.getContacts({ data ->
            Log.d(this::class.simpleName, data.toString())

            val itemType = object : TypeToken<List<ConnectionModel>>() {}.type
            connections = Gson().fromJson(data.getJSONArray("connections").toString(), itemType)
            Log.d(this::class.simpleName, "got from backend: $connections")
            if (savedInstanceState == null) {
                for (contact in connections) {
                    addContact(contact)
                }
            }
        }, requireContext())
    }
    fun addContact(contact: ConnectionModel) {
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.contacts_scroll_layout,
                ContactFragment.newInstance(contact.toJsonString().toString())
            )
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)
        contactPseudoId = root.findViewById(R.id.pseudoId)
        searchContactsButton = root.findViewById(R.id.searchContactsBtn)
        searchContactsButton.setOnClickListener {
            ContactsHttp.addContact(contactPseudoId.text.toString(), requireContext()) {
                val contact = Gson().fromJson(it.toString(), ConnectionModel::class.java)
                addContact(contact)
            }
        }
        getContacts(savedInstanceState)
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}