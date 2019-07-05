package com.redefine.welike.business.topic.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.topic.management.bean.TopicUser;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class TopicUserViewHolder extends BaseRecyclerViewHolder<TopicUser> {

    private final VipAvatar mUserHeader;
    private final TextView mUserName;
    private final TextView mPasserByTime;
    private final TextView mUserIntroduction;
    private final UserFollowBtn mUserFollowBtn;
    private final String mPublishNow;
    private final String mPublishInMinute;
    private final String mPublishInHour;
    private final String mPublishInTime;
    private final IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

    public TopicUserViewHolder(View itemView) {
        super(itemView);
        mUserHeader = itemView.findViewById(R.id.topic_user_head);
        mUserName = itemView.findViewById(R.id.topic_user_name);
        mPasserByTime = itemView.findViewById(R.id.topic_user_time);
        mUserFollowBtn = itemView.findViewById(R.id.topic_user_follow_btn);
        mUserIntroduction = itemView.findViewById(R.id.topic_user_introduction);
        mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mUserFollowBtn, false);

        mPublishNow = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_publish_just_now");
        mPublishInMinute = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_publish_minutes");
        mPublishInHour = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_publish_hours");
        mPublishInTime = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "topic_user_publish_day");
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final TopicUser user) {
        super.bindViews(adapter, user);

//        HeadUrlLoader.getInstance().loadHeaderUrl(mUserHeader, user.user.getHeadUrl());
        VipUtil.set(mUserHeader,user.user.getHeadUrl(),user.user.getVip());
        VipUtil.setNickName(mUserName, user.user.getCurLevel(), user.user.getNickName());

        if (user.user.getSex() == UserBase.MALE) {
            mUserName.setCompoundDrawables(null, null , ResourceTool.getBoundDrawable(mUserName.getResources(), R.drawable.common_sex_male), null);
            mUserName.setCompoundDrawablePadding(ScreenUtils.dip2Px(5));
        } else if (user.user.getSex() == UserBase.FEMALE) {
            mUserName.setCompoundDrawables(null, null , ResourceTool.getBoundDrawable(mUserName.getResources(), R.drawable.common_sex_female), null);
            mUserName.setCompoundDrawablePadding(ScreenUtils.dip2Px(5));
        } else {
            mUserName.setCompoundDrawables(null, null , null, null);
            mUserName.setCompoundDrawablePadding(ScreenUtils.dip2Px(0));
        }

        mPasserByTime.setText(getPasserByTime(user.time));
        mUserIntroduction.setText(user.user.getIntroduction());

        mFollowBtnPresenter.bindView(user.user.getUid(), user.user.isFollowing(), user.user.isFollower(), new FollowEventModel(EventConstants.FEED_PAGE_TOPIC_USER, null));


    }

    private String getPasserByTime(long passTime) {
        long offset = System.currentTimeMillis() - passTime;
        if (offset < DateTimeUtil.SECOND_60) {
            return mPublishNow;
        } else if (offset < DateTimeUtil.HOUR_1) {
            return mPublishInMinute;
        } else if (offset < DateTimeUtil.DAY_1) {
            return mPublishInHour;
        } else {
            return String.format(mPublishInTime, DateTimeUtil.INSTANCE.formatDateTime(passTime));
        }
    }
}
