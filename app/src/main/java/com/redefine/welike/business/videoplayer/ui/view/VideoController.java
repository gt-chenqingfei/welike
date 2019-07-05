package com.redefine.welike.business.videoplayer.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.download.DownloadState;
import com.redefine.multimedia.player.mediacontroller.IMediaController;
import com.redefine.multimedia.player.mediacontroller.MediaControllerInvoker;
import com.redefine.multimedia.player.mediaplayer.IMediaPlayerControl;
import com.redefine.welike.R;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.videoplayer.management.command.CommandId;
import com.redefine.welike.business.videoplayer.management.command.CommandInvoker;
import com.redefine.welike.business.videoplayer.management.command.VideoCommand;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.lang.ref.WeakReference;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

/**
 * Created by nianguowang on 2018/8/8
 */
public class VideoController extends ConstraintLayout implements IMediaController {

    private static final int SHOW_PROGRESS = 1;
    private static final int DISMISS_MEDIA_CONTROLLER = 2;

    private static final long UPDATE_PROGRESS_DURATION = 200;
    private static final int MEDIA_CONTROLLER_DISMISS_TIME = 3000;

    private View mCloseBtn, mCloseBtnH;
    private View mLoading;
    private View mMediaControllerRootView;
    private View mSeekBarContainer;
    private ImageView mPlayBtn;
    private SimpleDraweeView mCover;
    private ProgressBar mMiniProgressBar;
    private SeekBar mSeekBar;
    private TextView mPlayDuration, mTotalTime;
    private AppCompatImageView mRotateBtn, mRotateBtnH;
    private PostController mPostController;

    private int mDuration;
    private boolean mLandscape;
    private boolean mDragging = false;
    private boolean isPauseByUser = false;
    private VideoHandler mHandler;
    private IMediaPlayerControl mMediaPlayController;
    private CommandInvoker.VideoCommandInvoker mVideoCommandInvoker;

    public VideoController(Context context) {
        super(context);
        init();
    }

    public VideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.video_controller_layout, this, true);
        mPlayBtn = view.findViewById(R.id.video_play_btn);
        mCloseBtn = findViewById(R.id.video_close_btn);
        mCloseBtnH = findViewById(R.id.video_close_btn_h);
        mLoading = findViewById(R.id.video_progress_bar);
        mRotateBtn = findViewById(R.id.video_rotate_btn);
        mRotateBtnH = findViewById(R.id.video_rotate_btn_h);
        mCover = findViewById(R.id.video_cover);
        mMiniProgressBar = findViewById(R.id.video_player_mini_progress_bar);
        mPlayDuration = findViewById(R.id.video_player_start_time);
        mTotalTime = findViewById(R.id.video_player_end_time);
        mSeekBar = findViewById(R.id.video_player_seek_bar);
        mMediaControllerRootView = findViewById(R.id.video_media_controller_bar);
        mSeekBarContainer = findViewById(R.id.video_seek_bar_container);

//        setRotateBtn();
        mHandler = new VideoHandler(this);

        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayController == null) {
                    return;
                }
                if (mMediaPlayController.isPlaying()) {
                    if (mVideoCommandInvoker != null) {
                        mVideoCommandInvoker.invoke(new VideoCommand(CommandId.PAUSE_VIDEO));
                    }
                    pauseByUser();
                } else {
                    if (mVideoCommandInvoker != null) {
                        mVideoCommandInvoker.invoke(new VideoCommand(CommandId.PLAY_VIDEO));
                    }
                    playByUser();
                }
            }
        });
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoCommandInvoker != null) {
                    mVideoCommandInvoker.invoke(new VideoCommand(CommandId.BACK_BTN_CLICK));
                }
            }
        };
        mCloseBtn.setOnClickListener(listener);
        mCloseBtnH.setOnClickListener(listener);
        OnClickListener listener1 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoCommandInvoker != null) {
                    mVideoCommandInvoker.invoke(new VideoCommand(CommandId.ROTATE_BTN_CLICK));
                }
            }
        };
        mRotateBtn.setOnClickListener(listener1);
        mRotateBtnH.setOnClickListener(listener1);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                if (mDuration <= 0) {
                    return;
                }
                final long newPosition = (mDuration * progress) / 1000;
                String time = generateTime(newPosition);
                mPlayDuration.setText(time);
                long pos = 1000L * newPosition / mDuration;
                mMiniProgressBar.setProgress((int) pos);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mDragging = true;
                mHandler.removeMessages(DISMISS_MEDIA_CONTROLLER);
                mHandler.removeMessages(SHOW_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDragging = false;
                if (mMediaPlayController == null || mDuration <= 0) {
                    return;
                }

                final long newPosition = (mDuration * seekBar.getProgress()) / 100L;
                mMediaPlayController.seekTo((int) newPosition);
                long pos = 100L * newPosition / mDuration;
                mMiniProgressBar.setProgress((int) pos);
                String time = generateTime(newPosition);
                mPlayDuration.setText(time);
                mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, UPDATE_PROGRESS_DURATION);
                mHandler.sendEmptyMessageDelayed(DISMISS_MEDIA_CONTROLLER, MEDIA_CONTROLLER_DISMISS_TIME);
                if (mVideoCommandInvoker != null) {
                    mVideoCommandInvoker.invoke(new VideoCommand(CommandId.SEEK_PROGRESS));
                }
                playByUser();
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMediaController();
            }
        });
    }

    public void setCoverUrl(String coverUrl) {
        mCover.setImageURI(coverUrl);
        mCover.setVisibility(View.VISIBLE);
        mMiniProgressBar.setProgress(0);
        dismissMediaController();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void reset() {
        isPauseByUser = false;
        mDragging = false;
    }

    public void bindPostController(PostController postController) {
        mPostController = postController;
        if (mPostController != null) {
            mPostController.onMediaControllerChange(mMediaControllerRootView.getVisibility() == View.VISIBLE);
        }
    }

    public void setVideoCommandInvoker(CommandInvoker.VideoCommandInvoker videoCommandInvoker) {
        mVideoCommandInvoker = videoCommandInvoker;
    }

    private void setRotateBtn(boolean isLandscape) {
        mLandscape = isLandscape;
        if (isLandscape) {
            mRotateBtn.setVisibility(GONE);
            mRotateBtnH.setVisibility(VISIBLE);
            mCloseBtn.setVisibility(GONE);
            mCloseBtnH.setVisibility(VISIBLE);
        } else {
            mRotateBtn.setVisibility(VISIBLE);
            mRotateBtnH.setVisibility(GONE);
            mCloseBtn.setVisibility(VISIBLE);
            mCloseBtnH.setVisibility(GONE);

        }
    }

    @Override
    public void setMediaPlayerController(IMediaPlayerControl apolloVideoView) {
        mMediaPlayController = apolloVideoView;
    }

    @Override
    public void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoading() {
        mLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void updateProgress() {
        mHandler.removeMessages(SHOW_PROGRESS);
        mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, UPDATE_PROGRESS_DURATION);
        doUpdateProgress(mMediaPlayController.getCurrentPosition());
    }

    private void doUpdateProgress(int position) {
        if (mMediaPlayController == null || mDragging || !mMediaPlayController.isPlaying()) {
            return;
        }
        mDuration = mMediaPlayController.getDuration();

        if (mPlayDuration != null) {
            mPlayDuration.setText(generateTime(position));
        }
        if (mTotalTime != null) {
            mTotalTime.setText(generateTime(mDuration));
        }
        if (mDuration > 0) {
            // use long to avoid overflow
            long pos = 100L * position / mDuration;
            mMiniProgressBar.setProgress((int) pos);
            mSeekBar.setProgress((int) pos);
        }

        if (mVideoCommandInvoker != null) {
            mVideoCommandInvoker.invoke(new VideoCommand(CommandId.UPDATE_PROGRESS, position, mDuration));
        }

    }

    @Override
    public void autoPlay() {
        if (mMediaPlayController == null) {
            return;
        }
        if (!isPauseByUser && mMediaPlayController.start()) {
            mPlayBtn.setVisibility(View.GONE);
            mHandler.removeMessages(SHOW_PROGRESS);
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    }

    @Override
    public void autoPause() {
        if (mMediaPlayController == null) {
            return;
        }
        if (!isPauseByUser && mMediaPlayController.pause()) {
            mPlayBtn.setVisibility(View.VISIBLE);
        }
    }

    public void playByUser() {
        if (mMediaPlayController == null || mDuration <= 0) {
            return;
        }
        mHandler.removeMessages(SHOW_PROGRESS);
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        isPauseByUser = false;
        mMediaPlayController.start();
        mPlayBtn.setImageResource(com.redefine.multimedia.R.drawable.video_pause_btn);
        mHandler.removeMessages(DISMISS_MEDIA_CONTROLLER);
        mHandler.sendEmptyMessageDelayed(DISMISS_MEDIA_CONTROLLER, MEDIA_CONTROLLER_DISMISS_TIME);
    }

    public void pauseByUser() {
        if (mMediaPlayController == null) {
            return;
        }
        isPauseByUser = true;
        mMediaPlayController.pause();
        mPlayBtn.setImageResource(com.redefine.multimedia.R.drawable.video_play_btn);
        mHandler.removeMessages(DISMISS_MEDIA_CONTROLLER);
    }

    @Override
    public void release() {
        mHandler.removeCallbacksAndMessages(null);
        mCover.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPauseFlag(boolean b) {
        isPauseByUser = b;
    }

    @Override
    public void onPrepared() {
        mCover.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCompletion() {
        doUpdateProgress(mDuration);
    }

    @Override
    public void setMediaControllerInvoker(MediaControllerInvoker invoker) {

    }

    @Override
    public void gone() {
        setVisibility(GONE);
    }

    @Override
    public void dismissMediaController() {
        animateSeekBar(false);
    }

    @Override
    public void showMediaController() {
        if (mMediaPlayController == null || mDuration <= 0) {
            return;
        }

        if (mLoading.getVisibility() == View.VISIBLE) {
            mPlayBtn.setVisibility(View.GONE);
        } else {
            mPlayBtn.setVisibility(View.VISIBLE);
            if (mMediaPlayController.isPlaying()) {
                mPlayBtn.setImageResource(com.redefine.multimedia.R.drawable.video_pause_btn);
            } else {
                mPlayBtn.setImageResource(com.redefine.multimedia.R.drawable.video_play_btn);
            }
        }

        mHandler.removeMessages(SHOW_PROGRESS);
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (mMediaControllerRootView.getVisibility() == View.VISIBLE) {
            animateSeekBar(false);
        } else {
            mMediaControllerRootView.setVisibility(View.VISIBLE);
            mMiniProgressBar.setVisibility(View.GONE);
            changeRotateVisibility(false);
            if (mPostController != null) {
                mPostController.onMediaControllerChange(true);
            }
            animateSeekBar(true);
        }
    }

    private void realDismissMediaController() {
        mMediaControllerRootView.setVisibility(View.GONE);
        mMiniProgressBar.setVisibility(View.VISIBLE);
        changeRotateVisibility(true);
        if (mPostController != null) {
            mPostController.onMediaControllerChange(false);
        }
    }

    private void realShowMediaController() {
        mHandler.removeMessages(DISMISS_MEDIA_CONTROLLER);
        mHandler.sendEmptyMessageDelayed(DISMISS_MEDIA_CONTROLLER, MEDIA_CONTROLLER_DISMISS_TIME);
    }

    private void changeRotateVisibility(boolean show) {
        if (mLandscape) {
            mRotateBtnH.setVisibility(show ? View.VISIBLE : View.GONE);
            mRotateBtn.setVisibility(View.GONE);
        } else {
            mRotateBtn.setVisibility(show ? View.VISIBLE : View.GONE);
            mRotateBtnH.setVisibility(View.GONE);
        }
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

    public void changeConfig(int orientation) {
        setRotateBtn(orientation == ORIENTATION_LANDSCAPE);
    }

    private String generateTime(long duration) {
        return TimeUtil.timeParse(duration);
    }

    private void animateSeekBar(final boolean show) {
        int offset = DensityUtil.dp2px(44);
        ObjectAnimator translationY;
        if (show) {
            translationY = ObjectAnimator.ofFloat(mSeekBarContainer, "translationY", offset, 0);
        } else {
            translationY = ObjectAnimator.ofFloat(mSeekBarContainer, "translationY", 0, offset);
        }
        translationY.setDuration(200);
        translationY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (show) {
                    realShowMediaController();
                } else {
                    realDismissMediaController();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (show) {
                    realShowMediaController();
                } else {
                    realDismissMediaController();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translationY.start();
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
                case DISMISS_MEDIA_CONTROLLER:
                    mediaController.dismissMediaController();
                    break;
            }
        }
    }

}
