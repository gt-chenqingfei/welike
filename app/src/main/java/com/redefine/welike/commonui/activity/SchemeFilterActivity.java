package com.redefine.welike.commonui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.commonui.share.outshare.OutShareDelegate;
import com.redefine.welike.event.RouteDispatcher;
import com.redefine.welike.statistical.EventLog;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liwenbo on 2018/3/16.
 */

public class SchemeFilterActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private OutShareDelegate mOutShareDelegate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            if (TextUtils.equals(getIntent().getAction(), Intent.ACTION_SEND)
                    || TextUtils.equals(getIntent().getAction(), Intent.ACTION_SEND_MULTIPLE)) {
                handlePublish();
            } else {
                if (getIntent().getData() != null) {
                    handleSchemeFilter();
                } else {
                    ARouter.getInstance().build(RouteConstant.SPLASH_ROUTE_PATH).navigation(this);
                }
                finish();
            }
        } else {
            ARouter.getInstance().build(RouteConstant.SPLASH_ROUTE_PATH).navigation(this);
//            Uri uri = Uri.parse("welike://com.redefine.welike/main/home?page_name=post_detail&pid=c3466814-1b60-4910-8585-d7b9e4f21a6d");
//            uri = Uri.parse(uri.toString().replace(RouteConstant.MAIN_ROUTE_PATH, RouteConstant.AVOID_ROUTE_PATH));
//            ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
            finish();
        }
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_OPEN_APP);
        EventLog.LaunchApp.report1(1);
    }

    private void handleSchemeFilter() {
        Uri uri = getIntent().getData();
        boolean validUri = RouteDispatcher.validUri(uri);
        Account account = AccountManager.getInstance().getAccount();
        if (account != null &&
                account.getCompleteLevel() >= Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE &&
                account.isLogin()) {
            if (validUri) {
                ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
            } else {
                ARouter.getInstance().build(RouteConstant.SPLASH_ROUTE_PATH).navigation(this);
            }
        } else {
            if (validUri) {
                try {
                    uri = Uri.parse(uri.toString().replace(RouteConstant.MAIN_ROUTE_PATH, RouteConstant.AVOID_ROUTE_PATH));
                    ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
                } catch (Exception e) {
                    e.printStackTrace();
                    ARouter.getInstance().build(RouteConstant.SPLASH_ROUTE_PATH).navigation(this);
                }
            } else {
                ARouter.getInstance().build(RouteConstant.SPLASH_ROUTE_PATH).navigation(this);
            }
        }
    }

    private void handlePublish() {
        mOutShareDelegate = new OutShareDelegate(this);
        mOutShareDelegate.setFinishActivityCallback(new OutShareDelegate.FinishActivityCallback() {
            @Override
            public void finishActivity(int resultCode, Intent data) {
                setResult(resultCode, data);
                finish();
            }
        });
        mOutShareDelegate.setRequestPermissionCallback(new OutShareDelegate.RequestPermissionCallback() {
            @Override
            public void onRequestPermission(String message, int requestCode, String... permissions) {
                EasyPermissions.requestPermissions(SchemeFilterActivity.this, message, requestCode, permissions);
            }
        });
        mOutShareDelegate.init(getIntent());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        mOutShareDelegate.onPermissionsGranted(requestCode, perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        mOutShareDelegate.onPermissionsDenied(requestCode, perms);
    }
}
