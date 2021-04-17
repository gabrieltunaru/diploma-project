package com.cannondev.messaging

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.cannondev.messaging.Constants.BACKEND_URL
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.http.UserHttp
import com.cannondev.messaging.models.ProfileModel
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.ImageHandler
import com.google.gson.Gson
import com.squareup.picasso.Picasso


class ProfileActivity : AppCompatActivity() {
    val rq = Queue.getQueue()
    lateinit var userHttp: UserHttp
    lateinit var authToken: String
    lateinit var username: EditText
    lateinit var details: EditText
    lateinit var photo: ImageView
    fun getCurrentUser() {
        val req = userHttp.getCurrentUser(applicationContext, Response.Listener { data ->
            val user = Gson().fromJson(data, UserModel::class.java)
            username.setText(user.profile.username)
            details.setText(user.profile.details)
            val photoUrl = "$BACKEND_URL/general/image/${user.profile.photo}"
            Picasso.with(applicationContext).load(photoUrl).into(photo)
            Log.d("aici", user.toString())
            Toast.makeText(applicationContext, user.toString(), Toast.LENGTH_SHORT).show()
        })

        rq.add(req)
    }

    fun setProfile(view: View) {
        val profile = ProfileModel(
            username = username.text.toString(),
            details = details.text.toString(),
            id = null,
            photo = null
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
            Log.d(null, data?.data.toString())
            data?.data?.let { createImageData(it) }
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

    private fun uploadImage(imageData: ByteArray) {
        val request = object : VolleyFileUploadRequest(
            Method.POST,
            "${Constants.BACKEND_URL}/profile/setPhoto",
            authToken,
            Response.Listener {
                println("response is: $it")
            },
            Response.ErrorListener {
                println("error is: $it")
            }
        ) {
            override fun getByteData(): MutableMap<String, FileDataPart> {
                var params = HashMap<String, FileDataPart>()
                params["imageFile"] = FileDataPart("image", imageData!!, "jpeg")
                return params
            }
        }
        Queue.getQueue().add(request)
    }

    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            uploadImage((it.readBytes()))
        }
    }

}