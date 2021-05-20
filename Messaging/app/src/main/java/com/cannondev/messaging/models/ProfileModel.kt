package com.cannondev.messaging.models

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileModel(@SerializedName("_id") val id: String?, val displayName: String?, val username: String?, val photo: String?, val details: String?): Gsonable()