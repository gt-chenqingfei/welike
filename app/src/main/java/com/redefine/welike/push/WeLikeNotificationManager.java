package com.redefine.welike.push;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.commonui.fresco.oss.LoadPicCallback;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.shortcutbadger.ShortcutBadger;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.util.LifecycleHandler;
import com.redefine.welike.common.TempUtil;
import com.redefine.welike.commonui.activity.SchemeFilterActivity;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.PushEventManager;

import java.util.Map;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;


/**
 * Created by mengnan on 2018/5/2.
 **/

public class WeLikeNotificationManager implements LoadPicCallback {
    // TODO: 2018/5/9 for lizard

    private static final String TAG = "lizard";
    private static final int Follow = 0;
    private static final int UnFollow = 1;
    private static final int Forward = 2;
    private static final int PostMention = 3;
    private static final int CommentMention = 4;
    private static final int ReplyMention = 5;
    public static final int Comment = 6;
    private static final int Reply = 7;
    private static final int PostLike = 8;
    private static final int CommentLike = 9;
    private static final int ReplyLike = 10;
    private static final int SuperLike = 11;
    private static final int ForwardComment = 12;
    private static final int LinkHttp = 13;
    private static final int FollowerPost = 14;

    private static final int MERGE_COMMENT = 51;
    private static final int MERGE_LIKE = 52;
    private static final int MERGE_FOLLOW = 53;
    private static final int MERGE_MEMTIION = 54;
    private static final int MERGE_NEW_POST = 55;

    private static final int TEXT_Message = 100;
    private static final int PIC_Message = 101;
    private static final int Strange_Text_Message = 102;
    private static final int Strange_Pic_Message = 103;

    private static final int SMALL_PIC_BUSINESS = 201;
    private static final int BIG_PIC_BUSINESS = 202;
    private static final int BIG_TEXT_BUSINESS = 203;


    //unknow type
    public static final int Unknown_NewsType = 300;

    private boolean mAtSwitch, mCommentSwitch, mLikeSwitch, mImSwitch, mNewPostSwitch, mSheduledSwitch, mFollowSwitch;

    private String muteStartTime, muteEndTime;


    private Map<String, String> mData = null;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext = MyApplication.getAppContext();

    @SuppressLint("StaticFieldLeak")
    private volatile static WeLikeNotificationManager mNotificationManager;

    private NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


    private WeLikeNotificationManager() {
    }

    public static WeLikeNotificationManager getNotificationManager() {

        if (null == mNotificationManager) {

            synchronized (WeLikeNotificationManager.class) {
                if (null == mNotificationManager) {
                    mNotificationManager = new WeLikeNotificationManager();
                }

            }
        }

        return mNotificationManager;
    }


    public void checkEnv() {
        if (checkData()) {
            judgeForeGround();
        }

    }

    private boolean checkData() {
        if (null == mData.get("pushType") || null == mData.get("id") || null == mData.get("forwardUrl")) {
            EventLog.Push.report2(Unknown_NewsType, mData.get("id"), 3);
            PushEventManager.onPushDisplay(Unknown_NewsType, mData.get("id"), EventLog1.Push.Result.FOREGROUND_NOT_POP, mData.get("businessType"));
            return false;
        }
        EventLog.Push.report1(Integer.parseInt(mData.get("pushType")), mData.get("id"));
        PushEventManager.onPushArrive(Integer.parseInt(mData.get("pushType")), mData.get("id"), mData.get("businessType"));

        if (Integer.parseInt(mData.get("pushType")) == UnFollow) {
            EventLog.Push.report2(Integer.parseInt(mData.get("pushType")), mData.get("id"), 4);
            PushEventManager.onPushDisplay(Integer.parseInt(mData.get("pushType")), mData.get("id"), EventLog1.Push.Result.UNFOLLOW_NOT_POP, mData.get("businessType"));
            return false;
        }

        initSwitch();
        return checkMute(mData.get("pushType"));
    }

    private void initSwitch() {
        mAtSwitch = SpManager.Setting.getNotificationMuteAt(mContext);
        mCommentSwitch = SpManager.Setting.getNotificationMuteComment(mContext);
        mLikeSwitch = SpManager.Setting.getNotificationMuteLike(mContext);
        mImSwitch = SpManager.Setting.getNotificationMuteIm(mContext);
        mNewPostSwitch = SpManager.Setting.getNotificationMuteNewPost(mContext);
        mFollowSwitch = SpManager.Setting.getNotificationMuteFollow(mContext);
        mSheduledSwitch = SpManager.Setting.getNotificationMuteScheduled(mContext);
        muteStartTime = SpManager.Setting.getNotificationStartTime(mContext);
        muteEndTime = SpManager.Setting.getNotificationEndTime(mContext);

    }

    private boolean checkMute(String type) {
        int nType = Integer.parseInt(type);

        if (mSheduledSwitch) {

            if (judgeMuteTime()) {
                return false;
            } else {
                return judgeSwitch(nType);

            }

        }

        return judgeSwitch(nType);
    }

    private boolean judgeSwitch(int nType) {
        if (!mAtSwitch && (nType == Forward || nType == PostMention || nType == CommentMention || nType == ReplyMention)) {
            return false;

        }
        if (!mCommentSwitch && (nType == Comment || nType == Reply)) {
            return false;

        }
        if (!mLikeSwitch && (nType == PostLike || nType == CommentLike || nType == ReplyLike || nType == SuperLike)) {

            return false;

        }

        if (!mImSwitch && (nType == TEXT_Message || nType == PIC_Message)) {
            return false;
        }

        if (!mNewPostSwitch && nType == FollowerPost) {
            return false;
        }

        if (!mFollowSwitch && nType == Follow) {
            return false;
        }
        return true;
    }

    private boolean judgeMuteTime() {
        if (null != muteStartTime && null != muteEndTime) {
            boolean isMute = new TempUtil().test(System.currentTimeMillis(), muteStartTime, muteEndTime);
            // return !isEffectiveDate(nowDate, startTime, endTime);
            return isMute;

        }
        return true;

    }

    private void judgeForeGround() {
        if (LifecycleHandler.isApplicationInForeground()) {
            EventLog.Push.report2(Integer.parseInt(mData.get("pushType")), mData.get("id"), 2);
            PushEventManager.onPushDisplay(Integer.parseInt(mData.get("pushType")), mData.get("id"), EventLog1.Push.Result.FOREGROUND_NOT_POP, mData.get("businessType"));

        } else {
            getNotificationPic();

        }


    }

    private void getNotificationPic() {

        if (isBussinessType()) {

            if (Integer.parseInt(mData.get("pushType")) == BIG_PIC_BUSINESS) {
                getBusinessImage(mData.get("businessUrl"), Integer.parseInt(mData.get("pushType")));
            } else if (Integer.parseInt(mData.get("pushType")) == SMALL_PIC_BUSINESS) {
                getHeadImage(mData.get("businessUrl"), Integer.parseInt(mData.get("pushType")));
            } else {
                getHeadImage(mData.get("businessUrl"), Integer.parseInt(mData.get("pushType")));
            }


        } else {
            if (null != mData.get("url")) {
                getHeadImage(mData.get("url"), Integer.parseInt(mData.get("pushType")));
            } else {
                Resources res = mContext.getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res, com.redefine.commonui.R.drawable.user_default_head);
                makeNotification(bitmap);

            }

        }
    }

    private boolean isBussinessType() {
        return (Integer.parseInt(mData.get("pushType")) == SMALL_PIC_BUSINESS || Integer.parseInt(mData.get("pushType")) == BIG_PIC_BUSINESS || Integer.parseInt(mData.get("pushType")) == BIG_TEXT_BUSINESS);

    }

    private boolean isMergeType() {
        return (Integer.parseInt(mData.get("pushType")) == MERGE_COMMENT || Integer.parseInt(mData.get("pushType")) == MERGE_LIKE || Integer.parseInt(mData.get("pushType")) == MERGE_FOLLOW
                || Integer.parseInt(mData.get("pushType")) == MERGE_MEMTIION || Integer.parseInt(mData.get("pushType")) == MERGE_NEW_POST);

    }

    private boolean isNormalType() {
        return (Integer.parseInt(mData.get("pushType")) == Follow || Integer.parseInt(mData.get("pushType")) == Forward || Integer.parseInt(mData.get("pushType")) == PostMention
                || Integer.parseInt(mData.get("pushType")) == CommentMention || Integer.parseInt(mData.get("pushType")) == ReplyMention || Integer.parseInt(mData.get("pushType")) == Comment
                || Integer.parseInt(mData.get("pushType")) == Reply || Integer.parseInt(mData.get("pushType")) == PostLike || Integer.parseInt(mData.get("pushType")) == CommentLike
                || Integer.parseInt(mData.get("pushType")) == ReplyLike || Integer.parseInt(mData.get("pushType")) == SuperLike || Integer.parseInt(mData.get("pushType")) == ForwardComment
                || Integer.parseInt(mData.get("pushType")) == FollowerPost || Integer.parseInt(mData.get("pushType")) == TEXT_Message || Integer.parseInt(mData.get("pushType")) == PIC_Message
                || Integer.parseInt(mData.get("pushType")) == Strange_Text_Message || Integer.parseInt(mData.get("pushType")) == Strange_Pic_Message);

    }


    private void makeNotification(Bitmap bitmap) {

        String forwordUrl = mData.get("forwardUrl");
        String userName = mData.get("username");
        String pushType = mData.get("pushType");

        if (TextUtils.isEmpty(userName)) {
            userName = "";
        }
        if (TextUtils.isEmpty(forwordUrl)) {
            forwordUrl = "";
        }


        String notificationText = null;

        SpannableStringBuilder notificationTitle = null;

        if (isBussinessType()) {
            notificationTitle = new SpannableStringBuilder(mData.get("businessTitle"));
            notificationText = mData.get("businessContent");

        } else if (isMergeType()) {

            String content = mData.get("title");
            if (!TextUtils.isEmpty(content)) {
                String[] strs = content.split("people");
                if (strs.length > 1) {
                    notificationTitle = new SpannableStringBuilder(strs[0] + "people");
                    notificationText = strs[1];

                } else {
                    notificationTitle = new SpannableStringBuilder(content);
                }

            }

        } else {
            notificationText = getNotificationContentText(Integer.parseInt(pushType));
            if (userName.length() > 15) {
                notificationTitle = new SpannableStringBuilder("@" + userName);
                notificationTitle.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.common_color_2c97ed)), 0, userName.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                notificationText = getNotificationType(Integer.parseInt(pushType));
            } else {
                notificationTitle = new SpannableStringBuilder("@" + userName + " " + getNotificationType(Integer.parseInt(pushType)));
                notificationTitle.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.common_color_2c97ed)), 0, userName.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }


        int notifyId = getNotificationId(pushType);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, notifyId, makeJumpIntent(forwordUrl), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            notificationBuilder =
                    new NotificationCompat.Builder(mContext, "welike");
        } else {
            notificationBuilder =
                    new NotificationCompat.Builder(mContext);

        }

        notificationBuilder
                .setSmallIcon(R.drawable.welike_logo)
                .setPriority(PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);

        if (isBussinessType()) {
            if (Integer.parseInt(pushType) == SMALL_PIC_BUSINESS) {
                notificationBuilder.setLargeIcon(bitmap);
            }
            if (Integer.parseInt(pushType) == BIG_PIC_BUSINESS) {
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
            }
            if (Integer.parseInt(pushType) == BIG_TEXT_BUSINESS) {
                notificationBuilder.setLargeIcon(bitmap);
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText));
            }

        } else {
            notificationBuilder.setLargeIcon(bitmap);
        }

        if (isBussinessType() || isMergeType() || isNormalType()) {
            sendNotification(notificationBuilder.build(), notifyId);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel("welike", "welike_push", NotificationManager.IMPORTANCE_HIGH);
        mChannel.enableLights(true);
        mChannel.enableVibration(true);
        mChannel.setLightColor(Color.RED);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        mChannel.setShowBadge(true);
        mChannel.setBypassDnd(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400});
        notificationManager.createNotificationChannel(mChannel);


    }

    private void sendNotification(Notification notification, int notifyId) {
        EventLog.Push.report2(Integer.parseInt(mData.get("pushType")), mData.get("id"), 1);
        PushEventManager.onPushDisplay(Integer.parseInt(mData.get("pushType")), mData.get("id"), EventLog1.Push.Result.POP, mData.get("businessType"));
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            try {

                int count = SpManager.NotificationSp.getNotificationCountById(mContext, "no" + notifyId);

                ShortcutBadger.applyNotification(mContext, notification, ++count);

                SpManager.NotificationSp.setNotificationCountById("no" + notifyId, count, mContext);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int count = SpManager.NotificationSp.getNotificationCountById(mContext, "no");
            ShortcutBadger.applyCount(mContext, ++count);
            SpManager.NotificationSp.setNotificationCountById("no", count, mContext);
        }

        if (null != notificationManager) {
            notificationManager.notify(notifyId, notification);
        }

    }

    private void getHeadImage(String url, int type) {

        HeadUrlLoader.getInstance().LoadPicFromNet(mContext, url, 200, 200, this, type);

    }

    private void getBusinessImage(String url, int type) {

        HeadUrlLoader.getInstance().LoadPicFromNet(mContext, url, ScreenUtils.getSreenWidth(mContext), ScreenUtils.getSreenWidth(mContext) / 2, this, type);

    }

    private int getNotificationId(String pushType) {
        int notifyId = 0;
        if (Integer.parseInt(pushType) > 0) {
            notifyId = Integer.parseInt(pushType);
            if (notifyId == CommentMention || notifyId == ReplyMention) {
                //目前3种通知合并为一种通知
                notifyId = PostMention;
            }
        }

        return notifyId;
    }

    private String getNotificationType(int type) {
        String typeName = "";
        switch (type) {
            case Follow:
                typeName = ResourceTool.getString("follow_you");
                break;
            case UnFollow:
                break;
            case Forward:
                typeName = ResourceTool.getString( "message_comment_forward_text");
                break;
            case PostMention:
                typeName = ResourceTool.getString( "message_comment_mentioned_you");
                break;
            case CommentMention:
                typeName = ResourceTool.getString( "message_comment_mentioned_you");
                break;
            case ReplyMention:
                typeName = ResourceTool.getString( "message_comment_mentioned_you");
                break;
            case Comment:
                typeName = ResourceTool.getString( "message_comment_commented_you_post_text");
                break;
            case Reply:
                typeName = ResourceTool.getString( "message_comment_reply_you_text");
                break;
            case PostLike:
                typeName = ResourceTool.getString( "message_comment_liked_your_post_text");
                break;
            case CommentLike:
                typeName = ResourceTool.getString( "message_comment_liked_your_comment_text");
                break;
            case ReplyLike:
                typeName = ResourceTool.getString( "message_comment_liked_your_reply_text");
                break;
            case SuperLike:
                break;
            case ForwardComment:
                typeName = ResourceTool.getString( "message_comment_forward_comment");
                break;
            case FollowerPost:
                typeName = ResourceTool.getString( "follower_new_post");
                break;
            case TEXT_Message:
                typeName = ResourceTool.getString( "chat_sent_a_message");
                break;
            case PIC_Message:
                typeName = ResourceTool.getString( "chat_sent_a_message");
                break;
            case Strange_Text_Message:
                typeName = ResourceTool.getString( "chat_sent_a_message");
                break;
            case Strange_Pic_Message:
                typeName = ResourceTool.getString( "chat_sent_a_message");
                break;

            case Unknown_NewsType:
                break;


        }
        return typeName;
    }

    private String getNotificationContentText(int type) {
        String text = "";
        if (type == TEXT_Message || type == FollowerPost || type == Strange_Text_Message) {
            text = mData.get("content");
        } else if (type == PIC_Message || type == Strange_Pic_Message) {
            text = ResourceTool.getString( "im_session_pic_message");
        }
        return text;
    }

    private Intent makeJumpIntent(String jumpUrl) {
        Uri uri = Uri.parse(jumpUrl);
        Intent jumpIntent = new Intent(mContext, SchemeFilterActivity.class);
        jumpIntent.setData(uri);
        jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        Intent transmitIntent = new Intent(mContext, TransitBroadcastReceiver.class);
        transmitIntent.putExtra("jumpIntent", jumpIntent);
        transmitIntent.putExtra("pushId", mData.get("id"));
        transmitIntent.putExtra("pushType", mData.get("pushType"));
        transmitIntent.putExtra("businessType", mData.get("businessType"));
        transmitIntent.putExtra("schemeUrl", mData.get("forwardUrl"));


        return transmitIntent;

    }


    public void setData(Map<String, String> data) {
        mData = data;
    }

    @Override
    public void callback(Bitmap bitmap) {
        makeNotification(bitmap);
    }


}

