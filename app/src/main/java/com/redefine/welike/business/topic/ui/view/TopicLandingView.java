package com.redefine.welike.business.topic.ui.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidkun.xtablayout.XTabLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.dialog.SimpleMenuDialog;
import com.redefine.commonui.fresco.loader.BannerUrlLoader;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.feeds.ui.viewholder.BaseFeedViewHolder;
import com.redefine.welike.commonui.event.expose.base.IItemExposeManager;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.topic.management.bean.TopicInfo;
import com.redefine.welike.business.topic.ui.adapter.TopicPageAdapter;
import com.redefine.welike.business.topic.ui.adapter.TopicTopRecylerViewAdapter;
import com.redefine.welike.business.topic.ui.contract.ITopicLandingContract;
import com.redefine.welike.business.topic.ui.fragment.TopicLandingFragment;
import com.redefine.welike.business.topic.ui.page.TopicLandingActivity;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.common.BrowseSchemeManager;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.share.ShareHelper;
import com.redefine.welike.commonui.share.ShareManagerWrapper;
import com.redefine.welike.commonui.share.model.ShareTitleModel;
import com.redefine.welike.commonui.share.model.ShortLinkModel;
import com.redefine.welike.commonui.share.model.WaterMarkModel;
import com.redefine.welike.commonui.widget.NoScrollViewPager;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.NewShareEventManager;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by liwenbo on 2018/3/20.
 */

public class TopicLandingView implements ITopicLandingContract.ITopicLandingView, ErrorView.IErrorViewClickListener
        , View.OnClickListener, AppBarLayout.OnOffsetChangedListener {


    private NoScrollViewPager mViewPager;
    private ITopicLandingContract.ITopicLandingPresenter mPresenter;
    private View mBackBtn;
    private RefreshLayout mRefreshLayout;
    private View mTopicUserLayout;
    private TextView mTopicCount;
    private String mUserHasPostThisTopicText;
    private TextView mTopicUserAllUser;
    private LinearLayout mTopicUserHeader;
    private LayoutInflater mInflater;
    private TextView mTitleView;
    private SimpleDraweeView mBannerDraweeView;
    private TextView mTopicNameView;
    private AppBarLayout mAppBarLayout;
    private View mSendPostBtn;
    private View mMoreBtn;
    private SimpleMenuDialog mMenuDialog;
    private TopicInfo mTopicInfo;
    private View mTablayoutShadowView;

//    private RelativeLayout mTabHot, mTabLatest;
//    private TextView mHotText, mLatestText;
//    private View mHotIndicator, mLatestIndicator;
//    private LinearLayout mTabViewGroup;

    private XTabLayout xTabLayout;

    private ViewGroup mTopicTopFeedContainer;
    private TopicTopRecylerViewAdapter mTopicTopRecylerViewAdapter;
    private BaseFeedViewHolder mFeedViewHolder;
    private int mFeedViewType = -1;
    private TopicLandingView.TopicdapterObserver mAdapterObserver;
    private RichTextView mTopicDetilIntroduce;
    private boolean isBrowse;
    private IItemExposeManager mViewTimeDelegate;


    private TopicPageAdapter topicPageAdapter;

    private View divider;

    private FragmentActivity mActivity;
    private String topicId;

    private List<TopicLandingFragment> fragmentPages;

    public TopicLandingView(Activity activity, Bundle bundle) {
        isBrowse = !AccountManager.getInstance().isLoginComplete();
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public View createView(Context context, Bundle savedInstanceState) {
        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.topic_landing_layout, null);

        mViewPager = view.findViewById(R.id.topic_landing_view_pager);
        mTablayoutShadowView = view.findViewById(R.id.topic_landing_view_shadow);
        mBannerDraweeView = view.findViewById(R.id.topic_landing_banner);
        mTopicNameView = view.findViewById(R.id.topic_landing_topic_name);
        mTitleView = view.findViewById(R.id.common_title_view);
        mTopicUserLayout = view.findViewById(R.id.ll_top_user_all);
        mAppBarLayout = view.findViewById(R.id.topic_landing_app_bar);
        mTopicCount = view.findViewById(R.id.topic_landing_topic_count);
        mTopicUserAllUser = view.findViewById(R.id.tv_topic_landing_user_all);
        mSendPostBtn = view.findViewById(R.id.topic_landing_post_topic_image);
        mTopicUserHeader = view.findViewById(R.id.ll_topic_user_headers);
        mUserHasPostThisTopicText = ResourceTool.getString("post_nums_on_this_topic");
        divider = view.findViewById(R.id.divider);
        xTabLayout = view.findViewById(R.id.topic_page_tabs);

        mTopicTopFeedContainer = view.findViewById(R.id.topic_feed_container);

        mTopicDetilIntroduce = view.findViewById(R.id.topic_detial_content);

        mBackBtn = view.findViewById(R.id.common_back_btn);
        mMoreBtn = view.findViewById(R.id.common_more_btn);
        mRefreshLayout = view.findViewById(R.id.topic_landing_refresh_layout);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);
        mTopicUserAllUser.setText(ResourceTool.getString("all"));

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
//                mPresenter.onRefresh();

                PostEventManager.INSTANCE.setAction(EventConstants.PULL_DOWN_REFRESH);
                fragmentPages.get(mViewPager.getCurrentItem()).refresh();
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(this);
        mTitleView.setText(R.string.discover_trending_hashtag);

        xTabLayout.addTab(xTabLayout.newTab().setText(ResourceTool.getString("discover_hot")));
        xTabLayout.addTab(xTabLayout.newTab().setText(ResourceTool.getString("discover_latest")));
        xTabLayout.getTabAt(0).select();
        mBackBtn.setOnClickListener(this);
        mMoreBtn.setOnClickListener(this);
        mSendPostBtn.setOnClickListener(this);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == TopicLandingActivity.TAB_HOT) {
                    selectHotTab();
                } else {
                    selectLatestTab();
                }
                xTabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        xTabLayout.addOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                if (tab.getPosition() == TopicLandingActivity.TAB_HOT) {
                    selectHotTab();
                } else {
                    selectLatestTab();
                }
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {

            }
        });


//        if (isBrowse)
//            mSendPostBtn.setVisibility(View.GONE);

        PostEventManager.INSTANCE.reset();
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);

        return view;
    }


    @Override
    public void initViewPager(String topicId) {
        this.topicId = topicId;
        fragmentPages = new ArrayList<>();

        topicPageAdapter = new TopicPageAdapter(mActivity.getSupportFragmentManager());

        TopicLandingFragment feedHotPostsPage = TopicLandingFragment.create(topicId, TopicLandingActivity.TAB_HOT);

        feedHotPostsPage.setPresenter(this);

        fragmentPages.add(feedHotPostsPage);

        TopicLandingFragment feedLatestPostsPage = TopicLandingFragment.create(topicId, TopicLandingActivity.TAB_LATEST);

        feedLatestPostsPage.setPresenter(this);

        fragmentPages.add(feedLatestPostsPage);

        topicPageAdapter.initFragments(fragmentPages);

        mViewPager.setAdapter(topicPageAdapter);

    }


    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void destroy() {
        if (mTopicTopRecylerViewAdapter != null) {
            try {
                mTopicTopRecylerViewAdapter.unregisterAdapterDataObserver(mAdapterObserver);

            } catch (Exception e) {
                // do nothing
            }
            mTopicTopRecylerViewAdapter.destroy();
        }
    }

    @Override
    public void onErrorViewClick() {
        mPresenter.onRefresh();
        PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
    }

    @Override
    public void finishRefresh(boolean isSuccess) {
        mRefreshLayout.finishRefresh(300, isSuccess);
    }

    @Override
    public void setRefreshEnable(boolean b) {
        mRefreshLayout.setEnableRefresh(b);
    }

    @Override
    public void dismissNearBy() {
        mTopicUserLayout.setVisibility(View.GONE);
        mTopicNameView.setVisibility(View.GONE);
        mTopicCount.setVisibility(View.GONE);
    }

    @Override
    public void shoTopicInfo(TopicInfo topicInfo, List<User> userList) {
        mTopicInfo = topicInfo;

        if (null != mTopicInfo) {
            mMoreBtn.setVisibility(View.VISIBLE);
            mTopicNameView.setVisibility(View.VISIBLE);
            mTopicCount.setVisibility(View.VISIBLE);
            mTopicCount.setText(String.format(mUserHasPostThisTopicText, topicInfo.postsCount));

            if (!TextUtils.isEmpty(topicInfo.topicName)) {
                mTopicNameView.setText(topicInfo.topicName);
            }

            BannerUrlLoader.getInstance().loadBannerUrl(mBannerDraweeView, topicInfo.bannerUrl);
            if (null != topicInfo.topicDetailInfo && !TextUtils.isEmpty(topicInfo.topicDetailInfo)) {
                mTopicDetilIntroduce.setVisibility(View.VISIBLE);
                showTopicDeatilInfo(topicInfo);

            } else {
                mTopicDetilIntroduce.setVisibility(View.GONE);
            }
            createTopPostView(topicInfo);

        } else {
            mTopicNameView.setVisibility(View.GONE);
            mTopicCount.setVisibility(View.GONE);
            mMoreBtn.setVisibility(View.GONE);
        }

        if (null != userList) {
            mTopicUserHeader.setOnClickListener(this);
            mTopicUserLayout.setOnClickListener(this);
            mTopicUserLayout.setVisibility(View.VISIBLE);
            mTopicUserHeader.removeAllViews();

            List<User> userHeaderList = new ArrayList<>();
            if (userList.size() > 3) {
                userHeaderList = userList.subList(0, 3);
            } else {
                userHeaderList = userList;
            }
            for (int i = 0; i < userHeaderList.size(); i++) {
                View view = mInflater.inflate(R.layout.topic_landing_user_item, null, false);
                VipAvatar simpleDraweeView = view.findViewById(R.id.topic_landing_user_icon);
                VipUtil.set(simpleDraweeView, userHeaderList.get(i).getHeadUrl(), userHeaderList.get(i).getVip());

                if (userHeaderList.size() > 1 && i == 0) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, -16, 0);
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    view.setLayoutParams(layoutParams);
                }

                if (userHeaderList.size() > 2 && i == 1) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, -16, 0);
                    layoutParams.gravity = Gravity.CENTER_VERTICAL;
                    view.setLayoutParams(layoutParams);
                }

                mTopicUserHeader.addView(view);
            }
        } else {
            mTopicUserLayout.setVisibility(View.GONE);
        }
    }

    private void showTopicDeatilInfo(TopicInfo topicInfo) {

        mTopicDetilIntroduce.getRichProcessor().setRichContent(topicInfo.topicDetailInfo, null);

    }

    private void createTopPostView(TopicInfo topicinfo) {
        PostBase post = null;
        if (null != topicinfo.topPost) {
            mAdapterObserver = new TopicdapterObserver(this);
            post = PostsDataSourceParser.parsePostBase(topicinfo.topPost);
        } else {
            return;
        }

        try {
            mTopicTopRecylerViewAdapter.unregisterAdapterDataObserver(mAdapterObserver);
        } catch (Exception e) {
            // do nothing
        }
        mTopicTopRecylerViewAdapter = new TopicTopRecylerViewAdapter(null);
        mTopicTopFeedContainer.removeAllViews();
        mTopicTopRecylerViewAdapter.removeAll();
        mTopicTopRecylerViewAdapter.addFeed(post);
        try {
            mTopicTopRecylerViewAdapter.registerAdapterDataObserver(mAdapterObserver);
        } catch (Exception e) {
            // do nothing
        }
        if (mFeedViewHolder == null || mTopicTopRecylerViewAdapter.getItemViewType(0) != mFeedViewType) {
            mFeedViewType = mTopicTopRecylerViewAdapter.getItemViewType(0);
            mFeedViewHolder = (BaseFeedViewHolder) mTopicTopRecylerViewAdapter.onCreateItemViewHolder(mTopicTopFeedContainer, mFeedViewType);
            mTopicTopRecylerViewAdapter.onBindItemViewHolder(mFeedViewHolder, 0);
            if (isBrowse) mFeedViewHolder.setBrowseClickListener(new IBrowseClickListener() {
                @Override
                public void onBrowseClick(int tye, boolean isShowLogin, int showType) {
                    onBrowseTopicClick(tye, isShowLogin, 0);
                }
            });
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mFeedViewHolder.itemView.getLayoutParams();
        marginLayoutParams.topMargin = 0;
        mFeedViewHolder.itemView.setLayoutParams(marginLayoutParams);
        mTopicTopFeedContainer.addView(mFeedViewHolder.itemView);

    }


    @Override
    public void showOfflineView() {
        mAppBarLayout.removeOnOffsetChangedListener(this);
        mRefreshLayout.setEnableRefresh(false);
        mSendPostBtn.setVisibility(View.INVISIBLE);
        fragmentPages.get(mViewPager.getCurrentItem()).showOfflineView();
    }

    @Override
    public void autoRefresh() {
//        mAppBarLayout.setExpanded(true);
//        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void showChangeTab() {
        if (xTabLayout.isShown()) return;
        xTabLayout.setVisibility(View.VISIBLE);
        mViewPager.setEnableScroll(true);
        selectHotTab();

    }

    @Override
    public void hideChangeTab() {
        xTabLayout.setVisibility(View.GONE);

        fragmentPages.get(0).setCurrentSelectTab(TopicLandingActivity.TAB_LATEST);

//        topicPageAdapter.initFragments(fragmentPages);


    }

    @Override
    public void onFeedFollowChange() {
        if (mFeedViewHolder != null) {
            mTopicTopRecylerViewAdapter.onBindItemViewHolder(mFeedViewHolder, 0);
        }
    }


    @Override
    public void setRefreshCount(int size) {
    }


    @Override
    public void setPresenter(ITopicLandingContract.ITopicLandingPresenter locationMixPresenter) {
        mPresenter = locationMixPresenter;
    }


    private void selectHotTab() {
        mPresenter.changTab(TopicLandingActivity.TAB_HOT);
        mViewPager.setCurrentItem(0, true);
        if (mTopicTopRecylerViewAdapter != null) {
            mTopicTopRecylerViewAdapter.setFeedSource(EventConstants.FEED_PAGE_TOPIC_HOT);
        }
        if (mViewTimeDelegate != null) {
            mViewTimeDelegate.setSource(EventConstants.FEED_PAGE_TOPIC_HOT, EventConstants.FEED_SOURCE_TOPIC_HOT);
        }
    }

    private void selectLatestTab() {
        mPresenter.changTab(TopicLandingActivity.TAB_LATEST);
        mViewPager.setCurrentItem(1, true);

        if (mTopicTopRecylerViewAdapter != null) {
            mTopicTopRecylerViewAdapter.setFeedSource(EventConstants.FEED_PAGE_TOPIC_HOT);
        }
        if (mViewTimeDelegate != null) {
            mViewTimeDelegate.setSource(EventConstants.FEED_PAGE_TOPIC_LATEST, EventConstants.FEED_SOURCE_TOPIC_LATEST);
        }
    }

    @Override
    public void showLoading() {
        mSendPostBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorView() {
        mSendPostBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyView() {
//        mSendPostBtn.setVisibility(View.INVISIBLE);
        mSendPostBtn.setVisibility(View.VISIBLE);

    }

    @Override
    public void showContentView() {
        mSendPostBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void goPublish() {
        mPresenter.goPublish();
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            mPresenter.onBackPressed();
        } else if (v == mTopicUserHeader) {
            if (isBrowse) {
                BrowseSchemeManager.getInstance().setTopicLanding(mTopicInfo.tid, mTopicInfo.topicName);
                onBrowseTopicClick(BrowseConstant.TYPE_UNKOWN, true, 0);
                return;
            }
            mPresenter.goPasserByPage();
        } else if (v == mTopicUserLayout) {
            if (isBrowse) {
                BrowseSchemeManager.getInstance().setTopicLanding(mTopicInfo.tid, mTopicInfo.topicName);
                onBrowseTopicClick(BrowseConstant.TYPE_UNKOWN, true, 0);
                return;
            }
            mPresenter.goPasserByPage();
        } else if (v == mSendPostBtn) {
            mPresenter.goPublish();
        } else if (v == mMoreBtn) {
            if (mTopicInfo == null) {
                return;
            }
            if (isBrowse) {
                if (!TextUtils.isEmpty(mTopicInfo.tid) && (!TextUtils.isEmpty(mTopicInfo.topicName)))
                    BrowseSchemeManager.getInstance().setTopicLanding(mTopicInfo.tid, mTopicInfo.topicName);
                onBrowseTopicClick(BrowseConstant.TYPE_UNKOWN, true, 0);

                HalfLoginManager.getInstancce().showLoginDialog(v.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));

                return;
            }
            doShare();
        /*    MenuItem shareItem = new MenuItem(MenuItemIdConstant.MENU_ITEM_SHARE
                    , ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_share")
                    , v.getResources().getColor(R.color.common_menu_share_text_color));
            List<MenuItem> list = new ArrayList<>();
            list.add(shareItem);
            mMenuDialog = SimpleMenuDialog.show(v.getContext(), list, new OnMenuItemClickListener() {
                @Override
                public void onMenuClick(MenuItem menuItem) {

                    if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_SHARE) {
                        doShare();
                    }
                }
            });*/
        }
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset >= 0) {
            mRefreshLayout.setEnableRefresh(true);
            mRefreshLayout.setEnableOverScrollDrag(false);
        } else {
            mRefreshLayout.setEnableRefresh(false);
            mRefreshLayout.setEnableOverScrollDrag(false);
        }

//        if (mTabViewGroup.isShown())

        if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            divider.setVisibility(View.INVISIBLE);
            mTablayoutShadowView.setVisibility(View.VISIBLE);
        } else {
            divider.setVisibility(View.VISIBLE);
            mTablayoutShadowView.setVisibility(View.GONE);
        }

    }

    private void doShare() {
       /* TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder()
                .setCategory(TrackerConstant.EVENT_EV_CT)
                .setAction(TrackerConstant.EVENT_SHARE_SOURCE)
                .setLabel("topic").build());*/
        if (null != mTopicInfo) {
            ShareModel shareModel = new ShareModel();
            shareModel.setShareModelType(ShareModel.SHARE_MODEL_TYPE_TOPIC);
            shareModel.setImageUrl(ShareHelper.getShareTopicImage());
            ShareManagerWrapper.Builder builder = new ShareManagerWrapper.Builder();
            builder.with(mBackBtn.getContext())
                    .login(AccountManager.getInstance().isLoginComplete())
                    .shareModel(shareModel)
                    .eventModel(new EventModel(EventLog1.Share.ContentType.TOPIC, null, null, EventLog1.Share.ShareFrom.OTHER, EventLog1.Share.PopPage.TOPIC, null, "", null, null,
                            null, null, null, null, null, null, null, null))
                    .shortLinkModel(new ShortLinkModel(ShareModel.SHARE_MODEL_TYPE_TOPIC, mTopicInfo.tid))
                    .shareTitleModel(new ShareTitleModel(ShareModel.SHARE_MODEL_TYPE_TOPIC, mTopicInfo.topicName))
                    .waterMarkModel(new WaterMarkModel("", "", mTopicInfo.topicName))
                    .build().share();
            NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_TOPIC);
        }
    }

    public static class TopicdapterObserver extends RecyclerView.AdapterDataObserver {
        private final WeakReference<ITopicLandingContract.ITopicLandingView> mWeakRef;

        public TopicdapterObserver(ITopicLandingContract.ITopicLandingView hFeedRecyclerViewAdapter) {
            mWeakRef = new WeakReference<>(hFeedRecyclerViewAdapter);
        }

        @Override
        public void onChanged() {
            super.onChanged();
            ITopicLandingContract.ITopicLandingView ref = mWeakRef.get();
            if (ref != null) {
                ref.onFeedFollowChange();
            }
        }

    }

    @Override
    public void onBrowseTopicClick(int tye, boolean isShow, int showType) {

//        if (isShow) {
//
//            if (BrowseManager.getLoginB()) {
//                RegisterLoginMethodDialog.show(mViewPager.getContext());
//                return;
//            }
//
//
//            String info;
//            switch (showType) {
//
//                case BrowseConstant.TYPE_SHOW_CONTENT_FOLLOW0:
//                    info = ResourceTool.getString("common_continue_follow_by_login0");
//                    break;
//                case BrowseConstant.TYPE_SHOW_CONTENT_FOLLOEW1:
//                    info = ResourceTool.getString("common_continue_by_login");
//                    break;
//                case BrowseConstant.TYPE_SHOW_CONTENT_LIKE:
//                    info = ResourceTool.getString("common_continue_like_by_login");
//                    break;
//
//                case BrowseConstant.TYPE_SHOW_CONTENT_DEFAULT:
//                default:
//                    info = ResourceTool.getString("common_continue_by_login");
//            }
//
//            StartEventManager.getInstance().setActionType(tye > 5 ? 6 : tye);
//            StartEventManager.getInstance().setFrom_page(4);
//            EventLog.UnLogin.report14(StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
//            ActionSnackBar.getInstance().showLoginSnackBar(mViewPager,
//                    info,
//                    ResourceTool.getString("common_login"), 3000, new ActionSnackBar.ActionBtnClickListener() {
//                        @Override
//                        public void onActionClick(View view) {
//                            // TODO: 2018/7/11
//                            if (BrowseManager.getLoginA())
//                                RegisterLoginMethodDialog.show(view.getContext());
//                            else
//                                RegistActivity.show(view.getContext());
//
//                        }
//                    });
//        }
    }


}
