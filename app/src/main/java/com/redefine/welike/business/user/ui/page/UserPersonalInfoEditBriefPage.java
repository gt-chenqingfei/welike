package com.redefine.welike.business.user.ui.page;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pekingese.pagestack.framework.page.PageName;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.hive.AppsFlyerManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by gongguan on 2018/2/24.
 */
@PageName("UserPersonalInfoEditBriefPage")
@Route(path = RouteConstant.EDIT_BRIE_ROUTE_PATH)
public class UserPersonalInfoEditBriefPage extends BaseActivity implements AccountManager.AccountCallback {
    private TextView mTv_inputCount;
    private TextView mBtnSave;
    private ImageView mIvBack;
    private EditText mEditBrief;
    private LinearLayout mLlEdit;
    private LoadingDlg loadingDlg;
    private TextView mTvEditError;
    private String mOldIntrodution;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_personal_info_edit_brief);

        initView();
        setOnclick();
    }

    private void initView() {
        TextView mTitle = findViewById(R.id.tv_common_title);
        mIvBack = findViewById(R.id.iv_common_back);
        mBtnSave = findViewById(R.id.btn_user_info_edit_brief);
        mEditBrief = findViewById(R.id.edit_user_edit_brief);
        mTv_inputCount = findViewById(R.id.edit_user_edit_brief_count);
        mLlEdit = findViewById(R.id.ll_user_edit_brief);
        mTvEditError = findViewById(R.id.tv_edit_brief_error);

        AccountManager.getInstance().register(this);
        mTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_personal_info_page_title"));
        mBtnSave.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_personal_edit_name_save"));
        mEditBrief.setText(AccountManager.getInstance().getAccount().getIntroduction());
        loadingDlg = new LoadingDlg(this);

        if (!TextUtils.isEmpty(AccountManager.getInstance().getAccount().getIntroduction())) {
            String inputCount = String.valueOf(175 - AccountManager.getInstance().getAccount().getIntroduction().length());
            mTv_inputCount.setText(inputCount);
        } else {
            mTv_inputCount.setText(String.valueOf(175));
        }

        mOldIntrodution = AccountManager.getInstance().getAccount().getIntroduction();

    }

    private void setOnclick() {
        mLlEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditBrief.setFocusable(true);
                mEditBrief.setFocusableInTouchMode(true);
                mEditBrief.requestFocus();
                mEditBrief.requestFocusFromTouch();
                showSoftInput();
                mEditBrief.setSelection(mEditBrief.getText().length());
            }
        });

        mEditBrief.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputCount = String.valueOf(175 - s.length());
                mTv_inputCount.setText(inputCount);
                mTvEditError.setVisibility(View.INVISIBLE);
                if (!TextUtils.equals(mOldIntrodution, mEditBrief.getText().toString())) {
                    mBtnSave.setBackgroundResource(R.drawable.common_appcolor_btn_new_bg);
                    mBtnSave.setTextColor(ContextCompat.getColor(UserPersonalInfoEditBriefPage.this, R.color.white));
                } else {
                    mBtnSave.setBackgroundResource(R.drawable.common_gray_btn_bg);
                    mBtnSave.setTextColor(ContextCompat.getColor(UserPersonalInfoEditBriefPage.this, R.color.main_grey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(mOldIntrodution, mEditBrief.getText().toString())) {
                    Account account = AccountManager.getInstance().getAccount().copy();
                    if (TextUtils.isEmpty(mEditBrief.getText().toString())) {
                        account.setIntroduction("");
                    } else {
                        String introduce = deleteBlank(mEditBrief.getText().toString());
                        account.setIntroduction(introduce);
                    }
                    AppsFlyerManager.addEvent(AppsFlyerManager.EVENT_SIGN);
                    AccountManager.getInstance().modifyAccount(account);
                    loadingDlg.show();
                }
            }
        });

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountManager.getInstance().getAccount().setIntroduction(mOldIntrodution);
                finish();
            }
        });
    }

    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(mEditBrief.getWindowToken(), 0);
    }

    private void showSoftInput() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && mEditBrief != null)
            imm.showSoftInput(mEditBrief, InputMethodManager.SHOW_IMPLICIT);
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
        message.what = MessageIdConstant.MESSAGE_EDIT_INTRODUCTION_SUCCESS;
        EventBus.getDefault().post(message);
        finish();
    }

    @Override
    public void onModifyFailed(int errCode) {
        loadingDlg.dismiss();
        mBtnSave.setBackgroundResource(R.drawable.common_gray_btn_bg);
        mBtnSave.setTextColor(ContextCompat.getColor(this, R.color.main_grey));
        mTvEditError.setVisibility(View.VISIBLE);
        mTvEditError.setText(ErrorCode.showErrCodeText(errCode));
    }

    private String deleteBlank(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        StringBuilder contentBuilder = new StringBuilder();
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (!TextUtils.isEmpty(line)) {
                contentBuilder.append(line).append("\n");
            }
        }
        if (contentBuilder.length() > 0) {
            contentBuilder.deleteCharAt(contentBuilder.length() - 1);
        }
        return contentBuilder.toString();
    }

}
