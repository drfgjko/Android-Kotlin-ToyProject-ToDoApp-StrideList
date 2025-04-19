package com.example.todo.ui.tomato

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.todo.services.CountdownService
import com.example.todo.R

class TomatoClock2Activity : AppCompatActivity() {
    private lateinit var vibrator: Vibrator
    //创建countdownUpdateReceiver
    private val countdownUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val countdownTextView = findViewById<TextView>(R.id.countdownTextView)
            val timeLeftFormatted = intent?.getStringExtra("timeLeftFormatted")
            countdownTextView.text = timeLeftFormatted
        }
    }
    //创建countdownStopReceiver广播接收器
    private val countdownStopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // 在接收到广播后退出当前活动
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tomato_clock2)

        // 注册广播接收器
        val intentFilter = IntentFilter("countdown_update")
        registerReceiver(countdownUpdateReceiver, intentFilter)

        // 注册计时停止广播接收器
        val stopIntentFilter = IntentFilter("countdown_stop")
        registerReceiver(countdownStopReceiver, stopIntentFilter)

        val time = intent.getIntExtra("time", 0)
        startCountdownService(time)

        val stopBtn = findViewById<Button>(R.id.stopBtn)
        stopBtn.setOnClickListener{
            val stopIntent = Intent(this, CountdownService::class.java).apply {
                action = "STOP_COUNTDOWN"
            }
//            sendBroadcast(stopIntent)

            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

            // 检查权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(1000)
                }
            }

            Toast.makeText(this, "本次番茄钟计时已结束", Toast.LENGTH_SHORT).show()

            stopService(stopIntent) // 停止服务
            finish() // 结束当前活动
        }
    }

    private fun startCountdownService(time: Int) {
        val serviceIntent = Intent(this, CountdownService::class.java)
        serviceIntent.putExtra("time", time)
        startService(serviceIntent)
    }



    override fun onDestroy() {
        super.onDestroy()
        // 解除注册广播接收器
        unregisterReceiver(countdownUpdateReceiver)
        unregisterReceiver(countdownStopReceiver)
    }
}
