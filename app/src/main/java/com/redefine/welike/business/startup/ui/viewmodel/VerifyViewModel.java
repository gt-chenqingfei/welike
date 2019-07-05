package com.redefine.welike.business.startup.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.feeds.management.cache.SearchHistoryCache;
import com.redefine.welike.business.publisher.management.cache.TopicSearchHistoryCache;
import com.redefine.welike.business.startup.management.PhoneNumberProvider;
import com.redefine.welike.business.startup.management.SmsManager;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.business.startup.management.WelikeBindManager;
import com.redefine.welike.business.startup.management.bean.ResultBean;
import com.redefine.welike.business.startup.management.callback.LoginCallback;
import com.redefine.welike.business.startup.management.request.CheckPhoneIsNewRequest;
import com.redefine.welike.business.startup.management.request.VoiceSMSCodeRequest;
import com.redefine.welike.business.user.management.ContactsManager;
import com.redefine.welike.sync.SyncProfileSettingsHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * @author redefine honlin
 * @Date on 2018/10/31
 * @Description
 */
public class VerifyViewModel extends AndroidViewModel implements LoginCallback {


    private WelikeBindManager welikeLoginManager = new WelikeBindManager();

    private MutableLiveData<PageStatusEnum> bindStatus = new MutableLiveData<>();

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> mStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> mCodeStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> checkIsOldPhone = new MutableLiveData<>();

    private SmsManager smsManager = new SmsManager();

    public VerifyViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<PageStatusEnum> getBindStatus() {
        return bindStatus;
    }

    public void setStatus(Integer mStatus) {
        this.mStatus.postValue(mStatus);
    }

    public MutableLiveData<Integer> getStatus() {
        return mStatus;
    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
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


    public void tryBindFacebook(String facebookToken) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        welikeLoginManager.reqFacebookLogin(facebookToken, this);
    }

    public void tryBindGoogle(String googleToken) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        welikeLoginManager.reqGoogleLogin(googleToken, this);
    }

    public void tryBindTrueCaller(String payload, String signature, String signatureAlgorithm) {
        mPageStatus.postValue(PageStatusEnum.LOADING);
        welikeLoginManager.reqTrueCallerLogin(payload, signature, signatureAlgorithm, this);
    }

    @Override
    public void onLoginCallback(@NotNull JSONObject result, int loginType, int errorCode) {

        if (errorCode == ErrorCode.ERROR_SUCCESS) {

            Account account = AccountManager.getInstance().getAccount();
            account.setStatus(Account.ACCOUNT_ACTIVE);
            AccountManager.getInstance().updateAccount(account);
            mPageStatus.postValue(PageStatusEnum.CONTENT);
            mCodeStatus.postValue(ErrorCode.ERROR_SUCCESS);

        } else {
            mPageStatus.postValue(PageStatusEnum.ERROR);
            mCodeStatus.postValue(errorCode);
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


}
