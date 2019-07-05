package com.redefine.welike.business.feeds.ui.viewholder;

import android.view.View;
import android.view.ViewGroup;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean;

public class TopUserViewHolderDelegate {


    private final TopUserViewHolder mTopUserViewHolder;
  //  private final TopUserWithShakeViewHolder mTopWithShakeViewHolder;
    View rankView;

    public TopUserViewHolderDelegate() {
        mTopUserViewHolder = new TopUserViewHolder();
       // mTopWithShakeViewHolder = new TopUserWithShakeViewHolder();
    }

    public void createViewHolder(ViewGroup parent) {
        mTopUserViewHolder.createViewHolder(parent);
       // mTopWithShakeViewHolder.createViewHolder(parent);

    }

    public void bindView(ViewGroup parent, TopUserShakeBean topUser) {
        parent.removeAllViews();
        if (topUser == null || CollectionUtil.getCount(topUser.mTopUsers) < 3) {
            parent.setVisibility(View.GONE);
            return;
        }
        parent.setVisibility(View.VISIBLE);
//        mTopUserViewHolder.guideListener = guideListener;
        mTopUserViewHolder.bindView(parent, topUser);
        rankView = mTopUserViewHolder.mRootView;
      /*  if (!TextUtils.isEmpty(topUser.shakeUrl)) {
            mTopWithShakeViewHolder.guideListener = guideListener;
            mTopWithShakeViewHolder.bindView(parent, topUser);
            rankView = mTopWithShakeViewHolder.mTopUserRootView;
        } else {
            mTopUserViewHolder.guideListener = guideListener;
            mTopUserViewHolder.bindView(parent, topUser);
            rankView = mTopUserViewHolder.mRootView;
        }*/
//        guideListener.onShow(null, null);
    }

    public View getRankView() {
        return rankView;
    }

//    public GuideListener<View> guideListener;
}
