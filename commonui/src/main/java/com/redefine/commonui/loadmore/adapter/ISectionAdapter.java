package com.redefine.commonui.loadmore.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liwenbo on 2018/2/12.
 */

public interface ISectionAdapter {
    public boolean isSectionHeader(int position);

    public int getSectionForPosition(int position);

    public View getSectionHeaderView(int section, View convertView, ViewGroup parent);

    public int getSectionHeaderViewType(int section);

    public int getCount();
}
