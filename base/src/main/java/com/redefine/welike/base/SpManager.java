package com.redefine.welike.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.redefine.foundation.framework.BroadcastManagerBase;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 * <p>
 * Description : TODO
 * <p>
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */

public class SpManager extends BroadcastManagerBase {
    public static final String sharePreferencesSettingName = "setting";
    public static final String sharePreferencesNotificationName = "notification";
    public static final String sharePreferencesNotificationCount = "notificationCount";
    public static final String sharePreferencesScore = "scoreStatistics";
    public static final String sharePreferencesIMName = "im";
    public static final String sharePreferencesPostIdName = "postId";
    public static final String sharePreferencesConfigName = "config";
    /**
     * notification key begin
     **/
    /* begin 此字段已废弃 带用户留存没有即可清除 */
    @Deprecated
    public static final String mentionStampKeyName = "mentionStampKey";
    @Deprecated
    public static final String commentStampKeyName = "commentStampKey";
    @Deprecated
    public static final String likeStampKeyName = "likeStampKey";
    @Deprecated
    public static final String mentionCountKeyName = "mentionCountKey";
    @Deprecated
    public static final String commentCountKeyName = "commentCountKey";
    @Deprecated
    public static final String likeCountKeyName = "likeCountKey";
    /* end 此字段已废弃 */

    public static final String pushNotificationTokenKeyName = "pushNotificationTokenKey";
    /** notification key end **/

    /**
     * im key end
     **/
    @Deprecated
    public static final String imMessagesStampKeyName = "messagesStamp";
    /**
     * im key end
     **/

    public static final String settingMenuLanguageTypeKeyName = "mlanguage";
    public static final String settingContentLanguageTypeKeyName = "mContentlanguage";
    public static final String settingSubLanguageTypesKeyName = "sublanguages";
    public static final String settingUpgradedKeyName = "upgraded";
    public static final String settingUpgradeShowTime = "settingUpgradeShowTime";
    public static final String settingUpgradedForceKeyName = "upgradedForce";

    public static final String settingMobileModel = "settingMobileModel";
    public static final String settingMobileContactsModel = "settingMobileContactsModel";
    public static final String settingFollowByContactsModel = "settingFollowByContactsModel";


    public static final String notificationStartTime = "notificationStartTime";
    public static final String notificationEndTime = "notificationEndTime";

    public static final String notificationMuteAt = "notificationMuteAt";
    public static final String notificationMuteComment = "notificationMuteComment";
    public static final String notificationMuteLike = "notificationMuteLike";
    public static final String notificationMuteIm = "notificationMuteIm";
    public static final String notificationMuteStrangerIm = "notificationMuteStrangerIm";
    public static final String notificationMuteFollow = "notificationMuteFollow";

    public static final String notificationMuteNewPost = "notificationMuteNewPost";
    public static final String notificationMuteScheduled = "notificationMuteScheduled";

    public static final String loopPostId = "loopPostId";
    public static final String loopLeastPostId = "loopLeastPostId";


    public static final String luacherHasCreate = "luacherHasCreate";

    public static final String splashShowTime = "splashShowTime";

    public static final String splashShowIntro = "splashShowIntro";
    public static final String splashShowIntroVersion = "splashShowIntroVersion";

    public static final String configFollowCountBase = "followCountBase";

    /**
     * Score Key Name
     **/
    public static final String SCORE_DAY_COUNT = "dayCount";
    public static final String SCORE_SHARE_COUNT = "shareCount";
    public static final String SCORE_REFRESH_COUNT = "refreshCount";
    public static final String SCORE_POST_COUNT = "postCount";
    public static final String SCORE_HAS_SHOW = "hasShow";
    public static final String SCORE_HAS_SCORE = "hasScore";
    public static final String SCORE_CAN_SHOW = "canShow";
    public static final String SCORE_LAST_SHOW_TIME = "lastShowTime";

    public static final String SCORE_SHARE_SHOW_TIME = "shareButtonClickTimes";
    public static final String SCORE_DAYS_SHOW_TIME = "activeDays";
    public static final String SCORE_REFRESH_SHOW_TIME = "refreshTimes";

    // 记录用户的设置的comment的设置
    public static final String KEY_COMMENT_SORT = "commentKey";


    public interface SPDataListener {

        void onSPDataChanged(String spTypeName, String spKeyName);

    }

    private static class SpManagerHolder {
        public static SpManager instance = new SpManager();
    }

    private SpManager() {
    }

    public static SpManager getInstance() {
        return SpManagerHolder.instance;
    }

    public void register(SPDataListener listener) {
        super.register(listener);
    }

    public void unregister(SPDataListener listener) {
        super.unregister(listener);
    }

    public void clear(Context context) {
        NotificationSp.removeSpByKey(likeCountKeyName, context);
        NotificationSp.removeSpByKey(commentCountKeyName, context);
        NotificationSp.removeSpByKey(mentionCountKeyName, context);
        NotificationSp.removeSpByKey(mentionStampKeyName, context);
        NotificationSp.removeSpByKey(commentStampKeyName, context);
        NotificationSp.removeSpByKey(likeStampKeyName, context);
        ImSp.removeMessagesByKey(imMessagesStampKeyName, context);
    }


    public static class Setting {

        public static String getCurrentMenuLanguageType(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getString(settingMenuLanguageTypeKeyName, "");
        }

        public static void setCurrentMenuLanguageType(String languageType, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(settingMenuLanguageTypeKeyName, languageType);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, settingMenuLanguageTypeKeyName);
        }

        public static String getCurrentContentLanguageType(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getString(settingContentLanguageTypeKeyName, "");
        }

        public static void setCurrentContentLanguageType(String languageType, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(settingContentLanguageTypeKeyName, languageType);
            editor.apply();
        }


        public static Set<String> getCurrentSubLanguageTypeList(Context context) {
            Set<String> emptySet = new HashSet<>();
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getStringSet(settingSubLanguageTypesKeyName, emptySet);
        }

        public static void setCurrentSubLanguageTypeList(Set<String> subLanguageTypeList, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putStringSet(settingSubLanguageTypesKeyName, subLanguageTypeList);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, settingSubLanguageTypesKeyName);
        }

        public static String getUpgradedShowTime(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getString(settingUpgradeShowTime, "");
        }

        public static void setUpgradedShowTime(String upgradedTime, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(settingUpgradeShowTime, upgradedTime);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, settingUpgradeShowTime);
        }

        public static boolean getCurrentUpgraded(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(settingUpgradedKeyName, false);
        }

        public static void setCurrentUpgraded(boolean upgraded, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(settingUpgradedKeyName, upgraded);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, settingUpgradedKeyName);
        }

        public static boolean getCurrentForceUpgraded(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(settingUpgradedForceKeyName, false);
        }

        public static void setCurrentForceUpgraded(boolean upgraded, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(settingUpgradedForceKeyName, upgraded);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, settingUpgradedForceKeyName);
        }

        public static boolean getCurrentMobileModelSetting(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(settingMobileModel, false);
        }

        public static void setCurrentMobileModelSetting(boolean mobileModel, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(settingMobileModel, mobileModel);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, settingMobileModel);
        }

        public static boolean getSettingMobileContactsModel(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(settingMobileContactsModel, true);
        }

        public static void setMobileContactsModel(boolean contactsModel, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(settingMobileContactsModel, contactsModel);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, settingMobileModel);
        }

        public static boolean getSettingFollowByContactsModel(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(settingFollowByContactsModel, true);
        }

        public static void setFollowByContactsModel(boolean followByContactsModel, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(settingFollowByContactsModel, followByContactsModel);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, settingMobileModel);
        }

        public static void setNotificationStartTime(String startTime, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(notificationStartTime, startTime);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationStartTime);

        }

        public static String getNotificationStartTime(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getString(notificationStartTime, "22:00");
        }

        public static void setNotificationEndTime(String endTime, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(notificationEndTime, endTime);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationEndTime);

        }

        public static String getNotificationEndTime(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getString(notificationEndTime, "07:00");
        }

        ////////////////

        public static void setNotificationSelfStart(boolean selfstart, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("self_start", selfstart);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteAt);

        }

        public static boolean getNotificationSelfStart(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean("self_start", false);
        }

        public static void setNotificationSelfStartCount(int count, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("self_start_count", count);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteAt);

        }

        public static int getNotificationSelfStartCount(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getInt("self_start_count", 0);
        }

        public static Long getNotificationSelfStartTime(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getLong("self_start_time", 0);
        }

        public static void setNotificationSelfStartTime(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong("self_start_time", System.currentTimeMillis());
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteAt);

        }

        public static void setNotificationMuteAt(boolean muteAt, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(notificationMuteAt, muteAt);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteAt);

        }

        public static boolean getNotificationMuteAt(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(notificationMuteAt, true);
        }

        public static void setNotificationMuteComment(boolean muteComment, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(notificationMuteComment, muteComment);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteComment);

        }

        public static boolean getNotificationMuteComment(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(notificationMuteComment, true);
        }

        public static void setNotificationMuteLike(boolean muteLike, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(notificationMuteLike, muteLike);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteLike);

        }

        public static boolean getNotificationMuteLike(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(notificationMuteLike, true);
        }

        public static void setNotificationMuteIm(boolean muteIm, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(notificationMuteIm, muteIm);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteIm);

        }

        public static boolean getNotificationMuteIm(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(notificationMuteIm, true);
        }

        public static void setNotificationMuteStangerIm(boolean muteIm, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(notificationMuteStrangerIm, muteIm);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteStrangerIm);

        }

        public static boolean getNotificationMuteStagnerIm(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(notificationMuteStrangerIm, true);
        }

        public static void setNotificationMuteFollow(boolean muteIm, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(notificationMuteFollow, muteIm);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteFollow);

        }

        public static boolean getNotificationMuteFollow(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(notificationMuteFollow, true);
        }

        public static void setNotificationMuteNewPost(boolean muteNewPost, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(notificationMuteNewPost, muteNewPost);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteNewPost);

        }

        public static boolean getNotificationMuteNewPost(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(notificationMuteNewPost, true);
        }

        public static void setNotificationMuteScheduled(boolean muteScheduled, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(notificationMuteScheduled, muteScheduled);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesSettingName, notificationMuteScheduled);

        }

        public static boolean getNotificationMuteScheduled(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(notificationMuteScheduled, false);
        }

        public static void setShortcutHasCreate(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(luacherHasCreate, true);
            editor.apply();
        }

        public static boolean getShrotcutHasCreate(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(luacherHasCreate, false);
        }


        public static void setSplashShowTime(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(splashShowTime, System.currentTimeMillis());
            editor.apply();
        }

        public static long getSplashShowTime(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getLong(splashShowTime, 0);
        }

        public static void setSplashIsShowIntroduction(Context context, boolean isShow) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(splashShowIntro, isShow);
            editor.apply();
        }

        public static boolean getSplashIsShowIntroduction(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getBoolean(splashShowIntro, false);
        }

        public static void setSplashIsShowIntroductionVersion(Context context, String version) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(splashShowIntroVersion, version);
            editor.apply();
        }

        public static String getSplashIsShowIntroductionVersion(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getString(splashShowIntroVersion, "");
        }


        public static void setCommentSortValue(Context context, int commentSortValue) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(KEY_COMMENT_SORT, commentSortValue);
            editor.apply();
        }

        public static int getCommentSortValue(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesSettingName, Activity.MODE_PRIVATE);
            return settings.getInt(KEY_COMMENT_SORT, -1);
        }


    }

    public static class NotificationSp {

        public static void removeSpByKey(String key, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesNotificationName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(key);
            editor.apply();
        }

        public static String getPushNotificationToken(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesNotificationName, Activity.MODE_PRIVATE);
            return settings.getString(pushNotificationTokenKeyName, "");
        }

        public static void setPushNotificationToken(String token, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesNotificationName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(pushNotificationTokenKeyName, token);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesNotificationName, pushNotificationTokenKeyName);
        }

        public static int getNotificationCountById(Context context, String notifyID) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesNotificationCount, Activity.MODE_PRIVATE);
            return settings.getInt(notifyID, 0);
        }

        public static void setNotificationCountById(String notifyID, int count, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesNotificationCount, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(notifyID, count);
            editor.apply();
        }

        public static void resetNotificationCount(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesNotificationCount, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.apply();
        }

    }

    public static class GooglePlayScoreSp {

        public static void setLoginDayCount(Context context, int count) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = scoreSp.edit();

            editor.putInt(SCORE_DAY_COUNT, count);

            editor.apply();

        }

        public static int getLoginDayCount(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getInt(SCORE_DAY_COUNT, 0);

        }

        public static void setShareCount(Context context, int count) {

            SharedPreferences settings = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();

            editor.putInt(SCORE_SHARE_COUNT, count);

            editor.apply();

        }

        public static int getShareCount(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getInt(SCORE_SHARE_COUNT, 0);

        }

        public static void setRefreshCount(Context context, int count) {

            SharedPreferences settings = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();

            editor.putInt(SCORE_REFRESH_COUNT, count);

            editor.apply();

        }

        public static int getRefreshCount(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getInt(SCORE_REFRESH_COUNT, 0);

        }

        public static void setPostCount(Context context, int count) {

            SharedPreferences settings = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();

            editor.putInt(SCORE_POST_COUNT, count);

            editor.apply();

        }

        public static int getPostCount(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getInt(SCORE_POST_COUNT, 0);
        }

        public static void setIsShowScore(Context context, boolean isShow) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = scoreSp.edit();

            editor.putBoolean(SCORE_HAS_SHOW, isShow);

            editor.apply();

        }

        public static boolean getIsShowScore(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getBoolean(SCORE_HAS_SHOW, false);

        }

        public static void setIsScore(Context context, boolean isScore) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = scoreSp.edit();

            editor.putBoolean(SCORE_HAS_SCORE, isScore);

            editor.apply();

        }

        public static boolean getIsScore(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getBoolean(SCORE_HAS_SCORE, false);

        }

        public static void setShareTimes(Context context, int count) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = scoreSp.edit();

            editor.putInt(SCORE_SHARE_SHOW_TIME, count);

            editor.apply();

        }

        public static int getShareTimes(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getInt(SCORE_SHARE_SHOW_TIME, 1);

        }

        public static void setActiveTimes(Context context, int count) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = scoreSp.edit();

            editor.putInt(SCORE_DAYS_SHOW_TIME, count);

            editor.apply();

        }

        public static int getActiveTimes(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getInt(SCORE_DAYS_SHOW_TIME, 2);

        }

        public static void setRefreshTimes(Context context, int count) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = scoreSp.edit();

            editor.putInt(SCORE_REFRESH_SHOW_TIME, count);

            editor.apply();

        }

        public static int getRefreshTimes(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getInt(SCORE_REFRESH_SHOW_TIME, 3);

        }


        public static void setCanShowScore(Context context, boolean canShow) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = scoreSp.edit();

            editor.putBoolean(SCORE_CAN_SHOW, canShow);

            editor.apply();

        }

        public static boolean getCanShowScore(Context context) {

            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getBoolean(SCORE_CAN_SHOW, false);
        }

        public static void setLastShowTime(Context context, Long time) {
            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = scoreSp.edit();

            editor.putLong(SCORE_LAST_SHOW_TIME, time);

            editor.apply();
        }

        public static long getLastShowTime(Context context) {
            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);

            return scoreSp.getLong(SCORE_LAST_SHOW_TIME, 0);

        }

        public static void resetCount(Context context) {
            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = scoreSp.edit();
            editor.clear();
            editor.apply();
        }

        public static void resetDayCount(Context context) {
            SharedPreferences scoreSp = context.getSharedPreferences(sharePreferencesScore, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = scoreSp.edit();
            editor.putInt(SCORE_SHARE_COUNT, 0);
            editor.putInt(SCORE_POST_COUNT, 0);
            editor.putInt(SCORE_REFRESH_COUNT, 0);
            editor.putBoolean(SCORE_HAS_SHOW, false);
            editor.apply();
        }


    }

    public static class Config {

        public static int getFollowCountBase(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesConfigName, Activity.MODE_PRIVATE);
            return settings.getInt(configFollowCountBase, 20);
        }

        public static void setFollowCountBase(Context context, int followCountBase) {
            SharedPreferences config = context.getSharedPreferences(sharePreferencesConfigName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = config.edit();
            editor.putInt(configFollowCountBase, followCountBase);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesConfigName, configFollowCountBase);
        }
    }

    public static class LoopPostIdSp {


        public static String getLooperPostId(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesPostIdName, Activity.MODE_PRIVATE);
            return settings.getString(loopPostId, "");
        }

        public static void setLooperPostId(String postId, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesPostIdName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(loopPostId, postId);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesPostIdName, loopPostId);
        }

        public static String getLooperLeastPostId(Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesPostIdName, Activity.MODE_PRIVATE);
            return settings.getString(loopLeastPostId, "");
        }

        public static void setLooperLeastPostId(String postId, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesPostIdName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(loopLeastPostId, postId);
            editor.apply();
            SpManager.getInstance().broadcast(sharePreferencesPostIdName, loopPostId);
        }
    }


    @Deprecated
    public static class ImSp {

        public static void removeMessagesByKey(String key, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesIMName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(key);
            editor.apply();
        }

        public static boolean hasKey(String key, Context context) {
            SharedPreferences settings = context.getSharedPreferences(sharePreferencesIMName, Activity.MODE_PRIVATE);
            return settings.contains(key);
        }

    }

    private void broadcast(final String spTypeName, final String spKeyName) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof SPDataListener) {
                            SPDataListener listener = (SPDataListener) l;
                            listener.onSPDataChanged(spTypeName, spKeyName);
                        }
                    }
                }
            }

        });
    }

}
