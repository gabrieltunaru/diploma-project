package com.cannondev.messaging.models

import android.os.Parcelable
import com.google.gson.Gson
import org.json.JSONObject

abstract class Gsonable: Parcelable {
    fun toJson(): JSONObject {
        return JSONObject(Gson().toJson(this))
    }

    fun toJsonString(): String {
        return this.toJson().toString()
    }
}