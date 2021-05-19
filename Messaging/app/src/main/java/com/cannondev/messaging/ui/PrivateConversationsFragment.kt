package com.cannondev.messaging.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import com.cannondev.messaging.R
import com.cannondev.messaging.http.ContactsHttp
import com.cannondev.messaging.models.ConversationModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class PrivateConversationsFragment : Fragment() {
    lateinit var contactPseudoId: EditText
    lateinit var searchContactsButton: AppCompatImageButton
    lateinit var conversations: List<ConversationModel>

    fun getConversations(savedInstanceState: Bundle?) {
        ContactsHttp.getContacts({ data ->
            val itemType = object : TypeToken<List<ConversationModel>>() {}.type
            conversations = Gson().fromJson(data.getJSONArray("conversations").toString(), itemType)
            if (savedInstanceState == null) {
                for (contact in conversations) {
                    addContact(contact)
                }
            }
        }, requireContext(), true)
    }
    fun addContact(contact: ConversationModel) {
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
        contactPseudoId.hint = "private id"
        searchContactsButton = root.findViewById(R.id.searchContactsBtn)
        searchContactsButton.setOnClickListener {
            ContactsHttp.addPrivateConversation(contactPseudoId.text.toString(), requireContext()) {
                val contact = Gson().fromJson(it.toString(), ConversationModel::class.java)
                addContact(contact)
            }
        }
        getConversations(savedInstanceState)
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