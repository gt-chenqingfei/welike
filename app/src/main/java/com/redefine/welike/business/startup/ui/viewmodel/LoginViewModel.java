package com.redefine.welike.business.startup.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.FollowUsersCallBack;
import com.redefine.welike.business.browse.management.dao.LikeListCallBack;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.cache.SearchHistoryCache;
import com.redefine.welike.business.publisher.management.cache.TopicSearchHistoryCache;
import com.redefine.welike.business.startup.management.PhoneNumberProvider;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.business.startup.management.WelikeLoginManager;
import com.redefine.welike.business.startup.management.WelikeUpdateManager;
import com.redefine.welike.business.startup.management.bean.InterestsBean;
import com.redefine.welike.business.startup.management.bean.ResultBean;
import com.redefine.welike.business.startup.management.callback.LoginCallback;
import com.redefine.welike.business.startup.management.callback.UpdateCallback;
import com.redefine.welike.business.startup.management.request.CheckLoginStateRequest;
import com.redefine.welike.business.user.management.ContactsManager;
import com.redefine.welike.common.Life;
import com.redefine.welike.sync.SyncProfileSettingsHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author redefine honlin
 * @Date on 2018/11/20
 * @Description
 */
public class LoginViewModel extends AndroidViewModel implements LoginCallback, UpdateCallback {

    public enum LoginStatus {
        LOGIN_ING, LOGIN_SUCCESS, LOGIN_FAIL
    }

    private WelikeLoginManager welikeLoginManager = new WelikeLoginManager();
    private WelikeUpdateManager updateManager = new WelikeUpdateManager();

    private MutableLiveData<LoginStatus> loginStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> mCodeStatus = new MutableLiveData<>();

    private boolean isRegisting = false;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<LoginStatus> getLoginStatus() {
        return loginStatus;
    }

    public MutableLiveData<Integer> getCodeStatus() {
        return mCodeStatus;
    }

    public void tryLogin(String nickName) {

        if (isRegisting) return;

        isRegisting = true;
        loginStatus.postValue(LoginStatus.LOGIN_ING);
        try {

            new CheckLoginStateRequest(getApplication()).request(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    isRegisting = false;
                    loginStatus.postValue(LoginStatus.LOGIN_FAIL);
                    mCodeStatus.postValue(errCode);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    isRegisting = false;

                    loginSuccess(result);

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            isRegisting = false;
            loginStatus.postValue(LoginStatus.LOGIN_FAIL);
            mCodeStatus.postValue(ErrorCode.networkExceptionToErrCode(e));
        }

    }

    public void loginFacebook(String facebookToken) {
        loginStatus.postValue(LoginStatus.LOGIN_ING);
        welikeLoginManager.reqFacebookLogin(facebookToken, this);
    }

    public void loginGoogle(String googleToken) {
        loginStatus.postValue(LoginStatus.LOGIN_ING);
        welikeLoginManager.reqGoogleLogin(googleToken, this);
    }

    public void loginTrueCaller(String payload, String signature, String signatureAlgorithm) {
        loginStatus.postValue(LoginStatus.LOGIN_ING);
        welikeLoginManager.reqTrueCallerLogin(payload, signature, signatureAlgorithm, this);
    }


    @Override
    public void onLoginCallback(@NotNull JSONObject result, int loginType, int errorCode) {


        if (errorCode == ErrorCode.ERROR_SUCCESS) {

            loginSuccess(result);

        } else {

            loginStatus.postValue(LoginStatus.LOGIN_FAIL);
            mCodeStatus.postValue(errorCode);
        }

    }

    @Override
    public void onUpdateCallback(@NotNull JSONObject result, int updateType, int errorCode) {
//        if (errorCode == ErrorCode.ERROR_SUCCESS) {
//            loginSuccess(result);
//        } else {
//            loginStatus.postValue(LoginStatus.LOGIN_FAIL);
//            mCodeStatus.postValue(errorCode);
//        }
    }

    private void loginSuccess(JSONObject result) {


        Gson gson = new Gson();
        ResultBean resultBean = gson.fromJson(result.toString(), ResultBean.class);

        if (!result.containsKey("sex") && resultBean.getUserinfo() != null) {
            resultBean.getUserinfo().setSex((byte) -1);
        }

        if (loginReady(resultBean)) {
            if (AccountManager.getInstance().getAccount().getCompleteLevel() == Account.PROFILE_COMPLETE_LEVEL_BASE_USERINFO
                    || AccountManager.getInstance().getAccount().getCompleteLevel() == Account.PROFILE_COMPLETE_LEVEL_INTEREST) {
                Account account = AccountManager.getInstance().getAccount();
                account.setCompleteLevel(Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE);
                AccountManager.getInstance().updateAccount(account);
                AccountManager.getInstance().modifyAccount(account);
            }
            Life.login();
            tryFollowUsers();
            tryLikePost();
            loginStatus.postValue(LoginStatus.LOGIN_SUCCESS);
            mCodeStatus.postValue(ErrorCode.ERROR_SUCCESS);
        } else {
            loginStatus.postValue(LoginStatus.LOGIN_FAIL);
        }


    }

    private boolean loginReady(ResultBean resultBean) {
        boolean isHasImCursor = SpManager.ImSp.hasKey(SpManager.imMessagesStampKeyName, MyApplication.getAppContext());
        if (!TextUtils.isEmpty(resultBean.getAccess_token()) &&
                !TextUtils.isEmpty(resultBean.getToken_type()) &&
                !TextUtils.isEmpty(resultBean.getRefresh_token()) &&
                (resultBean.getExpires_in() > 0) &&
                !TextUtils.isEmpty(resultBean.getUserinfo().getId())) {
            Account account = AccountManager.getInstance().getAccount();
            if (account == null) {
                account = new Account();
            } else if (!account.isLogin()) {
                if (!TextUtils.equals(account.getUid(), resultBean.getUserinfo().getId())) {
                    ContactsManager.getInstance().clear();
//                    DraftManager.getInstance().clear();
                    SearchHistoryCache.getInstance().cleanAll();
                    TopicSearchHistoryCache.getInstance().cleanAll();
                    SpManager.getInstance().clear(MyApplication.getAppContext());
                    account = new Account();
                }
            }
            account.setUid(resultBean.getUserinfo().getId());

            account.setCompleteLevel(resultBean.getUserinfo().getFinishLevel());
            account.setAccessToken(resultBean.getAccess_token());
            account.setTokenType(resultBean.getToken_type());
            Date now = new Date();
            account.setExpired(now.getTime() + resultBean.getExpires_in() * 1000);
            account.setRefreshToken(resultBean.getRefresh_token());
            account.setNickName(resultBean.getUserinfo().getNickName());
            account.setHeadUrl(resultBean.getUserinfo().getAvatarUrl());
            account.setSex(resultBean.getUserinfo().getSex());
            account.setAllowUpdateNickName(resultBean.getUserinfo().isAllowUpdateNickName());
            account.setAllowUpdateSex(resultBean.getUserinfo().isAllowUpdateSex());
            account.setNextUpdateNickNameDate(resultBean.getUserinfo().getNextUpdateNickNameDate());
            account.setSexUpdateCount(resultBean.getUserinfo().getSexUpdateCount());
            account.setPostsCount(resultBean.getUserinfo().getPostsCount());
            account.setFollowUsersCount(resultBean.getUserinfo().getFollowUsersCount());
            account.setFollowedUsersCount(resultBean.getUserinfo().getFollowedUsersCount());
            account.setLikedMyPostsCount(resultBean.getUserinfo().getLikedCount());
            account.setMyLikedPostsCount(resultBean.getUserinfo().getLikePostsCount());
            if (resultBean.getUserinfo().getInterests() != null) {
                account.setIntrests(getSelectInterest(resultBean.getUserinfo().getInterests()));
            }

            account.setFirst(resultBean.getUserinfo().getFirstLogin());
            account.setLogin(true);
            account.setStatus(resultBean.getUserinfo().getStatus());
            account.setVip(resultBean.getUserinfo().getVip());
            AccountManager.getInstance().updateAccount(account);
            StartManager.getInstance().accountTasksInitialized(account, false);
            if (!TextUtils.isEmpty(resultBean.getUserinfo().getSettings()) && !isHasImCursor) {
                SyncProfileSettingsHelper.saveToLocal(resultBean.getUserinfo().getSettings());
            }
            if (!TextUtils.isEmpty(StartEventManager.getInstance().getPhone())) {
                PhoneNumberProvider.saveLoginPhone(MyApplication.getAppContext(), "+" + StartEventManager.getInstance().getPhone_location() + StartEventManager.getInstance().getPhone());
            }

        } else {

            return false;
        }

        return true;
    }

    private List<UserBase.Intrest> getSelectInterest(List items) {

        List<UserBase.Intrest> list = new ArrayList<>();
        UserBase.Intrest intrest2;
        for (Object obj : items) {
            if (obj instanceof InterestsBean) {
                InterestsBean interest = (InterestsBean) obj;
                intrest2 = new UserBase.Intrest();
                intrest2.setLabel(interest.getName());
                intrest2.setIid(interest.getId());
                intrest2.setIcon("");
                list.add(intrest2);
                if (interest.getSubset() != null && interest.getSubset().size() > 0) {
                    list.addAll(getSelectInterest(interest.getSubset()));
                }
            }
        }

        return list;
    }

    private void tryFollowUsers() {

        BrowseEventStore.INSTANCE.getFollowUsersList(new FollowUsersCallBack() {
            @Override
            public void onLoadEntity(@NotNull List<String> users) {
                Account account = AccountManager.getInstance().getAccount();
                if (account != null) {
                    account.setFollowUsersCount(account.getFollowUsersCount() + users.size());
                }
                updateManager.reqFollowUsers(users, LoginViewModel.this);
                BrowseEventStore.INSTANCE.delete();
            }
        });
    }

    private void tryLikePost() {

        BrowseEventStore.INSTANCE.getLostLikeList(new LikeListCallBack() {
            @Override
            public void onLoadEntity(@NotNull List<String> pids) {
                for (String pid : pids) {
                    SinglePostManager.like(pid);
                }


                BrowseEventStore.INSTANCE.deleteLikeData();
            }
        });

    }

}
