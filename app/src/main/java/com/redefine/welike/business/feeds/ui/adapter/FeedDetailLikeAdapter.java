package com.redefine.welike.business.feeds.ui.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.loadmore.viewholder.WhiteBgLoadMoreViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.ui.viewholder.LikeViewHolder;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwb on 2018/1/12.
 */

public class FeedDetailLikeAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, User> {

    private List<User> mData = new ArrayList<User>();

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new LikeViewHolder(mInflater.inflate(R.layout.feed_detail_like_item, parent, false));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new WhiteBgLoadMoreViewHolder(mInflater.inflate(R.layout.load_more_layout, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, mData.get(position));
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
    protected User getRealItem(int position) {
        return mData.get(position);
    }

    public void addNewData(User user) {
        boolean isFound = false;
        for (User u : mData) {
            if (TextUtils.equals(u.getUid(), user.getUid())) {
                u.setSuperLikeExp(user.getSuperLikeExp());
                notifyDataSetChanged();
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            mData.add(0, user);
            notifyDataSetChanged();
        }
    }

    public void addNewData(List<User> users) {
        mData.clear();
        if (!CollectionUtil.isEmpty(users)) {
            mData.addAll(0, users);
        }
        notifyDataSetChanged();
    }

    public void addHisData(List<User> users) {
        if (CollectionUtil.isEmpty(users)) {
            return;
        }
        mData.addAll(users);
        notifyDataSetChanged();
    }
}
