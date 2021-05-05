package com.cannondev.messaging.http

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cannondev.messaging.Constants
import com.cannondev.messaging.Constants.BACKEND_URL
import com.cannondev.messaging.MyApplication
import com.cannondev.messaging.R
import com.cannondev.messaging.models.Gsonable
import com.cannondev.messaging.models.ProfileModel
import com.cannondev.messaging.utils.Utils
import com.google.gson.JsonObject
import org.json.JSONObject

object Queue {
    private lateinit var queue: RequestQueue
    private val url = BACKEND_URL
    val defaultErrorListener = Response.ErrorListener { e ->
        if (e.networkResponse == null) {
            e.printStackTrace()
            Toast.makeText(
                MyApplication.applicationContext(),
                "Can't connect to server",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val errData = e.networkResponse.data
            val errMessage =
                if (errData != null && errData.isNotEmpty()) String(errData) else "Error"
            Toast.makeText(MyApplication.applicationContext(), errMessage, Toast.LENGTH_SHORT)
                .show()
            Log.e("Queue", errMessage)
        }
    }


    fun init(ctx: Context) {
        queue = Volley.newRequestQueue(ctx)
        Log.d("aici", " initialized")
    }

    private fun request(
        path: String,
        data: JSONObject,
        method: Int,
        responseHandler: Response.Listener<JSONObject>
    ) {
        val req = JsonObjectRequest(
            method, url + path, data,
            responseHandler,
            Response.ErrorListener { e ->
                Toast.makeText(MyApplication.applicationContext(), e.toString(), Toast.LENGTH_SHORT)
                    .show()
            })
        queue.add(req)
    }

    fun post(path: String, data: JSONObject, listener: Response.Listener<JSONObject>) {
        request(path, data, Request.Method.POST, listener)
    }

    fun get(path: String, data: JSONObject, listener: Response.Listener<JSONObject>) {
        request(path, data, Request.Method.GET, listener)
    }

    fun jsonRequest(
        path: String,
        data: Gsonable?,
        ctx: Context,
        responseListener: Response.Listener<JSONObject>?
    ) {
        val authToken = Utils.getSavedAuthToken(ctx)
        val dataString = data?.toJsonString() ?: JSONObject()
        val req = object : JsonObjectRequest(
            "${BACKEND_URL}/${path}",
            dataString,
            responseListener ?: Response.Listener {},
            defaultErrorListener
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["x-auth-token"] = authToken
                return headers
            }
        }
        queue.add(req)
    }

    fun getQueue(): RequestQueue {
        return queue
    }
}
