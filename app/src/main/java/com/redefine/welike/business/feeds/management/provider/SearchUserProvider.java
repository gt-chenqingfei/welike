package com.redefine.welike.business.feeds.management.provider;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.SearchUserRequest;
import com.redefine.welike.business.browse.management.request.SearchUserRequest1;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/2/12.
 */

public class SearchUserProvider implements RequestCallback {
    private BaseRequest searchUserRequest;
    private String keyword;
    private int pageNum;
    private SearchUserProviderCallback listener;

    public interface SearchUserProviderCallback {

        void onNewSearchUsers(final List<User> users, final int errCode);

        void onMoreSearchUsers(final List<User> users, final boolean last, final int errCode);

    }

    public void setListener(SearchUserProviderCallback listener) {
        this.listener = listener;
    }

    public void tryNewSearchUsers(String keyword, boolean auth) {
        if (searchUserRequest != null) {
            searchUserRequest.cancel();
            searchUserRequest = null;
        }

        pageNum = 0;
        this.keyword = keyword;
        if (!TextUtils.isEmpty(keyword)) {
            if (auth) {
                searchUserRequest = new SearchUserRequest(keyword, pageNum, MyApplication.getAppContext());
            } else {
                searchUserRequest = new SearchUserRequest1(keyword, pageNum, MyApplication.getAppContext());
            }
            try {
                searchUserRequest.req(this);
            } catch (Exception e) {
                e.printStackTrace();
                searchUserRequest = null;
                if (listener != null) {
                    listener.onNewSearchUsers(null, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            if (listener != null) {
                listener.onNewSearchUsers(null, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    public void tryMoreSearchUsers(boolean auth) {
        if (searchUserRequest != null) return;

        pageNum++;
        if (auth) {
            searchUserRequest = new SearchUserRequest(keyword, pageNum, MyApplication.getAppContext());
        } else {
            searchUserRequest = new SearchUserRequest1(keyword, pageNum, MyApplication.getAppContext());
        }
        try {
            searchUserRequest.req(this);
        } catch (Exception e) {
            e.printStackTrace();
            searchUserRequest = null;
            if (listener != null) {
                listener.onMoreSearchUsers(null, false, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void onError(BaseRequest request, int errCode) {
        if (searchUserRequest == request) {
            searchUserRequest = null;
            final int error = errCode;
            if (pageNum == 0) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onNewSearchUsers(null, error);
                        }
                    }

                });
            } else {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onMoreSearchUsers(null, false, error);
                        }
                    }

                });
            }
        }
    }

    @Override
    public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
        if (searchUserRequest == request) {
            searchUserRequest = null;
            final List<User> users = UserParser.parseUsers(result);
            if (pageNum == 0) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onNewSearchUsers(users, ErrorCode.ERROR_SUCCESS);
                        }
                    }

                });
            } else {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        if (users != null && users.size() > 0) {
                            if (listener != null) {
                                listener.onMoreSearchUsers(users, users.size() != 10, ErrorCode.ERROR_SUCCESS);
                            }
                        } else {
                            if (listener != null) {
                                listener.onMoreSearchUsers(null, true, ErrorCode.ERROR_SUCCESS);
                            }
                        }
                    }

                });
            }
        }
    }
}
