package com.example.todo.data

import android.content.Context
import android.content.SharedPreferences
import com.example.todo.TodoApplication
import com.example.todo.bean.User

object UserManager {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_ACCOUNT = "key_account"
    private const val KEY_NAME = "key_name"
    private const val KEY_PASSWORD = "key_password"
    private lateinit var dbHelper: DbHelper

    private var currentUser: User? = null
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        dbHelper = DbHelper(context)
        loadUser()
    }

    fun getCurrentUser(): User? {
        return currentUser
    }

    fun setCurrentUser(user: User) {
        currentUser = user
        saveUser()
    }

    fun logout() {
        currentUser = null
        sharedPreferences.edit().clear().apply()
    }

    private fun loadUser() {
        val account = sharedPreferences.getString(KEY_ACCOUNT, null)
        val name = sharedPreferences.getString(KEY_NAME, null)
        val password = sharedPreferences.getString(KEY_PASSWORD, null)
        if (account != null && name != null && password != null) {
            currentUser = User(account, name, password)
        }
    }

    private fun saveUser() {
        currentUser?.let {
            sharedPreferences.edit().apply {
                putString(KEY_ACCOUNT, it.account)
                putString(KEY_NAME, it.name)
                putString(KEY_PASSWORD, it.password)
                apply()
            }
        }
    }

    // 添加一个方法来获取用户的原始密码
    fun getOriginalPassword(): String? {
        return currentUser?.password
    }

    fun setUser(user: User) {
        currentUser = user
        updateUserInDatabse(user)
    }

    private fun updateUserInDatabse(user: User) {
        val password = user.password
        TodoApplication.getUsersDataSource().updatePassword(user.account,password)
    }

}