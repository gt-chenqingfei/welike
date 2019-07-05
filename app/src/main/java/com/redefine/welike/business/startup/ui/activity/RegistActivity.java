package com.redefine.welike.business.startup.ui.activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonSingleConfirmAndCancelDialog;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.h5.WebViewActivity;
import com.redefine.commonui.widget.LoadingTranslateBgDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.photoselector.constant.ImagePickConstant;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.CommonRequestCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.upgraded.UpdateHelper;
import com.redefine.welike.business.browse.ui.activity.AvoidTokenDiscoverActivity;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.startup.ui.fragment.RegistChooseLanguageFragment;
import com.redefine.welike.business.startup.ui.fragment.RegistInterestsFragment;
import com.redefine.welike.business.startup.ui.fragment.RegistMobileFragment;
import com.redefine.welike.business.startup.ui.fragment.RegistRecommondUserFragment;
import com.redefine.welike.business.startup.ui.fragment.RegistSmsCodeFragment;
import com.redefine.welike.business.startup.ui.fragment.RegistThirdLoginFragment;
import com.redefine.welike.business.startup.ui.fragment.RegistUserInfoFragment;
import com.redefine.welike.business.startup.ui.fragment.RegistVoiceCodeFragment;
import com.redefine.welike.business.startup.ui.fragment.RegistWeclomeFragment;
import com.redefine.welike.business.startup.ui.viewmodel.RegisteredViewModel;
import com.redefine.welike.business.user.ui.activity.RestoreAccountActivity;
import com.redefine.welike.business.user.ui.dialog.UpdateDialog;
import com.redefine.welike.common.BrowseManager;
import com.redefine.welike.common.CompatUtil;
import com.redefine.welike.commonui.activity.MainActivityEx;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.event.RouteDispatcher;
import com.redefine.welike.hive.AppsFlyerManager;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_GUIDE;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_INTERET;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_LANGUAGE;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_PHONENUM;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_RECOMMOND;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_SMS;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_THIRD_LOGIN;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_USERINFO;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_VOICE;

/**
 * 登录页
 *
 * @author gongguan 2018/1/7 下午11:38
 * @author honglin 2018/6/12
 */


@Route(path = RouteConstant.PATH_START_REGISTER_PAGE)
public class RegistActivity extends BaseActivity implements SpManager.SPDataListener {

    private int back_count = 0;
    private int fragmentType = 0;
    private int fragmentFirstType = 0;
    private static final String FRAG_STATE = "fragment_state";
    public static final int RC_GET_AUTH_CODE = 9003;
    private boolean fromBrowse = false;

    private RegisteredViewModel registeredViewModel;
    private LoadingTranslateBgDlg loadingDlg;
    private CommonSingleConfirmAndCancelDialog blockDialog;

    private String finishType = "";
    private String finishUrl = "";
    private RegisterAndLoginModel eventModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        CompatUtil.disableAutoFill(this);
        init(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    private void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_regist);
        registeredViewModel = ViewModelProviders.of(this).get(RegisteredViewModel.class);
        int startPageType = getIntent().getIntExtra("startPageType", FRAGMENT_THIRD_LOGIN);
        fromBrowse = getIntent().getBooleanExtra("fromBrowse", true);
        if (savedInstanceState != null) {
            selectFragment(savedInstanceState.getInt(FRAG_STATE));
        } else {
            fragmentFirstType = startPageType;
            selectFragment(startPageType);
        }

        if (getIntent().getExtras() != null) {
            finishType = getIntent().getExtras().getString("finish_type", "");
            finishUrl = getIntent().getExtras().getString("url", "");
            Serializable serializable = getIntent().getExtras().getSerializable(RegisteredConstant.KEY_EVENT_MODEL);
            if (serializable instanceof RegisterAndLoginModel) {
                eventModel = (RegisterAndLoginModel) serializable;
            } else {
                eventModel = new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.OTHER);
            }
        }

        SpManager.getInstance().register(this);
        UpdateHelper.getInstance().checkUpgraded();

        registeredViewModel.getStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {

                if (integer == null) return;
                goProcess(integer);

                if (integer.equals(StartConstant.START_STATE_MAIN)) {
                    if (eventModel != null) {
                        EventLog.RegisterAndLogin.report19(eventModel.nickName, eventModel.nickNameCheck, eventModel.phoneNumber, eventModel.verifyType,
                                eventModel.smsSend, eventModel.stayTime, eventModel.loginSource, eventModel.returnResult, eventModel.newUser, eventModel.language,
                                eventModel.smsCheck, eventModel.pageSource, eventModel.pageStatus, eventModel.tcInstalled, eventModel.inputWay, eventModel.requestWay,
                                eventModel.fromPage, eventModel.loginVerifyType, eventModel.accountStatus, eventModel.pageType, eventModel.verifySource);
                    }
                }
            }
        });


        registeredViewModel.getPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) return;

                switch (pageStatusEnum) {
                    case LOADING:
                        showLoadingDialog();

                        break;
                    case CONTENT:
                    case ERROR:
                        dissmissLoadingDialog();
                        break;
                }
            }
        });

        registeredViewModel.getCodeStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer errorCode) {
                if (errorCode == null) return;
                String showText = ErrorCode.showErrCodeText(errorCode);
                if (!TextUtils.isEmpty(showText)) {
                    ToastUtils.showShort(showText);
                }
            }
        });

        registeredViewModel.getLoginStatus().observe(this, new Observer<RegisteredViewModel.LoginStatus>() {
            @Override
            public void onChanged(@Nullable RegisteredViewModel.LoginStatus loginStatus) {
                if (loginStatus == null) {
                    return;
                }
                if (loginStatus == RegisteredViewModel.LoginStatus.LOGIN_SUCCESS) {
                    EventLog.RegisterAndLogin.report18(eventModel.nickName, eventModel.nickNameCheck, eventModel.phoneNumber, eventModel.verifyType,
                            eventModel.smsSend, eventModel.stayTime, eventModel.loginSource, eventModel.returnResult, eventModel.newUser, eventModel.language,
                            eventModel.smsCheck, eventModel.pageSource, eventModel.pageStatus, eventModel.tcInstalled, eventModel.inputWay, eventModel.requestWay,
                            eventModel.fromPage, eventModel.loginVerifyType, eventModel.accountStatus, eventModel.pageType, eventModel.verifySource);
                } else if (loginStatus == RegisteredViewModel.LoginStatus.UPDATE_INFO_SUCCESS ||
                        loginStatus == RegisteredViewModel.LoginStatus.SKIP_INFO_SUCCESS) {
                    EventLog.RegisterAndLogin.report19(eventModel.nickName, eventModel.nickNameCheck, eventModel.phoneNumber, eventModel.verifyType,
                            eventModel.smsSend, eventModel.stayTime, eventModel.loginSource, eventModel.returnResult, eventModel.newUser, eventModel.language,
                            eventModel.smsCheck, eventModel.pageSource, eventModel.pageStatus, eventModel.tcInstalled, eventModel.inputWay, eventModel.requestWay,
                            eventModel.fromPage, eventModel.loginVerifyType, eventModel.accountStatus, eventModel.pageType, eventModel.verifySource);
                }
            }
        });

        registeredViewModel.getUrl().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (TextUtils.isEmpty(s)) return;
                showBlockDialog(s);
            }
        });
    }

    public RegisterAndLoginModel getEventModel() {
        return eventModel;
    }

    private void showLoadingDialog() {
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg = null;
        }
        loadingDlg = new LoadingTranslateBgDlg(this);
        loadingDlg.show();
    }

    private void dissmissLoadingDialog() {
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg = null;
        }
    }

    public static void show(Context context, int startPageType, RegisterAndLoginModel eventModel) {
        Intent intent = new Intent();
        intent.setClass(context, RegistActivity.class);
        intent.putExtra("startPageType", startPageType);
        intent.putExtra("fromBrowse", false);
        intent.putExtra(RegisteredConstant.KEY_EVENT_MODEL, eventModel);
        context.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        RegistThirdLoginFragment thirdLoginFragment = (RegistThirdLoginFragment) getSupportFragmentManager().findFragmentByTag(RegistThirdLoginFragment.THIRD_LOGIN_FRAGMENT_TAG);
        if (fragmentType == FRAGMENT_THIRD_LOGIN && thirdLoginFragment != null && thirdLoginFragment.getThirdLoginLayout() != null && data != null) {
            thirdLoginFragment.getThirdLoginLayout().onActivityResult(requestCode, resultCode, data);
        }
        RegistMobileFragment mobileFragment = (RegistMobileFragment) getSupportFragmentManager().findFragmentByTag(RegistMobileFragment.MOBILE_FRAGMENT_TAG);
        if (fragmentType == FRAGMENT_PHONENUM && mobileFragment != null && mobileFragment.getThirdLoginLayout() != null && data != null) {
            mobileFragment.getThirdLoginLayout().onActivityResult(requestCode, resultCode, data);
        }
        RegistSmsCodeFragment smsFragment = (RegistSmsCodeFragment) getSupportFragmentManager().findFragmentByTag(RegistSmsCodeFragment.SMS_FRAGMENT_TAG);
        if (fragmentType == FRAGMENT_SMS && smsFragment != null && smsFragment.getThirdLoginLayout() != null && data != null) {
            smsFragment.getThirdLoginLayout().onActivityResult(requestCode, resultCode, data);
        }
        RegistVoiceCodeFragment voiceFragment = (RegistVoiceCodeFragment) getSupportFragmentManager().findFragmentByTag(RegistVoiceCodeFragment.VOCIE_FRAGMENT_TAG);
        if (fragmentType == FRAGMENT_VOICE && voiceFragment != null && voiceFragment.getThirdLoginLayout() != null && data != null) {
            voiceFragment.getThirdLoginLayout().onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CommonRequestCode.REGISTER_CHOOSE_HEADER_PIC_CODE) {
                ArrayList<Item> items = data.getParcelableArrayListExtra(ImagePickConstant.EXTRA_RESULT_SELECTION_ITEMS);

                if (!CollectionUtil.isEmpty(items)) {
                    RegistUserInfoFragment userInfoFragment = (RegistUserInfoFragment) getSupportFragmentManager().findFragmentByTag(RegistUserInfoFragment.FILL_USERINFO_TAG);
                    if (userInfoFragment != null) {
//                        userInfoFragment.handlePhotoSelectorResult(items.get(0));
                    }
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (fragmentType == FRAGMENT_SMS
                || fragmentType == FRAGMENT_VOICE) {
            registeredViewModel.setStatus(StartConstant.START_STATE_LOGIN_MOBILE);
            return;
        }

        if (fragmentType == FRAGMENT_PHONENUM) {

            if (fragmentType == fragmentFirstType) {
                finish();
                return;
            }

            registeredViewModel.setStatus(StartConstant.START_STATE_THIRD_LOGIN);
            return;
        }

        if (fragmentType == FRAGMENT_USERINFO) {
            return;
        }

        if (fromBrowse) {
            finish();
            return;
        }

        if (fragmentType == FRAGMENT_THIRD_LOGIN && BrowseManager.isVidMate()) {
            registeredViewModel.setStatus(StartConstant.START_STATE_VIDMATE);
            return;
        }

        new CountDownTimer(2000, 700) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                back_count = 0;
            }

        }.start();

        if (back_count < 1) {
            ToastUtils.showShort(ResourceTool.getString("regist_system_back_note"));
        } else {
            finish();
        }
        back_count++;
    }


    public void goProcess(int state) {
        switch (state) {
            case StartConstant.START_STATE_LANG:
                selectFragment(FRAGMENT_LANGUAGE);
                break;
            case StartConstant.START_STATE_THIRD_LOGIN:
                selectFragment(FRAGMENT_THIRD_LOGIN);
                break;
            case StartConstant.START_STATE_LOGIN_MOBILE:
                selectFragment(FRAGMENT_PHONENUM);
                break;
            case StartConstant.START_STATE_LOGIN_SMS_CODE:
                selectFragment(FRAGMENT_SMS);
                break;
            case StartConstant.START_STATE_LOGIN_VOICE_CODE:
                showFragment(FRAGMENT_VOICE);
                break;
            case StartConstant.START_STATE_LOGIN_BACK_SMS_CODE:
                showFragment(FRAGMENT_SMS);
                break;
            case StartConstant.START_STATE_REGISTER_USERINFO:
                selectFragment(FRAGMENT_USERINFO);
                break;
            case StartConstant.START_STATE_REGISTER_INTERESTS:
                selectFragment(FRAGMENT_INTERET);
                break;
            case StartConstant.START_STATE_REGISTER_SUG_USERS:
                selectFragment(FRAGMENT_RECOMMOND);
                break;
            case StartConstant.START_STATE_MAIN:
                jump2Main();
                break;
            case StartConstant.START_STATE_VIDMATE:
                jump2Browse();
                break;
            case StartConstant.START_STATE_DEACTIVATE:
                RestoreAccountActivity.luanch(this);
                finish();
                break;
            case StartConstant.REGISTER_STATE_FINISH:
                finish();
                break;
            default:
                break;
        }
    }

    private void showFragment(int type) {
        fragmentType = type;
        switch (type) {
            case FRAGMENT_SMS: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistSmsCodeFragment registSmsCodeFragment = (RegistSmsCodeFragment) getSupportFragmentManager().findFragmentByTag(RegistSmsCodeFragment.SMS_FRAGMENT_TAG);
                Fragment voiceCodeFragment = getSupportFragmentManager().findFragmentByTag(RegistVoiceCodeFragment.VOCIE_FRAGMENT_TAG);
                if (registSmsCodeFragment == null) {
                    registSmsCodeFragment = new RegistSmsCodeFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, registSmsCodeFragment, RegistSmsCodeFragment.SMS_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    if (voiceCodeFragment != null) {
                        fragmentTransaction.hide(voiceCodeFragment);
                    }
                    fragmentTransaction.show(registSmsCodeFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            case FRAGMENT_VOICE: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment voiceCodeFragment = getSupportFragmentManager().findFragmentByTag(RegistVoiceCodeFragment.VOCIE_FRAGMENT_TAG);
                RegistSmsCodeFragment registSmsCodeFragment = (RegistSmsCodeFragment) getSupportFragmentManager().findFragmentByTag(RegistSmsCodeFragment.SMS_FRAGMENT_TAG);
                if (voiceCodeFragment == null) {
                    voiceCodeFragment = new RegistVoiceCodeFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, voiceCodeFragment, RegistVoiceCodeFragment.VOCIE_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    if (registSmsCodeFragment != null) {
                        fragmentTransaction.hide(registSmsCodeFragment);
                    }
                    fragmentTransaction.show(voiceCodeFragment).commitNowAllowingStateLoss();
                }
                break;
            }
        }
    }


    private void showBlockDialog(final String s) {
        blockDialog = CommonSingleConfirmAndCancelDialog.showConfirmDialog(this, ResourceTool.getString("login_block_title")
                , ResourceTool.getString("login_block_appeal"),
                new CommonSingleConfirmAndCancelDialog.IConfirmDialogListener() {
                    @Override
                    public void onClickConfirm() {
                        WebViewActivity.luanch(RegistActivity.this, s, R.color.white);
                    }
                });
    }

    private void dissmissBlockDialog() {
        if (blockDialog != null) {
            blockDialog.dismiss();
            blockDialog = null;
        }
    }

    private void jump2Browse() {

        AvoidTokenDiscoverActivity.Companion.launch(this);
        finish();
    }

    private void jump2Main() {

        Uri uri = Uri.parse(AppsFlyerManager.getInstance().getReferrerUri());
        Uri finishUri = Uri.parse(finishUrl);
        if (RouteDispatcher.validUri(uri)) {
            ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
        } else if (TextUtils.equals(finishType, "1") && RouteDispatcher.validUri(finishUri)) {
            ARouter.getInstance().build(finishUri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
        } else {
            EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_HOME));
            MainActivityEx.show(this);
        }
        this.overridePendingTransition(0, 0);
        EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN, null));
        finish();
    }

    public void selectFragment(int type) {
        clearAllStartupFragments();
        fragmentType = type;
        switch (type) {
            case FRAGMENT_LANGUAGE: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistChooseLanguageFragment languageFragment = (RegistChooseLanguageFragment) getSupportFragmentManager().findFragmentByTag(RegistChooseLanguageFragment.LANGUAGE_FRAGMENT_TAG);
                if (languageFragment == null) {
                    languageFragment = new RegistChooseLanguageFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, languageFragment, RegistChooseLanguageFragment.LANGUAGE_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(languageFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            case FRAGMENT_THIRD_LOGIN: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistThirdLoginFragment thirdLoginFragment = (RegistThirdLoginFragment) getSupportFragmentManager().findFragmentByTag(RegistThirdLoginFragment.THIRD_LOGIN_FRAGMENT_TAG);
                if (thirdLoginFragment == null) {
                    thirdLoginFragment = new RegistThirdLoginFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, thirdLoginFragment, RegistThirdLoginFragment.THIRD_LOGIN_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(thirdLoginFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            case FRAGMENT_PHONENUM: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistMobileFragment mobileFragment = (RegistMobileFragment) getSupportFragmentManager().findFragmentByTag(RegistMobileFragment.MOBILE_FRAGMENT_TAG);
                if (mobileFragment == null) {
                    mobileFragment = new RegistMobileFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, mobileFragment, RegistMobileFragment.MOBILE_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(mobileFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            case FRAGMENT_SMS: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistSmsCodeFragment registSmsCodeFragment = (RegistSmsCodeFragment) getSupportFragmentManager().findFragmentByTag(RegistSmsCodeFragment.SMS_FRAGMENT_TAG);
                if (registSmsCodeFragment == null) {
                    registSmsCodeFragment = new RegistSmsCodeFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, registSmsCodeFragment, RegistSmsCodeFragment.SMS_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(registSmsCodeFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            case FRAGMENT_USERINFO: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistUserInfoFragment userInfoFragment = (RegistUserInfoFragment) getSupportFragmentManager().findFragmentByTag(RegistUserInfoFragment.FILL_USERINFO_TAG);
                if (userInfoFragment == null) {
                    userInfoFragment = new RegistUserInfoFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, userInfoFragment, RegistUserInfoFragment.FILL_USERINFO_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(userInfoFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            case FRAGMENT_INTERET: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistInterestsFragment interestsFragment = (RegistInterestsFragment) getSupportFragmentManager().findFragmentByTag(RegistInterestsFragment.REGIST_INTEREST_TAG);
                if (interestsFragment == null) {
                    interestsFragment = new RegistInterestsFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, interestsFragment, RegistInterestsFragment.REGIST_INTEREST_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(interestsFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            case FRAGMENT_RECOMMOND: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistRecommondUserFragment registRecommondUserFragment = (RegistRecommondUserFragment) getSupportFragmentManager().findFragmentByTag(RegistRecommondUserFragment.RECOMMOND_USER_FRAGMENT_TAG);
                if (registRecommondUserFragment == null) {
                    registRecommondUserFragment = new RegistRecommondUserFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, registRecommondUserFragment, RegistRecommondUserFragment.RECOMMOND_USER_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(registRecommondUserFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            case FRAGMENT_GUIDE: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                RegistWeclomeFragment registGuideFragment = (RegistWeclomeFragment) getSupportFragmentManager().findFragmentByTag(RegistWeclomeFragment.WELCOME_USER_FRAGMENT_TAG);
                if (registGuideFragment == null) {
                    registGuideFragment = new RegistWeclomeFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, registGuideFragment, RegistWeclomeFragment.WELCOME_USER_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(registGuideFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FRAG_STATE, fragmentType);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dissmissLoadingDialog();
        dissmissBlockDialog();
        SpManager.getInstance().unregister(this);
    }

    @Override
    public void onSPDataChanged(String spTypeName, String spKeyName) {
        if (TextUtils.equals(spTypeName, SpManager.sharePreferencesSettingName) &&
                TextUtils.equals(spKeyName, SpManager.settingUpgradedForceKeyName)) {
            boolean isUpgrade = SpManager.Setting.getCurrentForceUpgraded(MyApplication.getAppContext());
            if (isUpgrade) {
                UpdateDialog updateDialog = new UpdateDialog(this, true);
                updateDialog.show();
            }
        }
    }

    private void clearAllStartupFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        List<Fragment> lists = getSupportFragmentManager().getFragments();
        if (CollectionUtil.isEmpty(lists)) {
            return;
        }
        for (Fragment f : lists) {
            if (f instanceof RegistChooseLanguageFragment) {
                ft.remove(f);
            } else if (f instanceof RegistThirdLoginFragment) {
                ft.remove(f);
            } else if (f instanceof RegistMobileFragment) {
                ft.remove(f);
            } else if (f instanceof RegistSmsCodeFragment) {
                ft.remove(f);
            } else if (f instanceof RegistUserInfoFragment) {
                ft.remove(f);
            } else if (f instanceof RegistInterestsFragment) {
                ft.remove(f);
            } else if (f instanceof RegistRecommondUserFragment) {
                ft.remove(f);
            } else if (f instanceof RegistWeclomeFragment) {
                ft.remove(f);
            } else if (f instanceof RegistVoiceCodeFragment) {
                ft.remove(f);
            }
        }
        ft.commitNowAllowingStateLoss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        if (event.id == EventIdConstant.LAUNCH_LOGOUT_EVENT) {
            if (fragmentType == FRAGMENT_LANGUAGE) return;
            StartManager.getInstance().logout();
            selectFragment(FRAGMENT_THIRD_LOGIN);
        }
    }

}