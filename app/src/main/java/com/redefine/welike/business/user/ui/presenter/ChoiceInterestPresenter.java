package com.redefine.welike.business.user.ui.presenter;

import android.app.Activity;
import android.os.Bundle;

import com.redefine.commonui.widget.LoadingDlg;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.frameworkmvp.presenter.MvpTitlePagePresenter1;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.startup.management.IntrestsSuggester;
import com.redefine.welike.business.startup.management.request.ReferrerInfo;
import com.redefine.welike.business.user.ui.adapter.InterestCategoryAdapter;
import com.redefine.welike.business.user.ui.contract.IChoiceInterestContract;
import com.redefine.welike.commonui.util.ToastUtils;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/21.
 */

public class ChoiceInterestPresenter extends MvpTitlePagePresenter1<IChoiceInterestContract.IChoiceInterestView> implements IChoiceInterestContract.IChoiceInterestPresenter
        , IntrestsSuggester.IntrestsSuggesterCallback, AccountManager.AccountCallback {

    private final InterestCategoryAdapter mInterestCategoryAdapter;
    private IntrestsSuggester mModel;
    private LoadingDlg mLoadingDlg;

    public ChoiceInterestPresenter(Activity activity, Bundle pageConfig) {
        super(activity, pageConfig);
        mInterestCategoryAdapter = new InterestCategoryAdapter();

    }

    @Override
    protected IChoiceInterestContract.IChoiceInterestView createPageView() {
        return IChoiceInterestContract.ChoiceInterestFactory.createView();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mView.setAdapter(mInterestCategoryAdapter);
        mView.setPresenter(this);

        mModel = new IntrestsSuggester();
        mModel.setListener(this);
        AccountManager.getInstance().register(this);
        onRefresh();
    }

    @Override
    public void destroy() {
        super.destroy();
        mModel.setListener(null);
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
        AccountManager.getInstance().unregister(this);
    }

    @Override
    public void onRefreshIntrestSuggestions(List<UserBase.Intrest> intrests, int errCode, ReferrerInfo info) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            mInterestCategoryAdapter.setData(intrests);
            mView.showContent();
        } else {
            mView.showErrorView();
        }
    }

    @Override
    public void onHisIntrestSuggestions(List<UserBase.Intrest> intrests, boolean last, int errCode) {

    }

    @Override
    public void onRefresh() {
        mView.showLoading();
        mModel.refresh();
    }

    @Override
    public void onConfirmClick() {
        List<UserBase.Intrest> list = mInterestCategoryAdapter.getSelectInterest();
        if (CollectionUtil.isEmpty(list)) {
            mLoadingDlg = new LoadingDlg(mActivity);
            mLoadingDlg.show();
            Account account = AccountManager.getInstance().getAccount().copy();
            account.setIntrests(null);
            AccountManager.getInstance().modifyAccount(account);
        } else {
            mLoadingDlg = new LoadingDlg(mActivity);
            mLoadingDlg.show();
            Account account = AccountManager.getInstance().getAccount().copy();
            account.setIntrests(list);
            AccountManager.getInstance().modifyAccount(account);
        }
    }

    @Override
    public void onModified() {
        onBackPressed();
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }

    @Override
    public void onModifyFailed(int errCode) {
        ToastUtils.showShort(ErrorCode.showErrCodeText(errCode));
        if (mLoadingDlg != null) {
            mLoadingDlg.dismiss();
        }
    }
}
