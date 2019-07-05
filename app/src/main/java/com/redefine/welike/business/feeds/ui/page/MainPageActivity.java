package com.redefine.welike.business.feeds.ui.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.track.LogEvent;
import com.redefine.welike.business.common.ToastBusinessManager;
import com.redefine.welike.business.feeds.ui.contract.IMianContract;
import com.redefine.welike.commonui.framework.PageStackManager;
import com.redefine.welike.event.IEventDispatcher;
import com.redefine.welike.event.ILogDispatcher;
import com.redefine.welike.event.IMessageDispatcher;
import com.redefine.welike.event.LogEventDispatcher;
import com.redefine.welike.event.PageEventDispatcher;
import com.redefine.welike.event.PageMessageDispatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * updated by honlin on 2018/8/21.
 */
@Route(path = RouteConstant.PATH_MAIN_PAGE)
public class MainPageActivity extends BaseActivity {

    private IMianContract.IMainView mMainView;
    private IMianContract.IMainPresenter mMainPresenter;
    private ToastBusinessManager mToastManager;

    private IEventDispatcher mPageEventDispatcher;
    private IMessageDispatcher mMessageDispatcher;
    private ILogDispatcher mLogEventDuspatcher;



    public static void launcher(Context context) {

        Intent intent = new Intent();

        intent.setClass(context, MainPageActivity.class);

        context.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_main2, null);
        setContentView(rootView);
        mPageEventDispatcher = new PageEventDispatcher(new PageStackManager(this));
        mMessageDispatcher = new PageMessageDispatcher(null);
        mLogEventDuspatcher = new LogEventDispatcher();
        mMainView = IMianContract.MainPageFactory.createView(rootView, getIntent().getExtras(), savedInstanceState, this);
        mMainPresenter = IMianContract.MainPageFactory.createPresenter(mMainView);
        mToastManager = new ToastBusinessManager();
        mMainPresenter.onCreate(getIntent().getExtras(), savedInstanceState);
        mToastManager.onBind();
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mMainView.attach();
        mMainPresenter.showPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.destroy();
        mToastManager.onUnBind();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.onActivityResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.onActivityPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        mPageEventDispatcher.handleEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Message message) {
        mMessageDispatcher.handleMessage(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogEvent(LogEvent logEvent) {
        mLogEventDuspatcher.handleLogMessage(logEvent);
    }




//    @Override
//    public void onPageStateChanged(int oldPageState, int pageState) {
//        super.onPageStateChanged(oldPageState, pageState);
//        mMainPresenter.onPageStateChanged(oldPageState, pageState);
//    }


//    @Override //todo 2018 08 21
    public void onNewMessage(Message message) {
//        super.onNewMessage(message);
        mMainPresenter.onNewMessage(message);
    }
}
