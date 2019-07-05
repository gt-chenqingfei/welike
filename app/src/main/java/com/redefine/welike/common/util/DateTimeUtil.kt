package com.redefine.welike.common.util

import android.content.res.Resources
import com.redefine.welike.R
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Name: DateTimeUtil
 * Author: weber
 * Email:
 * Comment: //TODO
 * Date: 2019-02-20 17:23
 *
 */

object DateTimeUtil {

    const val SECOND_60 = (60 * 1000).toLong()
    const val HOUR_1 = (60 * 60 * 1000).toLong()
    const val MINUTE_1 = (60 * 1000).toLong()
    const val DAY_1 = (60 * 60 * 24 * 1000).toLong()

    private val hiDateTimeFormatter = SimpleDateFormat("dd-MM-yyyy H:mm")
    private val hiDateFormatter = SimpleDateFormat("dd-MM-yyyy")
//    private static SimpleDateFormat hiTimeFormatter = new SimpleDateFormat("dd-MM H:mm");
//    private static SimpleDateFormat hiDayFormatter = new SimpleDateFormat("H:mm");

    fun formatDateTime(time: Long): String {
        return hiDateFormatter.format(Date(time))
    }

    fun formatLogDateTime(time: Long): String {
        return hiDateTimeFormatter.format(Date(time))
    }

    fun formatArticleDateTime(time: Long): String {
        return hiDateTimeFormatter.format(Date(time))
    }

    fun formatModifyDate(date: Long): String {
        return hiDateFormatter.format(date)
    }

    fun formatImChatTime(resources: Resources, time: Long): String {
        val currentTime = System.currentTimeMillis()
        val offset = currentTime - time

        val currentDate = Calendar.getInstance()
        currentDate.timeInMillis = currentTime

        val receiveDate = Calendar.getInstance()
        receiveDate.timeInMillis = time

        return when {
            offset < SECOND_60 -> resources.getString(R.string.now)
            offset < HOUR_1 -> (offset / MINUTE_1).toString() + " Min"
            offset < DAY_1 -> (offset / HOUR_1).toString() + " Hrs"
            offset < 30 * DAY_1 -> (offset / DAY_1).toString() + resources.getString(R.string.day_Days)
            currentDate.get(Calendar.YEAR) == receiveDate.get(Calendar.YEAR) -> getDateInYear(resources, receiveDate)
            else -> getDateOutYear(resources, receiveDate)
        }
    }

    fun formatPostPublishTime(resources: Resources, time: Long): String {
        return getCommonTime(resources, time)
    }

    fun getMessageReceiveTime(resources: Resources, time: Long): String {

        return getCommonTime(resources, time)
    }

    private fun getCommonTime(resources: Resources, time: Long): String {
        val currentTime = System.currentTimeMillis()
        val offset = currentTime - time
        val currentDate = Calendar.getInstance()
        currentDate.timeInMillis = currentTime
        val postDate = Calendar.getInstance()
        postDate.timeInMillis = time
        return when {
            offset < SECOND_60 -> resources.getString(R.string.now)
            offset < HOUR_1 -> (offset / MINUTE_1).toString() + " Min"
            offset < DAY_1 -> (offset / HOUR_1).toString() + " Hrs"
            offset < 2 * DAY_1 -> resources.getString(R.string.day_yesterday)
            offset < 7 * DAY_1 -> getDay4Week(resources, postDate)
            currentDate.get(Calendar.YEAR) == postDate.get(Calendar.YEAR) -> getDateInYear(resources, postDate)
            else -> getDateOutYear(resources, postDate)
        }
    }

    fun secToTime(time: Int): String {
        var timeStr: String? = null
        var hour = 0
        var minute = 0
        val second = 0
        if (time <= 0)
            return "00:00"
        else {
            minute = time / 60
            if (minute < 60) {
                timeStr = unitFormat(minute) + "min"
            } else {
                hour = minute / 60
                if (hour > 99)
                    return "99:59:59"
                minute %= 60
                timeStr = if (minute == 0) {
                    unitFormat(hour) + "h"

                } else {
                    unitFormat(hour) + "h" + " " + unitFormat(minute) + "min"

                }
            }
        }
        return timeStr
    }

    private fun unitFormat(i: Int): String {
        return if (i in 0..9)
            Integer.toString(i)
        else
            "" + i
    }

    fun getVoteExpiredDayTime(time: Long): String {

        val isAddDay = getRealVoteExpiredHourTime(time) == "23"

        return if (time > DAY_1) {
            (time / DAY_1 + if (isAddDay) 1 else 0).toString()
        } else {
            if (isAddDay) "1" else "0"
        }
    }

    private fun getRealVoteExpiredHourTime(time: Long): String {
        return if (time > HOUR_1) {
            (time / HOUR_1 % 24).toString()
        } else {
            "0"
        }
    }


    fun getVoteExpiredHourTime(time: Long): String {
        val hours = time / HOUR_1 % 24

        return if (hours == 23L)
            "0"
        else
            (hours + 1).toString()
    }

    /***
     * @return DAY 4 WEEK
     */
    private fun getDay4Week(resources: Resources, calendar: Calendar): String {

        val week = calendar.get(Calendar.DAY_OF_WEEK)

        return when (week) {
            Calendar.MONDAY -> resources.getString(R.string.week_monday)
            Calendar.TUESDAY -> resources.getString(R.string.week_tueday)
            Calendar.WEDNESDAY -> resources.getString(R.string.week_wedday)
            Calendar.THURSDAY -> resources.getString(R.string.week_thurday)
            Calendar.FRIDAY -> resources.getString(R.string.week_friday)
            Calendar.SATURDAY -> resources.getString(R.string.week_satday)
            Calendar.SUNDAY -> resources.getString(R.string.week_sunday)
            else -> ""
        }
    }


    /**
     * @return "22 Oct 2016 "
     */
    private fun getDateOutYear(resources: Resources, calendar: Calendar): String {

        val date = StringBuilder()
        date.append(calendar.get(Calendar.DAY_OF_MONTH))
        date.append(' ')
        date.append(getDateMonth(resources, calendar))
        date.append(' ')
        date.append(calendar.get(Calendar.YEAR))

        return date.toString()

    }

    /**
     * @return "22 Oct"
     */
    private fun getDateInYear(resources: Resources, calendar: Calendar): String {

        val date = StringBuilder()
        date.append(calendar.get(Calendar.DAY_OF_MONTH))
        date.append(' ')
        date.append(getDateMonth(resources, calendar))
        return date.toString()

    }

    /**
     * @return "Oct"
     */

    private fun getDateMonth(resources: Resources, calendar: Calendar): String {

        val week = calendar.get(Calendar.MONTH)

        return when (week) {
            Calendar.JANUARY -> resources.getString(R.string.mon_Jan)
            Calendar.FEBRUARY -> resources.getString(R.string.mon_Feb)
            Calendar.MARCH -> resources.getString(R.string.mon_Mar)
            Calendar.APRIL -> resources.getString(R.string.mon_Apr)
            Calendar.MAY -> resources.getString(R.string.mon_May)
            Calendar.JUNE -> resources.getString(R.string.mon_June)
            Calendar.JULY -> resources.getString(R.string.mon_July)
            Calendar.AUGUST -> resources.getString(R.string.mon_Aug)
            Calendar.SEPTEMBER -> resources.getString(R.string.mon_Sep)
            Calendar.OCTOBER -> resources.getString(R.string.mon_Oct)
            Calendar.NOVEMBER -> resources.getString(R.string.mon_Nov)
            Calendar.DECEMBER -> resources.getString(R.string.mon_Dec)
            else -> ""
        }

    }

}