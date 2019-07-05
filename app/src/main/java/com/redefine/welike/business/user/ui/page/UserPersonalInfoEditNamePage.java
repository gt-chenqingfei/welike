package com.redefine.welike.business.user.ui.page;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.constant.UserConstants;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.startup.management.bean.NickNameCheckerBean;
import com.redefine.welike.business.user.ui.vm.NickNameViewModel;
import com.redefine.welike.commonui.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by gongguan on 2018/2/24.
 */
@Route(path = RouteConstant.EDIT_NAME_ROUTE_PATH)
public class UserPersonalInfoEditNamePage extends BaseActivity implements AccountManager.AccountCallback {
    private TextView mTvNickNameError, mTitle, mBtnSave, mTvAttention;
    private EditText mEt_name;
    private View etDiv;
    private ImageView mIvBack, iv_name_correct;

    private ProgressBar iv_check_name;


    private NickNameViewModel nickNameViewModel;

    private LoadingDlg loadingDlg;

    private boolean canNext = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_edit_nick_name);
        nickNameViewModel = ViewModelProviders.of(this).get(NickNameViewModel.class);
        initView();
        setOnclick();
    }

    private void initView() {
        mTitle = findViewById(R.id.tv_common_title);
        mTvNickNameError = findViewById(R.id.tv_edit_nick_error);
        mBtnSave = findViewById(R.id.btn_user_edit_name_save);
        mEt_name = findViewById(R.id.et_regist_name);
        mIvBack = findViewById(R.id.iv_common_back);
        iv_check_name = findViewById(R.id.iv_regist_check_anim);
        iv_name_correct = findViewById(R.id.iv_regist_check_correct);
        mTvAttention = findViewById(R.id.tv_edit_nickname_attention);
        etDiv = findViewById(R.id.edit_name_dv);

        Account account = AccountManager.getInstance().getAccount();
        int curLevel = account.getCurLevel();
        int changeNameCount = account.getChangeNameCount();
        String pre = "";
        if (curLevel > UserConstants.USER_COMMON_USER && changeNameCount > 0) {
            pre = String.format(ResourceTool.getString("profile_update_nick_count_pro"), String.valueOf(changeNameCount));
        } else {
            pre = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "profile_update_nick_count_common");
        }
        String inputSlogen = ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_personal_edit_name_slogen");
        mTvAttention.setText(pre + "\n" + inputSlogen);
        mTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_personal_info_page_title"));

        mBtnSave.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_personal_edit_name_save"));
        mBtnSave.setBackgroundResource(R.drawable.common_gray_btn_bg);
        mEt_name.setTextColor(getResources().getColor(R.color.mine_user_host_personal_nickName));

        mEt_name.setText(AccountManager.getInstance().getAccount().getNickName());

        loadingDlg = new LoadingDlg(this);
        AccountManager.getInstance().register(this);
    }

    private void setOnclick() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account account = AccountManager.getInstance().getAccount().copy();
                if (canNext && !TextUtils.equals(account.getNickName(), mEt_name.getText().toString())) {
                    account.setNickName(mEt_name.getText().toString());
                    AccountManager.getInstance().modifyAccount(account);
                    loadingDlg.show();
                }
            }
        });

        mEt_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                nickNameViewModel.checkNickName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
        mEt_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    etDiv.setBackgroundColor(ContextCompat.getColor(UserPersonalInfoEditNamePage.this, R.color.main_orange_dark));
                } else {
                    etDiv.setBackgroundColor(ContextCompat.getColor(UserPersonalInfoEditNamePage.this, R.color.main_grey));
                }

            }
        });


        nickNameViewModel.getCheckStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {

                if (pageStatusEnum == null) return;

                switch (pageStatusEnum) {


                    case LOADING:

                        checkLoading();
                        break;

                    case CONTENT:
                        checkContent();
                        break;

                }
            }
        });

        nickNameViewModel.getNickNameBeanData().observe(this, new Observer<NickNameCheckerBean>() {
            @Override
            public void onChanged(@Nullable NickNameCheckerBean nickNameCheckerBean) {

                if (nickNameCheckerBean == null) return;
                if (nickNameCheckerBean.getErrCode() == ErrorCode.ERROR_SUCCESS) {

                    if (nickNameCheckerBean.getRepeat()) {

                        if (nickNameCheckerBean.getGt()) {
                            mTvNickNameError.setText(String.format(getResources().getString(R.string.register_nickname_use_more), nickNameCheckerBean.getRepeatNum()));
                        } else {
                            mTvNickNameError.setText(String.format(getResources().getString(R.string.register_nickname_use_less), nickNameCheckerBean.getRepeatNum()));
                        }

                    } else {

                        mTvNickNameError.setText(getResources().getString(R.string.register_nickname_unique));

                    }
                    mTvNickNameError.setTextColor(getResources().getColor(R.color.main_grey));
                    canNext = true;

                } else {
                    mTvNickNameError.setTextColor(getResources().getColor(R.color.main_error));
                    mTvNickNameError.setText((ErrorCode.showErrCodeText(nickNameCheckerBean.getErrCode())));
                    canNext = false;
                }
                setNextBtnState();

            }
        });

    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(mEt_name.getWindowToken(), 0);
    }

    private void setNextBtnState() {
        Account account = AccountManager.getInstance().getAccount();
        if (canNext && !TextUtils.equals(mEt_name.getText().toString(), account.getNickName())) {
            mBtnSave.setBackgroundResource(R.drawable.common_appcolor_btn_new_bg);
            mBtnSave.setTextColor(ContextCompat.getColor(UserPersonalInfoEditNamePage.this, R.color.white));
        } else {
            mBtnSave.setBackgroundResource(R.drawable.common_gray_btn_bg);
            mBtnSave.setTextColor(ContextCompat.getColor(UserPersonalInfoEditNamePage.this, R.color.main_grey));
        }
    }


    private void checkLoading() {
        iv_check_name.setVisibility(View.VISIBLE);

        mTvNickNameError.setVisibility(View.GONE);

    }

    private void checkContent() {
        iv_check_name.setVisibility(View.INVISIBLE);
        mTvNickNameError.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideSoftInput();
        AccountManager.getInstance().unregister(this);
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg = null;
        }
    }

    @Override
    public void onModified() {
        if (loadingDlg != null) {
            loadingDlg.dismiss();
        }
        Message message = Message.obtain();
        message.what = MessageIdConstant.MESSAGE_EDIT_NICK_NAME_SUCCESS;
        EventBus.getDefault().post(message);
        finish();
    }

    @Override
    public void onModifyFailed(int errCode) {
        loadingDlg.dismiss();
        ToastUtils.showShort(ErrorCode.showErrCodeText(errCode));
    }
}
