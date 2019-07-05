package com.redefine.welike.business.startup.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.redefine.foundation.utils.InputMethodUtil;
import com.redefine.foundation.utils.PatternUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.BuildConfig;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.base.track.TrackerUtil;
import com.redefine.welike.business.browse.ui.activity.AvoidTokenDiscoverActivity;
import com.redefine.welike.business.startup.management.PhoneNumberConveter;
import com.redefine.welike.business.startup.management.PhoneNumberProvider;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.startup.ui.activity.VerifyPhoneActivity;
import com.redefine.welike.business.startup.ui.viewmodel.VerifyViewModel;
import com.redefine.welike.common.UrlSwitcher;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginLayout;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType;
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.truecaller.android.sdk.TrueProfile;

/**
 * @author gongguan
 * @time 2018/1/8 下午12:09
 */
public class VerifyMobileFragment extends Fragment implements View.OnClickListener {
    public static final String MOBILE_FRAGMENT_TAG = "mobile_fragment";

    private View mView;
    private TextView num_incorrect, btn_next;
    private EditText et_phoneNum;
    private String country;
    private ImageView iv_delete, ivBack;

    private View mRegistLine;
    private View mLogo;
    private TextView tvBrowse;
    private LinearLayout llBrowse;
    private TextView tvOR;
    private boolean mInputShown = false;
    private int mEditTextSize = 0;
    private ThirdLoginLayout mThirdLoginLayout;

    private int magic = 0;
    private VerifyViewModel registeredViewModel;
    private RegisterAndLoginModel eventModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_regist_mobile, container, false);
        final FragmentActivity activity = getActivity();
        registeredViewModel = ViewModelProviders.of(activity).get(VerifyViewModel.class);
        country = "91";
        initViews();
        initEvents();
        setOnclick();

        if (activity instanceof VerifyPhoneActivity) {
            eventModel = ((VerifyPhoneActivity) activity).getEventModel();
        }
        if (eventModel != null) {
            EventLog.RegisterAndLogin.report5(eventModel.pageSource, eventModel.tcInstalled, eventModel.inputWay, eventModel.pageType, eventModel.loginVerifyType, eventModel.accountStatus);
        }
        StartEventManager.getInstance().setVidmate_page_source(2);
        mView.findViewById(R.id.magic_mirror).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magic++;
                if (BuildConfig.DEBUG && magic > 3) {
                    UrlSwitcher.showSwitcher(activity);
                }
            }
        });
        et_phoneNum.post(new Runnable() {
            @Override
            public void run() {
                et_phoneNum.requestFocus();
                InputMethodUtil.showInputMethod(et_phoneNum);
            }
        });
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        magic = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null)
            mView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist_phoneNum_next:
                String phoneNum = et_phoneNum.getText().toString();
                if (PatternUtils.isPhoneNum(phoneNum)) {
                    et_phoneNum.setFocusable(false);
                    jumpNextPage(country, phoneNum);
                } else if (TextUtils.getTrimmedLength(phoneNum) == 0) {
                    num_incorrect.setText(ResourceTool.getString("error_regist_input_phonenumber_not_empty"));
                    num_incorrect.setVisibility(View.VISIBLE);
                } else if (TextUtils.getTrimmedLength(phoneNum) > 0) {
                    num_incorrect.setText(ResourceTool.getString("error_regist_input_phonenumber_incorrect"));
                    num_incorrect.setVisibility(View.VISIBLE);
                }

                StartEventManager.getInstance().setPhone(phoneNum);

                if (eventModel != null) {
                    eventModel.phoneNumber = phoneNum;
                    EventLog.RegisterAndLogin.report6(phoneNum, eventModel.pageSource, eventModel.newUser);
                }
                break;
            case R.id.common_back_btn:
                getActivity().finish();
                InputMethodUtil.hideInputMethod(et_phoneNum);
                break;
        }
    }

    public void onFailed(int errCode) {
        String showtext = ErrorCode.showErrCodeText(errCode);
        if (!TextUtils.isEmpty(showtext)) {
            ToastUtils.showShort(showtext);
        }
    }

    private void initViews() {
        mLogo = mView.findViewById(R.id.regist_logo);
        btn_next = mView.findViewById(R.id.btn_regist_phoneNum_next);
        ivBack = mView.findViewById(R.id.common_back_btn);
        tvBrowse = mView.findViewById(R.id.tv_browse);
        llBrowse = mView.findViewById(R.id.ll_browse);
        et_phoneNum = mView.findViewById(R.id.et_regist_phoneNum);
        iv_delete = mView.findViewById(R.id.regist_phoneNum_delete);
        num_incorrect = mView.findViewById(R.id.tv_regist_phoneNum_incorrect);
        mThirdLoginLayout = mView.findViewById(R.id.third_login_layout);
        mThirdLoginLayout.truecallerHideOnUnusable();

        mRegistLine = mView.findViewById(R.id.regist_line);
        tvOR = mView.findViewById(R.id.tv_or);
        tvOR.setText(ResourceTool.getString("or"));

        ivBack.setVisibility(View.VISIBLE);
    }

    private void initEvents() {
        et_phoneNum.setHint(ResourceTool.getString("regist_input_mobile"));
        btn_next.setText(ResourceTool.getString("regist_next_btn"));
        tvBrowse.setText(ResourceTool.getString("common_browse_info"));
    }

    public ThirdLoginLayout getThirdLoginLayout() {
        return mThirdLoginLayout;
    }

    private void setOnclick() {
        ivBack.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tvBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AvoidTokenDiscoverActivity.Companion.launch(getActivity());
            }
        });
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_phoneNum.setText("");
            }
        });

        //监听软键盘状态
        mView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        mThirdLoginLayout.registerCallback(new ThirdLoginCallback() {
            @Override
            public void onLoginBtnClick(ThirdLoginType thirdLoginType) {
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

        et_phoneNum.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditTextSize = s.length();
                if (mEditTextSize > 0) {
                    dismissPopup();
                    if (mEditTextSize == 5) {
                        //有个打点字段叫phone_source，1表示用户选择，2表示用户输入。
                        //如果是用户输入，一定会经历长度为5的阶段
                        if (eventModel != null) {
                            eventModel.inputWay = EventLog.RegisterAndLogin.InputWay.INPUT;
                        }
                    }
                } else {
                    if (mInputShown) {
                        showPhonePup();
                    }
                }
                if (s.length() > 9) {
                    iv_delete.setVisibility(View.VISIBLE);
                    num_incorrect.setVisibility(View.INVISIBLE);
                    btn_next.setEnabled(true);
                } else if (s.length() == 0) {

//                    mScroll.fullScroll(ScrollView.FOCUS_DOWN);
                    num_incorrect.setVisibility(View.VISIBLE);
                    num_incorrect.setText(ResourceTool.getString("error_regist_input_phonenumber_not_empty"));
                    iv_delete.setVisibility(View.GONE);
//                    btn_next.setBackgroundResource(R.drawable.common_gray_btn_bg);
                    btn_next.setEnabled(false);
                } else if (s.length() > 0 && s.length() < 11) {
                    iv_delete.setVisibility(View.GONE);
                    num_incorrect.setVisibility(View.INVISIBLE);
//                    btn_next.setBackgroundResource(R.drawable.common_gray_btn_bg);
                    btn_next.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    private void showPhonePup() {
        Context context = mView.getContext();
        final String[] shownPhoneNumber = PhoneNumberProvider.getShownPhoneNumber(context);
        if (shownPhoneNumber != null) {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    popupPhone(shownPhoneNumber);
                }
            });
        }

    }

    private ListPopupWindow popupWindow;

    private void popupPhone(final String[] phoneNumbers) {
        if (getActivity() == null) {
            return;
        }
        View anchor = mView.findViewById(R.id.regist_line);
        if (popupWindow == null) {
            popupWindow = new ListPopupWindow(getActivity());
        }
        popupWindow.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, phoneNumbers));
        popupWindow.setAnchorView(anchor);
        popupWindow.setVerticalOffset(10);
        popupWindow.setWidth(anchor.getMeasuredWidth());
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phoneNumber = phoneNumbers[position];
                setPhoneEditText(phoneNumber);
                popupWindow.dismiss();
                if (eventModel != null) {
                    eventModel.inputWay = EventLog.RegisterAndLogin.InputWay.CLICK_LIST;
                }
            }
        });
        popupWindow.show();
    }

    private void dismissPopup() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private void setPhoneEditText(String phoneNumber) {
        String[] nationCodePhone = PhoneNumberConveter.convertPhone(phoneNumber);
        country = nationCodePhone[0];
        et_phoneNum.setText(nationCodePhone[1]);
        try {
            et_phoneNum.setSelection(et_phoneNum.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void jumpNextPage(String nationCode, String phoneNum) {
        if (!TextUtils.isEmpty(phoneNum)) {
            StartEventManager.getInstance().setPhone_location(nationCode);
            registeredViewModel.setStatus(StartConstant.START_STATE_LOGIN_SMS_CODE);
        }
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            mView.getWindowVisibleDisplayFrame(rect);
            int height = mView.getRootView().getHeight();
            int heightDefere = height - rect.bottom;
            if (heightDefere > 200) {
//                    mScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mThirdLoginLayout.getLayoutParams();
            RelativeLayout.LayoutParams mobileParams = (RelativeLayout.LayoutParams) et_phoneNum.getLayoutParams();
            if (heightDefere < 200) {
                tvOR.setVisibility(View.VISIBLE);
                mLogo.setVisibility(View.VISIBLE);
                mobileParams.topMargin = DensityUtil.dp2px(35);
                layoutParams.addRule(RelativeLayout.BELOW, tvOR.getId());
                layoutParams.topMargin = DensityUtil.dp2px(10);
                mRegistLine.setBackgroundColor(mRegistLine.getContext().getResources().getColor(R.color.main_grey));
            } else {
                tvOR.setVisibility(View.INVISIBLE);
                mLogo.setVisibility(View.GONE);
                mobileParams.topMargin = DensityUtil.dp2px(130);
                layoutParams.addRule(RelativeLayout.BELOW, R.id.regist_line);
                layoutParams.topMargin = DensityUtil.dp2px(30);
                mRegistLine.setBackgroundColor(mRegistLine.getContext().getResources().getColor(R.color.main_orange_dark));
            }

            mInputShown = heightDefere > 200;
            if (heightDefere > 200 && mEditTextSize == 0) {
                showPhonePup();
            } else {
                dismissPopup();
                num_incorrect.setVisibility(View.INVISIBLE);
            }
        }
    };

    private void reportClickThirdLoginClick(EventLog.RegisterAndLogin.LoginSource loginSource) {
        if (eventModel != null) {
            eventModel.loginSource = loginSource;
            EventLog.RegisterAndLogin.report7(eventModel.loginSource, eventModel.fromPage, eventModel.loginVerifyType, eventModel.accountStatus);
        }
    }

    private void reportClickThirdLoginCallback(EventLog.RegisterAndLogin.LoginSource loginSource, EventLog.RegisterAndLogin.ReturnResult returnResult) {
        if (eventModel != null) {
            eventModel.loginSource = loginSource;
            eventModel.returnResult = returnResult;
            EventLog.RegisterAndLogin.report8(eventModel.loginSource, eventModel.returnResult);
        }
    }

}
