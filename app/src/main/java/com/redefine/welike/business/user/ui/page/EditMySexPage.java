package com.redefine.welike.business.user.ui.page;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.commonui.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by gongguan on 2018/2/26.
 */
@Route(path = RouteConstant.EDIT_SEX_ROUTE_PATH)
public class EditMySexPage extends BaseActivity implements AccountManager.AccountCallback {
    private TextView mTvBoy, mTvSave, mTvGirl;
    private ImageView mIvBack, mBoyChecked, mGirlChecked;
    private byte sex;
    private LoadingDlg loadingDlg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_sex_page);

        initView();
    }

    private void initView() {
        TextView mTvEditSlogen = findViewById(R.id.tv_edit_sex_title);
        mTvBoy = findViewById(R.id.tv_edit_sex_boy);
        mTvGirl = findViewById(R.id.tv_edit_sex_girl);
        TextView mTvTitle = findViewById(R.id.tv_common_title);
        mIvBack = findViewById(R.id.iv_common_back);
        mTvSave = findViewById(R.id.user_edit_sex_save_tv);
        mBoyChecked = findViewById(R.id.tv_edit_sex_boy_check);
        mGirlChecked = findViewById(R.id.tv_edit_sex_girl_check);

        mTvSave.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_personal_edit_name_save"));
        mTvTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_host_gender_title"));
        mTvGirl.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "user_sex_girl"));
        mTvBoy.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "user_sex_boy"));
        mTvEditSlogen.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_user_edit_sex_slogen"));
        loadingDlg = new LoadingDlg(this);
        AccountManager.getInstance().register(this);
        Account account = AccountManager.getInstance().getAccount();
        mBoyChecked.setImageResource(account.getSex() == UserBase.MALE ? R.drawable.setting_language_checked : R.drawable.setting_language_check);
        mGirlChecked.setImageResource(account.getSex() == UserBase.FEMALE ? R.drawable.setting_language_checked : R.drawable.setting_language_check);

        setOnclick();
    }

    private void setOnclick() {
        final Account account = AccountManager.getInstance().getAccount().copy();

        mTvBoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = 0;
                mGirlChecked.setImageResource(R.drawable.setting_language_check);
                mBoyChecked.setImageResource(R.drawable.setting_language_checked);
                setNextBtnState(sex);
            }
        });

        mTvGirl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = 1;
                mGirlChecked.setImageResource(R.drawable.setting_language_checked);
                mBoyChecked.setImageResource(R.drawable.setting_language_check);
                setNextBtnState(sex);
            }
        });

        mTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account.getSex() != sex) {
                    account.setSex(sex);
                    AccountManager.getInstance().modifyAccount(account);
                    loadingDlg.show();
                }
            }
        });

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setNextBtnState(byte sex) {
        Account account = AccountManager.getInstance().getAccount();
        if (sex != account.getSex()) {
            mTvSave.setBackgroundResource(R.drawable.common_appcolor_btn_new_bg);
            mTvSave.setTextColor(ContextCompat.getColor(this,R.color.white));
        } else {
            mTvSave.setBackgroundResource(R.drawable.common_gray_btn_bg);
            mTvSave.setTextColor(ContextCompat.getColor(this,R.color.main_grey));
        }
    }

    @Override
    public void onModified() {
        Message message = Message.obtain();
        message.what = MessageIdConstant.MESSAGE_EDIT_SEX_SUCCESS;
        EventBus.getDefault().post(message);
        if (loadingDlg != null) {
            loadingDlg.dismiss();
        }
        finish();
    }

    @Override
    public void onModifyFailed(int errCode) {
        loadingDlg.dismiss();
        ToastUtils.showShort(ErrorCode.showErrCodeText(errCode));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AccountManager.getInstance().unregister(this);
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg = null;
        }
    }
}
