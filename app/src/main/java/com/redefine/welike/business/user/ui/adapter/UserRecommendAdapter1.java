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
import com.redefine.welike.business.feeds.ui.page.FeedDetailActivity;
import com.redefine.welike.business.feeds.ui.viewholder.FeedLoadMoreViewHolder;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.management.bean.AttachmentBean;
import com.redefine.welike.business.user.management.bean.AttachmentContent;
import com.redefine.welike.business.user.management.bean.RecommendUser1;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.NumberUtils;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.expose.base.IDataProvider;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.view.MultiGridView;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhl on 2018/1/12.
 */

public class UserRecommendAdapter1 extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, RecommendUser1>
        implements FollowUserManager.FollowUserCallback, IDataProvider<List<RecommendUser1>> {
    private List<RecommendUser1> contactsList;

    private static int head = 0;
    private static int normal = 1;
    private String mSource;
    private FollowStatusChangeListener mListener;


    public UserRecommendAdapter1(FollowStatusChangeListener listener, String source) {
        contactsList = new ArrayList<>();
        FollowUserManager.getInstance().register(this);
        mListener = listener;
        mSource = source;
    }

    @Override
    public List<RecommendUser1> getData() {
        return contactsList;
    }

    public String getSource() {
        return mSource;
    }

    public void addNewData(List<RecommendUser1> mList) {
        if (!CollectionUtil.isEmpty(mList)) {
            contactsList.addAll(mList);
            notifyDataSetChanged();
        }
    }

    public void refreshData(List<RecommendUser1> mList) {
        contactsList.clear();
        if (!CollectionUtil.isEmpty(mList)) {
            contactsList.add(new RecommendUser1("HEAD", "head", "1", "", 0, 0, 0, 0, null, false, false, 0));
            contactsList.addAll(mList);
        }
        notifyDataSetChanged();
    }

    public void onDestroy() {
        FollowUserManager.getInstance().unregister(this);
    }

    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new FeedLoadMoreViewHolder(mInflater.inflate(R.layout.feed_load_more_layout, parent, false));
    }


    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        if (viewType == head) {
            View mView = mInflater.inflate(R.layout.recommend_user_head_item, parent, false);
            return new HeadiewHolder(mView);
        }
        View mView = mInflater.inflate(R.layout.recommend_user_item, parent, false);
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

        if (TextUtils.equals(contactsList.get(position).getUid(), "HEAD")) {
            return head;
        }
        return normal;
    }

    @Override
    public int getRealItemCount() {
        return contactsList != null ? contactsList.size() : 0;
    }

    @Override
    protected RecommendUser1 getRealItem(int position) {
        return contactsList.get(position);
    }

    class HeadiewHolder extends BaseRecyclerViewHolder<RecommendUser1> {
        public HeadiewHolder(View itemView) {
            super(itemView);
        }
    }

    class UserFollowViewHolder extends BaseRecyclerViewHolder<RecommendUser1> {
        private TextView tv_nickName, mTvFollower, mVerifiedInfo;
        private UserFollowBtn mIvFollow;
        private RecommendUser1 mUser;
        private VipAvatar simpleHeadView;
        private MultiGridView picView;
        private IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

        private RecommendNineGridAdapter mAdapter;

        private UserFollowViewHolder(View itemView) {
            super(itemView);
            initViews();
        }

        @Override
        public void bindViews(RecyclerView.Adapter adapter, RecommendUser1 user) {
            bindEvents(user);
        }

        private void initViews() {
            tv_nickName = itemView.findViewById(R.id.tv_user_follow_recycler_nickName);
            simpleHeadView = itemView.findViewById(R.id.simpleView_user_follow_recycler);
            mTvFollower = itemView.findViewById(R.id.tv_user_follow_recycler_follower);
            mIvFollow = itemView.findViewById(R.id.iv_user_follow_followBtn);
            mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mIvFollow, true);
            picView = itemView.findViewById(R.id.mgv_picture);
            mVerifiedInfo = itemView.findViewById(R.id.tv_user_follow_verify_info);
        }

        private void bindEvents(final RecommendUser1 user) {
            if (user == null) {
                return;
            }
            mUser = user;

            tv_nickName.setText(user.getName());
            String followerStr = mTvFollower.getResources().getString(R.string.mine_follower_num_text)
                    + ":\t" + String.valueOf(user.getFollowedUsersCount())
                    + "  " + mTvFollower.getResources().getString(R.string.message_like_text)
                    + ":\t" + NumberUtils.getBrowseNum(user.getLikeCount());

            mTvFollower.setText(followerStr);

            String vipDesc = VipUtil.getDescription(user.getVip());
            if (TextUtils.isEmpty(vipDesc)) {
                mVerifiedInfo.setVisibility(View.GONE);
            } else {
                mVerifiedInfo.setVisibility(View.VISIBLE);
                mVerifiedInfo.setText(vipDesc);
            }

            VipUtil.set(simpleHeadView, user.getAvatar(), user.getVip());
            VipUtil.setNickName(tv_nickName, user.getCurLevel(), user.getName());
            mIvFollow.setUnFollowEnable(false);
            mFollowBtnPresenter.bindView(user.getUid(), user.getFollowing(), user.getFollower(), new FollowEventModel(mSource, null));

            mAdapter = new RecommendNineGridAdapter();

            picView.setAdapter(mAdapter);
            picView.setOnItemClickListener(new MultiGridView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, Object t) {
                    AttachmentBean attachmentBean = user.getPostImages().get(position);
                    if (attachmentBean != null) {
                        AttachmentContent content = attachmentBean.getContent();
                        FeedDetailActivity.launch(content.getId());
                    }
                    EventLog1.Follow.report4(EventConstants.FEED_PAGE_HOME_FULL_SCREEN_RECOMMEND, mUser.getUid());
                }
            });

            mAdapter.setData(user.getPostImages());

            setOnclick(user);

        }

        public void setOnclick(final RecommendUser1 user) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserHostPage.launch(true, user.getUid());
                }
            });
        }
    }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        int index = 0;
        for (RecommendUser1 user : contactsList) {
            if (TextUtils.equals(user.getUid(), uid)) {
                user.setFollowing(true);
                mListener.onFollowerChanged(user);
                notifyItemChanged(index);
            }
            index++;
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        int index = 0;
        for (RecommendUser1 user : contactsList) {
            if (TextUtils.equals(user.getUid(), uid)) {
                user.setFollowing(false);
                mListener.onFollowerChanged(user);
                notifyItemChanged(index);
            }
            index++;
        }
    }

    public interface FollowStatusChangeListener {

        void onFollowerChanged(RecommendUser1 user);

    }


}