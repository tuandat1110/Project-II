package com.example.projectii.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        // Thông tin Database
        const val DB_NAME = "ContactDB"    // Tên database
        const val DB_VERSION = 3           // Phiên bản database

        // Thông tin bảng tài khoản
        const val TABLE_ACCOUNT = "Account"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        // Thông tin bảng user
        const val TABLE_USER = "User"
        const val COLUMN_FULLNAME = "Name"
        const val COLUMN_EMAIL = "Email"
        const val COLUMN_PHONE = "PhoneNumber"
        const val COLUMN_ADDRESS = "Address"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql1 = """
            CREATE TABLE IF NOT EXISTS $TABLE_ACCOUNT (
                $COLUMN_USERNAME VARCHAR(50) PRIMARY KEY,
                $COLUMN_PASSWORD VARCHAR(255) NOT NULL,
                $COLUMN_EMAIL VARCHAR(255) NOT NULL,
                FOREIGN KEY ($COLUMN_EMAIL) REFERENCES $TABLE_USER($COLUMN_EMAIL) ON DELETE CASCADE
            );
        """.trimIndent()

        val sql2 = """
            CREATE TABLE IF NOT EXISTS $TABLE_USER (
                $COLUMN_FULLNAME VARCHAR(255),
                $COLUMN_EMAIL VARCHAR(255) PRIMARY KEY,
                $COLUMN_PHONE VARCHAR(20),
                $COLUMN_ADDRESS VARCHAR(100)
            );
        """.trimIndent()

        db?.execSQL(sql2) // Tạo bảng User trước vì Account có ràng buộc FOREIGN KEY
        db?.execSQL(sql1)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }
}
