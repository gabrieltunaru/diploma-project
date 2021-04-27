package com.cannondev.messaging

import android.app.Application
import android.content.Context
import com.cannondev.messaging.http.Queue
import org.conscrypt.Conscrypt
import java.security.Security

class  MyApplication: Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context = MyApplication.applicationContext()
//        Security.insertProviderAt(Conscrypt.newProvider(), 1);
        Queue.init(context)

    }
}