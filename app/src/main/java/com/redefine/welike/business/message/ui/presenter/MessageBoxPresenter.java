package com.redefine.welike.business.message.ui.presenter;

import android.text.TextUtils;

import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.message.management.NotificationBoxManager;
import com.redefine.welike.business.message.management.bean.NotificationBase;
import com.redefine.welike.business.message.management.provider.INotifyCallback;
import com.redefine.welike.business.message.ui.adapter.MessageBoxAdapter;
import com.redefine.welike.business.message.ui.constant.MessageConstant;
import com.redefine.welike.business.message.ui.contract.IMessageBoxContract;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/10.
 */

public class MessageBoxPresenter implements IMessageBoxContract.IMessageBoxPresenter
        , INotifyCallback, OnClickRetryListener {

    private NotificationBoxManager mModel;
    private final MessageBoxAdapter mAdapter;
    private boolean isFirstCreate = true;
    private int news = 0;//新消息的数量。
    String frameType;
    IMessageBoxContract.IMessageBoxView view;

    public MessageBoxPresenter(String type, int news, IMessageBoxContract.IMessageBoxView view) {
        frameType = type;
        this.view = view;
        this.news = news;
        mAdapter = new MessageBoxAdapter();
        mModel = new NotificationBoxManager();
    }

    @Override
    public void init() {
        String messageBoxType = null;
        if (TextUtils.equals(frameType, MessageConstant.FRAGMNET_COMMENT)) {
            messageBoxType = "COMMENT";
//            TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_MESSAGE_BOX_COMMENT);
//            TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
        } else if (TextUtils.equals(frameType, MessageConstant.FRAGMNET_LIKE)) {
            messageBoxType = "LIKE";
//            TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_MESSAGE_BOX_LIKE);
//            TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
        } else if(TextUtils.equals(frameType, MessageConstant.FRAGMNET_ME)){
            messageBoxType = "MENTION";
//            TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_MESSAGE_BOX_MENTION);
//            TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
        }
        else if(TextUtils.equals(frameType, MessageConstant.FRAGMNET_PUSH)){
            messageBoxType = "OPERATE";

        }
        mModel.setNotificationType(messageBoxType);
        mModel.setListener(this);
        view.setAdapter(mAdapter);
        mAdapter.setRetryLoadMoreListener(this);
        if (isFirstCreate) {
            isFirstCreate = false;
            view.autoRefresh();
            view.showLoading();
        }
    }

    @Override
    public void onRefreshNotification(List<NotificationBase> posts, int errCode) {
//        if (mModel == manager) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.addNewData(posts, news);
            news = 0; //只生效一次。
            mAdapter.setMagic(view.getMagicLength());
            mAdapter.clearFinishFlag();
            int size = CollectionUtil.getCount(posts);
            // 提醒用户刷新成功条数
            if (size == 0 && mAdapter.getRealItemCount() == 0) {
                view.showEmptyView();
            } else {
                view.showContent();
            }
        } else {
            if (mAdapter.getRealItemCount() == 0) {
                view.showErrorView();
            } else {
                view.showContent();
            }
            // 网络失败给用户提醒
        }
        view.finishRefresh();
//        }
        mAdapter.showFooter();
    }

    @Override
    public void onReceiveHisNotification(List<NotificationBase> posts, boolean last, int errCode) {
//        if (mModel == manager) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mAdapter.addHisData(posts);
            mAdapter.setMagic(view.getMagicLength());
            if (last) {
                mAdapter.noMore();
            } else {
                mAdapter.finishLoadMore();
            }
        } else {
            mAdapter.loadError();
        }
//        }
        view.setRefreshEnable(true);
    }


    @Override
    public void destroy() {
        mModel.setListener(null);
    }

    @Override
    public void onRefresh() {
        if (mAdapter.getRealItemCount() == 0) {
            view.showLoading();
        }
        mAdapter.hideFooter();
        mModel.tryRefreshPosts();
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        view.setRefreshEnable(false);
        mAdapter.onLoadMore();
        mModel.tryHisPosts();
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }
}
