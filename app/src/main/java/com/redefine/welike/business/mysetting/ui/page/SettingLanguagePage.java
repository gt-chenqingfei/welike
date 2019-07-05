package com.redefine.welike.business.mysetting.ui.page;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSONObject;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.browse.management.request.RefreshPostCacheWhenLanguageRequest;
import com.redefine.welike.business.mysetting.ui.adapter.SettingLanguageAdapter;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.utils.GoogleUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by gongguan on 2018/2/23.
 */
@Route(path = RouteConstant.SETTING_LANGUAGE_ROUTE_PATH)
public class SettingLanguagePage extends BaseActivity {
    private ListView mList;
    private SettingLanguageAdapter mAdapter;
    private TextView mTitle;
    private TextView mBtnSave;
    private ImageView mBack;
    private LoadingDlg loadingDlg;
    private CountDownTimer mTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting_language);
        mAdapter = new SettingLanguageAdapter();
        initViews();
        initEvents();
//        TrackerUtil.getPageTracker().setScreenName(TrackerConstant.PAGE_SETTING);
//        TrackerUtil.getPageTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initViews() {
        mBack = findViewById(R.id.iv_common_back);
        mList = findViewById(R.id.lv_regist_choose_language);
        mTitle = findViewById(R.id.tv_common_title);
        mBtnSave = findViewById(R.id.mine_setting_language_save);
    }

    private void initEvents() {
        mList.setAdapter(mAdapter);

        mBtnSave.setText(ResourceTool.getString("mine_user_host_personal_edit_name_save"));
        mTitle.setText(ResourceTool.getString("mine_setting_language_text"));
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalizationManager.getInstance().changeLanguage(SettingLanguagePage.this, mAdapter.getCurrentLanguageType());
                loadingDlg = new LoadingDlg(SettingLanguagePage.this);
//                LocalizationManager.setApplicationLanguage(SettingLanguagePage.this, mAdapter.getCurrentLanguageType());
                loadingDlg.show();

                EventLog1.Language.report1(mAdapter.getCurrentLanguageType(), EventLog1.Language.ButtonFrom.SETTING);
                try {

                    new RefreshPostCacheWhenLanguageRequest(MyApplication.getAppContext(),
                            GoogleUtil.INSTANCE.getGaid()).req(new RequestCallback() {
                        @Override
                        public void onError(BaseRequest request, int errCode) {

                        }

                        @Override
                        public void onSuccess(BaseRequest request, JSONObject result) {
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


                mTimer = new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        loadingDlg.dismiss();
                        try {

                            new RefreshPostCacheWhenLanguageRequest(MyApplication.getAppContext(),
                                    GoogleUtil.INSTANCE.getGaid()).req(new RequestCallback() {
                                @Override
                                public void onError(BaseRequest request, int errCode) {

                                }

                                @Override
                                public void onSuccess(BaseRequest request, JSONObject result) {
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_MINE));
                        finish();
                    }
                };
                mTimer.start();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg = null;
        }
    }

}
