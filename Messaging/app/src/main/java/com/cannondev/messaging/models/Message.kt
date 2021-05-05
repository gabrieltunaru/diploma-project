package com.cannondev.messaging.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(val type: String, val token: String, val text: String?, val receiver: String?): Gsonable()