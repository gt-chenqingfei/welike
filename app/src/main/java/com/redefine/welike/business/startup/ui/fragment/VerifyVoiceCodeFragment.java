package com.redefine.welike.business.startup.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.redefine.commonui.widget.LoadingTranslateBgDlg;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.base.track.TrackerUtil;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.startup.ui.activity.VerifyPhoneActivity;
import com.redefine.welike.business.startup.ui.viewmodel.VerifyViewModel;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginLayout;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType;
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.truecaller.android.sdk.TrueProfile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gongguan
 * @time 2018/1/8 下午12:17
 */
public class VerifyVoiceCodeFragment extends Fragment {
    public static final String VOCIE_FRAGMENT_TAG = "voice_fragment";
    private static final int COUNT_DOWN_TOTAL = 60;
    private TextView tv_count, tv_resend;

    private View mView;
    private EditText et_sms_code;
    private TextView mSmsCodeHint, mBack;
    private ThirdLoginLayout thirdLoginLayout;
    private ViewGroup llParent;
    private ConstraintLayout logoCl;

    private int total = COUNT_DOWN_TOTAL;
    private static final int TIME_STEP = 1000;
    private CountDownTimer countDownTimer;
    private ImageView mIvDelete;
    private AppCompatImageView mVerifyIcon;

    private VerifyViewModel registeredViewModel;

    private LoadingTranslateBgDlg loadingDlg;
    private RegisterAndLoginModel eventModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_regist_sms_code, container, false);
        FragmentActivity activity = getActivity();
        registeredViewModel = ViewModelProviders.of(activity).get(VerifyViewModel.class);
        registeredViewModel.reqVoiceSMSCode(StartEventManager.getInstance().getPhone(), StartEventManager.getInstance().getPhone_location());
        registeredViewModel.reqCheckPhoneNum(StartEventManager.getInstance().getPhone());
        initViews();

        initEvents();

        setOnClick();

        setEvents();

        if (activity instanceof VerifyPhoneActivity) {
            eventModel = ((VerifyPhoneActivity) activity).getEventModel();
        }
        if (eventModel != null) {
            eventModel.verifyType = EventLog.RegisterAndLogin.VerifyType.SMS;
            eventModel.smsSend++;
            EventLog.RegisterAndLogin.report9(eventModel.newUser, eventModel.pageSource, eventModel.requestWay, eventModel.verifyType, eventModel.tcInstalled);
        }

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initViews() {
        logoCl = mView.findViewById(R.id.cl_logo);
        llParent = mView.findViewById(R.id.ll_parent);
        mSmsCodeHint = mView.findViewById(R.id.regist_otp_code_hint);
        mBack = mView.findViewById(R.id.tv_regist_back);
        tv_count = mView.findViewById(R.id.tv_regist_smscode_count);
        tv_resend = mView.findViewById(R.id.tv_regist_smscode_resend);
        et_sms_code = mView.findViewById(R.id.et_regist_sms_input);
        mIvDelete = mView.findViewById(R.id.regist_sms_delete);
        mVerifyIcon = mView.findViewById(R.id.verify_code_type);
        thirdLoginLayout = mView.findViewById(R.id.third_login_layout);
//        thirdLoginLayout.setThirdClickEnable(false);
    }

    public void initEvents() {
        mBack.setText(ResourceTool.getString("regist_change_to_sms"));
        String hint = ResourceTool.getString("regist_voice_code_hint");
        String mobile = StartEventManager.getInstance().getPhone();
        mSmsCodeHint.setText(String.format(hint, mobile));
        tv_resend.setText(ResourceTool.getString("regist_enter_code_resend"));
        et_sms_code.requestFocus();
        mVerifyIcon.setImageResource(R.drawable.verify_code_voice);

        tv_count.setText(String.valueOf(total));

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            if (!inputMethodManager.isActive()) {
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(total * 1000, TIME_STEP) {
                @Override
                public void onTick(long millisUntilFinished) {
                    total--;
                    String resentEx = ResourceTool.getString("regist_otp_resent");
                    String text = String.format(resentEx, total);
                    tv_count.setText(highKeyword(text, ResourceTool.getString("regist_otp_resent_key")));
                }

                @Override
                public void onFinish() {
                    countDownTimer.cancel();
                    tv_count.setVisibility(View.GONE);
                    tv_resend.setVisibility(View.VISIBLE);
                }

            }.start();
        }
    }

    public ThirdLoginLayout getThirdLoginLayout() {
        return thirdLoginLayout;
    }

    private void setEvents() {
        registeredViewModel.getCheckIsOldPhone().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) return;

                thirdLoginLayout.showButton(!aBoolean, !aBoolean);
                if (aBoolean) {
                    if (eventModel != null) {
                        eventModel.newUser = EventLog.RegisterAndLogin.NewUser.OLD_USER;
                    }
                } else {
                    if (eventModel != null) {
                        eventModel.newUser = EventLog.RegisterAndLogin.NewUser.NEW_USER;
                    }
                }
            }
        });

        thirdLoginLayout.registerCallback(new ThirdLoginCallback() {
            @Override
            public void onLoginBtnClick(ThirdLoginType thirdLoginType) {
                hideSoftInput();
                StartEventManager.getInstance().setPhone("");
//                registeredViewModel.tryThird(thirdLoginType);
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.FACEBOOK);
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.GOOGLE);
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER);
                }
            }

            @Override
            public void onLoginSuccess(ThirdLoginType thirdLoginType, ThirdLoginProfile profile) {
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    LoginResult facebookProfile = profile.facebookProfile;
                    if (facebookProfile != null) {
                        registeredViewModel.tryBindFacebook(facebookProfile.getAccessToken().getToken());
                    }
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.FACEBOOK, EventLog.RegisterAndLogin.ReturnResult.SUCCESS);
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    GoogleSignInAccount googleProfile = profile.googleProfile;
                    if (googleProfile != null) {
                        registeredViewModel.tryBindGoogle(googleProfile.getIdToken());
                    }
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.GOOGLE, EventLog.RegisterAndLogin.ReturnResult.SUCCESS);
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    TrueProfile trueCallerProfile = profile.mTrueCallerProfile;
                    if (trueCallerProfile != null) {
                        LogUtil.d("wng", "True caller get payload success : " + trueCallerProfile.payload);
                        registeredViewModel.tryBindTrueCaller(trueCallerProfile.payload, trueCallerProfile.signature, trueCallerProfile.signatureAlgorithm);
                    }
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER, EventLog.RegisterAndLogin.ReturnResult.SUCCESS);
                }
            }

            @Override
            public void onLoginFail(ThirdLoginType thirdLoginType, int errorCode) {
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    ToastUtils.showShort("Login failed");
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.FACEBOOK, EventLog.RegisterAndLogin.ReturnResult.FAIL);
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    ToastUtils.showShort("Login failed");
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.GOOGLE, EventLog.RegisterAndLogin.ReturnResult.FAIL);
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER, EventLog.RegisterAndLogin.ReturnResult.FAIL);
                }
            }

            @Override
            public void onLoginCancel(ThirdLoginType thirdLoginType) {
                String cancelText = ResourceTool.getString("canceled");
                ToastUtils.showShort(cancelText);
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.FACEBOOK, EventLog.RegisterAndLogin.ReturnResult.CANCEL);
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.GOOGLE, EventLog.RegisterAndLogin.ReturnResult.CANCEL);
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource.TRUE_CALLER, EventLog.RegisterAndLogin.ReturnResult.CANCEL);
                }
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (eventModel != null) {
                eventModel.verifyType = EventLog.RegisterAndLogin.VerifyType.VOICE;
            }
        }
    }

    public void setOnClick() {
        mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_sms_code.setText("");
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeredViewModel.setStatus(StartConstant.START_STATE_LOGIN_BACK_SMS_CODE);
                if (eventModel != null) {
                    EventLog.RegisterAndLogin.report13(eventModel.newUser, eventModel.phoneNumber, eventModel.smsSend, eventModel.pageStatus, eventModel.tcInstalled, eventModel.inputWay);
                }
            }
        });

        mView.findViewById(R.id.common_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeredViewModel.setStatus(StartConstant.START_STATE_LOGIN_MOBILE);
            }
        });

        et_sms_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                et_sms_code.setSelection(s.length());
                if (s.length() == 4) {
                    mIvDelete.setVisibility(View.VISIBLE);
                    next();
                    if (eventModel != null) {
                        eventModel.smsCheck++;
                        EventLog.RegisterAndLogin.report14(eventModel.newUser, eventModel.phoneNumber, eventModel.smsSend, eventModel.pageStatus, eventModel.tcInstalled, eventModel.inputWay);
                    }
                } else {
                    mIvDelete.setVisibility(View.GONE);
                }
            }
        });

        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_resend.setVisibility(View.GONE);
                tv_count.setVisibility(View.VISIBLE);
                total = COUNT_DOWN_TOTAL;
                countDownTimer.start();
                registeredViewModel.reqVoiceSMSCode(StartEventManager.getInstance().getPhone(), StartEventManager.getInstance().getPhone_location());

                if (eventModel != null) {
                    eventModel.smsSend++;
                    EventLog.RegisterAndLogin.report12(eventModel.newUser, eventModel.phoneNumber, eventModel.smsSend, eventModel.pageStatus, eventModel.stayTime, eventModel.tcInstalled);
                }
            }
        });

        //监听软键盘状态
        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                mView.getWindowVisibleDisplayFrame(rect);
                int height = mView.getRootView().getHeight();
                int heightDefere = height - rect.bottom;
                if (heightDefere > 200) {
                    TransitionManager.beginDelayedTransition(llParent);
                    logoCl.setVisibility(View.GONE);
                } else {

                    TransitionManager.beginDelayedTransition(llParent);
                    logoCl.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    public void next() {
        if (!TextUtils.isEmpty(et_sms_code.getText())) {

            String smsCode = et_sms_code.getText().toString();

            registeredViewModel.tryPhoneLogin(StartEventManager.getInstance().getPhone(), smsCode);

            hideSoftInput();
        }
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null)
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

    }

    /**
     * 高亮某个关键字，如果有多个则全部高亮
     */
    private SpannableString highKeyword(String str, String key) {
        if (getContext() == null) {
            return null;
        }

        SpannableString sp = new SpannableString(str);

        Pattern p = Pattern.compile(key);
        Matcher m = p.matcher(str);

        while (m.find()) {  //通过正则查找，逐个高亮
            int start = m.start();
            int end = m.end();
            sp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.main_grey)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sp;
    }

    private void reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource loginSource) {
        if (eventModel != null) {
            eventModel.loginSource = loginSource;
            EventLog.RegisterAndLogin.report10(eventModel.loginSource, eventModel.fromPage);
        }
    }

    private void reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource loginSource, EventLog.RegisterAndLogin.ReturnResult returnResult) {
        if (eventModel != null) {
            eventModel.loginSource = loginSource;
            eventModel.returnResult = returnResult;
            EventLog.RegisterAndLogin.report11(eventModel.loginSource, eventModel.returnResult, eventModel.pageStatus);
        }
    }

}
