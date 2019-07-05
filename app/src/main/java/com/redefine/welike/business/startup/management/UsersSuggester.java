package com.redefine.welike.business.startup.management;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.bean.RecommondUser;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;
import com.redefine.welike.business.startup.management.request.SugUsersRequest;
import com.redefine.welike.business.user.management.parser.UserParser;
import com.redefine.welike.hive.AppsFlyerManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/2/11.
 */

public class UsersSuggester implements RequestCallback {
    private SugUsersRequest request;
    private String cursor;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private UsersSuggesterCallback listener;

    public interface UsersSuggesterCallback {

        void onRefreshUserSuggestions(List<RecommondUser> users, int errCode, ReferrerInfo info);

        void onHisUserSuggestions(List<RecommondUser> users, boolean last, int errCode);

    }

    public void setListener(UsersSuggesterCallback listener) {
        this.listener = listener;
    }

    public void refresh() {
        if (request != null) return;

        cursor = null;
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        request = new SugUsersRequest(null, MyApplication.getAppContext(), AppsFlyerManager.getInstance().getReferrerId());
        try {
            request.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onRefreshUserSuggestions(null, ErrorCode.networkExceptionToErrCode(e), null);
            }
        }
    }

    public void his() {
        if (request != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        request = new SugUsersRequest(cursor, MyApplication.getAppContext(), AppsFlyerManager.getInstance().getReferrerId());
        try {
            request.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onHisUserSuggestions(null, false, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (this.request == request) {
            this.request = null;
            final int error = errCode;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onRefreshUserSuggestions(null, error, null);
                        }
                    }

                });
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onHisUserSuggestions(null, false, error);
                        }
                    }

                });
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, final JSONObject result) throws Exception {
        if (this.request == request) {
            this.request = null;
//            final List<User> users = UserParser.parseUsers(result);
            final List<RecommondUser> users = SuggesterParser.parser(result);
            final ReferrerInfo info = SuggesterParser.parserReferrer(result);

            cursor = UserParser.parseCursor(result);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onRefreshUserSuggestions(users, ErrorCode.ERROR_SUCCESS, info);
                        }
                    }

                });
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        boolean last = TextUtils.isEmpty(cursor);
                        if (listener != null) {
                            listener.onHisUserSuggestions(users, last, ErrorCode.ERROR_SUCCESS);
                        }
                    }

                });
            }
        }
    }
}
