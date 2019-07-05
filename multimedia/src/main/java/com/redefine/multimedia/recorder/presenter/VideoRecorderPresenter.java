package com.redefine.multimedia.recorder.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.media.mediasdk.codec.FileUtil;
import com.media.mediasdk.codec.MP4HeadInfo;
import com.redefine.foundation.io.FileManager;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.multimedia.R;
import com.redefine.multimedia.player.VideoPlayerActivity;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.multimedia.recorder.constant.VideoRecorderConstant;
import com.redefine.multimedia.recorder.contract.IVideoRecorderContract;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.resource.ResourceTool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liwenbo on 2018/4/8.
 */

public class VideoRecorderPresenter implements IVideoRecorderContract.IVideoRecorderPresenter {

    private final IVideoRecorderContract.IVideoRecorderView mView;
    private String mFilePath;
    private final HashMap<String, String> mPermissionTip;
    private String mTmpFilePath;
    private final int mMediaType;
    private Activity mActivity;
    private int mWidth;
    private int mHeight;
    private long mDuration;

    public VideoRecorderPresenter(Intent intent) {
        mPermissionTip = new HashMap<String, String>();
        mPermissionTip.put(Manifest.permission.CAMERA, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "camera_permission"));
        mPermissionTip.put(Manifest.permission.RECORD_AUDIO, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "record_audio_permission"));
        mPermissionTip.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "sd_write_permission"));
        mView = IVideoRecorderContract.VideoRecorderFactory.createView();
        mMediaType = intent.getIntExtra(VideoRecorderConstant.MEDIA_TYPE, VideoRecorderConstant.TYPE_VIDEO);
    }

    @Override
    public void initViews(Activity activity) {
        mActivity = activity;
        mView.setPresenter(this);
        mView.initViews(activity);
        // 校验权限
        final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        final List<String> permissionsToRequest = new ArrayList<>();

        for (String p : permissions) {
            if (!EasyPermissions.hasPermissions(activity, p)) {
                permissionsToRequest.add(p);
            }
        }

        if (CollectionUtil.isEmpty(permissionsToRequest)) {
            // 权限已分配
            File file = WeLikeFileManager.getPhotoSaveFile(WeLikeFileManager.APP_NAME + "_" + System.currentTimeMillis() + GlobalConfig.PUBLISH_MP4);
            mFilePath = file == null ? "" : file.getAbsolutePath();
            mTmpFilePath = FileManager.getInstance().getFileInAndroidCache(CommonHelper.generateUUID() + GlobalConfig.PUBLISH_MP4 + GlobalConfig.PUBLISH_PIC_SUFFIX).getAbsolutePath();
            mView.initCameraRecorder(mTmpFilePath);
        } else {
            EasyPermissions.requestPermissions(activity
                    , mPermissionTip.get(permissions[0])
                    , PermissionRequestCode.VIDEO_RECORD_PERMISSION_REQUEST_CODE
                    , permissionsToRequest.toArray(new String[permissionsToRequest.size()]));
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode != PermissionRequestCode.VIDEO_RECORD_PERMISSION_REQUEST_CODE) {
            return ;
        }
        final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mActivity, permissions)) {
            File file = WeLikeFileManager.getPhotoSaveFile(WeLikeFileManager.APP_NAME + "_" + System.currentTimeMillis() + GlobalConfig.PUBLISH_MP4);
            mFilePath = file == null ? "" : file.getAbsolutePath();
            mTmpFilePath = FileManager.getInstance().getFileInAndroidCache(CommonHelper.generateUUID() + GlobalConfig.PUBLISH_MP4 + GlobalConfig.PUBLISH_PIC_SUFFIX).getAbsolutePath();
            mView.initCameraRecorder(mTmpFilePath);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode != PermissionRequestCode.VIDEO_RECORD_PERMISSION_REQUEST_CODE) {
            return ;
        }
        Toast.makeText(mActivity, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_permissions_denied"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityPause() {
        mView.onActivityPause();
    }

    @Override
    public void onActivityResume() {
        mView.onActivityResume();
    }

    @Override
    public void onBackPressed() {
        try {
            mView.releaseCamera();
        } catch (Throwable e) {
            // do nothing
        }
        mActivity.setResult(Activity.RESULT_CANCELED);
        finishActivity();
    }

    private void finishActivity() {
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.sliding_to_left_in, R.anim.sliding_right_out);
    }

    @Override
    public void onConfirmRecordResult() {
        Intent intent = new Intent();
        WeLikeFileManager.refreshGallery(mActivity, mFilePath);
        intent.putExtra(VideoRecorderConstant.MEDIA_RESULT_PATH, mFilePath);
        intent.putExtra(VideoRecorderConstant.MEDIA_TYPE, mMediaType);
        intent.putExtra(VideoRecorderConstant.MEDIA_WIDTH, mWidth);
        intent.putExtra(VideoRecorderConstant.MEDIA_HEIGHT, mHeight);
        intent.putExtra(VideoRecorderConstant.MEDIA_DURATION, mDuration);

        intent.setData(Uri.fromFile(new File(mFilePath)));
        mActivity.setResult(Activity.RESULT_OK, intent);
        finishActivity();
    }


    @Override
    public void RecordCallback(boolean b, String s, final int width, final int height, final long duration) {
        if (!b) {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    mView.resetCamera();
                }
            });
            return ;
        }
        MP4HeadInfo mp4HeadInfo = new MP4HeadInfo();
        FileUtil.MP4FileAtomReady(mTmpFilePath, mp4HeadInfo);
        if(!mp4HeadInfo.exist_moov || !mp4HeadInfo.exist_mdat){
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    mView.resetCamera();
                }
            });
            return ;
        }
        if (mp4HeadInfo.is_moov_before_mdat) {
            boolean isSuccess = new File(mTmpFilePath).renameTo(new File(mFilePath));
            if (isSuccess) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        mWidth = width;
                        mHeight = height;
                        mDuration = duration;
                        mView.onCameraResult();
                    }
                });
            } else {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        mView.resetCamera();
                    }
                });
            }
            return ;
        }

        boolean isSuccess = FileUtil.MP4FileAtomMove(mTmpFilePath, mFilePath);
        if (!isSuccess || !new File(mFilePath).exists()) {
            isSuccess = new File(mTmpFilePath).renameTo(new File(mFilePath));
        }

        if (isSuccess) {
            new File(mTmpFilePath).deleteOnExit();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    mWidth = width;
                    mHeight = height;
                    mDuration = duration;
                    mView.onCameraResult();
                }
            });
        } else {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    mView.resetCamera();
                }
            });
        }
    }

    @Override
    public void previewRecordResult() {
        Intent intent = new Intent(mActivity, VideoPlayerActivity.class);
        intent.putExtra(VideoRecorderConstant.MEDIA_PLAYER_VIDEO_PATH, mFilePath);
        intent.putExtra(PlayerConstant.MEDIA_PLAYER_VIDEO_SOURCE, PlayerConstant.VIDEO_SITE_DEFAULT);
        mActivity.overridePendingTransition(com.redefine.commonui.R.anim.sliding_right_in, com.redefine.commonui.R.anim.sliding_to_left_out);
        mActivity.startActivity(intent);
    }
}
