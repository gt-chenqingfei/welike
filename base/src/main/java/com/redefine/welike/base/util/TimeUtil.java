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

public class TimeUtil {
    public static long HOUR_1 = (60 * 60 * 1000);
    public static long DAY_1 = (60 * 60 * 24 * 1000);


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

    public static boolean isSameDay(long lastTime, long currentTime) {

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(currentTime);

        Calendar lastTimeDate = Calendar.getInstance();
        lastTimeDate.setTimeInMillis(lastTime);

        return lastTimeDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR);

    }


}
