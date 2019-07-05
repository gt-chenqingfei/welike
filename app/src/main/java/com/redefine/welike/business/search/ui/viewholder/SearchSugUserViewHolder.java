package com.redefine.welike.business.search.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.management.bean.SugResult;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by liwenbo on 2018/3/14.
 */

public class SearchSugUserViewHolder extends BaseSearchSugViewHolder<SugResult> {
    private final VipAvatar mHeadView;
    private final TextView mName;
    private final TextView mFollower;
    private final String mFollowerText;
    private final TextView mIntroduction;
    private final TextView mLike;
    private final String mLikeText;

    public SearchSugUserViewHolder(View itemView) {
        super(itemView);
        mHeadView = itemView.findViewById(R.id.search_user_head);
        mName = itemView.findViewById(R.id.search_user_name);
        mIntroduction = itemView.findViewById(R.id.search_user_introduction);
        mFollower = itemView.findViewById(R.id.search_user_follow);
        mLike = itemView.findViewById(R.id.search_user_like);
        mFollowerText = ResourceTool.getString(ResourceTool.ResourceFileEnum.SEARCH,"search_item_follower_text");
        mLikeText = ResourceTool.getString(ResourceTool.ResourceFileEnum.SEARCH,"search_item_like_text");
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, SugResult sugResult) {
        super.bindViews(adapter, sugResult);
        User user = null;
        if (sugResult.getObj() instanceof User) {
            user = (User) sugResult.getObj();
        }
        if (user == null) {
            return ;
        }
//        HeadUrlLoader.getInstance().loadHeaderUrl(mHeadView, user.getHeadUrl());
        VipUtil.set(mHeadView,user.getHeadUrl(),user.getVip());

        mName.setText(addSpan(user.getNickName(), mCurrentSearchQuery));
        if (TextUtils.isEmpty(user.getIntroduction())) {
            mIntroduction.setVisibility(View.GONE);
        } else {
            mIntroduction.setVisibility(View.VISIBLE);
            mIntroduction.setText(user.getIntroduction());
        }
        if (user.getFollowedUsersCount() > 0) {
            mFollower.setVisibility(View.VISIBLE);
            mFollower.setText(String.format(mFollowerText, FeedHelper.getForwardCount(user.getFollowedUsersCount())));
        } else {
            mFollower.setVisibility(View.GONE);
        }
        if (user.getLikedMyPostsCount() > 0) {
            mLike.setVisibility(View.VISIBLE);
            mLike.setText(String.format(mLikeText, FeedHelper.getLikeCount(user.getLikedMyPostsCount())));

        } else {
            mLike.setVisibility(View.GONE);
        }
    }
}
