package com.cannondev.messaging.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(val text: String): Gsonable()