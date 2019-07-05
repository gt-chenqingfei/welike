package com.redefine.welike.business.feeds.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.LoadMoreBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.feeds.ui.viewholder.BackToTopViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.InterestHeaderViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.InterestUserViewHolder;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/4/16
 */
public class InterestCategoryRecyclerAdapter extends LoadMoreFooterRecyclerAdapter<List<Banner>, User> {

    private final List<User> mUserList = new ArrayList<>();
    private boolean mResumed;

    public InterestCategoryRecyclerAdapter() {
        setHeader(new ArrayList<Banner>());
        setFooter(new LoadMoreBean());
        setOnFooterItemClickListener(this);

    }

    public void setBanner(List<Banner> bannerList) {
        if(CollectionUtil.isEmpty(bannerList)) {
            hideHeader();
            return;
        }
        List<Banner> header = getHeader();
        if(header != null) {
            header.clear();
            header.addAll(bannerList);
            notifyItemChanged(getHeaderPosition());
        }
    }

    public void addNewData(List<User> users) {
        if(!CollectionUtil.isEmpty(users)) {
            mUserList.clear();
            mUserList.addAll(users);
            notifyDataSetChanged();
        }
    }

    public void addHisData(List<User> users) {
        if(CollectionUtil.isEmpty(users)) {
            return;
        }
        mUserList.addAll(users);
        notifyDataSetChanged();
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.user_follow_recycler_item1, parent, false);
        return new InterestUserViewHolder(view);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.banner_layout, parent, false);
        return new InterestHeaderViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        if(holder instanceof InterestUserViewHolder) {
            ((InterestUserViewHolder) holder).bindViews(this, getRealItem(position));
        }
    }

    @Override
    protected void onBindHeaderViewHolder(BaseRecyclerViewHolder holder, int position) {
        if(holder instanceof InterestHeaderViewHolder) {
            ((InterestHeaderViewHolder) holder).bindViews(this, getHeader());
            ((InterestHeaderViewHolder) holder).setCurrentActivityState(mResumed);
        }
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new BackToTopViewHolder(mInflater.inflate(R.layout.back_to_top_item, parent, false));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindFooterViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, getFooter());
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return mUserList.size();
    }

    @Override
    protected User getRealItem(int position) {
        if(position < mUserList.size()) {
            return mUserList.get(position);
        }
        return null;
    }

    public void onActivityResume() {
        mResumed = true;
    }

    public void onActivityPause() {
        mResumed = false;
    }

    public void refreshOnUnFollow(String uid) {
        for (int i = 0; i < mUserList.size(); i++) {
            if (TextUtils.equals(mUserList.get(i).getUid(), uid)) {
                mUserList.get(i).setFollowing(false);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void refreshOnFollow(String uid) {
        for (int i = 0; i < mUserList.size(); i++) {
            if (TextUtils.equals(mUserList.get(i).getUid(), uid)) {
                mUserList.get(i).setFollowing(true);
                notifyDataSetChanged();
                break;
            }
        }
    }


}
