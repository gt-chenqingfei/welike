package com.redefine.welike.business.location.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.location.management.bean.LBSNearInfo;
import com.redefine.welike.business.location.management.bean.LBSUser;
import com.redefine.welike.business.location.management.parser.LBSParser;
import com.redefine.welike.business.location.management.request.LBSNearUsersRequest;
import com.redefine.welike.business.location.management.request.LBSOneNearInfoRequest;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/3/21.
 */

public class LBSNearInfoGetter extends SingleListenerManagerBase implements RequestCallback {
    private LBSOneNearInfoRequest nearInfoRequest;
    private LBSNearUsersRequest nearUsersRequest;
    private LBSNearInfo nearInfo;
    private String placeId;

    public interface LBSNearInfoGetterListener {

        void onLBSNearInfoGetterCompleted(final LBSNearInfo nearInfo, final List<LBSUser> userList);
        void onLBSNearInfoGetterFailed(int errCode);

    }

    public void setListener(LBSNearInfoGetterListener listener) {
        super.setListener(listener);
    }

    public void refresh(String placeId) {
        if (nearInfoRequest != null) return;

        nearInfo = null;
        this.placeId = placeId;
        nearInfoRequest = new LBSOneNearInfoRequest(placeId, MyApplication.getAppContext());
        try {
            nearInfoRequest.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            nearInfoRequest = null;
            callLBSNearInfoGetterFailed(ErrorCode.networkExceptionToErrCode(e));
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (nearInfoRequest == request) {
            nearInfoRequest = null;
            callLBSNearInfoGetterFailed(errCode);
        } else if (nearUsersRequest == request) {
            nearUsersRequest = null;
            callLBSNearInfoGetterFailed(errCode);
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (nearInfoRequest == request) {
            nearInfoRequest = null;

            int feedCount = 0;
            int userCount = 0;
            String photo = null;
            String placeNameFromNet = "";

            try {
                feedCount = result.getIntValue("feedCount");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                userCount = result.getIntValue("userCount");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                photo = result.getString("photo");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                placeNameFromNet = result.getString("placeName");
            } catch (Exception e) {
                e.printStackTrace();
            }
            nearInfo = new LBSNearInfo();
            nearInfo.setPlaceId(placeId);
            nearInfo.setPlace(placeNameFromNet);
            nearInfo.setFeedCount(feedCount);
            nearInfo.setUserCount(userCount);
            nearInfo.setPhoto(photo);

            nearUsersRequest = new LBSNearUsersRequest(MyApplication.getAppContext());
            try {
                nearUsersRequest.reviewList(nearInfo.getPlaceId(), this);
            } catch (Exception e) {
                e.printStackTrace();
                nearUsersRequest = null;
                callLBSNearInfoGetterFailed(ErrorCode.networkExceptionToErrCode(e));
            }
        } else if (nearUsersRequest == request) {
            nearUsersRequest = null;
            List<LBSUser> lbsUsers = LBSParser.parserLBSUser(result);
            callLBSNearInfoGetterCompleted(nearInfo, lbsUsers);
        }
    }

    private LBSNearInfoGetterListener getCallback() {
        LBSNearInfoGetterListener callback = null;
        Object l = getListener();
        if (l != null && l instanceof LBSNearInfoGetterListener) {
            callback = (LBSNearInfoGetterListener)l;
        }
        return callback;
    }

    private void callLBSNearInfoGetterCompleted(final LBSNearInfo nearInfo, final List<LBSUser> userList) {
        final LBSNearInfoGetterListener listener = getCallback();
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                if (listener != null) {
                    listener.onLBSNearInfoGetterCompleted(nearInfo, userList);
                }
            }

        });
    }

    private void callLBSNearInfoGetterFailed(final int errCode) {
        final LBSNearInfoGetterListener listener = getCallback();
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                if (listener != null) {
                    listener.onLBSNearInfoGetterFailed(errCode);
                }
            }

        });
    }

}
