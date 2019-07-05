/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/8/1
 * <p>
 * Description : Activity的声明周期回调，传至每个Page，处理声明周期的事件
 * <p>
 * Creation    : 17/8/1
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/8/1, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public interface IActivityLifeCycle {
    boolean onBackPressed();

    void onActivitySaveInstanceState(Bundle outState);

    void onActivityRestoreInstanceState(Bundle savedInstanceState);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onActivityDestroy();

    void onActivityPause();

    void onActivityStop();

    void onActivityStart();

    void onActivityResume();

    boolean dispatchKeyEvent(KeyEvent event);
}
