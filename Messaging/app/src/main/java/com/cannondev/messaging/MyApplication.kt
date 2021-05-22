package com.cannondev.messaging

import android.app.Application
import android.content.Context
import android.content.Intent
import com.cannondev.messaging.http.Queue

class MyApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    private fun startMessagingService() {
        Intent(applicationContext, MessagingService::class.java)
            .also { intent ->
                startService(intent)
            }
    }

    override fun onCreate() {
        super.onCreate()
        Queue.init(applicationContext)
        startMessagingService()
    }
}