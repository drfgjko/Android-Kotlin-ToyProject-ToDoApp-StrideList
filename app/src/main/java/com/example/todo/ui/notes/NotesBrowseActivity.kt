package com.example.todo.ui.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.bean.Note
import kotlinx.android.synthetic.main.activity_notes_browse.*


class NotesBrowseActivity: AppCompatActivity(){
    companion object{
//        private val REQUEST_CODE_ADD_TODO = 1
//        private const val REQUEST_Todo = 2
//        private val REQUEST_Notes = 3
//        private val REQUEST_Tomato = 4
//        private val REQUEST_Mine = 5
//        private const val REQUEST_BROWSE = 6
        private const val REQUEST_EDIT = 7
//        private const val REQUEST_BACK = 8
        //用于在别的页面(NotesListActivity)时，点击Adapter某个条目后调用onItemClick方法后实现跳转

        // 前一步：val intent = NotesBrowseActivity.buildIntent(context, note.id)
        //                startActivityForResult(intent, REQUEST_BROWSE)
        fun buildIntent(context: Context, id: Long): Intent {
            return Intent(context, NotesBrowseActivity::class.java).putExtra("noteId", id)
        }

    }

    private lateinit var toolbar: Toolbar
    private lateinit var scrollView: NestedScrollView
    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var lbsTextView: TextView

    // 笔记主键,为 0 则为新笔记
    private var noteId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_browse)

        //获取控件
        initView()
        //导航栏
        setView()
        //获取noteId
        noteId = intent.getLongExtra("noteId", 0)
        //获取Note对象
        val note = TodoApplication.getNotesDataSource().selectOne(noteId)
        setNoteView(note)


        action_update.setOnClickListener{
            val intent = NotesEditActivity.buildIntent(this, noteId)
            startActivityForResult(intent, REQUEST_EDIT)
        }

        action_delete.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定要删除吗?")
                .setPositiveButton("确定") { _, _ ->
                    TodoApplication.getNotesDataSource().deleteNote(noteId)
                    setResult(RESULT_OK)
                    finish()
                }
                .setNegativeButton("取消", null)
                .create()
                .show()
        }
    }

    private fun setView() {
        // 将标题栏关联到页面
        setSupportActionBar(toolbar)
        // 显示返回键
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_icon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    //toolbar选项逻辑
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//            返回
            android.R.id.home -> {
                val intent = NotesListActivity.buildIntent(this, noteId)
                startActivityForResult(intent, REQUEST_EDIT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        scrollView = findViewById(R.id.scrollView)
        titleTextView = findViewById(R.id.title_textView)
        contentTextView = findViewById(R.id.content_textView)
        lbsTextView = findViewById(R.id.lbs_textView)
        errorTextView = findViewById(R.id.error_textView)
    }

    //填充数据
    private fun setNoteView(note:Note?){
        if(note == null){
            errorTextView.text = "未找到该笔记"
            errorTextView.visibility = View.VISIBLE
            scrollView.visibility = View.GONE
        }
        else{
            errorTextView.visibility = View.GONE
            scrollView.visibility = View.VISIBLE
            titleTextView.text = note.title
            lbsTextView.text = note.lbs
            val content = "\t\t\t\t" + note.content.replace("\n\t\t\t\t","")
            contentTextView.text = content
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_EDIT ->{
                if(resultCode == RESULT_OK){
                    setResult(RESULT_OK)
                    val note = TodoApplication.getNotesDataSource().selectOne(noteId)
                    setNoteView(note)
                }
            }
        }
    }

}