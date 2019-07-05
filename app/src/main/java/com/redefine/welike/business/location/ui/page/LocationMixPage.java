package com.redefine.welike.business.location.ui.page;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.fresco.loader.BannerUrlLoader;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.util.ViewUtil;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.multimedia.player.apollo.ApolloVideoView;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.adapter.FeedRecyclerViewAdapter;
import com.redefine.welike.business.location.management.bean.LBSNearInfo;
import com.redefine.welike.business.location.management.bean.LBSUser;
import com.redefine.welike.business.location.ui.adapter.LocationMixAdapter;
import com.redefine.welike.business.location.ui.constant.LocationConstant;
import com.redefine.welike.business.location.ui.contract.ILocationMixContract;
import com.redefine.welike.business.location.ui.presenter.LocationMixPresenter;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.view.VideoItemVisibilityCalculator;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/20.
 */
//public class LocationMixPage extends MvpBaseTitlePage<ILocationMixContract.ILocationMixPresenter> {
@Route(path = RouteConstant.PATH_LOCATION_MIX)
public class LocationMixPage extends BaseActivity implements  ILocationMixContract.ILocationMixView, ErrorView.IErrorViewClickListener
        , EmptyView.IEmptyBtnClickListener, View.OnClickListener, ILoadMoreDelegate {

    private LocationMixPresenter mPresenter;
    public static final int TAB_HOT = 0;
    public static final int TAB_LATEST = 1;

    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private RecyclerView mRecyclerView;
    private View mBackBtn;
    private RefreshLayout mRefreshLayout;
    private View mNearByLayout;
    private View mUsersLayout;
    private TextView mNearUserCount;
    private String mUserHasBeanHereText;
    private TextView mNearUserAllUser;
    private LinearLayout mNearByUserGrid;
    private LayoutInflater mInflater;
    private TextView mTitleView;
    private SimpleDraweeView mLbsDraweeView;
    private TextView mPoiText;
    private AppBarLayout mAppBarLayout;

    private RelativeLayout mTabHot, mTabLatest;
    private TextView mHotText, mLatestText;
    private View mHotIndicator, mLatestIndicator;
    private LinearLayout mTabViewGroup;

    private ItemExposeManager mDelegate;
    private int mCurrentSelectTab;

    private VideoItemVisibilityCalculator mVideoItemVisibilityCalculator = new VideoItemVisibilityCalculator(new VideoItemVisibilityCalculator.VideoPlayStateCallback() {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_mix_layout);
//        LocationMixView view = new LocationMixView(findViewById(R.id.parent));
        createView(this, savedInstanceState);
        mPresenter = new LocationMixPresenter(this);
        mPresenter.initView(getIntent().getBundleExtra(LocationConstant.BUNDLE_KEY_LOCATION), savedInstanceState);
//        view.setPresenter(mPresenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onActivityPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof FeedRecyclerViewAdapter) {
            ((FeedRecyclerViewAdapter) adapter).destroy();
            mVideoItemVisibilityCalculator.resetCurrentPlayItem();

        }
    }

    public void createView(Context context, Bundle savedInstanceState) {
        mInflater = LayoutInflater.from(context);
//        View view = mInflater.inflate(R.layout.location_mix_layout, null);
        mEmptyView = findViewById(R.id.common_empty_view);
        mErrorView = findViewById(R.id.common_error_view);
        mLoadingView = findViewById(R.id.common_loading_view);
        mLbsDraweeView = findViewById(R.id.location_lbs_default);
        mRecyclerView = findViewById(R.id.location_mix_recycler_view);
        mPoiText = findViewById(R.id.location_mix_poi);
        mTitleView = findViewById(R.id.tv_common_title);
        mNearByLayout = findViewById(R.id.ll_location_info);
        mUsersLayout = findViewById(R.id.ll_location_user_all);
        mAppBarLayout = findViewById(R.id.location_mix_app_bar);
        mNearUserCount = findViewById(R.id.location_landing_posts_count);
        mNearUserAllUser = findViewById(R.id.tv_location_landing_user_all);
        mNearByUserGrid = findViewById(R.id.ll_location_user_headers);


        mTabViewGroup = findViewById(R.id.ll_tab);
        mTabHot = findViewById(R.id.topic_hot_tab);
        mTabLatest = findViewById(R.id.topic_latest_tab);
        mHotText = findViewById(R.id.topic_hot_text);
        mLatestText = findViewById(R.id.topic_latest_text);
        mHotIndicator = findViewById(R.id.topic_hot_indicator);
        mLatestIndicator = findViewById(R.id.topic_latest_indicator);

        mUserHasBeanHereText = ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "location_has_user");
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(this));
//        mRecyclerView.addOnScrollListener(mVideoItemVisibilityCalculator);
        mBackBtn = findViewById(R.id.iv_common_back);
        mRefreshLayout = findViewById(R.id.location_mix_refresh_layout);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);
        mNearUserAllUser.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "all"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty
                , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "post")
                , ResourceTool.getString(ResourceTool.ResourceFileEnum.LOCATION, "location_empty_view")
                , this);
        mErrorView.setOnErrorViewClickListener(this);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.onRefresh();
                PostEventManager.INSTANCE.setAction(EventConstants.PULL_DOWN_REFRESH);
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
            }
        });

        mHotText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "discover_hot"));
        mLatestText.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.FEED, "discover_latest"));
        mTitleView.setText(getResources().getString(R.string.location_select_title));
        mBackBtn.setOnClickListener(this);
        mUsersLayout.setOnClickListener(this);
        mTabHot.setOnClickListener(this);
        mTabLatest.setOnClickListener(this);
        mRecyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
                if (mDelegate != null) {
                    mDelegate.onAttach();
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                if (adapter instanceof FeedRecyclerViewAdapter) {
                    ((FeedRecyclerViewAdapter) adapter).destroyVideo();
                    mVideoItemVisibilityCalculator.resetCurrentPlayItem();
                }
                if (mDelegate != null) {
                    mDelegate.onDetach();
                }
            }
        });
//        return view;
    }

    @Override
    public void onErrorViewClick() {
        mPresenter.onRefresh();
        PostEventManager.INSTANCE.setAction(EventConstants.CLICK_BUTTON_REFRESH);
    }

    @Override
    public void onClickEmptyBtn() {
        mPresenter.goPublish();
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
        mNearByLayout.setVisibility(View.GONE);
        mUsersLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNearBy(LBSNearInfo nearInfo, List<LBSUser> userList) {
        if (null != nearInfo) {
            mNearByLayout.setOnClickListener(this);
            mNearByLayout.setVisibility(View.VISIBLE);
            mNearUserCount.setText(String.format(mUserHasBeanHereText, nearInfo.getUserCount()));
            if (!TextUtils.isEmpty(nearInfo.getPlace())) {
                mPoiText.setText(nearInfo.getPlace());
            }
            BannerUrlLoader.getInstance().loadBannerUrl(mLbsDraweeView, nearInfo.getPhoto());

        } else {
            mNearByLayout.setVisibility(View.GONE);
        }


        if (null != userList) {
            mNearByUserGrid.removeAllViews();
            mUsersLayout.setVisibility(View.VISIBLE);
            List<LBSUser> userHeaderList = new ArrayList<>();
            if (userList.size() > 3) {
                userHeaderList = userList.subList(0, 3);
            } else {
                userHeaderList = userList;
            }
            for (int i = 0; i < userHeaderList.size(); i++) {
                View view = mInflater.inflate(R.layout.topic_landing_user_item, mNearByUserGrid, false);
                VipAvatar simpleDraweeView = view.findViewById(R.id.topic_landing_user_icon);
                VipUtil.set(simpleDraweeView, userHeaderList.get(i).getUser().getHeadUrl(), userHeaderList.get(i).getUser().getVip());

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

                mNearByUserGrid.addView(view);
            }
        } else {
            mUsersLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean canScroll() {
        return ViewUtil.canScroll(mRecyclerView);
    }

    @Override
    public void showChangeTab() {
        mTabViewGroup.setVisibility(View.VISIBLE);
        selectHotTab();
    }

    private void selectHotTab() {
        mHotText.setTextColor(Color.parseColor("#464646"));
        mLatestText.setTextColor(Color.parseColor("#AFB0B1"));
        mHotIndicator.setVisibility(View.VISIBLE);
        mLatestIndicator.setVisibility(View.GONE);
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_LOCATION_HOT);
        if (mDelegate != null) {
            mDelegate.setSource(EventConstants.FEED_PAGE_LOCATION_HOT, EventConstants.FEED_SOURCE_LOCATION_HOT);
        }
    }

    private void selectLatestTab() {
        mLatestText.setTextColor(Color.parseColor("#464646"));
        mHotText.setTextColor(Color.parseColor("#AFB0B1"));
        mLatestIndicator.setVisibility(View.VISIBLE);
        mHotIndicator.setVisibility(View.GONE);
        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_LOCATION_LATEST);
        if (mDelegate != null) {
            mDelegate.setSource(EventConstants.FEED_PAGE_LOCATION_LATEST, EventConstants.FEED_SOURCE_LOCATION_LATEST);
        }
    }

    @Override
    public void hideChangeTab() {
        mTabViewGroup.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResume() {
        mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
    }

    @Override
    public void onActivityPause() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof FeedRecyclerViewAdapter) {
            ((FeedRecyclerViewAdapter) adapter).destroyVideo();
            mVideoItemVisibilityCalculator.resetCurrentPlayItem();

        }
    }

    @Override
    public void setRefreshCount(int size) {
//        mClassicsHeader.setRefreshCount(size);
    }

    @Override
    public void autoPlayVideo() {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mVideoItemVisibilityCalculator.onScrollStateChanged(mRecyclerView, RecyclerView.SCROLL_STATE_IDLE);
            }
        });
    }


    @Override
    public void setPresenter(ILocationMixContract.ILocationMixPresenter locationMixPresenter) {
//        mPresenter = locationMixPresenter;
    }

    @Override
    public void setAdapter(LocationMixAdapter mAdapter) {
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showContentView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            finish();
        } else if (v == mUsersLayout) {
            mPresenter.goPasserByPage();
        } else if (v == mNearByLayout) {
            // mPresenter.goPasserByPage();
        } else if (v == mTabHot) {
            mPresenter.changTab(TAB_HOT);
            mCurrentSelectTab = TAB_HOT;
            selectHotTab();
        } else if (v == mTabLatest) {
            mPresenter.changTab(TAB_LATEST);
            mCurrentSelectTab = TAB_LATEST;
            selectLatestTab();
        }
    }

    @Override
    public void setRecyclerViewManager(ItemExposeManager manager) {
        mDelegate = manager;
        manager.attach(mRecyclerView, (FeedRecyclerViewAdapter) mRecyclerView.getAdapter(), EventConstants.FEED_SOURCE_LOCATION_LATEST);

    }

    @Override
    public boolean canLoadMore() {
        return mPresenter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mPresenter.onLoadMore();
        PostEventManager.INSTANCE.setAction(EventConstants.PULL_UP_REFRESH);
    }
}
