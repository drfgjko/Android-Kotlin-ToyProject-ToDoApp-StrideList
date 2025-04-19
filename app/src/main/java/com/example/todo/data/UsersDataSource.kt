package com.example.todo.data

import android.content.ContentValues
import com.example.todo.TodoApplication
import com.example.todo.bean.User

class UsersDataSource(private val dbHelper: DbHelper) {

    //user表的操作逻辑
    fun register(u: User): Long {
        val i: Long = -1
        //判空
        if (u.account.isEmpty() || u.name.isEmpty() || u.password.isEmpty()) {
            return i
        }
        val db = dbHelper.writableDatabase
        return if (isUserExists(u.account)) {
            db.close()
            i
        } else {
            val cv = ContentValues()
            cv.put(TodoApplication.USER_COLUMN_ACCOUNT, u.account)
            cv.put(TodoApplication.USER_COLUMN_NAME, u.name)
            cv.put(TodoApplication.USER_COLUMN_PASSWORD, u.password)
            val users = db.insert(TodoApplication.USER_TABLE_NAME, null, cv)
            db.close()
            users
        }
    }

    fun login(account: String, password: String): Boolean {
        if (account.isEmpty() || password.isEmpty()) {
            return false
        }
        val db1 = dbHelper.writableDatabase
        val result: Boolean
        val users = db1.query(TodoApplication.USER_TABLE_NAME, null, "${TodoApplication.USER_COLUMN_ACCOUNT} = ?", arrayOf(account), null, null, null)
        if (users != null) {
            while (users.moveToNext()) {
                val password1 = users.getString(2)
                result = password1 == password
                return result
            }
        }
        users?.close()
        db1.close()
        return false
    }

    private fun isUserExists(account: String): Boolean {
        val db2 = dbHelper.writableDatabase
        val cursor = db2.query(TodoApplication.USER_TABLE_NAME, null, "${TodoApplication.USER_COLUMN_ACCOUNT} = ?", arrayOf(account), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getUser(account: String): User? {
        val db = dbHelper.writableDatabase
        val cursor = db.query(
            TodoApplication.USER_TABLE_NAME,
            arrayOf(TodoApplication.USER_COLUMN_ACCOUNT, TodoApplication.USER_COLUMN_NAME, TodoApplication.USER_COLUMN_PASSWORD),
            "${TodoApplication.USER_COLUMN_ACCOUNT}=?",
            arrayOf(account),
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(TodoApplication.USER_COLUMN_NAME))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(TodoApplication.USER_COLUMN_PASSWORD))
            cursor.close()
            return User(account, name, password)
        }
        cursor?.close()
        return null
    }

    fun updatePassword(account: String, newPassword: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(TodoApplication.USER_COLUMN_PASSWORD, newPassword)
        db.update(TodoApplication.USER_TABLE_NAME, values, "${TodoApplication.USER_COLUMN_ACCOUNT}=?", arrayOf(account))
        db.close()
    }
}