package com.redefine.welike.business.startup.management;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;
import com.redefine.welike.business.startup.management.request.VerticalRequest;
import com.redefine.welike.hive.AppsFlyerManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/2/11.
 */

public class VerticalSuggester implements RequestCallback {
    private VerticalRequest request;
    private IntrestsSuggesterCallback listener;
    private int cursorNum;

    private boolean isAuth = true ;

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public interface IntrestsSuggesterCallback {

        void onRefreshIntrestSuggestions(List<UserBase.Intrest> intrests, int errCode, ReferrerInfo info);

        void onHisIntrestSuggestions(List<UserBase.Intrest> intrests, boolean last, int errCode);

    }

    public void setListener(IntrestsSuggesterCallback listener) {
        this.listener = listener;
    }

    public void refresh() {
        if (request != null) return;
        cursorNum = 0;
        if(isAuth)
        request = new VerticalRequest(cursorNum, MyApplication.getAppContext(), AppsFlyerManager.getInstance().getReferrerId());
        else  request = new VerticalRequest(cursorNum, MyApplication.getAppContext(),false, AppsFlyerManager.getInstance().getReferrerId());
        try {
            request.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            request = null;
            if (listener != null) {
                listener.onRefreshIntrestSuggestions(null, ErrorCode.networkExceptionToErrCode(e),null);
            }
        }
    }

    public void his() {
        if (request != null) return;

        cursorNum++;
        request = new VerticalRequest(cursorNum, MyApplication.getAppContext(), AppsFlyerManager.getInstance().getReferrerId());
        try {
            request.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            request = null;
            if (listener != null) {
                listener.onHisIntrestSuggestions(null, false, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == this.request) {
            this.request = null;
            final int error = errCode;
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (cursorNum == 0) {
                        if (listener != null) {
                            listener.onRefreshIntrestSuggestions(null, error,null);
                        }
                    } else {
                        if (listener != null) {
                            listener.onHisIntrestSuggestions(null, false, error);
                        }
                    }
                }

            });
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == this.request) {
            this.request = null;
            final List<UserBase.Intrest> intrests = parseIntrestList(result);
            final ReferrerInfo info = SuggesterParser.parserReferrer(result);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    if (cursorNum == 0) {
                        if (listener != null) {
                            listener.onRefreshIntrestSuggestions(intrests, ErrorCode.ERROR_SUCCESS,info);
                        }
                    } else {
                        boolean last;
                        if (intrests == null) {
                            last = true;
                        } else {
                            if (intrests.size() < GlobalConfig.INTRESTS_NUM_ONE_PAGE) {
                                last = true;
                            } else {
                                last = false;
                            }
                        }
                        if (listener != null) {
                            listener.onHisIntrestSuggestions(intrests, last, ErrorCode.ERROR_SUCCESS);
                        }
                    }
                }

            });
        }
    }

    private static List<UserBase.Intrest> parseIntrestList(JSONObject result) {
        JSONArray listJSON = result.getJSONArray("list");
        if (listJSON != null && listJSON.size() > 0) {
            List<UserBase.Intrest> intrests = new ArrayList<>();
            for (int i = 0; i < listJSON.size(); i++) {
                try {
                    JSONObject intrestJSON = listJSON.getJSONObject(i);
                    String iid = intrestJSON.getString("id");
                    String name = intrestJSON.getString("name");
                    String icon = intrestJSON.getString("icon");
                    int checked = intrestJSON.getIntValue("isDefault");
                    UserBase.Intrest intrest = new UserBase.Intrest();
                    intrest.setIid(iid);
                    intrest.setLabel(name);
                    intrest.setIcon(icon);
                    intrest.setChecked(checked != 0);
                    intrests.add(intrest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return intrests;
        }
        return null;
    }

}
