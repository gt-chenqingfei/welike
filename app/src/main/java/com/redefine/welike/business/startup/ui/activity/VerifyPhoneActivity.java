package com.redefine.welike.business.startup.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonSingleConfirmAndCancelDialog;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.LoadingTranslateBgDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.upgraded.UpdateHelper;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.startup.ui.fragment.VerifyMobileFragment;
import com.redefine.welike.business.startup.ui.fragment.VerifySmsCodeFragment;
import com.redefine.welike.business.startup.ui.fragment.VerifyVoiceCodeFragment;
import com.redefine.welike.business.startup.ui.viewmodel.VerifyViewModel;
import com.redefine.welike.business.user.ui.dialog.UpdateDialog;
import com.redefine.welike.common.CompatUtil;
import com.redefine.welike.commonui.activity.MainActivityEx;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.event.RouteDispatcher;
import com.redefine.welike.hive.AppsFlyerManager;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_PHONENUM;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_SMS;
import static com.redefine.welike.business.startup.management.constant.RegisteredConstant.FRAGMENT_VOICE;

/**
 * 登录页
 *
 * @author gongguan 2018/1/7 下午11:38
 * @author honglin 2018/6/12
 */


@Route(path = RouteConstant.LAUNCH_VERIFY_PHONE_ACTIVITY)
public class VerifyPhoneActivity extends BaseActivity implements SpManager.SPDataListener {

    private int fragmentType = 0;

    private VerifyViewModel registeredViewModel;
    private LoadingTranslateBgDlg loadingDlg;
    private CommonSingleConfirmAndCancelDialog blockDialog;
    private RegisterAndLoginModel eventModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CompatUtil.disableAutoFill(this);
        init(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_regist);
        registeredViewModel = ViewModelProviders.of(this).get(VerifyViewModel.class);
        int startPageType = getIntent().getIntExtra("startPageType", FRAGMENT_PHONENUM);
        selectFragment(startPageType);
        SpManager.getInstance().register(this);
        UpdateHelper.getInstance().checkUpgraded();
        eventModel = (RegisterAndLoginModel) getIntent().getSerializableExtra(RegisteredConstant.KEY_EVENT_MODEL);

        registeredViewModel.getStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {

                if (integer == null) return;
                goProcess(integer);

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

                if (errorCode == ErrorCode.ERROR_SUCCESS) {
                    jump2Main();
                    if (eventModel != null) {
                        EventLog.RegisterAndLogin.report18(eventModel.nickName, eventModel.nickNameCheck, eventModel.phoneNumber, eventModel.verifyType,
                                eventModel.smsSend, eventModel.stayTime, eventModel.loginSource, eventModel.returnResult, eventModel.newUser, eventModel.language,
                                eventModel.smsCheck, eventModel.pageSource, eventModel.pageStatus, eventModel.tcInstalled, eventModel.inputWay, eventModel.requestWay,
                                eventModel.fromPage, eventModel.loginVerifyType, eventModel.accountStatus, eventModel.pageType, eventModel.verifySource);
                        EventLog.RegisterAndLogin.report25(eventModel.accountStatus, eventModel.verifySource, eventModel.pageSource, eventModel.loginSource);
                    }
                    return;
                }

                String showText = ErrorCode.showErrCodeText(errorCode);
                if (!TextUtils.isEmpty(showText)) {
                    ToastUtils.showShort(showText);
                }
            }
        });


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

    public RegisterAndLoginModel getEventModel() {
        return eventModel;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        VerifySmsCodeFragment smsFragment = (VerifySmsCodeFragment) getSupportFragmentManager().findFragmentByTag(VerifySmsCodeFragment.SMS_FRAGMENT_TAG);
        if (fragmentType == FRAGMENT_SMS && smsFragment != null && smsFragment.getThirdLoginLayout() != null && data != null) {
            smsFragment.getThirdLoginLayout().onActivityResult(requestCode, resultCode, data);
        }
        VerifyVoiceCodeFragment voiceFragment = (VerifyVoiceCodeFragment) getSupportFragmentManager().findFragmentByTag(VerifyVoiceCodeFragment.VOCIE_FRAGMENT_TAG);
        if (fragmentType == FRAGMENT_VOICE && voiceFragment != null && voiceFragment.getThirdLoginLayout() != null && data != null) {
            voiceFragment.getThirdLoginLayout().onActivityResult(requestCode, resultCode, data);
        }
        VerifyMobileFragment verifyMobileFragment = (VerifyMobileFragment) getSupportFragmentManager().findFragmentByTag(VerifyMobileFragment.MOBILE_FRAGMENT_TAG);
        if (fragmentType == FRAGMENT_PHONENUM && verifyMobileFragment != null && verifyMobileFragment.getThirdLoginLayout() != null && data != null) {
            verifyMobileFragment.getThirdLoginLayout().onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void goProcess(int state) {
        switch (state) {
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
            default:
                break;
        }
    }

    private void showFragment(int type) {
        fragmentType = type;
        switch (type) {
            case FRAGMENT_SMS: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                VerifySmsCodeFragment registSmsCodeFragment = (VerifySmsCodeFragment) getSupportFragmentManager().findFragmentByTag(VerifySmsCodeFragment.SMS_FRAGMENT_TAG);
                Fragment voiceCodeFragment = getSupportFragmentManager().findFragmentByTag(VerifyVoiceCodeFragment.VOCIE_FRAGMENT_TAG);
                if (registSmsCodeFragment == null) {
                    registSmsCodeFragment = new VerifySmsCodeFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, registSmsCodeFragment, VerifySmsCodeFragment.SMS_FRAGMENT_TAG).commitNowAllowingStateLoss();
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
                Fragment voiceCodeFragment = getSupportFragmentManager().findFragmentByTag(VerifyVoiceCodeFragment.VOCIE_FRAGMENT_TAG);
                VerifySmsCodeFragment registSmsCodeFragment = (VerifySmsCodeFragment) getSupportFragmentManager().findFragmentByTag(VerifySmsCodeFragment.SMS_FRAGMENT_TAG);
                if (voiceCodeFragment == null) {
                    voiceCodeFragment = new VerifyVoiceCodeFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, voiceCodeFragment, VerifyVoiceCodeFragment.VOCIE_FRAGMENT_TAG).commitNowAllowingStateLoss();
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

    private void dissmissBlockDialog() {
        if (blockDialog != null) {
            blockDialog.dismiss();
            blockDialog = null;
        }
    }

    private void jump2Main() {

        Uri uri = Uri.parse(AppsFlyerManager.getInstance().getReferrerUri());
        if (RouteDispatcher.validUri(uri)) {
            ARouter.getInstance().build(uri).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).navigation();
        }
        MainActivityEx.show(this);
        this.overridePendingTransition(0, 0);
        EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN, null));
        finish();
    }

    public void selectFragment(int type) {
        clearAllStartupFragments();
        fragmentType = type;
        switch (type) {

            case FRAGMENT_PHONENUM: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                VerifyMobileFragment mobileFragment = (VerifyMobileFragment) getSupportFragmentManager().findFragmentByTag(VerifyMobileFragment.MOBILE_FRAGMENT_TAG);
                if (mobileFragment == null) {
                    mobileFragment = new VerifyMobileFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, mobileFragment, VerifyMobileFragment.MOBILE_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(mobileFragment).commitNowAllowingStateLoss();
                }
                break;
            }
            case FRAGMENT_SMS: {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                VerifySmsCodeFragment registSmsCodeFragment = (VerifySmsCodeFragment) getSupportFragmentManager().findFragmentByTag(VerifySmsCodeFragment.SMS_FRAGMENT_TAG);
                if (registSmsCodeFragment == null) {
                    registSmsCodeFragment = new VerifySmsCodeFragment();
                    fragmentTransaction.add(R.id.fl_regist_fg_container, registSmsCodeFragment, VerifySmsCodeFragment.SMS_FRAGMENT_TAG).commitNowAllowingStateLoss();
                } else {
                    fragmentTransaction.attach(registSmsCodeFragment).commitNowAllowingStateLoss();
                }
                break;
            }

            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (fragmentType == FRAGMENT_SMS
                || fragmentType == FRAGMENT_VOICE) {
            registeredViewModel.setStatus(StartConstant.START_STATE_LOGIN_MOBILE);
            return;
        }

        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
            if (f instanceof VerifyMobileFragment) {
                ft.remove(f);
            } else if (f instanceof VerifySmsCodeFragment) {
                ft.remove(f);
            } else if (f instanceof VerifyVoiceCodeFragment) {
                ft.remove(f);
            }
        }
        ft.commitNowAllowingStateLoss();
    }

}