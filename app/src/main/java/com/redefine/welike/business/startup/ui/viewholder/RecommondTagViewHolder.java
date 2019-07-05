package com.redefine.welike.business.startup.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.welike.R;

/**
 * Created by wangnianguo on 2018/4/11.
 */

public class RecommondTagViewHolder extends RecyclerView.ViewHolder {
    public TextView mTagName;
    public ImageView mChecked;

    public RecommondTagViewHolder(View itemView) {
        super(itemView);
        mTagName = itemView.findViewById(R.id.tv_tag_name);
        mChecked = itemView.findViewById(R.id.iv_tag_check);
    }

    public void bindViews(String tagName) {
        mTagName.setText(tagName);
    }

}
