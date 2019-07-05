/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/10/18
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/10/18
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/10/18, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.titlebar;

public class TitleActionObserverDelegate implements ITitleActionObserver {

    private ITitleActionObserver mObserver;

    public void setObserver(ITitleActionObserver observer) {
        mObserver = observer;
    }

    @Override
    public void onTitleActionFired(TitleAction action) {
        if (mObserver != null) {
            mObserver.onTitleActionFired(action);
        }
    }
}
