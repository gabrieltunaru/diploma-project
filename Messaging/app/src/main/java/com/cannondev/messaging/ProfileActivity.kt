package com.cannondev.messaging

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
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


class ProfileActivity : AppCompatActivity() {
    val rq = Queue.getQueue()
    lateinit var userHttp: UserHttp
    lateinit var authToken: String
    lateinit var username: EditText
    lateinit var details: EditText
    lateinit var photo: ImageView
    lateinit var user: UserModel
    lateinit var fileName: String
    private lateinit var appBarConfiguration: AppBarConfiguration

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
        if (requestCode == PICK_IMAGE) {
            data?.data?.let { ImageHandler.uploadImage(it, baseContext, authToken, Response.Listener{
                fileName = String(it.data)
                ImageHandler.loadPhoto(baseContext, String(it.data), photo)
            }) }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        username = findViewById(R.id.profileUsername)
        details = findViewById(R.id.profileDetails)
        photo = findViewById(R.id.profileAvatar)


        val sharedPref =
            this.getSharedPreferences(getString(R.string.shared_prefs_file), Context.MODE_PRIVATE)
        authToken = sharedPref.getString("authToken", "").toString()
        userHttp = UserHttp(authToken)
        getCurrentUser()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}