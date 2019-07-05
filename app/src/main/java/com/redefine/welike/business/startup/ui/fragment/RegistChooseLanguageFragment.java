package com.redefine.welike.business.startup.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.startup.management.StartEventManager;
import com.redefine.welike.business.startup.management.constant.LanguageConstant;
import com.redefine.welike.business.startup.management.constant.StartConstant;
import com.redefine.welike.business.startup.ui.adapter.LanguageAdapter;
import com.redefine.welike.business.startup.ui.viewmodel.RegisteredViewModel;
import com.redefine.welike.push.PushService;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.manager.UnLoginEventManager;

import org.jetbrains.annotations.NotNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * @author gongguan
 * @time 2018/1/8 下午3:07
 */
public class RegistChooseLanguageFragment extends Fragment {
    public static final String LANGUAGE_FRAGMENT_TAG = "language_fragment";
    private View mView;
    private TextView btnNext;
    private TextView mSelectLan;
    private RecyclerView chooseLanguageRv;
    private RegisteredViewModel registeredViewModel;
    private LanguageAdapter languageAdapter;
    private LoadingDlg loadingDlg;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (loadingDlg != null) loadingDlg.dismiss();
                    languageAdapter.notifyDataSetChanged();
                }
            });

        }
    };

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_regist_choose_language, container, false);

        registeredViewModel = ViewModelProviders.of(getActivity()).get(RegisteredViewModel.class);

        initViews();

        setOnclick();

        EventLog.UnLogin.report4();
        StartEventManager.getInstance().setVidmate_page_source(1);

        return mView;
    }

    public void initViews() {
        btnNext = mView.findViewById(R.id.btn_regist_choose_language_next);
        mSelectLan = mView.findViewById(R.id.tv_regist_select_language_title);
        chooseLanguageRv = mView.findViewById(R.id.lv_regist_choose_language);

        mSelectLan.setText(ResourceTool.getString("regist_select_language"));
        btnNext.setBackgroundResource(R.drawable.common_appcolor_btn_bg);
        btnNext.setText(ResourceTool.getString("login_lets_go"));

        callBack(false);
        languageAdapter = new LanguageAdapter();


        chooseLanguageRv.setAdapter(languageAdapter);
        chooseLanguageRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        languageAdapter.setDatas(LanguageConstant.initData());

        languageAdapter.setLanguageClickListener(new LanguageAdapter.LanguageClickListener() {
            @Override
            public void onItemListener(Boolean checked) {
                callBack(checked);
            }

            @Override
            public void showLoading() {

                if (loadingDlg == null) loadingDlg = new LoadingDlg(getActivity());
                loadingDlg.show();
                handler.postDelayed(runnable, 2000);
            }
        });
    }


    public void setOnclick() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languageAdapter.getSelectPos() == -1) return;
                LocalizationManager.getInstance().changeLanguage(btnNext.getContext(), languageAdapter.getDatas().get(languageAdapter.getSelectPos()).getShortName());
                EventLog.Login.report3(languageAdapter.getDatas().get(languageAdapter.getSelectPos()).getShortName());
                EventLog.UnLogin.report6(languageAdapter.getDatas().get(languageAdapter.getSelectPos()).getShortName(), UnLoginEventManager.INSTANCE.getFirstClickLanguage());
                registeredViewModel.setStatus(StartConstant.START_STATE_VIDMATE);

            }
        });
    }

    public void callBack(boolean isChecked) {
        if (isChecked) {
            btnNext.setTextColor(ContextCompat.getColor(btnNext.getContext(), R.color.white));
            btnNext.setBackgroundResource(R.drawable.common_btn_orange_solid_bg);
            btnNext.setClickable(true);
        } else {
            btnNext.setTextColor(ContextCompat.getColor(btnNext.getContext(), R.color.main_grey));
            btnNext.setBackgroundResource(R.drawable.common_gray_btn_bg);
            btnNext.setClickable(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadingDlg != null) loadingDlg.dismiss();
    }
}
