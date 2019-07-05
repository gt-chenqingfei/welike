package com.redefine.multimedia.recorder.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.redefine.foundation.utils.VersionUtil;
import com.redefine.multimedia.R;
import com.redefine.multimedia.recorder.contract.IVideoRecorderContract;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liwenbo on 2018/4/8.
 */

public class VideoRecorderActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private IVideoRecorderContract.IVideoRecorderPresenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setStatusBarJ(this);
        super.onCreate(savedInstanceState);
        if (VersionUtil.isLower5_0()) {
            setContentView(R.layout.video_surface_recorder_layout);
        } else {
            setContentView(R.layout.video_recorder_layout);
        }
        mPresenter = IVideoRecorderContract.VideoRecorderFactory.createPresenter(getIntent());
        mPresenter.initViews(this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }



    @Override
    protected void onPause() {
        mPresenter.onActivityPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mPresenter.onActivityResume();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        mPresenter.onBackPressed();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        mPresenter.onPermissionsGranted(requestCode, perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        mPresenter.onPermissionsDenied(requestCode, perms);
    }
}
