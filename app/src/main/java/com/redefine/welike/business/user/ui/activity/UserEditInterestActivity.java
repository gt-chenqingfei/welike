package com.redefine.welike.business.user.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;

import com.redefine.commonui.activity.BaseActivity;
import com.redefine.welike.R;

/**
 * @author gongguan
 * @time 2018/1/18 下午4:00
 */
public class UserEditInterestActivity extends BaseActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_interest);
    }

    public static void luanch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, UserEditInterestActivity.class);
        context.startActivity(intent);
    }


}
