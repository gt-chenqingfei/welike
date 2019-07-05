package com.redefine.welike.business.location.management;

import android.content.Context;

import com.redefine.welike.business.location.management.bean.PoiInfo;
import com.redefine.welike.business.location.management.provider.LBSNearPoiProvider;
import com.redefine.welike.business.location.management.provider.LBSSearchProvider;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/3/26.
 */

public class LBSPOISearchManager {
    private final LBSNearPoiProvider nearPoiProvider;
    private final LBSSearchProvider searchProvider;

    public interface LBSPOISearchManagerCallback {

        void onSearchManagerTryNewResultEnd(List<PoiInfo> poiInfoList, String keyword, int errCode);
        void onSearchManagerTryHisResultEnd(List<PoiInfo> poiInfoList, String keyword, boolean last, int errCode);

    }

    public LBSPOISearchManager(Context context) {
        nearPoiProvider = new LBSNearPoiProvider(context);
        searchProvider = new LBSSearchProvider(context);
    }

    public void tryPOIList(double lon, double lat, final LBSPOISearchManagerCallback callback) {
        nearPoiProvider.tryPOIList(lon, lat, new LBSNearPoiProvider.LBSNearPoiProviderCallback() {

            @Override
            public void onLBSNearPoiProviderNewResultEnd(List<PoiInfo> poiInfoList, int errCode) {
                newCallback(poiInfoList, null, errCode, callback);
            }

            @Override
            public void onLBSNearPoiProviderHisResultEnd(List<PoiInfo> poiInfoList, boolean last, int errCode) {
                hisCallback(poiInfoList, null, last, errCode, callback);
            }

        });
    }

    public void tryPOIHis(final LBSPOISearchManagerCallback callback) {
        nearPoiProvider.tryHis(new LBSNearPoiProvider.LBSNearPoiProviderCallback() {

            @Override
            public void onLBSNearPoiProviderNewResultEnd(List<PoiInfo> poiInfoList, int errCode) {
                newCallback(poiInfoList, null, errCode, callback);
            }

            @Override
            public void onLBSNearPoiProviderHisResultEnd(List<PoiInfo> poiInfoList, boolean last, int errCode) {
                hisCallback(poiInfoList, null, last, errCode, callback);
            }

        });
    }

    public void clearSearchPoiTask() {
        searchProvider.clear();
    }


    public void searchPOIList(double lon, double lat, String keyword, final LBSPOISearchManagerCallback callback) {
        searchProvider.tryPOIList(lon, lat, keyword, new LBSSearchProvider.LBSSearchProviderCallback() {

            @Override
            public void onLBSSearchProviderNewResultEnd(List<PoiInfo> poiInfoList, String keyword, int errCode) {
                newCallback(poiInfoList, keyword, errCode, callback);
            }

            @Override
            public void onLBSSearchProviderHisResultEnd(List<PoiInfo> poiInfoList, String keyword, boolean last, int errCode) {
                hisCallback(poiInfoList, keyword, last, errCode, callback);
            }

        });
    }

    public void searchPOIHis(final LBSPOISearchManagerCallback callback) {
        searchProvider.tryHis(new LBSSearchProvider.LBSSearchProviderCallback() {

            @Override
            public void onLBSSearchProviderNewResultEnd(List<PoiInfo> poiInfoList, String keyword, int errCode) {
                newCallback(poiInfoList, keyword, errCode, callback);
            }

            @Override
            public void onLBSSearchProviderHisResultEnd(List<PoiInfo> poiInfoList, String keyword, boolean last, int errCode) {
                hisCallback(poiInfoList, keyword, last, errCode, callback);
            }

        });
    }

    private void newCallback(final List<PoiInfo> poiInfos, final String keyword, final int errCode, final LBSPOISearchManagerCallback callback) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSearchManagerTryNewResultEnd(poiInfos, keyword, errCode);
                }
            }
        });
    }

    private void hisCallback(final List<PoiInfo> poiInfos, final String keyword, final boolean last, final int errCode, final LBSPOISearchManagerCallback callback) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSearchManagerTryHisResultEnd(poiInfos, keyword, last, errCode);
                }
            }
        });
    }

}
