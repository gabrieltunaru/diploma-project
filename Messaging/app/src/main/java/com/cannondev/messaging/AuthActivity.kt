package com.cannondev.messaging

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.models.AuthResponse
import com.cannondev.messaging.models.LoginInfo
import com.google.gson.Gson
import org.json.JSONObject


class AuthActivity : Fragment() {
    private val TAG = "AuthActivity"
    lateinit var email: EditText
    lateinit var password: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_main, null) as ViewGroup

        email = root.findViewById(R.id.editTextTextEmailAddress)
        password = root.findViewById(R.id.editTextNumberPassword)
        val btn = root.findViewById<Button>(R.id.loginButton)
        btn.setOnClickListener { login(it) }
        return root
    }

    private fun showDialog(jsonData: JSONObject) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("No account found for this email")
        builder.setMessage("Do you want to create a new account?")
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            Queue.post("/user/register", jsonData, Response.Listener { r ->
                try {
                    Toast.makeText(activity, "Account created!", Toast.LENGTH_SHORT).show()
                    val authResponse = Gson().fromJson(r.toString(), AuthResponse::class.java)
                    saveToken(authResponse.key)
//                    goToProfile()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, e.localizedMessage)
                    Log.e(TAG, "response: $r")
                    Toast.makeText(activity, "There has been an error", Toast.LENGTH_SHORT).show()
                }
            })
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
        val action =
            SpecifyAmountFragmentDirections
                .actionSpecifyAmountFragmentToConfirmationFragment(amount)
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