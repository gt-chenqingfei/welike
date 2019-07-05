package com.redefine.welike.business.startup.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.browse.management.request.RefreshPostCacheWhenLanguageRequest;
import com.redefine.welike.business.mysetting.ui.page.SettingLanguagePage;
import com.redefine.welike.business.startup.management.bean.LanguageBean;
import com.redefine.welike.business.startup.management.constant.LanguageConstant;
import com.redefine.welike.business.startup.ui.adapter.DialogLanguageAdapter;
import com.redefine.welike.business.startup.ui.adapter.LanguageAdapter;
import com.redefine.welike.common.WindowUtil;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.utils.GoogleUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class LanguageChooseDialog extends BaseActivity {

    private ConstraintLayout clParent;
    private RecyclerView rvChooseLanguage;
    private DialogLanguageAdapter languageAdapter;
    private ArrayList<LanguageBean> languageBeans;

    private LoadingDlg loadingDlg;
    private CountDownTimer mTimer;

    public static void show(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, LanguageChooseDialog.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initViews();
        languageBeans = LanguageConstant.initData();
        setAdapter();

    }

    private void initViews() {

        int width = ScreenUtils.getSreenWidth(this) * 91 / 100;

        View rootView = LayoutInflater.from(this).inflate(R.layout.dialog_choose_language, null);

        clParent = rootView.findViewById(R.id.cl_parent);

        clParent.setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));

        clParent.setMaxHeight(ScreenUtils.getScreenHeight(this) * 89 / 100);

        setContentView(rootView);

        rvChooseLanguage = findViewById(R.id.rv_choose_language);
        ImageView ivClose = findViewById(R.id.iv_close);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setAdapter() {
        languageAdapter = new DialogLanguageAdapter();

        rvChooseLanguage.setAdapter(languageAdapter);
        rvChooseLanguage.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        languageAdapter.setDatas(languageBeans);

        languageAdapter.setLanguageClickListener(new DialogLanguageAdapter.LanguageClickListener() {
            @Override
            public void onItemClick(int position, boolean canSelect) {

                LocalizationManager.getInstance().changeLanguage(LanguageChooseDialog.this, languageBeans.get(position).getShortName());

                loadingDlg = new LoadingDlg(LanguageChooseDialog.this);
//                LocalizationManager.setApplicationLanguage(SettingLanguagePage.this, mAdapter.getCurrentLanguageType());
                loadingDlg.show();
                EventLog1.Language.report1(languageBeans.get(position).getShortName(), EventLog1.Language.ButtonFrom.HOME);

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
                        if (loadingDlg != null)
                            loadingDlg.dismiss();
                        EventBus.getDefault().post(new Event(EventIdConstant.CLEAR_STACK_LAUNCH_MAIN_MINE));
                        LanguageChooseDialog.this.finish();
                    }
                };
                mTimer.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null)
            mTimer.cancel();
    }

    @Override
    public void finish() {
        super.finish();
        getWindow().setDimAmount(0f);
        overridePendingTransition(0,R.anim.preview_out);
    }
}
