package com.redefine.welike.business.easypost.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.redefine.welike.R;
import com.redefine.welike.business.easypost.ui.adapter.ThumbViewAdapter;

import java.util.List;

/**
 * Copyright (C) 2018 redefine , Inc.
 *
 * @author qingfei.chen
 * @date 2018-10-17 17:50:02
 */
public class CategoryThumbView extends LinearLayout implements ThumbViewAdapter.OnImageSelectedListener {
    private RecyclerView recyclerView;
    private ThumbViewAdapter adapter;
    private ViewPager viewPager;
    LinearLayoutManager layoutManager;

    public CategoryThumbView(Context context) {
        super(context);
        init(context);
    }

    public CategoryThumbView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_image_status_thumb_view, this);
        recyclerView = view.findViewById(R.id.layout_image_status_thumb_view_recycler_view);
        layoutManager = new LinearLayoutManager(context, HORIZONTAL, false);


        recyclerView.setLayoutManager(layoutManager);
        adapter = new ThumbViewAdapter(this);
        recyclerView.setAdapter(adapter);
    }


    public void attach(List<String> data, ViewPager viewPager) {
        this.adapter.notifyDataSetChange(data);
        this.viewPager = viewPager;
    }


    @Override
    public void onImageSelect(int position, String url) {
        this.viewPager.setCurrentItem(position, true);
    }

    public void smoothScrollToPosition(int position) {
        final int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        final int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        if (position >= lastVisiblePosition) {
            recyclerView.smoothScrollToPosition(position + 2);
        }

        if (position < firstVisiblePosition) {
            if (position - 2 > 0) {
                recyclerView.smoothScrollToPosition(position - 2);
            } else {
                recyclerView.smoothScrollToPosition(position);
            }
        }
    }
}
