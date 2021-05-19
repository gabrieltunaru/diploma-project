package com.cannondev.messaging.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.Response
import com.cannondev.messaging.R
import com.cannondev.messaging.http.Queue
import com.cannondev.messaging.http.UserHttp
import com.cannondev.messaging.models.UserModel
import com.cannondev.messaging.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class SettingsFragment : Fragment() {
    private val TAG = this::class.java.simpleName
    private lateinit var privateIdTextView: TextView
    private lateinit var regenerateBtn: Button
    private lateinit var ctx: Context
    private lateinit var userHttp: UserHttp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun initViews(root: View) {
        ctx = requireContext()
        userHttp = UserHttp(Utils.getSavedAuthToken(ctx))
        privateIdTextView = root.findViewById(R.id.privateIdTextView)
        regenerateBtn = root.findViewById(R.id.regeneratePvID)
        regenerateBtn.setOnClickListener{
            userHttp.regeneratePrivateId {
                val privateId = JSONObject(it).get("privateId").toString()
                privateIdTextView.text = privateId
            }
        }
    }

    fun populatePrivateId() {
        val ctx = requireContext()
        val token = Utils.getSavedAuthToken(ctx)
        UserHttp(token).getCurrentUser(ctx, Response.Listener{ data ->
            val user = Gson().fromJson(data, UserModel::class.java)
            privateIdTextView.text = user.privateId
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        initViews(root)
        populatePrivateId()
        return root
    }


}