package com.cannondev.messaging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.models.LoginInfo
import com.google.gson.Gson
import org.json.JSONObject

class AuthActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextNumberPassword)

    }

    fun login(view: View) {
        val data = LoginInfo(email.text.toString(), password.text.toString())
        val jsonData = JSONObject(Gson().toJson(data))
        Queue.post("/user/auth", jsonData, Response.Listener { r ->
            Toast.makeText(this, r.toString(), Toast.LENGTH_SHORT).show()
            Log.d("a venit raspunsul", r.toString())
        })
    }
}