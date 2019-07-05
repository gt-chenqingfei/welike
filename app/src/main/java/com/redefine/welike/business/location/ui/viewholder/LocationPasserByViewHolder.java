package com.redefine.welike.business.location.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.location.management.bean.LBSUser;
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

public class LocationPasserByViewHolder extends BaseRecyclerViewHolder<LBSUser> {

    private final VipAvatar mUserHeader;
    private final TextView mUserName;
    private final TextView mPasserByTime;
    private final TextView mUserIntroduction;
    private final UserFollowBtn mUserFollowBtn;
    private final String mPassHere;
    private final String mPassHereInMinute;
    private final String mPassHereInHour;
    private final String mPassHereInTime;
    private final IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

    public LocationPasserByViewHolder(View itemView) {
        super(itemView);
        mUserHeader = itemView.findViewById(R.id.location_passer_by_user_head);
        mUserName = itemView.findViewById(R.id.location_passer_by_user_name);
        mPasserByTime = itemView.findViewById(R.id.location_passer_by_time);
        mUserFollowBtn = itemView.findViewById(R.id.location_passer_by_follow_btn);
        mUserIntroduction = itemView.findViewById(R.id.location_passer_by_introduction);

        mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mUserFollowBtn, false);
        mPassHere = ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "location_pass_by_here");
        mPassHereInMinute = ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "location_pass_by_minute");
        mPassHereInHour = ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "location_pass_by_hour");
        mPassHereInTime = ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "location_pass_by_time");
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final LBSUser user) {
        super.bindViews(adapter, user);

//        HeadUrlLoader.getInstance().loadHeaderUrl(mUserHeader, user.getUser().getHeadUrl());
        VipUtil.set(mUserHeader, user.getUser().getHeadUrl(), user.getUser().getVip());
        VipUtil.setNickName(mUserName, user.getUser().getCurLevel(), user.getUser().getNickName());

        if (user.getUser().getSex() == UserBase.MALE) {
            mUserName.setCompoundDrawables(null, null, ResourceTool.getBoundDrawable(mUserName.getResources(), R.drawable.common_sex_male), null);
            mUserName.setCompoundDrawablePadding(ScreenUtils.dip2Px(5));
        } else if (user.getUser().getSex() == UserBase.FEMALE) {
            mUserName.setCompoundDrawables(null, null, ResourceTool.getBoundDrawable(mUserName.getResources(), R.drawable.common_sex_female), null);
            mUserName.setCompoundDrawablePadding(ScreenUtils.dip2Px(5));
        } else {
            mUserName.setCompoundDrawables(null, null, null, null);
            mUserName.setCompoundDrawablePadding(ScreenUtils.dip2Px(0));
        }

        mPasserByTime.setText(getPasserByTime(user.getPassTime()));
        mUserIntroduction.setText(user.getUser().getIntroduction());

        mFollowBtnPresenter.bindView(user.getUser().getUid(), user.getUser().isFollowing(), user.getUser().isFollower(), new FollowEventModel(EventConstants.FEED_PAGE_LOCATION_PASSERBY, null));
    }

    private String getPasserByTime(long passTime) {
        long offset = System.currentTimeMillis() - passTime;
        if (offset < DateTimeUtil.SECOND_60) {
            return mPassHere;
        } else if (offset < DateTimeUtil.HOUR_1) {
            return mPassHereInMinute;
        } else if (offset < DateTimeUtil.DAY_1) {
            return mPassHereInHour;
        } else {
            return String.format(mPassHereInTime, DateTimeUtil.INSTANCE.formatDateTime(passTime));
        }
    }
}
