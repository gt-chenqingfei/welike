package com.redefine.welike.business.topic.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.topic.ui.contract.ITopicUserContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by liwenbo on 2018/3/21.
 */
@Route(path = RouteConstant.PATH_TOPIC_USER)
public class TopicUserActivity extends BaseActivity {

    private ITopicUserContract.ITopicUserPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = ITopicUserContract.TopicUserFactory.createPresenter(this, getIntent().getExtras());

        setContentView(mPresenter.createView(this, getIntent().getExtras()));

        EventBus.getDefault().register(this);
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


}
