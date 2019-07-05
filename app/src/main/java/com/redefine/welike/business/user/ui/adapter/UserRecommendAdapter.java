package com.redefine.welike.business.user.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.management.bean.RecommendSlideUser;
import com.redefine.welike.business.user.management.bean.RecommendUser;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.expose.base.IDataProvider;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhl on 2018/1/12.
 */

public class UserRecommendAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, RecommendSlideUser> implements IDataProvider {
    private List<RecommendSlideUser> contactsList;
    private String mSource;

    public UserRecommendAdapter(String source) {
        contactsList = new ArrayList<>();
        mSource = source;
    }

    @Override
    public Object getData() {
        return contactsList;
    }

    @Override
    public String getSource() {
        return mSource;
    }

    public void addNewData(List<RecommendSlideUser> mList) {
        if (!CollectionUtil.isEmpty(mList)) {
            contactsList.addAll(mList);
            notifyDataSetChanged();
        }
    }

    public void refreshData(List<RecommendSlideUser> mList) {
        contactsList.clear();
        if (!CollectionUtil.isEmpty(mList)) {
            contactsList.addAll(mList);
        }
        notifyDataSetChanged();
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View mView = mInflater.inflate(R.layout.user_follow_recycler_item, parent, false);
        return new UserFollowViewHolder(mView);
    }

    @Override
    protected void onBindItemViewHolder(BaseRecyclerViewHolder holder, final int position) {

        if (holder instanceof UserFollowViewHolder) {
            final UserFollowViewHolder followerHolder = (UserFollowViewHolder) holder;

            followerHolder.bindViews(this, contactsList.get(position));
//
        }
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return contactsList != null ? contactsList.size() : 0;
    }

    @Override
    protected RecommendSlideUser getRealItem(int position) {
        return contactsList.get(position);
    }

    public void refreshOnFollow(String uid) {
        boolean hasFollowFeed = false;
        for (RecommendSlideUser user : contactsList) {
            if (TextUtils.equals(user.getUid(), uid)) {
                user.setFollowing(true);
                hasFollowFeed = true;
            }
        }
        if (hasFollowFeed) {
            notifyDataSetChanged();
        }
    }

    public void refreshOnUnFollow(String uid) {
        boolean hasFollowFeed = false;
        for (RecommendSlideUser user : contactsList) {
            if (TextUtils.equals(user.getUid(), uid)) {
                user.setFollowing(false);
                hasFollowFeed = true;
            }
        }
        if (hasFollowFeed) {
            notifyDataSetChanged();
        }
    }

    class UserFollowViewHolder extends BaseRecyclerViewHolder<RecommendSlideUser> implements FollowUserManager.FollowUserCallback {
        protected TextView tv_nickName, tv_introduce, mTvFollower;
        public UserFollowBtn mIvFollow;
        private RecommendSlideUser mUser;
        private VipAvatar simpleHeadView;
        private IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

        public UserFollowViewHolder(View itemView) {
            super(itemView);
            initViews();
        }

        @Override
        public void bindViews(RecyclerView.Adapter adapter, RecommendSlideUser user) {
            bindEvents(user);
        }

        private void initViews() {
            tv_nickName = itemView.findViewById(R.id.tv_user_follow_recycler_nickName);
            simpleHeadView = itemView.findViewById(R.id.simpleView_user_follow_recycler);
            tv_introduce = itemView.findViewById(R.id.tv_user_follow_recycler_introduce);
            mTvFollower = itemView.findViewById(R.id.tv_user_follow_recycler_follower);
            mIvFollow = itemView.findViewById(R.id.iv_user_follow_followBtn);
            mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mIvFollow, true);

        }

        private void bindEvents(RecommendSlideUser user) {
            if (user == null) {
                return;
            }
            mUser = user;

            tv_nickName.setText(user.getName());
            tv_introduce.setText(user.getIntro());
            String followerText = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_follower_num_text");
            String followerStr = followerText + ":\t" + String.valueOf(user.getFollowedUsersCount());
            mTvFollower.setText(followerStr);

            VipUtil.set(simpleHeadView, user.getAvatar(), user.getCertifyType() == 0 ? 0 : user.getCertifyType() == 1 ? 1 : user.getCertifyType() == 2 ? 1000000 : 2000000);
            VipUtil.setNickName(tv_nickName, user.getCurLevel(), user.getName());

            mFollowBtnPresenter.bindView(user.getUid(), user.getFollowing(), user.getFollower(), new FollowEventModel(mSource, null));
            mFollowBtnPresenter.setFollowCallback(this);

            setOnclick(user);

        }

        public void setOnclick(final RecommendSlideUser user) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserHostPage.launch(true, user.getUid());
                }
            });
        }

        @Override
        public void onFollowCompleted(String uid, int errCode) {
            if (mUser == null) {
                return;
            }
            if (TextUtils.equals(uid, mUser.getUid())) {
                mUser.setFollowing(true);
                mIvFollow.setFollowStatus(UserFollowBtn.FollowStatus.FOLLOWING);
            }
        }

        @Override
        public void onUnfollowCompleted(String uid, int errCode) {
            if (mUser == null) {
                return;
            }
            if (TextUtils.equals(uid, mUser.getUid())) {
                mUser.setFollowing(false);
                mIvFollow.setFollowStatus(UserFollowBtn.FollowStatus.FOLLOW);
            }
        }
    }

}