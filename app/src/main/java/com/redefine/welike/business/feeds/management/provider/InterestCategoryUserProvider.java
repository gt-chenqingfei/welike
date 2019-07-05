package com.redefine.welike.business.feeds.management.provider;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.common.UsersProviderBase;
import com.redefine.welike.business.feeds.management.request.InterestCategoryUserRequest;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by nianguowang on 2018/4/16
 */
public class InterestCategoryUserProvider extends UsersProviderBase implements RequestCallback {

    private int index;
    private int actionState = GlobalConfig.LIST_ACTION_NONE;

    private InterestCategoryUserRequest userRequest;
    private InterestUserCallback listener;

    public interface InterestUserCallback {

        void onRefreshUsers(final List<User> users, final int newCount, final int errCode);

        void onReceiveHisUsers(final List<User> users, final boolean last, final int errCode);
    }

    public void setListener(InterestUserCallback listener) {
        this.listener = listener;
    }

    public void refresh(String tag) {
        actionState = GlobalConfig.LIST_ACTION_REFRESH;
        index = 0;
        cacheList.clear();

        userRequest = new InterestCategoryUserRequest(MyApplication.getAppContext());

        try {
            userRequest.refresh(tag, index, this);
        } catch (Exception e) {
            e.printStackTrace();
            userRequest = null;
            if (listener != null) {
                listener.onRefreshUsers(null, 0, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    public void his(String tag) {
        if (userRequest != null) return;

        actionState = GlobalConfig.LIST_ACTION_HIS;

        index++;
        userRequest = new InterestCategoryUserRequest(MyApplication.getAppContext());
        try {
            userRequest.his(tag, index, this);
        } catch (Exception e) {
            e.printStackTrace();
            userRequest = null;
        }
    }

    @Override
    public void onError(BaseRequest request, final int errCode) {
        if (request == userRequest) {
            userRequest = null;
            if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onReceiveHisUsers(null, false, errCode);
                        }
                    }

                });

            } else if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onRefreshUsers(null, 0, errCode);
                        }
                    }

                });
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (request == userRequest) {
            userRequest = null;
            final List<User> users = UserParser.parseUsers(result);

            final List<User> list = filterUsers(users);
            if (actionState == GlobalConfig.LIST_ACTION_REFRESH) {
                final int newCount = refreshNewCount(list);
                cacheFirstPage(list);
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onRefreshUsers(users, newCount, ErrorCode.ERROR_SUCCESS);
                        }
                    }

                });

            } else if (actionState == GlobalConfig.LIST_ACTION_HIS) {
                if (listener != null) {
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                        @Override
                        public void run() {
                            if (CollectionUtil.isEmpty(list)) {
                                if (listener != null) {
                                    listener.onReceiveHisUsers(list, true, ErrorCode.ERROR_SUCCESS);
                                }
                            } else {
                                if (listener != null) {
                                    listener.onReceiveHisUsers(list, list.size() < GlobalConfig.INTEREST_USER_MORE_PAGE, ErrorCode.ERROR_SUCCESS);
                                }
                            }
                        }
                    });

                }
            }
        }
    }
}
