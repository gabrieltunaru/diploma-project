package com.cannondev.messaging.models

import com.google.gson.annotations.SerializedName

data class UserModel(@SerializedName("_id") val id: String, val email: String, val profile: ProfileModel)