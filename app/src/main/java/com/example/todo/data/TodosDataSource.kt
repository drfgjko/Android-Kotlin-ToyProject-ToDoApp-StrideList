package com.example.todo.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import com.example.todo.TodoApplication
import com.example.todo.bean.TodoItem

//   todo表的操作逻辑
class TodosDataSource(private val dbHelper: DbHelper) {
    //添加待办事务//需要增加一个id生成逻辑
    fun insertTodoItem(todoItem: TodoItem): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(TodoApplication.TODO_COLUMN_CONTENT, todoItem.content)
        values.put(TodoApplication.TODO_COLUMN_DEADLINE, todoItem.deadline)
        val currentUser = TodoApplication.getCurrentUser()
        if(currentUser!=null){
            values.put(TodoApplication.TODO_COLUMN_ACCOUNT,currentUser.account)
        }
        val rowId = db.insert(TodoApplication.TODO_TABLE_NAME, null, values)
        db.close()
        return rowId
    }

    //读取待办事务 -> 根据user_account获取
    @SuppressLint("Range")
    fun getTodoItemsByAccount(account: String): List<TodoItem> {
        val todoItems = mutableListOf<TodoItem>()
        val db = dbHelper.readableDatabase
        val selection = "${TodoApplication.TODO_COLUMN_ACCOUNT} = ?"
        val selectionArgs = arrayOf(account)
        val cursor: Cursor = db.query(
            TodoApplication.TODO_TABLE_NAME,
            arrayOf(TodoApplication.TODO_COLUMN_ID,TodoApplication.TODO_COLUMN_CONTENT, TodoApplication.TODO_COLUMN_DEADLINE),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(TodoApplication.TODO_COLUMN_ID))
                val content = cursor.getString(cursor.getColumnIndex(TodoApplication.TODO_COLUMN_CONTENT))
                val deadline = cursor.getString(cursor.getColumnIndex(TodoApplication.TODO_COLUMN_DEADLINE))
                todoItems.add(TodoItem(id, content, deadline))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return todoItems.toList()
    }

    // 更新待办事务
    fun updateTodoItem(id: Long?, content:String,deadline:String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(TodoApplication.TODO_COLUMN_CONTENT, content)
        values.put(TodoApplication.TODO_COLUMN_DEADLINE, deadline)

        // 根据ID更新记录
        db.update(TodoApplication.TODO_TABLE_NAME, values, "${TodoApplication.TODO_COLUMN_ID} = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteTodoItem(id: Long?) {
        val db = dbHelper.writableDatabase
        // 根据ID删除记录
        db.delete(TodoApplication.TODO_TABLE_NAME, "${TodoApplication.TODO_COLUMN_ID} = ?", arrayOf(id.toString()))
        db.close()
    }
}