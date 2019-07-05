package com.redefine.welike.business.feeds.ui.util;

import android.view.MotionEvent;
import android.view.View;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.contract.ISuperLikeContract;

/**
 * Created by liwenbo on 2018/3/23.
 */

public class SuperLikeListHelper implements View.OnTouchListener {

    private OnSuperLikeExpCallback mCallback;
    private View mTriggerView;
    private View mTargetView;

    private IPageStackManager mPageStackManager;
    private PostBase mPostBase;
    private int[] positions = new int[2];

    public SuperLikeListHelper() {

    }

    public void bindView(IPageStackManager pageStackManager, View triggerView, View targetView, PostBase postBase, OnSuperLikeExpCallback superLikeExpCallback) {
        mPageStackManager = pageStackManager;
        mCallback = superLikeExpCallback;
        mPostBase = postBase;
        mTriggerView = triggerView;
        mTargetView = targetView;
        mTriggerView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ISuperLikeContract.ISuperLikePresenter presenter = (ISuperLikeContract.ISuperLikePresenter) mPageStackManager.getPageStackLayer().getStackPresenter(ISuperLikeContract.ISuperLikePresenter.class);
        if (presenter != null) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPageStackManager.getPageStackLayer().getLocationInPageStack(mTargetView, positions);
                positions[0] = positions[0] + ScreenUtils.dip2Px(v.getContext(), 4.9F);
            }

            return presenter.onTouch(v, positions, event, mCallback, mPostBase.getSuperLikeExp());
        }
        return false;
    }
}
