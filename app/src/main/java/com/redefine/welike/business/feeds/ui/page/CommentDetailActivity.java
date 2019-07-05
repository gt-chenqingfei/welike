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
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.ICommentDetailContract;
import com.redefine.welike.common.WindowUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by liwenbo on 2018/2/2.
 */
@Route(path = RouteConstant.PATH_FEED_COMMENT_DETAIL)
public class CommentDetailActivity extends BaseActivity {
    private int softInputMode = -1;
    private ICommentDetailContract.ICommentDetailPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = ICommentDetailContract.CommentDetailFactory.createPresenter(this, getIntent().getExtras());
        setContentView(mPresenter.createView(this, getIntent().getExtras()));
        EventBus.getDefault().register(this);
    }

    //    @Override
//    protected IFeedDetailContract.IFeedDetailPresenter createPresenter() {
//        return IFeedDetailContract.FeedDetailFactory.createPresenter(mPageStackManager, mPageConfig.pageBundle);
//    }
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
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_COMMENT_DETAIL);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowUtil.INSTANCE.resumeSoftInputMode(getWindow(), softInputMode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        softInputMode = WindowUtil.INSTANCE.pauseSoftInputMode(getWindow());
    }

    public void onNewMessage(Message message) {
        mPresenter.onNewMessage(message);
    }

    public static void launch(PostBase postBase, Comment comment) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FeedConstant.KEY_COMMENT, comment);
        bundle.putSerializable(FeedConstant.KEY_FEED, postBase);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_COMMENT_DETAIL_EVENT, bundle));
    }
}
