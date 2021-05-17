package com.cannondev.messaging.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.Response
import com.cannondev.messaging.R
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.models.AuthResponse
import com.cannondev.messaging.models.LoginInfo
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.Encryption
import com.google.gson.Gson
import org.json.JSONObject


class AuthFragment : Fragment() {
    private val TAG = "AuthActivity"
    lateinit var email: EditText
    lateinit var password: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_auth, null) as ViewGroup

        email = root.findViewById(R.id.editTextTextEmailAddress)
        password = root.findViewById(R.id.editTextNumberPassword)
        val btn = root.findViewById<Button>(R.id.loginButton)
        btn.setOnClickListener { login(it) }
        return root
    }

    private fun showDialog(data: LoginInfo) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("No account found for this email")
        builder.setMessage("Do you want to create a new account?")
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val kp = Encryption.generate()!!
            data.pbKey = Base64.encodeToString(kp.public.encoded, Base64.DEFAULT)
            val jsonData = data.toJsonString()
            Queue.post("/user/register", jsonData) { r ->
                try {
                    Toast.makeText(activity, "Account created!", Toast.LENGTH_SHORT).show()
                    val authResponse = Gson().fromJson(r.toString(), AuthResponse::class.java)
                    saveToken(authResponse.key)
                    goToProfile()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, e.localizedMessage)
                    Log.e(TAG, "response: $r")
                    Toast.makeText(activity, "There has been an error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
        }

        builder.show()

    }

    fun saveToken(token: String) {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.shared_prefs_file),
            Context.MODE_PRIVATE
        )
        sharedPref?.edit()?.putString("authToken", token)?.apply()
        sharedPref?.getString("authToken", "nope")?.let { Log.d("s-a salvat cheia", it) }
    }

    fun goToProfile() {
        NavHostFragment.findNavController(this).navigate(R.id.action_nav_home_to_nav_gallery)
    }

    fun login(view: View) {
        val data = LoginInfo(email.text.toString(), password.text.toString(), null)
        val jsonData = JSONObject(Gson().toJson(data))
        Queue.post("/user/auth", jsonData, Response.Listener { r ->
            try {
                val authResponse = Gson().fromJson(r.toString(), AuthResponse::class.java)
                if (authResponse?.notFound == true) {
                    showDialog(data)
                } else {
                    Toast.makeText(activity, "Logged in!", Toast.LENGTH_SHORT).show()
                    saveToken(authResponse.key)
                    goToProfile()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, e.localizedMessage)
                Log.e(TAG, "response: $r")
                Toast.makeText(activity, "There has been an error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}