package com.cannondev.messaging.ui

import android.os.Bundle
import android.util.Log
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

class ContactsFragment : ConversationsFragment() {
    override fun getConversations(savedInstanceState: Bundle?) {
        ContactsHttp.getContacts({ data ->
            val itemType = object : TypeToken<List<ConversationModel>>() {}.type
            conversations = Gson().fromJson(data.getJSONArray("conversations").toString(), itemType)
            if (savedInstanceState == null) {
                for (contact in conversations) {
                    addContactFragment(contact)
                }
            }
        }, requireContext())
    }

    override fun addContactOnServer() {
        ContactsHttp.addContact(contactPseudoId.text.toString(), requireContext()) {
            val contact = Gson().fromJson(it.toString(), ConversationModel::class.java)
            addContactFragment(contact)
        }
    }
}