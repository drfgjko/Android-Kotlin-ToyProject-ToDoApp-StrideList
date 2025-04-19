package com.example.todo.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class DbHelper (context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME,null,
    DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "ToDo.db"
        private const val DATABASE_VERSION = 1
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
    }

    private val dropNote = "drop table if exists $NOTE_TABLE_NAME"
    private val dropToDo = "drop table if exists $TODO_TABLE_NAME"
    private val dropUser = "drop table if exists $USER_TABLE_NAME"

    private val createNote = "create table $NOTE_TABLE_NAME (" +
            "$NOTE_COLUMN_ID integer primary key autoincrement not null," +
            "$NOTE_COLUMN_LBS text," +
            "$NOTE_COLUMN_TITLE text not null," +
            "$NOTE_COLUMN_CONTENT text not null," +
            "$NOTE_COLUMN_CREATETIME integer," +
            "$NOTE_COLUMN_UPDATETIME integer," +
            "$NOTE_COLUMN_ACCOUNT text not null," +
            "foreign key($NOTE_COLUMN_ACCOUNT) references $USER_TABLE_NAME($USER_COLUMN_ACCOUNT))"

    private val createUser = "create table $USER_TABLE_NAME(" +
            "$USER_COLUMN_ACCOUNT text primary key," +
            "$USER_COLUMN_NAME text," +
            "$USER_COLUMN_PASSWORD text)"

    private val createToDo = """
CREATE TABLE $TODO_TABLE_NAME (
    $TODO_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    $TODO_COLUMN_CONTENT TEXT,
    $TODO_COLUMN_DEADLINE TEXT,
    $TODO_COLUMN_ACCOUNT TEXT NOT NULL,
    FOREIGN KEY ($TODO_COLUMN_ACCOUNT) REFERENCES $USER_TABLE_NAME($USER_COLUMN_ACCOUNT)
)
""".trimIndent()

    private val addNote = "INSERT INTO $NOTE_TABLE_NAME ($NOTE_COLUMN_LBS, $NOTE_COLUMN_TITLE, $NOTE_COLUMN_CONTENT, $NOTE_COLUMN_CREATETIME, $NOTE_COLUMN_UPDATETIME, $NOTE_COLUMN_ACCOUNT) VALUES" +
            "('note1_lbs', 'Note 1 Title', 'Note 1 Content', ?, ?, '123')," +
            "('note2_lbs', 'Note 2 Title', 'Note 2 Content', ?, ?, '123')," +
            "('note3_lbs', 'Note 3 Title', 'Note 3 Content', ?, ?, '123')," +
            "('note4_lbs', 'Note 4 Title', 'Note 4 Content', ?, ?, '123')," +
            "('note5_lbs', 'Note 5 Title', 'Note 5 Content', ?, ?, '123')," +
            "('note6_lbs', 'Note 6 Title', 'Note 6 Content', ?, ?, '123')," +
            "('note7_lbs', 'Note 7 Title', 'Note 7 Content', ?, ?, '123')," +
            "('note8_lbs', 'Note 8 Title', 'Note 8 Content', ?, ?, '1234')," +
            "('note9_lbs', 'Note 9 Title', 'Note 9 Content', ?, ?, '1234')"

    private val addToDo = """
INSERT INTO $TODO_TABLE_NAME ($TODO_COLUMN_CONTENT, $TODO_COLUMN_DEADLINE, $TODO_COLUMN_ACCOUNT) VALUES
('Buy groceries', '2023-06-15', '123'),
('Finish report', '2023-06-20', '1234')
"""

    private val addUser = "INSERT INTO $USER_TABLE_NAME ($USER_COLUMN_ACCOUNT, $USER_COLUMN_NAME, $USER_COLUMN_PASSWORD) VALUES" +
            "('123', 'User 1', '123')," +
            "('1234', 'User 2', '123')"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createNote)
        db.execSQL(createToDo)
        db.execSQL(createUser)

//        测试数据
        val currentDate = Date()
        val currentTime = currentDate.time

        db.execSQL(addNote, arrayOf(
            currentTime, currentTime, currentTime, currentTime,
            currentTime, currentTime, currentTime, currentTime,
            currentTime, currentTime, currentTime, currentTime
        ))
        db.execSQL(addUser)
        db.execSQL(addToDo)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(dropNote)
        db.execSQL(dropToDo)
        db.execSQL(dropUser)

        db.execSQL(createNote)
        db.execSQL(createToDo)
        db.execSQL(createUser)
    }



}