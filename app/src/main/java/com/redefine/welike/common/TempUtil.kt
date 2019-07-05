package com.redefine.welike.common

import java.text.SimpleDateFormat
import java.util.*

class TempUtil {

    fun test(current: Long, start: String, end: String): Boolean {
        val currentTime = getTime(SimpleDateFormat("HH:mm").format(Date(current)))
        var startTime = getTime(start)
        var endTime = getTime(end)
       /* return if (endTime > startTime) {
            (currentTime in startTime..endTime)
        } else {
            (currentTime in 0..startTime) || (currentTime in endTime..24 * 60)
        }*/

        if(endTime>startTime){
            return (currentTime in startTime..endTime);

        }
        if(endTime<startTime){
            if(currentTime>startTime){
                return true
            }

            if(currentTime<endTime&&currentTime<startTime){
                return true
            }

        }

        return false

    }

    fun getTime(time: String): Int {
        var x = 0
        time.split(":").let {
            x = it[0].toInt() * 60 + it[1].toInt()
        }
        return x
    }
}