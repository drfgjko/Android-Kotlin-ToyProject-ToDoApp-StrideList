package com.example.todo.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.*
import android.Manifest
import android.widget.Toast
import com.example.todo.R

class CountdownService : Service() {
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var vibrator: Vibrator
    private var isCountdownRunning: Boolean = false
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val time = intent?.getIntExtra("time", 0) ?: 0
        startCountdown(time)
        return START_STICKY
    }

    //开始计时
    private fun startCountdown(timeInMinutes: Int) {
        countDownTimer = object : CountDownTimer(timeInMinutes * 60 * 1000.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / (60 * 1000)
                val seconds = millisUntilFinished % (60 * 1000) / 1000
                val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                // 更新通知栏
                updateNotification(timeLeftFormatted)

                // 发送广播通知计时器的当前状态
                val intent = Intent("countdown_update").apply {
                    putExtra("timeLeftFormatted", timeLeftFormatted)
                }
                sendBroadcast(intent)
            }

            override fun onFinish() {
                // 发送广播通知计时器的停止状态
                val stop_intent = Intent("countdown_stop")
                sendBroadcast(stop_intent)
                // 计时结束，启动震动并提示
                startVibration()
                stopSelf()
            }
        }.start()
        isCountdownRunning = true
    }


    //震动功能
    private fun startVibration() {
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
    }

    //更新状态栏通知
    private fun updateNotification(timeLeftFormatted: String) {
        val channelId = "countdown_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Countdown Timer", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }


        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("番茄钟")
            .setContentText("时间还剩: $timeLeftFormatted")
            .setSmallIcon(R.drawable.tomato_icon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.tomato))
            .setOnlyAlertOnce(true) // 设置只提醒一次，确保在每次更新通知时不会播放声音
            .addAction(R.drawable.stop_icon, "结束计时", stopActionIntent())
            .build()

        startForeground(1, notification)
    }

    //状态栏中的停止番茄钟
    private fun stopActionIntent(): PendingIntent {
        val stopIntent = Intent(this, CountdownService::class.java).apply {
            action = "STOP_COUNTDOWN"
        }

        return PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        if (intent?.action == "STOP_COUNTDOWN") {
            stopCountdown()
        }
    }

    private fun stopCountdown() {
        // 发送广播通知计时器的停止状态
        val stop_intent = Intent("countdown_stop")
        sendBroadcast(stop_intent)
        countDownTimer.cancel()
        stopForeground(true)
        stopSelf()
        isCountdownRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }
}