package com.redefine.welike.business.publisher.ui.viewholder;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.publisher.ui.bean.BottomBehaviorItem;
import com.redefine.welike.business.publisher.ui.component.IBehaviorViewHolder;
import com.redefine.welike.business.publisher.ui.component.OnItemClickListener;

import org.jetbrains.annotations.NotNull;


public class BottomBehaviorVH extends LinearLayout implements IBehaviorViewHolder {

    private AppCompatImageView mIvItem;
    private TextView mTvItem;
    private LinearLayout.LayoutParams mTvParams;
    private LinearLayout.LayoutParams mIvParams;
    private LinearLayout.LayoutParams mItemParams;
    private int childCount = 1;
    private BottomBehaviorItem item;
    private OnItemClickListener mListener;

    public BottomBehaviorVH(Context context, OnItemClickListener listener) {
        super(context);
        this.mListener = listener;
        initItemView();
        initImageViewLayoutPrams(context);
        initTextViewLayoutParams(context);
    }

    public void toggleSize(boolean isSmallMode, int childCount) {
        this.childCount = childCount;
        initItemView();
        mIvParams = (LinearLayout.LayoutParams) mTvItem.getLayoutParams();
        if (isSmallMode) {
            mTvItem.setVisibility(View.GONE);
            mIvParams.width = ScreenUtils.dip2Px(28);
            mIvParams.height = ScreenUtils.dip2Px(28);
        } else {
            mTvItem.setVisibility(View.VISIBLE);
            mIvParams.width = ScreenUtils.dip2Px(48);
            mIvParams.height = ScreenUtils.dip2Px(48);
        }
        mTvParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mTvParams.setMargins(0, 0, 0, ScreenUtils.dip2Px(6));

        mTvItem.setLayoutParams(mTvParams);
        mIvItem.setLayoutParams(mIvParams);
    }

    @Override
    public BottomBehaviorVH bindView(BottomBehaviorItem data) {
        this.item = data;
        mIvItem.setImageResource(data.resource);
        mTvItem.setText(data.name);
        mIvItem.setEnabled(data.enabled);
        this.setEnabled(data.enabled);
        this.setSelected(data.isSelected);
        return this;
    }

    @Override
    public BottomBehaviorVH setEnable(boolean enable) {
        this.item.enabled = enable;

        mIvItem.setEnabled(enable);
        this.setEnabled(enable);
        return this;
    }

    @Override
    public BottomBehaviorVH setItemSelected(boolean selected) {
        this.item.isSelected = selected;
        this.setSelected(selected);
        return this;
    }

    private void initItemView() {
        int width = ScreenUtils.getSreenWidth(getContext()) / childCount;
        if (mItemParams == null) {
            mItemParams = new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT);
        } else {
            mItemParams.width = width;
        }
        this.setLayoutParams(mItemParams);
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(item);
                }
            }
        });
    }

    private void initTextViewLayoutParams(Context context) {
        mTvItem = new TextView(context);
        mTvParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mTvParams.setMargins(0, 0, 0, ScreenUtils.dip2Px(6));
        mTvItem.setMaxLines(1);
        mTvItem.setGravity(Gravity.CENTER_HORIZONTAL);
        mTvItem.setEllipsize(TextUtils.TruncateAt.END);
        mTvItem.setTextColor(getResources()
                .getColor(com.redefine.commonui.R.color.common_text_black_62));
        mTvItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mTvItem.setLayoutParams(mTvParams);
        this.addView(mTvItem);
    }

    private void initImageViewLayoutPrams(Context context) {
        mIvItem = new AppCompatImageView(context);
        mIvParams = new LinearLayout.LayoutParams(ScreenUtils.dip2Px(48),
                ScreenUtils.dip2Px(48));
        mIvParams.setMargins(0, 0, 0, ScreenUtils.dip2Px(8));
        mIvItem.setLayoutParams(mIvParams);
        mIvItem.setBackgroundResource(R.drawable.ripple1);
        this.addView(mIvItem);
    }

    @NotNull
    @Override
    public View getView() {
        return this;
    }

}
