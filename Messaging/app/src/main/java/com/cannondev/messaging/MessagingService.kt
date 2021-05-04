package com.cannondev.messaging

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.cannondev.messaging.utils.NaiveSSLContext
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import java.net.URI

class MessagingService : Service() {
    private lateinit var ws: WebSocket

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MessagingService = this@MessagingService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun handleMessage(text: String?) {
    }

    fun connect() {
        val uri = URI("ws://10.0.2.2:3000")
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
        ws.connectAsynchronously()
    }

    fun send(text: String?) {
        ws.sendText(text)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        connect()
        return START_STICKY
    }
}