package com.cannondev.messaging.utils

import android.content.Context
import com.cannondev.messaging.R

object Utils {
    fun getSavedAuthToken(ctx: Context): String {
        val sharedPref =
            ctx.getSharedPreferences(ctx.getString(R.string.shared_prefs_file), Context.MODE_PRIVATE)!!
        return sharedPref.getString("authToken", "").toString()
    }
}