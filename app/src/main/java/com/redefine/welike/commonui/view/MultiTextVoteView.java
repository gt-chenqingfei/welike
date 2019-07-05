package com.redefine.welike.commonui.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by mengnan on 2018/5/13.
 **/
public class MultiTextVoteView<T extends BaseAdapter> extends LinearLayout {

    private MultiGridView.OnItemClickListener mOnItemClickListener;
    protected T mAdapter;


    public MultiTextVoteView(Context context) {
        super(context);
    }

    public MultiTextVoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiTextVoteView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(T adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        checkViews();

        mAdapter.registerDataSetObserver(mDataSetObserver);
        mAdapter.notifyDataSetChanged();
    }

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            onDataSetChange();
        }
    };

    private void onDataSetChange() {
        checkViews();
        requestLayout();
    }

    private void checkViews() {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            removeAllViews();
            return;
        }
        int size = mAdapter.getCount();
        if (getChildCount() == size) {
            refreshChild(size);
            return;
        }
        removeAllViews();
        addChild(size);
    }


    private void refreshChild(int size) {
        for (int i = 0; i < size; i++) {
            View oldView = getChildAt(i);
            View view = mAdapter.getView(i, getChildAt(i), this);
            if (view != oldView) {
                removeViewAt(i);
                addView(view, i);
            }
            if (mOnItemClickListener != null) {
                final int position = i;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object t = mAdapter == null || position >= mAdapter.getCount() ? null : mAdapter.getItem(position);
                        mOnItemClickListener.onItemClick(v, position, t);
                    }
                });
            }
        }
    }

    private void addChild(int size) {
        for (int i = 0; i < size; i++) {
            View view = mAdapter.getView(i, null, this);
            if (mOnItemClickListener != null) {
                final int position = i;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object t = mAdapter == null || position >= mAdapter.getCount() ? null : mAdapter.getItem(position);
                        mOnItemClickListener.onItemClick(v, position, t);
                    }
                });
            }
            addView(view);
        }
    }

    public BaseAdapter getAdapter() {
        return mAdapter;
    }


    public static interface OnItemClickListener {
        void onItemClick(View view, int position, Object t);
    }

}
