package com.cannondev.messaging.models

import android.os.Parcelable
import com.google.gson.Gson
import org.json.JSONObject

abstract class Gsonable: Parcelable {
    fun toJsonString(): JSONObject {
        val jsonData = JSONObject(Gson().toJson(this))
        return jsonData
    }
}