package com.cannondev.messaging

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.cannondev.messaging.Constants.BACKEND_URL
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.models.UserModel
import com.google.gson.Gson
import java.util.logging.Logger
import android.util.Log as Log

class ProfileActivity : AppCompatActivity() {
    val rq = Queue.getQueue()
    lateinit var authToken: String
    fun getCurrentUser() {
        val req = object : StringRequest(
            Request.Method.GET,
            "$BACKEND_URL/user/getCurrent",
            Response.Listener { data ->
                val user = Gson().fromJson(data, UserModel::class.java)
                Toast.makeText(applicationContext, user.toString(), Toast.LENGTH_SHORT).show()
            },
            Queue.defaultErrorListener
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                Log.d("aici", "token ${authToken}")
                headers["x-auth-token"] = authToken
                return headers
            }
        }

        rq.add(req)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val sharedPref = this.getSharedPreferences(getString(R.string.shared_prefs_file),Context.MODE_PRIVATE)
        authToken = sharedPref.getString("authToken","").toString()
        getCurrentUser()

    }
}