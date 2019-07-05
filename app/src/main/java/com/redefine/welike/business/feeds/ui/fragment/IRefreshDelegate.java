package com.redefine.welike.business.feeds.ui.fragment;

/**
 * Created by MR on 2018/1/19.
 */

public interface IRefreshDelegate {
    void startRefresh();
    void stopRefresh();
    void setRefreshEnable(boolean isEnable);
}
