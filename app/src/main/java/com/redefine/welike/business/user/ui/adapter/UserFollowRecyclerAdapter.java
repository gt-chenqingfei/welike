package com.redefine.welike.business.user.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.viewholder.UserFollowViewHolder;
import com.redefine.welike.commonui.event.expose.base.IDataProvider;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongguan on 2018/1/12.
 */

public class UserFollowRecyclerAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, User> implements IDataProvider<List<User>> {
    private List<User> contactsList;
    private String mSource;
//    private Animation mFollowAnim;

    public UserFollowRecyclerAdapter(String source) {
        contactsList = new ArrayList<>();
        mSource = source;
    }

    public void setSource(String source) {
        mSource = source;
    }

    @Override
    public List<User> getData() {
        return contactsList;
    }

    @Override
    public String getSource() {
        return mSource;
    }

    public void addNewData(List<User> mList) {
        if (!CollectionUtil.isEmpty(mList)) {
            contactsList.addAll(mList);
            notifyDataSetChanged();
        }
    }

    public void refreshData(List<User> mList) {
        contactsList.clear();
        if (!CollectionUtil.isEmpty(mList)) {
            contactsList.addAll(mList);
        }
        notifyDataSetChanged();
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View mView = mInflater.inflate(R.layout.user_follow_recycler_item, parent, false);
        return new UserFollowViewHolder(mView);
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, final int position) {
        if (holder instanceof UserFollowViewHolder) {
            final UserFollowViewHolder followerHolder = (UserFollowViewHolder) holder;

            followerHolder.bindViews(this, contactsList.get(position));
        }
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return contactsList != null ? contactsList.size() : 0;
    }

    @Override
    protected User getRealItem(int position) {
        return contactsList.get(position);
    }

    public void refreshOnFollow(String uid) {
        boolean hasFollowFeed = false;
        for (User user : contactsList) {
            if (TextUtils.equals(user.getUid(), uid)) {
                user.setFollowing(true);
                hasFollowFeed = true;
            }
        }
        if (hasFollowFeed) {
            notifyDataSetChanged();
        }
    }

    public void refreshOnUnFollow(String uid) {
        boolean hasFollowFeed = false;
        for (User user : contactsList) {
            if (TextUtils.equals(user.getUid(), uid)) {
                user.setFollowing(false);
                hasFollowFeed = true;
            }
        }
        if (hasFollowFeed) {
            notifyDataSetChanged();
        }
    }

//    private void startFollowAnim(UserFollowViewHolder holder) {
//        mFollowAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        mFollowAnim.setDuration(800);
//        mFollowAnim.setRepeatCount(Animation.INFINITE);
//        mFollowAnim.setInterpolator(new LinearInterpolator());
//        holder.mIvFollow.setImageResource(R.drawable.user_follow_roata);
//        holder.mIvFollow.setVisibility(View.VISIBLE);
//        holder.mIvFollow.startAnimation(mFollowAnim);
//    }

}