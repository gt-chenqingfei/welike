package com.redefine.welike.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.browse.management.request.GPScoreStatusRequest;
import com.redefine.welike.business.browse.management.request.GPStatusRequest;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.GPScoreEventManager;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class ScoreManager {


    private static final int SHOW_GP_LOGIN_DAY_COUNT = 1;
    private static final int SHOW_GP_PUBLISH_COUNT = 0;
    private static final int SHOW_GP_REFRESH_COUNT = 3;
    private static final int SHOW_GP_SHARE_COUNT = 0;


    public static boolean canShowScoreView(Context context) {



        if (SpManager.GooglePlayScoreSp.getCanShowScore(context)) {//是否可以展示

            if (!SpManager.GooglePlayScoreSp.getIsScore(context)) {//是否已经评分

                if (!SpManager.GooglePlayScoreSp.getIsShowScore(context)) {//是否显示评分

                    if (SpManager.GooglePlayScoreSp.getLoginDayCount(context) >= SpManager.GooglePlayScoreSp.getActiveTimes(context)) {//

                        if (SpManager.GooglePlayScoreSp.getPostCount(context) > SHOW_GP_PUBLISH_COUNT
                                || SpManager.GooglePlayScoreSp.getRefreshCount(context) > SpManager.GooglePlayScoreSp.getRefreshTimes(context)
                                || SpManager.GooglePlayScoreSp.getShareCount(context) >= SpManager.GooglePlayScoreSp.getShareTimes(context)) {

                            SpManager.GooglePlayScoreSp.setIsShowScore(context, true);
                            SpManager.GooglePlayScoreSp.setLoginDayCount(context, -1);

                            return true;
                        }

                    }

                }

            }

        }

        return false;

    }


    public static void resetCountIfSameDay2ShowScore(Context context) {

        long lastTime = SpManager.GooglePlayScoreSp.getLastShowTime(context);

        if (!TimeUtil.isSameDay(lastTime, System.currentTimeMillis())) {
            SpManager.GooglePlayScoreSp.setLastShowTime(context, System.currentTimeMillis());
            int dayCount = SpManager.GooglePlayScoreSp.getLoginDayCount(context) + 1;

            SpManager.GooglePlayScoreSp.setLoginDayCount(context, dayCount);

            SpManager.GooglePlayScoreSp.resetDayCount(context);

        }

    }


    public static void setRefreshCount(Context context) {

        if (context == null) return;

        int count = SpManager.GooglePlayScoreSp.getRefreshCount(context);

        SpManager.GooglePlayScoreSp.setRefreshCount(context, count + 1);

    }


    public static void setPostCount(Context context) {

        if (context == null) return;

        int count = SpManager.GooglePlayScoreSp.getPostCount(context);

        SpManager.GooglePlayScoreSp.setPostCount(context, count + 1);

    }

    public static void setShareCount(Context context) {

        if (context == null) return;

        int count = SpManager.GooglePlayScoreSp.getShareCount(context);

        SpManager.GooglePlayScoreSp.setShareCount(context, count + 1);

    }


    public static void jumpToGooglePlay(Context context) {

        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            GPScoreEventManager.INSTANCE.setReturn_result(EventConstants.GP_RETURN_RESULT_SUCCESS);
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            GPScoreEventManager.INSTANCE.setReturn_result(EventConstants.GP_RETURN_RESULT_FAIL);
        }
        GPScoreEventManager.INSTANCE.report5();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_gp_score_gp_toast"));
                    }
                });

            }
        }).start();

    }

    public static void getToastStatus(final Context context) {
        try {

            new GPStatusRequest(context).request(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {

                    if (result.containsKey("allUserGPToastIsHide")) {
                        SpManager.GooglePlayScoreSp.setCanShowScore(context, !result.getBooleanValue("allUserGPToastIsHide"));
                    }

                    if (result.containsKey("userGPToastIsHide")) {
                        SpManager.GooglePlayScoreSp.setIsScore(context, result.getBooleanValue("userGPToastIsHide"));
                    }
                    if (result.containsKey("shareButtonClickTimes")) {
                        SpManager.GooglePlayScoreSp.setShareTimes(context, result.getIntValue("shareButtonClickTimes"));
                    }
                    if (result.containsKey("activeDays")) {
                        SpManager.GooglePlayScoreSp.setActiveTimes(context, result.getIntValue("activeDays"));
                    }
                    if (result.containsKey("refreshTimes")) {
                        SpManager.GooglePlayScoreSp.setRefreshTimes(context, result.getIntValue("refreshTimes"));
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void postToastStatus(final Context context, final int score) {

        SpManager.GooglePlayScoreSp.setIsScore(context, true);
        SpManager.GooglePlayScoreSp.setIsShowScore(context, true);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    new GPCloseRequest(context).request();
                    new GPScoreStatusRequest(context).request(score);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

}
