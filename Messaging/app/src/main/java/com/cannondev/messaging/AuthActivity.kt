package com.cannondev.messaging

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.models.AuthResponse
import com.cannondev.messaging.models.LoginInfo
import com.google.gson.Gson
import org.json.JSONObject
import java.lang.Exception

class AuthActivity : AppCompatActivity() {
    private val TAG = "AuthActivity"
    lateinit var email: EditText
    lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextNumberPassword)

    }

    private fun showDialog(jsonData: JSONObject) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No account found for this email")
        builder.setMessage("Do you want to create a new account?")
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Queue.post("/user/register", jsonData, Response.Listener { r ->
                try {
                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                    val authResponse = Gson().fromJson(r.toString(), AuthResponse::class.java)
                    saveToken(authResponse.key)
                    goToProfile()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, e.localizedMessage)
                    Log.e(TAG, "response: $r")
                    Toast.makeText(this, "There has been an error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
        }

        builder.show()

    }

    fun saveToken(token: String) {
        val sharedPref = this.getSharedPreferences(getString(R.string.shared_prefs_file),Context.MODE_PRIVATE)
        sharedPref.edit().putString("authToken", token).apply()
        sharedPref.getString("authToken", "nope")?.let { Log.d("s-a salvat cheia", it) }
    }

    fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun login(view: View) {
        val data = LoginInfo(email.text.toString(), password.text.toString())
        val jsonData = JSONObject(Gson().toJson(data))
        Queue.post("/user/auth", jsonData, Response.Listener { r ->
            try {
                val authResponse = Gson().fromJson(r.toString(), AuthResponse::class.java)
                if (authResponse?.notFound == true) {
                    showDialog(jsonData)
                } else {
                    Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show()
                    saveToken(authResponse.key)
                    goToProfile()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, e.localizedMessage)
                Log.e(TAG, "response: $r")
                Toast.makeText(this, "There has been an error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}