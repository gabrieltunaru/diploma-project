package com.cannondev.messaging.utils

import android.content.Context
import com.cannondev.messaging.R

object Utils {

    fun saveToPrefs(ctx: Context, key: String, value: String) {
        val sharedPref =
            ctx.getSharedPreferences(
                ctx.getString(R.string.shared_prefs_file),
                Context.MODE_PRIVATE
            )!!
        sharedPref.edit().putString(key, value).apply()
    }


    fun getFromPrefs(ctx: Context, key: String): String {
        val sharedPref =
            ctx.getSharedPreferences(
                ctx.getString(R.string.shared_prefs_file),
                Context.MODE_PRIVATE
            )!!
        return sharedPref.getString(key, "").toString()
    }


    fun getSavedAuthToken(ctx: Context): String {
        return getFromPrefs(ctx, "authToken")
    }

}