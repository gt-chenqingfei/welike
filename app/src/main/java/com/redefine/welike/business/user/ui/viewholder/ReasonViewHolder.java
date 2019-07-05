package com.redefine.welike.business.user.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.user.management.bean.DeactivateReasonBean;

/**
 * Created by honglin on 2018/5/16.
 */

public class ReasonViewHolder extends BaseRecyclerViewHolder<DeactivateReasonBean> {

    public TextView tvTitle;
    public ImageView cbCheck;
    public SimpleDraweeView simplePic;
    public RelativeLayout rlItem;


    public ReasonViewHolder(View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.tv_reason_title);
        cbCheck = itemView.findViewById(R.id.cb_reason_check);
        simplePic = itemView.findViewById(R.id.simple_reason_pic);
        rlItem = itemView.findViewById(R.id.rl_item);


    }
}
