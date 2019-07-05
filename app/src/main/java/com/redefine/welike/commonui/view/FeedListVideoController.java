package com.redefine.welike.commonui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.download.DownloadState;
import com.redefine.commonui.fresco.loader.VideoCoverUrlLoader;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.player.VideoPlayAction;
import com.redefine.multimedia.player.VideoStateHelper;
import com.redefine.multimedia.player.mediacontroller.IMediaController;
import com.redefine.multimedia.player.mediacontroller.MediaControllerInvoker;
import com.redefine.multimedia.player.mediaplayer.IMediaPlayerControl;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommand;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerCommandId;
import com.redefine.multimedia.player.mediaplayer.MediaPlayerInvoker;
import com.redefine.welike.R;
import com.redefine.welike.Switcher;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.ui.anko.FeedForwardDeleteItemViewUI;
import com.redefine.welike.business.feeds.ui.anko.FeedVideoControllerViewUI;
import com.redefine.welike.common.VideoListVolumeManager;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;

import org.jetbrains.anko.AnkoContext;

import java.lang.ref.WeakReference;

import kotlin.jvm.functions.Function0;

/**
 * Created by nianguowang on 2018/8/12
 */
public class FeedListVideoController extends ConstraintLayout implements IMediaController, MediaPlayerInvoker {

    private static final int SHOW_PROGRESS = 1;
    private static final long UPDATE_PROGRESS_DURATION = 200;

    private View mLoading;
    private View mPlayBtn;
    private SimpleDraweeView mCover;
    private TextView mPostContent;
    private ImageView mVoiceSwitch;
    private TextView tvOperation;

    private VideoHandler mHandler;
    private IMediaPlayerControl mMediaPlayController;
    private VideoStateHelper mStateHelper;

    private Function0 finishCallback;

    public FeedListVideoController(Context context) {
        super(context);
        init();
    }

    public FeedListVideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FeedListVideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setFinishCallback(Function0 finishCallback) {
        this.finishCallback = finishCallback;
    }

    private void init() {


//        LayoutInflater.from(getContext()).inflate(R.layout.feed_list_video_controller_layout, this, true);
        addView(new FeedVideoControllerViewUI().createView(AnkoContext.Companion.create(getContext(), this, false)));
        mLoading = findViewById(R.id.video_progress_bar);
        mLoading = findViewById(R.id.video_progress_bar);
        mPlayBtn = findViewById(R.id.video_feed_play_image);
        mCover = findViewById(R.id.video_cover);
        mPostContent = findViewById(R.id.video_post_content);
        mVoiceSwitch = findViewById(R.id.video_voice_switch);
        tvOperation = findViewById(R.id.tv_common_feed_operation);

        mVoiceSwitch.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(INVISIBLE);
        mPostContent.setVisibility(View.INVISIBLE);

        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(mCover.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setFailureImage(new ColorDrawable(Color.GRAY))
                .setPlaceholderImage(new ColorDrawable(Color.GRAY))
                .setRetryImage(new ColorDrawable(Color.GRAY))
                .build();
        mCover.setHierarchy(hierarchy);
        mHandler = new VideoHandler(this);
        mStateHelper = new VideoStateHelper();
    }

    public void setCoverUrl(View rootView, PostBase postBase, final VideoPost videoPost, int width, int height) {
        initStateHelper(postBase, videoPost);
        mPlayBtn.setVisibility(View.VISIBLE);
        mCover.setVisibility(View.VISIBLE);
        int marginTotalWidth = 0;
        if (postBase instanceof ForwardPost) {
            mPostContent.setBackground(getResources().getDrawable(R.drawable.video_bottom_forward_gradient));
            mPlayBtn.setBackground(getResources().getDrawable(R.drawable.drawable_shape_video_play_bg));
            mCover.getHierarchy().setRoundingParams(new RoundingParams().setCornersRadii(0, 0,
                    ScreenUtils.dip2Px(4),
                    ScreenUtils.dip2Px(4)));
            marginTotalWidth = ScreenUtils.dip2Px(24);
        } else {
            mPostContent.setBackground(getResources().getDrawable(R.drawable.video_bottom_gradient));
            mPlayBtn.setBackground(getResources().getDrawable(R.color.transparent_40));
            mCover.getHierarchy().setRoundingParams(RoundingParams.fromCornersRadius(0));
        }

        VideoCoverUrlLoader.getInstance().loadVideoThumbUrl(rootView, mCover, videoPost.getCoverUrl(),
                ABTest.INSTANCE.check(ABKeys.TEST_IMAGE_DISPLAY) == 1, width, height, marginTotalWidth);

        mVoiceSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean mute = VideoListVolumeManager.INSTANCE.toggleVoice();
                EventLog.VIDEO.report8(!mute ? EventConstants.VIDEO_MUTE_TYPE_VOICE_OFF : EventConstants.VIDEO_MUTE_TYPE_VOICE_ON,
                        videoPost.getPid(), videoPost.getUid(), videoPost.getLanguage(), videoPost.getTags());
                EventLog1.Video.report8(!mute ? EventConstants.VIDEO_MUTE_TYPE_VOICE_OFF : EventConstants.VIDEO_MUTE_TYPE_VOICE_ON,
                        videoPost.getPid(), videoPost.getUid(), videoPost.getLanguage(), videoPost.getTags());
                if (mMediaPlayController != null) {
                    mMediaPlayController.setMute(mute);
                }
                if (mute) {
                    mVoiceSwitch.setImageResource(R.drawable.video_voice_off);
                } else {
                    mVoiceSwitch.setImageResource(R.drawable.video_voice_on);
                }
                mStateHelper.appendPlayAction(VideoPlayAction.MUTE);
            }
        });

        if (Switcher.SHOW_FEED_INFO) {
            tvOperation.setVisibility(View.VISIBLE);
            StringBuilder buffer = new StringBuilder("Language=");
            buffer.append(postBase.getLanguage());
            buffer.append(";tags=[");
            if (postBase.getTags() != null) {
                for (String t : postBase.getTags()) {
                    buffer.append(t);
                    buffer.append(",");
                }
            }
            buffer.append("];Strategy=");
            buffer.append(postBase.getStrategy());
            buffer.append(";origin=");
            buffer.append(postBase.origin);
            buffer.append(";operationType=");
            buffer.append(postBase.getOperationType());
            buffer.append(";postId=");
            buffer.append(postBase.getPid());
            tvOperation.setText(buffer.toString());
        } else {
            tvOperation.setVisibility(View.GONE);
        }
    }

    public void setPostContent(final String content) {
        mPostContent.setVisibility(View.VISIBLE);
        mPostContent.setText(content);
        mPostContent.post(new Runnable() {
            @Override
            public void run() {
                if (mPostContent.getLineCount() > 2) {
                    String showContent;
                    try {
                        int lineEnd = mPostContent.getLayout().getLineEnd(1);
                        showContent = content.substring(0, lineEnd - 3) + GlobalConfig.AND_SO_ON;
                    } catch (Exception e) {
                        showContent = content;
                    }
                    mPostContent.setText(showContent);
                }
            }
        });
    }

    public void setPlaySource(String playSource) {
        mStateHelper.mPlayerSource = playSource;
    }

    public int getCurrentPosition() {
        if (mMediaPlayController != null) {
            return mMediaPlayController.getCurrentPosition();
        }
        return 0;
    }


    private void initStateHelper(PostBase postBase, VideoPost videoPost) {
        mStateHelper.mPostId = postBase.getPid();
        mStateHelper.mPostUid = postBase.getUid();
        mStateHelper.mStrategy = postBase.getStrategy();
        mStateHelper.mPostLa = postBase.getLanguage();
        mStateHelper.mPostTags = postBase.getTags();
        mStateHelper.mSequenceId = postBase.getSequenceId();
        if (postBase instanceof ForwardPost) {
            mStateHelper.mRootPostLa = videoPost.getLanguage();
            mStateHelper.mRootPostTags = videoPost.getTags();
            mStateHelper.mRootPostId = videoPost.getPid();
            mStateHelper.mRootPostUid = videoPost.getUid();
        }
    }

    @Override
    public void setMediaPlayerController(IMediaPlayerControl mediaPlayerController) {
        mMediaPlayController = mediaPlayerController;
    }

    @Override
    public void showLoading() {
        LogUtil.d("wng_", ">>FeedListVideoController showLoading");
        mLoading.setVisibility(View.VISIBLE);
        mPlayBtn.setVisibility(View.GONE);
        LogUtil.d("wng_", "<<FeedListVideoController showLoading");
    }

    @Override
    public void dismissLoading() {
        LogUtil.d("wng_", ">>FeedListVideoController dismissLoading");
        mLoading.setVisibility(View.GONE);
        LogUtil.d("wng_", "<<FeedListVideoController dismissLoading");
    }

    @Override
    public void dismissMediaController() {

    }

    @Override
    public void showMediaController() {

    }

    @Override
    public void updateProgress() {
        mStateHelper.mDurationTime = mMediaPlayController.getDuration();
        mStateHelper.mPlayDuration = mMediaPlayController.getCurrentPosition();
        mHandler.removeMessages(SHOW_PROGRESS);
        mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, UPDATE_PROGRESS_DURATION);
    }

    @Override
    public void autoPlay() {
        LogUtil.d("wng_", ">>FeedListVideoController autoPlay");
        if (mMediaPlayController.start()) {
            LogUtil.d("wng_", "FeedListVideoController autoPlay start success");
            mLoading.setVisibility(View.GONE);
            mPlayBtn.setVisibility(View.GONE);

            mHandler.removeMessages(SHOW_PROGRESS);
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
        LogUtil.d("wng_", "<<FeedListVideoController autoPlay");
    }

    @Override
    public void autoPause() {
        if (mMediaPlayController == null) {
            return;
        }
        if (mMediaPlayController.pause()) {
            mPlayBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void release() {
        LogUtil.d("wng_", ">>FeedListVideoController release");
        mPlayBtn.setVisibility(VISIBLE);
        mLoading.setVisibility(GONE);
        mCover.setVisibility(VISIBLE);
        mPostContent.setVisibility(View.VISIBLE);
        mVoiceSwitch.setVisibility(View.GONE);

        mStateHelper.mMuteType = VideoListVolumeManager.INSTANCE.isMute();
        mStateHelper.postEvent5();
        mStateHelper.resetLog(true);
        mHandler.removeCallbacksAndMessages(null);
        LogUtil.d("wng_", "<<FeedListVideoController release");
    }

    @Override
    public void setPauseFlag(boolean b) {

    }

    @Override
    public void onPrepared() {
        LogUtil.d("wng_", ">>FeedListVideoController onPrepared");
        mPlayBtn.setVisibility(View.GONE);
        mCover.setVisibility(View.GONE);
        mPostContent.setVisibility(View.INVISIBLE);
        if (VideoListVolumeManager.INSTANCE.isVoiceUIOn()) {
            mVoiceSwitch.setVisibility(View.VISIBLE);
            mVoiceSwitch.setImageResource(VideoListVolumeManager.INSTANCE.isMute() ? R.drawable.video_voice_off : R.drawable.video_voice_on);
        } else {
            mVoiceSwitch.setVisibility(View.GONE);
        }
        if (mMediaPlayController != null) {
            mMediaPlayController.setMute(VideoListVolumeManager.INSTANCE.isMute());
        }
        LogUtil.d("wng_", "<<FeedListVideoController onPrepared");
    }

    @Override
    public void onCompletion() {
        mStateHelper.mMuteType = VideoListVolumeManager.INSTANCE.isMute();
        mStateHelper.postEvent5();
        mStateHelper.resetLog(false);
        if (finishCallback != null) {
            finishCallback.invoke();
        }
    }

    @Override
    public void setMediaControllerInvoker(MediaControllerInvoker invoker) {

    }

    @Override
    public void gone() {

    }

    @Override
    public void dismissTrackThumb() {

    }

    @Override
    public void downloadError(String url) {

    }

    @Override
    public void downloadStart(String url) {

    }

    @Override
    public void downloadSuccess(String url) {

    }

    @Override
    public void downloadCancel(String url) {

    }

    @Override
    public void downloadProgressChanged(Integer integer) {

    }

    @Override
    public void updateDownloadStatus(String url, DownloadState status) {

    }

    @Override
    public void goneDownloadBtn() {

    }

    @Override
    public void showDownloadBtn() {

    }

    @Override
    public void goneShareBtn() {

    }

    @Override
    public void showShareBtn() {

    }

    @Override
    public void doCommand(MediaPlayerCommand command) {
        if (command == null) {
            return;
        }
        if (command.cid == MediaPlayerCommandId.ON_FIRST_FRAME_RENDERING) {
            mStateHelper.mVideoFirstFrame = SystemClock.elapsedRealtime() - mStateHelper.baseTime;
            mStateHelper.mAudioFirstFrame = SystemClock.elapsedRealtime() - mStateHelper.baseTime;
        }
    }

    private static class VideoHandler extends Handler {

        private final WeakReference<IMediaController> mMediaController;

        public VideoHandler(IMediaController mediaController) {
            mMediaController = new WeakReference<>(mediaController);
        }

        @Override
        public void handleMessage(Message msg) {
            IMediaController mediaController = mMediaController.get();

            if (mediaController == null) {
                return;
            }
            switch (msg.what) {
                case SHOW_PROGRESS:
                    mediaController.updateProgress();
                    break;
            }
        }
    }
}
