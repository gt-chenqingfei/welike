package com.redefine.welike.business.feeds.ui.adapter;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.request.RecommendSlideCloseRequest;
import com.redefine.welike.business.contact.view.ContactActivity;
import com.redefine.welike.business.feeds.ui.listener.OnRequestPermissionCallback;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.browse.management.request.RecommendCloseRequest;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.management.UserModelFactory;
import com.redefine.welike.business.user.management.bean.RecommendSlideUser;
import com.redefine.welike.business.user.management.bean.RecommendSlideUser;
import com.redefine.welike.business.user.management.bean.RecommendUser;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.expose.base.IDataProvider;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.widget.IFollowBtn;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

public class FeedItemFollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        FollowUserManager.FollowUserCallback, IDataProvider<List<RecommendSlideUser>> {


    private static final int ITEM_TYPE_RECOMMEND = 1;
    private static final int ITEM_TYPE_CONTACT = 2;
    private static final int ITEM_TYPE_RECOMMEND_BIG = 3;

    public static final int VIEW_HOLDER_TYPE_DEFAULT = 0;
    public static final int VIEW_HOLDER_TYPE_BIG_CARD = 1;
    private int mViewHolderType = VIEW_HOLDER_TYPE_DEFAULT;

    private List<RecommendSlideUser> users = new ArrayList<>();
    private OnAdapterClickListener listener;
    private OnRequestPermissionCallback mPermissionCallback;
    private String mSource;

    public FeedItemFollowAdapter(String source) {
        FollowUserManager.getInstance().register(this);
        if (TextUtils.equals(source, EventConstants.FEED_PAGE_DISCOVER_FOR_YOU)) {
            mSource = EventConstants.FEED_PAGE_DISCOVER_CARD_RECOMMEND;
        } else if (TextUtils.equals(source, EventConstants.FEED_PAGE_HOME)) {
            mSource = EventConstants.FEED_PAGE_HOME_CARD_RECOMMEND;
        } else if (TextUtils.isEmpty(source)){
            mSource = "";
        } else {
            mSource = source;
        }
    }

    public void setHolderType(int type) {
        mViewHolderType = type;
    }

    public void setUsers(List<RecommendSlideUser> users) {
        if (users == null) return;
        this.users.clear();
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void setPermissionCallback(OnRequestPermissionCallback callback) {
        mPermissionCallback = callback;
    }

    public List<RecommendSlideUser> getUsers() {
        return users;
    }

    @Override
    public List<RecommendSlideUser> getData() {
        return getUsers();
    }

    @Override
    public String getSource() {
        return mSource;
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

    public void setListener(OnAdapterClickListener listener) {
        this.listener = listener;
    }

    private void remove(int position) {
        if (CollectionUtil.isEmpty(users)) {
            return;
        }

        if (position > users.size()) return;
        users.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, users.size());
        listener.onDelete(users.size());
    }

    public void refreshOnFollow(String uid) {
        if (CollectionUtil.isEmpty(users)) {
            return;
        }
        boolean hasFollowFeed = false;
        int index = 0;
        for (RecommendSlideUser recommendUser : users) {
            if (TextUtils.equals(recommendUser.getUid(), uid)) {
                recommendUser.setFollowing(true);
                hasFollowFeed = true;
                break;
            }
            index++;
        }
        if (hasFollowFeed) {
            notifyItemChanged(index);
        }

        if (listener != null) {
            listener.onItemFollow(index);
        }
    }

    public void refreshOnUnFollow(String uid) {
        if (CollectionUtil.isEmpty(users)) {
            return;
        }
        boolean hasFollowFeed = false;
        for (RecommendSlideUser recommendUser : users) {
            if (TextUtils.equals(recommendUser.getUid(), uid)) {
                recommendUser.setFollowing(false);
                hasFollowFeed = true;
            }
        }
        if (hasFollowFeed) {
            notifyDataSetChanged();
        }
        if (listener != null) {
            listener.onItemUnFollow(uid);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTACT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_contact_layout, parent, false);
            return new RContactViewHolder(view);
        } else if (viewType == ITEM_TYPE_RECOMMEND_BIG) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_item_follow_big_layout, null);
            return new BigFollowViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_item_follow_layout, null);
            return new FollowViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final RecommendSlideUser user = users.get(position);
        if (holder instanceof RContactViewHolder) {
            EventLog1.Follow.report8(mSource);
            final RContactViewHolder contactHolder = (RContactViewHolder) holder;
            contactHolder.tvUserName.setText(user.getName());
            contactHolder.tvUserIntro.setText(user.getIntro());
            contactHolder.sdvUserAvatar.setImageURI(user.getAvatar());
            contactHolder.findFriend.setText(R.string.recommend_find_friends);
            contactHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventLog1.Follow.report9(mSource);
                    if (!EasyPermissions.hasPermissions(contactHolder.tvUserName.getContext(), Manifest.permission.READ_CONTACTS)) {
                        if (mPermissionCallback != null) {
                            mPermissionCallback.onRequestPermission(ResourceTool.getString("read_contact_permission"), PermissionRequestCode.READ_CONTACT_PERMISSION, Manifest.permission.READ_CONTACTS);
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("from", EventLog1.AddFriend.ButtonFrom.HORIZONTAL_CARD);
                        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CONTACT, bundle));
                    }
                }
            });
            contactHolder.ivDelelte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        remove(position);
                    }
                }
            });
        } else if (holder instanceof FollowViewHolder) {
            FollowViewHolder followViewHolder = (FollowViewHolder) holder;
            IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(followViewHolder.commonFeedFollowBtn, true);
            mFollowBtnPresenter.bindView(user.getUid(), user.getFollowing(), user.getFollower(), new FollowEventModel(mSource, null));

            followViewHolder.tvUserName.setText(user.getName());
            HeadUrlLoader.getInstance().loadHeaderUrl2(followViewHolder.sdvUserAvatar, user.getAvatar());

            ConstraintLayout.MarginLayoutParams marginLayoutParams = (ConstraintLayout.MarginLayoutParams) followViewHolder.mFollowLayout.getLayoutParams();
            if (position == 0) {
                marginLayoutParams.leftMargin = ScreenUtils.dip2Px(8);
                marginLayoutParams.rightMargin = ScreenUtils.dip2Px(2);
            } else if (position == users.size() - 1) {
                marginLayoutParams.leftMargin = ScreenUtils.dip2Px(2);
                marginLayoutParams.rightMargin = ScreenUtils.dip2Px(8);
            } else {
                marginLayoutParams.leftMargin = ScreenUtils.dip2Px(2);
                marginLayoutParams.rightMargin = ScreenUtils.dip2Px(2);
            }


            switch (user.getRecommendReason()) {

                case 1:
                    followViewHolder.ivCertify.setVisibility(View.VISIBLE);
                    followViewHolder.tvUserTag.setVisibility(View.VISIBLE);
                    followViewHolder.tvCertify.setVisibility(View.VISIBLE);
                    followViewHolder.tvUserIntro.setVisibility(View.INVISIBLE);
                    followViewHolder.ivCertify.setImageResource(VipUtil.getSlideResource(user.getCertifyType()));
                    followViewHolder.tvUserTag.setText(user.getCertifyTypeName());
                    followViewHolder.tvUserTag.setTextColor(followViewHolder.tvUserTag.getResources().getColor(R.color.main_orange_dark));
                    followViewHolder.tvCertify.setText(followViewHolder.tvUserTag.getResources().getString(R.string.recommend_verified));
                    break;
                case 2:
                    followViewHolder.ivCertify.setVisibility(View.VISIBLE);
                    followViewHolder.tvUserTag.setVisibility(View.VISIBLE);
                    followViewHolder.tvCertify.setVisibility(View.VISIBLE);
                    followViewHolder.tvUserIntro.setVisibility(View.INVISIBLE);
                    followViewHolder.ivCertify.setImageResource(R.drawable.ic_recommend_slideability);
                    followViewHolder.tvUserTag.setText(user.getAbilityTagName());
                    followViewHolder.tvUserTag.setTextColor(followViewHolder.tvUserTag.getResources().getColor(R.color.color_48779D));
                    followViewHolder.tvCertify.setText(followViewHolder.tvUserTag.getResources().getString(R.string.recommend_influence));
                    break;
                case 3:
                case 4:
                    followViewHolder.ivCertify.setVisibility(View.INVISIBLE);
                    followViewHolder.tvUserTag.setVisibility(View.INVISIBLE);
                    followViewHolder.tvCertify.setVisibility(View.INVISIBLE);
                    followViewHolder.tvUserIntro.setVisibility(View.VISIBLE);
                    followViewHolder.tvUserIntro.setText(user.getIntro());
                    break;
                default:
                    break;
            }

            followViewHolder.touchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserHostPage.launch(true, user.getUid());
                    InterestAndRecommendCardEventManager.INSTANCE.setButton_type(EventConstants.INTEREST_CARD_BUTTON_TYPE_PORTRAIT);
                    InterestAndRecommendCardEventManager.INSTANCE.report7();
                }
            });

            followViewHolder.ivDelelte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new RecommendSlideCloseRequest(MyApplication.getAppContext()).request(user.getUid());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    if (listener != null) {
                        remove(position);
                    }
                }
            });

            followViewHolder.commonFeedFollowBtn.setFollowBtnClickCallback(new IFollowBtn.OnFollowBtnClickCallback() {
                @Override
                public void onClickFollow(View view) {
                    InterestAndRecommendCardEventManager.INSTANCE.setButton_type(EventConstants.INTEREST_CARD_BUTTON_TYPE_FOLLOW);
                    InterestAndRecommendCardEventManager.INSTANCE.report7();
                }

                @Override
                public void onClickUnfollow(View view) {
                    InterestAndRecommendCardEventManager.INSTANCE.setButton_type(EventConstants.INTEREST_CARD_BUTTON_TYPE_UNFOLLOW);
                    InterestAndRecommendCardEventManager.INSTANCE.report7();
                }
            });
        } else if (holder instanceof BigFollowViewHolder) {
            BigFollowViewHolder followViewHolder = (BigFollowViewHolder) holder;
            ConstraintLayout.MarginLayoutParams marginLayoutParams = (ConstraintLayout.MarginLayoutParams) followViewHolder.mFollowLayout.getLayoutParams();

            if (position == 0) {
                marginLayoutParams.leftMargin = ScreenUtils.dip2Px(12);
                marginLayoutParams.rightMargin = ScreenUtils.dip2Px(6);
            } else if (position == users.size() - 1) {
                marginLayoutParams.leftMargin = ScreenUtils.dip2Px(6);
                marginLayoutParams.rightMargin = ScreenUtils.dip2Px(12);
            } else {
                marginLayoutParams.leftMargin = ScreenUtils.dip2Px(6);
                marginLayoutParams.rightMargin = ScreenUtils.dip2Px(6);
            }

            IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(followViewHolder.commonFeedFollowBtn, true);
            mFollowBtnPresenter.bindView(user.getUid(), user.getFollowing(), user.getFollower(), new FollowEventModel(mSource, null));

            followViewHolder.tvUserName.setText(user.getName());
            followViewHolder.tvUserIntro.setText(user.getIntro());
            HeadUrlLoader.getInstance().loadHeaderUrl2(followViewHolder.sdvUserAvatar, user.getAvatar());
            followViewHolder.sdvUserAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserHostPage.launch(true, user.getUid());
                    InterestAndRecommendCardEventManager.INSTANCE.setButton_type(EventConstants.INTEREST_CARD_BUTTON_TYPE_PORTRAIT);
                    InterestAndRecommendCardEventManager.INSTANCE.report7();
                }
            });
            followViewHolder.ivDelelte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new RecommendSlideCloseRequest(MyApplication.getAppContext()).request(user.getUid());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    if (listener != null) {
                        remove(position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (users == null) {
            super.getItemViewType(position);
        }
        if (position >= users.size()) {
            super.getItemViewType(position);
        }
        RecommendSlideUser user = users.get(position);
        if (user.getType() == UserModelFactory.RECOMMEND_TYPE_CONTACT) {
            return ITEM_TYPE_CONTACT;
        } else if (user.getType() == UserModelFactory.RECOMMEND_TYPE_NORMAL){
            if (mViewHolderType == VIEW_HOLDER_TYPE_BIG_CARD) {
                return ITEM_TYPE_RECOMMEND_BIG;
            } else {
                return ITEM_TYPE_RECOMMEND;
            }
        }
        return super.getItemViewType(position);

    }

    @Override
    public int getItemCount() {
        if (CollectionUtil.isEmpty(users)) {
            return 0;
        }
        return users.size();
    }

    public class FollowViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView sdvUserAvatar;
        private TextView tvUserName;
        private TextView tvUserIntro;
        private UserFollowBtn commonFeedFollowBtn;
        private ImageView ivDelelte;
        private ViewGroup mFollowLayout;
        public View touchView;
        public TextView tvUserTag, tvCertify;
        public ImageView ivCertify;

        public FollowViewHolder(View itemView) {
            super(itemView);
            tvUserTag = itemView.findViewById(R.id.tv_intro_tag);
            tvCertify = itemView.findViewById(R.id.tv_certifyType);
            ivCertify = itemView.findViewById(R.id.iv_certifyType);
            touchView = itemView.findViewById(R.id.view_touch);
            sdvUserAvatar = itemView.findViewById(R.id.sdv_user_avatar);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserIntro = itemView.findViewById(R.id.tv_user_intro);
            commonFeedFollowBtn = itemView.findViewById(R.id.common_feed_follow_btn);
            ivDelelte = itemView.findViewById(R.id.iv_user_delete);
            mFollowLayout = itemView.findViewById(R.id.constraintLayout);
            mFollowLayout.getLayoutParams().height = ScreenUtils.dip2Px(212);
            mFollowLayout.getLayoutParams().width = (int) (ScreenUtils.getSreenWidth(itemView.getContext()) * 0.4);
            commonFeedFollowBtn.setUnFollowShowStroke(true);
        }
    }

    public class BigFollowViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView sdvUserAvatar;
        private TextView tvUserName;
        private TextView tvUserIntro;
        private UserFollowBtn commonFeedFollowBtn;
        private ImageView ivDelelte;
        private ViewGroup mFollowLayout;
        public BigFollowViewHolder(View itemView) {
            super(itemView);

            sdvUserAvatar = itemView.findViewById(R.id.sdv_user_avatar);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserIntro = itemView.findViewById(R.id.tv_user_intro);
            commonFeedFollowBtn = itemView.findViewById(R.id.common_feed_follow_btn);
            ivDelelte = itemView.findViewById(R.id.iv_user_delete);
            mFollowLayout = itemView.findViewById(R.id.constraintLayout);

            mFollowLayout.getLayoutParams().height = ScreenUtils.dip2Px(319);
            mFollowLayout.getLayoutParams().width = (int) (ScreenUtils.getSreenWidth(itemView.getContext()) * 0.67);
            commonFeedFollowBtn.setTextSize(DensityUtil.dp2px(14));
            commonFeedFollowBtn.setUnFollowShowStroke(true);
        }
    }

    public class RContactViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup mFollowLayout;
        public ImageView ivDelelte;
        public SimpleDraweeView sdvUserAvatar;
        public TextView tvUserName;
        public TextView tvUserIntro;
        public TextView findFriend;

        public RContactViewHolder(View itemView) {
            super(itemView);
            findFriend = itemView.findViewById(R.id.tv_common_feed_following);
            sdvUserAvatar = itemView.findViewById(R.id.sdv_user_avatar);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserIntro = itemView.findViewById(R.id.tv_user_intro);
            ivDelelte = itemView.findViewById(R.id.iv_user_delete);
            mFollowLayout = itemView.findViewById(R.id.constraintLayout);
            mFollowLayout.getLayoutParams().height = ScreenUtils.dip2Px(212);
            mFollowLayout.getLayoutParams().width = (int) (ScreenUtils.getSreenWidth(itemView.getContext()) * 0.4);
        }
    }


    @Override
    public void onFollowCompleted(String uid, int errCode) {

        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnFollow(uid);
        } else {
            refreshOnUnFollow(uid);
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            refreshOnUnFollow(uid);
        } else {
            refreshOnFollow(uid);
        }
    }

    public interface OnAdapterClickListener {
        void onItemFollow(int position);

        void onItemUnFollow(String uid);

        void onDelete(int size);
    }

}
