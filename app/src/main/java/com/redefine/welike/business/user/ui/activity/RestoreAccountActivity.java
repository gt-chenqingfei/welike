package com.redefine.welike.business.user.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.ui.activity.AvoidTokenDiscoverActivity;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.business.startup.management.constant.RegisteredConstant;
import com.redefine.welike.business.startup.ui.activity.RegistActivity;
import com.redefine.welike.business.user.management.request.RestoreRequest;
import com.redefine.welike.business.user.ui.vm.RestoreViewModel;
import com.redefine.welike.commonui.activity.MainActivityEx;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.bean.RegisterAndLoginModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by honglin on 2018/5/17.
 */

public class RestoreAccountActivity extends BaseActivity {


    private TextView tvRestoreInfo;
    private TextView btnReactivate, btnReister;

    private ImageView ivback;
    private TextView tvPageTitle;
    private RestoreViewModel restoreViewModel;
    private LoadingDlg mLoadingDlg;

    private Account account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restore_account);

        restoreViewModel = ViewModelProviders.of(this).get(RestoreViewModel.class);

        account = AccountManager.getInstance().getAccount();

        initView();

        setOnclick();

        setEvent();
    }


    public static void luanch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RestoreAccountActivity.class);
        context.startActivity(intent);
    }

    protected void initView() {

        tvPageTitle = findViewById(R.id.tv_common_title);
        ivback = findViewById(R.id.iv_common_back);
        tvRestoreInfo = findViewById(R.id.tv_restore_info2);
        btnReactivate = findViewById(R.id.btn_reactivate);
        btnReister = findViewById(R.id.btn_register);

        tvPageTitle.setText(ResourceTool.getString("user_restore_title"));
        btnReactivate.setText(String.format(ResourceTool.getString("user_reactivate_xxx"), account.getNickName()));
        btnReister.setText(ResourceTool.getString("user_register"));
        tvRestoreInfo.setText(ResourceTool.getString("user_restore_info"));

    }

    private void setOnclick() {

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StartManager.getInstance().logout();
                AvoidTokenDiscoverActivity.Companion.launch(v.getContext());
                finish();
            }
        });
        btnReactivate.setOnClickListener(onReactivateListener);

        btnReister.setOnClickListener(onRegisterListener);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        StartManager.getInstance().logout();
        AvoidTokenDiscoverActivity.Companion.launch(this);
    }

    private void setEvent() {
        restoreViewModel.getmPageStatus().observe(this, new Observer<PageStatusEnum>() {
            @Override
            public void onChanged(@Nullable PageStatusEnum pageStatusEnum) {
                if (pageStatusEnum == null) return;
                switch (pageStatusEnum) {
                    case EMPTY:

                        break;
                    case ERROR:
                        if (mLoadingDlg != null) {
                            mLoadingDlg.dismiss();
                        }
                        break;
                    case CONTENT:
                        if (mLoadingDlg != null) {
                            mLoadingDlg.dismiss();
                        }
                        break;
                    case LOADING:
                        mLoadingDlg = new LoadingDlg(RestoreAccountActivity.this);
                        mLoadingDlg.show();
                        break;
                }
            }
        });
        restoreViewModel.getAccounyStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {

                if (integer == null) return;


                if (integer == RestoreRequest.TYPE_NEW_ACCOUNT) {
                    RegistActivity.show(RestoreAccountActivity.this, RegisteredConstant.FRAGMENT_USERINFO, new RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LOGOUT));
                    StartManager.getInstance().logout4new();
                    finish();
                } else if (integer == RestoreRequest.TYPE_RESTORE) {

                    Account account = AccountManager.getInstance().getAccount();
                    account.setStatus(0);
                    account.setLogin(true);
                    account.setCompleteLevel(Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE);
                    AccountManager.getInstance().updateAccount(account);
                    MainActivityEx.show(RestoreAccountActivity.this);
                    EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_MINE));//
                    EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN, null));
                    finish();
                }


            }
        });

        restoreViewModel.getErrcode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                Toast.makeText(RestoreAccountActivity.this, ErrorCode.showErrCodeText(integer), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private View.OnClickListener onReactivateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            restoreViewModel.restore(RestoreRequest.TYPE_RESTORE);

        }
    };

    private View.OnClickListener onRegisterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            CommonConfirmDialog.showConfirmDialog(v.getContext()
                    , ResourceTool.getString("user_register_title")
                    , ResourceTool.getString("common_cancel")
                    , ResourceTool.getString("common_continue")
                    , new CommonConfirmDialog.IConfirmDialogListener() {

                        @Override
                        public void onClickCancel() {

                        }

                        @Override
                        public void onClickConfirm() {
                            restoreViewModel.restore(RestoreRequest.TYPE_NEW_ACCOUNT);
                        }
                    });
        }
    };


}
