package com.example.todo.ui.mine

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.data.UserManager

class MineFragment :Fragment(), ChangePhotoActivity.OnAvatarChangedListener{
    private lateinit var rootView: View
    private lateinit var user_name:TextView
    private lateinit var changePass: RelativeLayout
    private lateinit var logoutBtn: TextView
    private lateinit var loginBtn: TextView
    private lateinit var userImage: ImageView

    fun updateUserImage(imageUri: Uri?) {
        Glide.with(this)
            .load(imageUri)
            .centerCrop()
            .into(userImage)
//        user_image.setImageURI(imageUri)
    }

    override fun onAvatarChanged(uri: Uri) {
//        userImage.setImageURI(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_mine,container,false)
        //初始化控件
        userImage = rootView.findViewById(R.id.user_image)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user_name = view.findViewById(R.id.user_name)
        changePass = view.findViewById(R.id.changePassRelativeLayout)
        logoutBtn = view.findViewById(R.id.logoutBtn) // 找到退出登录按钮
        loginBtn = view.findViewById(R.id.loginBtn)
        userImage = view.findViewById(R.id.user_image)
        updateUserName()

        val user = TodoApplication.getCurrentUser()
        if(user!= null){
            logoutBtn.visibility = View.VISIBLE
            loginBtn.visibility = View.GONE
        }else{
            loginBtn.visibility = View.VISIBLE
            logoutBtn.visibility = View.GONE
        }

        logoutBtn.setOnClickListener {
            val currentUser = TodoApplication.getCurrentUser()
            if (currentUser?.account != null) {
                logout()
            }
        }

        userImage.setOnClickListener{
            val intent = Intent(requireContext(), ChangePhotoActivity::class.java)
            startActivity(intent)
        }
        (requireActivity() as? ChangePhotoActivity)?.setOnAvatarChangedListener(this)

        loginBtn.setOnClickListener{
            val intent = Intent(requireContext(),LoginActivity::class.java)
            startActivity(intent)
        }

    }


    //    退出
    private fun logout() {
        //清除用户的登陆状态
        UserManager.logout()

//    用于全局
        TodoApplication.setCurrentUser(null)

        Toast.makeText(context,"退出登录成功",Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), MineActivity::class.java)
        startActivity(intent)

        requireActivity().finish()
    }

    private fun updateUserName(){
        val userName = UserManager.getCurrentUser()?.name?:"默认用户"
        user_name.text=userName
    }

    // 公开方法，用于设置 changePass 的点击事件
    fun setChangePassClickListener(listener: View.OnClickListener) {
        changePass.setOnClickListener(listener)
    }
}
