package com.redefine.welike.commonui.framework;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.pekingese.pagestack.framework.config.StatusBarConfig;
import com.pekingese.pagestack.framework.layer.BasePageStackLayer;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.view.BasePageStack;
import com.pekingese.pagestack.framework.view.PageStack;
import com.redefine.welike.R;
import com.redefine.welike.business.assignment.ui.contract.IAssignmentNotifyContract;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;
import com.redefine.welike.statistical.lifecyclehelper.PageLifecycleHelper;

import java.util.List;

/**
 * Created by liwenbo on 2018/2/1.
 */

public class PageStackManager extends BaseLifecyclePageStackManager implements BasePageStack.IPageStateChangeListener, ActivityCompat.PermissionCompatDelegate {

    private IAssignmentNotifyContract.IAssignmentNotifyPresenter mPresenter = IAssignmentNotifyContract.AssignmentFactory.createNotifyPresenter();
    private PageLifecycleHelper mPageHelper = new PageLifecycleHelper();

    public PageStackManager(Activity activity) {
        super(activity);
        StatusBarConfig.sDefaultColor = activity.getResources().getColor(R.color.app_color);
    }

    @Override
    protected BasePageStackLayer createPageStackLayer(Context context) {
        return new PageStackLayer(context);
    }

    @Override
    protected void initPageStack(PageStack pageStack) {
        super.initPageStack(pageStack);
        pageStack.registerPageStateChangeListener(this);
    }

    @Override
    public void onPageStateChange(BasePage basePage, int oldPageState, int newPageState) {
        mPresenter.onPageStateChange(basePage, oldPageState, newPageState);
        mPageHelper.onPageStateChange(basePage, oldPageState, newPageState);
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        mPageHelper.onActivityResume(getPageStack().getCurrentPage());
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        mPageHelper.onActivityPause(getPageStack().getCurrentPage());
    }

    public void onPermissionsGranted(int requestCode, List<String> perms) {
        int position = getPageStack().parseRequestCodeToPosition(requestCode);
        int realRequestCode = getPageStack().parseRequestCode(requestCode);
        BasePage basePage = getPageStack().getPage(position);
        if (basePage == null) {
            return ;
        }
        String[] permissions = new String[perms.size()];
        int[] grants = new int[perms.size()];
        for (int i = 0; i < perms.size(); i++) {
            permissions[i] = perms.get(i);
            grants[i] = PackageManager.PERMISSION_GRANTED;
        }
        basePage.onRequestPermissionsResult(realRequestCode, permissions, grants);
    }

    public void onPermissionsDenied(int requestCode, List<String> perms) {
        int position = getPageStack().parseRequestCodeToPosition(requestCode);
        int realRequestCode = getPageStack().parseRequestCode(requestCode);
        BasePage basePage = getPageStack().getPage(position);
        if (basePage == null) {
            return ;
        }
        String[] permissions = new String[perms.size()];
        int[] grants = new int[perms.size()];
        for (int i = 0; i < perms.size(); i++) {
            permissions[i] = perms.get(i);
            grants[i] = PackageManager.PERMISSION_DENIED;
        }
        basePage.onRequestPermissionsResult(realRequestCode, permissions, grants);
    }

    @Override
    public ActivityCompat.PermissionCompatDelegate getPermissionCompatDelegate() {
        return this;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
        activity.requestPermissions(permissions, requestCode);
        return true;
    }

    @Override
    public boolean onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        return false;
    }
}
