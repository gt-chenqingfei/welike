package com.redefine.welike.business.user.ui.contract;

import android.app.Activity;
import android.os.Bundle;

import com.redefine.foundation.mvp.IBasePagePresenter;
import com.redefine.foundation.mvp.IBasePageView;
import com.redefine.welike.business.user.ui.adapter.InterestCategoryAdapter;
import com.redefine.welike.business.user.ui.presenter.ChoiceInterestPresenter;
import com.redefine.welike.business.user.ui.view.ChoiceInterestView;

/**
 * Created by liwenbo on 2018/3/21.
 */

public interface IChoiceInterestContract {

    interface IChoiceInterestPresenter extends IBasePagePresenter {

        void onRefresh();

        void onConfirmClick();
    }

    interface IChoiceInterestView extends IBasePageView {

        void setAdapter(InterestCategoryAdapter mInterestCategoryAdapter);

        void setPresenter(IChoiceInterestPresenter choiceInterestPresenter);

        void showLoading();

        void showContent();

        void showErrorView();
    }

    class ChoiceInterestFactory {
        public static IChoiceInterestPresenter createPresenter(Activity activity, Bundle pageBundle) {
            return new ChoiceInterestPresenter(activity, pageBundle);
        }

        public static IChoiceInterestView createView() {
            return new ChoiceInterestView();
        }
    }
}
