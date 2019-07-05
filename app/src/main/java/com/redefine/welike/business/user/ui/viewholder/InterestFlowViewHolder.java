package com.redefine.welike.business.user.ui.viewholder;

import android.view.View;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.widget.FlowLayout;
import com.redefine.welike.R;
import com.redefine.welike.business.user.management.bean.Interest;

/**
 * Created by honglin on 2018/6/29.
 */

public class InterestFlowViewHolder extends BaseRecyclerViewHolder<Interest> {


    public FlowLayout flInterest;


    public InterestFlowViewHolder(View itemView) {
        super(itemView);
        flInterest = itemView.findViewById(R.id.fl_interest);
    }


}
