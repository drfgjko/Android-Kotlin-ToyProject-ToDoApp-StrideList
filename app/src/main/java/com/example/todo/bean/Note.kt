package com.example.todo.bean

import java.io.Serializable
import java.util.*

class Note : Serializable {
    //主键
    var id: Long = 0
    //定位
    var lbs: String = ""
    //标题
    var title: String = ""
    //内容
    var content: String = ""
    //创建时间
    var createTime: Date = Date()
    //修改时间
    var updateTime: Date = Date()
}