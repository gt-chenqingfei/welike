package com.redefine.welike.business.topic.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.topic.ui.contract.ITopicLandingContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by liwenbo on 2018/3/20.
 */
@Route(path = RouteConstant.PATH_TOPIC_LANDING)
public class TopicLandingActivity extends BaseActivity {

    public static final int TAB_HOT = 0;
    public static final int TAB_LATEST = 1;

    private ITopicLandingContract.ITopicLandingPresenter mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = ITopicLandingContract.TopicLandingFactory.createPresenter(this, getIntent().getExtras());
        setContentView(mPresenter.createView(this, getIntent().getExtras()));
        EventBus.getDefault().register(this);
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_TOPIC_DETAIL);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT == event.id || event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onActivityPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onActivityResume();
    }
}
