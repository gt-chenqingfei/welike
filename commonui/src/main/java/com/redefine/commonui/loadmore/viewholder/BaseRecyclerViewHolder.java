package com.redefine.commonui.loadmore.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by MR on 2018/1/14.
 */

public class BaseRecyclerViewHolder<T extends Object> extends RecyclerView.ViewHolder {


    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public void bindViews(RecyclerView.Adapter adapter, T data) {
    }

    public void onAttachToWindow() {}

    public void onDetachToWindow() {}
}
