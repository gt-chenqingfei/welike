package com.redefine.welike.business.user.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.user.management.request.RestoreRequest;
import com.redefine.welike.business.user.ui.vm.RestoreViewModel;
import com.redefine.welike.commonui.activity.MainActivityEx;
import com.redefine.welike.commonui.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by honglin on 2018/5/8.
 */

public class DeactivateAccountResultActivity extends BaseActivity {

    private String TAG = "DeactivateAccountResultActivity";

    private TextView btnQuit, tvRecallInfo2;
    private TextView tvReactivate, tvReister, tvHasDeactivate;

    private ImageView ivback;
    private TextView tvPageTitle;
    private RestoreViewModel restoreViewModel;

    private LoadingDlg mLoadingDlg;

    public static void luanch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DeactivateAccountResultActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactivate_result);
        restoreViewModel = ViewModelProviders.of(this).get(RestoreViewModel.class);
        initView();

        setOnclick();

        setEvent();
    }

    protected void initView() {

        tvPageTitle = findViewById(R.id.tv_common_title);
        ivback = findViewById(R.id.iv_common_back);
        ivback.setVisibility(View.INVISIBLE);
        btnQuit = findViewById(R.id.btn_quit);
        tvRecallInfo2 = findViewById(R.id.tv_recall_info2);
        tvReactivate = findViewById(R.id.tv_reactivate);
        tvReister = findViewById(R.id.tv_new);
        tvHasDeactivate = findViewById(R.id.tv_has_deactivate);

        tvPageTitle.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_account"));
        btnQuit.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_quit"));
        tvRecallInfo2.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_reactivate_info2"));
        tvReactivate.setText(highKeyword(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_reactivate_info"),
                ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_reactivate_0")));
        tvReister.setText(highKeyword(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_reactivate_info1"),
                ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_reactivate_info3")));
        tvHasDeactivate.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_has_deact"));

    }

    private void setOnclick() {

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadingDlg != null) {
                    mLoadingDlg.dismiss();
                }
//                MainActivityEx.exitApp(v.getContext());
                EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT, null));

                finish();
            }
        });
        tvReactivate.setOnClickListener(onReactivateListener);

        tvReister.setOnClickListener(onRegisterListener);


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
//todo
                        if (mLoadingDlg != null) {
                            mLoadingDlg.dismiss();
                        }
                        Toast.makeText(MyApplication.getAppContext(), ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_network_poor"), Toast.LENGTH_SHORT).show();

                        break;
                    case CONTENT:
                        if (mLoadingDlg != null) {
                            mLoadingDlg.dismiss();
                        }
                        Account account = AccountManager.getInstance().getAccount();
                        account.setStatus(0);
                        account.setCompleteLevel(Account.PROFILE_COMPLETE_LEVEL_MAIN_DONE);
                        account.setLogin(true);
                        AccountManager.getInstance().updateAccount(account);
//                        MainPageActivity.launcher(DeactivateAccountResultActivity.this);
                        MainActivityEx.show(DeactivateAccountResultActivity.this);
                        finish();
                        break;
                    case LOADING:
                        mLoadingDlg = new LoadingDlg(DeactivateAccountResultActivity.this);
                        mLoadingDlg.show();
                        break;
                }
            }
        });

    }

    private byte mBackPressedCount = 0;
    private ExitCountDownTimer mExitTimer;
    @Override
    public void onBackPressed() {
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
        if (mExitTimer == null) {
            mExitTimer = new ExitCountDownTimer(2000, 500);
        }
        mExitTimer.start();

        mBackPressedCount++;
        if (mBackPressedCount < 2) {
            ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "click_back_again", false));
        } else {
            AccountManager.getInstance().logout();
            EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT, null));
            super.onBackPressed();
        }

    }

    /**
     * 高亮某个关键字，如果有多个则全部高亮
     */
    private SpannableString highKeyword(String str, String key) {

        SpannableString sp = new SpannableString(str);

        Pattern p = Pattern.compile(key);
        Matcher m = p.matcher(str);

        while (m.find()) {  //通过正则查找，逐个高亮
            int start = m.start();
            int end = m.end();
            sp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.common_menu_share_text_color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sp;
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
            RestoreAccountActivity.luanch(v.getContext());
            finish();
        }
    };

    private class ExitCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public ExitCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mBackPressedCount = 0;
        }
    }
}
