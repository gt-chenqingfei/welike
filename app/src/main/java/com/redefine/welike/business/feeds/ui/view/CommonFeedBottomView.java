package com.redefine.welike.business.feeds.ui.view;

import android.animation.Animator;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.foundation.framework.Event;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.InsertLikeCallBack;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.LikeManager;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.ICommonFeedBottomContract;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.location.ui.constant.LocationConstant;
import com.redefine.welike.business.publisher.management.PublisherEventManager;
import com.redefine.welike.business.publisher.ui.activity.PublishCommentStarter;
import com.redefine.welike.business.publisher.ui.activity.PublishForwardStarter;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.common.BrowseSchemeManager;
import com.redefine.welike.common.ScoreManager;
import com.redefine.welike.common.animation.Animations;
import com.redefine.welike.commonui.event.commonenums.FeedButtonFrom;
import com.redefine.welike.commonui.event.helper.ShareEventHelper;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.popupwindow.ShareVideoPopupWindow;
import com.redefine.welike.commonui.share.ShareHelper;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.NewShareEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liwb on 2018/1/11.
 */

public class CommonFeedBottomView implements ICommonFeedBottomContract.ICommonFeedBottomView, View.OnClickListener
//        OnSuperLikeExpCallback
{
    private final TextView mForwardView;
    private final TextView mCommentView;
    public final TextView mLikeView;
    private ImageView mLikeImage;
    public TextView mShareView;
    public ImageView mShareImage;
    //    public LottieAnimationView mShareLottie;
    private View mShareTab;
    private final View mRootView;
    private final View mForwardTab;
    private final View mCommentTab;
    public final View mLikeTab;
    private final View mFeedDivider, mFeedTopDivider;
    private final ConstraintLayout mBottomContent;
    private final TextView mLocationView;
    //    private final IPageStackManager mPageStackManager;
    //    private SuperLikeListHelper mSuperLike;
    private PostBase mPostBase;
    private ShareVideoPopupWindow mShareVideoPopupWindow;

    private View.OnClickListener likeListener, shareListener;
    private OnPostButtonClickListener mPostButtonListener;
    private IBrowseClickListener iBrowseClickListener;
    private Animator animator;
    private RecyclerView.Adapter mAdapter;

    View mShareCenterImageView;

    public CommonFeedBottomView(View view) {
        mRootView = view;
        mForwardView = view.findViewById(R.id.common_feed_forward_count);
        mForwardTab = view.findViewById(R.id.common_feed_forward_tab);
        mCommentTab = view.findViewById(R.id.common_feed_comment_tab);
        mShareTab = view.findViewById(R.id.common_feed_share_tab);
        mShareView = view.findViewById(R.id.common_feed_share);
        mShareImage = view.findViewById(R.id.common_feed_share_icon);
        mShareCenterImageView = view.findViewById(R.id.common_feed_center_share_view);
//        mShareLottie = view.findViewById(R.id.common_feed_share_lottie);
        mBottomContent = view.findViewById(R.id.common_feed_bottom_content);
        mLikeTab = view.findViewById(R.id.common_feed_like_tab);
        mCommentView = view.findViewById(R.id.common_feed_comment_count);
        mLikeView = view.findViewById(R.id.common_feed_like_count);
        mLikeImage = view.findViewById(R.id.common_feed_like_icon);
        mFeedDivider = view.findViewById(R.id.common_feed_divider);
        mFeedTopDivider = view.findViewById(R.id.common_feed_top_divider);
        mLocationView = view.findViewById(R.id.feed_location_view);
//        mShareLottie.setRepeatCount(LottieDrawable.INFINITE);
//        mSuperLike = new SuperLikeListHelper();
//        animator = AnimatorInflater.loadAnimator(mRootView.getContext(), R.animator.wsapp);
        animator = Animations.INSTANCE.getShareAnim(mShareImage);
    }

    private int animShownTime = 0;

    public void showShareAnim(boolean show) {
        if (show) {
            if (animShownTime++ < 1) {
                animator.start();
            }
        } else {
            animator.end();
//            animator.cancel();
        }
//        mShareImage.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
//        mShareLottie.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//        if (show) {
//            mShareLottie.playAnimation();
//        } else {
//            mShareLottie.cancelAnimation();
//        }
    }

    @Override
    public void bindViews(PostBase postBase, RecyclerView.Adapter adapter) {
        mPostBase = postBase;
        mAdapter = adapter;
        mForwardView.setText(postBase.getForwardCount() > 0 ? FeedHelper.getForwardCount(postBase.getForwardCount()) : mForwardView.getResources().getString(R.string.feed_repost));
        mCommentView.setText(postBase.getCommentCount() > 0 ? FeedHelper.getCommentCount(postBase.getCommentCount()) : mCommentView.getResources().getString(R.string.feed_comment));
        if (postBase.getShareCount() > 0) {
            mShareView.setText(String.valueOf(postBase.getShareCount()));
        } else {
            mShareView.setText(R.string.feed_share);
        }

        mForwardTab.setOnClickListener(this);
        mCommentTab.setOnClickListener(this);
        mShareTab.setOnClickListener(this);
        mLikeTab.setOnClickListener(this);
        updateLikeCount((int) postBase.getSuperLikeExp());
//        mSuperLike.bindView(mPageStackManager, mLikeTab, mLikeImage, postBase, this);

        if (postBase.getLocation() != null && !TextUtils.isEmpty(postBase.getLocation().getPlace())) {
            mLocationView.setText(postBase.getLocation().getPlace());
            mLocationView.setVisibility(View.VISIBLE);
            mLocationView.setOnClickListener(this);
        } else {
            mLocationView.setVisibility(View.GONE);
        }
//        changeShareViewToWhatsApp(); //no need.
    }

    public void updateLikeCount(int exp) {

        mLikeImage.setImageResource(LikeManager.getImage(exp));

        mLikeView.setText(mPostBase.getLikeCount() > 0 ? FeedHelper.getLikeCount(mPostBase.getLikeCount()) : mLikeView.getResources().getString(R.string.feed_like));

        mLikeView.setTextColor(ContextCompat.getColor(mLikeView.getContext(), mPostBase.isLike()
                ? R.color.feed_flag_trending_text_color : R.color.text_feed_content_text_color));
    }

    @Override
    public void setVisibility(int i) {
        mRootView.setVisibility(i);
    }

    @Override
    public void setDismissDivider(boolean b) {
        mFeedDivider.setVisibility(View.GONE);
        mFeedTopDivider.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    public void setDismissLocation(boolean b) {
        mLocationView.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    private void changeShareViewToWhatsApp() {

        mShareImage.setImageResource(R.drawable.common_feed_share);
    }

    private void changeShareViewToNomal() {
        mShareImage.setImageResource(R.drawable.ic_feed_post_bottom_share);
        mShareView.setTextColor(mRootView.getResources().getColor(R.color.text_feed_content_text_color));
    }

    @Override
    public void setDismissBottomContent(boolean b) {
        mBottomContent.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == mCommentTab) {

//            if (iBrowseClickListener != null) {
//                BrowseSchemeManager.getInstance().setPostDetail(mPostBase.getPid());
//                iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_COMMENT, true, 0);
//                return;
//            }

            if (mPostBase != null) {
                if (mPostBase.getCommentCount() == 0) {

                    PublishCommentStarter.INSTANCE.startPopupActivityFromCard((AppCompatActivity) v.getContext(), mPostBase);
                    PublisherEventManager.INSTANCE.setSource(2);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(FeedConstant.KEY_FEED_DETAIL_INDEX, FeedConstant.PAGE_COMMENT_INDEX);
                    bundle.putSerializable(FeedConstant.KEY_FEED, mPostBase);
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_FEED_DETAIL_EVENT, bundle));
                }
                if (mPostButtonListener != null) {
                    mPostButtonListener.onCommentClick(mPostBase);
                }
                String source = "";
                if (mAdapter instanceof FeedRecyclerViewAdapter) {
                    source = ((FeedRecyclerViewAdapter) mAdapter).getFeedSource();
                }
                EventLog1.FeedForment.report1(ShareEventHelper.convertPostType(mPostBase), source,
                        FeedButtonFrom.POST_CARD, mPostBase.getPid(), mPostBase.getStrategy(), mPostBase.getOperationType(), mPostBase.getLanguage(), mPostBase.getTags(),
                        FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), FeedHelper.getRootOrPostUid(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
            }
        } else if (v == mForwardTab) {
            if (iBrowseClickListener != null) {
                BrowseSchemeManager.getInstance().setPostDetail(mPostBase.getPid());
                iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_REPOST, true, 0);
                HalfLoginManager.getInstancce().showLoginDialog(v.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.FORWARD));
                return;
            }
            if (mPostBase != null) {
                String source = "";
                if (mAdapter instanceof FeedRecyclerViewAdapter) {
                    source = ((FeedRecyclerViewAdapter) mAdapter).getFeedSource();
                }
                PublishForwardStarter.INSTANCE.startActivity4PostFromFeedCard(v.getContext(), mPostBase);
                PublisherEventManager.INSTANCE.setSource(2);
                if (mPostButtonListener != null) {
                    mPostButtonListener.onForwardClick(mPostBase);
                }
                EventLog1.FeedForment.report2(ShareEventHelper.convertPostType(mPostBase), source,

                FeedButtonFrom.POST_DETAIL, mPostBase.getPid(), mPostBase.getStrategy(), mPostBase.getOperationType(), mPostBase.getLanguage(), mPostBase.getTags(),
                        FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), FeedHelper.getRootOrPostUid(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs())
                ;
            }
        } else if (v == mLocationView) {
            if (iBrowseClickListener != null) {
                iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_UNKOWN, true, 0);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(LocationConstant.BUNDLE_KEY_LOCATION, mPostBase.getLocation());
            EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_LOCATION_NEAR_BY_PAGE, bundle));
        } else if (v == mShareTab) {
            showShareAnim(false);
            if (iBrowseClickListener != null) {
                iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_SHARE, false, 0);
            }
            if (null != mPostBase) {
                if (shareListener != null) {
                    shareListener.onClick(v);
                }
                if (FeedHelper.shouldShowShareMenu(mPostBase)) {
                    if (mShareVideoPopupWindow == null) {
                        mShareVideoPopupWindow = new ShareVideoPopupWindow(mShareTab.getContext(), new ShareVideoPopupWindow.OnShareClickListener() {
                            @Override
                            public void onShareVideo() {
                                EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(mPostBase),
                                        EventLog1.Share.VideoPostType.VIDEO_FILE, EventLog1.Share.ShareFrom.POST_CARD, EventLog1.Share.PopPage.POST_CARD, mPostBase.getStrategy(), mPostBase.getOperationType(),
                                        mAdapter instanceof FeedRecyclerViewAdapter ? ((FeedRecyclerViewAdapter) mAdapter).getFeedSource() : "", mPostBase.getPid(), mPostBase.getLanguage(), mPostBase.getTags(), mPostBase.getUid(),
                                        FeedHelper.getRootPostId(mPostBase), FeedHelper.getRootPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
                                ShareHelper.sharePostToWhatsApp(mShareTab.getContext(), mPostBase, true, eventModel);
                                ScoreManager.setShareCount(MyApplication.getAppContext());

                                ShareEventManager.INSTANCE.report4(EventConstants.SHARE_TYPE_VIDEO, 1);
                                NewShareEventManager.INSTANCE.setVideoPostType(EventConstants.NEW_SHARE_VIDEO_POST_TYPE_VIDEO);
                                NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_CARD);
                                NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_POST_CARD);
                            }

                            @Override
                            public void onShareLink() {
                                EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(mPostBase),
                                        EventLog1.Share.VideoPostType.POST_LINK, EventLog1.Share.ShareFrom.POST_CARD, EventLog1.Share.PopPage.POST_CARD, mPostBase.getStrategy(), mPostBase.getOperationType(),
                                        mAdapter instanceof FeedRecyclerViewAdapter ? ((FeedRecyclerViewAdapter) mAdapter).getFeedSource() : "", mPostBase.getPid(), mPostBase.getLanguage(), mPostBase.getTags(), mPostBase.getUid(),
                                        FeedHelper.getRootPostId(mPostBase), FeedHelper.getRootPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
                                ShareHelper.sharePostToWhatsApp(mShareTab.getContext(), mPostBase, false, eventModel);
                                ScoreManager.setShareCount(MyApplication.getAppContext());

                                ShareEventManager.INSTANCE.report4(EventConstants.SHARE_TYPE_LINK, 1);
                                NewShareEventManager.INSTANCE.setVideoPostType(EventConstants.NEW_SHARE_VIDEO_POST_TYPE_LINK);
                                NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_CARD);
                                NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_POST_CARD);

                            }
                        });
                    }
                    if (mPostButtonListener != null) {
                        mPostButtonListener.onShareClick(mPostBase, EventConstants.SHARE_TYPE_VIDEO);
                    }
                    mShareVideoPopupWindow.show(mShareCenterImageView);
                    return;
                }

                NewShareEventManager.INSTANCE.setVideoPostType(EventConstants.NEW_SHARE_VIDEO_POST_TYPE_LINK);
                NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_CARD);
                NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_POST_CARD);

                EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(mPostBase),
                        null, EventLog1.Share.ShareFrom.POST_CARD, EventLog1.Share.PopPage.POST_CARD, mPostBase.getStrategy(), mPostBase.getOperationType(),
                        mAdapter instanceof FeedRecyclerViewAdapter ? ((FeedRecyclerViewAdapter) mAdapter).getFeedSource() : "", mPostBase.getPid(), mPostBase.getLanguage(), mPostBase.getTags(), mPostBase.getUid(),
                        FeedHelper.getRootPostId(mPostBase), FeedHelper.getRootPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
                ShareHelper.sharePostToWhatsApp(mShareTab.getContext(), mPostBase, false, eventModel);
                ScoreManager.setShareCount(MyApplication.getAppContext());
            }
            if (mPostButtonListener != null) {
                mPostButtonListener.onShareClick(mPostBase, EventConstants.SHARE_TYPE_LINK);
            }

        } else if (v == mLikeTab) {

            if (likeListener != null) {
                likeListener.onClick(v);
            }
            if (iBrowseClickListener != null) {
                BrowseEventStore.INSTANCE.updateLikeCount(mPostBase, new InsertLikeCallBack() {
                    @Override
                    public void onLoadEntity(boolean inserted, int count) {
                        if (inserted) {

                            if (count % 3 == 1) {
//                                RegisterActivity.Companion.show(mLikeTab.getContext(), 1, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));

                                HalfLoginManager.getInstancce().showLoginDialog(mLikeTab.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
                            }

                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    iBrowseClickListener.onBrowseClick(BrowseConstant.TYPE_LIKE, true, BrowseConstant.TYPE_SHOW_CONTENT_LIKE);
                                }
                            });
                        } else {
                            HalfLoginManager.getInstancce().showLoginDialog(mLikeTab.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
//                            RegisterActivity.Companion.show(mLikeTab.getContext(), 2, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
                        }
                    }
                });
                BrowseSchemeManager.getInstance().setPostDetail(mPostBase.getPid());
            }

            if (mPostBase.isLike()) return;

            onSuperLikeExpCallback(true, (int) mPostBase.getSuperLikeExp() + 1);

        }

    }

    private void onSuperLikeExpCallback(boolean isLast, int exp) {

        mPostBase.setLike(exp > 0);

        if (isLast) {
            if (mPostButtonListener != null) {
                mPostButtonListener.onLikeClick(mPostBase, exp);
            }
            SinglePostManager.superLike(mPostBase, exp, iBrowseClickListener != null);
            updateLikeCount((int) mPostBase.getSuperLikeExp());
        } else {
            updateLikeCount(exp);
        }
        String source = "";
        if (mAdapter instanceof FeedRecyclerViewAdapter) {
            source = ((FeedRecyclerViewAdapter) mAdapter).getFeedSource();
        }
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_LIKE);
        EventLog1.FeedLike.report1(ShareEventHelper.convertPostType(mPostBase), source, FeedButtonFrom.POST_CARD, mPostBase.getPid(), mPostBase.getUid(), mPostBase.getStrategy(),

        mPostBase.getOperationType(), mPostBase.getLanguage(), mPostBase.getTags(), FeedHelper.getRootOrPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs())
        ;
    }

    public void setLikeListener(View.OnClickListener likeListener) {
        this.likeListener = likeListener;
    }

    public void setShareListener(View.OnClickListener shareListener) {
        this.shareListener = shareListener;
    }

    public void setOnPostButtonClickListener(OnPostButtonClickListener listener) {
        mPostButtonListener = listener;
    }

    public void setBrowseClickListener(IBrowseClickListener iBrowseClickListener) {
        this.iBrowseClickListener = iBrowseClickListener;

        mForwardTab.setVisibility(View.GONE);
//        mLikeTab.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//        mLikeTab.setOnClickListener(this);
    }
}
