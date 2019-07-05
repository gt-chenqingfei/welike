package com.redefine.welike.commonui.view;

import com.redefine.welike.commonui.adapter.BaseNineGridAdapter;

/**
 * Created by liwenbo on 2018/3/17.
 */

public abstract class PicBaseAdapter<T> extends BaseNineGridAdapter<T> {


    public abstract int getItemWidth(int position);

    public abstract int getItemHeight(int position);
}
