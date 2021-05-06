package com.cannondev.messaging.models

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConversationModel(@SerializedName("_id") val id: String, val isPrivate: Boolean, val otherUser: UserModel): Gsonable()