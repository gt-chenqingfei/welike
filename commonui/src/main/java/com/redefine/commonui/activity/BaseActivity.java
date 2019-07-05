package com.redefine.commonui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.redefine.foundation.language.LocalizationManager;

import static com.redefine.welike.base.ext.ExtsKt.setFullScreenActivity;
import static com.redefine.welike.base.ext.ExtsKt.setStatusBarJ;

/**
 * Created by liwenbo on 2018/2/26.
 */

public class BaseActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyStatusBar();
    }

    protected boolean isFullScreenActivity() {
        return false;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationManager.wrapContext(newBase));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            applyStatusBar();
        }
    }

    private void applyStatusBar() {
        if (isFullScreenActivity()) {
            setFullScreenActivity(this);
        } else {
            setStatusBarJ(this);
        }
    }
}
