package com.redefine.welike.business.topic.ui.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.topic.management.bean.TopicUser;
import com.redefine.welike.business.topic.ui.viewholder.TopicUserViewHolder;
import com.redefine.welike.business.user.management.FollowUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class TopicUserAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, TopicUser> implements FollowUserManager.FollowUserCallback{


    public TopicUserAdapter() {
        FollowUserManager.getInstance().register(this);
    }

    private final List<TopicUser> mData = new ArrayList<>();

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new TopicUserViewHolder(mInflater.inflate(R.layout.topic_user_item, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, getRealItem(position));
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return mData.size();
    }

    @Override
    protected TopicUser getRealItem(int position) {
        return mData.get(position);
    }

    public void addHisData(List<TopicUser> replies) {
        if (!CollectionUtil.isEmpty(replies)) {
            mData.addAll(replies);
        }
        notifyDataSetChanged();
    }


    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnFollow(uid);
        } else {
            refreshOnUnFollow(uid);
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnUnFollow(uid);
        } else {
            refreshOnFollow(uid);
        }
    }

    public void refreshOnFollow(String uid) {
        boolean hasFollowFeed = false;
        for (TopicUser user : mData) {
            if (TextUtils.equals(user.user.getUid(), uid)) {
                user.user.setFollowing(true);
                hasFollowFeed = true;
            }
        }
        if (hasFollowFeed) {
            notifyDataSetChanged();
        }
    }

    public void refreshOnUnFollow(String uid) {
        boolean hasFollowFeed = false;
        for (TopicUser user : mData) {
            if (TextUtils.equals(user.user.getUid(), uid)) {
                user.user.setFollowing(false);
                hasFollowFeed = true;
            }
        }
        if (hasFollowFeed) {
            notifyDataSetChanged();
        }
    }

    public void destroy() {
        FollowUserManager.getInstance().unregister(this);
    }
}
