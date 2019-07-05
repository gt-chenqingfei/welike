package com.redefine.welike.business.user.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.bean.RecommendTagBean;
import com.redefine.welike.business.user.ui.adapter.RecommendUserAdapter;
import com.redefine.welike.business.user.ui.vm.RecommendUserViewModel1;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.InterestAndRecommendCardEventManager;
import com.redefine.welike.statistical.manager.PostEventManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * create by honglin
 */
public class HomeRecommendFragment extends Fragment {


    private View mRootView;
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;
    private LoadingView mLoadingView;
    private TextView mMainTitleView;
    private TextView tvFollowBtn;

    private RecommendUserAdapter followRecyclerAdapter;

    private RecommendUserViewModel1 recommendUserViewModel;

    private LoadingDlg loadingDlg;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recommendUserViewModel = ViewModelProviders.of(this).get(RecommendUserViewModel1.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.page_recommend_home_layout, null);

        initViews();


        setEvent();

        recommendUserViewModel.tryRefreshRecommendWithTag();

        return mRootView;
    }


    private void initViews() {
        mMainTitleView = mRootView.findViewById(R.id.common_title_view);
        mMainTitleView.setText(ResourceTool.getString("main_tab_home"));
        mRefreshLayout = mRootView.findViewById(R.id.main_home_refresh_layout);
        mEmptyView = mRootView.findViewById(R.id.common_empty_view);
        mErrorView = mRootView.findViewById(R.id.common_error_view);
        mLoadingView = mRootView.findViewById(R.id.common_loading_view);
        mRecyclerView = mRootView.findViewById(R.id.main_home_recycler_view);
        tvFollowBtn = mRootView.findViewById(R.id.tv_follow_btn);
        mRootView.findViewById(R.id.header_layout).setVisibility(View.GONE);
        followRecyclerAdapter = new RecommendUserAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(followRecyclerAdapter);
        mRefreshLayout.setEnableAutoLoadMore(false);
        mRefreshLayout.setEnableLoadMore(false);

        tvFollowBtn.setText(ResourceTool.getString("follow_all_select"));

    }


    private void setEvent() {

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                // TODO: 2018/7/26
                recommendUserViewModel.tryRefreshRecommendWithTag();
            }
        });

        mEmptyView.showEmptyBtn(R.drawable.ic_common_empty, ResourceTool.getString("follow_list_empty"), new EmptyView.IEmptyBtnClickListener() {
            @Override
            public void onClickEmptyBtn() {

                recommendUserViewModel.tryRefreshRecommendWithTag();
            }
        });

        tvFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> ids = followRecyclerAdapter.getSelectUserIds();
                ids.add("2023");
                recommendUserViewModel.reqFollowUsers(followRecyclerAdapter.getSelectUserIds());
                InterestAndRecommendCardEventManager.INSTANCE.setFollow_number(CollectionUtil.getCount(ids) - 1);
                InterestAndRecommendCardEventManager.INSTANCE.setFollow_list(followRecyclerAdapter.getSelectTagIds());
                InterestAndRecommendCardEventManager.INSTANCE.report5();
            }
        });

        recommendUserViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {

                if (pageStatusEnum == null) return;


                switch (pageStatusEnum) {
                    case ERROR:
                        mRefreshLayout.finishRefresh();
                        if (followRecyclerAdapter.getRealItemCount() == 2) {
                            showErrorView();
                        } else {
                            showContentView();
                        }
                        dissmissDlg();
                        break;
                    case CONTENT:
                        mRefreshLayout.finishRefresh();
                        if (followRecyclerAdapter.getRealItemCount() == 2) {
                            showEmptyView();
                        } else {
                            showContentView();
                        }
                        dissmissDlg();
                        break;
                    case LOADING:
                        if (followRecyclerAdapter.getRealItemCount() == 2)
                            showLoading();
                        else {
                            showLoadingDlg();
                        }
                        break;

                }
            }
        });


        recommendUserViewModel.getRecommendTagBeans().observe(this, new Observer<List<RecommendTagBean>>() {
            @Override
            public void onChanged(@Nullable List<RecommendTagBean> recommendTagBeans) {

                if (recommendTagBeans == null) return;

                followRecyclerAdapter.addNewData(recommendTagBeans);
            }
        });

        recommendUserViewModel.getCode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer == null) return;
                if (integer != ErrorCode.ERROR_SUCCESS)
                    ToastUtils.showShort(ErrorCode.showErrCodeText(integer));
                else {
                    AccountManager.getInstance().getAccount().setFollowUsersCount(followRecyclerAdapter.getSelectUserIds().size());
                    AccountManager.getInstance().updateAccount(AccountManager.getInstance().getAccount().copy());
                    EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_HOME));
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dissmissDlg();
    }

    private void showLoadingDlg() {
        if (loadingDlg == null) loadingDlg = new LoadingDlg(getActivity());
        loadingDlg.dismiss();
        loadingDlg.show();
    }


    private void dissmissDlg() {
        if (loadingDlg != null) loadingDlg.dismiss();
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
