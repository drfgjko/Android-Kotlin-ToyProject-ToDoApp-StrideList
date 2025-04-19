package com.example.todo.ui.mine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.bean.User
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    private lateinit var account:String
    private lateinit var name:String
    private lateinit var password:String
    private lateinit var conpassword:String
    private lateinit var user: User
    private var l:Long = -1
    private var num:Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        signup_button.setOnClickListener {
            register(it)
        }
        loginRedirectText.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register(view: View) {
        account = signup_account.text.toString()
        name = signup_name.text.toString()
        password=signup_password.text.toString()
        conpassword = signup_confirm.text.toString()

        //判空检查
        if(account.isEmpty() || name.isEmpty() || password.isEmpty() || conpassword.isEmpty()){
            Toast.makeText(this,"所有字段必须填写",Toast.LENGTH_SHORT).show()
        }

        if(password==conpassword){
            user = User(account, name, password)
            l= TodoApplication.getUsersDataSource().register(user)
            if(l != num){
                Toast.makeText(this,"注册成功！",Toast.LENGTH_SHORT).show()
//                进入登录页
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,"注册失败！",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,"两次输入的密码不一致！",Toast.LENGTH_SHORT).show()
        }
    }
}