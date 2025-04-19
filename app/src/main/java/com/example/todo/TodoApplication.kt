package com.example.todo

import android.app.Application
import android.content.Context
import com.example.todo.bean.User
import com.example.todo.data.DbHelper
import com.example.todo.data.NotesDataSource
import com.example.todo.data.TodosDataSource
import com.example.todo.data.UsersDataSource

class TodoApplication : Application() {
    companion object {
        //        用户表
        const val USER_TABLE_NAME = "user"
        const val USER_COLUMN_NAME = "name"
        const val USER_COLUMN_PASSWORD= "password"
        const val USER_COLUMN_ACCOUNT = "account"
        //        笔记表
        const val NOTE_TABLE_NAME = "note"
        const val NOTE_COLUMN_ID = "id"
        const val NOTE_COLUMN_LBS = "lbs"
        const val NOTE_COLUMN_TITLE = "title"
        const val NOTE_COLUMN_CONTENT = "content"

        const val NOTE_COLUMN_CREATETIME = "createTime"
        const val NOTE_COLUMN_UPDATETIME = "updateTime"
        const val NOTE_COLUMN_ACCOUNT= "account"

        //        待办表
        const val TODO_TABLE_NAME = "todo"
        const val TODO_COLUMN_ID = "id" // 新增一个ID列
        const val TODO_COLUMN_CONTENT = "content"
        const val TODO_COLUMN_DEADLINE = "deadline"
        const val TODO_COLUMN_ACCOUNT = "account"

        private lateinit var appContext: Context

        private var notesDataSource: NotesDataSource? = null
        private var todoDataSource:TodosDataSource? = null
        private var usersDataSource:UsersDataSource? = null

        private var dbHelper:DbHelper? = null
//        可空 ->初始化null
        private var currentUser: User? = null

        fun initialize(context: Context) {
            appContext = context.applicationContext
        }


//        操作逻辑
        fun getNotesDataSource(): NotesDataSource {
            if (notesDataSource == null) {
                notesDataSource = NotesDataSource(dbHelper!!)
            }
            return notesDataSource!!
        }
        fun getToDosDataSource(): TodosDataSource {
            if (todoDataSource == null) {
                todoDataSource = TodosDataSource(dbHelper!!)
            }
            return todoDataSource!!
        }
        fun getUsersDataSource(): UsersDataSource {
            if (usersDataSource == null) {
                usersDataSource = UsersDataSource(dbHelper!!)
            }
            return usersDataSource!!
        }
        fun setCurrentUser(user: User?) {
            currentUser = user
        }
        fun getCurrentUser(): User? {
            return currentUser
        }
    }

    override fun onCreate() {
        super.onCreate()
        initialize(this)
        dbHelper = DbHelper(appContext)
    }
}
