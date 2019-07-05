package com.redefine.welike.business.user.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.viewholder.UserFollowingViewHolder;
import com.redefine.welike.commonui.event.expose.base.IDataProvider;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongguan on 2018/1/28.
 */

public class UserFollowingRecyclerAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, User> implements IDataProvider<List<User>> {
    private List<User> contactsList;
    private String mSource;
//    private Animation mFollowAnim;

    public UserFollowingRecyclerAdapter(String source) {
        mSource = source;
        contactsList = new ArrayList<>();
    }

    public void setSource(String source) {
        this.mSource = source;
    }

    @Override
    public List<User> getData() {
        return contactsList;
    }

    @Override
    public String getSource() {
        return mSource;
    }

    public void addNewData(List<User> mList) {
        if (!CollectionUtil.isEmpty(mList)) {
            contactsList.addAll(mList);
            notifyDataSetChanged();
        }
    }

    public void refreshData(List<User> mList) {
        contactsList.clear();
        if (!CollectionUtil.isEmpty(mList)) {
            contactsList.addAll(mList);
        }
        notifyDataSetChanged();
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.user_follow_recycler_item, parent, false);
        return new UserFollowingViewHolder(mView);
    }

    @Override
    protected void onBindItemViewHolder(final BaseRecyclerViewHolder holder, final int position) {
//        if (mFollowAnim != null && holder instanceof UserFollowingViewHolder) {
//            mFollowAnim.cancel();
//            mFollowAnim = null;
//            ((UserFollowingViewHolder) holder).mIvFollow.clearAnimation();
//        }

        if (holder instanceof UserFollowingViewHolder) {
            final UserFollowingViewHolder followingHolder = (UserFollowingViewHolder) holder;
//            if (FollowUserManager.getInstance().isFollowRequesting(contactsList.get(position).getUid())) {
//                startFollowAnim(followingHolder);
//            } else if (FollowUserManager.getInstance().isUnfollowRequesting(contactsList.get(position).getUid())) {
//                startFollowAnim(followingHolder);
//            }

            followingHolder.bindViews(this, contactsList.get(position));
//            followingHolder.mIvFollow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (FollowUserManager.getInstance().isFollowRequesting(contactsList.get(position).getUid())) {
//                        startFollowAnim(followingHolder);
//                    } else if (FollowUserManager.getInstance().isUnfollowRequesting(contactsList.get(position).getUid())) {
//                        startFollowAnim(followingHolder);
//                    } else {
//                        if (contactsList.get(position).isFollowing()) {
//                            showUnfollowDialog(followingHolder, contactsList.get(position).getUid());
//                        } else {
//                            startFollowAnim(followingHolder);
//                            FollowUserManager.getInstance().follow(contactsList.get(position).getUid());
//                        }
//                    }
//                }
//            });
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
    protected User getRealItem(int position) {
        return contactsList.get(position);
    }

    public void refreshOnUnFollow(String uid) {
        for (int i = 0; i < contactsList.size(); i++) {
            if (TextUtils.equals(contactsList.get(i).getUid(), uid)) {
                contactsList.get(i).setFollowing(false);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void refreshOnFollow(String uid) {
        for (int i = 0; i < contactsList.size(); i++) {
            if (TextUtils.equals(contactsList.get(i).getUid(), uid)) {
                contactsList.get(i).setFollowing(true);
                notifyDataSetChanged();
                break;
            }
        }
    }

//    private void showUnfollowDialog(final UserFollowingViewHolder holder, String uId) {
//        UnfollowDialog mUnFollowDialog = new UnfollowDialog(holder.mIvFollow.getContext(), uId, new UnfollowDialog.UnFollowCallback() {
//            @Override
//            public void CallBack(boolean isConfirm) {
//                if (isConfirm) {
//                    startFollowAnim(holder);
//                }
//            }
//        });
//        mUnFollowDialog.show();
//    }

//    private void startFollowAnim(UserFollowingViewHolder holder) {
//        mFollowAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        mFollowAnim.setDuration(800);
//        mFollowAnim.setRepeatCount(Animation.INFINITE);
//        mFollowAnim.setInterpolator(new LinearInterpolator());
//        holder.mIvFollow.setImageResource(R.drawable.user_follow_roata);
//        holder.mIvFollow.setVisibility(View.VISIBLE);
//        holder.mIvFollow.startAnimation(mFollowAnim);
//    }

}
