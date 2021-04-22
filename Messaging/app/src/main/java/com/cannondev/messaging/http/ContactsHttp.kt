package com.cannondev.messaging.http

import android.content.Context
import com.cannondev.messaging.models.Gsonable

data class Contact(val contactPseudoId: String): Gsonable()

object ContactsHttp {
    fun addContact(contactPseudoId: String, ctx: Context) {
        val contact = Contact(contactPseudoId)
        Queue.jsonRequest("contacts/add", contact, ctx, null)
    }
}