package com.redefine.welike.business.assignment.ui.page;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.foundation.framework.Event;
import com.redefine.multimedia.photoselector.constant.ImagePickConstant;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.welike.base.constant.CommonRequestCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.assignment.ui.contract.IWebViewPageContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/16.
 */
@Route(path = RouteConstant.WEB_VIEW_ROUTE_PATH)
public class WebViewPage extends BaseActivity {

    private IWebViewPageContract.IWebViewPagePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Bundle extras = intent.getExtras();
        mPresenter = IWebViewPageContract.WebViewPageFactory.createPresenter(this, extras);
        View view = mPresenter.createView(this, savedInstanceState);
        setContentView(view);
        mPresenter.attach();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Message message) {
        mPresenter.onNewMessage(message);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT == event.id || event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonRequestCode.WEB_CHOOSE_PIC_CODE) {
            List<Uri> result = new ArrayList<>();
            if (resultCode == RESULT_OK) {
                ArrayList<Item> items = data.getParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS);
                if (items != null && items.size() > 0) {
                    for (Item item: items) {
                        result.add(item.uri);
                    }
                }
            }

            mPresenter.onWebFileChosenResult(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
        mPresenter.destroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.interceptGoBackPressed()) {
            super.onBackPressed();
        }
    }

}
