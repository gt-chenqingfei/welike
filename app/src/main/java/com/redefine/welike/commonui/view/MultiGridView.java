package com.redefine.welike.commonui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;

/**
 * Created by liwb on 2018/1/6.
 */

public class MultiGridView<T extends BaseAdapter> extends ViewGroup {

    private static final int MAX_ROW_COUNT = 3;
    private static final int ROW_CHILD_COUNT = 3;
    private static final int DEFAULT_CHILD_MARGIN = 3;

    protected int mMaxRowCount;
    protected int mRowChildCount;
    protected int mChildMargin;
    private OnItemClickListener mOnItemClickListener;
    protected T mAdapter;

    protected int marginTotalWidth = 0;

    public MultiGridView(Context context) {
        super(context);
        init(context, null);
    }

    public MultiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MultiGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MultiGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mChildMargin = ScreenUtils.dip2Px(getContext(), DEFAULT_CHILD_MARGIN);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultiGridView);
            mMaxRowCount = ta.getInt(R.styleable.MultiGridView_row_count, MAX_ROW_COUNT);
            mRowChildCount = ta.getInt(R.styleable.MultiGridView_num_column, ROW_CHILD_COUNT);
            mChildMargin = ta.getDimensionPixelOffset(R.styleable.MultiGridView_child_margin, mChildMargin);
            ta.recycle();
        } else {
            mMaxRowCount = MAX_ROW_COUNT;
            mRowChildCount = ROW_CHILD_COUNT;
            mChildMargin = DEFAULT_CHILD_MARGIN;
        }
    }

    public void setMarginTotalWidth(int marginTotalWidth) {
        this.marginTotalWidth = marginTotalWidth;
    }

    public void setmMaxRowCount(int mMaxRowCount) {
        this.mMaxRowCount = mMaxRowCount;
    }

    public void setmRowChildCount(int mRowChildCount) {
        this.mRowChildCount = mRowChildCount;
    }

    public void setmChildMargin(int mChildMargin) {
        this.mChildMargin = mChildMargin;
    }

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            onDataSetChange();
        }
    };

    private void checkViews() {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            removeAllViews();
            return;
        }
        int size = Math.min(mAdapter.getCount(), mMaxRowCount * mRowChildCount);
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
                addView(view, i, newLayoutParams());
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
            addView(view, newLayoutParams());
        }
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

    private void onDataSetChange() {
        checkViews();
        requestLayout();
    }

    private LayoutParams newLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 设置child的Marin(px)
     *
     * @param margin
     */
    public void setChildMargin(int margin) {
        mChildMargin = margin;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int realWidth = 0;
        int realHeight = 0;
        if (mAdapter == null || mAdapter.getCount() == 0 || mRowChildCount == 0 || mMaxRowCount == 0) {
            setMeasuredDimension(realWidth, realHeight);
            return;
        }
        int urlSize = mAdapter.getCount();
        int size = Math.min(urlSize, getChildCount());
        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int itemSize = (specWidthSize - getPaddingLeft() - getPaddingRight() - (mRowChildCount - 1) * mChildMargin) / mRowChildCount;
        View view;
        realWidth = specWidthSize;
        int lineCount = ((size + mRowChildCount - 1) / mRowChildCount);
        realHeight = itemSize * lineCount + mChildMargin * (lineCount - 1) + getPaddingTop() + getPaddingBottom();
        int childMeasureSpec = MeasureSpec.makeMeasureSpec(
                Math.max(0, itemSize), MeasureSpec.EXACTLY);
        for (int i = 0; i < size; i++) {
            view = getChildAt(i);
            view.measure(childMeasureSpec, childMeasureSpec);
        }
        setMeasuredDimension(realWidth, realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter == null || mAdapter.getCount() == 0 || mRowChildCount == 0 || mMaxRowCount == 0) {
            return;
        }
        int urlSize = mAdapter.getCount();
        int size = Math.min(urlSize, getChildCount());

        for (int i = 0; i < size; i++) {
            layoutChildren(size, mRowChildCount);
        }
    }

    /**
     * 布局每个child
     *
     * @param size          总体的child的数量
     * @param rowChildCount 每行显示的child的数量
     */
    protected void layoutChildren(int size, int rowChildCount) {
        View view;
        int left = getPaddingLeft();
        int top = getPaddingTop();

        for (int i = 0; i < size; i++) {
            view = getChildAt(i);
            view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
            left += (view.getMeasuredWidth() + mChildMargin);
            if ((i + 1) % rowChildCount == 0) {
                left = getPaddingLeft();
                top += (view.getMeasuredHeight() + mChildMargin);
            }
        }
    }

    public BaseAdapter getAdapter() {
        return mAdapter;
    }


    public static interface OnItemClickListener {
        void onItemClick(View view, int position, Object t);
    }

}

