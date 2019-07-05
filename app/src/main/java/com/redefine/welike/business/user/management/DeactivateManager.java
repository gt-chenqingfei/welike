package com.redefine.welike.business.user.management;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.business.user.management.bean.DeactivateInfoBean;
import com.redefine.welike.business.user.management.bean.DeactivateReasonBean;
import com.redefine.welike.business.user.management.parser.DeactivateUserInfoParser;
import com.redefine.welike.business.user.management.request.CheckUserRequest;
import com.redefine.welike.business.user.management.request.DeactivateInfoRequest;
import com.redefine.welike.business.user.management.request.DeactivateReasonRequest;
import com.redefine.welike.business.user.management.request.DeactivateRequest;
import com.redefine.welike.business.user.management.request.RestoreRequest;
import com.redefine.welike.business.user.ui.listener.IAccountStatusChangeListener;
import com.redefine.welike.business.user.ui.listener.IDeactivateCheckListener;
import com.redefine.welike.business.user.ui.listener.IReasonCallBackListener;
import com.redefine.welike.business.user.ui.listener.IUserInfoCallBackListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by honglin on 2018/5/15.
 */

public class DeactivateManager {

    private String TAG = "hl";

    private String CHECK_RESULT = "checkResult";

    private static class DeactivateManagerHolder {
        public static DeactivateManager instance = new DeactivateManager();
    }

    public static DeactivateManager getInstance() {
        return DeactivateManagerHolder.instance;
    }


    public void check(String checkInfo, final int type, final IDeactivateCheckListener listener) {
        CheckUserRequest request = new CheckUserRequest(MyApplication.getAppContext());

        try {
            request.check(checkInfo, type, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCheckInfo(false, type);
                        }
                    });

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

                    final JSONObject result1 = result;

                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (result1 != null && result1.containsKey(CHECK_RESULT)) {

                                listener.onCheckInfo(result1.getBooleanValue(CHECK_RESULT), type);

                            } else
                                listener.onCheckInfo(false, type);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    listener.onCheckInfo(false, type);
                }
            });
        }
    }

    public void check(String payload, String signature, String signatureAlgorithm, final IDeactivateCheckListener listener) {
        CheckUserRequest request = new CheckUserRequest(MyApplication.getAppContext());
        try {
            request.check(payload, signature, signatureAlgorithm, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCheckInfo(false, CheckUserRequest.TYPE_TRUE_CALLER);
                        }
                    });
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    final JSONObject result1 = result;

                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            if (result1 != null && result1.containsKey(CHECK_RESULT)) {

                                listener.onCheckInfo(result1.getBooleanValue(CHECK_RESULT), CheckUserRequest.TYPE_TRUE_CALLER);

                            } else
                                listener.onCheckInfo(false, CheckUserRequest.TYPE_TRUE_CALLER);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    listener.onCheckInfo(false, CheckUserRequest.TYPE_TRUE_CALLER);
                }
            });
        }
    }

    public void getReason(final IReasonCallBackListener listener) {

        try {
            DeactivateReasonRequest request = new DeactivateReasonRequest(MyApplication.getAppContext());
            request.getDeactivateReason(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    if (listener != null)
                        listener.onReason(null, errCode);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    if (listener != null) {
                        Gson gson = new Gson();
                        Type founderListType = new TypeToken<ArrayList<DeactivateReasonBean>>(){}.getType();
                        List<DeactivateReasonBean> list =   gson.fromJson(result.getJSONArray("list").toString(), founderListType);
                        listener.onReason(list, ErrorCode.ERROR_SUCCESS);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null)
                listener.onReason(null, ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public void getInfo(final IUserInfoCallBackListener listener) {

        try {
            DeactivateInfoRequest request = new DeactivateInfoRequest(MyApplication.getAppContext());
            request.getDeactivateReason(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    if (listener != null)
                        listener.onResult(null, errCode);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    if (listener != null) {
                        DeactivateInfoBean deactivateInfoBean = DeactivateUserInfoParser.parseData(result);
                        listener.onResult(deactivateInfoBean, ErrorCode.ERROR_SUCCESS);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null)
                listener.onResult(null, ErrorCode.networkExceptionToErrCode(e));
        }
    }

    public void updataAccountStatis(List<Integer> ids, final IAccountStatusChangeListener listener) {

        try {
            DeactivateRequest request = new DeactivateRequest(MyApplication.getAppContext());
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {

                request.postDeactivateAccount(account.getUid(), 2, ids, new RequestCallback() {
                    @Override
                    public void onError(BaseRequest request, int errCode) {
                        if (listener != null)
                            listener.onChanged(0, errCode);
                    }

                    @Override
                    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                        if (listener != null) {
                            listener.onChanged(1, ErrorCode.ERROR_SUCCESS);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null)
                listener.onChanged(0, ErrorCode.networkExceptionToErrCode(e));
        }
    }


    public void updataAccountStatus(final int type, final IAccountStatusChangeListener listener) {

        try {
            RestoreRequest request = new RestoreRequest(MyApplication.getAppContext());
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {

                request.restoreAccount(type, new RequestCallback() {
                    @Override
                    public void onError(BaseRequest request, int errCode) {
                        if (listener != null)
                            listener.onChanged(0, errCode);
                    }

                    @Override
                    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                        if (listener != null) {
                            StartManager.getInstance().restore();
                            if (type == RestoreRequest.TYPE_NEW_ACCOUNT)
                                StartManager.getInstance().restoreReady(result);
                            listener.onChanged(type, ErrorCode.ERROR_SUCCESS);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null)
                listener.onChanged(0, ErrorCode.networkExceptionToErrCode(e));
        }
    }

}
