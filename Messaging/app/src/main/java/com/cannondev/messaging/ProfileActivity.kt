package com.cannondev.messaging

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.http.UserHttp
import com.cannondev.messaging.models.ProfileModel
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.ImageHandler
import com.google.gson.Gson


class ProfileActivity : AppCompatActivity() {
    val rq = Queue.getQueue()
    lateinit var userHttp: UserHttp
    lateinit var authToken: String
    lateinit var username: EditText
    lateinit var details: EditText
    lateinit var photo: ImageView
    lateinit var user: UserModel
    fun getCurrentUser() {
        val req = userHttp.getCurrentUser(applicationContext, Response.Listener { data ->
            user = Gson().fromJson(data, UserModel::class.java)
            username.setText(user.profile?.username)
            details.setText(user.profile?.details)
            ImageHandler.loadPhoto(applicationContext, user.profile?.photo, photo)
        })

        rq.add(req)
    }

    fun setProfile(view: View) {
        val profile = ProfileModel(
            username = username.text.toString(),
            details = details.text.toString(),
            id = null,
            photo = user.profile?.photo
        )
        userHttp.setProfile(profile)
    }

    fun setPhoto(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    val PICK_IMAGE = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            data?.data?.let { ImageHandler.uploadImage(it, baseContext, authToken) }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        username = findViewById(R.id.profileUsername)
        details = findViewById(R.id.profileDetails)
        photo = findViewById(R.id.profileAvatar)


        val sharedPref =
            this.getSharedPreferences(getString(R.string.shared_prefs_file), Context.MODE_PRIVATE)
        authToken = sharedPref.getString("authToken", "").toString()
        userHttp = UserHttp(authToken)
        getCurrentUser()
    }

}