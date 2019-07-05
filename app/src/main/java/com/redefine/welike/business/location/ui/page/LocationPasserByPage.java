package com.redefine.welike.business.location.ui.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.redefine.commonui.activity.BaseActivity;
import com.redefine.welike.base.constant.RouteConstant;
import com.redefine.welike.business.location.ui.constant.LocationConstant;
import com.redefine.welike.business.location.ui.contract.ILocationPasserByContract;

/**
 * Created by liwenbo on 2018/3/21.
 */
@Route(path = RouteConstant.PATH_LOCATION_PASS)
public class LocationPasserByPage extends BaseActivity {
    ILocationPasserByContract.ILocationPasserByPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Bundle extras = intent.getBundleExtra(LocationConstant.BUNDLE_KEY_LOCATION);
        presenter = ILocationPasserByContract.LocationPasserByFactory.createPresenter(this, extras);
        View view = presenter.createView(this, savedInstanceState);
        setContentView(view);
        presenter.attach();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detach();
        presenter.destroy();
    }
}
