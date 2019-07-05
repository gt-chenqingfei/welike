package com.redefine.welike.business.user.ui.fragment;

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

import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.business.browse.ui.listener.IBrowseClickListener;
import com.redefine.welike.business.common.mission.OnPostButtonClickListener;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter;
import com.redefine.welike.business.startup.management.HalfLoginManager;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.user.ui.adapter.UserPostFeedAdapter;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.vm.ProfileLikeViewModel;
import com.redefine.welike.common.BrowseSchemeManager;
import com.redefine.welike.common.FragmentVisibleDetector;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.redefine.welike.statistical.manager.ShareEventManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

/**
 * Created by nianguowang on 2018/8/21
 */
public class ProfileLikeFragment extends Fragment implements ILoadMoreDelegate, OnRefreshListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;

    private UserPostFeedAdapter mAdapter;
    private String mUid;
    private boolean mIsBrowse;
    private ProfileLikeViewModel mPostViewModel;

    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createPostExposeCallback(),
            ItemExposeCallbackFactory.Companion.createPostViewCallback(),
            ItemExposeCallbackFactory.Companion.createPostFollowBtnCallback());

    private final FragmentVisibleDetector fragmentVisibleDetector = new FragmentVisibleDetector(new FragmentVisibleDetector.AutoPlayVideoListener() {
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

    public static ProfileLikeFragment create(String uid) {
        Bundle bundle = new Bundle();
        bundle.putString(UserConstant.UID, uid);
        ProfileLikeFragment fragment = new ProfileLikeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_host_profile_fragment, container, false);

        parseBundle();
        initView(view);
        initObservers();
        initData();

        mPostViewTimeManager.attach(mRecyclerView, mAdapter, EventConstants.FEED_SOURCE_PROFILE_LIKE);
        PostEventManager.INSTANCE.reset();
        PostEventManager.INSTANCE.setAction(EventConstants.AUTO_REFRESH);
        return view;
    }

    private void parseBundle() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            throw new NullPointerException("bundle can not be null!");
        }
        mUid = bundle.getString(UserConstant.UID);
        mIsBrowse = !AccountManager.getInstance().isLogin();
    }

    private void initObservers() {
        mPostViewModel = ViewModelProviders.of(this).get(ProfileLikeViewModel.class);
        mPostViewModel.refresh(mUid, mIsBrowse, mAdapter.getFeedSource());
        mPostViewModel.getPageLoadMoreStatus().observe(this, new Observer<PageLoadMoreStatusEnum>() {
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
        mPostViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                mRefreshLayout.finishRefresh();
                if (pageStatusEnum == null) {
                    return;
                }
                switch (pageStatusEnum) {
                    case LOADING:
                        if (mAdapter.getRealItemCount() > 0) {
                            showContent();
                        } else
                            showLoading();
                        break;
                    case EMPTY:
                        if (mAdapter.getRealItemCount() > 0) {
                            showContent();
                        } else
                            showEmptyView();
                        break;
                    case ERROR:
                        if (mAdapter.getRealItemCount() > 0) {
                            showContent();
                        } else
                            showErrorView();
                        break;
                    case CONTENT:
                        showContent();
                        break;
                }
            }
        });
        mPostViewModel.getPostLiveData().observe(this, new Observer<List<PostBase>>() {
            @Override
            public void onChanged(@Nullable List<PostBase> postBases) {
                mAdapter.addNewData(postBases);
                fragmentVisibleDetector.onDataLoaded();
                mPostViewTimeManager.setData(postBases, mAdapter.hasHeader());
                mPostViewTimeManager.onDataLoaded();
            }
        });
    }

    private void initView(View view) {
        mRefreshLayout = view.findViewById(R.id.feed_refresh_layout);
        mRecyclerView = view.findViewById(R.id.recycler_host_profile);
//        mRecyclerView.addOnScrollListener(mVideoItemVisibilityCalculator);
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mErrorView = view.findViewById(R.id.common_error_view);
        mLoadingView = view.findViewById(R.id.common_loading_view);

        mEmptyView.setClickable(false);
        mErrorView.setClickable(false);
        mLoadingView.setClickable(false);

        String source = AccountManager.getInstance().isSelf(mUid) ? EventConstants.FEED_PAGE_PROFILE_LIKE_OWNER : EventConstants.FEED_PAGE_PROFILE_LIKE_VISIT;
        mAdapter = new UserPostFeedAdapter(null, source);
        mAdapter.setRetryLoadMoreListener(new OnClickRetryListener() {
            @Override
            public void onRetryLoadMore() {
                if (canLoadMore()) {
                    onLoadMore();
                }
            }
        });
        mAdapter.setOnPostButtonClickListener(mOnPostButtonClickListener);
        mAdapter.setFirstTopMargin(0);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.showReadCount(false);
        if (mIsBrowse) {
            mAdapter.setBrowseClickListener(new IBrowseClickListener() {
                @Override
                public void onBrowseClick(int tye, boolean isShowLogin, int showType) {
                    BrowseSchemeManager.getInstance().setUserProfile(mUid);
                    EventLog.Login.report29(tye);
                    if (tye == BrowseConstant.TYPE_SHARE) {
                        EventLog.UnLogin.report16(2, EventConstants.LABEL_OTHER);
                    }
                    if (isShowLogin) {
                        StartEventManager.getInstance().setActionType(tye > 5 ? 6 : tye);
                        StartEventManager.getInstance().setFrom_page(2);
                        EventLog.UnLogin.report14(StartEventManager.getInstance().getActionType(), StartEventManager.getInstance().getFrom_page());
                        showLoginDialog();
                    }
                }
            });
        }
    }

    private void initData() {
        LinearLayoutManager llManager = new LinearLayoutManager(mContext);
        llManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (AccountManager.getInstance().isSelf(mUid)) {
            mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.
                    getString(ResourceTool.ResourceFileEnum.USER, "user_my_post_page_empty_text_next"), ResourceTool.
                    getString(ResourceTool.ResourceFileEnum.USER, "user_host_post_list_empty"), new EmptyView.IEmptyBtnClickListener() {
                @Override
                public void onClickEmptyBtn() {
                    PublishPostStarter.INSTANCE.startActivityFromMain(mContext, EventLog1.Publish.MainSource.ME);
                }
            });
        } else {
            mEmptyView.showEmptyImageText(R.drawable.ic_common_empty, ResourceTool.
                    getString(ResourceTool.ResourceFileEnum.USER, "user_host_post_list_empty"));
        }

        mErrorView.setOnErrorViewClickListener(new ErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                showLoading();
                mPostViewModel.refresh(mUid, mIsBrowse, mAdapter.getFeedSource());
                PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
            }
        });
        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                fragmentVisibleDetector.attach();
                mPostViewTimeManager.onAttach();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                fragmentVisibleDetector.detach();
                mPostViewTimeManager.onDetach();
            }
        });

        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);
        mRefreshLayout.setEnableAutoLoadMore(true);

        mRecyclerView.setLayoutManager(llManager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
        mRefreshLayout.setOnRefreshListener(this);
    }

    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showContent() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    public void showLoginDialog() {
//        RegisterActivity.Companion.show(mRecyclerView.getContext(), 0, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
        HalfLoginManager.getInstancce().showLoginDialog(mRecyclerView.getContext(), new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LIKE));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mPostViewTimeManager.onShow();
            PostEventManager.INSTANCE.reset();
            fragmentVisibleDetector.show();
        } else {
            fragmentVisibleDetector.hide();
            mPostViewTimeManager.onHide();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentVisibleDetector.onResume();
        mPostViewTimeManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentVisibleDetector.onPause();
        mPostViewTimeManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter != null && adapter instanceof FeedRecyclerViewAdapter) {
            ((FeedRecyclerViewAdapter) adapter).destroyVideo();
            mVideoItemVisibilityCalculator.resetCurrentPlayItem();

        }
        mPostViewTimeManager.onDestroy();
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mPostViewModel.loadMore();
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mPostViewModel.refresh(mUid, mIsBrowse, mAdapter.getFeedSource());
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_DOWN_REFRESH);
    }

    private OnPostButtonClickListener mOnPostButtonClickListener = new OnPostButtonClickListener() {
        @Override
        public void onCommentClick(PostBase postBase) {
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {
                if (TextUtils.equals(account.getUid(), mUid)) {
                    EventLog.Feed.report6(10, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                } else {
                    EventLog.Feed.report6(12, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                }
                if (!AccountManager.getInstance().isLogin()) {
                    EventLog.UnLogin.report23(EventConstants.UNLOGIN_FROM_PAGE_PROFILE);
                }
            }
        }

        @Override
        public void onLikeClick(PostBase postBase, int exp) {
            exp = Math.min(exp, 100);
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {
                if (TextUtils.equals(account.getUid(), mUid)) {
                    EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                            postBase == null ? 0 : postBase.getSuperLikeExp(), exp, 10, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                } else {
                    EventLog.Feed.report5(postBase == null ? "" : postBase.getPid(),
                            postBase == null ? 0 : postBase.getSuperLikeExp(), exp, 12, PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                }

                if (!AccountManager.getInstance().isLogin()) {
                    EventLog.UnLogin.report22(EventConstants.UNLOGIN_FROM_PAGE_PROFILE, null);
                }
            }
        }

        @Override
        public void onForwardClick(PostBase postBase) {
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {
                if (TextUtils.equals(account.getUid(), mUid)) {
                    EventLog.Feed.report7(10, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                } else {
                    EventLog.Feed.report7(12, postBase == null ? "" : postBase.getPid(), PostEventManager.getPostType(postBase), postBase == null ? null : postBase.getStrategy(), postBase == null ? "" : postBase.getSequenceId());
                }
            }
        }

        @Override
        public void onFollowClick(PostBase postBase) {
            if (!AccountManager.getInstance().isLogin()) {
                EventLog.UnLogin.report24(EventConstants.UNLOGIN_FROM_PAGE_PROFILE, mUid);
            }
        }

        @Override
        public void onShareClick(PostBase postBase, int shareType) {
            ShareEventManager.INSTANCE.setShareType(shareType);
            ShareEventManager.INSTANCE.setFrompage(EventConstants.SHARE_FROM_PAGE_PROFILE);
            ShareEventManager.INSTANCE.report3();
        }

        @Override
        public void onPostBodyClick(PostBase postBase) {
            ShareEventManager.INSTANCE.setChannel_page(EventConstants.SHARE_CHANNEL_PAGE_PROFILE);
        }

        @Override
        public void onPostAreaClick(PostBase postBase, int clickArea) {

            if (postBase == null) return;

            EventLog.Feed.report8(EventConstants.FEED_SOURCE_PROFILE_LIKE,
                    postBase.getUid(), PostEventManager.getPostRootUid(postBase),
                    postBase.getPid(), PostEventManager.getPostRootPid(postBase), clickArea,
                    PostEventManager.getPostType(postBase), postBase.getStrategy(), postBase.getSequenceId());
        }
    };

}
