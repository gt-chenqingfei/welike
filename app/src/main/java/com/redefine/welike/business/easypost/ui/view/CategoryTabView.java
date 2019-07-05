package com.redefine.welike.business.easypost.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.redefine.commonui.fresco.loader.BigPicUrlLoader;
import com.redefine.welike.R;
import com.redefine.welike.business.easypost.api.bean.PostStatus;
import com.redefine.welike.business.easypost.ui.adapter.CategoryViewAdapter;
import com.redefine.welike.business.easypost.ui.adapter.ThumbViewAdapter;

import java.util.List;

/**
 * Copyright (C) 2018 redefine , Inc.
 *
 * @author qingfei.chen
 * @date 2018-10-17 17:50:02
 */
public class CategoryTabView extends LinearLayout {
    private RecyclerView recyclerViewCategory;
    private CategoryViewAdapter adapterCategory;
    private LinearLayoutManager layoutManager;

    public CategoryTabView(Context context) {
        super(context);
        init(context);
    }

    public CategoryTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        this.recyclerViewCategory = new RecyclerView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.recyclerViewCategory.setLayoutParams(params);
        this.addView(recyclerViewCategory);
        this.layoutManager = new LinearLayoutManager(context, HORIZONTAL, false);

        this.recyclerViewCategory.setLayoutManager(layoutManager);
        this.adapterCategory = new CategoryViewAdapter();
        this.recyclerViewCategory.setAdapter(adapterCategory);
    }


    public void attach(List<PostStatus> data, CategoryViewAdapter.OnTabSelectedListener listener) {
        this.adapterCategory.setOnTabSelectedListener(listener);
        if (data == null) {
            return;
        }

        if (data.size() == 1) {
            this.setVisibility(View.INVISIBLE);
        }
        this.adapterCategory.notifyDataSetChange(data);
    }


}
