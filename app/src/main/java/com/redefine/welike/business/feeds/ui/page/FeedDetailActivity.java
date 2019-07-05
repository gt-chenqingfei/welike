package com.redefine.welike.business.feeds.ui.page;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailContract;
import com.redefine.welike.common.WindowUtil;
import com.redefine.welike.commonui.framework.PageStackManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by liwenbo on 2018/2/1.
 */
@Route(path = RouteConstant.PATH_FEED_DETAIL)
public class FeedDetailActivity extends BaseActivity {
    private IFeedDetailContract.IFeedDetailPresenter mPresenter;
    private int softInputMode = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = IFeedDetailContract.FeedDetailFactory.createPresenter(new PageStackManager(this), getIntent().getExtras());
        setContentView(mPresenter.createView(this, getIntent().getExtras()));
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT == event.id || event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            finish();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Message message) {
        if (message.what == MessageIdConstant.MESSAGE_COMMENT_PUBLISH) {
            onNewMessage(message);
        } else if (message.what == MessageIdConstant.MESSAGE_FORWARD_POST_PUBLISH) {
            onNewMessage(message);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowUtil.INSTANCE.resumeSoftInputMode(getWindow(), softInputMode);
        mPresenter.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        softInputMode = WindowUtil.INSTANCE.pauseSoftInputMode(getWindow());
        mPresenter.onActivityPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.destroy();
        mPresenter.onActivityDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_FEED_DETAIL);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    public Bundle onSaveInstanceState() {
        return mPresenter.onSaveInstanceState();
    }

    public void onNewMessage(Message message) {
        mPresenter.onNewMessage(message);
    }

    public static void launch(PostBase postBase, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, index);
        bundle.putSerializable(FeedConstant.KEY_FEED, postBase);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
    }

    public static void launch(String pid, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, index);
        bundle.putString(FeedConstant.KEY_FEED_ID, pid);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
    }

    public static void launch(String pid) {
        Bundle bundle = new Bundle();
        bundle.putString(FeedConstant.KEY_FEED_ID, pid);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
    }

    public static void launch(PostBase postBase) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FeedConstant.KEY_FEED, postBase);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
    }
}
