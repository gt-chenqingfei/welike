package com.redefine.welike.commonui.event.expose.base;

import android.support.v7.widget.RecyclerView;

import com.redefine.welike.business.feeds.management.bean.PostBase;

import java.util.List;


public interface IItemExposeManager {


    /**
     * Invoke the method when both RecyclerView and Adapter are init.
     * @param recyclerView
     * @param adapter
     * @param oldSource
     */
    void attach(RecyclerView recyclerView, IDataProvider adapter, String oldSource);

    /**
     * Invoke when more data are loaded.
     * @param feeds
     */
    @Deprecated
    void updateData(List<PostBase> feeds);

    /**
     * Invoke when load the first page data.
     * @param feeds
     * @param isShowHeader
     */
    @Deprecated
    void setData(List<PostBase> feeds, boolean isShowHeader);

    /**
     * Set the page id.
     * @param source
     * @param oldSource
     */
    void setSource(String source, String oldSource);

    /**
     * Invoke this method when the first page data is loaded.
     */
    void onDataLoaded();

    /**
     * Call it when RecyclerView is attached to the window.
     */
    void onAttach();

    /**
     * Call it when RecyclerView is detached from the window.
     */
    void onDetach();

    /**
     * When RecyclerView is in an Activity, call it when activity resume;
     * And when is in a Fragment, call it when the Fragment is visiable.
     * <code>
     *     @Override
     *     public void setUserVisibleHint(boolean isVisibleToUser) {
     *         super.setUserVisibleHint(isVisibleToUser);
     *         if (isVisibleToUser) {
     *             mItemDetector.onShow();
     *         } else {
     *             mItemDetector.onHide();
     *         }
     *     }
     * </code>
     */
    void onShow();

    /**
     * When RecyclerView is in an Activity, call it when activity pause;
     * And when is in a Fragment, call it when the Fragment is invisiable.
     * <code>
     *     @Override
     *     public void setUserVisibleHint(boolean isVisibleToUser) {
     *         super.setUserVisibleHint(isVisibleToUser);
     *         if (isVisibleToUser) {
     *             mItemDetector.onShow();
     *         } else {
     *             mItemDetector.onHide();
     *         }
     *     }
     * </code>
     */
    void onHide();
    /**
     * Call it if Activity or Fragment onPause.
     */
    void onPause();

    /**
     * Call it if Activity or Fragment onResume.
     */
    void onResume();

    /**
     * Call it if Activity or Fragment onDestroy.
     */
    void onDestroy();
}
