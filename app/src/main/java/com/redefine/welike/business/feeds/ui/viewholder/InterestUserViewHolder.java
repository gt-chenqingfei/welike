package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;

/**
 * Created by gongguan on 2018/1/12.
 */

public class InterestUserViewHolder extends BaseRecyclerViewHolder<User> {
    protected TextView tv_nickName, tv_introduce, mTvFollower;
    public UserFollowBtn mIvFollow;
    private AppCompatImageView mSex;
    private User mUser;
    private VipAvatar simpleHeadView;
    private IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

    public InterestUserViewHolder(View itemView) {
        super(itemView);
        initViews();
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, User user) {
        bindEvents(user);
    }

    private void initViews() {
        tv_nickName = itemView.findViewById(R.id.tv_user_follow_recycler_nickName);
        simpleHeadView = itemView.findViewById(R.id.simpleView_user_follow_recycler);
        tv_introduce = itemView.findViewById(R.id.tv_user_follow_recycler_introduce);
        mTvFollower = itemView.findViewById(R.id.tv_user_follow_recycler_follower);
        mIvFollow = itemView.findViewById(R.id.iv_user_follow_followBtn);
        mSex = itemView.findViewById(R.id.iv_user_sex);
        mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mIvFollow, false);
    }

    private void bindEvents(User user) {
        if(user == null) {
            return;
        }
        mUser = user;

        tv_introduce.setText(user.getIntroduction());
        String followerText = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_follower_num_text");
        String followerStr = followerText + ":\t" + String.valueOf(user.getFollowedUsersCount());
        mTvFollower.setText(followerStr);

        if(user.getSex() == UserBase.FEMALE) {
            mSex.setImageResource(R.drawable.common_sex_female);
        } else {
            mSex.setImageResource(R.drawable.common_sex_male);
        }

//        HeadUrlLoader.getInstance().loadHeaderUrl(simpleHeadView, user.getHeadUrl());
        VipUtil.set(simpleHeadView,user.getHeadUrl(),user.getVip());
        VipUtil.setNickName(tv_nickName, user.getCurLevel(), user.getNickName());

//        if (TextUtils.equals(user.getUid(), AccountManager.getInstance().getAccount().getUid())) {
//            mIvFollow.setVisibility(View.INVISIBLE);
//        } else {
//            mIvFollow.setVisibility(View.VISIBLE);
//        }
//
//        if (user.isFollowing()) {
//            if (user.isFollower()) {
//                mIvFollow.setImageResource(R.drawable.user_follower_btn);
//            } else {
//                mIvFollow.setImageResource(R.drawable.user_following_btn);
//            }
//        } else {
//            mIvFollow.setImageResource(R.drawable.user_follow_btn);
//        }
        mFollowBtnPresenter.bindView(user.getUid(), user.isFollowing(), user.isFollower(), new FollowEventModel("INVALID", null));
//        mFollowBtnPresenter.setFollowCallback(this);

        setOnclick(user);

    }

    public void setOnclick(final User user) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHostPage.launch(true, user.getUid());
            }
        });
    }
}
