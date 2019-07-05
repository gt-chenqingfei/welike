package com.redefine.welike.business.user.ui.viewholder;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.business.im.ui.widget.GridSpaceItemDecoration;
import com.redefine.welike.business.user.management.bean.GroupedPhoto;
import com.redefine.welike.business.user.ui.adapter.ProfilePhotoAdapter;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * Created by nianguowang on 2018/10/14
 */
public class ProfileGroupPhotoViewHolder extends RecyclerView.ViewHolder {

    private static final int GRID_SPAN_COUNT = 3;

    private TextView mTime;
    private RecyclerView mRecyclerView;
    private ProfilePhotoAdapter mAdapter;

    private ProfilePhotoAdapter.OnAttachmentClickListener mListener;

    public void setAttachmentClickListener(ProfilePhotoAdapter.OnAttachmentClickListener listener) {
        mListener = listener;
    }

    public ProfileGroupPhotoViewHolder(View itemView) {
        super(itemView);
        mTime = itemView.findViewById(R.id.profile_photo_item_time);
        mRecyclerView = itemView.findViewById(R.id.profile_photo_item_recycler);
        mAdapter = new ProfilePhotoAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), GRID_SPAN_COUNT));
        mRecyclerView.addItemDecoration(new GridSpaceItemDecoration(DensityUtil.dp2px(3), GRID_SPAN_COUNT));
    }

    public void bindView(GroupedPhoto groupedPhoto) {
        if (groupedPhoto == null) {
            return;
        }
        mTime.setText(groupedPhoto.getShowTime());
        mAdapter.setData(groupedPhoto.getAttachments());
        mAdapter.setAttachmentClickListener(mListener);
    }
}
