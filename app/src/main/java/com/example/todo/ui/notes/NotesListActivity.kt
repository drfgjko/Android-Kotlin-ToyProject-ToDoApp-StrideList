package com.example.todo.ui.notes

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.todo.*
import com.example.todo.adapter.NotesAdapter
import com.example.todo.bean.Note
import com.example.todo.ui.mine.MineActivity
import com.example.todo.ui.todos.ToDoListActivity
import com.example.todo.ui.tomato.TomatoClockActivity
import com.example.todo.utils.SizeUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesListActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_BROWSE = 6
        private const val REQUEST_EDIT = 7
        private const val REQUEST_BACK = 8

        // 相关(笔记功能内的)页面联动时要使用
        fun buildIntent(context: Context, id: Long): Intent {
            return Intent(context, NotesListActivity::class.java).putExtra("noteId", id)
        }
    }

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var list: List<Note>
    private lateinit var createBtn: FloatingActionButton
    private val context: NotesListActivity = this

//    导航栏相关
    private lateinit var todoBtn : LinearLayout
    private lateinit var tomatoBtn :LinearLayout
    private lateinit var notesBtn :LinearLayout
    private lateinit var mineBtn :LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_list)
        //绑定控件
        initView()
        //获取数据
        initData()
        //设置列表的适配器
        initAdapter()
        //当点击某个条目式会触发监听器的OnItemClick()方法
        notesAdapter.setOnItemClickListener(object : NotesAdapter.OnItemClickListener{
            override fun onItemClick(note: Note, position: Int) {
                val intent = NotesBrowseActivity.buildIntent(context, note.id)
                startActivityForResult(intent, REQUEST_BROWSE)
            }
        })

        createBtn.setOnClickListener{
            val intent = NotesEditActivity.buildIntent(this, 0)
            startActivity(intent)
        }

        //导航栏相关
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
        notesAdapter.setNewData(list)
    }


    private fun initAdapter() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        notesAdapter = NotesAdapter()

        //设置列表的间隔距离 -> 使用他人的工具类
        recyclerView.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val count = parent.adapter!!.itemCount
                val index = parent.getChildAdapterPosition(view)
                if (index < count - 1) {
                    outRect[0, 0, 0] = SizeUtils.dp2px(30F)
                }
            }
        })
        recyclerView.adapter = notesAdapter
        notesAdapter.setNewData(list)
    }

//获取数据
    private fun initData() {
        val currentUser = TodoApplication.getCurrentUser()
        if(currentUser!=null){
            list= TodoApplication.getNotesDataSource().selectListByAccount(currentUser.account)
        }
        else{
            Toast.makeText(this,"获取信息失败",Toast.LENGTH_LONG)
        }
    }

    private fun initView(){

        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        createBtn = findViewById(R.id.createBtn)


//      底部导航栏相关
        todoBtn=findViewById(R.id.todoBtn)
        tomatoBtn = findViewById(R.id.tomatoBtn)
        notesBtn = findViewById(R.id.notesBtn)
        mineBtn = findViewById(R.id.mineBtn)

//        显示返回
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_icon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    //回到该页面要刷新数据
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val currentUser = TodoApplication.getCurrentUser()
        if(currentUser!=null){
            list= TodoApplication.getNotesDataSource().selectListByAccount(currentUser.account)
        }
        when(requestCode){
            REQUEST_BROWSE -> {
                    notesAdapter.setNewData(list)
            }
            REQUEST_BACK -> {
                notesAdapter.setNewData(list)
            }
            REQUEST_EDIT -> {
                notesAdapter.setNewData(list)
            }
        }
    }


// 菜单点击逻辑
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
//                返回
                val intent = Intent(this, ToDoListActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}