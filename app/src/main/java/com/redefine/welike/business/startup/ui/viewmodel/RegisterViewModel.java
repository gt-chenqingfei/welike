package com.redefine.welike.business.startup.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.NickNameChecker;
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
import com.redefine.welike.business.startup.management.WelikeUpdateManager;
import com.redefine.welike.business.startup.management.bean.InterestsBean;
import com.redefine.welike.business.startup.management.bean.NickNameCheckerBean;
import com.redefine.welike.business.startup.management.bean.ResultBean;
import com.redefine.welike.business.startup.management.callback.UpdateCallback;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.management.request.CheckLoginStateRequest;
import com.redefine.welike.business.startup.management.request.HalfLoginRequest;
import com.redefine.welike.business.user.management.ContactsManager;
import com.redefine.welike.common.Life;
import com.redefine.welike.sync.SyncProfileSettingsHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author redefine honlin
 * @Date on 2018/10/30
 * @Description
 */
public class RegisterViewModel extends AndroidViewModel implements UpdateCallback {

    private String TAG = "honlin";

    private NickNameChecker nickNameChecker = new NickNameChecker();
    private WelikeUpdateManager updateManager;
    private SinglePostManager singlePostManager =  SinglePostManager.getInstance();


    //CHECK nick
    private MutableLiveData<NickNameCheckerBean> nickNameBeanData = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> checkStatus = new MutableLiveData<>();

    //CREATE
    private boolean isRegisting = false;
    private MutableLiveData<PageStatusEnum> loginStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> mCodeStatus = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        updateManager = new WelikeUpdateManager();
    }


    public MutableLiveData<NickNameCheckerBean> getNickNameBeanData() {
        return nickNameBeanData;
    }

    public MutableLiveData<PageStatusEnum> getCheckStatus() {
        return checkStatus;
    }

    public MutableLiveData<PageStatusEnum> getLoginStatus() {
        return loginStatus;
    }

    public MutableLiveData<Integer> getCodeStatus() {
        return mCodeStatus;
    }

    public void checkLoginState() {


        loginStatus.postValue(PageStatusEnum.LOADING);

        try {

            new CheckLoginStateRequest(getApplication()).request(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    loginStatus.postValue(PageStatusEnum.ERROR);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {
                    Gson gson = new Gson();
                    ResultBean resultBean = gson.fromJson(result.toString(), ResultBean.class);

                    if (!result.containsKey("sex") && resultBean.getUserinfo() != null) {
                        resultBean.getUserinfo().setSex((byte) -1);
                    }

                    if (loginReady(resultBean)) {
                        Life.login();
                        tryFollowUsers();
                        tryLikePost();
                        loginStatus.postValue(PageStatusEnum.CONTENT);
                        mCodeStatus.postValue(ErrorCode.ERROR_SUCCESS);
                    } else {
                        loginStatus.postValue(PageStatusEnum.ERROR);
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            mCodeStatus.postValue(ErrorCode.networkExceptionToErrCode(e));
            checkStatus.postValue(PageStatusEnum.CONTENT);
        }


    }


    public void checkNickName(final String nick) {

        checkStatus.postValue(PageStatusEnum.LOADING);

        nickNameChecker.check2(nick, getApplication(), new NickNameChecker.NickNameCheckerCallback() {
            @Override
            public void onCheckResult(String result, int errCode) {
                NickNameCheckerBean bean;
                if (errCode == ErrorCode.ERROR_SUCCESS) {
                    Gson gson = new Gson();
                    bean = gson.fromJson(result, NickNameCheckerBean.class);
                    bean.setErrCode(errCode);
                } else {
                    bean = new NickNameCheckerBean(0, false, false, errCode);
                }
                nickNameBeanData.postValue(bean);
                checkStatus.postValue(PageStatusEnum.CONTENT);
            }
        });

    }

    public void tryLogin(String nickName) {

        if (isRegisting) return;

        isRegisting = true;
        loginStatus.postValue(PageStatusEnum.LOADING);
        try {

            new HalfLoginRequest(getApplication()).tryRequest(nickName, new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {

                    isRegisting = false;

                    mCodeStatus.postValue(errCode);
                    loginStatus.postValue(PageStatusEnum.ERROR);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) {

                    isRegisting = false;
                    Gson gson = new Gson();
                    ResultBean resultBean = gson.fromJson(result.toString(), ResultBean.class);

                    if (!result.containsKey("sex") && resultBean.getUserinfo() != null) {
                        resultBean.getUserinfo().setSex((byte) -1);
                    }

                    if (loginReady(resultBean)) {
                        Life.login();
                        tryFollowUsers();
                        tryLikePost();
                        loginStatus.postValue(PageStatusEnum.CONTENT);
                        mCodeStatus.postValue(ErrorCode.ERROR_SUCCESS);
                    } else {
                        loginStatus.postValue(PageStatusEnum.ERROR);
                        mCodeStatus.postValue(ErrorCode.ERROR_LOGIN_FAILED);
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            isRegisting = false;
            loginStatus.postValue(PageStatusEnum.ERROR);
            mCodeStatus.postValue(ErrorCode.ERROR_LOGIN_FAILED);

        }

    }


    @Override
    public void onUpdateCallback(@NotNull JSONObject result, int updateType, int errorCode) {
        if (errorCode == ErrorCode.ERROR_SUCCESS) {
            Account account = AccountManager.getInstance().getAccount();
            AccountManager.getInstance().updateAccount(account);
            if (updateType == RegisteredConstant.LOGIN_UPDATE_FOLLOWS) {
                BrowseEventStore.INSTANCE.delete();
            }
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
                updateManager.reqFollowUsers(users, RegisterViewModel.this);
                BrowseEventStore.INSTANCE.delete();
            }
        });
    }

    private void tryLikePost(){

        BrowseEventStore.INSTANCE.getLostLikeList(new LikeListCallBack() {
            @Override
            public void onLoadEntity(@NotNull List<String> pids) {
                for(String pid :pids ){
                    SinglePostManager.like(pid);
                }


                BrowseEventStore.INSTANCE.deleteLikeData();
            }
        });

    }


}
