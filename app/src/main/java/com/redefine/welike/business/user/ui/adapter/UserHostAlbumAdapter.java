package com.redefine.welike.business.user.ui.adapter;

import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.user.management.UserDetailManager;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.viewholder.UserHostAlbumViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongguan on 2018/1/16.
 */

public class UserHostAlbumAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, UserDetailManager.UserAlbumPic> {

    private final List<UserDetailManager.UserAlbumPic> dataList;

    public UserHostAlbumAdapter() {
        dataList = new ArrayList<UserDetailManager.UserAlbumPic>();
    }

    public void addHisData(List<UserDetailManager.UserAlbumPic> dataList) {
        if (!CollectionUtil.isEmpty(dataList)) {
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }

    }

    public void addNewData(List<UserDetailManager.UserAlbumPic> dataList) {
        this.dataList.clear();
        if (!CollectionUtil.isEmpty(dataList)) {
            this.dataList.addAll(0, dataList);
        }
        notifyDataSetChanged();
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new UserHostAlbumViewHolder(UserConstant.ALBUM_SPAN_COUNT, mInflater.inflate(R.layout.user_host_album_item, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.bindViews(this, dataList.get(position));
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    protected UserDetailManager.UserAlbumPic getRealItem(int position) {
        return dataList.get(position);
    }

    public List<UserDetailManager.UserAlbumPic> getData() {
        return dataList;
    }
}
