package com.cannondev.messaging

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.http.UserHttp
import com.cannondev.messaging.models.ProfileModel
import com.cannondev.messaging.models.UserModel
import com.google.gson.Gson
import android.util.Log as Log

class ProfileActivity : AppCompatActivity() {
    val rq = Queue.getQueue()
    lateinit var userHttp: UserHttp
    lateinit var authToken: String
    lateinit var username: EditText
    lateinit var details: EditText
    fun getCurrentUser() {
        val req = userHttp.getCurrentUser(applicationContext, Response.Listener { data ->
            val user = Gson().fromJson(data, UserModel::class.java)
            Log.d("aici", user.toString())
            Toast.makeText(applicationContext, user.toString(), Toast.LENGTH_SHORT).show()
        })

        rq.add(req)
    }

    fun setProfile(view: View) {
        val profile = ProfileModel(username = username.text.toString(), details = details.text.toString(), id=null, photo = null)
        userHttp.setProfile(profile)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        username = findViewById(R.id.profileUsername)
        details = findViewById(R.id.profileDetails)


        val sharedPref =
            this.getSharedPreferences(getString(R.string.shared_prefs_file), Context.MODE_PRIVATE)
        authToken = sharedPref.getString("authToken", "").toString()
        userHttp = UserHttp(authToken)
        getCurrentUser()

    }
}