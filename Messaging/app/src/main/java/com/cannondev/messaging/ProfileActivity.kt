package com.cannondev.messaging

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.Response
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.http.UserHttp
import com.cannondev.messaging.models.ProfileModel
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.ImageHandler
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson


class ProfileActivity : Fragment() {
    val rq = Queue.getQueue()
    lateinit var userHttp: UserHttp
    lateinit var authToken: String
    lateinit var username: EditText
    lateinit var details: EditText
    lateinit var photo: ImageView
    lateinit var user: UserModel
    lateinit var fileName: String
    lateinit var ctx: Context
    private lateinit var appBarConfiguration: AppBarConfiguration

    fun getCurrentUser() {
        val ctx = activity?.applicationContext!!
        val req = userHttp.getCurrentUser(ctx, Response.Listener { data ->
            user = Gson().fromJson(data, UserModel::class.java)
            username.setText(user.profile?.username)
            details.setText(user.profile?.details)
            ImageHandler.loadPhoto(ctx, user.profile?.photo, photo)
        })

        rq.add(req)
    }

    fun setProfile(view: View) {
        val profile = ProfileModel(
            username = username.text.toString(),
            details = details.text.toString(),
            id = null,
            photo = fileName
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
        val ctx = requireContext()
        if (requestCode == PICK_IMAGE) {
            data?.data?.let { ImageHandler.uploadImage(it, ctx, authToken, Response.Listener{
                fileName = String(it.data)
                ImageHandler.loadPhoto(ctx, String(it.data), photo)
            }) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_profile, null) as ViewGroup

        username = root.findViewById(R.id.profileUsername)
        details = root.findViewById(R.id.profileDetails)
        photo = root.findViewById(R.id.profileAvatar)
        val btn = root.findViewById<Button>(R.id.saveProfile)
        btn.setOnClickListener { setProfile(it) }
        photo.setOnClickListener { setPhoto(it) }
        val sharedPref =
            activity?.getSharedPreferences(getString(R.string.shared_prefs_file), Context.MODE_PRIVATE)!!
        authToken = sharedPref.getString("authToken", "").toString()
        userHttp = UserHttp(authToken)
        getCurrentUser()
        return root
    }

}