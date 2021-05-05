package com.cannondev.messaging.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.cannondev.messaging.MessagingService
import com.cannondev.messaging.R
import com.cannondev.messaging.models.ConnectionModel
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.NaiveSSLContext
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import java.net.URI


class ConversationFragment : Fragment() {
    private lateinit var conversation: ConnectionModel
    private lateinit var contact: UserModel
    private val args: ConversationFragmentArgs by navArgs()
    private val TAG = this::class.simpleName
    lateinit var msTest: TextView
    private lateinit var mService: MessagingService
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MessagingService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_conversation, container, false)
        val sendBtn = root.findViewById<AppCompatImageButton>(R.id.sendMessageBtn)
        msTest = root.findViewById(R.id.messageTest)
        val msgText = root.findViewById<EditText>(R.id.messageText)
        sendBtn.setOnClickListener{
            sendText(msgText.text.toString())
            msgText.setText("")
        }
        return root
    }

    private fun sendText(text: String?) {
        if (mBound) {
            mService.send(text)
        }
    }

    private fun addContactView() {
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.conversation_contact_container,
                ContactFragment.newInstance(conversation.toJsonString().toString())
            )
            .commit()
    }
    private fun showMessage(ctx: Context, text: String?) {
        activity?.runOnUiThread{
            Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show()
            msTest.text = text
        }
    }

    override fun onStart() {
        super.onStart()
        val ctx = requireContext()
        Intent(ctx, MessagingService::class.java).also { intent ->
            ctx.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        requireContext().unbindService(connection)
        mBound = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        conversation = args.connection
        contact = conversation.otherUser
        addContactView()
        Log.d(this::class.simpleName, "conv: $conversation")
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        ws.close()
    }


}