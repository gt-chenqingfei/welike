package com.redefine.multimedia.recorder.contract;

import android.app.Activity;
import android.content.Intent;

import com.media.mediasdk.UI.IRecorder;
import com.redefine.foundation.mvp.IBasePresenter;
import com.redefine.foundation.mvp.IBaseView;
import com.redefine.foundation.utils.VersionUtil;
import com.redefine.multimedia.recorder.presenter.VideoRecorderPresenter;
import com.redefine.multimedia.recorder.view.VideoRecorderView;
import com.redefine.multimedia.recorder.view.VideoSurfaceRecorderView;

import java.util.List;

/**
 * Created by liwenbo on 2018/4/8.
 */

public interface IVideoRecorderContract {
    interface IVideoRecorderPresenter extends IBasePresenter, IRecorder.IRecordCallback {

        void initViews(Activity activity);

        void onActivityPause();

        void onActivityResume();

        void onBackPressed();

        void onConfirmRecordResult();

        void previewRecordResult();

        void onPermissionsGranted(int requestCode, List<String> perms);

        void onPermissionsDenied(int requestCode, List<String> perms);
    }

    interface IVideoRecorderView extends IBaseView {
        void initViews(Activity activity);

        void initCameraRecorder(String fileName);

        void setPresenter(IVideoRecorderPresenter videoRecorder2Presenter);

        void onActivityPause();

        void onActivityResume();

        void finishRecord();

        void releaseCamera();

        void onCameraResult();

        void resetCamera();

        void checkCamera(String mTmpFilePath);

        long getDuration();
    }

    class VideoRecorderFactory {
        public static IVideoRecorderPresenter createPresenter(Intent intent) {
            return new VideoRecorderPresenter(intent);
        }

        public static IVideoRecorderView createView() {
            if (VersionUtil.isLower5_0()) {
                return new VideoSurfaceRecorderView();
            }
            return new VideoRecorderView();
        }
    }
}
