package com.redefine.welike.business.location.management.provider;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.location.management.bean.PoiInfo;
import com.redefine.welike.business.location.management.parser.LBSParser;
import com.redefine.welike.business.location.management.request.LBSPOISearchRequest;

import java.util.List;

/**
 * Created by liubin on 2018/3/26.
 */

public class LBSSearchProvider {
    private LBSPOISearchRequest poiListRequest;
    private String cursor;
    private String keyword;
    private Context context;

    public interface LBSSearchProviderCallback {

        void onLBSSearchProviderNewResultEnd(List<PoiInfo> poiInfoList, String keyword, int errCode);
        void onLBSSearchProviderHisResultEnd(List<PoiInfo> poiInfoList, String keyword, boolean last, int errCode);

    }

    public LBSSearchProvider(Context context) {
        this.context = context;
    }

    public void tryPOIList(double lon, double lat, String keyword, final LBSSearchProviderCallback callback) {
        if (poiListRequest != null) return;

        cursor = null;
        this.keyword = keyword;
        poiListRequest = new LBSPOISearchRequest(lon, lat, null, keyword, context);
        try {
            poiListRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    if (poiListRequest == request) {
                        poiListRequest = null;
                        if (callback != null) {
                            callback.onLBSSearchProviderNewResultEnd(null, LBSSearchProvider.this.keyword, errCode);
                        }
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    if (poiListRequest == request) {
                        poiListRequest = null;
                        final List<PoiInfo> poiInfoList = LBSParser.parserPoiList(result);
                        if (result.containsKey("pageToken")) {
                            cursor = result.getString("pageToken");
                        } else {
                            cursor = null;
                        }
                        if (callback != null) {
                            callback.onLBSSearchProviderNewResultEnd(poiInfoList, LBSSearchProvider.this.keyword, ErrorCode.ERROR_SUCCESS);
                        }
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            poiListRequest = null;
            if (callback != null) {
                callback.onLBSSearchProviderNewResultEnd(null, this.keyword, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public void clear() {
        if (poiListRequest != null) {
            poiListRequest.cancel();
            cursor = null;
            poiListRequest = null;
        }
    }


    public void tryHis(final LBSSearchProviderCallback callback) {
        if (poiListRequest != null) return;

        if (!TextUtils.isEmpty(cursor)) {
            poiListRequest = new LBSPOISearchRequest(0, 0, cursor, null, context);
            try {
                poiListRequest.req(new RequestCallback() {

                    @Override
                    public void onError(BaseRequest request, int errCode) {
                        if (poiListRequest == request) {
                            poiListRequest = null;
                            if (callback != null) {
                                callback.onLBSSearchProviderHisResultEnd(null, LBSSearchProvider.this.keyword, false, errCode);
                            }
                        }
                    }

                    @Override
                    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                        if (poiListRequest == request) {
                            poiListRequest = null;
                            final List<PoiInfo> poiInfoList = LBSParser.parserPoiList(result);
                            if (result.containsKey("pageToken")) {
                                cursor = result.getString("pageToken");
                            } else {
                                cursor = null;
                            }
                            boolean last = TextUtils.isEmpty(cursor);
                            if (callback != null) {
                                callback.onLBSSearchProviderHisResultEnd(poiInfoList, LBSSearchProvider.this.keyword, last, ErrorCode.ERROR_SUCCESS);
                            }
                        }
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                poiListRequest = null;
                if (callback != null) {
                    callback.onLBSSearchProviderHisResultEnd(null, keyword, false, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            if (callback != null) {
                callback.onLBSSearchProviderHisResultEnd(null, keyword, true, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

}
