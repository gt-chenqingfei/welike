package com.redefine.welike.business.user.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.user.ui.contract.IChoiceInterestContract;

/**
 * Created by liwenbo on 2018/3/21.
 */
@Route(path = RouteConstant.PATH_USER_CHOICE_INTEREST)
public class ChoiceInterestActivity extends BaseActivity {

    private IChoiceInterestContract.IChoiceInterestPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = createPresenter();

        setContentView(presenter.createView(this, getIntent().getExtras()));
    }


    protected IChoiceInterestContract.IChoiceInterestPresenter createPresenter() {
        return IChoiceInterestContract.ChoiceInterestFactory.createPresenter(this, getIntent().getExtras());
    }
}
