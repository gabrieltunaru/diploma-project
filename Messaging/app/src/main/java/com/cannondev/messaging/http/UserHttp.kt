package com.cannondev.messaging.http

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.cannondev.messaging.Constants
import com.cannondev.messaging.models.ProfileModel

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
        queue.add(req)
        return req
    }

    fun setProfile(profile: ProfileModel) {
        val req = object : JsonObjectRequest(
            "${Constants.BACKEND_URL}/profile/setProfile",
            profile.toJson(),
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

    fun regeneratePrivateId(listener: Response.Listener<String>) {
        val req = object : StringRequest(
            Method.PUT,
            "${Constants.BACKEND_URL}/user/privateId",
            listener,
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