package com.cannondev.messaging.http

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cannondev.messaging.MyApplication
import org.json.JSONObject

object Queue {
    private lateinit var queue: RequestQueue
    private val url = "http://10.0.2.2:3000"


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
            method, url+path, data,
            responseHandler,
            Response.ErrorListener { e ->
                Toast.makeText(MyApplication.applicationContext(), e.toString(), Toast.LENGTH_SHORT).show()
            })
        queue.add(req)
    }

    fun post(path: String, data: JSONObject, listener: Response.Listener<JSONObject>) {
        request(path, data, Request.Method.POST, listener)
    }

    fun get(path: String, data: JSONObject, listener: Response.Listener<JSONObject>) {
        request(path, data, Request.Method.GET, listener)
    }
}
