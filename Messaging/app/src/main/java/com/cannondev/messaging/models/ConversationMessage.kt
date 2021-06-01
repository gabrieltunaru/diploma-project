package com.cannondev.messaging.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class ConversationMessage(
    val type: String,
    val token: String?,
    var text: String?,
    val otherUserId: String?,
    val conversationId: String,
    val timestamp: Long,
    val isSender: Boolean? = null
) : Gsonable()