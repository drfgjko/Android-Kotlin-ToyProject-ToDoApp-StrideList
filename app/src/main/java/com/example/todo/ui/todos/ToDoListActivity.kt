package com.example.todo.ui.todos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.adapter.TodoAdapter
import com.example.todo.bean.TodoItem
import com.example.todo.data.DbHelper
import com.example.todo.ui.mine.MineActivity
import com.example.todo.ui.notes.NotesListActivity
import com.example.todo.ui.tomato.TomatoClockActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ToDoListActivity : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    private lateinit var todoAdapter: TodoAdapter

    companion object{
        private const val REQUEST_CODE_ADD_TODO = 1
        private const val REQUEST_Notes = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)

//        导航栏部分
        val todoBtn=findViewById<LinearLayout>(R.id.todoBtn)
        val tomatoBtn = findViewById<LinearLayout>(R.id.tomatoBtn)
        val notesBtn = findViewById<LinearLayout>(R.id.notesBtn)
        val mineBtn = findViewById<LinearLayout>(R.id.mineBtn)

        todoBtn.setOnClickListener{
            val intent = Intent(this, ToDoListActivity::class.java)
            startActivity(intent)
        }
        tomatoBtn.setOnClickListener{
            val intent = Intent(this, TomatoClockActivity::class.java)
            startActivity(intent)
        }
        mineBtn.setOnClickListener{
            val intent = Intent(this, MineActivity::class.java)
            startActivity(intent)
        }
        notesBtn.setOnClickListener{
            val intent = Intent(this, NotesListActivity::class.java)
            startActivity(intent)
        }

//        新增代办
        val addTodoButton: Button = findViewById(R.id.addButton)
        addTodoButton.setOnClickListener {
            val intent = Intent(this, AddToDoActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_TODO)
        }

        // 初始化数据库帮助器并加载数据
        dbHelper = DbHelper(this)
        loadTodoItems()
    }


    private fun loadTodoItems() {
        val todoRecyclerView: RecyclerView = findViewById(R.id.todoRecyclerView)
        todoRecyclerView.layoutManager = LinearLayoutManager(this)
        val currentUser = TodoApplication.getCurrentUser()
        if(currentUser!=null){
            val todoItems = TodoApplication.getToDosDataSource().getTodoItemsByAccount(currentUser.account)
            todoAdapter = TodoAdapter(todoItems.toMutableList(), this) // 传递this作为回调实例
            todoRecyclerView.adapter = todoAdapter
        }else{
            Toast.makeText(this,"获取信息失败", Toast.LENGTH_LONG).show()
        }
    }

    fun onEditItem(item: TodoItem) {
        // 处理编辑项->AddToDoActivity
        val intent = Intent(this, AddToDoActivity::class.java)
        intent.putExtra("editMode", true)
        intent.putExtra("todoId", item.id)
        intent.putExtra("content", item.content)
        intent.putExtra("deadline", item.deadline)
        startActivityForResult(intent, REQUEST_CODE_ADD_TODO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TODO && resultCode == RESULT_OK) {
            // 如果添加成功，刷新列表
            loadTodoItems()
        }
        if (requestCode == REQUEST_Notes ){
            loadTodoItems()
        }
    }
}