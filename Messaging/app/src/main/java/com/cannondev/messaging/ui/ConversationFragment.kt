package com.cannondev.messaging.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.cannondev.messaging.R
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.NaiveSSLContext
import com.neovisionaries.ws.client.WebSocketFactory
import java.net.URI


class ConversationFragment : Fragment() {
    private lateinit var contact: UserModel
    private val args: ConversationFragmentArgs by navArgs()
    private val TAG = this::class.simpleName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_conversation, container, false)
    }

    private fun addContactView() {
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.conversation_contact_container,
                ContactFragment.newInstance(contact.toJsonString().toString())
            )
            .commit()
    }

    private fun wsTest() {
        val uri = URI("ws://10.0.2.2:3000")
        val factory = WebSocketFactory();
        val context = NaiveSSLContext.getInstance("TLS")
        factory.sslContext = context

        factory.verifyHostname = false;
        val ws = factory.createSocket(uri)
        Thread{
            ws.connect()
            ws.sendBinary("mergee".toByteArray())
        }.start()
// Set the custom SSL context.

// Set the custom SSL context.

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contact = args.contact
        addContactView()
        Log.d(this::class.simpleName, "$contact")
        wsTest()
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        ws.close()
    }


}