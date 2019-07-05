package com.redefine.welike.business.location.ui.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.adapter.OnClickRetryListener;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter1;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.location.management.LBSUsersManager;
import com.redefine.welike.business.location.management.bean.LBSUser;
import com.redefine.welike.business.location.management.bean.Location;
import com.redefine.welike.business.location.ui.adapter.LocationPasserByAdapter;
import com.redefine.welike.business.location.ui.constant.LocationConstant;
import com.redefine.welike.business.location.ui.contract.ILocationPasserByContract;
import com.redefine.welike.business.user.ui.page.UserHostPage;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class LocationPasserByPresenter extends MvpTitlePagePresenter1<ILocationPasserByContract.ILocationPasserByView> implements ILocationPasserByContract.ILocationPasserByPresenter
        , OnClickRetryListener, LBSUsersManager.LBSUsersCallback {
    private LocationPasserByAdapter mAdapter;
    private LBSUsersManager mModel;
    private Location mLocation;

    public LocationPasserByPresenter(Activity stackManager, Bundle pageConfig) {
        super(stackManager, pageConfig);
    }

    @Override
    protected ILocationPasserByContract.ILocationPasserByView createPageView() {
        return ILocationPasserByContract.LocationPasserByFactory.createView();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        parseBundle(mPageBundle, savedInstanceState);
        mAdapter = new LocationPasserByAdapter();
        mView.setPresenter(this);
        mView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
                if (t instanceof LBSUser) {
                    UserHostPage.launch(true, ((LBSUser) t).getUser().getUid());
                }
            }
        });
        mAdapter.setRetryLoadMoreListener(this);
        mModel = new LBSUsersManager(mLocation.getPlaceId());
        mModel.setListener(this);
        onRefresh();
    }

    private void parseBundle(Bundle mPageBundle, Bundle savedInstanceState) {
        mLocation = (Location) mPageBundle.getSerializable(LocationConstant.BUNDLE_KEY_LOCATION);
        if (mLocation == null && savedInstanceState != null) {
            mLocation = (Location) savedInstanceState.getSerializable(LocationConstant.BUNDLE_KEY_LOCATION);
        }
    }

    @Override
    public void onRefresh() {
        mView.showLoading();
        mModel.tryRefreshUsers();
    }

    @Override
    public boolean canLoadMore() {
        return mAdapter.canLoadMore();
    }

    @Override
    public void onLoadMore() {
        mAdapter.onLoadMore();
        // do load more data
        mModel.tryHisUsers();
    }

    @Override
    public void destroy() {
        super.destroy();
        mAdapter.destroy();
    }

    @Override
    public void onRetryLoadMore() {
        if (canLoadMore()) {
            onLoadMore();
        }
    }

    @Override
    public void onRefreshLBSUsers(List<LBSUser> users, int errCode) {
        boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
        if (isSuccess) {
            if (!CollectionUtil.isEmpty(users)) {
                mAdapter.addHisData(users);
            }
            mAdapter.finishLoadMore();
            mAdapter.goneLoadMore();
        } else {
            mAdapter.loadError();
        }
        if (!CollectionUtil.isEmpty(users) || mAdapter.getRealItemCount() > 1) {
            mView.showContent();
        } else if (isSuccess) {
            mView.showEmptyView();
        } else {
            mView.showErrorView();
        }
    }

    @Override
    public void onReceiveHisLBSUsers(List<LBSUser> users, boolean last, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            if (!CollectionUtil.isEmpty(users)) {
                mAdapter.addHisData(users);
            }
            if (last) {
                mAdapter.noMore();
            } else {
                mAdapter.finishLoadMore();
            }
        } else {
            mAdapter.loadError();
        }
    }

}
