package com.redefine.welike.business.location.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.business.location.management.LBSPOISearchManager;
import com.redefine.welike.business.location.management.bean.PoiInfo;
import com.redefine.welike.business.location.ui.contract.ILocationSelectContract;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liwenbo on 2018/3/20.
 */

public class LocationSelectPresenter implements ILocationSelectContract.ILocationSelectPresenter
        , LBSPOISearchManager.LBSPOISearchManagerCallback {

    //    private final Activity mActivity;
    private final ILocationSelectContract.ILocationSelectView mView;

    private LBSPOISearchManager mSearchManager;
    private String mLastQuery = "";

    private Disposable mDispose;

    private Location mCurrentLocation;

    public LocationSelectPresenter(Context context, ILocationSelectContract.ILocationSelectView view) {
//        mActivity = activity;
//        mAdapter = new LocationPoiAdapter();
        mSearchManager = new LBSPOISearchManager(context);
        mView = view;
//        mView.setPresenter(this);
    }

    @Override
    public void onCreate(View rootView, Bundle savedInstanceState) {
//        mView.onCreate(rootView, savedInstanceState);
//        mView.setAdapter(mAdapter);
//        mAdapter.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(RecyclerView.ViewHolder viewHolder, Object t) {
//                if (t instanceof PoiInfo) {
//                    Intent intent = new Intent();
//                    intent.putExtra(EditorConstant.KEY_POI_INFO, ((PoiInfo) t));
//                    mActivity.setResult(Activity.RESULT_OK, intent);
//                    finishActivity();
//                }
//            }
//        });
//        mAdapter.setRetryLoadMoreListener(this);
//        mView.showLoading();
//        if (EasyPermissions.hasPermissions(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            doLocation();
//        } else {
//            EasyPermissions.requestPermissions(mActivity,
//                    ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "location_permission"),
//                    PermissionRequestCode.LOCATION_PERMISSION_REQUEST, Manifest.permission.ACCESS_COARSE_LOCATION);
//        }
    }

    public void doLocation(Location location) {
        mCurrentLocation = location;
        mSearchManager.tryPOIList(location.getLongitude(), location.getLatitude(), LocationSelectPresenter.this);

//        FusedLocationHelper.INSTANCE.tryLocateOnce(mActivity, new LocationOnceCallback() {
//            @Override
//            public void onLocateOnce(Location location) {
//                if (location == null) {
//                    mView.showEmptyView();
//                } else {
//                    mCurrentLocation = location;
//                    mSearchManager.tryPOIList(location.getLongitude(), location.getLatitude(), LocationSelectPresenter.this);
//                }
//            }
//
//            @Override
//            public void onLocateOnceFailed() {
//                mView.showEmptyView();
//            }
//        });
    }

//    @Override
//    public void showEmptyView() {
//        mView.showEmptyView();
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy() {
//        mView.onDestroy();
    }

//    @Override
//    public void onBackPressed() {
////        finishActivity();
//    }

//    private void finishActivity() {
//        mActivity.overridePendingTransition(R.anim.sliding_to_left_in, R.anim.sliding_right_out);
//        mActivity.finish();
//    }

    @Override
    public void onTextChange(String s) {
        if (!TextUtils.equals(s, mLastQuery) && mCurrentLocation != null) {
            mLastQuery = s;
//            mAdapter.goneLoadMore();
//            mAdapter.addNewData(null);
            mView.stopLoadmore();
            if (mDispose != null && !mDispose.isDisposed()) {
                mDispose.dispose();
            }
            mDispose = Schedulers.newThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(mLastQuery)) {
                        mSearchManager.tryPOIList(mCurrentLocation.getLongitude(), mCurrentLocation.getLatitude(), LocationSelectPresenter.this);
                    } else {
                        mSearchManager.clearSearchPoiTask();
                        mSearchManager.searchPOIList(mCurrentLocation.getLongitude(), mCurrentLocation.getLatitude(), mLastQuery, LocationSelectPresenter.this);
                    }

                }
            }, 300, TimeUnit.MILLISECONDS);
        }
    }

//    @Override
//    public boolean canLoadMore() {
//        return mAdapter.canLoadMore();
//    }

    @Override
    public void onLoadMore() {
//        mAdapter.onLoadMore();
        // do load more data
        if (TextUtils.isEmpty(mLastQuery)) {
            mSearchManager.tryPOIHis(this);
        } else {
            mSearchManager.searchPOIHis(this);
        }

    }

//    @Override
//    public void onRetryLoadMore() {
//        if (canLoadMore()) {
//            onLoadMore();
//        }
//    }

    @Override
    public void onClickError() {
        mView.showLoading();
        if (mDispose != null && !mDispose.isDisposed()) {
            mDispose.dispose();
        }
        mDispose = Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mLastQuery)) {
                    mSearchManager.tryPOIList(mCurrentLocation.getLongitude(), mCurrentLocation.getLatitude(), LocationSelectPresenter.this);
                } else {
                    mSearchManager.searchPOIList(mCurrentLocation.getLongitude(), mCurrentLocation.getLatitude(), mLastQuery, LocationSelectPresenter.this);
                }
            }
        }, 300, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onSearchManagerTryNewResultEnd(List<PoiInfo> poiInfoList, String keyword, int errCode) {
        boolean isSuccess = errCode == ErrorCode.ERROR_SUCCESS;
        if (isSuccess) {
            if (!TextUtils.equals(mLastQuery, keyword) && !TextUtils.isEmpty(mLastQuery)) {
                return;
            }
            mView.showNew(poiInfoList);
        } else {
            mView.loadError();
        }
        if (!CollectionUtil.isEmpty(poiInfoList)) {
            mView.showContent();
        } else if (isSuccess) {
            mView.showEmptyView();
        } else {
            mView.showErrorView();
        }
    }

    @Override
    public void onSearchManagerTryHisResultEnd(List<PoiInfo> poiInfoList, String keyword, boolean last, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            if (!TextUtils.equals(mLastQuery, keyword) && !TextUtils.isEmpty(mLastQuery)) {
                return;
            }
            if (!CollectionUtil.isEmpty(poiInfoList)) {
                mView.showLoadMore(poiInfoList);
            }
            if (last) {
                mView.showNoMore();
            } else {
                mView.finishLoadMore();
            }
        } else {
            mView.loadError();
        }
    }
}
