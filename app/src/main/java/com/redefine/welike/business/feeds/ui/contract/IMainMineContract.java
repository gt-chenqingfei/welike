package com.redefine.welike.business.feeds.ui.contract;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.foundation.mvp.IBasePresenter;
import com.redefine.foundation.mvp.IBaseView;
import com.redefine.welike.business.feeds.ui.presenter.MainMinePresenter;
import com.redefine.welike.business.feeds.ui.view.MainMineView;

/**
 * @author gongguan
 * @time 2018/1/13 下午6:33
 */
public interface IMainMineContract {

    interface IMainMinePresenter extends IBasePresenter {

        void onCreate(Bundle arguments, Bundle savedInstanceState);

        View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

        void attach(boolean isFistShow);

        void destroy();

        void onPageShown();

//        void refreshUserTask();

        void onActivityResume();

        void onVerifyClick();

        void setAccountGender(byte gender);

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void tryBindFacebook(String facebookToken);

        void tryBindGoogle(String googleToken);

        void tryBindTrueCaller(String payload, String signature, String signatureAlgorithm);

    }

    interface IMainMineView extends IBaseView {

        View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

        void setPresenter(IMainMinePresenter mPresenter);

        void refreshDraftBoxCount();

//        void refreshTask(MissionTask value);

        void setVerifyView(boolean show);

        void showGenderView(boolean show);

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void setVerifyAccountView(boolean show);
    }

    class IMainMineFactory {
        public static IMainMinePresenter createPresenter() {
            return new MainMinePresenter();
        }

        public static IMainMineView createView() {
            return new MainMineView();
        }
    }

}
