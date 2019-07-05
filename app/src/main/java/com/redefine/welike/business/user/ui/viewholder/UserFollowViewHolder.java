package com.redefine.welike.business.user.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.adapter.UserFollowRecyclerAdapter;
import com.redefine.welike.business.user.ui.adapter.UserFollowingRecyclerAdapter;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by gongguan on 2018/1/12.
 */

public class UserFollowViewHolder extends BaseRecyclerViewHolder<User> implements FollowUserManager.FollowUserCallback {
    protected TextView tv_nickName, tv_introduce, mTvFollower;
    public UserFollowBtn mIvFollow;
    private User mUser;
    private VipAvatar simpleHeadView;
    private IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

    public UserFollowViewHolder(View itemView) {
        super(itemView);
        initViews();
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, User user) {
        bindEvents(user, adapter);
    }

    private void initViews() {
        tv_nickName = itemView.findViewById(R.id.tv_user_follow_recycler_nickName);
        simpleHeadView = itemView.findViewById(R.id.simpleView_user_follow_recycler);
        tv_introduce = itemView.findViewById(R.id.tv_user_follow_recycler_introduce);
        mTvFollower = itemView.findViewById(R.id.tv_user_follow_recycler_follower);
        mIvFollow = itemView.findViewById(R.id.iv_user_follow_followBtn);
        mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mIvFollow, true);

    }

    private void bindEvents(User user, RecyclerView.Adapter adapter) {
        if(user == null) {
            return;
        }
        mUser = user;

        tv_introduce.setText(user.getIntroduction());
        String followerText = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_follower_num_text");
        String followerStr = followerText + ":\t" + String.valueOf(user.getFollowedUsersCount());
        mTvFollower.setText(followerStr);

//        HeadUrlLoader.getInstance().loadHeaderUrl(simpleHeadView, user.getHeadUrl());
        VipUtil.set(simpleHeadView, user.getHeadUrl(), user.getVip());
        VipUtil.setNickName(tv_nickName, user.getCurLevel(), user.getNickName());

        mFollowBtnPresenter.bindView(user.getUid(), user.isFollowing(), user.isFollower(), new FollowEventModel(getSource(adapter), null));
        mFollowBtnPresenter.setFollowCallback(this);

        setOnclick(user);

    }

    private String getSource(RecyclerView.Adapter adapter) {
        if (adapter instanceof UserFollowRecyclerAdapter) {
            return ((UserFollowRecyclerAdapter) adapter).getSource();
        } else {
            return "INVALID";
        }
    }

    public void setOnclick(final User user) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHostPage.launch(true, user.getUid());
            }
        });
    }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if(mUser == null) {
            return;
        }
        if(TextUtils.equals(uid, mUser.getUid())) {
            mUser.setFollowing(true);
            mIvFollow.setFollowStatus(UserFollowBtn.FollowStatus.FOLLOWING);
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if(mUser == null) {
            return;
        }
        if(TextUtils.equals(uid, mUser.getUid())) {
            mUser.setFollowing(false);
            mIvFollow.setFollowStatus(UserFollowBtn.FollowStatus.FOLLOW);
        }
    }
}
