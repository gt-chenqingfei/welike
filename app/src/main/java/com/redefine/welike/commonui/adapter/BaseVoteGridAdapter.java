package com.redefine.welike.commonui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by liwb on 2018/1/7.
 */

public abstract class BaseVoteGridAdapter<T> extends BaseAdapter {

    protected List<T> mUrls;

    public void setData(List<T> urls) {
        mUrls = urls;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mUrls == null ? 0 : mUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return mUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);


    public List<T> getData() {
        return mUrls;
    }
}
