package com.redefine.welike.business.setting.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.bean.CommonTextHeadBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;

public class BlockFollowingUserHeadViewHolder extends BaseRecyclerViewHolder<CommonTextHeadBean> {

    private final TextView mTitleView;

    public BlockFollowingUserHeadViewHolder(View itemView) {
        super(itemView);
        mTitleView = itemView.findViewById(R.id.block_user_head_title);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, CommonTextHeadBean data) {
        super.bindViews(adapter, data);
        mTitleView.setText(data.text);
    }
}
