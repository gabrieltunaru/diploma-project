package com.cannondev.messaging.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.NavHostFragment
import com.cannondev.messaging.R
import com.cannondev.messaging.models.ConversationModel
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.ImageHandler
import com.google.gson.Gson

private const val ARG_PARAM1 = "param1"

class ContactFragment : Fragment() {
    private lateinit var conversation: ConversationModel
    private lateinit var contact: UserModel
    private var conversationJson: String? = null
    lateinit var username: TextView
    lateinit var details: TextView
    lateinit var photo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            conversationJson = it.getString(ARG_PARAM1)
            conversation = Gson().fromJson(conversationJson, ConversationModel::class.java)
            contact = conversation.otherUser
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
        username.text = contact.profile?.displayName
        details.text = contact.profile?.details
        ImageHandler.loadPhoto(requireContext(), contact.profile?.photo, photo)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contactContainer = view.findViewById<ConstraintLayout>(R.id.contact_container)
        contactContainer.setOnClickListener {
            goToConversation()
        }
    }

    fun goToConversation() {
        val action =
            if (conversation.isPrivate) PrivateConversationsFragmentDirections.actionNavPrivateConversationsToNavConversation(
                conversation
            ) else ContactsFragmentDirections.actionNavContactsToNavConversation(conversation)
        NavHostFragment.findNavController(this).navigate(action)
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