package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;

public class InterestCategoryItemViewHolder extends RecyclerView.ViewHolder {

    public SimpleDraweeView mInterestImg;
    public TextView mInterestTag;

    public InterestCategoryItemViewHolder(View itemView) {
        super(itemView);

        mInterestImg = itemView.findViewById(R.id.discover_interest_category_img);
        mInterestTag = itemView.findViewById(R.id.discover_interest_category_tag);
    }

    public void bindViews(UserBase.Intrest intrest) {
        if (intrest != null) {
            if (intrest.isShowIcon()) {
                mInterestImg.setVisibility(View.VISIBLE);
                if (TextUtils.equals(intrest.getIid(), BrowseConstant.INTEREST_VIDEO)) {
                    mInterestImg.setImageResource(R.drawable.ic_feed_video_interest);
                } else {
                    ViewGroup.LayoutParams params = mInterestImg.getLayoutParams();
                    HeadUrlLoader.getInstance().supportLoad(mInterestImg, intrest.getIcon(), params.width, params.height);
                }
            } else {
                mInterestImg.setVisibility(View.GONE);
            }
            mInterestTag.setText(intrest.getLabel());

            mInterestTag.setTextColor(intrest.isSelected() ? mInterestTag.getContext().getResources().getColor(R.color.discover_table_layout_indicator) :
                    mInterestTag.getContext().getResources().getColor(R.color.feed_detail_item_user_nick_text_color));
        }
    }


}
