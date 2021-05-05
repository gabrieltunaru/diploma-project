package com.cannondev.messaging.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class ConversationModel(val isPrivate: Boolean, val otherUser: UserModel): Gsonable()