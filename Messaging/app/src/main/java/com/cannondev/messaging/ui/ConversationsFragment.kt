package com.cannondev.messaging.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import com.cannondev.messaging.R
import com.cannondev.messaging.http.ContactsHttp
import com.cannondev.messaging.models.ConversationModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

abstract class ConversationsFragment  : Fragment() {
    lateinit var contactPseudoId: EditText
    lateinit var searchContactsButton: AppCompatImageButton
    lateinit var conversations: List<ConversationModel>

    abstract fun getConversations(savedInstanceState: Bundle?)

    fun addContactFragment(contact: ConversationModel) {
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.contacts_scroll_layout,
                ContactFragment.newInstance(contact)
            )
            .commit()
    }

    abstract fun addContactOnServer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getConversations(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_contacts, container, false)
        contactPseudoId = root.findViewById(R.id.pseudoId)
        searchContactsButton = root.findViewById(R.id.searchContactsBtn)
        searchContactsButton.setOnClickListener { addContactOnServer() }
        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("initialized", true)
    }
}