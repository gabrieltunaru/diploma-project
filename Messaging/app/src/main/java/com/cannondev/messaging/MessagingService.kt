package com.cannondev.messaging

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.models.ConversationMessage
import com.cannondev.messaging.models.ConversationModel
import com.cannondev.messaging.models.Message
import com.cannondev.messaging.utils.Encryption
import com.cannondev.messaging.utils.MessageDbHelper
import com.cannondev.messaging.utils.NaiveSSLContext
import com.cannondev.messaging.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import io.reactivex.rxjava3.subjects.PublishSubject
import java.lang.Exception
import java.net.URI
import java.security.PublicKey
import java.util.*
import java.util.concurrent.Executors

class MessagingService : Service() {
    private lateinit var ws: WebSocket
    private val WSS_URL = "ws://10.0.2.2:3000"
    val messagePublisher = PublishSubject.create<ConversationMessage>()
    lateinit var dbHelper: MessageDbHelper

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MessagingService = this@MessagingService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun handleMessage(text: String?) {
        val message = Gson().fromJson(text, ConversationMessage::class.java)
        dbHelper.saveMessage(message, false)
        messagePublisher.onNext(message)
        sendAck(message)
        Log.d(this::class.simpleName, "received ws message: $message")
    }

    private fun sendAck(message: ConversationMessage) {
        val ackMessage = ConversationMessage(
            "ack",
            Utils.getSavedAuthToken(applicationContext),
            null,
            null,
            message.conversationId,
            message.timestamp
        )
        ws.sendText(ackMessage.toJsonString())
    }

    fun initializeConnection() {
        val data = Message("init", Utils.getSavedAuthToken(applicationContext), null, null)
        ws.sendText(data.toJson().toString())
    }

    fun getMessages() {
        Queue.jsonRequest("messages", null, applicationContext) {data ->
            val itemType = object : TypeToken<List<ConversationMessage>>() {}.type
            val messages: List<ConversationMessage> = Gson().fromJson(data.getJSONArray("messages").toString(), itemType)
            for (message in messages) {
                dbHelper.saveMessage(message, false)
            }
        }
    }

    fun connect() {
        val uri = URI(WSS_URL)
        val factory = WebSocketFactory();
        val context = NaiveSSLContext.getInstance("TLS")
        factory.sslContext = context
        factory.verifyHostname = false;
        ws = factory.createSocket(uri)
        ws.addListener(object : WebSocketAdapter() {
            override fun onTextMessage(websocket: WebSocket?, text: String?) {
                handleMessage(text)
            }

            override fun onError(websocket: WebSocket?, cause: WebSocketException?) {
                cause?.printStackTrace()
            }
        })
        val es = Executors.newSingleThreadExecutor();
        val future = ws.connect(es);
        try {
            future.get()
            initializeConnection()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun send(text: String, conversation: ConversationModel, pbKey: PublicKey) {
        val encryptedText = Encryption.encryptMessage(text, pbKey)
        val message = ConversationMessage(
            "text",
            Utils.getSavedAuthToken(applicationContext),
            encryptedText,
            conversation.otherUser.id,
            conversation.id,
            System.currentTimeMillis()
        )
        val messageString = message.toJson().toString()
        ws.sendText(messageString)
        message.text = Encryption.encryptMessage(text, Encryption.getPublicKey())
        dbHelper.saveMessage(message, true)

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        connect()
        dbHelper = MessageDbHelper(applicationContext)
        getMessages()
        return START_STICKY
    }

}