package com.cannondev.messaging.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginInfo(var email: String, var password: String, var pbKey: String?): Gsonable()