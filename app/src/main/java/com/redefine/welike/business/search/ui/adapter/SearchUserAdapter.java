package com.redefine.welike.business.search.ui.adapter;

import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.ui.viewholder.FeedLoadMoreViewHolder;
import com.redefine.welike.business.search.ui.viewholder.SearchResultUserViewHolder;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by liwenbo on 2018/2/12.
 */

public class SearchUserAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, User> {

    private final List<User> mData = new ArrayList<>();

    public SearchUserAdapter() {
    }

    public void addNewData(List<User> users) {
        mData.clear();
        if (!CollectionUtil.isEmpty(users)) {
            mData.addAll(users);
        }
        notifyDataSetChanged();
    }

    public void addHisData(List<User> users) {
        if (!CollectionUtil.isEmpty(users)) {
            mData.addAll(users);
            notifyDataSetChanged();
        }
    }

    public int indexOfUser(User user) {
        if (mData.contains(user)) {
            return mData.indexOf(user);
        }
        return -1;
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new FeedLoadMoreViewHolder(mInflater.inflate(R.layout.feed_load_more_layout, parent, false));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new SearchResultUserViewHolder(mInflater.inflate(R.layout.search_user_item_layout, parent, false));
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

}
