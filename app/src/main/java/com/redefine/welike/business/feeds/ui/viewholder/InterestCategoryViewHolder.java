package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.feeds.ui.adapter.DiscoverInterestCategoryAdapter;

import java.util.List;

public class InterestCategoryViewHolder{
    public interface onClickInterestListener {
        void onclickInterestItem(UserBase.Intrest intrest);
    }
    private onClickInterestListener mListener;

    public void setListener(onClickInterestListener listener) {
        mListener = listener;

    }
    private DiscoverInterestCategoryAdapter mAdapter;
    private RecyclerView mRecyclerView;
    public InterestCategoryViewHolder(View itemView) {
        mRecyclerView = (RecyclerView) itemView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new DiscoverInterestCategoryAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new DiscoverInterestCategoryAdapter.onClickInterestListener(){
            @Override
            public void onclickInterestItem(UserBase.Intrest intrest) {
                mListener.onclickInterestItem(intrest);
            }
        });

    }

    public void bindViews(List<UserBase.Intrest> list) {
        mAdapter.addInterestCategroy(list);
    }

//    @Override
//    public void onclickInterestItem(UserBase.Intrest intrest) {
//      mListener.onclickInterestItem(intrest);
//    }

}
