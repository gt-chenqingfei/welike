package com.redefine.welike.business.feeds.discovery.ui.page;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.IPageStackManager;
import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.bean.Interest;
import com.redefine.welike.business.browse.management.bean.InterestPost;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.InterestSelectListener;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionType;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.common.mission.TipType;
import com.redefine.welike.business.feeds.discovery.ui.fragment.FeedFragment;
import com.redefine.welike.business.feeds.discovery.ui.vm.DiscoverListViewModel;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.feeds.ui.adapter.HotFeedRecyclerAdapter;
import com.redefine.welike.business.feeds.ui.listener.GuideListener;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.feeds.ui.listener.OnRequestPermissionCallback;
import com.redefine.welike.business.user.management.UserModelFactory;
import com.redefine.welike.business.user.management.bean.FollowPost;
import com.redefine.welike.business.user.management.bean.RecommendSlideUser;
import com.redefine.welike.business.user.management.bean.RecommendUser;
import com.redefine.welike.common.FragmentVisibleDetector;
import com.redefine.welike.common.ScoreManager;
import com.redefine.welike.common.abtest.ABKeys;
import com.redefine.welike.common.abtest.ABTest;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.commonui.widget.ArrowTextView;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.GPScoreEventManager;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by daining on 2018/6/14.
 **/
public class NewFeedFragment extends Fragment implements ILoadMoreDelegate, OnClickRetryListener, OnPostButtonClickListener, ErrorView.IErrorViewClickListener, OnRequestPermissionCallback, EasyPermissions.PermissionCallbacks {
    public static final String FOR_YOU = "FOR_YOU";
    public static final String LATEST = "LATEST";

    private String interestId = FOR_YOU;

    private RecyclerView mRecyclerView;
    private HotFeedRecyclerAdapter mAdapter;
    private IPageStackManager mPageStackManager;

    private ErrorView mErrorView;
    private EmptyView mEmptyView;
    private View mLoadingView;

    private DiscoverListViewModel viewModel;
    private Listener listener;
    private String pageSource;

    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());

    private final FragmentVisibleDetector mFragmentVisibleDetector = new FragmentVisibleDetector(new FragmentVisibleDetector.AutoPlayVideoListener() {

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
            if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
                ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                mVideoItemVisibilityCalculator.resetCurrentPlayItem();
            }
        }
    });
    private final VideoItemVisibilityCalculator mVideoItemVisibilityCalculator = new VideoItemVisibilityCalculator(new VideoItemVisibilityCalculator.VideoPlayStateCallback() {
        @Override
        public void stopPlayback() {
            if (mAdapter != null) {
                mAdapter.destroyVideo();
                mVideoItemVisibilityCalculator.resetCurrentPlayItem();
            }
        }

        @Override
        public void startNewPlay(int position, ApolloVideoView videoPlayerView) {
            if (mAdapter != null) {
                mAdapter.playVideo(position, videoPlayerView);
            }
        }
    });

    public static NewFeedFragment create(String interest, Listener listener, IPageStackManager pageStackManager) {
        Bundle bundle = new Bundle();
        bundle.putString("interest_id", interest);
        NewFeedFragment fragment = new NewFeedFragment();
        fragment.setArguments(bundle);
        fragment.listener = listener;
        fragment.mPageStackManager = pageStackManager;
        return fragment;
    }

    public static NewFeedFragment create(String interest, IPageStackManager pageStackManager) {
        Bundle bundle = new Bundle();
        bundle.putString("interest_id", interest);
        NewFeedFragment fragment = new NewFeedFragment();
        fragment.setArguments(bundle);
        fragment.mPageStackManager = pageStackManager;
        return fragment;
    }

//    public void setListener(Listener listener, IPageStackManager pageStackManager) {
//        this.listener = listener;
//        mPageStackManager = pageStackManager;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DiscoverListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            interestId = getArguments().getString("interest_id", FOR_YOU);
        }
        View view = inflater.inflate(R.layout.discover_sub_page, container, false);
        mErrorView = view.findViewById(R.id.discover_error_view);
        mLoadingView = view.findViewById(R.id.discover_loading_view);
        mEmptyView = view.findViewById(R.id.discover_empty_view);
//        mViewContainer = getView().findViewById(R.id.discover_feeds_container);
        mErrorView.setOnErrorViewClickListener(this);
        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_discover_empty_refresh"), new EmptyView.IEmptyBtnClickListener() {
            @Override
            public void onClickEmptyBtn() {
                if (listener != null) {
                    listener.onRefresh();
                }
                PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
            }
        });

        mErrorView.setClickable(false);
        mEmptyView.setClickable(false);
        mLoadingView.setClickable(false);

        mRecyclerView = view.findViewById(R.id.feed_posts_recycler_view);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
//        mRecyclerView.addOnScrollListener(mVideoItemVisibilityCalculator);

        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mFragmentVisibleDetector.attach();
                mPostViewTimeManager.onAttach();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mFragmentVisibleDetector.detach();
                mPostViewTimeManager.onDetach();
            }
        });
//        LinearLayoutManager layoutManager = ;
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
        if (TextUtils.equals(interestId, LATEST)) {
            mAdapter = new HotFeedRecyclerAdapter(mPageStackManager, EventConstants.FEED_PAGE_DISCOVER_LATEST);
            mAdapter.setFirstTopMargin(0);
            pageSource = EventConstants.FEED_SOURCE_DISCOVER_LATEST;
        } else if (TextUtils.equals(interestId, FOR_YOU)) {
            mAdapter = new HotFeedRecyclerAdapter(mPageStackManager, EventConstants.FEED_PAGE_DISCOVER_FOR_YOU);
            pageSource = EventConstants.FEED_SOURCE_DISCOVER_FOR_YOU;
        } else if (TextUtils.equals(interestId, BrowseConstant.INTEREST_VIDEO)) {
            mAdapter = new HotFeedRecyclerAdapter(mPageStackManager, EventConstants.FEED_PAGE_DISCOVER_VIDEO);
            pageSource = EventConstants.FEED_SOURCE_DISCOVER_VIDEO;
        } else {
            mAdapter = new HotFeedRecyclerAdapter(mPageStackManager, EventConstants.FEED_PAGE_DISCOVER + interestId);
            pageSource = EventConstants.FEED_SOURCE_DISCOVER + interestId;
        }
        initAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mPostViewTimeManager.attach(mRecyclerView, mAdapter, pageSource);
        changeViewWithData();
        viewModel.init(interestId);
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
        return view;
    }

//    @NonNull
//    protected View createView(ViewGroup container, Bundle saveState) {
//        Log.w("DDAI", "NewFeedPage (" + interestId + ") createView");
//
//        return mPageStackManager.getLayoutInflater().inflate(R.layout.discover_sub_page, null);
//    }

//    @Override
//    public void onPageStateChanged(int oldPageState, int pageState) {
//        super.onPageStateChanged(oldPageState, pageState);
//        Log.w("DDAI", "NewFeedPage (" + interestId + "). onPageStateChanged = " + oldPageState + " >> pageState=" + pageState);
//        if (pageState == PAGE_STATE_SHOW) {
//        }
//    }

    public void scrollToTop() {
        if (null != mRecyclerView) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    public void refresh() {
        if (viewModel != null) {
            viewModel.refresh();
        }
    }

//    public void onShow() {
//        if (viewModel != null)
//            viewModel.refresh();
//    }

    @Override
    public void onResume() {
        super.onResume();
        mFragmentVisibleDetector.onResume();
        mPostViewTimeManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFragmentVisibleDetector.onPause();
        mPostViewTimeManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
        mVideoItemVisibilityCalculator.resetCurrentPlayItem();
        mPostViewTimeManager.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mFragmentVisibleDetector.show();
            mPostViewTimeManager.onShow();
        } else {
            mFragmentVisibleDetector.hide();
            mPostViewTimeManager.onHide();
        }
    }

    private void changeViewWithData() {
        viewModel.getData().observe(this, new Observer<ArrayList<PostBase>>() {
            @Override
            public void onChanged(@Nullable ArrayList<PostBase> postBases) {
                mAdapter.addNewData(postBases);

                mFragmentVisibleDetector.onDataLoaded();

                mPostViewTimeManager.setData(postBases, mAdapter.hasHeader());

                if (null != postBases && postBases.size() > 0) {

                    ScoreManager.setRefreshCount(getContext());

                    if (ScoreManager.canShowScoreView(getContext())) {
                        GPScoreEventManager.INSTANCE.setAction_type(EventConstants.GP_ACTION_TYPE_REFRESH);
                        mAdapter.addScoreData(0);
                    }

                    showContentView();
                } else {
                    showEmptyView();
                }
                mPostViewTimeManager.onDataLoaded();
            }
        });

        viewModel.getHisData().observe(this, new Observer<ArrayList<PostBase>>() {
            @Override
            public void onChanged(@Nullable ArrayList<PostBase> postBases) {


                int count = mAdapter.getRealItemCount();

                mAdapter.addHisData(postBases);

                mPostViewTimeManager.updateData(postBases);

                if (null != postBases && postBases.size() > 0) {
                    ScoreManager.setRefreshCount(getContext());
                    if (ScoreManager.canShowScoreView(getContext())) {
                        mAdapter.addScoreData(count);
                    }
                    showContentView();
                } else {
                    showEmptyView();
                }
            }
        });

        viewModel.getRecommendData().observe(this, new Observer<List<RecommendSlideUser>>() {
            @Override
            public void onChanged(@Nullable List<RecommendSlideUser> list) {
                if (list == null) {
                    return;
                }
                if (!EasyPermissions.hasPermissions(mRecyclerView.getContext(), Manifest.permission.READ_CONTACTS)) {
                    list.add(0, UserModelFactory.Companion.createRecommendContactUser(mRecyclerView.getContext()));
                }

                FollowPost followPost = new FollowPost();
                followPost.setList(list);
                mAdapter.setPermissionCallback(NewFeedFragment.this);
                mAdapter.addFollowData4U(followPost);
                InterestAndRecommendCardEventManager.INSTANCE.report6();
            }
        });

        viewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) {
                    return;
                }


                switch (pageStatusEnum) {
                    case EMPTY:
                        if (listener != null) {
                            listener.finishRefresh(0);
                        }
//                        parentViewModel.finishRefresh(0);
                        if (mAdapter.getRealItemCount() == 0) {
                            showEmptyView();
                        } else {
                            showContentView();
                        }
                        break;
                    case ERROR:
                        if (listener != null) {
                            listener.finishRefresh(-1);
                        }
//                        parentViewModel.finishRefresh(-1);
                        if (mAdapter.getRealItemCount() == 0) {
                            showErrorView();
                        } else {
                            showContentView();
                        }
                        break;
                    case CONTENT:
                        if (listener != null) {
                            listener.finishRefresh(1);
                        }
                        if (mAdapter.getRealItemCount() == 0) {
                            showEmptyView();
                        } else {
                            showContentView();
                        }
                        break;
                    case LOADING:
                        if (mAdapter.getRealItemCount() == 0)
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
                mAdapter.addInterestData(interestPost);
                InterestAndRecommendCardEventManager.INSTANCE.setFrom_page(EventConstants.INTEREST_CARD_FROM_PAGE_DISCOVER);
                InterestAndRecommendCardEventManager.INSTANCE.report1();
            }
        });

    }

//    @Override
//    protected void initView(ViewGroup container, Bundle saveState) {
//        Log.w("DDAI", "NewFeedPage (" + interestId + ") initView");
//        mErrorView = getView().findViewById(R.id.discover_error_view);
//        mLoadingView = getView().findViewById(R.id.discover_loading_view);
//        mEmptyView = getView().findViewById(R.id.discover_empty_view);
////        mViewContainer = getView().findViewById(R.id.discover_feeds_container);
//        mErrorView.setOnErrorViewClickListener(this);
//        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "feed_discover_empty_refresh"), new EmptyView.IEmptyBtnClickListener() {
//            @Override
//            public void onClickEmptyBtn() {
//                if (listener != null) {
//                    listener.onRefresh();
//                }
//                PostEventManager.INSTANCE.setAction(4);
//            }
//        });
//
//        mErrorView.setClickable(false);
//        mEmptyView.setClickable(false);
//        mLoadingView.setClickable(false);
//
//        mRecyclerView = getView().findViewById(R.id.feed_posts_recycler_view);
//        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
////        LinearLayoutManager layoutManager = ;
////        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getView().getContext()));
////        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
//        mAdapter = new HotFeedRecyclerAdapter(mPageStackManager);
//        initAdapter();
//        mRecyclerView.setAdapter(mAdapter);
//        changeViewWithData();
//        viewModel.init(interestId);
//        viewModel.refresh();
//    }


    private void initAdapter() {
        mAdapter.setInterestId(interestId);
        mAdapter.setRetryLoadMoreListener(this);
        mAdapter.setOnPostButtonClickListener(this);
//        mAdapter.setFirstShownListener(new GuideListener<BaseRecyclerViewHolder>() {
//            ArrowTextView guideMore;
//            ArrowTextView guide;
//
//            @Override
//            public void onShow(BaseRecyclerViewHolder holder, ArrowTextView atv) {
//                if (MissionManager.INSTANCE.checkGuide(MissionType.SHARE_POST)) {
//                    View target = holder.itemView.findViewById(R.id.common_feed_arrow_btn);
//                    guideMore = holder.itemView.findViewById(R.id.guide_follow);
//                    MissionManager.INSTANCE.showGuide(MissionType.SHARE_POST, guideMore, target);
//                }
//                if (MissionManager.INSTANCE.checkGuide(MissionType.FORWARD)) {
//                    View target = holder.itemView.findViewById(R.id.common_feed_forward_tab);
//                    guide = holder.itemView.findViewById(R.id.guide);
//                    MissionManager.INSTANCE.showGuide(MissionType.FORWARD, guide, target);
//                }
//
//            }
//
//            @Override
//            public void onClick(View view) {
//                if (view != null && view.getId() == R.id.common_feed_arrow_btn) {
//                    if (guideMore != null && guideMore.getVisibility() == View.VISIBLE) {
//                        guideMore.setVisibility(View.GONE);
//                        MissionManager.INSTANCE.hideGuide(MissionType.SHARE_POST);
//                    }
//                }
//                if (view != null && view.getId() == R.id.common_feed_forward_tab) {
//                    if (guide != null && guide.getVisibility() == View.VISIBLE) {
//                        guide.setVisibility(View.GONE);
//                        MissionManager.INSTANCE.hideGuide(MissionType.FORWARD);
//                    }
//                }
//            }
//        });
        mAdapter.setFirstShareGuide(new GuideListener<BaseRecyclerViewHolder>() {
            ArrowTextView guide;

            @Override
            public void onShow(BaseRecyclerViewHolder holder, ArrowTextView _guide) {
                //SET super like listener
                if (MissionManager.INSTANCE.checkTip(TipType.DISCOVERY_SHARE)) {
                    View target = holder.itemView.findViewById(R.id.common_feed_share_tab);
//                    guide = holder.itemView.findViewById(R.id.guide);
//                    MissionManager.INSTANCE.showTip(TipType.DISCOVERY_SHARE, guide, target);
                }
            }

            @Override
            public void onClick(View view) {
                if (guide != null && guide.getVisibility() == View.VISIBLE) {
                    guide.setVisibility(View.GONE);
                }
            }
        });
//        mAdapter.setHotGuide(new GuideListener<View>() {
//            @Override
//            public void onShow(View view, ArrowTextView guide) {
////                listener.onShowHot();
//            }
//
//            @Override
//            public void onClick(View view) {
//                listener.onClickHot();
//            }
//        });

        mAdapter.setInterestSelectListener(new InterestSelectListener() {
            @Override
            public void onSelectInterest(@NotNull List<? extends Interest> list) {
                if (list.size() < 1) {
                    ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_interest_info_title", false));
                    return;
                }
                viewModel.insertInterest2DB(list);
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    //    private GuideListener<View> hotGuideListener;

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        viewModel.loadMore();
        mAdapter.onLoadMore();
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onCommentClick(PostBase postBase) {
        MissionManager.INSTANCE.notifyEvent(MissionType.COMMENT);
        EventLog.Feed.report7(interestId.equals(LATEST) ? 3 : 2, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onLikeClick(PostBase postBase, int exp) {
        EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                postBase == null ? 0 : postBase.getSuperLikeExp(),
                exp, interestId.equals(LATEST) ? 3 : 2, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onForwardClick(PostBase postBase) {
        EventLog.Feed.report6(interestId.equals(LATEST) ? 3 : 2, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase),
                postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
    }

    @Override
    public void onFollowClick(PostBase postBase) {

    }

    @Override
    public void onShareClick(PostBase postBase, int shareType) {
        ShareEventManager.INSTANCE.setShareType(shareType);
        ShareEventManager.INSTANCE.setFrompage(EventConstants.SHARE_FROM_PAGE_DISCOVER + interestId);
        ShareEventManager.INSTANCE.report3();

        if (ScoreManager.canShowScoreView(getContext())) {
            GPScoreEventManager.INSTANCE.setAction_type(EventConstants.GP_ACTION_TYPE_SHARE);
            mAdapter.addScoreData(postBase.getPosition() + 1);
        }

    }

    @Override
    public void onPostBodyClick(PostBase postBase) {
        ShareEventManager.INSTANCE.setChannel_page(EventConstants.SHARE_CHANNEL_PAGE_DISCOVER);
    }

    @Override
    public void onPostAreaClick(PostBase postBase, int clickArea) {

        if (postBase == null) return;

        EventLog.Feed.report8(pageSource,
                postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
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
//        if (hotGuideListener != null) {
//            hotGuideListener.onShow(null, null);
//        }
    }

    @Override
    public void onErrorViewClick() {
        PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
        if (listener != null) {
            listener.onRefresh();
        }
    }

    public interface Listener {
        void onRefresh();

        void finishRefresh(int value);

        void onClickHot();

//        void onShowHot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onRequestPermission(@NotNull String message, int requestCode, @NotNull String permission) {
        EasyPermissions.requestPermissions(this, message, requestCode, permission);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("from", EventLog1.AddFriend.ButtonFrom.OTHER);
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CONTACT, bundle));
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //do nothing
    }
}
