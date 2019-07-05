package com.redefine.welike.business.assignment.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.assignment.management.request.BannerRequest;
import com.redefine.welike.business.assignment.management.request.HomeBannerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class BannerManagement {
    private BannerRequest bannerRequest;
    private HomeBannerRequest homeBnnerRequest;

    public interface BannerManagementCallback {

        void onBannerManagementEnd(List<Banner> bannerList, int errCode);

    }

    public void load(final BannerManagementCallback callback) {
        if (bannerRequest != null) return;

        bannerRequest = new BannerRequest(MyApplication.getAppContext());
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
                                    callback.onBannerManagementEnd(null, errCode);
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
                        final List<Banner> banners = new ArrayList<>();
                        if (bannersJSON != null && bannersJSON.size() > 0) {
                            for (int i = 0; i < bannersJSON.size(); i++) {
                                JSONObject bannerJSON = bannersJSON.getJSONObject(i);
                                try {
                                    String photo = bannerJSON.getString("picUrl");
                                    String action = bannerJSON.getString("forwardUrl");
                                    String language = bannerJSON.getString("la");
                                    long id = bannerJSON.getLong("id");
                                    Banner banner = new Banner();
                                    banner.setPhoto(photo);
                                    banner.setAction(action);
                                    banner.setLanguage(language);
                                    banner.setId(id);
                                    banners.add(banner);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onBannerManagementEnd(banners, ErrorCode.ERROR_SUCCESS);
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
                        callback.onBannerManagementEnd(null, errCode);
                    }
                }

            });
        }
    }

    public void loadSkipBanner(final BannerManagementCallback callback) {
        if (bannerRequest != null) return;

        bannerRequest = new BannerRequest(MyApplication.getAppContext(),false);
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
                                    callback.onBannerManagementEnd(null, errCode);
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
                        final List<Banner> banners = new ArrayList<>();
                        if (bannersJSON != null && bannersJSON.size() > 0) {
                            for (int i = 0; i < bannersJSON.size(); i++) {
                                JSONObject bannerJSON = bannersJSON.getJSONObject(i);
                                try {
                                    String photo = bannerJSON.getString("picUrl");
                                    String action = bannerJSON.getString("forwardUrl");
                                    Banner banner = new Banner();
                                    banner.setPhoto(photo);
                                    banner.setAction(action);
                                    banners.add(banner);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onBannerManagementEnd(banners, ErrorCode.ERROR_SUCCESS);
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
                        callback.onBannerManagementEnd(null, errCode);
                    }
                }

            });
        }
    }

    public void loadHomeBanner(final BannerManagementCallback callback) {
        // TODO: 2018/5/9  优化
        if (homeBnnerRequest != null) return;

        homeBnnerRequest = new HomeBannerRequest(MyApplication.getAppContext());
        try {
            homeBnnerRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, final int errCode) {
                    if (homeBnnerRequest == request) {
                        homeBnnerRequest = null;
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onBannerManagementEnd(null, errCode);
                                }
                            }

                        });
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    if (homeBnnerRequest == request) {
                        homeBnnerRequest = null;
                        JSONArray bannersJSON = null;
                        try {
                            bannersJSON = result.getJSONArray("banners");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        final List<Banner> banners = new ArrayList<>();
                        if (bannersJSON != null && bannersJSON.size() > 0) {
                            Map<String, Banner> temp = new HashMap<>();
                            for (int i = 0; i < bannersJSON.size(); i++) {
                                JSONObject bannerJSON = bannersJSON.getJSONObject(i);
                                try {
                                    String photo = bannerJSON.getString("picUrl");
                                    String action = bannerJSON.getString("forwardUrl");
                                    String language = bannerJSON.getString("la");
                                    long id = bannerJSON.getLong("id");
                                    Banner banner = new Banner();

                                    banner.setPhoto(photo);
                                    banner.setAction(action);
                                    banner.setId(id);
                                    banner.setLanguage(language);
                                    if (temp.containsKey(action)) continue;
                                    temp.put(action, banner);
                                    banners.add(banner);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onBannerManagementEnd(banners, ErrorCode.ERROR_SUCCESS);
                                }
                            }

                        });
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            homeBnnerRequest = null;
            final int errCode = ErrorCode.networkExceptionToErrCode(e);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (callback != null) {
                        callback.onBannerManagementEnd(null, errCode);
                    }
                }

            });
        }
    }


}
