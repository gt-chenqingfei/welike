package com.redefine.welike.business.feeds.ui.presenter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.constant.WebViewConstant;
import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.commonui.share.sharemedel.ShareModel;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.im.room.SESSION;
import com.redefine.welike.BuildConfig;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.upgraded.UpdateHelper;
import com.redefine.welike.business.common.mission.MissionManager;
import com.redefine.welike.business.common.mission.MissionTask;
import com.redefine.welike.business.feeds.management.bean.UserHonor;
import com.redefine.welike.business.feeds.ui.contract.IMainMineContract;
import com.redefine.welike.business.feeds.ui.util.DefaultUrlRedirectHandler;
import com.redefine.welike.business.im.IMHelper;
import com.redefine.welike.business.im.ui.constant.ImConstant;
import com.redefine.welike.business.startup.management.WelikeBindManager;
import com.redefine.welike.business.startup.management.callback.LoginCallback;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.user.management.FollowerManager;
import com.redefine.welike.business.user.management.UserDetailManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.util.UserGradeRedirectHandler;
import com.redefine.welike.business.user.ui.page.UserFollowActivity;
import com.redefine.welike.business.user.ui.page.UserHostPage;
import com.redefine.welike.common.VipUtil;
import com.redefine.welike.commonui.event.model.EventModel;
import com.redefine.welike.commonui.share.ShareHelper;
import com.redefine.welike.commonui.share.ShareManagerWrapper;
import com.redefine.welike.commonui.share.model.ShareTitleModel;
import com.redefine.welike.commonui.share.model.ShortLinkModel;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.commonui.widget.VipAvatar;
import com.redefine.welike.hive.CommonListener;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.redefine.welike.statistical.manager.NewShareEventManager;
import com.redefine.welike.statistical.pagename.INameParser;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.redefine.welike.business.user.ui.constant.UserConstant.FOLLOWER;
import static com.redefine.welike.business.user.ui.constant.UserConstant.FOLLOWING;


/**
 * @author gongguan
 * @time 2018/1/13 下午6:58
 */

public class MainMinePresenter implements IMainMineContract.IMainMinePresenter,
        View.OnClickListener, UserDetailManager.UserDetailCallback, SpManager.SPDataListener,
        FollowerManager.GetFollowersCallback, CommonListener<MissionTask>,
        LoginCallback {
    private IMainMineContract.IMainMineView mMineView;
    private View ll_mine;
    private LinearLayout ll_post, ll_follow, ll_follower, ll_setting, ll_myLike, mLlFeedBack, llShare, llContact;
    private VipAvatar simpleDraw_mine_headView;
    private TextView tv_nickName, tv_postNum, tv_postNum_text, tv_followNum, tv_followNum_text,
            tv_followerNum, tv_followerNum_text, tv_invite_friend_text, tv_feedBack_text,
            tv_setting_text, mTvProfile, tvShare, tvContact;
    private TextView mRedPoint;
    private UserDetailManager userDetailManager;
    private ImageView iv_myLike, iv_feedBack, iv_setting, mSettingRedPoint;


    private SimpleDraweeView mTaskMedal;
    private SimpleDraweeView mWorldCupMedal;

    private Disposable mDispost;

    private LoadingDlg loadingDlg;

    private WelikeBindManager welikeBindManager = new WelikeBindManager();

    public MainMinePresenter() {
        SpManager.getInstance().register(this);
        userDetailManager = new UserDetailManager();
        userDetailManager.setDetailListener(this);
        FollowerManager.INSTANCE.regCallback(this);
    }

    @Override
    public void onCreate(Bundle arguments, Bundle savedInstanceState) {
        mMineView = IMainMineContract.IMainMineFactory.createView();
        mMineView.setPresenter(this);

    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = mMineView.createView(inflater, container, savedInstanceState);
        initViews(mView);
        bindEvents(mView);
        setOnclick();
//        mTaskModel.load(this);
//        MissionManager.INSTANCE.loadMission(this);
        return mView;
    }

    public void initViews(View mView) {
        ll_mine = mView.findViewById(R.id.ll_mine_user);
        ll_follow = mView.findViewById(R.id.ll_main_mine_follow);
        ll_follower = mView.findViewById(R.id.ll_main_mine_follower);
        ll_post = mView.findViewById(R.id.ll_main_mine_post);
        ll_setting = mView.findViewById(R.id.ll_main_mine_setting);
        mSettingRedPoint = mView.findViewById(R.id.iv_mine_setting_next);
        mRedPoint = mView.findViewById(R.id.tv_mine_follownum_red_point);
        llShare = mView.findViewById(R.id.ll_main_mine_share);
        tvShare = mView.findViewById(R.id.tv_mine_share);
//        mFollowerPoint = mView.findViewById(R.id.tv_mine_follownum_red_point_bignum);

        tv_nickName = mView.findViewById(R.id.tv_mine_name);
        tv_postNum = mView.findViewById(R.id.tv_mine_postNum);
        tv_postNum_text = mView.findViewById(R.id.tv_mine_postNum_text);
        tv_followerNum = mView.findViewById(R.id.tv_mine_followerNum);
        tv_followerNum_text = mView.findViewById(R.id.tv_mine_followerNum_text);
        tv_followNum = mView.findViewById(R.id.tv_mine_followNum);
        tv_followNum_text = mView.findViewById(R.id.tv_mine_followNum_text);
        simpleDraw_mine_headView = mView.findViewById(R.id.simpleDraw_mine_headView);
        mTvProfile = mView.findViewById(R.id.tv_mine_MyProfile);

        ll_myLike = mView.findViewById(R.id.ll_main_mine_invite);
        iv_myLike = mView.findViewById(R.id.iv_mine_level);
        iv_feedBack = mView.findViewById(R.id.iv_mine_feedBack);
        iv_setting = mView.findViewById(R.id.iv_mine_setting);

        tv_invite_friend_text = mView.findViewById(R.id.tv_mine_level_text);
        tv_feedBack_text = mView.findViewById(R.id.tv_mine_feedBack);
//        tv_rateUs_text = mView.findViewById(R.id.tv_mine_rateUs);
        tv_setting_text = mView.findViewById(R.id.tv_mine_setting);

        mLlFeedBack = mView.findViewById(R.id.ll_main_mine_feedBack);
        llContact = mView.findViewById(R.id.ll_contract);
        tvContact = mView.findViewById(R.id.contract_title);

        mTaskMedal = mView.findViewById(R.id.mine_medal_task);
        mWorldCupMedal = mView.findViewById(R.id.mine_medal_assignment);
    }

    public void bindEvents(View mView) {
        mSettingRedPoint.setImageResource(R.drawable.setting_check_update_red_point);
        tv_postNum_text.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_post_num_text"));
        tv_followerNum_text.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_follower_num_text"));
        tv_followNum_text.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_following_num_text"));
        tvShare.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_rate_us_text"));

        tv_followerNum.setTextColor(mView.getResources().getColor(R.color.mine_user_nickname_color));
        tv_nickName.setTextColor(mView.getResources().getColor(R.color.mine_user_nickname_color));
        tv_postNum.setTextColor(mView.getResources().getColor(R.color.mine_user_nickname_color));
        tv_followNum.setTextColor(mView.getResources().getColor(R.color.mine_user_nickname_color));
//        tv_invite_friend_text.setTextColor(mView.getResources().getColor(R.color.mine_user_nickname_color));

        tv_invite_friend_text.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_invite_friends_text"));
        tv_feedBack_text.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_feed_back_text"));
//        tv_rateUs_text.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_rate_us_text"));
        tv_setting_text.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_text"));
        tvContact.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "contact_title"));

        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            tv_nickName.setText(account.getNickName());
//            HeadUrlLoader.getInstance().loadHeaderUrl(simpleDraw_mine_headView, account.getHeadUrl());
            VipUtil.set(simpleDraw_mine_headView, account.getHeadUrl(), account.getVip());
            VipUtil.setNickName(tv_nickName, account.getCurLevel(), account.getNickName());
            mTvProfile.setText(account.getIntroduction());

//            iv_myLike.setImageResource(R.drawable.mine_my_like_icon);
//            iv_feedBack.setImageResource(R.drawable.mine_feedback_icon);
//            iv_setting.setImageResource(R.drawable.mine_setting_icon);
//
            tv_postNum.setText(String.valueOf(account.getPostsCount()));
            tv_followerNum.setText(String.valueOf(account.getFollowedUsersCount()));
            tv_followNum.setText(String.valueOf(account.getFollowUsersCount()));

            mMineView.setVerifyAccountView(account.getStatus() == Account.ACCOUNT_HALF);
        }

        String certifyAddress = VipUtil.getCertifyAddress();
        mMineView.setVerifyView(!TextUtils.isEmpty(certifyAddress));

        boolean isUpgrade = SpManager.Setting.getCurrentUpgraded(mView.getContext());
        if (isUpgrade) {
            mSettingRedPoint.setVisibility(View.VISIBLE);
        } else {
            mSettingRedPoint.setVisibility(View.GONE);
        }


    }

    public void setOnclick() {

        ll_mine.setOnClickListener(this);

        ll_follower.setOnClickListener(this);

        ll_follow.setOnClickListener(this);

        ll_post.setOnClickListener(this);

        ll_setting.setOnClickListener(this);

        ll_myLike.setOnClickListener(this);

        mLlFeedBack.setOnClickListener(this);

        llShare.setOnClickListener(this);

        llContact.setOnClickListener(this);

        mTaskMedal.setOnClickListener(this);

        mWorldCupMedal.setOnClickListener(this);
    }

    @Override
    public void attach(boolean isFistShow) {
//        if (isFistShow) {
        refreshUserData();
//        }
        FollowerManager.INSTANCE.requestNews();
//        mTaskModel.load(this);
        mMineView.refreshDraftBoxCount();
//        MissionManager.INSTANCE.loadMission(this);
        refreshUserData();
    }

    private void refreshUserData() {
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            userDetailManager.loadContactDetail(account.getUid());
        }
    }

    @Override
    public void destroy() {
        SpManager.getInstance().unregister(this);
        if (mDispost != null && !mDispost.isDisposed()) {
            mDispost.dispose();
        }
    }


    @Override
    public void onActivityResume() {
        mMineView.refreshDraftBoxCount();
//        MissionManager.INSTANCE.loadMission(this);
    }

    @Override
    public void onVerifyClick() {
        new UserGradeRedirectHandler().onRedirect();
    }

    @Override
    public void onPageShown() {
        refreshUserData();
        FollowerManager.INSTANCE.requestNews();
        mMineView.refreshDraftBoxCount();
//        MissionManager.INSTANCE.loadMission(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mMineView.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void tryBindFacebook(String facebookToken) {
        welikeBindManager.reqFacebookLogin(facebookToken, this);
    }

    @Override
    public void tryBindGoogle(String googleToken) {
        welikeBindManager.reqGoogleLogin(googleToken, this);
    }

    @Override
    public void tryBindTrueCaller(String payload, String signature, String signatureAlgorithm) {
        welikeBindManager.reqTrueCallerLogin(payload, signature, signatureAlgorithm, this);
    }

    @Override
    public void onLoginCallback(@NotNull JSONObject result, int loginType, int errorCode) {
        if (errorCode == ErrorCode.ERROR_SUCCESS) {
            try {
                Account account = AccountManager.getInstance().getAccount();
                if (account == null) return;
                account.setStatus(Account.ACCOUNT_ACTIVE);
                AccountManager.getInstance().updateAccount(account);
                mMineView.setVerifyAccountView(false);
                if (loginType == RegisteredConstant.LOGIN_SOURCE_FACEBOOK) {
                    EventLog.RegisterAndLogin.report25(EventLog.RegisterAndLogin.AccountStatus.LOGIN_UNVERIFY, EventLog.RegisterAndLogin.VerifySource.MAIN_ME_LINK, EventLog.RegisterAndLogin.PageSource.OTHER, EventLog.RegisterAndLogin.LoginSource.FACEBOOK);
                } else if (loginType == RegisteredConstant.LOGIN_SOURCE_GOOGLE) {
                    EventLog.RegisterAndLogin.report25(EventLog.RegisterAndLogin.AccountStatus.LOGIN_UNVERIFY, EventLog.RegisterAndLogin.VerifySource.MAIN_ME_LINK, EventLog.RegisterAndLogin.PageSource.OTHER, EventLog.RegisterAndLogin.LoginSource.GOOGLE);
                } else if (loginType == RegisteredConstant.LOGIN_SOURCE_TRUE_CALLER) {
                    EventLog.RegisterAndLogin.report25(EventLog.RegisterAndLogin.AccountStatus.LOGIN_UNVERIFY, EventLog.RegisterAndLogin.VerifySource.MAIN_ME_LINK, EventLog.RegisterAndLogin.PageSource.OTHER, EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.showShort(ErrorCode.showErrCodeText(errorCode));
        }
    }

    @Override
    public void onClick(View v) {
        Account account = AccountManager.getInstance().getAccount();
        switch (v.getId()) {
            case R.id.ll_mine_user: {
                if (account != null) {
                    UserHostPage.launch(true, account.getUid(), EventConstants.PROFILE_FROM_PAGE_ME);
                    EventLog1.MainMe.report1();
                    EventLog.MainMe.report1();
                }
                break;
            }
            case R.id.ll_main_mine_follower: {
                EventLog1.MainMe.report3();
                EventLog.MainMe.report3();
                if (account != null) {
                    FollowerManager.INSTANCE.setRead();
                    UserFollowActivity.launch(FOLLOWER, account.getUid(), account.getNickName());
                }
                break;
            }
            case R.id.ll_main_mine_follow: {
                EventLog1.MainMe.report2();
                EventLog.MainMe.report2();
                if (account != null) {
                    UserFollowActivity.launch(FOLLOWING, account.getUid(), account.getNickName());
                }
                break;
            }
            case R.id.ll_main_mine_post:
                EventLog1.MainMe.report4();
                EventLog.MainMe.report4();
                if (account != null) {
                    UserHostPage.launch(true, account.getUid());
                }
                break;
            case R.id.ll_main_mine_feedBack:
                EventLog1.MainMe.report10();

                if (account == null || account.getStatus() == Account.ACCOUNT_HALF) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER));
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_VERIFY_DIALOG, bundle));

                    return;
                }

//                String dn = account.getUid();
//                String av = BuildConfig.VERSION_NAME;
//                String to = account.getAccessToken();
//                String la = LocalizationManager.getInstance().getCurrentLanguage();
//                String lo = account.isLogin()?"1":"0";
//                String url = String.format(RouteConstant.HELPANDFEEDBACK,dn,av,to,la,lo);
//
//                Bundle bundle = new Bundle();
//                bundle.putString("url", url.toString());
//                bundle.putString(WebViewConstant.KEY_FROM, DefaultUrlRedirectHandler.FROM_FEEDBACK);
//                bundle.putBoolean(WebViewConstant.KEY_SHOW_TITLE,true);
//                bundle.putString(WebViewConstant.KEY_TITLE_TEXT,ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_feed_back_text"));
//                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_WEB_VIEW, bundle));

                IMHelper.INSTANCE.getCustomerSession(new Function1<SESSION, Unit>() {
                    @Override
                    public Unit invoke(final SESSION session) {
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(ImConstant.IM_SESSION_KRY, session);
                                bundle.putBoolean(ImConstant.IM_SESSION_CUSTOMER, true);
                                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CHAT_EVENT, bundle));
                            }
                        });
                        return null;
                    }
                }, new Function1<Integer, Unit>() {
                    @Override
                    public Unit invoke(Integer integer) {
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "create_chat_failed"));
                            }
                        });
                        return null;
                    }
                });
                break;
            case R.id.ll_main_mine_setting:
                EventLog1.MainMe.report11();
                EventLog.MainMe.report11();
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SETTING_EVENT, null));
                break;
            case R.id.ll_main_mine_invite:
                EventLog1.MainMe.report8();
                EventLog.MainMe.report8();
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_MINE_MY_LIKE, null));
                break;
            case R.id.ll_main_mine_share:
                EventLog1.MainMe.report9();
                EventLog.MainMe.report9();
                ShareModel shareModel = new ShareModel();
                shareModel.setShareModelType(ShareModel.SHARE_MODEL_TYPE_APP);
                shareModel.setImageUrl(ShareHelper.getShareAppImage());
                ShareManagerWrapper.Builder builder = new ShareManagerWrapper.Builder();
                builder.with(v.getContext())
                        .login(AccountManager.getInstance().isLoginComplete())
                        .eventModel(new EventModel(EventLog1.Share.ContentType.APP, null, null, EventLog1.Share.ShareFrom.OTHER, EventLog1.Share.PopPage.APP, null, "", null, null,
                                null, null, null, null, null, null, null, null))
                        .shareModel(shareModel)
                        .shortLinkModel(new ShortLinkModel(ShareModel.SHARE_MODEL_TYPE_APP, ""))
                        .shareTitleModel(new ShareTitleModel(ShareModel.SHARE_MODEL_TYPE_APP, ""))
                        .build().share();
                NewShareEventManager.INSTANCE.setPopFrom(EventConstants.NEW_SHARE_POP_FROM_APP);
                break;
            case R.id.ll_contract:
                EventLog1.MainMe.report7();
                EventLog.MainMe.report7();
                Bundle bundle = new Bundle();
                bundle.putSerializable("from", EventLog1.AddFriend.ButtonFrom.ME);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_CONTACT, bundle));
                break;
            case R.id.mine_medal_task:
//                MissionManager.INSTANCE.notifyEvent(MissionType.TASK_LIST);
                new DefaultUrlRedirectHandler(mTaskMedal.getContext(), DefaultUrlRedirectHandler.FROM_TASK).onUrlRedirect(MissionManager.INSTANCE.getForwardUrl());
                break;
            case R.id.mine_medal_assignment:
                String forwardUrl = (String) v.getTag();
                if (!TextUtils.isEmpty(forwardUrl)) {
                    new DefaultUrlRedirectHandler(v.getContext(), DefaultUrlRedirectHandler.FROM_TASK).onUrlRedirect(forwardUrl);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onContactDetailCompleted(UserDetailManager manager, User user, int errCode) {//todo
        if (manager == userDetailManager) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                Account account = AccountManager.getInstance().getAccount();
                if (account != null && user != null) {
                    if (TextUtils.equals(account.getUid(), user.getUid())) {
                        tv_followerNum.setText(String.valueOf(user.getFollowedUsersCount()));
                        tv_followNum.setText(String.valueOf(user.getFollowUsersCount()));
                        tv_postNum.setText(String.valueOf(user.getPostsCount()));
                        mTvProfile.setText(user.getIntroduction());
                        account.setStatus(user.getStatus());
                        UserHonor taskHonor = null, wcHonor = null;
                        List<UserHonor> userHonors = user.getUserHonors();
                        if (!CollectionUtil.isEmpty(userHonors)) {
                            for (UserHonor userHonor : userHonors) {
                                if (userHonor.type == UserHonor.TYPE_TASK) {
                                    taskHonor = userHonor;
                                }
                                if (userHonor.type == UserHonor.TYPE_EVENT) {
                                    wcHonor = userHonor;
                                }
                            }
                        }
                        if (wcHonor != null) {
                            mWorldCupMedal.setVisibility(View.VISIBLE);
                            mWorldCupMedal.setImageURI(wcHonor.honorPic);
                            mWorldCupMedal.setTag(wcHonor.forwardUrl);
                        } else {
                            mWorldCupMedal.setVisibility(View.GONE);
                        }

                        if (taskHonor != null) {
                            mTaskMedal.setVisibility(View.VISIBLE);
                            mTaskMedal.setImageURI(taskHonor.honorPic);
                        } else {
                            mTaskMedal.setVisibility(View.GONE);
                        }

                        mMineView.showGenderView(account.getSex() == -1);
                        mMineView.setVerifyAccountView(account.getStatus() == 3);

                        mTaskMedal.setVisibility(mTaskMedal.getDrawable() == null ? View.GONE : View.VISIBLE);
//                        HeadUrlLoader.getInstance().loadHeaderUrl(simpleDraw_mine_headView, user.getHeadUrl());
                        VipUtil.set(simpleDraw_mine_headView, account.getHeadUrl(), account.getVip());
                        VipUtil.setNickName(tv_nickName, user.getCurLevel(), user.getNickName());

                        account.setNickName(user.getNickName());
                        account.setHeadUrl(user.getHeadUrl());
                        account.setPostsCount(user.getPostsCount());
                        account.setAllowUpdateNickName(user.isAllowUpdateNickName());
                        account.setAllowUpdateSex(user.isAllowUpdateSex());
                        account.setNextUpdateNickNameDate(user.getNextUpdateNickNameDate());
                        account.setSexUpdateCount(user.getSexUpdateCount());
                        account.setFollowUsersCount(user.getFollowUsersCount());
                        account.setFollowedUsersCount(user.getFollowedUsersCount());
                        account.setVip(user.getVip());
                        account.setLinks(user.getLinks());
                        account.setCurLevel(user.getCurLevel());
                        account.setChangeNameCount(user.getChangeNameCount());
                        AccountManager.getInstance().updateAccount(account);
                    }
                }
            }
        }
    }

    @Override
    public void onSPDataChanged(String spTypeName, String spKeyName) {
        if (TextUtils.equals(spTypeName, SpManager.sharePreferencesSettingName) &&
                TextUtils.equals(spKeyName, SpManager.settingUpgradedKeyName)) {
            boolean isUpgrade = SpManager.Setting.getCurrentUpgraded(mSettingRedPoint.getContext());
            if (isUpgrade) {
                mSettingRedPoint.setVisibility(View.VISIBLE);
            } else {
                mSettingRedPoint.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onNewFollowers(int count) {
        mRedPoint.setVisibility(count > 0 ? View.VISIBLE : View.INVISIBLE);
        mRedPoint.setText(String.valueOf(count));
    }

    @Override
    public void onFinish(final MissionTask value) {
    }

    @Override
    public void onError(final MissionTask value) {
    }

    @Override
    public void setAccountGender(byte gender) {

        Account account = AccountManager.getInstance().getAccount();

        if (account != null) {
            account.setSex(gender);
            AccountManager.getInstance().updateAccount(account);
            AccountManager.getInstance().modifyAccount(account);
        }
        mMineView.showGenderView(false);

    }
}
