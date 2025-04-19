package com.example.todo.data

import android.content.ContentValues
import android.database.Cursor
import com.example.todo.TodoApplication
import com.example.todo.bean.Note
import java.util.*

//对Note数据操作
class NotesDataSource(private val dbHelper: DbHelper) {
    fun insertNote(note:Note){
        val values = ContentValues()
        values.put(TodoApplication.NOTE_COLUMN_LBS,note.lbs)
        values.put(TodoApplication.NOTE_COLUMN_TITLE,note.title)
        values.put(TodoApplication.NOTE_COLUMN_CONTENT,note.content)
        values.put(TodoApplication.NOTE_COLUMN_CREATETIME,System.currentTimeMillis())
        values.put(TodoApplication.NOTE_COLUMN_UPDATETIME,System.currentTimeMillis())
        val currentUser = TodoApplication.getCurrentUser()
        if(currentUser!=null){
            values.put(TodoApplication.NOTE_COLUMN_ACCOUNT,currentUser.account)
        }
        dbHelper.writableDatabase.run{
            insert(TodoApplication.NOTE_TABLE_NAME,null,values)
            close()
        }
    }
    //删除
    fun deleteNote(id:Long){
        dbHelper.writableDatabase.run{
            delete(TodoApplication.NOTE_TABLE_NAME,"${TodoApplication.NOTE_COLUMN_ID}=?", arrayOf(id.toString()))
            close()
        }
    }
    //修改 修改的时候更新“修改时间”
    fun updateNote(id:Long,note: Note){
        val values = ContentValues()
        values.put(TodoApplication.NOTE_COLUMN_LBS,note.lbs)
        values.put(TodoApplication.NOTE_COLUMN_TITLE,note.title)
        values.put(TodoApplication.NOTE_COLUMN_CONTENT,note.content)
        values.put(TodoApplication.NOTE_COLUMN_UPDATETIME,System.currentTimeMillis())
        dbHelper.writableDatabase.run{
            update(TodoApplication.NOTE_TABLE_NAME,values,"${TodoApplication.NOTE_COLUMN_ID}=?", arrayOf(id.toString()))
            close()
        }
    }

    fun selectOne(id:Long):Note?{
        val columns = arrayOf("id","lbs","title","content","createTime","updateTime")
        dbHelper.readableDatabase.query("note",columns,"id=?", arrayOf(id.toString()),null,null,null)
            .use {
                cursor -> return if(cursor.moveToNext()) createNote(cursor) else null
            }
    }
    //根据用户account -> 获取所有数据
    fun selectListByAccount(account: String): List<Note> {
        val columns = arrayOf("id", "lbs", "title", "content", "createTime", "updateTime", "account")
        val selection = "account = ?"
        val selectionArgs = arrayOf(account)
        val list = mutableListOf<Note>()

        dbHelper.readableDatabase.query("note", columns, selection, selectionArgs, null, null, null)
            .use { cursor ->
                while (cursor.moveToNext()) {
                    list.add(createNote(cursor))
                }
            }
        return list
    }

    //将查询结果返回Note对象，供上层使用
    fun createNote(cursor: Cursor): Note {

        //不这样写会报错
        val id_index = cursor.getColumnIndex("id")
        val lbs_index = cursor.getColumnIndex("lbs")
        val title_index = cursor.getColumnIndex("title")
        val content_index = cursor.getColumnIndex("content")
        val createTime_index = cursor.getColumnIndex("createTime")
        val updateTime_index = cursor.getColumnIndex("updateTime")

        val id = cursor.getLong(id_index)
        val lbs = cursor.getString(lbs_index)
        val title = cursor.getString(title_index)
        val content =cursor.getString(content_index)
        val createTime = cursor.getLong(createTime_index)
        val updateTime = cursor.getLong(updateTime_index)

        return Note().apply {
            this.id = id
            this.lbs = lbs
            this.title = title
            this.content = content
            this.createTime = Date(createTime)
            this.updateTime = Date(updateTime)
        }
    }

}