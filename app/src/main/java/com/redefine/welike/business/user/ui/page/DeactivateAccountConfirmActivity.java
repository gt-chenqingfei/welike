package com.redefine.welike.business.user.ui.page;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonSingleConfirmDialog;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.PatternUtils;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.DeactivateManager;
import com.redefine.welike.business.user.management.request.CheckUserRequest;
import com.redefine.welike.business.user.ui.activity.DeactivateAccountResultActivity;
import com.redefine.welike.business.user.ui.listener.IAccountStatusChangeListener;
import com.redefine.welike.business.user.ui.listener.IDeactivateCheckListener;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginCallback;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginLayout;
import com.redefine.welike.commonui.thirdlogin.ThirdLoginType;
import com.redefine.welike.commonui.thirdlogin.bean.ThirdLoginProfile;
import com.redefine.welike.commonui.util.ToastUtils;
import com.truecaller.android.sdk.TrueProfile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by honglin on 2018/5/8.
 */
@Route(path = RouteConstant.PATH_USER_DEACT_CONFIRM)
public class DeactivateAccountConfirmActivity extends BaseActivity {

    private String TAG = "DeactivateAccountConfirmActivity";

    private TextView numIncorrect, btnNext;
    private EditText etPhoneNum;
    private ImageView ivDelete;
    private TextView tvDeactivateTitle;
    private TextView tvOR;
    private ImageView ivback;
    private TextView tvPageTitle;
    private ThirdLoginLayout mThirdLoginLayout;

    private Account account;

    private LoadingDlg mLoadingDlg;


    private View rootView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = LayoutInflater.from(this).inflate(R.layout.page_deactivate_confirm, null);
        setContentView(rootView);
        account = AccountManager.getInstance().getAccount();
        EventBus.getDefault().register(this);
        initView();

    }


    protected void initView() {

        tvPageTitle = rootView.findViewById(R.id.tv_common_title);
        ivback = findViewById(R.id.iv_common_back);
        btnNext = findViewById(R.id.btn_regist_phoneNum_next);
        etPhoneNum = findViewById(R.id.et_regist_phoneNum);
        ivDelete = findViewById(R.id.regist_phoneNum_delete);
        numIncorrect = findViewById(R.id.tv_regist_phoneNum_incorrect);
        tvOR = findViewById(R.id.tv_or);
        tvDeactivateTitle = findViewById(R.id.deactivate_title);
        mThirdLoginLayout = findViewById(R.id.third_login_layout);
        tvOR.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "or"));

        etPhoneNum.setHint(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "regist_input_mobile", false));
        tvPageTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_account", false));
        btnNext.setText(String.format(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_xxx"), account.getNickName()));
        tvDeactivateTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_info_0"));

        btnNext.setEnabled(false);

        setOnclick();
        mThirdLoginLayout.truecallerHideOnUnusable();

        mThirdLoginLayout.registerCallback(new ThirdLoginCallback() {
            @Override
            public void onLoginBtnClick(ThirdLoginType thirdLoginType) {

            }

            @Override
            public void onLoginSuccess(ThirdLoginType thirdLoginType, ThirdLoginProfile profile) {
                if (thirdLoginType == ThirdLoginType.FACEBOOK) {
                    LoginResult facebookProfile = profile.facebookProfile;
                    if (facebookProfile != null) {
                        DeactivateManager.getInstance().check(facebookProfile.getAccessToken().getToken(), CheckUserRequest.TYPE_FACEBOOK, deactivateCheckListener);
                    }
                } else if (thirdLoginType == ThirdLoginType.GOOGLE) {
                    GoogleSignInAccount googleProfile = profile.googleProfile;
                    if (googleProfile != null) {
                        DeactivateManager.getInstance().check(googleProfile.getIdToken(), CheckUserRequest.TYPE_GOOGLE, deactivateCheckListener);
                    }
                } else if (thirdLoginType == ThirdLoginType.TRUE_CALLER) {
                    TrueProfile trueCallerProfile = profile.mTrueCallerProfile;
                    if (trueCallerProfile != null) {
                        DeactivateManager.getInstance().check(trueCallerProfile.payload, trueCallerProfile.signature, trueCallerProfile.signatureAlgorithm, deactivateCheckListener);
                    }
                }
            }

            @Override
            public void onLoginFail(ThirdLoginType thirdLoginType, int errorCode) {
            }

            @Override
            public void onLoginCancel(ThirdLoginType thirdLoginType) {
            }
        });

    }

    private void setOnclick() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = etPhoneNum.getText().toString();
                if (PatternUtils.isPhoneNum(phoneNum)) {
                    mLoadingDlg = new LoadingDlg(DeactivateAccountConfirmActivity.this);
                    mLoadingDlg.show();
                    DeactivateManager.getInstance().check(phoneNum, CheckUserRequest.TYPE_MOBILE, deactivateCheckListener);
                }

                try {
                    InputMethodManager imm = (InputMethodManager) rootView.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btnNext.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPhoneNum.setText("");
            }
        });

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

        etPhoneNum.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 9) {
                    ivDelete.setVisibility(View.VISIBLE);
                    numIncorrect.setVisibility(View.INVISIBLE);
//                    btnNext.setBackgroundResource(R.drawable.common_appcolor_btn_bg);
                    btnNext.setEnabled(true);
                } else if (s.length() == 0) {
                    numIncorrect.setVisibility(View.VISIBLE);
                    numIncorrect.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "error_regist_input_phonenumber_not_empty", false));
                    ivDelete.setVisibility(View.GONE);
//                    btnNext.setBackgroundResource(R.drawable.common_gray_btn_bg);
                    btnNext.setEnabled(false);
                } else if (s.length() > 0 && s.length() < 11) {
                    ivDelete.setVisibility(View.GONE);
                    numIncorrect.setVisibility(View.INVISIBLE);
//                    btnNext.setBackgroundResource(R.drawable.common_gray_btn_bg);
                    btnNext.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mThirdLoginLayout.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


//    @Override
//    public void onPageStateChanged(int oldPageState, int pageState) {
//        super.onPageStateChanged(oldPageState, pageState);
//        if (pageState == BasePage.PAGE_STATE_DETACH) {
//            getView().getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {

        if (EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT == event.id) {

            finish();

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            if (rootView == null) return;
            rootView.getWindowVisibleDisplayFrame(rect);
            int height = rootView.getRootView().getHeight();
            int heightDefere = height - rect.bottom;
            tvOR.setVisibility(heightDefere < 200 ? View.VISIBLE : View.INVISIBLE);
            mThirdLoginLayout.setVisibility(heightDefere < 200 ? View.VISIBLE : View.INVISIBLE);
        }
    };


    /**
     * check is or not Current user login key
     */
    private IDeactivateCheckListener deactivateCheckListener = new IDeactivateCheckListener() {
        @Override
        public void onCheckInfo(boolean isCheck, final int type) {

            if (isCheck) {//deactivate

                // TODO: 2018/5/16
                DeactivateManager.getInstance().updataAccountStatis(getIntent().getExtras().getIntegerArrayList("reasonIds"), iAccountStatusChangeListener);

            } else {//dialog show error
                if (mLoadingDlg != null) {
                    mLoadingDlg.dismiss();
                }

                String message = "Check account failed!";

                CommonSingleConfirmDialog.showConfirmDialog(DeactivateAccountConfirmActivity.this,
                        message,
                        ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_ok"),
                        new CommonSingleConfirmDialog.IConfirmDialogListener() {
                            @Override
                            public void onClickConfirm() {
//                                etPhoneNum.setText("");
                            }
                        });
            }
        }
    };


    /**
     * check is or not Current user login key
     */
    private IAccountStatusChangeListener iAccountStatusChangeListener = new IAccountStatusChangeListener() {

        @Override
        public void onChanged(int status, final int errorCode) {


            if (mLoadingDlg != null) {
                mLoadingDlg.dismiss();
            }


            if (errorCode == ErrorCode.ERROR_SUCCESS) {
                DeactivateAccountResultActivity.luanch(DeactivateAccountConfirmActivity.this);
                DeactivateAccountConfirmActivity.this.finish();
                account.setStatus(2);
                AccountManager.getInstance().updateAccount(account);
                AccountManager.getInstance().logout();

            } else {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShort(ErrorCode.showErrCodeText(errorCode));
                    }
                });
            }
        }
    };


}
