package com.redefine.welike.business.user.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.adapter.UserFollowingRecyclerAdapter;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.commonui.widget.VipAvatar;

/**
 * Created by gongguan on 2018/1/28.
 */

public class UserFollowingViewHolder extends BaseRecyclerViewHolder<User> {
    public TextView tv_nickName, tv_introduce, mTvFollower;
    private VipAvatar simpleHeadView;
    public UserFollowBtn mIvFollow;
    private User mUser;
    private IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

    public UserFollowingViewHolder(View itemView) {
        super(itemView);
        initViews();
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, User contact) {
        bindEvents(contact, adapter);
        setOnclick(contact);
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
        if (user == null) {
            return;
        }
        mUser = user;
        tv_introduce.setText(user.getIntroduction());
        String followerText = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_follower_num_text");
        String followerStr = followerText + ":\t" + String.valueOf(user.getFollowedUsersCount());
        mTvFollower.setText(followerStr);

        VipUtil.set(simpleHeadView, user.getHeadUrl(), user.getVip());
        VipUtil.setNickName(tv_nickName, user.getCurLevel(), user.getNickName());

        mFollowBtnPresenter.bindView(user.getUid(), user.isFollowing(), user.isFollower(), new FollowEventModel(getSource(adapter), null));

    }

    private String getSource(RecyclerView.Adapter adapter) {
        if (adapter instanceof UserFollowingRecyclerAdapter) {
            return ((UserFollowingRecyclerAdapter) adapter).getSource();
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

}