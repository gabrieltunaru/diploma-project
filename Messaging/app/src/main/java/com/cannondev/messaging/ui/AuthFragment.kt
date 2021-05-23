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
import com.cannondev.messaging.models.Gsonable
import com.cannondev.messaging.models.LoginInfo
import com.cannondev.messaging.utils.Encryption
import com.cannondev.messaging.utils.Utils
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

    private fun register(data: LoginInfo) {
        val kp = Encryption.generate()!!
        data.pbKey = Base64.encodeToString(kp.public.encoded, Base64.DEFAULT)
        val jsonData = data.toJson()
        Queue.post("/user/register", jsonData, { r ->
            Toast.makeText(activity, "Account created!", Toast.LENGTH_SHORT).show()
            val authResponse = Gson().fromJson(r.toString(), AuthResponse::class.java)
            saveToken(authResponse.key)
            goToProfile()
        })
    }

    private fun confirm(data: LoginInfo) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("No account found for this email")
        builder.setMessage("Do you want to create a new account?")
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            register(data)
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
        }

        builder.show()

    }

    private fun saveToken(token: String) {
        Utils.saveToPrefs(this.requireContext(), "authToken", token)
    }

    private fun goToProfile() {
        NavHostFragment.findNavController(this).navigate(R.id.action_nav_home_to_nav_gallery)
    }

    private fun login(view: View) {
        val data = LoginInfo(email.text.toString(), password.text.toString(), null)
        val jsonData = JSONObject(Gson().toJson(data))
        Queue.post("/user/auth", jsonData, Response.Listener { r ->
            try {
                val authResponse = Gson().fromJson(r.toString(), AuthResponse::class.java)
                Toast.makeText(activity, "Logged in!", Toast.LENGTH_SHORT).show()
                saveToken(authResponse.key)
                goToProfile()
            } catch (e: Exception) {
                Toast.makeText(activity, "There has been an error", Toast.LENGTH_SHORT).show()
            }
        }, { e ->
            if (e.networkResponse.statusCode == 404) {
                confirm(data)
            } else {
                Queue.handleNetworkError(e)
            }
        })
    }
}