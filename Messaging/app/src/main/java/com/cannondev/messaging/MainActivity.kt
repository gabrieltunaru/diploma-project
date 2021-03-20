package com.cannondev.messaging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.cannondev.messaging.http.Requester
import com.cannondev.messaging.models.LoginInfo
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextNumberPassword)

    }

    fun login(view: View) {
        val data = LoginInfo(email.text.toString(),password.text.toString())
        Requester.post(JSONObject(Gson().toJson(data)))
    }
}