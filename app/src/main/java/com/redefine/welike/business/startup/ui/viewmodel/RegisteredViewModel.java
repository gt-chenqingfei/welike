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
import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.business.browse.management.dao.BrowseEventStore;
import com.redefine.welike.business.browse.management.dao.FollowUsersCallBack;
import com.redefine.welike.business.browse.management.dao.LikeListCallBack;
import com.redefine.welike.business.feeds.management.SinglePostManager;
import com.redefine.welike.business.feeds.management.cache.SearchHistoryCache;
import com.redefine.welike.business.publisher.management.cache.TopicSearchHistoryCache;
import com.redefine.welike.business.startup.management.PhoneNumberProvider;
import com.redefine.welike.business.startup.management.SmsManager;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.business.startup.management.WelikeLoginManager;
import com.redefine.welike.business.startup.management.WelikeUpdateManager;
import com.redefine.welike.business.startup.management.bean.BlockBean;
import com.redefine.welike.business.startup.management.bean.InterestsBean;
import com.redefine.welike.business.startup.management.bean.NickNameCheckerBean;
import com.redefine.welike.business.startup.management.bean.ResultBean;
import com.redefine.welike.business.startup.management.bean.UserinfoBean;
import com.redefine.welike.business.startup.management.callback.LoginCallback;
import com.redefine.welike.business.startup.management.callback.UpdateCallback;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.management.request.CheckPhoneIsNewRequest;
import com.redefine.welike.business.startup.management.request.SkipEditUserInfoRequest;
import com.redefine.welike.business.startup.management.request.VoiceSMSCodeRequest;
import com.redefine.welike.business.user.management.ContactsManager;
import com.redefine.welike.common.Life;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType;
import com.redefine.welike.sync.SyncProfileSettingsHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_DEACTIVATE;
import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_MAIN;
import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_REGISTER_USERINFO;
import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_THIRD_LOGIN;


/**
 * Created by honglin on 2018/6/12.
 */

public class RegisteredViewModel extends AndroidViewModel implements LoginCallback, UpdateCallback {


    public enum LoginStatus {
        LOGIN_SUCCESS, LOGIN_FAIL, UPDATE_INFO_SUCCESS, UPDATE_INFO_FAIL, SKIP_INFO_SUCCESS, SKIP_INFO_FAIL
    }

    private SmsManager smsManager;
    private WelikeLoginManager welikeLoginManager;
    private WelikeUpdateManager updateManager;
    private NickNameChecker nickNameChecker = new NickNameChecker();

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> mStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> mCodeStatus = new MutableLiveData<>();
    private MutableLiveData<LoginStatus> mLoginStatus = new MutableLiveData<>();
    private MutableLiveData<String> mUrl = new MutableLiveData<>();
    private MutableLiveData<Boolean> checkIsOldPhone = new MutableLiveData<>();

    private MutableLiveData<ThirdLoginType> mThirdLoginType = new MutableLiveData<>();

    public RegisteredViewModel(@NonNull Application application) {
        super(application);
        smsManager = new SmsManager();
        welikeLoginManager = new WelikeLoginManager();
        updateManager = new WelikeUpdateManager();
    }

    public MutableLiveData<String> getUrl() {
        return mUrl;
    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<Integer> getStatus() {
        return mStatus;
    }

    public MutableLiveData<ThirdLoginType> getThirdLoginType() {
        return mThirdLoginType;
    }

    public MutableLiveData<LoginStatus> getLoginStatus() {
        return mLoginStatus;
    }

    public void setStatus(Integer mStatus) {
        this.mStatus.postValue(mStatus);
    }

    public MutableLiveData<Integer> getCodeStatus() {
        return mCodeStatus;
    }

    public MutableLiveData<Boolean> getCheckIsOldPhone() {
        return checkIsOldPhone;
    }

    public void reqSMSCode(String mobile, String nationCode) {


        if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(nationCode)) {
            smsManager.reqSmsCode(mobile, nationCode, new SmsManager.SmsManagerCallback() {
                @Override
                public void onSmsCallback(int errorCode) {

                }
            });

        } else {

        }


    }

    public void reqVoiceSMSCode(String mobile, String nationCode) {
        try {
            new VoiceSMSCodeRequest(mobile, nationCode, getApplication()).req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reqCheckPhoneNum(String mobile) {
        try {

            new CheckPhoneIsNewRequest(getApplication(), mobile).req(new RequestCallback
                    () {
                @Override
                public void onError(BaseRequest request, int errCode) {

                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

                    if (result.containsKey("isExist")) {

                        checkIsOldPhone.postValue(result.getBooleanValue("isExist"));

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tryPhoneLogin(String mobile, String smscode) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        welikeLoginManager.reqLogin(mobile, smscode, this);
    }


    public void loginFacebook(String facebookToken) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        welikeLoginManager.reqFacebookLogin(facebookToken, this);
    }

    public void loginGoogle(String googleToken) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        welikeLoginManager.reqGoogleLogin(googleToken, this);
    }

    public void loginTrueCaller(String payload, String signature, String signatureAlgorithm) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        welikeLoginManager.reqTrueCallerLogin(payload, signature, signatureAlgorithm, this);
    }

    /**
     * 更新 account  data
     */
    public void tryUpdateUserInfo() {

        if (!TextUtils.isEmpty(StartEventManager.getInstance().getNickname()) && StartEventManager.getInstance().getSex() != -1) {
            mPageStatus.postValue(PageStatusEnum.LOADING);
            Account account = AccountManager.getInstance().getAccount();
            account.setNickName(StartEventManager.getInstance().getNickname());
            account.setHeadUrl(StartEventManager.getInstance().getHeadUrl());
            account.setSex(StartEventManager.getInstance().getSex());

            updateManager.reqUpdateUserInfo(account, RegisteredConstant.LOGIN_UPDATE_USERINFO, this);
        } else if (TextUtils.isEmpty(StartEventManager.getInstance().getNickname())) {
            mCodeStatus.postValue(ErrorCode.ERROR_USERINFO_NICKNAME_EMPTY);
        } else {
            mCodeStatus.postValue(ErrorCode.ERROR_USERINFO_FAILED);
        }
    }

    public void tryThird(final ThirdLoginType thirdLoginType) {

        mStatus.postValue(START_STATE_THIRD_LOGIN);
        mThirdLoginType = new MutableLiveData<>();
        mThirdLoginType.postValue(thirdLoginType);

    }

    public void trySkip() {

        try {
            mPageStatus.postValue(PageStatusEnum.LOADING);
            new SkipEditUserInfoRequest(getApplication()).req(new RequestCallback() {
                @Override
                public void onError(BaseRequest request, int errCode) {
                    mPageStatus.postValue(PageStatusEnum.ERROR);
                    mCodeStatus.postValue(errCode);
                    mLoginStatus.postValue(LoginStatus.SKIP_INFO_FAIL);
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

                    mPageStatus.postValue(PageStatusEnum.CONTENT);
                    Gson gson = new Gson();
                    UserinfoBean resultBean = gson.fromJson(result.toString(), UserinfoBean.class);

                    if (resultBean != null) {


                        Account account = AccountManager.getInstance().getAccount();
                        if (account == null) {
                            account = new Account();
                        } else if (!account.isLogin()) {
                            if (!TextUtils.equals(account.getUid(), resultBean.getId())) {
                                ContactsManager.getInstance().clear();
//                                DraftManager.getInstance().clear();
                                SearchHistoryCache.getInstance().cleanAll();
                                TopicSearchHistoryCache.getInstance().cleanAll();
                                SpManager.getInstance().clear(MyApplication.getAppContext());
                                account = new Account();
                            }
                        }
                        account.setUid(resultBean.getId());
                        account.setCompleteLevel(resultBean.getFinishLevel());
                        account.setNickName(resultBean.getNickName());
                        account.setHeadUrl(resultBean.getAvatarUrl());
                        account.setSex(resultBean.getSex());
                        account.setAllowUpdateNickName(resultBean.isAllowUpdateNickName());
                        account.setAllowUpdateSex(resultBean.isAllowUpdateSex());
                        account.setNextUpdateNickNameDate(resultBean.getNextUpdateNickNameDate());
                        account.setSexUpdateCount(resultBean.getSexUpdateCount());
                        account.setPostsCount(resultBean.getPostsCount());
                        account.setFollowUsersCount(resultBean.getFollowUsersCount());
                        account.setFollowedUsersCount(resultBean.getFollowedUsersCount());
                        account.setLogin(true);
                        account.setStatus(resultBean.getStatus());
                        account.setVip(resultBean.getVip());
                        StartManager.getInstance().accountTasksInitialized(account, false);
//                        TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder().setCategory(TrackerConstant.EVENT_EV_CT).setAction(TrackerConstant.EVENT_PIN_SUCCESS).build());
                        if (!TextUtils.isEmpty(StartEventManager.getInstance().getPhone())) {
                            PhoneNumberProvider.saveLoginPhone(MyApplication.getAppContext(), "+" + StartEventManager.getInstance().getPhone_location() + StartEventManager.getInstance().getPhone());
                        }
                        Life.login();
                        StartEventManager.getInstance().setLogin_source(0);
                        account.setCompleteLevel(Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE);
                        AccountManager.getInstance().updateAccount(account);
                        mStatus.postValue(resumeRegistered(false, Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE,
                                AccountManager.getInstance().getAccount().getStatus()));
                        mLoginStatus.postValue(LoginStatus.SKIP_INFO_SUCCESS);
                    } else {
                        mPageStatus.postValue(PageStatusEnum.ERROR);
                        mCodeStatus.postValue(ErrorCode.ERROR_LOGIN_FAILED);
                        mLoginStatus.postValue(LoginStatus.SKIP_INFO_FAIL);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mLoginStatus.postValue(LoginStatus.SKIP_INFO_FAIL);
        }
    }

    public void tryFakeSkip() {

        mPageStatus.postValue(PageStatusEnum.LOADING);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);

                    mStatus.postValue(resumeRegistered(false, Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE,
                            AccountManager.getInstance().getAccount().getStatus()));
                    mLoginStatus.postValue(LoginStatus.SKIP_INFO_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }


    /**
     * 更新 account  data
     */
    public void tryUpdateUserInterest(List<UserBase.Intrest> list) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        Account account = AccountManager.getInstance().getAccount();

        account.setIntrests(list);
        updateManager.reqUpdateUserInfo(account, RegisteredConstant.LOGIN_UPDATE_INTEREST, this);
    }

    public void tryFollowUsers(List<String> selectedUids) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        updateManager.reqFollowUsers(selectedUids, this);
    }


    public void tryFollowUsers() {

        BrowseEventStore.INSTANCE.getFollowUsersList(new FollowUsersCallBack() {
            @Override
            public void onLoadEntity(@NotNull List<String> users) {
                Account account = AccountManager.getInstance().getAccount();
                if (account != null) {
                    account.setFollowUsersCount(account.getFollowUsersCount() + users.size());
                }
                updateManager.reqFollowUsers(users, RegisteredViewModel.this);
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
//            TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder().setCategory(TrackerConstant.EVENT_EV_CT).setAction(TrackerConstant.EVENT_PIN_SUCCESS).build());
            if (!TextUtils.isEmpty(StartEventManager.getInstance().getPhone())) {
                PhoneNumberProvider.saveLoginPhone(MyApplication.getAppContext(), "+" + StartEventManager.getInstance().getPhone_location() + StartEventManager.getInstance().getPhone());
            }

        } else {

            return false;
        }

        return true;
    }


    public List<UserBase.Intrest> getSelectInterest(List items) {

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

    private int resumeRegistered(boolean isFirstLogin, int finishLevel, int status) {


        if (isFirstLogin || finishLevel == Account.PROFILE_COMPLETE_LEVEL_BASE) {
            return START_STATE_REGISTER_USERINFO;
        } else {
            if (status == Account.ACCOUNT_DEACTIVATE) {
                return (START_STATE_DEACTIVATE);
            } else
                return (START_STATE_MAIN);
        }


    }

    @Override
    public void onUpdateCallback(@NotNull JSONObject result, int updateType, int errorCode) {
        if (errorCode == ErrorCode.ERROR_SUCCESS) {
            Account account = AccountManager.getInstance().getAccount();
            AccountManager.getInstance().updateAccount(account);
            if (updateType == RegisteredConstant.LOGIN_UPDATE_FOLLOWS) {
                BrowseEventStore.INSTANCE.delete();
            } else if (updateType == RegisteredConstant.LOGIN_UPDATE_USERINFO) {
                mPageStatus.postValue(PageStatusEnum.CONTENT);
                account.setCompleteLevel(Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE);
                AccountManager.getInstance().updateAccount(account);
                AccountManager.getInstance().modifyAccount(account);
                ContactsManager.getInstance().refreshAll();
                mStatus.postValue(START_STATE_MAIN);
                StartEventManager.getInstance().reset();
            }
            mLoginStatus.postValue(LoginStatus.UPDATE_INFO_SUCCESS);
        } else {
            mPageStatus.postValue(PageStatusEnum.ERROR);
            mCodeStatus.postValue(errorCode);
            mLoginStatus.postValue(LoginStatus.UPDATE_INFO_FAIL);
        }


    }

    /**
     * @param result    login success data
     * @param loginType login type: 1->phone     2->facebook    3->google  4->true caller
     */
    @Override
    public void onLoginCallback(@NotNull JSONObject result, int loginType, int errorCode) {
        if (errorCode == ErrorCode.ERROR_SUCCESS) {
            mPageStatus.postValue(PageStatusEnum.CONTENT);
            AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_LOGIN);
            Gson gson = new Gson();
            BlockBean blockBean = gson.fromJson(result.toString(), BlockBean.class);
            if (blockBean.getStatus() != null && blockBean.getStatus() == Account.ACCOUNT_BLOCKED) {
                mUrl.postValue(blockBean.getBlockUrl());
                return;
            }


            ResultBean resultBean = gson.fromJson(result.toString(), ResultBean.class);

            if (!result.containsKey("sex") && resultBean.getUserinfo() != null) {
                resultBean.getUserinfo().setSex((byte) -1);
            }

            if (loginReady(resultBean)) {
                Life.login();

                if (AccountManager.getInstance().getAccount().getCompleteLevel() == Account.PROFILE_COMPLETE_LEVEL_BASE_USERINFO
                        || AccountManager.getInstance().getAccount().getCompleteLevel() == Account.PROFILE_COMPLETE_LEVEL_INTEREST) {
                    Account account = AccountManager.getInstance().getAccount();
                    account.setCompleteLevel(Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE);
                    AccountManager.getInstance().updateAccount(account);
                    AccountManager.getInstance().modifyAccount(account);
                }
                tryFollowUsers();
                tryLikePost();
                mStatus.postValue(resumeRegistered(resultBean.getUserinfo().getFirstLogin(), resultBean.getUserinfo().getFinishLevel(),
                        resultBean.getUserinfo().getStatus()));
                mLoginStatus.postValue(LoginStatus.LOGIN_SUCCESS);
            } else {
                mPageStatus.postValue(PageStatusEnum.ERROR);
                mCodeStatus.postValue(ErrorCode.ERROR_LOGIN_FAILED);
                mLoginStatus.postValue(LoginStatus.LOGIN_FAIL);
            }
        } else {
            mPageStatus.postValue(PageStatusEnum.ERROR);
            mCodeStatus.postValue(errorCode);
            mLoginStatus.postValue(LoginStatus.LOGIN_FAIL);
        }
    }

    //CHECK nick
    private MutableLiveData<NickNameCheckerBean> nickNameBeanData = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> checkStatus = new MutableLiveData<>();


    public MutableLiveData<NickNameCheckerBean> getNickNameBeanData() {
        return nickNameBeanData;
    }

    public MutableLiveData<PageStatusEnum> getCheckStatus() {
        return checkStatus;
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


}
