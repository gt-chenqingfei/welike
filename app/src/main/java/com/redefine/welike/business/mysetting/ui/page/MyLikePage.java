package com.redefine.welike.business.mysetting.ui.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.mysetting.ui.contract.IMyLikeContract;

/**
 *
 * Created by gongguan on 2018/2/23.
 */
@Route(path = RouteConstant.MY_LIKE_ROUTE_PATH)
public class MyLikePage extends BaseActivity {

    private IMyLikeContract.IMyLikePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        mPresenter = IMyLikeContract.IMyLikeFactory.createPresenter(this, intent.getExtras());
        View view = mPresenter.createView(this, savedInstanceState);
        setContentView(view);
        mPresenter.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onActivityPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
        mPresenter.destroy();
    }
}
