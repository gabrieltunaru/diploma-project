package com.cannondev.messaging.models

import com.google.gson.annotations.SerializedName

data class ProfileModel(@SerializedName("_id") val id: String?, val username: String?, val photo: String?, val details: String?): Gsonable()