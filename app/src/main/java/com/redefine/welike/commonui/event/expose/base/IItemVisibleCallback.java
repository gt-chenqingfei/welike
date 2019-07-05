package com.redefine.welike.commonui.event.expose.base;

import android.view.View;

/**
 * Created by nianguowang on 2019/1/9
 */
public interface IItemVisibleCallback<T> {

    /**
     * Bind data and page id to callback.
     * @param dataProvider
     * @param oldSource
     */
    void bindDataAndSource(IDataProvider<T> dataProvider, String oldSource);

    void updateSource(String source, String oldSource);

    /**
     * When itemView's show area / itemView's area is bigger than {@link #getRatioForExpose()}, this method will be called back.
     * @param layoutPosition
     * @param view
     * @param showRatio
     */
    void onItemVisible(int layoutPosition, View view, float showRatio);

    /**
     * When itemView's show area / itemView's area is smaller than {@link #getRatioForExpose()}, this method will be called back.
     * @param layoutPosition
     * @param view
     */
    void onItemInvisible(int layoutPosition, View view);

    /**
     * To define the ratio that expose.
     * @return
     */
    float getRatioForExpose();
}
