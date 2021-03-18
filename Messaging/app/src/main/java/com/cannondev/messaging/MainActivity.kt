package com.cannondev.messaging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cannondev.messaging.http.Requester
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
        Log.d("aici", "emal ${email.text}")
        Requester.post(JSONObject().put("email", email.text))
// Request a string response from the provided URL.


// Add the request to the RequestQueue.
    }
}