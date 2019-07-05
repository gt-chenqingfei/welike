package com.redefine.welike.business.search.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pekingese.pagestack.framework.IPageStackManager;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.view.FitSystemWindowFrameLayout;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.search.ui.page.SearchResultPage;
import com.redefine.welike.commonui.framework.PageStackManager;

@Route(path = RouteConstant.PATH_SEARCH)
public class SearchResultActivity extends BaseActivity {
    private IPageStackManager mPageStackManager;
    private FrameLayout mRootView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageStackManager = new PageStackManager(this);
        mRootView = new FrameLayout(this);
        mRootView.setClipToPadding(false);
        mRootView.setFitsSystemWindows(true);
        mPageStackManager.bindPageStack(mRootView);
        setContentView(mRootView);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent == null) {
                finish();
                return;
            }
            Bundle extras = intent.getExtras();
            mPageStackManager.pushPage(new PageConfig.Builder(SearchResultPage.class).setPageBundle(extras)
                    .setUserGestureEnable(false)
                    .setFitSystemWindow(false)
                    .setCanDragFromEdge(false)
                    .setPushWithAnimation(false)
                    .setPopWithAnimation(false).build());
        }
    }

    @Override
    protected void onPause() {
        mPageStackManager.onActivityPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPageStackManager.onActivitySaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPageStackManager.onActivityRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageStackManager.onActivityResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPageStackManager.onActivityStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPageStackManager.onActivityStop();
    }

//    private byte mBackPressedCount = 0;

    @Override
    public void onBackPressed() {
        if (!mPageStackManager.onBackPressed()) {
//            mBackPressedCount++;
//            if (mBackPressedCount < 2) {
//                ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "click_back_again", false));
//            } else {
                super.onBackPressed();
//            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPageStackManager.onActivityDestroy();
    }
}
