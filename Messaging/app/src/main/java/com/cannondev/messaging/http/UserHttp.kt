package com.cannondev.messaging.http

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.cannondev.messaging.Constants
import com.cannondev.messaging.models.ProfileModel
import com.cannondev.messaging.models.UserModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class UserHttp(val authToken: String) {
    private val queue = Queue.getQueue()

    fun getCurrentUser(ctx: Context, listener: Response.Listener<String>): StringRequest {
        val req = object : StringRequest(
            Method.GET,
            "${Constants.BACKEND_URL}/user/getCurrent",
            listener,
            Queue.defaultErrorListener
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["x-auth-token"] = authToken
                return headers
            }
        }
        return req
    }

    fun setProfile(profile: ProfileModel) {
        val req = object : JsonObjectRequest(
            "${Constants.BACKEND_URL}/profile/setProfile",
            profile.toJsonString(),
            Response.Listener {},
            Queue.defaultErrorListener
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["x-auth-token"] = authToken
                return headers
            }
        }
        queue.add(req)
    }
}