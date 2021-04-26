package com.cannondev.messaging.http

import android.content.Context
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.cannondev.messaging.models.Gsonable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class Contact(val contactPseudoId: String): Gsonable()

object ContactsHttp {
    fun addContact(contactPseudoId: String, ctx: Context, resListener: Response.Listener<JSONObject>) {
        val contact = Contact(contactPseudoId)
        Queue.jsonRequest("contacts/add", contact, ctx, resListener)
    }

    fun getContacts(listener: Response.Listener<JSONObject>, ctx: Context) {
        Queue.jsonRequest("contacts/getAll", null, ctx, listener)
    }
}