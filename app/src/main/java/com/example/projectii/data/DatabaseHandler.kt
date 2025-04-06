package com.example.projectii.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        // Thông tin Database
        const val DB_NAME = "Project-II"    // Tên database
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

        // Thông tin bảng lightBulb
        const val TABLE_LIGHT_BULB = "LightBulb"
        const val COLUMN_NAME_LIGHT = "NameLight"
        const val COLUMN_PIN = "Pin"
        const val COLUMN_BRIGHTNESS = "Brightness"
        const val COLUMN_STATUS = "Status"

        // Thông tin bảng turn on/turn off
        const val TABLE_TURN_ON_OFF = "TurnOnOff"
        const val COLUMN_TURN_ON_OFF_TIME = "ScheduleTime"
        const val COLUMN_TOGGLE_STATUS = "ToggleStatus"

        // Thông tin bảng set timer
        const val TABLE_SET_TIMER = "SetTimer"
        const val COLUMN_SET_TIME = "SetTime"
        const val COLUMN_ACTIVE_TIME = "ActiveTime"
        const val COLUMN_TOGGLE_STATUS1 = "Toggle status"

        // Thông tin bảng Room
        const val TABLE_ROOM = "Room"
        const val COLUMN_ROOM_NAME = "RoomName"
        const val COLUMN_NUMBER_OF_LIGHTS = "NumberOfLights"

        //Thông tin bảng Account_Room
        const val TABLE_ACCOUNT_ROOM = "AccountRoom"
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

        val sql3 = """
            CREATE TABLE IF NOT EXISTS $TABLE_ROOM (
                $COLUMN_ROOM_NAME VARCHAR(100) PRIMARY KEY,
                $COLUMN_NUMBER_OF_LIGHTS INT
            );
        """.trimIndent()

        val sql4 = """
            CREATE TABLE IF NOT EXISTS $TABLE_LIGHT_BULB (
                $COLUMN_NAME_LIGHT VARCHAR(100) PRIMARY KEY,
                $COLUMN_PIN INT,
                $COLUMN_BRIGHTNESS INT,
                $COLUMN_STATUS BOOLEAN,
                $COLUMN_ROOM_NAME VARCHAR(100),
                FOREIGN KEY ($COLUMN_ROOM_NAME) REFERENCES $TABLE_ROOM($COLUMN_ROOM_NAME) ON DELETE CASCADE
            );
        """.trimIndent()

        val sql5= """
            CREATE TABLE IF NOT EXISTS $TABLE_TURN_ON_OFF (
                $COLUMN_USERNAME VARCHAR(50),
                $COLUMN_NAME_LIGHT VARCHAR(100),
                $COLUMN_TURN_ON_OFF_TIME DATETIME,
                $COLUMN_TOGGLE_STATUS BOOLEAN,
                PRIMARY KEY ($COLUMN_USERNAME, $COLUMN_NAME_LIGHT, $COLUMN_TURN_ON_OFF_TIME),
                FOREIGN KEY ($COLUMN_USERNAME) REFERENCES $TABLE_ACCOUNT($COLUMN_USERNAME) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_NAME_LIGHT) REFERENCES $TABLE_LIGHT_BULB($COLUMN_NAME_LIGHT) ON DELETE CASCADE
            );
        """.trimIndent()

        val sql6 = """
            CREATE TABLE IF NOT EXISTS $TABLE_SET_TIMER (
                $COLUMN_USERNAME VARCHAR(50),
                $COLUMN_NAME_LIGHT VARCHAR(100),
                $COLUMN_SET_TIME DATETIME,
                $COLUMN_ACTIVE_TIME DATETIME,
                $COLUMN_TOGGLE_STATUS1 BOOLEAN,
                PRIMARY KEY ($COLUMN_USERNAME, $COLUMN_NAME_LIGHT, $COLUMN_ACTIVE_TIME),
                FOREIGN KEY ($COLUMN_USERNAME) REFERENCES $TABLE_ACCOUNT($COLUMN_USERNAME) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_NAME_LIGHT) REFERENCES $TABLE_LIGHT_BULB($COLUMN_NAME_LIGHT) ON DELETE CASCADE
            );
        """.trimIndent()

        val sql7 = """
            CREATE TABLE IF NOT EXISTS $TABLE_ACCOUNT_ROOM (
                $COLUMN_USERNAME VARCHAR(50),
                $COLUMN_ROOM_NAME VARCHAR(100),
                PRIMARY KEY ($COLUMN_USERNAME, $COLUMN_ROOM_NAME),
                FOREIGN KEY ($COLUMN_USERNAME) REFERENCES $TABLE_ACCOUNT($COLUMN_USERNAME) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_ROOM_NAME) REFERENCES $TABLE_ROOM($COLUMN_ROOM_NAME) ON DELETE CASCADE
            );
        """.trimIndent()


        db?.execSQL(sql7)
        db?.execSQL(sql6)
        db?.execSQL(sql5)
        db?.execSQL(sql4)
        db?.execSQL(sql3)
        db?.execSQL(sql2) // Tạo bảng User trước vì Account có ràng buộc FOREIGN KEY
        db?.execSQL(sql1)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SET_TIMER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TURN_ON_OFF")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_LIGHT_BULB")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ROOM")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT_ROOM")
        onCreate(db)
    }
}
