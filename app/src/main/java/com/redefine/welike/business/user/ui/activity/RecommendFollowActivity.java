package com.redefine.welike.business.user.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.feeds.ui.constant.FeedConstant;
import com.redefine.welike.business.user.management.bean.RecommendUser1;
import com.redefine.welike.business.user.ui.adapter.BottomFollowUserAdapter;
import com.redefine.welike.business.user.ui.adapter.UserRecommendAdapter1;
import com.redefine.welike.business.user.ui.vm.RecommendUserViewModel2;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhl
 */
@Route(path = RouteConstant.LAUNCH_RECOMMEND_ACTIVITY)
public class RecommendFollowActivity extends BaseActivity {
    private TextView title;
    private ImageView ivClose;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private View mBottomClickDelegate;

    private View clBottom;

    private RecyclerView followRecyclerView;

    private BottomFollowUserAdapter followUserAdapter;

    private UserRecommendAdapter1 followRecyclerAdapter;

    private RecommendUserViewModel2 recommendUserViewModel;

    private boolean canLoadMore = true;

    private int pageIndex = 0;

    private ItemExposeManager mItemExposeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createFullscreenUserCallback());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.dialog_bottom_enter, 0);
        setContentView(R.layout.page_recommend_follow_layout1);

        recommendUserViewModel = ViewModelProviders.of(this).get(RecommendUserViewModel2.class);

        initView();
        setEvents();
        recommendUserViewModel.tryRefresh();

    }

    public static void launch() {
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_RECOMMEND_USER_ACTIVITY, null));
    }

    private void initView() {
        title = findViewById(R.id.tv_common_title);
        ivClose = findViewById(R.id.iv_common_close);
        recyclerView = findViewById(R.id.rv_recommend_user);
        mRefreshLayout = findViewById(R.id.pullLayout_recommend);
        mEmptyView = findViewById(R.id.common_empty_view);
        mErrorView = findViewById(R.id.common_error_view);
        mLoadingView = findViewById(R.id.common_loading_view);
        clBottom = findViewById(R.id.cl_recommend_bottom);
        followRecyclerView = findViewById(R.id.rv_follow_user);
        mBottomClickDelegate = findViewById(R.id.cl_recommend_bottom_click_delegate);

        clBottom.setVisibility(View.GONE);
        followUserAdapter = new BottomFollowUserAdapter();

        followRecyclerView.setAdapter(followUserAdapter);

        followRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        followRecyclerAdapter = new UserRecommendAdapter1(new UserRecommendAdapter1.FollowStatusChangeListener() {
            @Override
            public void onFollowerChanged(RecommendUser1 user) {

                recommendUserViewModel.addFollowUser(user);

            }
        }, EventConstants.FEED_PAGE_HOME_FULL_SCREEN_RECOMMEND);
        recyclerView.setAdapter(followRecyclerAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);
        mItemExposeManager.attach(recyclerView, followRecyclerAdapter, null);

        title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_follow_recommend_title"));
    }

    private void setEvents() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recommendUserViewModel.getHasFollowUserList().size() > 0) {
                    goFollowing();
                }
                finish();
            }
        });

        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "follow_list_empty"), new EmptyView.IEmptyBtnClickListener() {
            @Override
            public void onClickEmptyBtn() {
                pageIndex = 0;
                recommendUserViewModel.tryRefresh();

            }
        });

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(new ILoadMoreDelegate() {
            @Override
            public boolean canLoadMore() {
                return canLoadMore;
            }

            @Override
            public void onLoadMore() {
                followRecyclerAdapter.onLoadMore();
                recommendUserViewModel.tryLoadMore(pageIndex);
            }
        }));
        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mItemExposeManager.onAttach();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mItemExposeManager.onDetach();
            }
        });

        mErrorView.setOnErrorViewClickListener(new ErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                pageIndex = 0;
                recommendUserViewModel.tryRefresh();
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                pageIndex = 0;
                recommendUserViewModel.tryRefresh();
            }
        });


        mBottomClickDelegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recommendUserViewModel.getHasFollowUserList().size() > 0) {
                    goFollowing();

                    EventLog1.Follow.report5();
                    finish();
                }

            }
        });

        recommendUserViewModel.getRecommedUsers().observe(this, new Observer<List<RecommendUser1>>() {
            @Override
            public void onChanged(@Nullable List<RecommendUser1> list) {
                if (list == null) return;
                if (pageIndex == 0) {
                    followRecyclerAdapter.refreshData(list);
                    mItemExposeManager.onDataLoaded();
                } else {
                    followRecyclerAdapter.finishLoadMore();
                    followRecyclerAdapter.addNewData(list);
                }

                if (list.size() == 0) {
                    followRecyclerAdapter.noMore();
                    canLoadMore = false;
                } else {
                    canLoadMore = true;
                    pageIndex++;
                }


            }
        });


        recommendUserViewModel.getMPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {

                if (pageStatusEnum == null) return;


                switch (pageStatusEnum) {
                    case ERROR:
                        mRefreshLayout.finishRefresh();
                        if (followRecyclerAdapter.getRealItemCount() == 0) {
                            showErrorView();
                        } else {
                            showContent();
                        }
                        break;
                    case CONTENT:
                        mRefreshLayout.finishRefresh();
                        if (followRecyclerAdapter.getRealItemCount() == 0) {
                            showEmptyView();
                        } else {
                            showContent();
                        }
                        break;
                    case LOADING:
                        if (followRecyclerAdapter.getRealItemCount() == 0)
                            showLoading();
                        break;

                }
            }
        });


        recommendUserViewModel.getFollowUsers().observe(this, new Observer<ArrayList<RecommendUser1>>() {
            @Override
            public void onChanged(@Nullable ArrayList<RecommendUser1> recommendUsers) {

                if (recommendUsers == null) {
                    return;
                }
                if (CollectionUtil.isEmpty(recommendUsers)) {
                    clBottom.setVisibility(View.GONE);
                } else {
                    clBottom.setVisibility(View.VISIBLE);
                }
                followUserAdapter.setData(recommendUsers);
            }
        });

    }

    private void goFollowing() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(FeedConstant.BUNDLE_KEY_FORCE_SHOW_FOLLOWING, true);
        Event event = new Event(EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_HOME, bundle);
        EventBus.getDefault().post(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (recommendUserViewModel.getHasFollowUserList().size() > 0) {
            goFollowing();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.dialog_bottom_exit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mItemExposeManager.onResume();
        mItemExposeManager.onShow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mItemExposeManager.onPause();
        mItemExposeManager.onHide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mItemExposeManager.onDestroy();
        if (followRecyclerAdapter != null) {
            followRecyclerAdapter.onDestroy();
        }
    }

    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mRefreshLayout.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showContent() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRefreshLayout.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRefreshLayout.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    public void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mRefreshLayout.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }
}
