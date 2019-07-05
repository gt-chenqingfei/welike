package com.redefine.welike.business.user.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by gongguan on 2018/1/16.
 */

public abstract class BaseUserHostViewHolder<T extends Object> extends RecyclerView.ViewHolder {
    protected View view;

    public BaseUserHostViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public abstract void bindViews(int position, T feedBase);
}
