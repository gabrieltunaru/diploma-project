package com.cannondev.messaging.models

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(@SerializedName("_id") val id: String, val email: String, val profile: ProfileModel?, var pbKey: String?): Gsonable()