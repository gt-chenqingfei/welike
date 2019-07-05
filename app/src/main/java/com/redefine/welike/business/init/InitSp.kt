package com.redefine.welike.business.init

import android.content.Context
import android.content.SharedPreferences
import com.redefine.welike.MyApplication
import java.util.*

/**
 * helper for check setting.
 */
enum class InitCheck {
    VipCheck, ABTest, C;

    /**
     * 当天数间隔 大于等于day 的时候，才会执行 after()
     */
    fun check(after: () -> Unit, day: Int = 1) {
        //Get start time
        val startCalendar = Calendar.getInstance().also { it.time = Date(InitSp.sp.getLong(name, 0L)) }
        //Get current time
        val endCalendar = Calendar.getInstance().also { it.time = Date(System.currentTimeMillis()) }
        val between = endCalendar.get(Calendar.DAY_OF_YEAR) - startCalendar.get(Calendar.DAY_OF_YEAR)
        if (between >= day) {
            after()
        }
    }

    /**
     * 保存完成时间
     */
    fun done() {
        InitSp.sp.edit().putLong(this.name, System.currentTimeMillis()).apply()
    }
}

object InitSp {
    val sp: SharedPreferences by lazy { MyApplication.getApp().getSharedPreferences("InitSp", Context.MODE_PRIVATE) }

}