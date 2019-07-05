package com.redefine.welike.base.util;

import android.annotation.SuppressLint;

import com.redefine.welike.base.resource.ResourceTool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by liwb on 2018/1/11.
 * <p>
 * change by zhhl on 2018、7、3
 */

public class DateTimeUtil {
    public static final long SECOND_60 = 60 * 1000;
    public static final long HOUR_1 = 60 * 60 * 1000;
    public static final long MINUTE_1 = 60 * 1000;
    public static final long DAY_1 = 60 * 60 * 24 * 1000;

    private static SimpleDateFormat hiDateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy H:mm");
    private static SimpleDateFormat hiDateFormatter = new SimpleDateFormat("dd-MM-yyyy");
//    private static SimpleDateFormat hiTimeFormatter = new SimpleDateFormat("dd-MM H:mm");
//    private static SimpleDateFormat hiDayFormatter = new SimpleDateFormat("H:mm");

    public static String formatDateTime(long time) {
        return hiDateFormatter.format(new Date(time));
    }

    public static String formatLogDateTime(long time) {
        return hiDateTimeFormatter.format(new Date(time));
    }

    public static String formatArticleDateTime(long time) {
        return hiDateTimeFormatter.format(new Date(time));
    }

    public static String formatModifyDate(long date) {
        return hiDateFormatter.format(date);
    }

    public static String formatImChatTime(long time) {
        long currentTime = System.currentTimeMillis();
        long offset = currentTime - time;

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(currentTime);

        Calendar receiveDate = Calendar.getInstance();
        receiveDate.setTimeInMillis(time);

        if (offset < SECOND_60) {
            return ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "now");
        } else if (offset < HOUR_1) {
            return String.valueOf(offset / MINUTE_1) + " Min";
        } else if (offset < DAY_1) {
            return String.valueOf(offset / HOUR_1) + " Hrs";
        } else if (offset < 30 * DAY_1) {
            return String.valueOf(offset / DAY_1) + ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "day_Days");
        } else if (currentDate.get(Calendar.YEAR) == receiveDate.get(Calendar.YEAR)) {
            return getDateInYear(receiveDate);
        } else {
            return getDateOutYear(receiveDate);
        }
    }

    public static String formatPostPublishTime(long time) {
        return getCommonTime(time);
    }

    public static String getMessageReceiveTime(long time) {

        return getCommonTime(time);
    }

    private static String getCommonTime(long time) {
        long currentTime = System.currentTimeMillis();
        long offset = currentTime - time;
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(currentTime);
        Calendar postDate = Calendar.getInstance();
        postDate.setTimeInMillis(time);
        if (offset < SECOND_60) {
            return ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "now");
        } else if (offset < HOUR_1) {
            return String.valueOf(offset / MINUTE_1) + " Min";
        } else if (offset < DAY_1) {
            return String.valueOf(offset / HOUR_1) + " Hrs";
        } else if (offset < 2 * DAY_1) {
            return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "day_yesterday");
        } else if (offset < 7 * DAY_1) {
            return getDay4Week(postDate);
        } else if (currentDate.get(Calendar.YEAR) == postDate.get(Calendar.YEAR)) {
            return getDateInYear(postDate);
        } else {
            return getDateOutYear(postDate);
        }
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                timeStr = unitFormat(minute) + "min";
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                if (minute == 0) {
                    timeStr = unitFormat(hour) + "h";

                } else {
                    timeStr = unitFormat(hour) + "h" + " " + unitFormat(minute) + "min";

                }
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getVoteExpiredDayTime(long time) {

        boolean isAddDay = getRealVoteExpiredHourTime(time).equals("23");

        if (time > DAY_1) {
            return String.valueOf(time / DAY_1 + (isAddDay ? 1 : 0));
        } else {
            return isAddDay ? "1" : "0";
        }
    }

    private static String getRealVoteExpiredHourTime(long time) {
        if (time > HOUR_1) {
            return String.valueOf(time / HOUR_1 % 24);
        } else {
            return "0";
        }
    }


    public static String getVoteExpiredHourTime(long time) {
        long hours = time / HOUR_1 % 24;

        if (hours == 23) return "0";
        else return String.valueOf(hours + 1);
    }

    /**
     * MS turn every minute
     *
     * @param duration Millisecond
     * @return Every minute
     */
    @SuppressLint("DefaultLocale")
    public static String timeParse(long duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    /***
     * @return DAY 4 WEEK
     *
     */
    private static String getDay4Week(Calendar calendar) {

        int week = calendar.get(Calendar.DAY_OF_WEEK);

        switch (week) {
            case Calendar.MONDAY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "week_monday");
            case Calendar.TUESDAY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "week_tueday");
            case Calendar.WEDNESDAY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "week_wedday");
            case Calendar.THURSDAY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "week_thurday");
            case Calendar.FRIDAY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "week_friday");
            case Calendar.SATURDAY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "week_satday");
            case Calendar.SUNDAY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "week_sunday");
            default:
                return "";
        }
    }


    /**
     * @return "22 Oct 2016 "
     */
    private static String getDateOutYear(Calendar calendar) {

        StringBuilder date = new StringBuilder();
        date.append(calendar.get(Calendar.DAY_OF_MONTH));
        date.append(' ');
        date.append(getDateMonth(calendar));
        date.append(' ');
        date.append(calendar.get(Calendar.YEAR));

        return date.toString();

    }

    /**
     * @return "22 Oct"
     */
    private static String getDateInYear(Calendar calendar) {

        StringBuilder date = new StringBuilder();
        date.append(calendar.get(Calendar.DAY_OF_MONTH));
        date.append(' ');
        date.append(getDateMonth(calendar));
        return date.toString();

    }

    /**
     * @return "Oct"
     */

    private static String getDateMonth(Calendar calendar) {

        int week = calendar.get(Calendar.MONTH);

        switch (week) {
            case Calendar.JANUARY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_Jan");
            case Calendar.FEBRUARY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_Feb");
            case Calendar.MARCH:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_Mar");
            case Calendar.APRIL:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_Apr");
            case Calendar.MAY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_May");
            case Calendar.JUNE:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_June");
            case Calendar.JULY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_July");
            case Calendar.AUGUST:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_Aug");
            case Calendar.SEPTEMBER:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_Sep");
            case Calendar.OCTOBER:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_Oct");
            case Calendar.NOVEMBER:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_Nov");
            case Calendar.DECEMBER:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "mon_Dec");
            default:
                return "";
        }

    }


    public static boolean isSameDay(long lastTime, long currentTime) {

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(currentTime);

        Calendar lastTimeDate = Calendar.getInstance();
        lastTimeDate.setTimeInMillis(lastTime);

        return lastTimeDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR);

    }

    public static boolean isOver5Min(long newTime, long preTime) {
        long diff = Math.abs(newTime - preTime);
        return diff > 5 * 60 * 1000;
    }


}
