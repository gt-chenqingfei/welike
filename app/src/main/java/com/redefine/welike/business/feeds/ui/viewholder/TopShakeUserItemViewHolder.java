package com.redefine.welike.business.feeds.ui.viewholder;

import android.view.View;

import com.redefine.welike.R;
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by liwenbo on 2018/3/29.
 */

public class TopShakeUserItemViewHolder {

    private final View mItemView;
    private final VipAvatar mTopUserHeader;

    public TopShakeUserItemViewHolder(View itemView) {
        mItemView = itemView;
        mTopUserHeader = mItemView.findViewById(R.id.discover_top_user_header);
    }

    public void bindViews(int position, TopUserShakeBean.TopReviewUser topReviewUser) {
//        HeadUrlLoader.getInstance().loadHeaderUrl(, topReviewUser.getHead());
        VipUtil.set(mTopUserHeader,topReviewUser.getHead(),topReviewUser.getVip());
    }
}
