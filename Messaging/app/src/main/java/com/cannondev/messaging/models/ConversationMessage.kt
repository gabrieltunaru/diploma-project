package com.cannondev.messaging.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class ConversationMessage(val type: String, val token: String, val text: String?, val otherUserId: String, val conversationId: String): Gsonable()