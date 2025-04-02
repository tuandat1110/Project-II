package com.example.projectii.data

import android.content.ContentValues
import android.content.Context

class UserDAO(context:Context) {
    private val dbHelper = DatabaseHandler(context)

    fun insertUser(user: UserData): Boolean {
        val db = dbHelper.writableDatabase

        // Kiểm tra username tồn tại trước
        val cursor = db.query(
            DatabaseHandler.TABLE_ACCOUNT,
            arrayOf(DatabaseHandler.COLUMN_USERNAME),
            "${DatabaseHandler.COLUMN_USERNAME} = ?",
            arrayOf(user.username),
            null, null, null
        )

        val usernameExists = cursor.count > 0
        cursor.close()

        if (usernameExists) {
            db.close()
            return false
        }

        // Nếu username chưa tồn tại thì insert
        val values = ContentValues().apply {
            put(DatabaseHandler.COLUMN_USERNAME, user.username)
            put(DatabaseHandler.COLUMN_PASSWORD, user.password)
            put(DatabaseHandler.COLUMN_EMAIL, user.email)
        }

        val result = db.insert(DatabaseHandler.TABLE_ACCOUNT, null, values)
        db.close()
        return result != -1L
    }

    fun insertUser1(user:User): Boolean{
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHandler.COLUMN_FULLNAME, user.fullName)
            put(DatabaseHandler.COLUMN_EMAIL, user.email)
            put(DatabaseHandler.COLUMN_PHONE, user.phone)
            put(DatabaseHandler.COLUMN_ADDRESS, user.address)
        }
        val rs = db.insert(DatabaseHandler.TABLE_USER,null,values)
        db.close()
        return rs != -1L
    }

    fun updateUser(user: User, email: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            // Đưa các giá trị từ đối tượng User vào ContentValues
            put(DatabaseHandler.COLUMN_FULLNAME, user.fullName)
            put(DatabaseHandler.COLUMN_PHONE, user.phone)
            put(DatabaseHandler.COLUMN_ADDRESS,user.address)
        }

        // Số hàng bị ảnh hưởng bởi câu lệnh update
        val rowsAffected = db.update(
            DatabaseHandler.TABLE_USER,                 // Tên bảng
            values,                      // Giá trị mới
            "${DatabaseHandler.COLUMN_EMAIL} = ?",                 // Điều kiện WHERE
            arrayOf(email)               // Giá trị cho điều kiện WHERE
        )

        // Trả về true nếu có ít nhất 1 hàng được cập nhật
        return rowsAffected > 0
    }

    fun checkUser(email:String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHandler.TABLE_USER} WHERE ${DatabaseHandler.COLUMN_EMAIL} = ?"
        val cursor = db.rawQuery(query,arrayOf(email))

        var isCheck = false
        if(cursor != null && cursor.moveToFirst()){
            isCheck = true
        }
        cursor?.close()
        db.close()
        return isCheck
    }

    fun checkLogin(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHandler.TABLE_ACCOUNT} WHERE ${DatabaseHandler.COLUMN_USERNAME} = ? AND ${DatabaseHandler.COLUMN_PASSWORD} = ?"
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