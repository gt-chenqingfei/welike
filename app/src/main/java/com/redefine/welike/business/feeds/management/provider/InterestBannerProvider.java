package com.redefine.welike.business.feeds.management.provider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.feeds.management.request.InterestBannerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/4/16
 */
public class InterestBannerProvider {

    private InterestBannerRequest bannerRequest;
    private String tagId;

    private Map<String, List<Banner>> mBannerMap = new HashMap<>();

    public interface ITagBannerCallback {
        void onBannerCallback(List<Banner> bannerList, int errCode);
    }

    public void load(String tagId, final ITagBannerCallback callback) {
        this.tagId = tagId;
        if(mBannerMap.size() < 1) {
            load(callback);
        } else {
            callback.onBannerCallback(mBannerMap.get(tagId), ErrorCode.ERROR_SUCCESS);
        }
    }

    private void load(final ITagBannerCallback callback) {
        if(bannerRequest != null) {
            return;
        }

        bannerRequest = new InterestBannerRequest(MyApplication.getAppContext());
        try {
            bannerRequest.req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, final int errCode) {
                    if (bannerRequest == request) {
                        bannerRequest = null;
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onBannerCallback(null, errCode);
                                }
                            }

                        });
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    if (bannerRequest == request) {
                        bannerRequest = null;
                        JSONArray bannersJSON = null;
                        try {
                            bannersJSON = result.getJSONArray("banners");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (bannersJSON != null && bannersJSON.size() > 0) {
                            for (int i = 0; i < bannersJSON.size(); i++) {
                                JSONObject bannerJSON = bannersJSON.getJSONObject(i);
                                Integer type = 1;
                                try {
                                    type = bannerJSON.getInteger("type");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                JSONArray innerBanners = bannerJSON.getJSONArray("banners");
                                final List<Banner> banners = new ArrayList<>();
                                if(innerBanners != null && innerBanners.size() > 0) {
                                    for (int j = 0 ; j < innerBanners.size() ; j++) {
                                        JSONObject innerBanner = innerBanners.getJSONObject(j);
                                        try {
                                            String photo = innerBanner.getString("picUrl");
                                            String action = innerBanner.getString("forwardUrl");
                                            Banner banner = new Banner();
                                            banner.setPhoto(photo);
                                            banner.setAction(action);
                                            banners.add(banner);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                mBannerMap.put(String.valueOf(type), banners);
                            }
                        }
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onBannerCallback(mBannerMap.get(tagId), ErrorCode.ERROR_SUCCESS);
                                }
                            }

                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            bannerRequest = null;
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (callback != null) {
                        callback.onBannerCallback(null, errCode);
                    }
                }

            });
        }
    }
}
