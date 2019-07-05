package com.redefine.welike.business.mysetting.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.dialog.CommonConfirmDialog;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.utils.ChannelHelper;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.SpManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.mysetting.ui.view.ClearCache;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.StartManager;
import com.redefine.welike.business.user.ui.dialog.UpdateDialog;
import com.redefine.welike.cache.WeLikeCacheManager;
import com.redefine.welike.commonui.event.commonenums.BooleanValue;
import com.redefine.welike.commonui.util.ToastUtils;
import com.redefine.welike.statistical.EventLog1;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author gongguan
 * @time 2018/1/15 下午12:03
 */
@Route(path = RouteConstant.SETTING_ROUTE_PATH)
public class SettingActivity extends BaseActivity implements ClearCache, SpManager.SPDataListener, TimePickerDialog.OnTimeSetListener {
    private ImageView iv_back, mShowRedPoint;
    private LinearLayout ll_clearCache, mLlUpdate;
    private TextView mTvClear, tv_setting_logout, title, mTvClearText, tvSettingDeactivate,
            mTvUpdate, mVersion, mTvNotification, tvPrivacy;
    private LoadingDlg loadingDlg;
    private ClearCache clearCache;
    private CommonConfirmDialog mDeactivateDialog, mLogoutDialog, mClearCacheDialog;
    private TextView mLanguageItem;
    private TextView mBlockItem;
    private View ll_notification;
    private String mUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        initEvents();
        setOclick();
        SpManager.getInstance().register(this);
        StartEventManager.getInstance().setVidmate_page_source(3);
        EventBus.getDefault().register(this);
        EventLog1.SettingPage.report1(mUid);
    }

    private void initView() {
        clearCache = this;
        loadingDlg = new LoadingDlg(this, ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_pop_loading_text"));
        iv_back = findViewById(R.id.iv_common_back);
        title = findViewById(R.id.tv_common_title);
        ll_clearCache = findViewById(R.id.ll_clearCache);
        ll_notification = findViewById(R.id.ll_mute_notification);
        mLlUpdate = findViewById(R.id.ll_check_update);
        mShowRedPoint = findViewById(R.id.mine_check_update_red_point);
        mLanguageItem = findViewById(R.id.setting_language_item);
        mBlockItem = findViewById(R.id.setting_block_item);

        tvPrivacy = findViewById(R.id.setting_privacy_item);
        mTvClear = findViewById(R.id.tv_mine_clear_cache);
        mTvUpdate = findViewById(R.id.tv_check_update);
        tv_setting_logout = findViewById(R.id.tv_setting_logout);
        tvSettingDeactivate = findViewById(R.id.tv_setting_deactivate);
        mTvNotification = findViewById(R.id.tv_mute_notification);


        mTvClearText = findViewById(R.id.tv_mine_clear_cache_text);
        mVersion = findViewById(R.id.tv_setting_app_version);
//        String versionName = ChannelHelper.getTagString(MyApplication.getApp());
//        versionName = versionName.replace("vidmate", "v");
//        versionName = versionName.replace("facebook", "f");
//        mVersion.setText("V" + CommonHelper.getAppVersionName(this) + " " + versionName);
        mVersion.setText("V" + CommonHelper.getAppVersionName(this));
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                final String fileSize = WeLikeCacheManager.getCacheSizeShow(getApplicationContext());
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        mTvClearText.setText(fileSize);
                    }
                });
            }
        });
    }

    private void initEvents() {
        Account account = AccountManager.getInstance().getAccount();

        if (account == null || account.getStatus() == 3) {
            tvSettingDeactivate.setVisibility(View.GONE);
        }
        mUid = account.getUid();
        mShowRedPoint.setImageResource(R.drawable.setting_check_update_red_point);
        title.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_text"));

        mTvClear.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_text"));
        mLanguageItem.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_language_text"));
        mTvUpdate.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_check_the_update"));
        tv_setting_logout.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_log_out_text"));
        tvSettingDeactivate.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_account"));
        mTvNotification.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "setting_notification_mute_time"));
        mBlockItem.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "block"));
        tvPrivacy.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_privacy"));

        mTvUpdate.setTextColor(getResources().getColor(R.color.mine_setting_type_text_color));
        mTvClear.setTextColor(getResources().getColor(R.color.mine_setting_type_text_color));


        boolean isUpgrade = SpManager.Setting.getCurrentUpgraded(this);
        String startTime = SpManager.Setting.getNotificationStartTime(this);
        String endTime = SpManager.Setting.getNotificationEndTime(this);
        String notificationTime = startTime + '-' + endTime;

        if (isUpgrade) {
            mShowRedPoint.setVisibility(View.VISIBLE);
        } else {
            mShowRedPoint.setVisibility(View.GONE);
        }


    }

    private void setOclick() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ll_clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog1.SettingPage.report2(mUid,EventLog1.SettingPage.ClickType.CLEARCACHE);
                mClearCacheDialog = CommonConfirmDialog.showConfirmDialog(v.getContext()
                        , ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_pop_title")
                        , new CommonConfirmDialog.IConfirmDialogListener() {

                            @Override
                            public void onClickCancel() {

                            }

                            @Override
                            public void onClickConfirm() {
                                clearCache.setOnClearCache(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_pop_title"));
                            }
                        });
                mClearCacheDialog.show();
            }
        });

        mLanguageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog1.SettingPage.report2(mUid,EventLog1.SettingPage.ClickType.LANGUAGE);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SETTING_LANGUAGE, null));
            }
        });

        mBlockItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog1.SettingPage.report2(mUid,EventLog1.SettingPage.ClickType.BLOCK);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_BLOCK_PAGE));
            }
        });

        tv_setting_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EventLog1.SettingPage.report2(mUid,EventLog1.SettingPage.ClickType.LOGOUT);
                mLogoutDialog = CommonConfirmDialog.showCancelDialog(view.getContext()
                        , ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_exit_app_pop_title")
                        , new CommonConfirmDialog.IConfirmDialogListener() {

                            @Override
                            public void onClickCancel() {
                                EventLog1.Setting.report1(BooleanValue.NO);
                            }

                            @Override
                            public void onClickConfirm() {
                                StartManager.getInstance().logout();
                                EventLog1.Setting.report1(BooleanValue.YES);
                            }
                        });

                mLogoutDialog.show();

            }

        });
        tvSettingDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeactivateDialog = CommonConfirmDialog.showCancelDialog(v.getContext()
                        , ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "user_deactivate_info_title")
                        , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_cancel")
                        , ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "common_continue")
                        , new CommonConfirmDialog.IConfirmDialogListener() {

                            @Override
                            public void onClickCancel() {

                            }

                            @Override
                            public void onClickConfirm() {
                                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_DEACTIVATE_PAGE, null));
                            }
                        });
            }
        });

        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog1.SettingPage.report2(mUid,EventLog1.SettingPage.ClickType.PRIVACY);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_SETTING_PRIVACY_PAGE, null));
            }
        });


        mLlUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventLog1.SettingPage.report2(mUid,EventLog1.SettingPage.ClickType.CHECKORUPDATES);
                if (SpManager.Setting.getCurrentUpgraded(SettingActivity.this)) {
                    UpdateDialog updateDialog = new UpdateDialog(SettingActivity.this, false);
                    updateDialog.show();
                } else {
                    ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_check_the_update_version_latest"));
                }
            }
        });
        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        SettingActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd.show(((Activity) (mPageStackManager.getContext())).getFragmentManager(), "Timepickerdialog");*/
//                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_DEACTIVATE_NOTIFICATION_PAGE, null));
                EventLog1.SettingPage.report2(mUid,EventLog1.SettingPage.ClickType.NOTIFICATION);
                ARouter.getInstance().build(RouteConstant.PATH_NOTIFICATION_SETTING).navigation();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpManager.getInstance().unregister(this);
        EventBus.getDefault().unregister(this);
        if (mClearCacheDialog != null) {
            mClearCacheDialog.dismiss();
            mClearCacheDialog = null;
        }
        if (mLogoutDialog != null) {
            mLogoutDialog.dismiss();
            mLogoutDialog = null;
        }
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg.cancel();
            loadingDlg = null;
        }
    }

    @Override
    public void setOnClearCache(String confirmType) {
        if (TextUtils.equals(confirmType, ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_clear_cache_pop_title"))) {
            loadingDlg.show();
            Schedulers.newThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    WeLikeCacheManager.clearAppCache();
                    AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                        @Override
                        public void run() {
                            mTvClearText.setText(WeLikeCacheManager.formatFileSize(mTvClearText.getContext(), 0));
                            if (loadingDlg != null) {
                                loadingDlg.dismiss();
                            }
                            ToastUtils.showShort(ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "cleared"));
                        }
                    });
                }
            });
        } else if (TextUtils.equals(confirmType, ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "mine_setting_exit_app_pop_title"))) {
            StartManager.getInstance().logout();
        }
    }

    @Override
    public void onSPDataChanged(String spTypeName, String spKeyName) {
        if (TextUtils.equals(spTypeName, SpManager.sharePreferencesSettingName)
                && TextUtils.equals(spKeyName, SpManager.settingUpgradedKeyName)) {
            boolean isUpgrade = SpManager.Setting.getCurrentUpgraded(this);
            if (isUpgrade) {
                mShowRedPoint.setVisibility(View.VISIBLE);
            } else {
                mShowRedPoint.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(changTime(hourOfDay)).append(":").append(changTime(minute)).append("-").append(changTime(hourOfDayEnd)).append(":").append(changTime(minuteEnd));
        SpManager.Setting.setNotificationStartTime(hourOfDay + ":" + minute, this);
        SpManager.Setting.setNotificationEndTime(hourOfDayEnd + ":" + minuteEnd, this);

    }

    private String changTime(int time) {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(time);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {

        if (EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGOUT == event.id
                || EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_MINE == event.id) {

            finish();

        }
    }


}
