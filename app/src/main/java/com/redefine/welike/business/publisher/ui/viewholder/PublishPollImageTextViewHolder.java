package com.redefine.welike.business.publisher.ui.viewholder;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.im.ui.widget.GridSpaceItemDecoration;
import com.redefine.welike.business.publisher.ui.adapter.PollImageAdapter;

import java.util.List;

public class PublishPollImageTextViewHolder {
    private final LayoutInflater mInflater;
    private final ViewGroup mPollImageRootView;
    private final RecyclerView mPollGridView;
    private PollImageAdapter mPollAdapter;
    private PollItemChangeListener listener;

    public PublishPollImageTextViewHolder(Context context, PollItemChangeListener listener) {
        this.listener = listener;
        mInflater = LayoutInflater.from(context);
        mPollImageRootView = (ViewGroup) mInflater.inflate(R.layout.publish_poll_image_layout, null);
        mPollGridView = mPollImageRootView.findViewById(R.id.poll_multi_grid_view);
        mPollAdapter = new PollImageAdapter(listener);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);


        GridSpaceItemDecoration gridSpaceItemDecoration = new GridSpaceItemDecoration(ScreenUtils.dip2Px(context, 4), 2);
        mPollGridView.setLayoutManager(layoutManager);
        mPollGridView.addItemDecoration(gridSpaceItemDecoration);
        mPollGridView.setAdapter(mPollAdapter);
    }

    public void bindViews(List<PollItemInfo> pollItemInfos) {
        mPollAdapter = new PollImageAdapter(listener);

        mPollGridView.setAdapter(mPollAdapter);
        mPollAdapter.setData(pollItemInfos);
    }

    public void bindGroup(ViewGroup mPollLayout) {
        mPollLayout.addView(mPollImageRootView);
    }


    public interface PollItemChangeListener {
        void onPollItemDelete(PollItemInfo pollItemInfo, final int position);

        void onPollEditorChange(PollItemInfo pollItemInfo, final int position);
    }
}
