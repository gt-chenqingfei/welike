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
package com.pekingese.pagestack.framework.view;

import android.view.ViewGroup;

import com.pekingese.pagestack.framework.page.BasePage;
import com.pekingese.pagestack.framework.titlebar.ITitleActionObserver;
import com.pekingese.pagestack.framework.titlebar.TitleActionObserverDelegate;

public class TitlePageStackAdapter extends PageStackAdapter {

    private TitleActionObserverDelegate mTitleActionObserver;

    public TitlePageStackAdapter(IPageFactory factory) {
        super(factory);
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, BasePage object) {
        if (mTitleActionObserver == null) {
            return ;
        }
        if (object instanceof ITitleActionObserver) {
            mTitleActionObserver.setObserver((ITitleActionObserver) object);
        }
    }

    public void setTitleActionObserver(TitleActionObserverDelegate titleActionObserver) {
        this.mTitleActionObserver = titleActionObserver;
    }
}
