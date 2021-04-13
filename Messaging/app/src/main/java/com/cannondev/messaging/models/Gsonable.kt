package com.cannondev.messaging.models

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

abstract class Gsonable {
    fun toJsonString(): JSONObject {
        val jsonData = JSONObject(Gson().toJson(this))
        return jsonData
    }
}