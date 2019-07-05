package com.redefine.welike.business.browse.ui.page;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pekingese.pagestack.framework.cache.PageCache;
import com.pekingese.pagestack.framework.config.PageConfig;
import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.StickHeaderItemDecoration;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.browse.management.bean.InterestPost;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.adapter.BrowseFeedLatestRecyclerViewAdapter;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.browse.ui.viewmodel.BrowseDiscoverCViewModel;
import com.redefine.welike.business.browse.ui.viewmodel.DiscoverType;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.discovery.ui.DiscoverGuideDialog;
import com.redefine.welike.business.feeds.discovery.ui.adapter.DiscoverLatestPageAdapter;
import com.redefine.welike.business.feeds.management.bean.DiscoverHeader;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.feeds.ui.viewholder.DiscoverStickyHeaderHolder;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.constant.LanguageConstant;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.common.PageVisibleDetector;
import com.redefine.welike.common.ScoreManager;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.commonui.widget.SwitchViewPager;
import com.redefine.welike.event.RouteDispatcher;
import com.redefine.welike.frameworkmvvm.BaseLifecycleFragmentPage;
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager;
import com.redefine.welike.frameworkmvvm.ViewModelProviders;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.GPScoreEventManager;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.SearchEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

@PageName("BrowseDiscoverCPage")
public class BrowseDiscoverCPage extends BaseLifecycleFragmentPage implements ILoadMoreDelegate, IBrowseClickListener, OnClickRetryListener, OnPostButtonClickListener {

    // zone : search view
    private View searchView, searchViewIn;//TODO
    private TextView searchTitle, searchTitleIn;

    private SmartRefreshLayout mRefreshLayout;
    private View refreshHeader;
    private AppBarLayout mAppBarLayout;
    private com.youth.banner.Banner mBannerView;
    private View defaultBanner;

    //View Model
    private BrowseDiscoverCViewModel viewModel;

    // 排行榜的顶部的布局
    private ViewGroup mSearchUserTopViewGroup;
    private LottieAnimationView mSearchUserTopImageView;

    private ViewGroup mSearchUserTopViewGroupOut;
    private LottieAnimationView mSearchUserTopImageViewOut;

    private ArrayList<Banner> bannerMap = new ArrayList<>();

    //setting recycler view
    private RecyclerView mRecyclerView;
    private BrowseFeedLatestRecyclerViewAdapter mAdapter;

    private ErrorView mErrorView;
    private EmptyView mEmptyView;
    private View mLoadingView;


    private String interestId = BrowseConstant.INTEREST_LATEST;
    private String pageSource;

    private boolean mShown;
    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());

    private final PageVisibleDetector pageVisibleDetector = new PageVisibleDetector(new PageVisibleDetector.AutoPlayVideoListener() {
        @Override
        public void autoPlay() {
            if (mRecyclerView == null || mVideoItemVisibilityCalculator == null) {
                return;
            }
            mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
        }

        @Override
        public void stopPlay() {
            if (mRecyclerView == null || mVideoItemVisibilityCalculator == null) {
                return;
            }
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                mVideoItemVisibilityCalculator.resetCurrentPlayItem();
            }
        }
    });

    private final VideoItemVisibilityCalculator mVideoItemVisibilityCalculator = new VideoItemVisibilityCalculator(new VideoItemVisibilityCalculator.VideoPlayStateCallback() {
        @Override
        public void stopPlayback() {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                mVideoItemVisibilityCalculator.resetCurrentPlayItem();

            }
        }

        @Override
        public void startNewPlay(int position, ApolloVideoView videoPlayerView) {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).playVideo(position, videoPlayerView);
            }
        }
    });


    public BrowseDiscoverCPage(BaseLifecyclePageStackManager stackManager, PageConfig config, PageCache cache) {
        super(stackManager, config, cache);
        viewModel = ViewModelProviders.of(this).get(BrowseDiscoverCViewModel.class);
    }

    @NonNull
    @Override
    protected View createView(ViewGroup container, Bundle saveState) {
        return mPageStackManager.getLayoutInflater().inflate(R.layout.page_discover_latest, null);
    }

    View magicHeader;

    @Override
    protected void initView(ViewGroup container, Bundle saveState) {
        mRefreshLayout = getView().findViewById(R.id.main_discover_refresh_layout);
        mRefreshLayout.setHeaderInsetStart(48F);
        mAppBarLayout = getView().findViewById(R.id.discover_app_bar);
        magicHeader = getView().findViewById(R.id.item_discover_header);
        refreshHeader = getView().findViewById(R.id.main_discover_refresh_header);
        searchView = getView().findViewById(R.id.discovery_search_layout);
        searchViewIn = getView().findViewById(R.id.discovery_search_layout_in);
        searchTitle = getView().findViewById(R.id.discover_search_title);
        searchTitleIn = getView().findViewById(R.id.discover_search_title_in);

        mBannerView = getView().findViewById(R.id.hot_banner_view);
        defaultBanner = getView().findViewById(R.id.default_discovery_banner);

        mSearchUserTopViewGroup = getView().findViewById(R.id.discover_search_title_top_layout_in);
        mSearchUserTopImageView = getView().findViewById(R.id.discover_search_title_top_image_in);

        mSearchUserTopViewGroupOut = getView().findViewById(R.id.discover_search_title_top_layout_out);
        mSearchUserTopImageViewOut = getView().findViewById(R.id.discover_search_title_top_image_out);

        mErrorView = getView().findViewById(R.id.discover_error_view);
        mLoadingView = getView().findViewById(R.id.discover_loading_view);
        mEmptyView = getView().findViewById(R.id.discover_empty_view);
        mRecyclerView = getView().findViewById(R.id.feed_posts_recycler_view);

        //set view.
        mErrorView.setOnErrorViewClickListener(new BaseErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
                viewModel.refresh();
            }
        });
        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString("feed_discover_empty_refresh"), new EmptyView.IEmptyBtnClickListener() {
            @Override
            public void onClickEmptyBtn() {

                viewModel.refresh();
                PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
            }
        });
        mErrorView.setClickable(false);
        mEmptyView.setClickable(false);
        mLoadingView.setClickable(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getView().getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
//        mRecyclerView.addOnScrollListener(mVideoItemVisibilityCalculator);
        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                pageVisibleDetector.attach();
                mPostViewTimeManager.onAttach();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                pageVisibleDetector.detach();
                mPostViewTimeManager.onDetach();
            }
        });

        mAdapter = new BrowseFeedLatestRecyclerViewAdapter(EventConstants.FEED_PAGE_UNLOGIN_LATEST);
        pageSource = EventConstants.FEED_PAGE_UNLOGIN_LATEST;
//        mAdapter.setMagicView((SimpleDraweeView) getView().findViewById(R.id.discover_magic_view));
        mAdapter.setBrowseClickListener(this);
        mAdapter.setRetryLoadMoreListener(this);
        mAdapter.setOnPostButtonClickListener(this);
//        mAdapter.setMagicHeader(magicHeader);
        mRecyclerView.setAdapter(mAdapter);

        mPostViewTimeManager.attach(mRecyclerView, mAdapter, pageSource);
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);

        setSearchBar();
        setAppBarLayout();
        setRankingTop();
        changeViewByData();
        PostEventManager.INSTANCE.reset();
        boolean showRising = viewModel.checkShowRising();
        if (!showRising) {
            mAdapter.showStickHeader(magicHeader,mRecyclerView);
            TextView textView = getView().findViewById(R.id.tv_latest_title);
            textView.setText(R.string.discover_welike_trends);
        }
        mAdapter.setHeaderClick(new Function1<PostBase, Unit>() {
            @Override
            public Unit invoke(PostBase postBase) {
                if (postBase instanceof DiscoverHeader) {
                    DiscoverHeader header = ((DiscoverHeader) postBase);
                    EventLog1.DiscoverTopic.report1(header.topicId);
                    //go to topic landing.
                    gotoTopicLanding(header);
                }
                return null;
            }
        });
        viewModel.setContentType(showRising ? DiscoverType.SkipRising : DiscoverType.SkipDiscover);
        viewModel.refresh();
    }

    private void gotoTopicLanding(DiscoverHeader header) {
        TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
        bean.name = header.topicName;
        bean.id = String.valueOf(header.topicId);
        bean.count = header.postCount;
        Bundle bundle = new Bundle();
        bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, bean);
        ARouter.getInstance().build(RouteConstant.PATH_TOPIC_LANDING).with(bundle).navigation();
    }


    private void setRankingTop() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = viewModel.getForwardUrl();
                if (!TextUtils.isEmpty(url)) {
                    new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_TOP_USER).onUrlRedirect(url);
                }
            }
        };
        mSearchUserTopViewGroup.setOnClickListener(listener);

        mSearchUserTopViewGroupOut.setOnClickListener(listener);

        mSearchUserTopImageView.setAnimation(LanguageConstant.getLanguageSrcWithLottie(LanguageSupportManager.getInstance().getCurrentMenuLanguageType()));
        mSearchUserTopImageView.playAnimation();
        mSearchUserTopImageViewOut.setAnimation(LanguageConstant.getLanguageSrcWithLottie(LanguageSupportManager.getInstance().getCurrentMenuLanguageType()));
        mSearchUserTopImageViewOut.playAnimation();

    }

    private void setSearchBar() {
        String hint = ResourceTool.getString("discover_search_default");
        View.OnClickListener searchClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SEARCH_SUG_EVENT, bundle));
                SearchEventManager.INSTANCE.report1();
                EventLog.UnLogin.report20(EventConstants.UNLOGIN_DISCOVERACTION_SEARCH);
            }
        };
        searchTitle.setHint(hint);
        searchTitle.setOnClickListener(searchClickListener);
        searchTitleIn.setHint(hint);
        searchTitleIn.setOnClickListener(searchClickListener);


    }

    private void setAppBarLayout() {
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mRecyclerView.scrollToPosition(0);
                PostEventManager.INSTANCE.setAction(EventConstants.PULL_DOWN_REFRESH);
                viewModel.refresh();
                EventLog.UnLogin.report8(interestId);
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                searchView.setVisibility(verticalOffset == 0 ? View.VISIBLE : View.INVISIBLE);
                searchViewIn.setVisibility(verticalOffset == 0 ? View.INVISIBLE : View.VISIBLE);
                refreshHeader.setVisibility(verticalOffset == 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void changeViewByData() {
        viewModel.getPosts().observe(this, new Observer<ArrayList<PostBase>>() {
            @Override
            public void onChanged(@Nullable ArrayList<PostBase> postBases) {
                mAdapter.resetHeader();
                mAdapter.addNewData(postBases);

            }
        });

        viewModel.getMorePosts().observe(this, new Observer<ArrayList<PostBase>>() {
            @Override
            public void onChanged(@Nullable ArrayList<PostBase> postBases) {
                mAdapter.addHisData(postBases);
            }
        });


        viewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) {
                    return;
                }

                mRefreshLayout.finishRefresh();

                switch (pageStatusEnum) {
                    case EMPTY:
                        if (mAdapter.getRealItemCount() == 0) {
                            showEmptyView();
                        } else {
                            showContentView();
                        }
                        break;
                    case ERROR:
                        if (mAdapter.getRealItemCount() == 0) {
                            showErrorView();
                        } else {
                            showContentView();
                        }

                        break;
                    case CONTENT:
                        if (mAdapter.getRealItemCount() == 0) {
                            showEmptyView();
                        } else {
                            showContentView();
                        }

                        break;
                    case LOADING:
                        showLoading();
                        break;
                }
            }
        });
        viewModel.getLoadMoreStatus().observe(this, new Observer<PageLoadMoreStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageLoadMoreStatusEnum pageLoadMoreStatusEnum) {
                if (pageLoadMoreStatusEnum == null) {
                    return;
                }
                switch (pageLoadMoreStatusEnum) {
                    case NONE:
                        mAdapter.clearFinishFlag();
                        break;
                    case FINISH:
                        mAdapter.finishLoadMore();
                        break;
                    case LOADING:
                        mAdapter.onLoadMore();
                        break;
                    case NO_MORE:
                        mAdapter.noMore();
                        break;
                    case LOAD_ERROR:
                        mAdapter.loadError();
                        break;
                }
            }
        });

        viewModel.getInterestPost().observe(this, new Observer<InterestPost>() {
            @Override
            public void onChanged(@Nullable InterestPost interestPost) {
                if (interestPost == null) return;
                InterestAndRecommendCardEventManager.INSTANCE.setFrom_page(EventConstants.INTEREST_CARD_FROM_PAGE_UNLOGIN);
                InterestAndRecommendCardEventManager.INSTANCE.report1();
                mAdapter.addInterestData(interestPost);

            }
        });

        viewModel.getBanners().observe(this, new Observer<List<Banner>>() {
            @Override
            public void onChanged(@Nullable List<Banner> banners) {
                initBanners(banners);
            }
        });

        viewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {

                if (pageStatusEnum == null) return;

                switch (pageStatusEnum) {
                    case EMPTY:
                    case ERROR:
                    case CONTENT:
                        mRefreshLayout.finishRefresh();
                        break;
                    case LOADING:
                        break;


                }
            }
        });

    }

    @Override
    public void attach(ViewGroup container) {
        super.attach(container);
//        if (mainAdapter != null)
//            mainAdapter.setFragmentVisible(true);
        setFragmentVisible(true);
        mPostViewTimeManager.onResume();
        viewModel.checkGuide(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                new DiscoverGuideDialog(getPageStackManager().getActivity()).show();
                return null;
            }
        });
    }

    @Override
    public void detach(ViewGroup container) {
        super.detach(container);
//        if (mainAdapter != null)
//            mainAdapter.setFragmentVisible(false);
        setFragmentVisible(false);
        mAdapter.destroy();
    }

    private void setFragmentVisible(boolean visible) {
        mShown = visible;
        if (visible) {
            pageVisibleDetector.show();
            mPostViewTimeManager.onShow();
        } else {
            pageVisibleDetector.hide();
            mPostViewTimeManager.onHide();
        }
    }

    @Override
    public void onPageStateChanged(int oldPageState, int pageState) {
        super.onPageStateChanged(oldPageState, pageState);
        if (pageState == BasePage.PAGE_STATE_SHOW) {
            pageVisibleDetector.pageShow();
        } else if (pageState == BasePage.PAGE_STATE_HIDE) {
            pageVisibleDetector.pageHide();
        }
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
        pageVisibleDetector.onResume();
        mPostViewTimeManager.onResume();
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
        pageVisibleDetector.onPause();
        mPostViewTimeManager.onPause();
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
    }

    private void initBanners(List<Banner> banners) {
        if (CollectionUtil.isEmpty(banners)) {
            mBannerView.setVisibility(View.INVISIBLE);
            defaultBanner.setVisibility(View.VISIBLE);
        } else {
            defaultBanner.setVisibility(View.GONE);
            mBannerView.setVisibility(View.VISIBLE);
            mBannerView.setDelayTime(5000);
//            guideListenerBanner.onShow(null, null);
            bannerMap.clear();
            ArrayList<String> bannerUrls = new ArrayList<>();
            for (Banner banner : banners) {
                bannerUrls.add(banner.getPhoto());
                bannerMap.add(banner);
            }

            mBannerView.setImages(bannerUrls)
                    .setImageLoader(new BrowseDiscoverCPage.MyImageLoader())
                    .setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            final Banner bean = bannerMap.get(position);
                            if (!TextUtils.isEmpty(bean.getAction())) {
//                                MissionManager.INSTANCE.notifyEvent(MissionType.BROWSE_OFFICIAL_ACTIVE);
//

                                String url = bean.getAction();
                                boolean validUri = RouteDispatcher.validUri(Uri.parse(url));
                                if (validUri) {
                                    url = url.replace(RouteConstant.MAIN_ROUTE_PATH, RouteConstant.AVOID_ROUTE_PATH);
                                }
                                new DefaultUrlRedirectHandler(mBannerView.getContext(), DefaultUrlRedirectHandler.FROM_BANNER).onUrlRedirect(url);
                            }
                        }
                    })
                    .start();
        }
    }

    @Override
    public void onCommentClick(PostBase postBase) {
        EventLog.Feed.report7(2, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        EventLog.UnLogin.report23(EventConstants.UNLOGIN_FROM_PAGE_HOME);
    }

    @Override
    public void onLikeClick(PostBase postBase, int exp) {
        EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                postBase == null ? 0 : postBase.getSuperLikeExp(),
                exp, 2, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
        EventLog.UnLogin.report22(EventConstants.UNLOGIN_FROM_PAGE_HOME, interestId);
    }

    @Override
    public void onForwardClick(PostBase postBase) {
        EventLog.Feed.report6(2, postBase == null ? null : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? null : postBase.getSequenceId());
    }

    @Override
    public void onFollowClick(PostBase postBase) {
        EventLog.UnLogin.report24(EventConstants.UNLOGIN_FROM_PAGE_HOME, postBase.getUid());
    }

    @Override
    public void onShareClick(PostBase postBase, int shareType) {
        ShareEventManager.INSTANCE.setFrompage(EventConstants.SHARE_FROM_PAGE_OTHER);
        ShareEventManager.INSTANCE.setShareType(shareType);
        ShareEventManager.INSTANCE.report3();
    }

    @Override
    public void onPostBodyClick(PostBase postBase) {
        ShareEventManager.INSTANCE.setChannel_page(EventConstants.SHARE_CHANNEL_PAGE_DISCOVER);
    }

    @Override
    public void onPostAreaClick(PostBase postBase, int clickArea) {

        if (postBase == null) return;

        EventLog.Feed.report8(pageSource, postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
    }

    public class MyImageLoader extends ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            if (path != null) {
                Uri uri = Uri.parse((String) path);
                imageView.setImageURI(uri);
            }
        }

        @Override
        public ImageView createImageView(Context context) {
            //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
            SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context);
            simpleDraweeView.setBackgroundColor(context.getResources().getColor(R.color.discover_search_hot_feed_title_divider));
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
            builder.setFadeDuration(300)
                    .setFailureImage(R.drawable.feed_nine_grid_img_error)
                    .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER)
                    .setPlaceholderImage(R.drawable.feed_nine_grid_img_default)
                    .setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER);
            simpleDraweeView.setHierarchy(builder.build());
            return simpleDraweeView;
        }
    }

    public void refresh() {
        mAppBarLayout.setExpanded(true, true);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        EventLog.UnLogin.report9(interestId);
        viewModel.onHisLoad();
        mAdapter.onLoadMore();
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
    }

    @Override
    public void onBrowseClick(int tye, boolean isShowLogin, int showType) {
        StartEventManager.getInstance().setActionType(tye > 5 ? 6 : tye);
        StartEventManager.getInstance().setFrom_page(1);
        if (tye == BrowseConstant.TYPE_SHARE) {
            EventLog.UnLogin.report16(StartEventManager.getInstance().getFrom_page(), interestId);
        }

    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showContentView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

}
