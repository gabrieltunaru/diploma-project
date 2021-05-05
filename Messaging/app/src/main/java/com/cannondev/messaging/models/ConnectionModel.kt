package com.cannondev.messaging.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class ConnectionModel(val isPrivate: Boolean, val otherUser: UserModel): Gsonable()