package com.cannondev.messaging

import android.app.Application
import com.cannondev.messaging.http.Requester

class  MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Requester.init(applicationContext)
    }
}