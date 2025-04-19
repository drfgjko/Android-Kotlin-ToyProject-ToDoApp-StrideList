package com.example.todo.ui.notes

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationListener
import com.example.todo.R
import com.example.todo.TodoApplication
import com.example.todo.bean.Note
import kotlinx.android.synthetic.main.activity_notes_edit.*


class NotesEditActivity : AppCompatActivity(){

    companion object{
        private const val REQUEST_EDIT = 7
        private const val REQUEST_BACK = 8
        private const val REQUEST_CODE = 9
        fun buildIntent(context: Context, id: Long): Intent {
            return Intent(context, NotesEditActivity::class.java).putExtra("noteId",id)
        }
    }

    private lateinit var toolbar: Toolbar
    private lateinit var lbsTextView: TextView
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    private var noteId: Long = 0

    //声明AMapLocationClient类对象
    private lateinit var mLocationClient: AMapLocationClient
    private lateinit var mLocationListener: AMapLocationListener

    private var mLocationOption  = AMapLocationClientOption()

    private var newPermission = false

    private var city: String? = null
    private var district: String? = null
    private var street: String? = null
    private var lbsString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_edit)
        // 获取控件
        initView()
        //标题栏
        setView()
        //保证隐私政策合规
        AMapLocationClient.updatePrivacyShow(this,true,true)
        AMapLocationClient.updatePrivacyAgree(this,true)
        //初始化定位
        mLocationClient = AMapLocationClient(this.applicationContext)
        // 配置mLocationOption参数
        initOption()
        mLocationListener = AMapLocationListener { amapLocation ->
            if (amapLocation != null) {
                if (amapLocation.errorCode == 0) {
                    // 可在此处解析 amapLocation 获取相应内容
                    city = amapLocation.city // 城市信息
                    district = amapLocation.district // 城区信息
                    street = amapLocation.street // 街道信息
                    Log.d("city", "$city")
                    Log.d("district", "$district")
                    Log.d("street", "$street")
                    lbsString = "$city,$district,$street"
                    // 处理位置信息
                } else {
                    // 定位失败时,可通过 ErrCode(错误码)信息来确定失败的原因,errInfo是错误信息,详见高德开放平台错误码表
                    // 真机测试时看
                    Toast.makeText(this, "location Error, ErrCode: ${amapLocation.errorCode}, errInfo: ${amapLocation.errorInfo}", Toast.LENGTH_SHORT).show()
                    // 自己看
                    Log.e("AmapError",
                        "location Error, ErrCode: ${amapLocation.errorCode}, errInfo: ${amapLocation.errorInfo}")
                }
            }
        }
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener)

        // 获取noteId
        noteId = intent.getLongExtra("noteId", 0)

        if (noteId > 0) { //修改
            val note = TodoApplication.getNotesDataSource().selectOne(noteId)
            if (note == null) {
                Toast.makeText(this, "未找到该日记", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            //设置数据
            toolbar.title = "修改笔记"
            titleEditText.text = Editable.Factory.getInstance().newEditable(note.title)
            contentEditText.text = Editable.Factory.getInstance().newEditable(note.content)
            lbsTextView.text = note.lbs

        } else { //创建
            //设置默认值
            toolbar.title = "创建笔记"
        }

//        两个逻辑 -> 新增保存+修改保存
        action_save.setOnClickListener{
//            包含在saveNote()里
                saveNote()

        }
        action_location.setOnClickListener{
            checkPermission()
        }
    }

    private fun initOption() {
        //初始化AMapLocationClientOption对象
        mLocationOption = AMapLocationClientOption()
        //定位场景 -> 签到场景，获取一次定位
        mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
        mLocationClient.setLocationOption(mLocationOption)
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient.stopLocation()
        mLocationClient.startLocation()
        //定位模式 -> 低精度
        mLocationOption.locationMode = AMapLocationMode.Hight_Accuracy
        //获取一次定位结果
        mLocationOption.isOnceLocation = true
        //获取最近3s内精度最高的一次定位结果
        mLocationOption.isOnceLocationLatest = true
        //模拟软件Mock位置结果 -> 模拟GPS定位结果
        mLocationOption.isMockEnable = true
        //设置定位请求超时时间
        mLocationOption.httpTimeOut = 20000
        mLocationOption.isNeedAddress = true
        mLocationOption.isLocationCacheEnable = false;
    }


    private fun setView() {
        // 将标题栏关联到页面
        setSupportActionBar(toolbar)
        // 显示返回键
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_icon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        titleEditText = findViewById(R.id.title_editText)
        contentEditText = findViewById(R.id.content_editText)
        lbsTextView = findViewById(R.id.lbs_textView)
    }

    //菜单点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_OK)
                //关闭编辑活动
                finish()
                val intent = NotesListActivity.buildIntent(this, noteId)
                startActivityForResult(intent, REQUEST_BACK)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 新增保存
    private fun saveNote() {
        // 检查输入情况
        val title = titleEditText.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "未输入标题", Toast.LENGTH_SHORT).show()
            return
        }
        val content = contentEditText.text.toString().trim()
        if (content.isEmpty()) {
            Toast.makeText(this, "未输入内容", Toast.LENGTH_SHORT).show()
            return
        }

        val note = Note()
        note.title = title
        note.content = content
        note.lbs = lbsString.toString()
        if (noteId > 0) { // 修改
            TodoApplication.getNotesDataSource().updateNote(noteId, note)
        } else { // 创建
            TodoApplication.getNotesDataSource().insertNote(note)
        }
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        //关闭编辑活动
        finish()

        val intent = NotesListActivity.buildIntent(this, noteId)
        startActivityForResult(intent, REQUEST_EDIT)
    }


    private fun startLocationIfPermissionsGranted() {
        if (newPermission) {
            //开启定位
            mLocationClient.startLocation()
            Log.d("check","开启定位")
        }else{
            Toast.makeText(this, "权限不足", Toast.LENGTH_SHORT).show()
        }
    }

//    权限请求是一个异步操作，注意逻辑
    private fun checkPermission() {
        val requiredPermissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
        )
        val unGrantedPermissions = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        if (unGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, unGrantedPermissions, REQUEST_CODE)
        } else {
            // 如果所有权限都已授予，则执行相应的操作
            startLocationIfPermissionsGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            newPermission = allPermissionsGranted
            if (allPermissionsGranted) {
                // 如果所有权限都被授予，则执行相应的操作
                startLocationIfPermissionsGranted()
            } else {
                Toast.makeText(this, "拒绝了动态申请", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //停止定位
        mLocationClient.stopLocation()
        //销毁定位客户端
        mLocationClient.onDestroy()
    }
}