package com.example.todo.ui.tomato

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.todo.services.CountdownService
import com.example.todo.R
import com.example.todo.ui.mine.MineActivity
import com.example.todo.ui.notes.NotesListActivity
import com.example.todo.ui.todos.ToDoListActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class TomatoClockActivity : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var lastUpdate: Long = 0
    private val SHAKE_THRESHOLD = 5

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tomato_clock)

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

        //1m
        val startBtn_1m=findViewById<Button>(R.id.StartBtn_1m)
        startBtn_1m.setOnClickListener{
            // 检查是否存在运行中的服务
            if (isServiceRunning(CountdownService::class.java)) {
                // 如果存在，弹出提示并跳转到正在运行的活动
                Toast.makeText(this, "当前已存在番茄钟", Toast.LENGTH_SHORT).show()
            } else {
                // 如果不存在，启动番茄钟 1 分钟活动
                val time = 1
                val intent = Intent(this, TomatoClock2Activity::class.java)
                intent.putExtra("time", time)
                startActivity(intent)
            }
        }

        val startBtn_25m = findViewById<Button>(R.id.StartBtn_25m)
        startBtn_25m.setOnClickListener {
            if (!isServiceRunning(CountdownService::class.java)) {
                val time = 25
                startTomatoClock(time)
            } else {
                Toast.makeText(this, "当前已存在番茄钟", Toast.LENGTH_SHORT).show()
            }
        }

        // 40分钟番茄钟按钮点击事件
        val startBtn_40m = findViewById<Button>(R.id.StartBtn_40m)
        startBtn_40m.setOnClickListener {
            if (!isServiceRunning(CountdownService::class.java)) {
                val time = 40
                startTomatoClock(time)
            } else {
                Toast.makeText(this, "当前已存在番茄钟", Toast.LENGTH_SHORT).show()
            }
        }

        // 自定义时间按钮点击事件
        val timeEditText = findViewById<EditText>(R.id.timeEditText)
        val startBtn_cus = findViewById<Button>(R.id.StartBtn_cus)
        startBtn_cus.setOnClickListener {
            val time = timeEditText.text.toString()
            if (time.isNotEmpty()) {
                if (!isServiceRunning(CountdownService::class.java)) {
                    val intent = Intent(this, TomatoClock2Activity::class.java)
                    intent.putExtra("time", time.toInt())
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "当前已存在番茄钟", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "请输入时间", Toast.LENGTH_SHORT).show()
            }
        }


//        Log.d("create","create")
        // 获取传感器管理器
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // 注册陀螺仪传感器监听器
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        lastUpdate = System.currentTimeMillis()



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val btnGetLocation: Button = findViewById(R.id.btnGetLocation)
        btnGetLocation.setOnClickListener {
            getLocation()
        }

        getLocationPermission()
    }

    override fun onResume() {
        Log.d("resume","resume")
        super.onResume()
        // 注册陀螺仪传感器监听器
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        lastUpdate = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        // 取消注册陀螺仪传感器监听器
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastUpdate) > 100) {
                val time = 25 // 设置番茄钟时间
                if (isShakeDetected(event.values)) {
                    // 摇动事件检测到，启动番茄钟 25 分钟活动
                    startTomatoClock(time)
                }
                lastUpdate = currentTime
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private fun isShakeDetected(values: FloatArray): Boolean {
        val x = values[0]
        val y = values[1]
        val z = values[2]
        val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble())
        Log.d("shake",acceleration.toString())
        return acceleration > SHAKE_THRESHOLD
    }

    private fun startTomatoClock(time: Int) {
        val intent = Intent(this, TomatoClock2Activity::class.java)
        intent.putExtra("time", time)
        startActivity(intent)
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun getLocation() {
        if (locationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitudeStr = if (location.latitude >= 0) {
                            "${"%.3f".format(location.latitude)}°N"
                        } else {
                            "${"%.3f".format(-location.latitude)}°S"
                        }
                        val longitudeStr = if (location.longitude >= 0) {
                            "${"%.3f".format(location.longitude)}°E"
                        } else {
                            "${"%.3f".format(-location.longitude)}°W"
                        }
                        val msg = "纬度: $latitudeStr\n经度: $longitudeStr"
                        val tvLocation: TextView = findViewById(R.id.tvLocation)
                        tvLocation.text = msg

                        val btnGetLocation = findViewById<Button>(R.id.btnGetLocation)
                        btnGetLocation.visibility=View.GONE
                    } else {
                        Toast.makeText(this, "获取位置信息失败", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        } else {
            locationPermissionGranted = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true
            }
        }
    }


}