/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 2017/10/2
 * <p>
 * Description : description
 * <p>
 * Creation    : 2017/10/2
 * Author      : liwenbo0328@163.com
 * History     : Creation, 2017/10/2, bobo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.titlebar;

public class SingleTitleActionSubject {

    private ITitleActionObserver mObserver;

    public SingleTitleActionSubject() {

    }

    public void setObserver(ITitleActionObserver observer) {
        mObserver = observer;
    }

    public void clearObserver() {
        mObserver = null;
    }

    public void fireAction(TitleAction action) {
        if (mObserver != null) {
            mObserver.onTitleActionFired(action);
        }
    }
}
