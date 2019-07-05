package com.redefine.multimedia.player.mediacontroller;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.redefine.commonui.download.DownloadState;
import com.redefine.multimedia.R;
import com.redefine.multimedia.player.VideoStateHelper;
import com.redefine.multimedia.picturelooker.config.PictureMimeType;
import com.redefine.multimedia.player.mediaplayer.IMediaPlayerControl;
import com.redefine.multimedia.widget.CircleDownloadProgressBar;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.LogEventConstant;
import com.redefine.welike.base.util.TimeUtil;

import java.lang.ref.WeakReference;

public class VideoMediaController extends FrameLayout implements IMediaController, SeekBar.OnSeekBarChangeListener {

    private static final int MEDIA_CONTROLLER_DISMISS_TIME = 3000;

    private static final int SHOW_LOADING = 1;
    private static final int DISMISS_LOADING = 2;
    private static final int DISMISS_MEDIA_CONTROLLER = 3;
    private static final int SHOW_PROGRESS = 4;
    private static final long UPDATE_PROGRESS_DURATION = 200;
    private ImageView mVideoPlayBtn;
    private TextView mVideoPlayerStartTime;
    private TextView mVideoPlayerEndTime;
    private View mMediaControllerBar;
    private ProgressBar mMiniProgressBar;
    private SeekBar mSeekBar;
    private IMediaPlayerControl mMediaPlayController;
    private View mLoadingView;
    private boolean isPauseByUser = false;
    private int mDuration;
    private VideoHandler mHandler;
    private boolean mDragging = false;
    private boolean isMute = false;
    private AppCompatImageView mVideoRotateBtn;
    private AppCompatImageView mVideoMuteBtn;
    private View mBackBtn;
    private MediaControllerInvoker mCommandInvoker;
    private AppCompatImageView mVideoDownloadBtn;
    private AppCompatImageView mVideoShareBtn;
    private CircleDownloadProgressBar mVideoDownloadProgress;
    private RelativeLayout mVideoPlayerDownloadRl;


    public VideoMediaController(@NonNull Context context) {
        super(context);
        initViews(context, null);
    }

    public VideoMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public VideoMediaController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoMediaController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.video_player_controller, this, true);
        mVideoPlayBtn = findViewById(R.id.video_play_btn);
        mVideoRotateBtn = findViewById(R.id.video_rotate_btn);
        mVideoMuteBtn = findViewById(R.id.video_player_mute_btn);
        mVideoDownloadBtn = findViewById(R.id.video_player_download_btn);
        mVideoShareBtn = findViewById(R.id.video_player_share_btn);
        mVideoDownloadProgress = findViewById(R.id.video_player_download_progress_btn);
        mVideoPlayerDownloadRl = findViewById(R.id.video_player_download_rl);
        mVideoPlayerStartTime = findViewById(R.id.video_player_start_time);
        mVideoPlayerEndTime = findViewById(R.id.video_player_end_time);
        mMediaControllerBar = findViewById(R.id.video_media_controller_bar);
        mMiniProgressBar = findViewById(R.id.video_player_mini_progress_bar);
        View mMediaControllerRootView = findViewById(R.id.video_media_controller_root_view);
        mSeekBar = findViewById(R.id.video_player_seek_bar);
        mBackBtn = findViewById(R.id.common_back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommandInvoker == null) {
                    return;
                }
                mCommandInvoker.doCommand(new MediaControllerCommand(MediaControllerCommandId.BACK_BTN_CLICK));
            }
        });

        mVideoPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayController == null) {
                    return;
                }
                if (mMediaPlayController.isPlaying()) {
                    pauseByUser();
                } else {
                    playByUser();
                }
            }
        });
        mVideoMuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayController == null) {
                    return;
                }
                isMute = !isMute;
                mMediaPlayController.setMute(isMute);
                mVideoMuteBtn.setImageResource(isMute ? R.drawable.common_mute_btn : R.drawable.common_voice_btn);
            }
        });

        mVideoRotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommandInvoker == null) {
                    return;
                }
                mCommandInvoker.doCommand(new MediaControllerCommand(MediaControllerCommandId.ROTATE_BTN_CLICK));
                Configuration mConfiguration = getResources().getConfiguration();
                int ori = mConfiguration.orientation;
                if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                    mVideoRotateBtn.setImageResource(R.drawable.video_land_rotate);
                } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
                    mVideoRotateBtn.setImageResource(R.drawable.video_portrait_rotate);
                }
            }
        });

        mVideoDownloadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommandInvoker == null) {
                    return;
                }
                VideoStateHelper.sendLog(LogEventConstant.LOG_EVENT_VIDEO_DOWNLOAD);
                mCommandInvoker.doCommand(new MediaControllerCommand(MediaControllerCommandId.DOWNLOAD_BTN_CLICK));
            }
        });

        mVideoShareBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommandInvoker == null) {
                    return;
                }
                mCommandInvoker.doCommand(new MediaControllerCommand(MediaControllerCommandId.SHARE_BTN_CLICK));
            }
        });

        mHandler = new VideoHandler(this);
        mSeekBar.setMax(1000);
        mMiniProgressBar.setMax(1000);
        mLoadingView = findViewById(R.id.player_loading);

        mSeekBar.setOnSeekBarChangeListener(this);

        mMediaControllerRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onTouchMediaController();
                }
                return false;
            }
        });


    }

    private void onTouchMediaController() {

        showMediaController();
    }

    public void showMediaController() {
        if (mMediaPlayController == null || mDuration <= 0) {
            return;
        }

        if (mCommandInvoker != null) {
            mCommandInvoker.doCommand(new MediaControllerCommand(MediaControllerCommandId.SHOW_CONTROLLER_BAR));
        }

        if (mLoadingView.getVisibility() == View.VISIBLE) {
            mVideoPlayBtn.setVisibility(View.GONE);
        } else {
            mVideoPlayBtn.setVisibility(View.VISIBLE);
        }

        mHandler.removeMessages(SHOW_PROGRESS);
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (mMediaControllerBar.getVisibility() == View.VISIBLE) {
            mMediaControllerBar.setVisibility(View.GONE);
            mMiniProgressBar.setVisibility(View.VISIBLE);
        } else {
            mHandler.removeMessages(DISMISS_MEDIA_CONTROLLER);
            mMediaControllerBar.setVisibility(View.VISIBLE);
            mMiniProgressBar.setVisibility(View.GONE);
            mHandler.sendEmptyMessageDelayed(DISMISS_MEDIA_CONTROLLER, MEDIA_CONTROLLER_DISMISS_TIME);
        }
    }

    @Override
    public void updateDownloadStatus(String url, DownloadState status) {
        if (PictureMimeType.isHttp(url)) {
            switch (status) {
                case IDLE:
                    // 没有任务执行
                    mVideoDownloadProgress.setVisibility(GONE);
                    mVideoDownloadBtn.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setSelected(false);
                    mVideoDownloadBtn.setEnabled(true);
                    break;
                case PENDING:
                    // 正在处理
                    mVideoDownloadProgress.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setEnabled(false);
                    break;
                case RUNNING:
                    // 正在执行
                    mVideoDownloadProgress.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setEnabled(false);
                    break;
                case UNKNOWN:
                    // 未知
                    mVideoDownloadProgress.setVisibility(GONE);
                    mVideoDownloadBtn.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setSelected(false);
                    mVideoDownloadBtn.setEnabled(true);
                    break;
                case COMPLETED:
                    // 已完成
                    mVideoDownloadProgress.setVisibility(GONE);
                    mVideoDownloadBtn.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setSelected(true);
                    mVideoDownloadBtn.setEnabled(true);
                    break;
                case PROCESSING:
                    mVideoDownloadProgress.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setEnabled(false);
                    mVideoDownloadBtn.setEnabled(true);
                    break;
                default:
                    mVideoDownloadProgress.setVisibility(GONE);
                    mVideoDownloadBtn.setVisibility(VISIBLE);
                    mVideoDownloadBtn.setSelected(false);
                    mVideoDownloadBtn.setEnabled(true);
                    break;
            }
        } else {
            mVideoPlayerDownloadRl.setVisibility(GONE);
            mVideoDownloadProgress.setVisibility(GONE);
            mVideoDownloadBtn.setVisibility(GONE);
        }
    }

    @Override
    public void goneDownloadBtn() {
//        mVideoDownloadProgress.setVisibility(GONE);
//        mVideoDownloadBtn.setVisibility(GONE);
        mVideoPlayerDownloadRl.setVisibility(GONE);
    }

    @Override
    public void showDownloadBtn() {
        mVideoPlayerDownloadRl.setVisibility(VISIBLE);
    }

    @Override
    public void goneShareBtn() {
        mVideoShareBtn.setVisibility(View.GONE);
    }

    @Override
    public void showShareBtn() {
        mVideoShareBtn.setVisibility(View.VISIBLE);
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

        if (mDuration > 0) {
            // use long to avoid overflow
            long pos = 1000L * position / mDuration;
            mSeekBar.setProgress((int) pos);
            mMiniProgressBar.setProgress((int) pos);
        }
        int percent = mMediaPlayController.getBufferPercentage();
        if (percent >= 98) percent = 100;
        mSeekBar.setSecondaryProgress(percent * 10);

        if (mCommandInvoker != null) {
            mCommandInvoker.doCommand(new MediaControllerCommand(MediaControllerCommandId.UPDATE_PROGRESS, position, mDuration));
        }

        if (mVideoPlayerEndTime != null) {
            mVideoPlayerEndTime.setText(generateTime(mDuration));
        }
        if (mVideoPlayerStartTime != null) {
            mVideoPlayerStartTime.setText(generateTime(position));
        }
//        LogUtil.e("liwb", "mDuration:" + mDuration + "--position" + position + "--SecondaryProgress" + percent * 10);
    }


    public void playByUser() {
        if (mMediaPlayController == null || mDuration <= 0) {
            return;
        }
        mHandler.removeMessages(SHOW_PROGRESS);
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        isPauseByUser = false;
        mMediaPlayController.start();
        mVideoPlayBtn.setImageResource(R.drawable.video_pause_btn);
        mHandler.removeMessages(DISMISS_MEDIA_CONTROLLER);
        mHandler.sendEmptyMessageDelayed(DISMISS_MEDIA_CONTROLLER, MEDIA_CONTROLLER_DISMISS_TIME);
    }

    public void pauseByUser() {
        if (mMediaPlayController == null) {
            return;
        }
        isPauseByUser = true;
        mMediaPlayController.pause();
        mVideoPlayBtn.setImageResource(R.drawable.video_play_btn);
        mHandler.removeMessages(DISMISS_MEDIA_CONTROLLER);
    }


    public void autoPause() {
        if (mMediaPlayController == null) {
            return;
        }
        if (!isPauseByUser && mMediaPlayController.pause()) {
            mVideoPlayBtn.setImageResource(R.drawable.video_play_btn);
        }
    }

    @Override
    public void release() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void setPauseFlag(boolean b) {
        isPauseByUser = b;
    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onCompletion() {
        doUpdateProgress(mDuration);
    }

    @Override
    public void setMediaControllerInvoker(MediaControllerInvoker invoker) {
        mCommandInvoker = invoker;
    }

    @Override
    public void gone() {
        setVisibility(GONE);
    }

    @Override
    public void dismissTrackThumb() {
        mSeekBar.setThumb(ResourceTool.getBoundDrawable(mSeekBar.getResources(), R.drawable.video_track_thumb_tran));
        mSeekBar.setOnSeekBarChangeListener(null);
        mSeekBar.setEnabled(false);
    }

    @Override
    public void downloadError(String url) {
        updateDownloadStatus(url, DownloadState.UNKNOWN);
    }

    @Override
    public void downloadStart(String url) {
        updateDownloadStatus(url, DownloadState.RUNNING);
    }

    @Override
    public void downloadSuccess(String url) {
        VideoStateHelper.sendLog(LogEventConstant.LOG_EVENT_VIDEO_DOWNLOAD_SUCCESS);
        updateDownloadStatus(url, DownloadState.COMPLETED);
    }

    @Override
    public void downloadCancel(String url) {
        updateDownloadStatus(url, DownloadState.IDLE);
    }

    @Override
    public void downloadProgressChanged(Integer integer) {
        if (integer == null) {
            return;
        }
        mVideoDownloadProgress.setProgress(integer);
    }

    public void autoPlay() {
        if (mMediaPlayController == null) {
            return;
        }
        if (!isPauseByUser && mMediaPlayController.start()) {
            mVideoPlayBtn.setImageResource(R.drawable.video_pause_btn);
            mHandler.removeMessages(SHOW_PROGRESS);
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    }

    private String generateTime(long duration) {
        return TimeUtil.timeParse(duration);
    }


    @Override
    public void setMediaPlayerController(IMediaPlayerControl mediaPlayerController) {
        mMediaPlayController = mediaPlayerController;
    }


    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mVideoPlayBtn.setVisibility(View.GONE);
    }

    @Override
    public void dismissLoading() {
        mLoadingView.setVisibility(View.GONE);
        if (mMediaControllerBar.getVisibility() == View.VISIBLE) {
            mVideoPlayBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dismissMediaController() {
        mVideoPlayBtn.setVisibility(View.GONE);
        mMediaControllerBar.setVisibility(View.GONE);
        mMiniProgressBar.setVisibility(View.VISIBLE);
    }

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
        mVideoPlayerStartTime.setText(time);
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

        final long newPosition = (mDuration * seekBar.getProgress()) / 1000L;
        mMediaPlayController.seekTo((int) newPosition);
        long pos = 1000L * newPosition / mDuration;
        mMiniProgressBar.setProgress((int) pos);
        String time = generateTime(newPosition);
        mVideoPlayerStartTime.setText(time);
        mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, UPDATE_PROGRESS_DURATION);
        mHandler.sendEmptyMessageDelayed(DISMISS_MEDIA_CONTROLLER, MEDIA_CONTROLLER_DISMISS_TIME);

        playByUser();
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
                case SHOW_LOADING:
                    mediaController.showLoading();
                    break;
                case DISMISS_LOADING:
                    mediaController.dismissLoading();
                    break;
                case DISMISS_MEDIA_CONTROLLER:
                    mediaController.dismissMediaController();
                    break;
                case SHOW_PROGRESS:
                    mediaController.updateProgress();
                    break;
            }
        }
    }
}
