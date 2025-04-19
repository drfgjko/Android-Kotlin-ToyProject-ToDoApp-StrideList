package com.example.todo.utils

import java.text.SimpleDateFormat
import java.util.*

//时间处理工具
//来源：@https://gitee.com/codewanli
object TimeUtils {


    /**
     * 获取简要时间
     */
    fun getSimpleTime(millis: Long): String? {
        val now = System.currentTimeMillis()
        val span = now - millis
        if (span < 0) {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(millis))
        } else if (span < 1000) {
            return "刚刚"
        } else if (span < 60000) {
            return String.format(Locale.getDefault(), "%d秒前", span / 1000)
        } else if (span < 3600000) {
            return String.format(Locale.getDefault(), "%d分钟前", span / 60000)
        }
        val wee = getWeeOfToday()
        return if (millis >= wee) {
            "今天" + SimpleDateFormat("HH:mm:ss").format(Date(millis))
        } else if (millis >= wee - 86400000) {
            "昨天" + SimpleDateFormat("HH:mm:ss").format(Date(millis))
        } else {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(millis))
        }
    }

    /**
     * 获取时分秒归0的当前时间
     */
    private fun getWeeOfToday(): Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

}
