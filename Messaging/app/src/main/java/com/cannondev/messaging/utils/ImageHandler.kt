package com.cannondev.messaging.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.cannondev.messaging.Constants.BACKEND_URL
import java.net.URL


object ImageHandler {
    fun getImage(path: String): Bitmap? {
        val url = URL("$BACKEND_URL/$path")
        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        return bmp
    }
}