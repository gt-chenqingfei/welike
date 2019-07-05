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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.enums.PageLoadMoreStatusEnum;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener;
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate;
import com.redefine.commonui.loadmore.adapter.ProfilePhotoLoadMoreOnScrollListener;
import com.redefine.commonui.widget.BaseErrorView;
import com.redefine.commonui.widget.EmptyView;
import com.redefine.commonui.widget.ErrorView;
import com.redefine.commonui.widget.LoadingView;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.user.management.bean.GroupedPhoto;
import com.redefine.welike.business.user.ui.activity.ProfilePhotoPreviewActivity;
import com.redefine.welike.business.user.ui.adapter.ProfileGroupPhotoAdapter;
import com.redefine.welike.business.user.ui.adapter.ProfilePhotoAdapter;
import com.redefine.welike.business.user.ui.constant.UserConstant;
import com.redefine.welike.business.user.ui.vm.ProfilePhotoViewModel;
import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;
import com.redefine.welike.statistical.EventLog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by nianguowang on 2018/9/28
 */
public class ProfilePhotoFragment extends Fragment implements ILoadMoreDelegate {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;
    private LoadingView mLoadingView;
    private EmptyView mEmptyView;
    private ErrorView mErrorView;

    private ProfileGroupPhotoAdapter mAdapter;
    private ProfilePhotoViewModel mProfilePhotoViewModel;
    private String mUid;

    public static ProfilePhotoFragment create(String uid) {
        Bundle bundle = new Bundle();
        bundle.putString(UserConstant.UID, uid);
        ProfilePhotoFragment fragment = new ProfilePhotoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_photo, container, false);

        initView(rootView);
        parseBundle();
        initData();

        return rootView;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.profile_photo_recycler);
        mRefreshLayout = view.findViewById(R.id.feed_refresh_layout);
        mLoadingView = view.findViewById(R.id.common_loading_view);
        mEmptyView = view.findViewById(R.id.common_empty_view);
        mErrorView = view.findViewById(R.id.common_error_view);

        mEmptyView.showEmptyImageText(R.drawable.ic_common_empty, getString(R.string.album_is_empty));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(false);
        mRefreshLayout.setEnableAutoLoadMore(true);

        mEmptyView.setClickable(false);
        mErrorView.setClickable(false);
        mLoadingView.setClickable(false);
    }

    private void parseBundle() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        mUid = arguments.getString(UserConstant.UID);
    }

    private void initData() {
        mProfilePhotoViewModel = ViewModelProviders.of(this).get(ProfilePhotoViewModel.class);
        mProfilePhotoViewModel.autoRefresh(mUid);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mProfilePhotoViewModel.refresh();
            }
        });
        mErrorView.setOnErrorViewClickListener(new BaseErrorView.IErrorViewClickListener() {
            @Override
            public void onErrorViewClick() {
                mProfilePhotoViewModel.autoRefresh(mUid);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addOnScrollListener(new ProfilePhotoLoadMoreOnScrollListener(this));
        mAdapter = new ProfileGroupPhotoAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setAttachmentClickListener(new ProfilePhotoAdapter.OnAttachmentClickListener() {
            @Override
            public void onAttachmentClick(AttachmentBase attachmentBase) {
                List<AttachmentBase> attachmentList = mAdapter.getAttachmentList();
                int position = attachmentList.indexOf(attachmentBase);
                ProfilePhotoPreviewActivity.show(attachmentList, position);
                EventLog.Profile.report15();
            }
        });
        mProfilePhotoViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) {
                    return;
                }
                switch (pageStatusEnum) {
                    case ERROR:
                        showErrorView();
                        break;
                    case CONTENT:
                        showContent();
                        break;
                    case LOADING:
                        showLoading();
                        break;
                    case EMPTY:
                        showEmptyView();
                        break;
                }
            }
        });
        mProfilePhotoViewModel.getLoadMoreStatus().observe(this, new Observer<PageLoadMoreStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageLoadMoreStatusEnum pageLoadMoreStatusEnum) {
                if (pageLoadMoreStatusEnum == null) {
                    return ;
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
        mProfilePhotoViewModel.getAttachmentsLiveData().observe(this, new Observer<List<AttachmentBase>>() {
            @Override
            public void onChanged(@Nullable List<AttachmentBase> attachmentBases) {
                mRefreshLayout.finishRefresh();
                List<GroupedPhoto> groupedPhotos = mAdapter.groupImageByMonth(attachmentBases);
                Collections.sort(groupedPhotos);
                if (CollectionUtil.isEmpty(groupedPhotos)) {
                    showEmptyView();
                } else {
                    showContent();
                    mAdapter.setData(groupedPhotos);
                }
            }
        });
    }

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    private void showContent() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    private void showEmptyView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.INVISIBLE);
    }

    private void showErrorView() {
        mLoadingView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mProfilePhotoViewModel.loadMore();
    }
}
