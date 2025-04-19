package com.example.todo.ui.todos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.bean.TodoItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddToDoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_do)
        val contentEditText: EditText = findViewById(R.id.contentEditText)
        val deadline: EditText = findViewById(R.id.deadline)
        val saveButton: FloatingActionButton = findViewById(R.id.saveButton)

        // 获取传递过来的参数
        val isEditMode = intent.getBooleanExtra("editMode", false)
        val todoId = intent.getLongExtra("todoId", -1) // 假设ID是Long类型，并设置默认值为-1表示没有ID
        val content2 = intent.getStringExtra("content") ?: ""
        val deadline2 = intent.getStringExtra("deadline") ?: ""

        // 如果在编辑模式，则设置文本视图和截止日期的值
        if (isEditMode) {
            contentEditText.setText(content2)
            deadline.setText(deadline2)
        }


        saveButton.setOnClickListener {
            val content = contentEditText.text.toString()
            val dl = deadline.text.toString()
            // 非空验证
            if (content.isBlank() || dl.isBlank()) {
                Toast.makeText(this, "不能为空！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isEditMode) {
                if (todoId != -1L) { // 检查是否有效ID
                    TodoApplication.getToDosDataSource().updateTodoItem(todoId, content, dl)
                    setResult(RESULT_OK, Intent().putExtra("update_success", true))
                    Log.d("isEditMode", "TodoItem content: $content, deadline: $dl")
                } else {
                    // 处理没有有效ID的情况（可能是错误的情况）
                    Toast.makeText(this, "无效ID", Toast.LENGTH_SHORT).show()
                }
            }else{
                val newId = TodoApplication.getToDosDataSource().insertTodoItem(TodoItem(0, content, dl)) // 传递0作为ID占位符，因为数据库将自动生成ID
                // 输出日志以检查TodoItem的内容
                Log.d("AddToDoActivity", "TodoItem content: $content, deadline: $dl")
                // 检查是否成功获得了ID
                if (newId > 0) {
                    Log.d("AddToDoActivity", "New TodoItem inserted with ID: $newId")// 插入成功，newId是新行的ID
                } else {
                    Log.e("AddToDoActivity", "Failed to insert new TodoItem")// 插入失败
                }
                setResult(RESULT_OK, Intent().putExtra("insert_success", true))
           }
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}