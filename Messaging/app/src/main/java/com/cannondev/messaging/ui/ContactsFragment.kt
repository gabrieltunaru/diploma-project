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
import com.cannondev.messaging.R
import com.cannondev.messaging.http.ContactsHttp

class ContactsFragment : Fragment() {
    lateinit var contactPseudoId: EditText
    lateinit var searchContactsButton: AppCompatImageButton

    fun getContacts() {
        ContactsHttp.getContacts({ data ->
            Log.d(this::class.simpleName, data.toString())

        }, requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)
        contactPseudoId = root.findViewById(R.id.pseudoId)
        searchContactsButton = root.findViewById(R.id.searchContactsBtn)
        searchContactsButton.setOnClickListener {
            ContactsHttp.addContact(contactPseudoId.text.toString(), requireContext())
        }
        getContacts()
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