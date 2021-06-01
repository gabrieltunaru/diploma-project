package com.cannondev.messaging.utils

import android.provider.BaseColumns

object MessageContract {
    object MessageEntry: BaseColumns {
        const val TABLE_NAME="message"
        const val COLUMN_NAME_TYPE="type"
        const val COLUMN_NAME_TEXT="text"
        const val COLUMN_NAME_IS_SENDER="isSender"
        const val COLUMN_NAME_CONVERSATION_ID="conversationId"
        const val COLUMN_NAME_TIMESTAMP="timestamp"
    }

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${MessageEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${MessageEntry.COLUMN_NAME_TYPE} TEXT," +
                "${MessageEntry.COLUMN_NAME_IS_SENDER} BOOL," +
                "${MessageEntry.COLUMN_NAME_TEXT} TEXT," +
                "${MessageEntry.COLUMN_NAME_CONVERSATION_ID} TEXT," +
                "${MessageEntry.COLUMN_NAME_TIMESTAMP} INT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${MessageEntry.TABLE_NAME}"
}