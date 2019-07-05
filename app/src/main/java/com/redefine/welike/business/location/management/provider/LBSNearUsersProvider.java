package com.redefine.welike.business.location.management.provider;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.common.UsersProviderBase;
import com.redefine.welike.business.location.management.bean.LBSUser;
import com.redefine.welike.business.location.management.parser.LBSParser;
import com.redefine.welike.business.location.management.request.LBSNearUsersRequest;

import java.util.List;

/**
 * Created by liubin on 2018/3/21.
 */

public class LBSNearUsersProvider extends UsersProviderBase implements RequestCallback {
    private LBSNearUsersRequest usersRequest;
    private String placeId;
    private int pageIdx;
    private boolean last;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;
    private LBSNearUsersProviderCallback listener;

    public interface LBSNearUsersProviderCallback {

        void onRefreshLBSNearUsers(final List<LBSUser> users, final int errCode);

        void onReceiveHisLBSNearUsers(final List<LBSUser> users, final boolean last, final int errCode);

    }

    public LBSNearUsersProvider(String placeId) {
        this.placeId = placeId;
    }

    public void setListener(LBSNearUsersProviderCallback listener) {
        this.listener = listener;
    }

    public void tryRefreshUsers() {
        if (usersRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        pageIdx = 0;
        last = false;
        usersRequest = new LBSNearUsersRequest(MyApplication.getAppContext());
        try {
            usersRequest.tryUsers(placeId, pageIdx, this);
        } catch (Exception e) {
            e.printStackTrace();
            usersRequest = null;
            callReceiveNewContacts(null, ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public void tryHisUsers() {
        if (usersRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;
        if (!last) {
            usersRequest = new LBSNearUsersRequest(MyApplication.getAppContext());
            try {
                usersRequest.tryUsers(placeId, pageIdx, this);
            } catch (Exception e) {
                e.printStackTrace();
                usersRequest = null;
                callReceiveHisContacts(null, ErrorCode.networkExceptionToErrCode(e));
            }
        } else {
            usersRequest = null;
            callReceiveHisContacts(null, ErrorCode.ERROR_SUCCESS);
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (request == usersRequest) {
            usersRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisContacts(null, errCode);
            } else if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                callReceiveNewContacts(null, errCode);
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == usersRequest) {
            List<LBSUser> users = LBSParser.parserLBSUser(result);
            try {
                last = !result.getBoolean("hasMore");
            } catch (Exception e) {
                e.printStackTrace();
            }
            pageIdx++;
            usersRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                callReceiveNewContacts(users, ErrorCode.ERROR_SUCCESS);
            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                callReceiveHisContacts(users, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void callReceiveNewContacts(List<LBSUser> users, int errCode) {
        if (listener != null) {
            listener.onRefreshLBSNearUsers(users, errCode);
        }
    }

    private void callReceiveHisContacts(List<LBSUser> users, int errCode) {
        if (listener != null) {
            listener.onReceiveHisLBSNearUsers(users, last, errCode);
        }
    }

}
