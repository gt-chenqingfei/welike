package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.common.LikeManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by MR on 2018/1/20.
 */

public class LikeViewHolder extends BaseRecyclerViewHolder<User> {

    private final VipAvatar mHeader;
    private final TextView mNick;
    private final ImageView mLikeView;

    public LikeViewHolder(View itemView) {
        super(itemView);
        mHeader = itemView.findViewById(R.id.feed_detail_like_user_header);
        mNick = itemView.findViewById(R.id.feed_detail_like_user_nick);
        mLikeView = itemView.findViewById(R.id.feed_detail_like_item_view);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final User user) {
        super.bindViews(adapter, user);
//        HeadUrlLoader.getInstance().loadHeaderUrl(mHeader, user.getHeadUrl());
        VipUtil.set(mHeader, user.getHeadUrl(), user.getVip());
        VipUtil.setNickName(mNick, user.getCurLevel(), user.getNickName());

        updateLikeCount((int) user.getSuperLikeExp());
    }

    private void updateLikeCount(int exp) {
        mLikeView.setImageResource(LikeManager.getImage(exp));
    }
}
