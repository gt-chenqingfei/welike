package com.redefine.multimedia.player;

import android.os.SystemClock;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.LogEvent;
import com.redefine.welike.base.track.LogEventConstant;
import com.redefine.welike.base.track.TrackerConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class VideoStateHelper {

    public static final int LOG_TYPE_VIDEO_END = 1;
    public static final int LOG_TYPE_DESTROY_PAGE = 2;
    public static final int LOG_TYPE_SWITCH = 3;
    /**
     * log begin
     */
    public long mPlayDuration;
    public long mDurationTime;
    public String mPostUid;
    public String mRootPostUid;
    public String mPostId;
    public String mRootPostId;
    public String mPostLa;
    public String[] mPostTags;
    public String mRootPostLa;
    public String[] mRootPostTags;
    public String mPostUserHeader;
    public String mPostUserNick;
    public String mSequenceId;
    public boolean mIsCache;
    public long mStartTime;
    public long mPreparedTime;
    public long mAudioFirstFrame;
    public long mVideoFirstFrame;
    public long mEndTime;
    public StringBuilder mVideoFrameRate = new StringBuilder();
    public int mVideoFrameRateCount = 0;
    public int  mBufferedCount;
    public long mBufferedTime;
    public int mSeekCount;
    public String mVideoPath;

    public int mPlayerType;

    public long mBufferedStartTime;
    public long baseTime;
    public long videoBaseTime;
    public String mPlayerSource;
    public int mVideoSource;
    public int mOpenType = 1;
    public String mStrategy;
    public boolean mMuteType;
    public JSONObject mPlayClick = new JSONObject();

    /**
     * log end
     */


    public VideoStateHelper() {
        baseTime = SystemClock.elapsedRealtime();
        videoBaseTime = SystemClock.elapsedRealtime();
    }

    public void appendPlayAction(VideoPlayAction action) {
        if (action == null) {
            return;
        }
        int value = mPlayClick.getIntValue(String.valueOf(action.getValue()));
        value++;
        mPlayClick.put(String.valueOf(action.getValue()), value);
    }

    /**
     * 发送事件
     * @param eventType 1，视频播放完成；2，退出播放页面
     */
    public void postLog(int eventType) {
        if (TextUtils.isEmpty(mRootPostId)) {
            return ;
        }
        if (eventType == LOG_TYPE_VIDEO_END && SystemClock.elapsedRealtime() - videoBaseTime <= 200) {
            return;
        }

        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_WATCH_VIDEO);
        Map<String, String> map = new HashMap<>();
        map.put(PlayerConstant.LOG_POST_ID, mPostId);
        map.put(PlayerConstant.LOG_POST_UID, mPostUid);
        map.put(PlayerConstant.LOG_ROOTPOST_ID, mRootPostId);
        map.put(PlayerConstant.LOG_ROOTPOST_UID, mRootPostUid);
        map.put(PlayerConstant.LOG_SEQUENCE_ID, mSequenceId);
        map.put(PlayerConstant.LOG_PLAY_DURATION, String.valueOf(mPlayDuration));
        map.put(PlayerConstant.LOG_DURATION_TIME, String.valueOf(mDurationTime));
        map.put(PlayerConstant.LOG_URL, mVideoPath);
//        map.put(PlayerConstant.LOG_IS_LOCAL, String.valueOf(mIsCache ? 1 : 0));
        map.put(PlayerConstant.LOG_START_TIME, String.valueOf(mStartTime));
        map.put(PlayerConstant.LOG_VIDEO_TYPE, String.valueOf(mVideoSource));
        map.put(PlayerConstant.LOG_PLAY_TYPE, String.valueOf(mPlayerType));
        map.put(PlayerConstant.LOG_PREPARED_TIME, String.valueOf(mPreparedTime));
        map.put(PlayerConstant.LOG_FIRST_AUDIO_TIME, String.valueOf(mAudioFirstFrame));
        map.put(PlayerConstant.LOG_FIRST_VIDEO_TIME, String.valueOf(mVideoFirstFrame));
        map.put(PlayerConstant.LOG_END_TIME, String.valueOf(mEndTime));
//        map.put(PlayerConstant.LOG_VIDEO_FRAMERATE, mVideoFrameRate.toString());
        map.put(PlayerConstant.LOG_BUFFERED_COUNT, String.valueOf(mBufferedCount));
        map.put(PlayerConstant.LOG_BUFFERED_TIME, String.valueOf(mBufferedTime));
        map.put(PlayerConstant.LOG_SEEK_COUNT, String.valueOf(mSeekCount));
        map.put(PlayerConstant.LOG_PLAY_SOURCE, mPlayerSource);
        map.put(PlayerConstant.LOG_OPEN_TYPE, String.valueOf(mOpenType));
        map.put(PlayerConstant.LOG_STRATEGY, mStrategy);
        map.put(PlayerConstant.LOG_POST_LANGUAGE, mPostLa);
        map.put(PlayerConstant.LOG_POST_TAGS, mPostTags == null ? null : toStringArrayString(mPostTags));
        map.put(PlayerConstant.LOG_ROOT_POST_LANGUAGE, mRootPostLa);
        map.put(PlayerConstant.LOG_ROOT_POST_TAGS, mRootPostTags == null ? null : toStringArrayString(mRootPostTags));
        map.put(PlayerConstant.LOG_MUTE_TYPE, mMuteType ? "1" : "2");
        map.put(PlayerConstant.LOG_PLAY_ACTION, mPlayClick.toJSONString());

        LogEvent logEvent = new LogEvent();
        logEvent.id = LogEventConstant.LOG_EVENT_VIDEO_PLAYER;
        logEvent.logs = map;
        EventBus.getDefault().post(logEvent);

        if(eventType == LOG_TYPE_VIDEO_END) {
            AFGAEventManager.getInstance().sendEventWithLabel(TrackerConstant.EVENT_PLAY_BUMBER, convertVideoSource(mVideoSource));
            AFGAEventManager.getInstance().sendEventWithLabel(TrackerConstant.EVENT_PLAYALL_NUMBER, convertVideoSource(mVideoSource));
        } else if (eventType == LOG_TYPE_DESTROY_PAGE) {
            AFGAEventManager.getInstance().sendEventWithLabel(TrackerConstant.EVENT_PLAY_BUMBER, convertVideoSource(mVideoSource));
        }
    }

    public void postEvent5() {
        if (SystemClock.elapsedRealtime() - videoBaseTime <= 200) {
            return;
        }
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_WATCH_VIDEO);
        Map<String, String> map = new HashMap<>();
        map.put(PlayerConstant.LOG_POST_ID, mPostId);
        map.put(PlayerConstant.LOG_POST_UID, mPostUid);
        map.put(PlayerConstant.LOG_ROOTPOST_ID, mRootPostId);
        map.put(PlayerConstant.LOG_ROOTPOST_UID, mRootPostUid);
        map.put(PlayerConstant.LOG_SEQUENCE_ID, mSequenceId);
        map.put(PlayerConstant.LOG_PLAY_DURATION, String.valueOf(mPlayDuration));
        map.put(PlayerConstant.LOG_DURATION_TIME, String.valueOf(mDurationTime));
        map.put(PlayerConstant.LOG_FIRST_AUDIO_TIME, String.valueOf(mAudioFirstFrame));
        map.put(PlayerConstant.LOG_FIRST_VIDEO_TIME, String.valueOf(mVideoFirstFrame));
        map.put(PlayerConstant.LOG_PLAY_SOURCE, mPlayerSource);
        map.put(PlayerConstant.LOG_STRATEGY, mStrategy);
        map.put(PlayerConstant.LOG_POST_LANGUAGE, mPostLa);
        map.put(PlayerConstant.LOG_POST_TAGS, mPostTags == null ? null : toStringArrayString(mPostTags));
        map.put(PlayerConstant.LOG_ROOT_POST_LANGUAGE, mRootPostLa);
        map.put(PlayerConstant.LOG_ROOT_POST_TAGS, mRootPostTags == null ? null : toStringArrayString(mRootPostTags));
        map.put(PlayerConstant.LOG_MUTE_TYPE, mMuteType ? "1" : "2");
        map.put(PlayerConstant.LOG_PLAY_ACTION, mPlayClick.toJSONString());
        LogEvent logEvent = new LogEvent();
        logEvent.id = LogEventConstant.LOG_EVENT_VIDEO_AUTO_PLAY;
        logEvent.logs = map;
        EventBus.getDefault().post(logEvent);
    }

    public static void sendLog(int action) {
        LogEvent logEvent = new LogEvent();
        logEvent.id = action;
        EventBus.getDefault().post(logEvent);
    }

    private String convertVideoSource(int videoSource) {
        if(videoSource == 1) {
            return TrackerConstant.LABEL_VIDEOTYPE_YOUTUBE;
        } else {
            return TrackerConstant.LABEL_VIDEOTYPE_ORIGIN;
        }
    }

    public static String toStringArrayString(String[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (String arg : args) {
            sb.append(arg).append(",");
        }
        sb = new StringBuilder(sb.substring(0, sb.length() - 1));
        sb.append("]");
        return sb.toString();
    }

    public void resetLog(boolean resetBaseTime) {
        mPlayDuration = 0;
        mDurationTime = 0;
        mIsCache = false;
        mPreparedTime = 0;
        mStartTime = SystemClock.elapsedRealtime() - baseTime;
        mVideoFirstFrame = 0;
        mAudioFirstFrame = 0;
        mEndTime = 0;
        mVideoFrameRate = new StringBuilder();
        mVideoFrameRateCount = 0;
        mBufferedCount = 0;
        mBufferedTime = 0;
        mBufferedStartTime = 0;
        mSeekCount = 0;
        mOpenType = 4;
        videoBaseTime = SystemClock.elapsedRealtime();
        mPlayClick.clear();
        if (resetBaseTime) {
            baseTime = SystemClock.elapsedRealtime();
        }
    }

}
