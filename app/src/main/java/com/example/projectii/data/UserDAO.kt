package com.example.projectii.data

import android.content.ContentValues
import android.content.Context
import com.example.projectii.LightItem
import com.example.projectii.RoomItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserDAO(context: Context) {
    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
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

    fun insertUser1(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHandler.COLUMN_FULLNAME, user.fullName)
            put(DatabaseHandler.COLUMN_EMAIL, user.email)
            put(DatabaseHandler.COLUMN_PHONE, user.phone)
            put(DatabaseHandler.COLUMN_ADDRESS, user.address)
        }
        val rs = db.insert(DatabaseHandler.TABLE_USER, null, values)
        db.close()
        return rs != -1L
    }

    fun findUser(email: String): Boolean {
        val db = dbHelper.readableDatabase
        val query =
            "SELECT * FROM ${DatabaseHandler.TABLE_USER} WHERE ${DatabaseHandler.COLUMN_EMAIL} = ? "
        val cursor = db.rawQuery(query, arrayOf(email))
        var isCheck = false
        if (cursor != null && cursor.moveToFirst()) { // Kiểm tra cursor có dữ liệu không
            isCheck = true
        }
        cursor?.close()
        db.close()
        return isCheck
    }

    fun findAccount(user: String): Boolean {
        val db = dbHelper.readableDatabase
        val query =
            "SELECT * FROM ${DatabaseHandler.TABLE_ACCOUNT} WHERE ${DatabaseHandler.COLUMN_USERNAME} = ? "
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
            put(DatabaseHandler.COLUMN_ADDRESS, user.address)
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

    fun insertRoom(username: String, room: RoomItem): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            db.execSQL(
                "INSERT INTO ${DatabaseHandler.TABLE_ROOM} " +
                        "(${DatabaseHandler.COLUMN_ROOM_NAME}) " +
                        "VALUES ('${room.name}')"
            )


            db.execSQL(
                "INSERT INTO ${DatabaseHandler.TABLE_ACCOUNT_ROOM} " +
                        "(${DatabaseHandler.COLUMN_USERNAME}, ${DatabaseHandler.COLUMN_ROOM_NAME}) " +
                        "VALUES ('$username', '${room.name}')"
            )

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }


    fun checkUser(email: String): Boolean {
        val db = dbHelper.readableDatabase
        val query =
            "SELECT * FROM ${DatabaseHandler.TABLE_USER} WHERE ${DatabaseHandler.COLUMN_EMAIL} = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        var isCheck = false
        if (cursor != null && cursor.moveToFirst()) {
            isCheck = true
        }
        cursor?.close()
        db.close()
        return isCheck
    }

    fun checkLogin(username: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val query =
            "SELECT * FROM ${DatabaseHandler.TABLE_ACCOUNT} WHERE ${DatabaseHandler.COLUMN_USERNAME} = ? AND ${DatabaseHandler.COLUMN_PASSWORD} = ?"
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

    fun getAccountByUsername(username: String): UserData? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHandler.TABLE_ACCOUNT} WHERE ${DatabaseHandler.COLUMN_USERNAME} = ?",
            arrayOf(username)
        )
        var user: UserData? = null

        if (cursor.moveToFirst()) {
            val userName =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_USERNAME))
            val passWord =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_PASSWORD))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_EMAIL))
            user = UserData(userName, passWord, email)
        }

        cursor.close()
        return user
    }

    fun getUserByEmail(email: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHandler.TABLE_USER} WHERE ${DatabaseHandler.COLUMN_EMAIL} = ?",
            arrayOf(email)
        )
        var user: User? = null

        if (cursor.moveToFirst()) {
            val fullName =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_FULLNAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_PHONE))
            val address =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_ADDRESS))
            user = User(fullName, email, phone, address)
        }

        cursor.close()
        return user
    }


    fun getUsernameByEmail(email: String): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHandler.TABLE_ACCOUNT} WHERE ${DatabaseHandler.COLUMN_EMAIL} = ?",
            arrayOf(email)
        )
        var username: String? = null
        if (cursor.moveToFirst()) {
            username =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_USERNAME))
        }
        cursor.close()
        return username
    }

    fun getEmailByUsername(username: String): String? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DatabaseHandler.TABLE_ACCOUNT} WHERE ${DatabaseHandler.COLUMN_USERNAME} = ?",
            arrayOf(username)
        )
        var email: String? = null
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.COLUMN_EMAIL))
        }
        cursor.close()
        return email
    }

    fun updatePassword(username: String, newPassword: String): Boolean {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHandler.COLUMN_PASSWORD, newPassword)
        }

        val result = db.update(DatabaseHandler.TABLE_ACCOUNT, contentValues, "${DatabaseHandler.COLUMN_USERNAME} = ?", arrayOf(username))
        db.close()
        return result > 0
    }

    fun getRoomsByUsername(username: String): List<RoomItem> {
        val roomList = mutableListOf<RoomItem>()
        val db = dbHelper.readableDatabase

        val query = """
        SELECT r.${DatabaseHandler.COLUMN_ROOM_NAME}
        FROM ${DatabaseHandler.TABLE_ACCOUNT_ROOM} r
        WHERE r.${DatabaseHandler.COLUMN_USERNAME} = ?
    """

        val cursor = db.rawQuery(query, arrayOf(username))

        if (cursor.moveToFirst()) {
            do {
                val roomName = cursor.getString(0)
                //val numberOfLights = cursor.getInt(1)
                roomList.add(RoomItem(roomName))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return roomList
    }

    fun getLightByNameRoom(name: String): List<LightItem> {
        val lightList = mutableListOf<LightItem>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT r.${DatabaseHandler.COLUMN_NAME_LIGHT}, r.${DatabaseHandler.COLUMN_PIN},r.${DatabaseHandler.COLUMN_ISMARKED},r.${DatabaseHandler.COLUMN_IP},r.${DatabaseHandler.COLUMN_STATUS}
        FROM ${DatabaseHandler.TABLE_LIGHT_BULB} r
        WHERE r.${DatabaseHandler.COLUMN_ROOM_NAME} = ?
    """

        val cursor = db.rawQuery(query, arrayOf(name))

        if (cursor.moveToFirst()) {
            do {
                val nameLight = cursor.getString(0)
                val pin = cursor.getString(1)
                val mark = cursor.getInt(2) == 1
                val ip = cursor.getString(3)
                val status = cursor.getInt(4) == 1
                lightList.add(LightItem(nameLight, pin,mark,ip, status))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lightList
    }

    fun insertLight(name: String, light: LightItem): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHandler.COLUMN_NAME_LIGHT, light.name)
            put(DatabaseHandler.COLUMN_PIN, light.pin)
            put(DatabaseHandler.COLUMN_ISMARKED,light.isMarked)
            put(DatabaseHandler.COLUMN_IP, light.ip)
            put(DatabaseHandler.COLUMN_STATUS, if (light.status) 1 else 0)
            put(DatabaseHandler.COLUMN_ROOM_NAME, name)
        }
        val rs = db.insert(DatabaseHandler.TABLE_LIGHT_BULB, null, values)
        db.close()
        return rs != -1L
    }

    fun deleteRoom(username: String, roomName: String): Boolean {
        val db = dbHelper.writableDatabase

        // Xóa liên kết trong bảng ACCOUNT_ROOM trước
        val linkRows = db.delete(
            DatabaseHandler.TABLE_ACCOUNT_ROOM,
            "${DatabaseHandler.COLUMN_ROOM_NAME} = ? AND ${DatabaseHandler.COLUMN_USERNAME} = ?",
            arrayOf(roomName, username)
        )

        // Nếu liên kết đã bị xóa, tiếp tục xóa bóng đèn và phòng
        val roomRows = if (linkRows > 0) {
            // Xóa bóng đèn trong phòng
            db.delete(
                DatabaseHandler.TABLE_LIGHT_BULB,
                "${DatabaseHandler.COLUMN_ROOM_NAME} = ?",
                arrayOf(roomName)
            )

            // Xóa phòng
            db.delete(
                DatabaseHandler.TABLE_ROOM,
                "${DatabaseHandler.COLUMN_ROOM_NAME} = ?",
                arrayOf(roomName)
            )
        } else 0

        db.close()
        return roomRows > 0
    }

    fun deleteLight(tenPhong: String, tenDen: String): Boolean {
        val db = dbHelper.writableDatabase

        val lightRows = db.delete(
            DatabaseHandler.TABLE_LIGHT_BULB,
            "${DatabaseHandler.COLUMN_ROOM_NAME} = ? AND ${DatabaseHandler.COLUMN_NAME_LIGHT} = ?",
            arrayOf(tenPhong, tenDen)
        )

        db.close()
        return lightRows > 0
    }

    fun countLights(roomName: String): Int {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT COUNT(*) FROM ${DatabaseHandler.TABLE_LIGHT_BULB}
        WHERE ${DatabaseHandler.COLUMN_ROOM_NAME} = ?
    """
        val cursor = db.rawQuery(query, arrayOf(roomName))
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }
    fun updateLight(lightName: String, pin: String?, ip: String?): Boolean {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues()

        // Chỉ thêm vào nếu giá trị không null
        pin?.let { contentValues.put(DatabaseHandler.COLUMN_PIN, it) }
        ip?.let { contentValues.put(DatabaseHandler.COLUMN_IP, it) }

        val rowsAffected = db.update(
            DatabaseHandler.TABLE_LIGHT_BULB,
            contentValues,
            "${DatabaseHandler.COLUMN_NAME_LIGHT} = ?",
            arrayOf(lightName)
        )

        db.close()
        return rowsAffected > 0
    }

    fun updateState(lightName: String, state: Boolean): Boolean {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHandler.COLUMN_STATUS, if (state) 1 else 0) // true = 1, false = 0
        }

        val rowsAffected = db.update(
            DatabaseHandler.TABLE_LIGHT_BULB,
            contentValues,
            "${DatabaseHandler.COLUMN_NAME_LIGHT} = ?",
            arrayOf(lightName)
        )

        db.close()
        return rowsAffected > 0
    }

    fun addSetTimer(username: String, nameRoom: String, setTime: Date, activeTime: Date, state: Boolean): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHandler.COLUMN_USERNAME, username)
            put(DatabaseHandler.COLUMN_NAME_LIGHT, nameRoom)
            put(DatabaseHandler.COLUMN_SET_TIME, timeFormat.format(setTime))       // Format chuẩn ISO 8601
            put(DatabaseHandler.COLUMN_ACTIVE_TIME, timeFormat.format(activeTime))
            put(DatabaseHandler.COLUMN_TOGGLE_STATUS, if (state) 1 else 0)          // SQLite không có kiểu BOOLEAN thực, nên dùng 0/1
        }

        val result = db.insert(DatabaseHandler.TABLE_SET_TIMER, null, values)
        db.close()
        return result != -1L
    }

    fun getLightByNameLight(nameLight: String): LightItem? {
        if (nameLight.isBlank()) return null

        val db = dbHelper.readableDatabase
        val light: LightItem?

        db.use { database ->
            val cursor = database.query(
                DatabaseHandler.TABLE_LIGHT_BULB,
                arrayOf(
                    DatabaseHandler.COLUMN_NAME_LIGHT,
                    DatabaseHandler.COLUMN_PIN,
                    DatabaseHandler.COLUMN_ISMARKED,
                    DatabaseHandler.COLUMN_IP,
                    DatabaseHandler.COLUMN_STATUS
                ),
                "${DatabaseHandler.COLUMN_NAME_LIGHT} = ?",
                arrayOf(nameLight),
                null,
                null,
                null
            )

            light = cursor.use {
                if (it.moveToFirst()) {
                    val name = it.getString(it.getColumnIndexOrThrow(DatabaseHandler.COLUMN_NAME_LIGHT))
                    val pin = it.getString(it.getColumnIndexOrThrow(DatabaseHandler.COLUMN_PIN))
                    val mark = it.getInt(it.getColumnIndexOrThrow(DatabaseHandler.COLUMN_ISMARKED))
                    val ip = it.getString(it.getColumnIndexOrThrow(DatabaseHandler.COLUMN_IP))
                    val status = it.getInt(it.getColumnIndexOrThrow(DatabaseHandler.COLUMN_STATUS))
                    LightItem(name, pin, mark == 1, ip, status == 1)
                } else {
                    null
                }
            }
        }

        return light
    }

    fun updateMark(lightName: String, isMarked: Boolean): Boolean {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseHandler.COLUMN_ISMARKED, if (isMarked) 1 else 0) // true = 1, false = 0
        }

        val rowsAffected = db.update(
            DatabaseHandler.TABLE_LIGHT_BULB,
            contentValues,
            "${DatabaseHandler.COLUMN_NAME_LIGHT} = ?",
            arrayOf(lightName)
        )

        db.close()
        return rowsAffected > 0
    }





}


