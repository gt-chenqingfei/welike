package com.redefine.welike.business.feeds.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.dialog.MenuItem;
import com.redefine.commonui.dialog.MenuItemIdConstant;
import com.redefine.commonui.dialog.OnMenuItemClickListener;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.commonui.fresco.loader.HeadUrlLoader;
import com.redefine.commonui.share.sharemedel.SharePackageModel;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.NetWorkUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.copy.RichTextClipboardManager;
import com.redefine.richtext.helper.RichTextLoader;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.InsertLikeCallBack;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.LikeManager;
import com.redefine.welike.business.feedback.ui.constants.FeedbackConstants;
import com.redefine.welike.business.feedback.ui.page.ReportDescActivity;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.feeds.ui.FeedDetailRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.bean.FeedDetailCommentHeadBean;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.feeds.ui.contract.IFeedDetailContract;
import com.redefine.welike.business.feeds.ui.fragment.FeedDetailPageSwitcher;
import com.redefine.welike.business.feeds.ui.fragment.IRefreshDelegate;
import com.redefine.welike.business.feeds.ui.listener.IFeedDetailCommentOpListener;
import com.redefine.welike.business.feeds.ui.util.FeedHelper;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.FeedDetailCommentOrderViewHolder;
import com.redefine.welike.business.feeds.ui.viewholder.TextFeedViewHolder;
import com.redefine.welike.business.publisher.management.PublisherEventManager;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.commonui.event.commonenums.FeedButtonFrom;
import com.redefine.welike.commonui.event.helper.LoginEventHelper;
import com.redefine.welike.commonui.event.helper.ShareEventHelper;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.popupwindow.ShareVideoPopupWindow;
import com.redefine.welike.commonui.share.CustomShareMenuFactory;
import com.redefine.welike.commonui.share.ShareHelper;
import com.redefine.welike.commonui.share.ShareMenu;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.commonui.view.FeedListVideoController;
import com.redefine.welike.commonui.widget.IFollowBtn;
import com.redefine.welike.commonui.widget.UserFollowBtn;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.NewShareEventManager;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by liwb on 2018/1/11.
 */

public class FeedDetailView implements IFeedDetailContract.IFeedDetailView, View.OnClickListener
        , IRefreshDelegate, BaseErrorView.IErrorViewClickListener
//        OnSuperLikeExpCallback
{

    private final IPageStackManager mPageStackManager;
    private ViewGroup mFeedDetailHeadContainer;
    private FeedDetailRecyclerViewAdapter mFeedRecyclerViewAdapter;
    private BaseFeedViewHolder mFeedViewHolder;
    private int mFeedViewType = -1;
    private FeedDetailPageSwitcher mFeedDetailPageSwitch;
    private IFeedDetailContract.IFeedDetailPresenter mPresenter;
    private SmartRefreshLayout mRefreshLayout;
    private AppBarLayout mAppBarLayout;
    private View mTopShadowView;
    private FrameLayout mTopContentLayout;
    private View mBottomFeedForwardBtn;
    private View mBottomFeedSharedBtn;
    private TextView mBottomFeedCommentBtn;
    private View mBottomFeedEmojiBtn;
    private ImageView mBottomFeedLikeBtn;
    private TextView mForwardText;
    private TextView mCommentText;
    private TextView mLikeText;
    private TextView mFeedDetailTitleView;
    private ViewGroup mFragmentPageContainer;
    private View mBackBtn;
    private View mReportBtn;
    private View mMoreBtn;
    private LinearLayout feedDetailTopLl;
    private View divider;
    private EmptyView mEmptyView;
    private LinearLayout headLayout;
    private SimpleDraweeView mUserHeader;
    private TextView commonNickName;
    private ApolloVideoView mApolloVideoView;
    private FeedAdapterObserver mAdapterObserver;
    private PostBase mPostBase;
    private int mIndex;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private RelativeLayout mSwitchRl;
    //    private SuperLikeDetailHelper superLikeHelper;
    private SimpleMenuDialog mMenuDialog;
    private int topViewHeight = -1;
    private ShareVideoPopupWindow mShareVideoPopupWindow;

    private UserFollowBtn userFollowBtn;

    private FeedDetailCommentHeadBean.CommentSortType mSortType;

//    private boolean isBrowse = false;

    public FeedDetailView(IPageStackManager pageStackManager) {
        mPageStackManager = pageStackManager;
    }

    public FeedDetailView(IPageStackManager pageStackManager, Bundle pageBundle) {
        mPageStackManager = pageStackManager;
    }

    @Override
    public View createView(Context context, Bundle saveState) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_detail_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View rootView) {
        mRefreshLayout = rootView.findViewById(R.id.feed_detail_refresh_layout);
        mAppBarLayout = rootView.findViewById(R.id.feed_detail_app_bar);
        mTopShadowView = rootView.findViewById(R.id.feed_detail_title_top_shadow);
        mTopContentLayout = rootView.findViewById(R.id.feed_detail_head_top_shadow);
        userFollowBtn = rootView.findViewById(R.id.common_feed_follow_btn);
        mFeedDetailTitleView = rootView.findViewById(R.id.common_title_view);
        mBackBtn = rootView.findViewById(R.id.common_back_btn);
        mMoreBtn = rootView.findViewById(R.id.common_more_btn);
        mReportBtn = rootView.findViewById(R.id.common_feed_report);
        mFeedDetailTitleView.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_detail"));
        mFeedDetailHeadContainer = rootView.findViewById(R.id.feed_detail_top_view);
        mForwardText = rootView.findViewById(R.id.feed_detail_head_tab_forward);
        mCommentText = rootView.findViewById(R.id.feed_detail_head_tab_comment);
        mLikeText = rootView.findViewById(R.id.feed_detail_head_tab_like);
        mEmptyView = rootView.findViewById(R.id.common_empty_view);
        mErrorView = rootView.findViewById(R.id.common_error_view);
        mLoadingView = rootView.findViewById(R.id.common_loading_view);
//        mForwardTab = rootView.findViewById(R.id.feed_detail_head_tab_forward_layout);
//        mCommentTab = rootView.findViewById(R.id.feed_detail_head_tab_comment_layout);
//        mLikeTab = rootView.findViewById(R.id.feed_detail_head_tab_like_layout);
        mSwitchRl = rootView.findViewById(R.id.switch_rl);

        mBottomFeedForwardBtn = rootView.findViewById(R.id.feed_detail_bottom_forward);
        mBottomFeedSharedBtn = rootView.findViewById(R.id.detail_quick_share);

        mBottomFeedCommentBtn = rootView.findViewById(R.id.feed_detail_bottom_comment);
        mBottomFeedEmojiBtn = rootView.findViewById(R.id.feed_detail_bottom_emoji);
        mFragmentPageContainer = rootView.findViewById(R.id.feed_detail_view_container);
        mBottomFeedLikeBtn = rootView.findViewById(R.id.feed_detail_bottom_like);
        mBottomFeedCommentBtn.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_detail_input_placeholder"));
        mBackBtn.setOnClickListener(this);
        mReportBtn.setOnClickListener(this);
        mErrorView.setOnErrorViewClickListener(this);
//        superLikeHelper = new SuperLikeDetailHelper();


        feedDetailTopLl = rootView.findViewById(R.id.feed_detail_top_ll);
//        divider = rootView.findViewById(R.id.divider);
        headLayout = rootView.findViewById(R.id.head_layout);
        mUserHeader = rootView.findViewById(R.id.title_user_header);
        commonNickName = rootView.findViewById(R.id.common_feed_name);
        if (!AccountManager.getInstance().isLoginComplete()) {
            userFollowBtn.addOtherListener(this);
        }
        headLayout.setOnClickListener(this);
    }

    @Override
    public void initViewDetail(final PostBase postBase, int index) {
        showContentView();
        mPostBase = postBase;
        // ShareSp 存储用户的排序方式 没有存储的情况下 直接判断postBase的commentCount > 10 Top
        int commentSortValue = SpManager.Setting.getCommentSortValue(MyApplication.getAppContext());
        if (commentSortValue == -1) {
            mSortType = mPostBase.getCommentCount() >= FeedConstant.COMMENT_COUNT ?
                    FeedDetailCommentHeadBean.CommentSortType.HOT : FeedDetailCommentHeadBean.CommentSortType.CREATED;
        } else {
            switch (commentSortValue) {
                case FeedConstant.COMMENT_SORT_TOP:
                    mSortType = FeedDetailCommentHeadBean.CommentSortType.HOT;
                    break;
                case FeedConstant.COMMENT_SORT_LATEST:
                    mSortType = FeedDetailCommentHeadBean.CommentSortType.CREATED;
                    break;
            }
        }

        initCommentOrderView();
        mIndex = index;
        mFeedDetailPageSwitch = new FeedDetailPageSwitcher(mPageStackManager, mPostBase, this);
        mAdapterObserver = new FeedAdapterObserver(this);
        mFeedRecyclerViewAdapter = new FeedDetailRecyclerViewAdapter(mPageStackManager, EventConstants.FEED_PAGE_POST_DETAIL);
        refreshMainFeed(mPostBase);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mFeedDetailPageSwitch.startRefresh();
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >= 0) {
                    mRefreshLayout.setEnableRefresh(true);
                    mRefreshLayout.setEnableOverScrollDrag(false);
                } else {
                    mRefreshLayout.setEnableRefresh(false);
                    mRefreshLayout.setEnableOverScrollDrag(false);
                }

                if (verticalOffset < -100) {
                    headLayout.setVisibility(View.VISIBLE);
                    mFeedDetailTitleView.setVisibility(View.GONE);
                } else {
                    headLayout.setVisibility(View.GONE);
                    mFeedDetailTitleView.setVisibility(View.VISIBLE);
                }


                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    mTopShadowView.setVisibility(View.GONE);
                    mTopContentLayout.setForeground(MyApplication.getAppContext().getResources().getDrawable(R.color.white));
                    mTopContentLayout.setBackgroundResource(R.color.white);
                } else {
                    mTopShadowView.setVisibility(View.VISIBLE);
                    mTopContentLayout.setForeground(MyApplication.getAppContext().getResources().getDrawable(R.drawable.common_shadow_icon));
                    mTopContentLayout.setBackgroundResource(R.color.common_color_f8f8f8);
                }

            }
        });

        mBottomFeedForwardBtn.setOnClickListener(this);
        mBottomFeedCommentBtn.setOnClickListener(this);
        mBottomFeedEmojiBtn.setOnClickListener(this);
        mBottomFeedSharedBtn.setOnClickListener(this);


        mMoreBtn.setOnClickListener(this);
        if (mPostBase.isDeleted()) {
            mMoreBtn.setVisibility(View.GONE);
        } else {
            mMoreBtn.setVisibility(View.VISIBLE);
        }
        updateFeedCount();
        if (mIndex != FeedConstant.ERROR_INDEX) {
            mAppBarLayout.setExpanded(false, false);
            setSelectIndex(mIndex);
        } else {
            mAppBarLayout.setExpanded(true, false);
            setSelectIndex(FeedConstant.PAGE_COMMENT_INDEX);
        }


        // Report 按钮去掉了

//        if (AccountManager.getInstance().isSelf(mPostBase.getUid())) {
//            mReportBtn.setVisibility(View.INVISIBLE);
//        } else {
//            mReportBtn.setVisibility(View.VISIBLE);
//        }

        if (postBase.isDeleted()) {
            onPostDelete();
        }
        PostEventManager.INSTANCE.addPost(postBase.getPid());

        if (!AccountManager.getInstance().isLogin()) {
            EventLog.UnLogin.report11(StartEventManager.getInstance().getInterestId());
        }
    }


    private FeedDetailCommentHeadBean feedDetailCommentHeadBean;
    private FeedDetailCommentOrderViewHolder headerViewHolder;

    private void initCommentOrderView() {
        feedDetailCommentHeadBean = new FeedDetailCommentHeadBean(mSortType);
        headerViewHolder = new FeedDetailCommentOrderViewHolder(mSwitchRl, new IFeedDetailCommentOpListener() {
            @Override
            public void onSwitchCommentOrder(@NotNull FeedDetailCommentHeadBean.CommentSortType sortType) {
                mFeedDetailPageSwitch.onChangeCommentOrder(sortType);
                feedDetailCommentHeadBean.setmSortType(sortType);
                headerViewHolder.bindViews(feedDetailCommentHeadBean);
            }
        });
        headerViewHolder.bindViews(feedDetailCommentHeadBean);

    }

    private void refreshMainFeed(final PostBase postBase) {
        try {
            mFeedRecyclerViewAdapter.unregisterAdapterDataObserver(mAdapterObserver);
        } catch (Exception e) {
            // do nothing
        }
        postBase.setRealHot(postBase.isHot());
        postBase.setHot(false);
//        VipUtil.set(vipAvatar, postBase.getHeadUrl(), postBase.getVip());
        HeadUrlLoader.getInstance().loadHeaderUrl2(mUserHeader, postBase.getHeadUrl());
        commonNickName.setText(postBase.getNickName());
        mFeedDetailHeadContainer.removeAllViews();
        mFeedRecyclerViewAdapter.removeAll();
        mFeedRecyclerViewAdapter.addFeed(postBase);
        mPresenter.initFeedFollowBtn(postBase, userFollowBtn);
        if (!AccountManager.getInstance().isLoginComplete()) {
            userFollowBtn.setOnClickFollowBtnListener(null);
        }
        mPresenter.checkShowFollowBtn(userFollowBtn.getContext(), postBase);
        try {
            mFeedRecyclerViewAdapter.registerAdapterDataObserver(mAdapterObserver);
        } catch (Exception e) {
            // do nothing
        }
        mFeedDetailPageSwitch.refreshPost(postBase);
        if (mFeedViewHolder == null || mFeedRecyclerViewAdapter.getItemViewType(0) != mFeedViewType) {
            mFeedViewType = mFeedRecyclerViewAdapter.getItemViewType(0);
            mFeedViewHolder = (BaseFeedViewHolder) mFeedRecyclerViewAdapter.onCreateItemViewHolder(mFeedDetailHeadContainer, mFeedViewType);
            mFeedRecyclerViewAdapter.onBindItemViewHolder(mFeedViewHolder, 0);
        } else if (mFeedViewHolder != null) {
            mFeedRecyclerViewAdapter.onBindItemViewHolder(mFeedViewHolder, 0);
        }
        mFeedDetailHeadContainer.addView(mFeedViewHolder.itemView);//real view
        if (!AccountManager.getInstance().isLogin()) {
            mFeedViewHolder.setBrowseClickListener(new IBrowseClickListener() {
                @Override
                public void onBrowseClick(int tye, boolean isShow, int showType) {
                    mPresenter.onBrowseFeedDetailClick(tye, isShow, showType);
                    if (tye == BrowseConstant.TYPE_SHARE) {
                        EventLog.UnLogin.report16(3, EventConstants.LABEL_OTHER);
                    } else if (tye == BrowseConstant.TYPE_FOLLOW) {
                        // 在ViewHolder 点击的时候去判断 是否隐藏外部的Follow
                        mPresenter.checkShowFollowBtn(userFollowBtn.getContext(), postBase);
                    }
                }
            });
        }

        mBottomFeedLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (!AccountManager.getInstance().isLogin()) {

                    BrowseEventStore.INSTANCE.updateLikeCount(mPostBase, new InsertLikeCallBack() {
                        @Override
                        public void onLoadEntity(boolean inserted, int count) {
                            if (inserted) {
                                if (count % 3 == 1)
                                    HalfLoginManager.getInstancce().showLoginDialog(mBottomFeedLikeBtn.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
//                                    RegisterActivity.Companion.show(mBottomFeedLikeBtn.getContext(), 1, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
                            } else {
                                HalfLoginManager.getInstancce().showLoginDialog(mBottomFeedLikeBtn.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
//                                RegisterActivity.Companion.show(v.getContext(), 2, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
                            }
                            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                @Override
                                public void run() {
                                    mPresenter.onBrowseFeedDetailClick(BrowseConstant.TYPE_LIKE, true, BrowseConstant.TYPE_SHOW_CONTENT_LIKE);
                                }
                            });
                        }
                    });
                }
                if (mPostBase.isLike()) return;

                onSuperLikeExpCallback(true, (int) mPostBase.getSuperLikeExp() + 1);
                if (!AccountManager.getInstance().isLogin()) {
                    EventLog.UnLogin.report22(EventConstants.UNLOGIN_FROM_PAGE_POST_DETAIL, null);
                }

            }
        });
//            superLikeHelper.bindView(mPageStackManager, mBottomFeedLikeBtn, postBase, this);
    }

    private void playVideoIfVideoPost(View itemView) {
        if (1==1) {
            return;
        }
        if (!NetWorkUtil.isWifiConnected(itemView.getContext())) {
            return;
        }
        if (mApolloVideoView != null) {
            return;
        }
        VideoPost videoPost = null;
        if (mPostBase instanceof VideoPost) {
            videoPost = (VideoPost) mPostBase;
        } else if (mPostBase instanceof ForwardPost) {
            PostBase rootPost = ((ForwardPost) mPostBase).getRootPost();
            if (rootPost instanceof VideoPost) {
                videoPost = (VideoPost) rootPost;
            }
        }
        if (videoPost == null || videoPost.isDeleted()
                || TextUtils.equals(videoPost.getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
            return;
        }

        ViewGroup videoViewContainer = itemView.findViewById(R.id.video_feed_video_player);
        FeedListVideoController videoController = itemView.findViewById(R.id.video_controller);
        if (videoController == null || videoViewContainer == null) {
            return;
        }

        mApolloVideoView = new ApolloVideoView(itemView.getContext().getApplicationContext());
        videoController.setCoverUrl(videoViewContainer, mPostBase, videoPost, videoPost.getWidth(), videoPost.getHeight());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        videoViewContainer.removeAllViews();
        videoViewContainer.addView(mApolloVideoView.getView(), layoutParams);
        mApolloVideoView.setMediaController(videoController);
        mApolloVideoView.setMediaPlayerInvoker(videoController);
        mApolloVideoView.setMute(true);
        mApolloVideoView.setDataSource(videoPost.getVideoUrl());
        itemView.setKeepScreenOn(true);
    }

    @Override
    public void updatePostToView(PostBase postBase) {
        mPostBase = postBase;

        refreshMainFeed(postBase);
        playVideoIfVideoPost(mFeedViewHolder.itemView);
        updateFeedCount();
        if (mPostBase.isDeleted()) {
            mMoreBtn.setVisibility(View.GONE);
        } else {
            mMoreBtn.setVisibility(View.VISIBLE);
        }
        if (postBase.isDeleted()) {
            onPostDelete();
        }
        if (!postBase.isFollowing()) {
            EventLog1.Follow.report3(EventConstants.FEED_PAGE_POST_DETAIL, postBase.getUid(), postBase.getPid(), postBase.getStrategy(), postBase.getOperationType(), postBase.getLanguage(), postBase.getTags());
        }
    }

    private void updateFeedCount() {
        updateLikeCount((int) mPostBase.getSuperLikeExp());
        updateForwardCount();
        updateCommentCount();
    }

    private void updateForwardCount() {
        if (mPostBase.getForwardCount() > 0) {
            String count = FeedHelper.getForwardCount(mPostBase.getForwardCount());
            mForwardText.setText(String.format(mLikeText.getContext().getString(R.string.post_detail_repost_count), count));
        } else {
            mForwardText.setText(String.format(mLikeText.getContext().getString(R.string.post_detail_repost_count), "0"));
        }
    }

    public void updateLikeCount(int exp) {
        Drawable drawable = ResourceTool.getBoundDrawable(mBottomFeedLikeBtn.getResources(), LikeManager.getFeedImage(exp), ScreenUtils.dip2Px(24), ScreenUtils.dip2Px(24));
        mBottomFeedLikeBtn.setImageDrawable(drawable);

        if (mPostBase.getLikeCount() > 0) {
            String count = FeedHelper.getLikeCount(mPostBase.getLikeCount());
            mLikeText.setText(String.format(mLikeText.getContext().getString(R.string.post_detail_like_count), count));
        } else {
            mLikeText.setText(String.format(mLikeText.getContext().getString(R.string.post_detail_like_count), "0"));
        }
    }

    private void updateCommentCount() {
        String commentCount = ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "comment_count");
        if (mPostBase.getCommentCount() > 0) {
            String count = FeedHelper.getCommentCount(mPostBase.getCommentCount());
            mCommentText.setText(String.format(commentCount, count));
        } else {
            mCommentText.setText(String.format(commentCount, ""));
        }
    }

    private void setSelectIndex(int index) {
        mIndex = index;
        mFeedDetailPageSwitch.setCurrentItem(mFragmentPageContainer, mIndex);
    }

    @Override
    public void showErrorView() {
        mEmptyView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }

    private void showContentView() {
        mEmptyView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingView() {
        mEmptyView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isShowContent() {
        return mEmptyView.getVisibility() == View.GONE && mLoadingView.getVisibility() == View.GONE && mErrorView.getVisibility() == View.GONE;
    }

    @Override
    public void showLoginSnackBar(int type) {


//        if (BrowseManager.getLoginB()) {
//            RegisterLoginMethodDialog.show(mRefreshLayout.getContext());
//            return;
//        }
//
//        String info;
//        switch (type) {
//
//            case BrowseConstant.TYPE_SHOW_CONTENT_FOLLOW0:
//                info = ResourceTool.getString("common_continue_follow_by_login0");
//                break;
//            case BrowseConstant.TYPE_SHOW_CONTENT_FOLLOEW1:
//                info = ResourceTool.getString("common_continue_by_login");
//                break;
//            case BrowseConstant.TYPE_SHOW_CONTENT_LIKE:
//                info = ResourceTool.getString("common_continue_like_by_login");
//                break;
//
//            case BrowseConstant.TYPE_SHOW_CONTENT_DEFAULT:
//            default:
//                info = ResourceTool.getString("common_continue_by_login");
//        }
//        ActionSnackBar.getInstance().showLoginSnackBar(mRefreshLayout,
//                info,
//                ResourceTool.getString("common_login"), 3000, new ActionSnackBar.ActionBtnClickListener() {
//                    @Override
//                    public void onActionClick(View view) {
//                        // TODO: 2018/7/11
//                        if (BrowseManager.getLoginA())
//                            RegisterLoginMethodDialog.show(mRefreshLayout.getContext());
//                        else
//                            RegistActivity.show(mRefreshLayout.getContext());
//                    }
//                });
        HalfLoginManager.getInstancce().showLoginDialog(mRefreshLayout.getContext(), new RegisterAndLoginModel(LoginEventHelper.convertTypeToPageSource(type)));
//        RegisterActivity.Companion.show(mRefreshLayout.getContext(),1, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
    }

    @Override
    public void refreshFollow(boolean follow) {
        if (mFeedViewHolder == null) {
            return;
        }
        if (follow) {
            userFollowBtn.setFollowStatus(IFollowBtn.FollowStatus.FOLLOWING);
            mFeedViewHolder.getFeedHeadView().mFollowBtnPresenter.getView().setFollowStatus(IFollowBtn.FollowStatus.FOLLOWING);
        } else {
            userFollowBtn.setFollowStatus(IFollowBtn.FollowStatus.FOLLOW);
            mFeedViewHolder.getFeedHeadView().mFollowBtnPresenter.getView().setFollowStatus(IFollowBtn.FollowStatus.FOLLOW);
        }
    }

    @Override
    public void onActivityResume() {
        if (mApolloVideoView != null) {
            mApolloVideoView.onResume();
        }
    }

    @Override
    public void onActivityPause() {
        if (mApolloVideoView != null) {
            mApolloVideoView.onPause();
        }
    }

    @Override
    public void onActivityDestroy() {
        if (mApolloVideoView != null) {
            mApolloVideoView.destroy();
        }
    }

    @Override
    public void onPostDelete() {
        mEmptyView.setVisibility(View.VISIBLE);
        mMoreBtn.setVisibility(View.GONE);
        mReportBtn.setVisibility(View.GONE);
        mEmptyView.showEmptyImageText(R.drawable.feed_detail_delete_img, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "forward_feed_delete_content"));
    }

    @Override
    public void setPresenter(IFeedDetailContract.IFeedDetailPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onFeedFollowChange() {
        if (mFeedViewHolder != null) {
            mFeedRecyclerViewAdapter.onBindItemViewHolder(mFeedViewHolder, 0);
        }
        if (!AccountManager.getInstance().isLogin()) {
            EventLog.UnLogin.report24(EventConstants.UNLOGIN_FROM_PAGE_POST_DETAIL, mPostBase.getUid());
        }
    }

    @Override
    public void onNewComment(Comment comment) {
        mFeedDetailPageSwitch.onNewComment(comment);
        mPostBase.setCommentCount(mPostBase.getCommentCount() + 1);
        updateCommentCount();
    }

    @Override
    public void onNewForward(PostBase postBase) {
        mFeedDetailPageSwitch.onNewForward(postBase);
        mPostBase.setForwardCount(mPostBase.getForwardCount() + 1);
        updateForwardCount();
    }


    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {
        if (mFeedDetailPageSwitch != null) {
            mFeedDetailPageSwitch.destroy(mFragmentPageContainer);
        }
        if (mFeedRecyclerViewAdapter != null) {
            try {
                mFeedRecyclerViewAdapter.unregisterAdapterDataObserver(mAdapterObserver);

            } catch (Exception e) {
                // do nothing
            }
            mFeedRecyclerViewAdapter.destroy();
        }

    }

    @Override
    public void onClick(final View v) {
        if (v == mBottomFeedForwardBtn) {

            if (!AccountManager.getInstance().isLoginComplete()) {
                mPresenter.onBrowseFeedDetailClick(BrowseConstant.TYPE_REPOST, true, 0);
                return;
            }
            mPresenter.onForwardFeed(v.getContext(), mPostBase);
            PublisherEventManager.INSTANCE.setSource(3);
            PublisherEventManager.INSTANCE.setMain_source(0);
        } else if (v == userFollowBtn) {
            mPresenter.onFollowUser(mPostBase, userFollowBtn.getContext());
        } else if (v == mBottomFeedCommentBtn) {
            mPresenter.onCommentFeed(v.getContext(), mPostBase);
            PublisherEventManager.INSTANCE.setSource(3);
            PublisherEventManager.INSTANCE.setMain_source(0);
        } else if (v == mBottomFeedEmojiBtn) {
            mPresenter.onCommentFeed(v.getContext(), mPostBase, true);
            PublisherEventManager.INSTANCE.setSource(3);
            PublisherEventManager.INSTANCE.setMain_source(0);

        } else if (v == mBackBtn) {
            mPresenter.onBackPressed();
        } else if (v == mBottomFeedSharedBtn) {
            if (!AccountManager.getInstance().isLoginComplete()) {
                mPresenter.onBrowseFeedDetailClick(BrowseConstant.TYPE_SHARE, false, 0);
//                return;
            }
            doShare(mPostBase);
        } else if (v == mMoreBtn) {
            if (!AccountManager.getInstance().isLogin()) {

                mPresenter.onBrowseFeedDetailClick(BrowseConstant.TYPE_UNKOWN, true, 0);
                return;
            }
            onMoreMenuClick(v.getContext(), mPostBase);
        } else if (v == headLayout) {
            if (!AccountManager.getInstance().isLogin()) {
                mPresenter.onBrowseFeedDetailClick(BrowseConstant.TYPE_HEAD, false, 0);
            }
            UserHostPage.launch(true, mPostBase.getUid());
        } else if (v == mReportBtn) {
            if (mPostBase == null) {
                return;
            }
            MenuItem porn = new MenuItem(MenuItemIdConstant.MENU_ITEM_REPORT_PORN, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason1"));
            MenuItem violation = new MenuItem(MenuItemIdConstant.MENU_ITEM_REPORT_VIOLATION, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason2"));
            MenuItem annoying = new MenuItem(MenuItemIdConstant.MENU_ITEM_REPORT_ANNOYING, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason3"));
            MenuItem infringement = new MenuItem(MenuItemIdConstant.MENU_ITEM_REPORT_INFRINGEMENT, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason4"));
            final MenuItem copyright = new MenuItem(MenuItemIdConstant.MENU_ITEM_REPORT_COPYRIGHT, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "report_reason5"));
            List<MenuItem> list = new ArrayList<>();
            list.add(porn);
            list.add(violation);
            list.add(annoying);
            list.add(infringement);
            list.add(copyright);

            SimpleMenuDialog.show(v.getContext(), list, new OnMenuItemClickListener() {
                @Override
                public void onMenuClick(MenuItem menuItem) {
//                    mPresenter.onReport(v.getContext(), mPostBase.getPid(), mPostBase.getUid(), menuItem.menuText.toString());
                    int reasonId = 0;
                    if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_REPORT_PORN) {
                        reasonId = 0;
                    } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_REPORT_VIOLATION) {
                        reasonId = 1;
                    } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_REPORT_ANNOYING) {
                        reasonId = 2;
                    } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_REPORT_INFRINGEMENT) {
                        reasonId = 3;
                    } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_REPORT_COPYRIGHT) {
                        reasonId = 4;
                    }
                    ReportDescActivity.show(reasonId, menuItem.menuText.toString(), mPostBase);
//                    if (menuItem == copyright) {
//                        EmailReporter.emailReport(v.getContext(), mPostBase.getPid());
//                    }
                }
            });
        }
    }

    private void onMoreMenuClick(final Context context, final PostBase postBase) {
        Function1<ShareMenu, Unit> menuInvoker = new Function1<ShareMenu, Unit>() {
            @Override
            public Unit invoke(ShareMenu shareMenu) {
                switch (shareMenu) {
                    case DELETE:
                        doDelete(postBase);
                        break;
                    case REPORT:
                        doReport(postBase);
                        break;
                    case BLOCK:
                        mFeedRecyclerViewAdapter.doBlockUser(context, postBase);
                        EventLog1.BlockUser.report1(mPostBase.getUid(), EventConstants.FEED_PAGE_POST_DETAIL, mPostBase.getPid(), mPostBase.getLanguage(), mPostBase.getTags(), null);
                        break;
                    case UNBLOCK:
                        mFeedRecyclerViewAdapter.doUnblockUser(context, postBase);
                        EventLog1.BlockUser.report2(mPostBase.getUid(), EventConstants.FEED_PAGE_POST_DETAIL, mPostBase.getPid(), mPostBase.getLanguage(), mPostBase.getTags(), null);
                        break;
                }
                return null;
            }
        };
        SharePackageModel delete = CustomShareMenuFactory.Companion.createMenu(ShareMenu.DELETE, menuInvoker);
        SharePackageModel report = CustomShareMenuFactory.Companion.createMenu(ShareMenu.REPORT, menuInvoker);
        SharePackageModel block = CustomShareMenuFactory.Companion.createMenu(ShareMenu.BLOCK, menuInvoker);
        SharePackageModel unBlock = CustomShareMenuFactory.Companion.createMenu(ShareMenu.UNBLOCK, menuInvoker);
        List<SharePackageModel> list = new ArrayList<>();
        if (AccountManager.getInstance().isSelf(mPostBase.getUid())) {
            list.add(delete);
        } else {
            list.add(report);
            if (mPostBase.isBlock()) {
                list.add(unBlock);
            } else {
                list.add(block);
            }
        }
        NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_POST_DETAIL);
        NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_POST_DETAIL);

        EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(mPostBase),
                null, EventLog1.Share.ShareFrom.POST_DETAIL, EventLog1.Share.PopPage.POST_DETAIL, mPostBase.getStrategy(), mPostBase.getOperationType(),
                mFeedRecyclerViewAdapter.getFeedSource(), mPostBase.getPid(), mPostBase.getLanguage(), mPostBase.getTags(), mPostBase.getUid(),
                FeedHelper.getRootPostId(mPostBase), FeedHelper.getRootPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
        ShareHelper.sharePostWithCustomMenu(context, postBase, false, eventModel, list);
    }

    private void doReport(PostBase post) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FeedbackConstants.FEEDBACK_KEY_POST, post);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_REPOER_PAGE, bundle));
    }

    private void doShare(PostBase post) {
//        TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder()
//                .setCategory(TrackerConstant.EVENT_EV_CT)
//                .setAction(TrackerConstant.EVENT_SHARE_SOURCE)
//                .setLabel("postDetail").build());
        if (post == null) {
            ToastUtils.showLong(ErrorCode.showErrCodeText(ErrorCode.ERROR_NETWORK_INVALID));
            return;
        }

        if (FeedHelper.shouldShowShareMenu(mPostBase)) {
            if (mShareVideoPopupWindow == null) {
                mShareVideoPopupWindow = new ShareVideoPopupWindow(mBottomFeedSharedBtn.getContext(), new ShareVideoPopupWindow.OnShareClickListener() {
                    @Override
                    public void onShareVideo() {
                        ShareEventManager.INSTANCE.report4(EventConstants.SHARE_TYPE_VIDEO, 1);
                        NewShareEventManager.INSTANCE.setVideoPostType(EventConstants.NEW_SHARE_VIDEO_POST_TYPE_VIDEO);
                        NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_POST_DETAIL);
                        NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_POST_DETAIL);

                        EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(mPostBase),
                                EventLog1.Share.VideoPostType.VIDEO_FILE, EventLog1.Share.ShareFrom.POST_DETAIL, EventLog1.Share.PopPage.POST_DETAIL, mPostBase.getStrategy(),
                                mPostBase.getOperationType(), mFeedRecyclerViewAdapter.getFeedSource(), mPostBase.getPid(), mPostBase.getLanguage(), mPostBase.getTags(), mPostBase.getUid(),
                                FeedHelper.getRootPostId(mPostBase), FeedHelper.getRootPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
                        ShareHelper.sharePostToWhatsApp(mBottomFeedSharedBtn.getContext(), mPostBase, true, eventModel);
                    }

                    @Override
                    public void onShareLink() {
                        ShareEventManager.INSTANCE.report4(EventConstants.SHARE_TYPE_LINK, 1);
                        NewShareEventManager.INSTANCE.setVideoPostType(EventConstants.NEW_SHARE_VIDEO_POST_TYPE_LINK);
                        NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_POST_DETAIL);
                        NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_POST_DETAIL);

                        EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(mPostBase),
                                EventLog1.Share.VideoPostType.POST_LINK, EventLog1.Share.ShareFrom.POST_DETAIL, EventLog1.Share.PopPage.POST_DETAIL, mPostBase.getStrategy(),
                                mPostBase.getOperationType(), mFeedRecyclerViewAdapter.getFeedSource(), mPostBase.getPid(), mPostBase.getLanguage(), mPostBase.getTags(), mPostBase.getUid(),
                                FeedHelper.getRootPostId(mPostBase), FeedHelper.getRootPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
                        ShareHelper.sharePostToWhatsApp(mBottomFeedSharedBtn.getContext(), mPostBase, false, eventModel);
                    }
                });
            }

            mShareVideoPopupWindow.show(mBottomFeedSharedBtn);
            return;
        }
        NewShareEventManager.INSTANCE.setShareFrom(EventConstants.NEW_SHARE_SHARE_FROM_POST_DETAIL);
        NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_POST_DETAIL);

        EventModel eventModel = new EventModel(EventLog1.Share.ContentType.POST, ShareEventHelper.convertPostType(mPostBase),
                null, EventLog1.Share.ShareFrom.POST_DETAIL, EventLog1.Share.PopPage.POST_DETAIL, mPostBase.getStrategy(), mPostBase.getOperationType(),
                mFeedRecyclerViewAdapter.getFeedSource(), mPostBase.getPid(), mPostBase.getLanguage(), mPostBase.getTags(), mPostBase.getUid(),
                FeedHelper.getRootPostId(mPostBase), FeedHelper.getRootPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
        ShareHelper.sharePostToWhatsApp(mBottomFeedSharedBtn.getContext(), mPostBase, false, eventModel);
    }

    private void doDelete(PostBase mPostBase) {
        mFeedRecyclerViewAdapter.doRealFeedDelete(mMoreBtn.getContext(), mPostBase);
        mPageStackManager.getActivity().finish();
    }

    @Override
    public void startRefresh() {
        mFeedDetailPageSwitch.startRefresh();
    }

    @Override
    public void stopRefresh() {
        mRefreshLayout.finishRefresh();
    }

    @Override
    public void setRefreshEnable(boolean isEnable) {
        mRefreshLayout.setEnableRefresh(isEnable);
    }

    @Override
    public void onErrorViewClick() {
        mPresenter.onClickErrorView();
    }

    private void onSuperLikeExpCallback(boolean isLast, int exp) {

        if (isLast) {

            SinglePostManager.superLike(mPostBase, exp, !AccountManager.getInstance().isLogin());
            updateLikeCount((int) mPostBase.getSuperLikeExp());
            if (AccountManager.getInstance().isLogin())
                mFeedDetailPageSwitch.onNewLike(User.convertFromAccount(AccountManager.getInstance().getAccount()), mPostBase.getSuperLikeExp());
        } else {
            updateLikeCount(exp);
        }
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_LIKE);
        EventLog1.FeedLike.report1(ShareEventHelper.convertPostType(mPostBase), EventConstants.FEED_PAGE_POST_DETAIL, FeedButtonFrom.POST_DETAIL, mPostBase.getPid(), mPostBase.getUid(), mPostBase.getStrategy(),
                mPostBase.getOperationType(), mPostBase.getLanguage(), mPostBase.getTags(), FeedHelper.getRootOrPostUid(mPostBase), FeedHelper.getRootPostLanguage(mPostBase), FeedHelper.getRootPostTags(mPostBase), mPostBase.getSequenceId(),mPostBase.getReclogs());
        EventLog.Feed.report5(mPostBase.getPid(), 0, 1, 15, PostEventManager.getPostType(mPostBase), mPostBase.getStrategy(), mPostBase.getSequenceId());
    }

    public static class FeedAdapterObserver extends RecyclerView.AdapterDataObserver {
        private final WeakReference<IFeedDetailContract.IFeedDetailView> mWeakRef;

        public FeedAdapterObserver(IFeedDetailContract.IFeedDetailView hFeedRecyclerViewAdapter) {
            mWeakRef = new WeakReference<>(hFeedRecyclerViewAdapter);
        }

        @Override
        public void onChanged() {
            super.onChanged();
            IFeedDetailContract.IFeedDetailView ref = mWeakRef.get();
            if (ref != null) {
                ref.onFeedFollowChange();
            }
        }
    }
}
