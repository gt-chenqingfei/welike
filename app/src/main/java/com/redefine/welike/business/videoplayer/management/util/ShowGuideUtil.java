package com.redefine.welike.business.videoplayer.management.util;

import android.content.Context;

import com.redefine.foundation.utils.SPUtil;

/**
 * 出现引导的条件：有视频后推荐流的版本更新/安装后，用户前四次重新打开APP（后台切前台），每次打开app期间，第一次进入带有后推荐流的视频播放页时。
 *  总结起来几点
 *      1，一次进程启动只显示一次；
 *      2，总共显示的次数不超过四次；
 * Created by nianguowang on 2018/8/10
 */
public class ShowGuideUtil {

    private static final String SP_NAME = "VIDEO_SP";
    private static final String SP_KEY_SHOW_COUNT = "SHOW_COUNT";
    private static final int MAX_SHOW_GUIDE_COUNT = 4;
    /**
     * 记录本次进程启动是否已经显示过引导动画
     */
    private static boolean sShown;

    private ShowGuideUtil() {}

    public static boolean shouldShowGuide(Context context) {
        boolean showGuide;
        SPUtil spUtil = SPUtil.getInstance(context, SP_NAME);
        int showCount = spUtil.getInt(SP_KEY_SHOW_COUNT, 0);
        showGuide = !sShown && showCount < MAX_SHOW_GUIDE_COUNT;
        if (showGuide) {
            sShown = true;
            spUtil.put(SP_KEY_SHOW_COUNT, showCount + 1);
        }
        return showGuide;
    }
}
