package com.cannondev.messaging.http

import android.widget.Toast
import com.android.volley.Response
import com.cannondev.messaging.models.UserModel
import com.google.gson.Gson
import org.json.JSONObject

object UserHttp {
//    suspend fun getCurrentUser(): UserModel{
//        Queue.get("/user/getCurrent", JSONObject(), Response.Listener<UserModel> { data ->
//            val user = Gson().fromJson(data.toString(), UserModel::class.java)
//            return user
//        } )
//    }
}