package com.redefine.welike.business.user.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;

/**
 * Created by liwenbo on 2018/3/24.
 */

public class InterestCategoryViewHolder extends BaseRecyclerViewHolder<UserBase.Intrest> {

    private final SimpleDraweeView mIcon;
    private final TextView mCategory;

    public InterestCategoryViewHolder(View itemView) {
        super(itemView);
        mIcon = itemView.findViewById(R.id.interest_category_icon);
        mCategory = itemView.findViewById(R.id.interest_category);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, UserBase.Intrest data) {
        super.bindViews(adapter, data);
        mIcon.setImageURI(data.getIcon());
        mCategory.setText(data.getLabel());
    }
}
