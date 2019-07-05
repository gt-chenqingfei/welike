package com.redefine.welike.business.feeds.ui.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.foundation.utils.NumberFormatUtil;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.contract.ICommonFeedHeadContract;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.view.CommonFeedHeadView;

/**
 * Created by liwb on 2018/1/10.
 */

public class CommonFeedHeadPresenter implements ICommonFeedHeadContract.ICommonFeedHeadPresenter {


    private final CommonFeedHeadView mView;
    private final IFeedOperationListener mListener;
    private final View mRootView;


    public CommonFeedHeadPresenter(View view, IFeedOperationListener listener) {
        mRootView = view;
        mView = ICommonFeedHeadContract.CommonFeedHeadFactory.createView(view);
        mView.setPresenter(this);
        mListener = listener;
    }

    @Override
    public void setIBrowseListener(IBrowseClickListener iBrowseListener) {
        if (mView != null)
            mView.setBrowseClickListener(iBrowseListener);
    }

    @Override
    public void bindViews(PostBase postBase, RecyclerView.Adapter adapter) {
        mView.bindViews(postBase, adapter);
        String formatReadCount = NumberFormatUtil.formatNumber(postBase.getReadCount());
        mView.setReadCount(formatReadCount);
    }

    @Override
    public void performClickDeleteBtn() {
        mView.performClickDeleteBtn();
    }

    @Override
    public void dismissFollowBtn(boolean b) {
        mView.dismissFollowBtn(b);
    }

    @Override
    public void onMenuBtnClick(PostBase postBase) {
        if (mListener != null) {
            mListener.onMenuBtnClick(mRootView.getContext(), postBase);
        }
    }

    @Override
    public void dismissArrowBtn(boolean b) {
        mView.dismissArrowBtn(b);
    }

    @Override
    public void showReadCount(boolean b) {
        mView.showReadCount(b);
    }

    @Override
    public CommonFeedHeadView getView() {
        return mView;
    }

}
