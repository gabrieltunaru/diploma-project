package com.cannondev.messaging.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.cannondev.messaging.R
import com.cannondev.messaging.models.UserModel
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import javax.net.ssl.SSLSocketFactory


class ConversationFragment : Fragment() {
    private lateinit var contact: UserModel
    private val args: ConversationFragmentArgs by navArgs()
    private val TAG = this::class.simpleName
    private lateinit var ws: WebSocketClient


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
        val uri = URI("wss://10.0.2.2:3000")
        ws = object: WebSocketClient(uri){
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "onMessage")
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
            }

            override fun onError(ex: Exception?) {
                ex?.printStackTrace()
                Log.e(TAG, "onError")
            }
        }
//        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
//        ws.setSocketFactory(socketFactory)

        ws.connectBlocking()
        Log.d(TAG, "ws?")
        Log.d(TAG, "${ws.connection}")
//        ws.send("hahaha")
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