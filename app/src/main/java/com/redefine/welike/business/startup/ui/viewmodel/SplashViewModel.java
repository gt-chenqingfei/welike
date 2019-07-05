package com.redefine.welike.business.startup.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.gson.Gson;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.business.startup.management.ReauthSuggester;
import com.redefine.welike.business.startup.management.SplashSuggester;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.business.startup.management.bean.SplashEntity;
import com.redefine.welike.business.startup.management.callback.DatabaseCallback;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.startup.management.dao.SplashEventStore;
import com.redefine.welike.common.BrowseManager;

import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_DEACTIVATE;
import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_LANG;
import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_LOGIN_MOBILE;
import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_MAIN;
import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_REGISTER_USERINFO;
import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_THIRD_LOGIN;
import static com.redefine.welike.business.startup.management.constant.StartConstant.START_STATE_VIDMATE;


/**
 * Created by honglin on 2018/5/9.
 */

public class SplashViewModel extends AndroidViewModel implements DatabaseCallback, SplashSuggester.SplashSuggesterCallback {

    private MutableLiveData<SplashEntity> mSplashEntity = new MutableLiveData<>();
    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> mStatus = new MutableLiveData<>();

    private SplashSuggester splashSuggester;
//    private TopicCardsManager topicCardsManager;
    private ReauthSuggester reauthSuggester;


    public SplashViewModel(@NonNull Application application) {
        super(application);
        SplashEventStore.INSTANCE.init(application);
        splashSuggester = new SplashSuggester();
//        topicCardsManager = new TopicCardsManager();
        reauthSuggester = new ReauthSuggester();
    }

    public MutableLiveData<SplashEntity> getmSplashEntity() {
        return mSplashEntity;
    }

    public MutableLiveData<PageStatusEnum> getmPageStatus() {
        return mPageStatus;
    }

    public MutableLiveData<Integer> getStatus() {
        return mStatus;
    }

    public void getSplashData() {

        SplashEventStore.INSTANCE.getSplashBean(this);

        splashSuggester.reqSplash(this);
    }

    public void getTopicData() {
//        topicCardsManager.reqTopicCards(this);
    }

    /**
     * 确定跳转
     */
    public void getCurrentAccountStatus() {//
        String mainLanguage = LanguageSupportManager.getInstance().getCurrentMenuLanguageType();
        if (!TextUtils.isEmpty(mainLanguage)) {
            if (AccountManager.getInstance().isLogin()) {
                StartManager.getInstance().accountTasksInitialized(AccountManager.getInstance().getAccount(), true);
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        resumeRegistered();
                    }
                });
            } else {

                if (BrowseManager.isVidMate()) {
                    mStatus.postValue(START_STATE_VIDMATE);
                } else {
                    mStatus.postValue(START_STATE_THIRD_LOGIN);
                    StartEventManager.getInstance().setPage_source(1);
                }
            }
        } else {
            mStatus.postValue(START_STATE_LANG);
            StartEventManager.getInstance().setPage_source(1);
        }
    }

    /**
     * 确定跳转
     */
    public void getCurrentAccountStatus2Login() {//
        String mainLanguage = LanguageSupportManager.getInstance().getCurrentMenuLanguageType();
        if (!TextUtils.isEmpty(mainLanguage)) {
            if (AccountManager.getInstance().isLogin()) {
                StartManager.getInstance().accountTasksInitialized(AccountManager.getInstance().getAccount(), true);
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        resumeRegistered();
                    }
                });
            } else {

                mStatus.postValue(START_STATE_THIRD_LOGIN);
                StartEventManager.getInstance().setPage_source(1);
            }
        } else {
            mStatus.postValue(START_STATE_LANG);
            StartEventManager.getInstance().setPage_source(1);
        }
    }

    private void resumeRegistered() {
        Account account = AccountManager.getInstance().getAccount();
        if (account == null) {
            mStatus.postValue(START_STATE_VIDMATE);

        } else {
            if (account.getCompleteLevel() == Account.PROFILE_COMPLETE_LEVEL_BASE) {

                mStatus.postValue(START_STATE_REGISTER_USERINFO);

            } else {

                if (account.getStatus() == Account.ACCOUNT_DEACTIVATE) {
                    mStatus.postValue(START_STATE_DEACTIVATE);
                } else {
                    tryMain();
                }
            }
        }
    }


    private void tryMain() {
        Account account = AccountManager.getInstance().getAccount();
        account.setCompleteLevel(Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE);
        AccountManager.getInstance().updateAccount(account);

        long expired = account.getExpired() - StartConstant.TOKEN_REFRESH_INTERVAL;
        long now = new Date().getTime();
        if (now < expired) {
            mStatus.postValue(START_STATE_MAIN);
        } else {
            doReauth(account.getRefreshToken());
        }
    }

    private void doReauth(String refreshToken) {
        reauthSuggester.reqReAuth(refreshToken, new ReauthSuggester.ReAuthSuggesterCallback() {
            @Override
            public void onReAuthDone(JSONObject result, int errorCode) {
                if (errorCode == ErrorCode.ERROR_SUCCESS) {
                    String accessToken = result.getString("access_token");
                    String tokenType = result.getString("token_type");
                    long expires = result.getLongValue("expires_in");
                    String refreshToken = result.getString("refresh_token");
                    Account account = AccountManager.getInstance().getAccount();
                    account.setAccessToken(accessToken);
                    account.setTokenType(tokenType);
                    Date now = new Date();
                    account.setExpired(now.getTime() + expires * 1000);
                    account.setRefreshToken(refreshToken);
                    AccountManager.getInstance().updateAccount(account);
                    mStatus.postValue(START_STATE_MAIN);
                } else {
                    if (errorCode == ErrorCode.ERROR_NETWORK_AUTH_PASSWORD) {
                        mStatus.postValue(START_STATE_LOGIN_MOBILE);
                    } else {
                        mStatus.postValue(START_STATE_MAIN);
                    }
                }
            }
        });

    }


    @Override
    public void onSplashDone(List<String> list, int errorCode) {
        if (errorCode == ErrorCode.ERROR_SUCCESS) {
            try {
                SplashEntity splashEntity;
                SplashEventStore.INSTANCE.delete();
                for (String splash : list) {
                    Gson gson = new Gson();
                    splashEntity = gson.fromJson(splash, SplashEntity.class);
                    if (TextUtils.isEmpty(splashEntity.getId())) splashEntity.setId("-1");
                    SplashEventStore.INSTANCE.insert(splashEntity);

                    // TODO: 2018/6/1
                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
                    ImageRequest imageRequest1 = ImageRequest.fromUri(splashEntity.getImgUrl());
                    imagePipeline.prefetchToBitmapCache(imageRequest1, this.getApplication());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    @Override
    public void onLoadEntity(SplashEntity splashEntity) {

        if (splashEntity == null) {
            mPageStatus.postValue(PageStatusEnum.EMPTY);
        } else {
            mPageStatus.postValue(PageStatusEnum.CONTENT);
            mSplashEntity.postValue(splashEntity);
        }
    }

//    @Override
//    public void onTopicCardsDone(List<String> list, int errorCode) {
//        if (null != list) {
//            for (String topic : list) {
//                TopicCardInfo topicCardInfo;
//                Gson gson = new Gson();
//                topicCardInfo = gson.fromJson(topic, TopicCardInfo.class);
//                if (null != topicCardInfo) {
//                    MyApplication.mTopicInfos.put(topicCardInfo.id, topicCardInfo);
//
//                }
//            }
//        }
//    }
}