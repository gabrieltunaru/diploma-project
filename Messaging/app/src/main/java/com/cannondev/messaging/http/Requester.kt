package com.cannondev.messaging.http

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object Requester {
    private lateinit var queue: RequestQueue
    private lateinit var ctx: Context
    private val url = "http://192.168.100.7:3000"


    fun init(ctx: Context) {
        this.ctx=ctx
        queue = Volley.newRequestQueue(ctx)
        Log.d("aici"," initialized")
    }

    fun post(data: JSONObject) {
        val req = JsonObjectRequest(
            Request.Method.POST, url, data,
            Response.Listener { response ->
                // Display the first 500 characters of the response string.
                Log.d("aici", "Response is: ${response}")
            },
            Response.ErrorListener { e ->

                Log.e("aici", "That didn't work! $e")
                e.printStackTrace()

            })
        queue.add(req)

    }
}
