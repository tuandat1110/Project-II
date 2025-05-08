package com.example.projectii.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        // Thông tin Database
        const val DB_NAME = "Project-II-update"    // Tên database
        const val DB_VERSION = 8                   // Phiên bản database

        // Thông tin bảng tài khoản
        const val TABLE_ACCOUNT = "Account"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_EMAIL = "email"

        // Thông tin bảng user
        const val TABLE_USER = "User"
        const val COLUMN_FULLNAME = "Name"
        const val COLUMN_EMAIL_USER = "Email"
        const val COLUMN_PHONE = "PhoneNumber"
        const val COLUMN_ADDRESS = "Address"

        // Thông tin bảng lightBulb
        const val TABLE_LIGHT_BULB = "LightBulb"
        const val COLUMN_NAME_LIGHT = "NameLight"
        const val COLUMN_PIN = "Pin"
        //const val COLUMN_BRIGHTNESS = "Brightness"
        const val COLUMN_STATUS = "Status"
        const val COLUMN_IP = "IP"

        // Thông tin bảng set timer
        const val TABLE_SET_TIMER = "SetTimer"
        const val COLUMN_SET_TIME = "SetTime"
        const val COLUMN_ACTIVE_TIME = "ActiveTime"
        const val COLUMN_TOGGLE_STATUS = "ToggleStatus"

        // Thông tin bảng Room
        const val TABLE_ROOM = "Room"
        const val COLUMN_ROOM_NAME = "RoomName"
        const val COLUMN_NUMBER_OF_LIGHTS = "NumberOfLights"

        // Thông tin bảng Account_Room
        const val TABLE_ACCOUNT_ROOM = "AccountRoom"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // 1. Tạo bảng User trước vì Account tham chiếu đến nó
        val sqlUser = """
            CREATE TABLE IF NOT EXISTS $TABLE_USER (
                $COLUMN_FULLNAME VARCHAR(255),
                $COLUMN_EMAIL_USER VARCHAR(255) PRIMARY KEY,
                $COLUMN_PHONE VARCHAR(20),
                $COLUMN_ADDRESS VARCHAR(100)
            );
        """.trimIndent()

        // 2. Tạo bảng Account, tham chiếu đến User
        val sqlAccount = """
               CREATE TABLE IF NOT EXISTS $TABLE_ACCOUNT (
                $COLUMN_USERNAME VARCHAR(50) NOT NULL,
                $COLUMN_PASSWORD VARCHAR(255) NOT NULL,
                $COLUMN_EMAIL VARCHAR(255) NOT NULL,
                FOREIGN KEY ($COLUMN_EMAIL) REFERENCES $TABLE_USER($COLUMN_EMAIL_USER) ON DELETE CASCADE,
                PRIMARY KEY ($COLUMN_USERNAME, $COLUMN_EMAIL)
               );
        """.trimIndent()

        // 3. Tạo bảng Room
        val sqlRoom = """
            CREATE TABLE IF NOT EXISTS $TABLE_ROOM (
                $COLUMN_ROOM_NAME VARCHAR(100) PRIMARY KEY,
                $COLUMN_NUMBER_OF_LIGHTS INTEGER
            );
        """.trimIndent()

        // 4. Tạo bảngul LightBb, tham chiếu đến Room
        val sqlLightBulb = """
            CREATE TABLE IF NOT EXISTS $TABLE_LIGHT_BULB (
                $COLUMN_NAME_LIGHT VARCHAR(100),
                $COLUMN_PIN VARCHAR(30),
                $COLUMN_IP VARCHAR(40),
                $COLUMN_STATUS BOOLEAN,
                $COLUMN_ROOM_NAME VARCHAR(100),
                PRIMARY KEY ($COLUMN_NAME_LIGHT, $COLUMN_ROOM_NAME),
                FOREIGN KEY ($COLUMN_ROOM_NAME) REFERENCES $TABLE_ROOM($COLUMN_ROOM_NAME) ON DELETE CASCADE
            );
        """.trimIndent()

        // 5. Tạo bảng SetTimer, tham chiếu đến Account và LightBulb
        val sqlSetTimer = """
            CREATE TABLE IF NOT EXISTS $TABLE_SET_TIMER (
                $COLUMN_USERNAME VARCHAR(50),
                $COLUMN_NAME_LIGHT VARCHAR(100),
                $COLUMN_SET_TIME DATETIME,
                $COLUMN_ACTIVE_TIME DATETIME,
                $COLUMN_TOGGLE_STATUS BOOLEAN,
                PRIMARY KEY ($COLUMN_USERNAME, $COLUMN_NAME_LIGHT, $COLUMN_ACTIVE_TIME),
                FOREIGN KEY ($COLUMN_USERNAME) REFERENCES $TABLE_ACCOUNT($COLUMN_USERNAME) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_NAME_LIGHT) REFERENCES $TABLE_LIGHT_BULB($COLUMN_NAME_LIGHT) ON DELETE CASCADE
            );
        """.trimIndent()

        // 6. Tạo bảng AccountRoom, tham chiếu đến Account và Room
        val sqlAccountRoom = """
            CREATE TABLE IF NOT EXISTS $TABLE_ACCOUNT_ROOM (
                $COLUMN_USERNAME VARCHAR(50),
                $COLUMN_ROOM_NAME VARCHAR(100),
                PRIMARY KEY ($COLUMN_USERNAME, $COLUMN_ROOM_NAME),
                FOREIGN KEY ($COLUMN_USERNAME) REFERENCES $TABLE_ACCOUNT($COLUMN_USERNAME) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_ROOM_NAME) REFERENCES $TABLE_ROOM($COLUMN_ROOM_NAME) ON DELETE CASCADE
            );
        """.trimIndent()

        // Thực thi các lệnh SQL theo thứ tự logic
        db?.execSQL(sqlUser)        // Tạo User trước
        db?.execSQL(sqlAccount)     // Tạo Account sau vì tham chiếu User
        db?.execSQL(sqlRoom)        // Tạo Room
        db?.execSQL(sqlLightBulb)   // Tạo LightBulb sau vì tham chiếu Room
        db?.execSQL(sqlSetTimer)    // Tạo SetTimer sau vì tham chiếu Account và LightBulb
        db?.execSQL(sqlAccountRoom) // Tạo AccountRoom sau vì tham chiếu Account và Room
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Xóa các bảng nếu tồn tại, theo thứ tự ngược với onCreate để tránh lỗi khóa ngoại
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT_ROOM")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SET_TIMER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LIGHT_BULB")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ROOM")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        // Tạo lại cơ sở dữ liệu
        onCreate(db)
    }
}