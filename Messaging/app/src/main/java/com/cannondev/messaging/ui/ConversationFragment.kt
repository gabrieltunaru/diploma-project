package com.cannondev.messaging.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.cannondev.messaging.R
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.NaiveSSLContext
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import java.net.URI


class ConversationFragment : Fragment() {
    private lateinit var contact: UserModel
    private val args: ConversationFragmentArgs by navArgs()
    private val TAG = this::class.simpleName
    lateinit var ws: WebSocket
    lateinit var msTest: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_conversation, container, false)
        val sendBtn = root.findViewById<AppCompatImageButton>(R.id.sendMessageBtn)
        msTest = root.findViewById(R.id.messageTest)
        sendBtn.setOnClickListener{
            ws.sendText("hopa")
        }
        return root
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
    private fun showMessage(ctx: Context, text: String?) {
        activity?.runOnUiThread{
            Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show()
            msTest.text = text
        }
    }

    private fun wsTest() {
        val ctx = requireContext()
        val uri = URI("ws://10.0.2.2:3000")
        val factory = WebSocketFactory();
        val context = NaiveSSLContext.getInstance("TLS")
        factory.sslContext = context
        factory.verifyHostname = false;
        ws = factory.createSocket(uri)
        ws.addListener(object : WebSocketAdapter() {
            override fun onTextMessage(websocket: WebSocket?, text: String?) {
//                super.onTextMessage(websocket, text)
                Log.d(TAG, text ?: "nope")
                showMessage(ctx, "uite ba: $text")
//                Handler(Looper.getMainLooper()).post{
//                    showMessage(ctx, "ce naiba")
//                }
            }

            override fun onError(websocket: WebSocket?, cause: WebSocketException?) {
                cause?.printStackTrace()
            }
        })
        ws.connectAsynchronously()
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