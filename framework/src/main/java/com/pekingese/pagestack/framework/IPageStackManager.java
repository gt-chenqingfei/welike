/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/7/25
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/7/25
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/7/25, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pekingese.pagestack.framework.layer.BasePageStackLayer;
import com.pekingese.pagestack.framework.view.IPageStackEnv;
import com.pekingese.pagestack.framework.view.PageStack;

import java.util.List;

public interface IPageStackManager extends IPageStack, IActivityLifeCycle, IPageStackEnv, IPageMessageDispatcher {

    void bindPageStack(ViewGroup parent);

    Context getContext();

    Activity getActivity();

    LayoutInflater getLayoutInflater();

    PageStack getPageStack();

    void saveStartForResultPage();

    void clearPageStack();

    BasePageStackLayer getPageStackLayer();

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    ActivityCompat.PermissionCompatDelegate getPermissionCompatDelegate();
}
