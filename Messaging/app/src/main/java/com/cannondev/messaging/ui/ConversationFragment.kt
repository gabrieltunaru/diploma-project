package com.cannondev.messaging.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.cannondev.messaging.MessagingService
import com.cannondev.messaging.R
import com.cannondev.messaging.models.ConversationMessage
import com.cannondev.messaging.models.ConversationModel
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.Encryption
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec


class ConversationFragment : Fragment() {
    private lateinit var conversation: ConversationModel
    private lateinit var contact: UserModel
    private val args: ConversationFragmentArgs by navArgs()
    private val TAG = this::class.simpleName
    private lateinit var mService: MessagingService
    private var mBound: Boolean = false
    private lateinit var messagesLayout: LinearLayout
    private lateinit var pbKey: PublicKey
    private var pvKey: PrivateKey? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MessagingService.LocalBinder
            mService = binder.getService()
            mService.messagePublisher.subscribe { handleMessage(it) }
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    fun handleMessage(message: ConversationMessage) {
        try {
            if (message.conversationId == conversation.id) {
                activity?.runOnUiThread {
                    Log.d(TAG, "message got to conv: $message")
                    val text = Encryption.decryptMessage(message.text ?: "", pvKey!!)
                    addMessage(text, false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addMessage(text: String?, isSender: Boolean) {
        val textView = MessageView(requireContext())
        textView.text = text
        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        if (isSender) {
            textView.setBackgroundResource(R.drawable.rounded_corner)
            textView.setPadding(15, 15, 15, 15)
            params.setMargins(200, 0, 50, 0)
            params.gravity = Gravity.END
        } else {
            params.setMargins(50, 0, 200, 0)
        }
        textView.textSize = 24.0F
        textView.layoutParams = params
        messagesLayout.addView(textView)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_conversation, container, false)
        val sendBtn = root.findViewById<AppCompatImageButton>(R.id.sendMessageBtn)
        messagesLayout = root.findViewById(R.id.messagesLayout)
        val msgText = root.findViewById<EditText>(R.id.messageText)
        sendBtn.setOnClickListener {
            sendText(msgText.text.toString())
            msgText.setText("")
        }
        return root
    }

    private fun sendText(text: String?) {
        if (mBound) {
            mService.send(text ?: "", conversation, pbKey)
            addMessage(text, true)
        }
    }

    private fun addContactView() {
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.conversation_contact_container,
                ContactFragment.newInstance(conversation.toJson().toString())
            )
            .commit()
    }

    override fun onStart() {
        super.onStart()
        val ctx = requireContext()
        Intent(ctx, MessagingService::class.java).also { intent ->
            ctx.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        pvKey = Encryption.getPrivateKey()
    }

    override fun onStop() {
        super.onStop()
        requireContext().unbindService(connection)
        mBound = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        conversation = args.conversation
        contact = conversation.otherUser
        pbKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(Base64.decode(contact.pbKey, Base64.DEFAULT)))
        addContactView()
        Log.d(this::class.simpleName, "conv: $conversation")
    }

}