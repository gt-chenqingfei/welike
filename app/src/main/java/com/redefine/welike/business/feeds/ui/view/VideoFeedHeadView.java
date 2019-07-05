package com.redefine.welike.business.feeds.ui.view;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.welike.R;
import com.redefine.welike.business.browse.management.bean.FollowUser;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.FollowUserCallBack;
import com.redefine.welike.business.browse.management.dao.FollowUserCountCallBack;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.UserHonor;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.listener.IFeedOperationListener;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.user.ui.contract.IUserFollowBtnContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.BrowseSchemeManager;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.expose.ExposeEventReporter;
import com.redefine.welike.commonui.event.expose.model.FollowEventModel;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/12/21
 */
public class VideoFeedHeadView {
    private final VipAvatar mHeadView;
    private final TextView mName;
    private final SimpleDraweeView mEventMark;
    private final UserFollowBtn mFollowBtn;

    private View.OnClickListener mFollowClickListener;
    private IBrowseClickListener iBrowseClickListener;
    private OnPostButtonClickListener mPostButtonClickListener;
    public final IUserFollowBtnContract.IUserFollowBtnPresenter mFollowBtnPresenter;

    public VideoFeedHeadView(View itemView, IFeedOperationListener listener) {
        mHeadView = itemView.findViewById(R.id.common_feed_head);
        mName = itemView.findViewById(R.id.common_feed_name);
        mEventMark = itemView.findViewById(R.id.common_feed_event_mark);
        mFollowBtn = itemView.findViewById(R.id.common_feed_follow_btn);

        mFollowBtnPresenter = IUserFollowBtnContract.UserFollowBtnFactory.createPresenter(mFollowBtn, false);
    }

    public void bindViews(final PostBase postBase, final RecyclerView.Adapter adapter) {
        VipUtil.set(mHeadView, postBase.getHeadUrl(), postBase.getVip());
        VipUtil.setNickName(mName, postBase.getCurLevel(), postBase.getNickName());

        mFollowBtn.setTag(postBase);
        mFollowBtnPresenter.bindView(postBase.getUid(), postBase.isFollowing(), false, new FollowEventModel(getFeedSource(adapter), postBase));

        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPostButtonClickListener != null) {
                    mPostButtonClickListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_HEAD);
                }

                if (iBrowseClickListener != null) {
                    iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_HEAD, false, 0);
                }
                UserHostPage.launch(true, postBase.getUid());
                ExposeEventReporter.INSTANCE.reportPostClick(postBase, getFeedSource(adapter), EventLog1.FeedView.FeedClickArea.AVATAR);
            }
        });
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mPostButtonClickListener != null) {
                    mPostButtonClickListener.onPostAreaClick(postBase, EventConstants.CLICK_AREA_HEAD);
                }

                if (iBrowseClickListener != null) {
                    iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_HEAD, false, 0);
                }

                UserHostPage.launch(true, postBase.getUid());
                ExposeEventReporter.INSTANCE.reportPostClick(postBase, getFeedSource(adapter), EventLog1.FeedView.FeedClickArea.AVATAR);
            }
        });

        try {
            UserHonor shownHonor = null;
            for (UserHonor honor : postBase.userhonors) {
                if (honor.type == UserHonor.TYPE_EVENT) {
                    shownHonor = honor;
                }
            }
            if (shownHonor != null) {
                mEventMark.setVisibility(View.VISIBLE);
                mEventMark.setImageURI(Uri.parse(shownHonor.honorPic));
                final UserHonor finalShownHonor = shownHonor;
                mEventMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iBrowseClickListener != null) {
                            iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_UNKOWN, true, 0);
                            return;
                        }
                        new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_TASK).onUrlRedirect(finalShownHonor.forwardUrl);
                    }
                });
            } else {
                mEventMark.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFeedSource(RecyclerView.Adapter adapter) {
        if (adapter instanceof FeedRecyclerViewAdapter) {
            return ((FeedRecyclerViewAdapter) adapter).getFeedSource();
        } else {
            return "INVALID";
        }
    }

    public void setFollowBtnListener(View.OnClickListener listener) {
        mFollowClickListener = listener;
        mFollowBtn.addOtherListener(listener);
    }

    public void setPostButtonClickListener(OnPostButtonClickListener mPostButtonClickListener) {
        this.mPostButtonClickListener = mPostButtonClickListener;
    }

    public void setBrowseClickListener(final IBrowseClickListener iBrowseClickListener) {

        this.iBrowseClickListener = iBrowseClickListener;

        if (iBrowseClickListener != null) {
            setBrowseMode();
        }
    }

    private void setBrowseMode() {
        mFollowBtn.setOnClickFollowBtnListener(null);

        BrowseEventStore.INSTANCE.getFollowUser(((PostBase) mFollowBtn.getTag()).getUid(), new FollowUserCallBack() {
            @Override
            public void onLoadEntity(FollowUser user) {
                if (user == null) return;
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        mFollowBtn.setVisibility(View.GONE);
                    }
                });
            }
        });

        mFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PostBase postBase = (PostBase) v.getTag();
                BrowseSchemeManager.getInstance().setUserProfile(postBase.getUid());

                if (mFollowClickListener != null) {
                    mFollowClickListener.onClick(mFollowBtn);
                }
                BrowseEventStore.INSTANCE.getFollowUserCount(new FollowUser(postBase.getUid(), postBase.getNickName()), new FollowUserCountCallBack() {
                    @Override
                    public void onLoadEntity(boolean inserted, final int count) {
                        if (inserted) {
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    mFollowBtn.setVisibility(View.GONE);
                                    if (count % 3 == 1) {
                                        HalfLoginManager.getInstancce().showLoginDialog(mFollowBtn.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                                    }
                                }
                            });

                        } else {
                            HalfLoginManager.getInstancce().showLoginDialog(mFollowBtn.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FOLLOW));
                        }

                        if (iBrowseClickListener != null)
                            iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_FOLLOW, false, 1);

                    }
                });


            }
        });
    }
}
