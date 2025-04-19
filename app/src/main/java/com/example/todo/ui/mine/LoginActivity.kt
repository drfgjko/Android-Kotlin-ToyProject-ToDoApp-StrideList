package com.example.todo.ui.mine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.data.UserManager
import com.example.todo.ui.todos.ToDoListActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var account:String
    private lateinit var password:String
    var login:Boolean = false

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        UserManager.initialize(this)


        login_button.setOnClickListener {
            account=login_account.text.toString()
            password=login_password.text.toString()
            login = TodoApplication.getUsersDataSource().login(account,password)
            if (login){
                val user = TodoApplication.getUsersDataSource().getUser(account)
                if(user != null){
                    UserManager.setCurrentUser(user)

                    //全局管理
                    TodoApplication.setCurrentUser(user)

                    Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show()

//                    主功能页为待办
                    intent = Intent(this, ToDoListActivity::class.java)
                    startActivity(intent)

                }
            }else{
                Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show()
            }
        }

        signupRedirectText.setOnClickListener {
            intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}