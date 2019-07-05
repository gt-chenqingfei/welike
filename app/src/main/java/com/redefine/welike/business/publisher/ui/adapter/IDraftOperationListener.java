package com.redefine.welike.business.publisher.ui.adapter;

import com.redefine.welike.business.publisher.management.bean.DraftBase;

/**
 * Created by liwenbo on 2018/3/20.
 */

public interface IDraftOperationListener {

    void onDelete(DraftBase draftBase);

    void onResend(DraftBase draftBase);
}
