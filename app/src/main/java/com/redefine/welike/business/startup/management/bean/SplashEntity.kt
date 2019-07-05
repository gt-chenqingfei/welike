package com.redefine.welike.business.startup.management.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import com.google.gson.annotations.SerializedName

/**
 * Created by honglin on 2018/5/23.
 */
@Entity(tableName = "splash_table")
open class SplashEntity {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    var id: String? = ""//闪屏id
    @SerializedName("imgUrl")
    var imgUrl: String? = null//图片地址
    @SerializedName("type")
    var type: Int = 0//闪屏类型
    @SerializedName("routing")
    var routing: String? = null//雷达
    @SerializedName("endTime")
    var endTime: Long = 0//结束时间
    @SerializedName("startTime")
    var startTime: Long = 0//闪屏开始出现的时间
    @SerializedName("weight")
    var weight: Int = 0//权重
    @SerializedName("playTime")
    var playTime: Int = 0//持续时间
    @SerializedName("needLogin")
    var needLogin: Boolean = false //是否需要登录
    @SerializedName("needUserInfo")
    var needUserInfo: Boolean = false //是否需要用户信息

}