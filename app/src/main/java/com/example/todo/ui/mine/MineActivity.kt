package com.example.todo.ui.mine
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.data.UserManager
import com.example.todo.ui.notes.NotesListActivity
import com.example.todo.ui.todos.ToDoListActivity
import com.example.todo.ui.tomato.TomatoClockActivity

class MineActivity : AppCompatActivity() {
    private var mineFragment: MineFragment? = null

    companion object{
       fun actionStart(context: Context){
           val intent = Intent(context, MineActivity::class.java)
           context.startActivity(intent)
       }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mine)

//        MineActivity= Fragment+底部导航栏
        val todoBtn=findViewById<LinearLayout>(R.id.todoBtn)
        val tomatoBtn = findViewById<LinearLayout>(R.id.tomatoBtn)
        val notesBtn = findViewById<LinearLayout>(R.id.notesBtn)
        val mineBtn = findViewById<LinearLayout>(R.id.mineBtn)

        tomatoBtn.setOnClickListener{
            val currentUser = TodoApplication.getCurrentUser()
            if (currentUser != null) {
                val intent = Intent(this, TomatoClockActivity::class.java)
                startActivity(intent)
            } else {
                // 直接不允许跳转，保证在使用其他页面时TodoApplication的currentUser一定非空
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            }
        }
        mineBtn.setOnClickListener{
            val currentUser = TodoApplication.getCurrentUser()
            if (currentUser != null) {
                val intent = Intent(this, MineActivity::class.java)
                startActivity(intent)
            } else {
                // 直接不允许跳转，保证在使用其他页面时TodoApplication的currentUser一定非空
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            }
        }

        todoBtn.setOnClickListener{
            val currentUser = TodoApplication.getCurrentUser()
            if (currentUser != null) {
                val intent = Intent(this, ToDoListActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            }
        }

        notesBtn.setOnClickListener {
            val currentUser = TodoApplication.getCurrentUser()
            if (currentUser != null) {
                val intent = Intent(this, NotesListActivity::class.java)
                startActivity(intent)
            } else {
                // 直接不允许跳转，保证在使用其他页面时TodoApplication的currentUser一定非空
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            }
        }

        //初始化UserManager
        UserManager.initialize(this)

        // 获取 MineFragment 实例
        mineFragment = supportFragmentManager.findFragmentById(R.id.mineFrag) as? MineFragment

        //更新头像
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val currentUser = TodoApplication.getCurrentUser()
        //实际上一定不为null
        if(currentUser != null){
            val account = currentUser.account
            val avatarUriString = sharedPrefs.getString("avatar_uri_$account", null)
            if (avatarUriString != null) {
                val avatarUri = Uri.parse(avatarUriString)
                updateAvatar(avatarUri)
            } else {
                val intentAvatarUriString = intent.getStringExtra("avatarUri")
                if (intentAvatarUriString != null) {
                    val avatarUri = Uri.parse(intentAvatarUriString)
                    updateAvatar(avatarUri)
                }
            }
        }
        mineFragment?.setChangePassClickListener {
            // 点击事件：启动 ChangePassActivity
            try {
                val intent = Intent(this, ChangePassActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.d("MineActivity", "跳转失败")
            }
        }
    }

    private fun updateAvatar(imageUri: Uri) {
        val mineFragment = supportFragmentManager.findFragmentById(R.id.mineFrag) as? MineFragment
        mineFragment?.updateUserImage(imageUri)
    }

}