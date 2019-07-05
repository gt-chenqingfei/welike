package com.redefine.welike.business.user.ui.view.contactlist;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.welike.R;

import java.util.Map;

public class FloatingBarItemDecoration extends RecyclerView.ItemDecoration {

    private final String TAG = FloatingBarItemDecoration.class.getSimpleName();
    private Context mContext;
    private int mTitleHeight;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private int mTextHeight;
    private int mTextBaselineOffset;
    private int mTextStartMargin;
    private Map<Integer, String> mList;

    public FloatingBarItemDecoration(Context context, Map<Integer, String> list) {
        this.mContext = context;
        Resources resources = mContext.getResources();
        this.mList = list;
        this.mTitleHeight = resources.getDimensionPixelSize(R.dimen.item_decoration_title_height);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(ContextCompat.getColor(mContext, R.color.editor_forward_card_bg));

        mTextPaint = new Paint();
        mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.contacts_menu_title_color));
        mTextPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.item_decoration_title_fontsize));

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextHeight = (int) (fm.bottom - fm.top);
        mTextBaselineOffset = (int) fm.bottom;
        mTextStartMargin = resources.getDimensionPixelOffset(R.dimen.item_decoration_title_start_margin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        outRect.set(0, mList.containsKey(position) ? mTitleHeight : 0, 0, 0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewAdapterPosition();
            if (!mList.containsKey(position)) {
                continue;
            }
            drawTitleArea(c, left, right, child, params, position);
        }
    }

    private void drawTitleArea(Canvas c, int left, int right, View child,
                               RecyclerView.LayoutParams params, int position) {
        final int rectBottom = child.getTop() - params.topMargin;
        c.drawRect(left, rectBottom - mTitleHeight, right,
                rectBottom, mBackgroundPaint);
        c.drawText(mList.get(position), child.getPaddingLeft() + mTextStartMargin,
                rectBottom - (mTitleHeight - mTextHeight) / 2 - mTextBaselineOffset, mTextPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int position = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        View child = parent.findViewHolderForAdapterPosition(position).itemView;
        String initial = getTag(position);
        if (initial == null) {
            return;
        }

        boolean flag = false;
        if (getTag(position + 1) != null && !initial.equals(getTag(position + 1))) {
            if (child.getHeight() + child.getTop() < mTitleHeight) {
                c.save();
                flag = true;
                c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
            }
        }

        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mBackgroundPaint);
        c.drawText(initial, child.getPaddingLeft() + mTextStartMargin,
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight - mTextHeight) / 2 - mTextBaselineOffset, mTextPaint);

        if (flag) {
            c.restore();
        }
    }

    private String getTag(int position) {
        while (position >= 0) {
            if (mList.containsKey(position)) {
                return mList.get(position);
            }
            position--;
        }
        return null;
    }

    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }


}

