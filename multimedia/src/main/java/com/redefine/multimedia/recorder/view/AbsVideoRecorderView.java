package com.redefine.multimedia.recorder.view;

import android.app.Activity;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.media.mediasdk.UI.IRecorder;
import com.redefine.multimedia.R;
import com.redefine.multimedia.recorder.contract.IVideoRecorderContract;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.base.util.TimeUtil;

import java.io.File;

import static com.media.mediasdk.UI.IRecorder.MEDIA_VIDEO;


/**
 * Created by liwenbo on 2018/4/8.
 */

public abstract class AbsVideoRecorderView<T> implements IVideoRecorderContract.IVideoRecorderView, View.OnClickListener {

    // add offset 
    private static final long MAX_VIDEO_DURATION = 60200;
    private TextView mRecorderTime;
    private View mBackBtn;
    private ImageView mFlashBtn;
    private ImageView mFrontBtn;
    private AppCompatImageView mVideoRecorderStartBtn;
    private AppCompatImageView mVideoRecorderResetBtn;
    private AppCompatImageView mVideoRecorderOkBtn;
    private IRecorder mCamera;
    private CountDownTimer mCountDownTimer;

    private static final int RECORD_STATE_INIT = 0;
    private static final int RECORD_STATE_PREVIEW = 1;
    private static final int RECORD_STATE_RECORDING = 2;
    private static final int RECORD_STATE_RECORDED = 3;

    private int mRecordState = RECORD_STATE_INIT;
    private IVideoRecorderContract.IVideoRecorderPresenter mPresenter;
    private String mFilePath;
    private T mSurface;
    private int mWidth;
    private int mHeight;
    private boolean isTextureAvailable = false;
    private Activity mActivity;
    private boolean mIsOver2Sec = false;

    public void initViews(Activity activity) {
        mActivity = activity;
        mRecorderTime = activity.findViewById(R.id.video_recorder_time);
        mBackBtn = activity.findViewById(R.id.common_back_btn);
        mFlashBtn = activity.findViewById(R.id.camera_flash_btn);
        mFrontBtn = activity.findViewById(R.id.camera_front_btn);

        mVideoRecorderStartBtn = activity.findViewById(R.id.video_recorder_start_btn);
        mVideoRecorderResetBtn = activity.findViewById(R.id.video_recorder_reset_btn);
        mVideoRecorderOkBtn = activity.findViewById(R.id.video_recorder_ok_btn);
        mBackBtn.setOnClickListener(this);

        initSurfaceView(activity);
    }

    protected abstract void initSurfaceView(Activity activity);


    public void initCameraRecorder(String fileName) {
        checkCamera(fileName);
        if (isTextureAvailable) {
            if (mRecordState == RECORD_STATE_INIT) {
                openCamera(mSurface, mWidth, mHeight);
            }
        }
    }

    @Override
    public void setPresenter(IVideoRecorderContract.IVideoRecorderPresenter videoRecorder2Presenter) {
        mPresenter = videoRecorder2Presenter;
    }

    private void closeCamera() {
        try {
            if (mCamera != null) {
                mCamera.StopPreview();
                mCamera.Close();
            }
        } catch (Throwable r) {
            //  do nothing
        }

    }

    private void openCamera(T surface, int width, int height) {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.Open();
            mCamera.SetSurface(surface);
            mCamera.SetPreviewSize(width, height);
            mCamera.StartPreview();
        } catch (Throwable e) {
            // do nothing
        }
        mRecorderTime.setText(R.string.recorder_time);
        mVideoRecorderStartBtn.setImageResource(R.drawable.video_recorder_start_btn);
        mVideoRecorderOkBtn.setVisibility(View.INVISIBLE);
        mVideoRecorderResetBtn.setVisibility(View.INVISIBLE);
        setRecordState(RECORD_STATE_PREVIEW);
    }


    @Override
    public void onActivityPause() {
        if (mRecordState == RECORD_STATE_RECORDING) {
            // 如果处于preview状态，则什么都不做
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                mIsOver2Sec = false;
            }
            finishRecord();
        }
    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onCameraResult() {
        mVideoRecorderStartBtn.setEnabled(true);
        mVideoRecorderStartBtn.setImageResource(R.drawable.video_recorder_preview_play);
        mVideoRecorderResetBtn.setVisibility(View.VISIBLE);
        mVideoRecorderOkBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void resetCamera() {
        clearRecord();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            if(mRecordState == RECORD_STATE_PREVIEW) {
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEO_BACK);
            } else if (mRecordState == RECORD_STATE_RECORDING) {
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEOING_BACK);
            } else if (mRecordState == RECORD_STATE_RECORDED) {
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEODONE_BACK);
            }
            mPresenter.onBackPressed();
        } else if (v == mFlashBtn) {
            // 切换闪光灯
        } else if (v == mFrontBtn) {
            // 切换前后摄像头
            try {
                if (mCamera != null && (mRecordState == RECORD_STATE_PREVIEW || mRecordState == RECORD_STATE_RECORDING)) {
                    mCamera.SwitchCamera();
                }
            } catch (Throwable e) {
                // do nothing
            }

            if(mRecordState == RECORD_STATE_PREVIEW) {
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEO_SELFIE);
            } else if (mRecordState == RECORD_STATE_RECORDING) {
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEOING_SELFIE);
            }
        } else if (v == mVideoRecorderStartBtn) {
            // 录制按钮被点击
            if (mRecordState == RECORD_STATE_RECORDING) {
                if (mIsOver2Sec) {
                    // 正在录制的话，完成录制
                    if (mCountDownTimer != null) {
                        mCountDownTimer.cancel();
                        mCountDownTimer = null;
                        mIsOver2Sec = false;
                    }
                    finishRecord();
                } else {
                    Toast.makeText(v.getContext(), ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "video_at_least_time"), Toast.LENGTH_SHORT).show();
                }
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEOING_STOP);
            } else if (mRecordState == RECORD_STATE_PREVIEW) {
                // preview状态的话，开始录制
                startRecord();
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEO_START);
            } else if (mRecordState == RECORD_STATE_RECORDED) {
                // 当前是录制完成状态的话，播放录制视频
                previewRecordResult();
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEODONE_PLAY);
            }

        } else if (v == mVideoRecorderResetBtn) {
            // 清除录制内容重新录制
            clearRecord();
            AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEODONE_RESHOOT);
        } else if (v == mVideoRecorderOkBtn) {
            // 确认当前的录制内容
            confirmRecordResult();
            AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_VIDEODONE_DONE);
        }
    }

    private void previewRecordResult() {
        mPresenter.previewRecordResult();
    }

    public void finishRecord() {
        mVideoRecorderStartBtn.setEnabled(false);
        stopRecord();
        // 截取关键帧
        closeCamera();
    }

    private void enableAllBtn(boolean isEnable) {
        mVideoRecorderResetBtn.setEnabled(isEnable);
        mVideoRecorderOkBtn.setEnabled(isEnable);
        mVideoRecorderStartBtn.setEnabled(isEnable);
        mBackBtn.setEnabled(isEnable);
        mFrontBtn.setEnabled(isEnable);
        mFlashBtn.setEnabled(isEnable);
    }

    @Override
    public void releaseCamera() {
        if (mCamera == null) {
            return;
        }
        stopRecord();
        closeCamera();
        // 截取关键帧
        new File(mFilePath).deleteOnExit();
    }

    @Override
    public void checkCamera(String fileName) {
        if (mCamera != null) {
            return ;
        }
        mFilePath = fileName;

        mCamera = IRecorder.CreateRecordInstance();
        mCamera.setRecorderType(MEDIA_VIDEO);
        mCamera.SetOutputPath(fileName);
        mCamera.SetResultCallback(mPresenter);
        mCamera.SetRotation(mActivity.getWindowManager().getDefaultDisplay().getRotation());
        mFlashBtn.setOnClickListener(this);
        mFrontBtn.setOnClickListener(this);
        mVideoRecorderStartBtn.setOnClickListener(this);
        mVideoRecorderOkBtn.setOnClickListener(this);
        mVideoRecorderResetBtn.setOnClickListener(this);
    }

    private void stopRecord() {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.StopRecord();
            setRecordState(RECORD_STATE_RECORDED);
        } catch (Throwable e) {
            // do nothing
        }

    }

    @Override
    public long getDuration() {
        return 0;
    }

    private void setRecordState(int recordState) {
        mRecordState = recordState;
        if (mRecordState == RECORD_STATE_PREVIEW || mRecordState == RECORD_STATE_RECORDING) {
            mFrontBtn.setVisibility(View.VISIBLE);
        } else {
            mFrontBtn.setVisibility(View.GONE);
        }
    }

    private void confirmRecordResult() {
        mPresenter.onConfirmRecordResult();
    }

    private void clearRecord() {
        mVideoRecorderStartBtn.setEnabled(true);
        new File(mFilePath).deleteOnExit();
        if (mSurface != null) {
            openCamera(mSurface, mWidth, mHeight);
        }

    }

    private void startRecord() {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.StartRecord();
            mVideoRecorderStartBtn.setImageResource(R.drawable.video_recorder_stop_btn);
            mRecorderTime.setText(TimeUtil.timeParse(MAX_VIDEO_DURATION));
            mIsOver2Sec = false;
            mCountDownTimer = new CountDownTimer(MAX_VIDEO_DURATION, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mIsOver2Sec = (MAX_VIDEO_DURATION - millisUntilFinished) > 2 * 1000;
                    mRecorderTime.setText(TimeUtil.timeParse(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    mRecorderTime.setText(TimeUtil.timeParse(0));
                    if (mRecordState == RECORD_STATE_RECORDING) {
                        finishRecord();
                    }
                }
            };
            mCountDownTimer.start();
            setRecordState(RECORD_STATE_RECORDING);
        } catch (Throwable e) {
            // do nothing
        }

    }

    public void onSurfaceAvailable(T surface, int width, int height) {
        mWidth = width;
        mHeight = height;
        mSurface = surface;
        isTextureAvailable = true;
        if (mCamera != null && mRecordState == RECORD_STATE_INIT) {
            openCamera(mSurface, mWidth, mHeight);
        }
    }
}
