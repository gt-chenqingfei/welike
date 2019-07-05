package com.redefine.welike.business.location.ui.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.feeds.ui.viewholder.FeedLoadMoreViewHolder;
import com.redefine.welike.business.location.management.bean.LBSUser;
import com.redefine.welike.business.location.ui.viewholder.LocationPasserByViewHolder;
import com.redefine.welike.business.user.management.FollowUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class LocationPasserByAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, LBSUser> implements FollowUserManager.FollowUserCallback{


    public LocationPasserByAdapter() {
        FollowUserManager.getInstance().register(this);
    }

    private final List<LBSUser> mData = new ArrayList<>();

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new LocationPasserByViewHolder(mInflater.inflate(R.layout.location_passer_by_item, parent, false));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new FeedLoadMoreViewHolder(mInflater.inflate(R.layout.feed_load_more_layout, parent, false));

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
    protected LBSUser getRealItem(int position) {
        return mData.get(position);
    }

    public void addHisData(List<LBSUser> replies) {
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
        for (LBSUser user : mData) {
            if (TextUtils.equals(user.getUser().getUid(), uid)) {
                user.getUser().setFollowing(true);
                hasFollowFeed = true;
            }
        }
        if (hasFollowFeed) {
            notifyDataSetChanged();
        }
    }

    public void refreshOnUnFollow(String uid) {
        boolean hasFollowFeed = false;
        for (LBSUser user : mData) {
            if (TextUtils.equals(user.getUser().getUid(), uid)) {
                user.getUser().setFollowing(false);
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
