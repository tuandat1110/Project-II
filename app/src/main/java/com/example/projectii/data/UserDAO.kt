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

    fun findUser(email: String): Boolean{
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHandler.TABLE_USER} WHERE ${DatabaseHandler.COLUMN_EMAIL} = ? "
        val cursor = db.rawQuery(query, arrayOf(email))
        var isCheck = false
        if (cursor != null && cursor.moveToFirst()) { // Kiểm tra cursor có dữ liệu không
            isCheck = true
        }
        cursor?.close()
        db.close()
        return isCheck
    }

    fun findAccount(user:String): Boolean{
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHandler.TABLE_ACCOUNT} WHERE ${DatabaseHandler.COLUMN_USERNAME} = ? "
        val cursor = db.rawQuery(query, arrayOf(user))
        var isCheck = false
        if (cursor != null && cursor.moveToFirst()) { // Kiểm tra cursor có dữ liệu không
            isCheck = true
        }
        cursor?.close()
        db.close()
        return isCheck
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

        return rowsAffected > 0
    }

    fun insertSampleRooms() {
        val db = dbHelper.writableDatabase
        db.execSQL("INSERT INTO ${DatabaseHandler.TABLE_ROOM} (${DatabaseHandler.COLUMN_ROOM_NAME}, ${DatabaseHandler.COLUMN_NUMBER_OF_LIGHTS}) VALUES ('Living Room', 3)")
        db.execSQL("INSERT INTO ${DatabaseHandler.TABLE_ROOM} (${DatabaseHandler.COLUMN_ROOM_NAME}, ${DatabaseHandler.COLUMN_NUMBER_OF_LIGHTS}) VALUES ('Bedroom', 2)")
        db.execSQL("INSERT INTO ${DatabaseHandler.TABLE_ROOM} (${DatabaseHandler.COLUMN_ROOM_NAME}, ${DatabaseHandler.COLUMN_NUMBER_OF_LIGHTS}) VALUES ('Kitchen', 4)")
        db.execSQL("INSERT INTO ${DatabaseHandler.TABLE_ROOM} (${DatabaseHandler.COLUMN_ROOM_NAME}, ${DatabaseHandler.COLUMN_NUMBER_OF_LIGHTS}) VALUES ('Bathroom', 1)")
        db.execSQL("INSERT INTO ${DatabaseHandler.TABLE_ROOM} (${DatabaseHandler.COLUMN_ROOM_NAME}, ${DatabaseHandler.COLUMN_NUMBER_OF_LIGHTS}) VALUES ('Garage', 2)")
        db.close()
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

//    fun deleteLastUser() {
//        val db = dbHelper.writableDatabase
//        db.execSQL("DELETE FROM UserTable WHERE ROWID = (SELECT MAX(ROWID) FROM UserTable)")
//        db.close()
//    }

    fun getAccountByUsername(username:String) : UserData? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHandler.TABLE_ACCOUNT} WHERE ${DatabaseHandler.COLUMN_USERNAME} = ?",arrayOf(username))
        var user: UserData ?= null

        if(cursor.moveToFirst()){
            val userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_USERNAME))
            val passWord = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_PASSWORD))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_EMAIL))
            user = UserData(userName,passWord,email)
        }

        cursor.close()
        return user
    }

    fun getUserByEmail(email:String): User?{
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHandler.TABLE_USER} WHERE ${DatabaseHandler.COLUMN_EMAIL} = ?",arrayOf(email))
        var user:User ?= null

        if(cursor.moveToFirst()){
            val fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_FULLNAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_PHONE))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_ADDRESS))
            user = User(fullName,email,phone,address)
        }

        cursor.close()
        return user
    }

}