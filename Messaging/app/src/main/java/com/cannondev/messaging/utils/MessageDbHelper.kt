package com.cannondev.messaging.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.cannondev.messaging.models.ConversationMessage
import java.sql.Timestamp
import java.text.SimpleDateFormat

class MessageDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(MessageContract.SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(MessageContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "Messages.db"
    }

    fun saveMessage(message: ConversationMessage, isSender: Boolean) {
        val values = ContentValues().apply {
            put(MessageContract.MessageEntry.COLUMN_NAME_TYPE, message.type)
            put(MessageContract.MessageEntry.COLUMN_NAME_TEXT, message.text)
            put(MessageContract.MessageEntry.COLUMN_NAME_IS_SENDER, isSender)
            put(MessageContract.MessageEntry.COLUMN_NAME_CONVERSATION_ID, message.conversationId)
            put(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP, message.timestamp)
        }

        writableDatabase.insert(
            MessageContract.MessageEntry.TABLE_NAME,
            null,
            values
        )
    }

    fun getMessages(conversationId: String): MutableList<ConversationMessage> {

        val selection = "${MessageContract.MessageEntry.COLUMN_NAME_CONVERSATION_ID} = ?"
        val selectionArgs = arrayOf(conversationId)

        val sortOrder = "${MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP} ASC"

        val cursor = readableDatabase.query(
            MessageContract.MessageEntry.TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        val messages = mutableListOf<ConversationMessage>()
        with(cursor) {
            while (moveToNext()) {
                val type = getString(getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_NAME_TYPE))
                val text = getString(getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_NAME_TEXT))
                val isSender = getInt(getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_NAME_IS_SENDER)) == 1
                val convId =
                    getString(getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_NAME_CONVERSATION_ID))
                val timestamp = getLong(getColumnIndexOrThrow(MessageContract.MessageEntry.COLUMN_NAME_TIMESTAMP))
                val message = ConversationMessage(type, null, text, null, convId, timestamp, isSender)
                messages.add(message)
            }
        }
        return messages
    }
}