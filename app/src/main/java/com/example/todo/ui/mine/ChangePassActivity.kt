package com.example.todo.ui.mine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.data.UserManager

class ChangePassActivity : AppCompatActivity() {
    private lateinit var originPass: EditText
    private lateinit var changePass: EditText
    private lateinit var confirmPass: EditText
    private lateinit var confirmBtn : Button
    private lateinit var returnText : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)

        originPass = findViewById(R.id.orginPass)
        changePass = findViewById(R.id.changePass)
        confirmPass = findViewById(R.id.confirmPass)
        returnText = findViewById(R.id.returnText)
        confirmBtn = findViewById(R.id.confirmBtn)
        returnText.setOnClickListener{
            intent = Intent(this, MineActivity::class.java)
            startActivity(intent)
        }
        confirmBtn.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword(){
        val originPassword = originPass.text.toString()
        val newPassword = changePass.text.toString()
        val confirmPassword = confirmPass.text.toString()

        val sqlPassword = UserManager.getOriginalPassword()
        //检查原始密码是否正确
        if(originPassword != sqlPassword){
            Toast.makeText(this,"原有密码错误",Toast.LENGTH_SHORT).show()
            return
        }

        if(newPassword != confirmPassword){
            Toast.makeText(this,"密码不一致",Toast.LENGTH_SHORT).show()
            return
        }
//         更新本地存储中的用户密码
        val currentUser = UserManager.getCurrentUser()
        currentUser?.let {
            it.password = newPassword
            UserManager.setUser(it)

            //实际上一定非null -> 更改数据库的里的密码
            val currentUser = TodoApplication.getCurrentUser()
            if(currentUser != null){
                TodoApplication.getUsersDataSource().updatePassword(currentUser.account,newPassword)

                currentUser.password = newPassword
                UserManager.setUser(currentUser)
            }
            // 密码修改成功，显示成功消息并返回
            Toast.makeText(this,"修改成功，请重新登录",Toast.LENGTH_SHORT).show()
            //清除用户的登陆状态
            UserManager.logout()
            TodoApplication.setCurrentUser(null)
            //跳转到登录页面
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            // 关闭当前界面
            finish()
        }
    }
}