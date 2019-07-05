package com.redefine.multimedia.snapshot;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.media.mediasdk.common.AspectRatio;
import com.media.mediasdk.core.RDCamera.CameraView.CameraView;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.constant.ImagePickConstant;
import com.redefine.multimedia.recorder.constant.VideoRecorderConstant;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.resource.ResourceTool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

public class PhotoSnapShotActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private ImageView mCameraSnap;
    private View mCameraFrontBtn;
    private ImageView mCameraFlashBtn;
    private View mDeleteBtn;
    private View mConfirmBtn;
    private View mBackBtn;
    private CameraView mCameraView;
    private ViewGroup mCameraContent;
    private String mFilePath;
    public int mWidth;
    public int mHeight;
    private int mMediaType;
    private HashMap<String, String> mPermissionTip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_snap_layout);
        mMediaType = getIntent().getIntExtra(VideoRecorderConstant.MEDIA_TYPE, VideoRecorderConstant.TYPE_IMAGE);
        initViews();
    }

    private void initViews() {
        mPermissionTip = new HashMap<String, String>();
        mPermissionTip.put(Manifest.permission.CAMERA, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "camera_permission"));
        mPermissionTip.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "sd_write_permission"));
        mBackBtn = findViewById(R.id.common_back_btn);
        mCameraSnap = findViewById(R.id.camera_recorder_btn);
        mCameraFrontBtn = findViewById(R.id.camera_front_btn);
        mCameraFlashBtn = findViewById(R.id.camera_flash_btn);
        mDeleteBtn = findViewById(R.id.camera_delete_btn);
        mConfirmBtn = findViewById(R.id.camera_confirm_btn);
        mCameraContent = findViewById(R.id.camera_content);

        mBackBtn.setOnClickListener(this);
        mCameraFlashBtn.setOnClickListener(this);
        mCameraSnap.setOnClickListener(this);
        mCameraFrontBtn.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        // 校验权限
        final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        final List<String> permissionsToRequest = new ArrayList<>();

        for (String p : permissions) {
            if (!EasyPermissions.hasPermissions(this, p)) {
                permissionsToRequest.add(p);
            }
        }

        if (CollectionUtil.isEmpty(permissionsToRequest)) {
            initCameraView();
        } else {
            EasyPermissions.requestPermissions(this
                    , mPermissionTip.get(permissions[0])
                    , PermissionRequestCode.CAMERA_SNAPSHOT_PERMISSION_REQUEST_CODE
                    , permissions);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode != PermissionRequestCode.CAMERA_SNAPSHOT_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            initCameraView();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode != PermissionRequestCode.CAMERA_SNAPSHOT_PERMISSION_REQUEST_CODE) {
            return;
        }
        Toast.makeText(this, ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_permissions_denied"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void initCameraView() {
        File file = WeLikeFileManager.getPhotoSaveFile(WeLikeFileManager.APP_NAME + "_" + System.currentTimeMillis() + ImagePickConstant.JPEG_SUFFIX);
        mFilePath = file == null ? "" : file.getAbsolutePath();
        mCameraView = new CameraView(this);
        mCameraView.setAspectRatio(initAspectRadio());
        mCameraView.setFacing(CameraView.FACING_BACK);
        mCameraView.setFlash(CameraView.FLASH_OFF);
        mCameraContent.addView(mCameraView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCameraView.addCallback(new CameraView.Callback() {

            @Override
            public void onCameraOpened(CameraView cameraView) {
                enableAllBtn(true);
                showConfirmBtn(false);
            }

            @Override
            public void onCameraClosed(CameraView cameraView) {
                enableAllBtn(false);
            }

            @Override
            public void onPictureTaken(final CameraView cameraView, int nWidth, int nHeight, final byte[] data) {
                super.onPictureTaken(cameraView, nWidth, nHeight, data);
                cameraView.stop();
                // 做文件存储

                Schedulers.io().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (cameraView.getFacing() == CameraView.FACING_FRONT) {
                                // do Camera mirror
                                WeLikeFileManager.saveFrontCameraBitmap(mFilePath, data);
                            } else {
                                WeLikeFileManager.saveBackgroundCameraBitmap(mFilePath, data);
                            }

                        } catch (Throwable e) {
                            // do nothing
                        }
                    }
                });
                PhotoSnapShotActivity.this.mWidth = nWidth;
                PhotoSnapShotActivity.this.mHeight = nHeight;
                showConfirmBtn(true);
            }

        });
    }

    private void showConfirmBtn(boolean b) {
        mCameraSnap.setVisibility(b ? View.INVISIBLE : View.VISIBLE);
        mDeleteBtn.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        mConfirmBtn.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    private void enableAllBtn(boolean isEnable) {
        if (isEnable) {
            mCameraFrontBtn.setVisibility(View.VISIBLE);
            mCameraFlashBtn.setVisibility(View.VISIBLE);
            mCameraSnap.setEnabled(true);
        } else {
            mCameraFrontBtn.setVisibility(View.INVISIBLE);
            mCameraFlashBtn.setVisibility(View.INVISIBLE);
            mCameraSnap.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraView == null) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
                initCameraView();
            }
        } else {
            try {
                // 部分手机修改了权限控制，默认就是给权限，每次询问，添加了动态权限，在做统一修改
                if (!isShowConfirmBtn()) {
                    if (mCameraView != null) {
                        mCameraView.start();
                    }
                }
            } catch (Throwable e) {
                // do nothing
            }
        }

    }

    private boolean isShowConfirmBtn() {
        return mConfirmBtn.getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraView == null) {
            return;
        }
        try {
            if (!isShowConfirmBtn()) {
                if (mCameraView != null) {
                    mCameraView.stop();
                }
            }
        } catch (Throwable e) {
            // do nothing
        }
    }

    private AspectRatio initAspectRadio() {
        return AspectRatio.of(this.getResources().getDisplayMetrics().heightPixels, this.getResources().getDisplayMetrics().widthPixels);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if (v == mCameraSnap) {
            if (mCameraView != null && mCameraView.isCameraOpened()) {
                try {
                    mCameraView.takePicture();
                } catch (Throwable e) {
                    // do nothing
                }
            }
        } else if (v == mCameraFrontBtn) {
            try {
                if (mCameraView != null && mCameraView.isCameraOpened()) {
                    if (mCameraView.getFacing() == CameraView.FACING_BACK) {
                        mCameraView.setFacing(CameraView.FACING_FRONT);
                    } else {
                        mCameraView.setFacing(CameraView.FACING_BACK);
                    }
                }
            } catch (Exception e) {
                // do nothing
            }

        } else if (v == mCameraFlashBtn) {
            if (mCameraView != null && mCameraView.isCameraOpened()) {
                if (mCameraView.getFlash() == CameraView.FLASH_AUTO) {
                    mCameraView.setFlash(CameraView.FLASH_ON);
                    mCameraFlashBtn.setImageResource(R.drawable.camera_flash_on);
                } else if (mCameraView.getFlash() == CameraView.FLASH_ON) {
                    mCameraView.setFlash(CameraView.FLASH_OFF);
                    mCameraFlashBtn.setImageResource(R.drawable.camera_flash_off);
                } else if (mCameraView.getFlash() == CameraView.FLASH_OFF) {
                    mCameraView.setFlash(CameraView.FLASH_AUTO);
                    mCameraFlashBtn.setImageResource(R.drawable.camera_flash_auto);
                } else {
                    mCameraView.setFlash(CameraView.FLASH_AUTO);
                    mCameraFlashBtn.setImageResource(R.drawable.camera_flash_auto);
                }
            }
        } else if (v == mConfirmBtn) {
            Intent intent = new Intent();
            WeLikeFileManager.refreshGallery(this, mFilePath);
            intent.putExtra(VideoRecorderConstant.MEDIA_RESULT_PATH, mFilePath);
            intent.putExtra(VideoRecorderConstant.MEDIA_TYPE, mMediaType);
            intent.putExtra(VideoRecorderConstant.MEDIA_WIDTH, mWidth);
            intent.putExtra(VideoRecorderConstant.MEDIA_HEIGHT, mHeight);
            intent.setData(Uri.fromFile(new File(mFilePath)));
            this.setResult(Activity.RESULT_OK, intent);
            finish();
        } else if (v == mDeleteBtn) {
            WeLikeFileManager.deleteFile(mFilePath);
            try {
                if (mCameraView != null) {
                    mCameraView.start();
                }
            } catch (Throwable e) {
                // do nothing
            }

        }
    }

}
