package com.example.projectii.data

import android.content.ContentValues
import android.content.Context

class UserDAO(context:Context) {
    private val dbHelper = DatabaseHandler(context)

    fun insertUser(user: UserData): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHandler.userName, user.username)
            put(DatabaseHandler.passWord, user.password)
        }
        val result = db.insert(DatabaseHandler.tableName, null, values)
        db.close()
        return result != -1L
    }

    fun checkLogin(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHandler.tableName} WHERE ${DatabaseHandler.userName} = ? AND ${DatabaseHandler.passWord} = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))

        var isLoggedIn = false

        if (cursor != null && cursor.moveToFirst()) { // Kiểm tra cursor có dữ liệu không
            isLoggedIn = true
        }

        cursor?.close()
        db.close()

        return isLoggedIn
    }
}