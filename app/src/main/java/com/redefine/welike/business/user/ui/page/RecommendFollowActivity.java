package com.redefine.welike.business.user.ui.page;

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
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.commonui.event.expose.ItemExposeManager;
import com.redefine.welike.business.user.management.FollowUserManager;
import com.redefine.welike.business.user.management.bean.RecommendSlideUser;
import com.redefine.welike.business.user.ui.adapter.UserRecommendAdapter;
import com.redefine.welike.business.user.ui.vm.RecommendUserViewModel;
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author zhl
 */
@Route(path = RouteConstant.LAUNCH_RECOMMEND_FOLLOW_PAGE)
public class RecommendFollowActivity extends BaseActivity implements FollowUserManager.FollowUserCallback {
    private TextView title;
    private ImageView iv_back, iv_next;
    private RecyclerView recyclerView;
    private SmartRefreshLayout mRefreshLayout;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;

    private UserRecommendAdapter followRecyclerAdapter;

    private RecommendUserViewModel recommendUserViewModel;

    private boolean canLoadMore = true;
    private ItemExposeManager mPostViewTimeManager = new ItemExposeManager(ItemExposeCallbackFactory.Companion.createRecomUserFollowBtnCallback());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_recommend_follow_layout);
        recommendUserViewModel = ViewModelProviders.of(this).get(RecommendUserViewModel.class);
        initView();
        setEvents();
        recommendUserViewModel.tryRefresh();
        FollowUserManager.getInstance().register(this);
    }

    public static void launch() {
        EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_RECOMMEND_USER_PAGE, null));
    }


    private void initView() {
        title = findViewById(R.id.tv_common_title);
        iv_back = findViewById(R.id.iv_common_back);
        iv_next = findViewById(R.id.iv_common_next);
        findViewById(R.id.ll_common_layout).setBackgroundResource(R.color.white);
        recyclerView = findViewById(R.id.rv_recommend_user);
        mRefreshLayout = findViewById(R.id.pullLayout_recommend);
        mEmptyView = findViewById(R.id.common_empty_view);
        mErrorView = findViewById(R.id.common_error_view);
        mLoadingView = findViewById(R.id.common_loading_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        followRecyclerAdapter = new UserRecommendAdapter(EventConstants.FEED_PAGE_RECOMMENT_ALL_FOLLOW);
        recyclerView.setAdapter(followRecyclerAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);

        title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_follow_recommend_title"));
        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                mPostViewTimeManager.onAttach();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mPostViewTimeManager.onDetach();
            }
        });
        mPostViewTimeManager.attach(recyclerView, followRecyclerAdapter, null);
    }

    private void setEvents() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("from", EventLog1.AddFriend.ButtonFrom.OTHER);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CONTACT, bundle));
            }
        });

        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "follow_list_empty"), new EmptyView.IEmptyBtnClickListener() {
            @Override
            public void onClickEmptyBtn() {

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
                recommendUserViewModel.tryLoadMore();
            }
        }));

        mErrorView.setOnErrorViewClickListener(new ErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                showLoading();
                recommendUserViewModel.tryRefresh();
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                recommendUserViewModel.tryRefresh();
            }
        });

        recommendUserViewModel.getRecommedUsers().observe(this, new Observer<List<RecommendSlideUser>>() {
            @Override
            public void onChanged(@Nullable List<RecommendSlideUser> list) {
                if (list == null) return;
                followRecyclerAdapter.refreshData(list);
                mPostViewTimeManager.onDataLoaded();
            }
        });

        recommendUserViewModel.getRecommedMoreUsers().observe(this, new Observer<List<RecommendSlideUser>>() {
            @Override
            public void onChanged(@Nullable List<RecommendSlideUser> list) {


                if (list == null) {
                    followRecyclerAdapter.loadError();
                    return;
                }

                if (list.size() == 0) {
                    canLoadMore = false;
                    followRecyclerAdapter.noMore();
                }
                followRecyclerAdapter.addNewData(list);
            }
        });


        recommendUserViewModel.getHasNextPage().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean==null)return;

                if(!aBoolean){
                    followRecyclerAdapter.noMore();
                }
            }
        });

        recommendUserViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPostViewTimeManager.onResume();
        mPostViewTimeManager.onShow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPostViewTimeManager.onPause();
        mPostViewTimeManager.onHide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPostViewTimeManager.onDestroy();
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

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            followRecyclerAdapter.refreshOnFollow(uid);
        } else {
            followRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            followRecyclerAdapter.refreshOnUnFollow(uid);
        } else {
            followRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
