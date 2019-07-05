package com.redefine.welike.business.location.management.provider;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.location.management.bean.Location;
import com.redefine.welike.business.location.management.bean.PoiInfo;
import com.redefine.welike.business.location.management.parser.LBSParser;
import com.redefine.welike.business.location.management.request.LBSNearInfoRequest;

import java.util.List;

/**
 * Created by liubin on 2018/3/26.
 */

public class LBSNearPoiProvider {
    private LBSNearInfoRequest poiListRequest;
    private String cursor;
    private Context context;

    public interface LBSNearPoiProviderCallback {

        void onLBSNearPoiProviderNewResultEnd(List<PoiInfo> poiInfoList, int errCode);

        void onLBSNearPoiProviderHisResultEnd(List<PoiInfo> poiInfoList, boolean last, int errCode);

    }

    public LBSNearPoiProvider(Context context) {
        this.context = context;
    }

    public void tryPOIList(double lon, double lat, final LBSNearPoiProviderCallback callback) {
        if (poiListRequest != null) return;

        cursor = null;
        Location location = new Location();
        location.setLatitude(lat);
        location.setLongitude(lon);
        poiListRequest = new LBSNearInfoRequest(null, context, location, AccountManager.getInstance().isLoginComplete());
        try {
            poiListRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    if (poiListRequest == request) {
                        poiListRequest = null;
                        if (callback != null) {
                            callback.onLBSNearPoiProviderNewResultEnd(null, errCode);
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
                            callback.onLBSNearPoiProviderNewResultEnd(poiInfoList, ErrorCode.ERROR_SUCCESS);
                        }
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            poiListRequest = null;
            if (callback != null) {
                callback.onLBSNearPoiProviderNewResultEnd(null, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public void tryHis(final LBSNearPoiProviderCallback callback) {
        if (poiListRequest != null) return;

        if (!TextUtils.isEmpty(cursor)) {
            poiListRequest = new LBSNearInfoRequest(cursor, context, null, AccountManager.getInstance().isLoginComplete());
            try {
                poiListRequest.req(new RequestCallback() {

                    @Override
                    public void onError(BaseRequest request, int errCode) {
                        if (poiListRequest == request) {
                            poiListRequest = null;
                            if (callback != null) {
                                callback.onLBSNearPoiProviderHisResultEnd(null, false, errCode);
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
                                callback.onLBSNearPoiProviderHisResultEnd(poiInfoList, last, ErrorCode.ERROR_SUCCESS);
                            }
                        }
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                poiListRequest = null;
                if (callback != null) {
                    callback.onLBSNearPoiProviderHisResultEnd(null, false, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            if (callback != null) {
                callback.onLBSNearPoiProviderHisResultEnd(null, true, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

}
