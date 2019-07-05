package com.redefine.welike.business.user.ui.viewholder;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.user.management.bean.Interest;

/**
 * Created by honglin on 2018/6/29.
 */

public class InterestFirstViewHolder extends BaseRecyclerViewHolder<Interest> {


    public TextView tvTitle;
    private int viewWidth = 0;

    public InterestFirstViewHolder(View itemView) {
        super(itemView);
        viewWidth = (ScreenUtils.getSreenWidth(itemView.getContext()) - ScreenUtils.dip2Px(itemView.getContext(), 60)) / 2;

        tvTitle = itemView.findViewById(R.id.tv_interest_name);


        ConstraintLayout.LayoutParams cl = new ConstraintLayout.LayoutParams(
                viewWidth - 1, ViewGroup.LayoutParams.WRAP_CONTENT);


        tvTitle.setLayoutParams(cl);

    }


}
