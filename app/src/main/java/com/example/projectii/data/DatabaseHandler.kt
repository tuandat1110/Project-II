package com.example.projectii.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context:Context): SQLiteOpenHelper(context, DBName, null, DBVersion)  {

    companion object {
        // Thông tin Database
        val DBName = "ContactDB"    // Tên database
        val DBVersion = 1           // Phiên bản database

        // Thông tin bảng
        val tableName = "UserTable"
        val userName = "username"   //cot username
        val passWord = "password"   //cot password
    }

    private val sqlObj: SQLiteDatabase = this.writableDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        val sql1 = "CREATE TABLE IF NOT EXISTS $tableName ("+
                " $userName VARCHAR(50) PRIMARY KEY," +
                " $passWord VARCHAR(255) NOT NULL );"

        db?.execSQL(sql1)
    }
    
    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db!!.execSQL("DROP TABLE IF EXISTS $tableName") // Xóa bảng cũ
        onCreate(db) // Tạo lại bảng mới
    }


}