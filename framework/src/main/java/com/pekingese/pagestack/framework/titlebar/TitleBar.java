/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/9/27
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/9/27
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/9/27, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.titlebar;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pekingese.pagestack.framework.constant.CommonConstant;
import com.pekingese.pagestack.framework.util.CollectionUtil;
import com.pekingese.pagestack.framework.util.DeviceUtil;

import java.util.List;

public class TitleBar extends ViewGroup implements ITitleBar {

    private static final float DEFAULT_HEIGHT = 50;
    private TitleActionContainer mLeftContainer;
    private TitleActionContainer mCenterContainer;
    private TitleActionContainer mRightContainer;

    private PageTitleActionPack mTitleActionPack;
    private ITitleActionParse mTitleActionParse;
    private SingleTitleActionSubject mTitleActionSubject;

    public TitleBar(Context context) {
        super(context);
        init();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化TitleBar的模板，用于初始化出当前TitleBar的子View
     */
    private void init() {
        mTitleActionParse = createTitleActionParseFactory().createTitleActionParse();
        setMinimumHeight(getUserMinimumHeight());
        mLeftContainer = new TitleActionContainer(getContext());
        mCenterContainer = new TitleActionContainer(getContext());
        mRightContainer = new TitleActionContainer(getContext());
        mLeftContainer.setOrientation(LinearLayout.HORIZONTAL);
        mCenterContainer.setOrientation(LinearLayout.VERTICAL);
        mRightContainer.setOrientation(LinearLayout.HORIZONTAL);
        addView(mLeftContainer, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        addView(mCenterContainer, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        addView(mRightContainer, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        // 首先测量两边的container，
        measureChild(mLeftContainer, widthMeasureSpec, 0, heightMeasureSpec, 0);
        measureChild(mRightContainer, widthMeasureSpec, 0, heightMeasureSpec, 0);

        int leftWidth = mLeftContainer.getMeasuredWidth();
        int rightWidth = mRightContainer.getMeasuredWidth();
        int usedWidth = 2 * Math.max(leftWidth, rightWidth);
        measureChild(mCenterContainer, widthMeasureSpec, usedWidth, heightMeasureSpec, 0);
    }

    private void measureChild(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final LayoutParams lp = child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + widthUsed, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + heightUsed, lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mLeftContainer.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + mLeftContainer.getMeasuredWidth(),
                getPaddingTop() + mLeftContainer.getMeasuredHeight());

        mCenterContainer.layout((getMeasuredWidth() - mCenterContainer.getMeasuredWidth()) / 2, getPaddingTop(),
                (getMeasuredWidth() + mCenterContainer.getMeasuredWidth()) / 2,
                getPaddingTop() + mCenterContainer.getMeasuredHeight());


        mRightContainer.layout(getMeasuredWidth() - mRightContainer.getMeasuredWidth() - getPaddingRight(),
                getPaddingTop(), getMeasuredWidth() - getPaddingRight(),
                getPaddingTop() + mRightContainer.getMeasuredHeight());
    }

    @Override
    public void applyTitleAction(PageTitleActionPack actionPack) {
        mTitleActionPack = actionPack;
        removeContainerViews();
        if (mTitleActionPack == null || !mTitleActionPack.isValid()) {
            return ;
        }
        applyLeftAction(mTitleActionPack.getLeftActions());
        applyCenterAction(mTitleActionPack.getCenterActions());
        applyRightAction(mTitleActionPack.getRightActions());
        mLeftContainer.requestLayout();
        mRightContainer.requestLayout();
        mCenterContainer.requestLayout();
    }

    private void removeContainerViews() {
        mLeftContainer.removeAllViewsInLayout();
        mRightContainer.removeAllViewsInLayout();
        mCenterContainer.removeAllViewsInLayout();
    }

    protected ITitleActionParseFactory createTitleActionParseFactory() {
        return new TitleActionParseFactory();
    }

    private void applyLeftAction(List<TitleAction> actions) {
        List<View> views = mTitleActionParse.parseTitleActions(getContext(), actions);
        if (CollectionUtil.isArrayEmpty(views)) {
            return ;
        }
        int size = views.size();
        View view;
        for (int i = 0; i < size; i ++) {
            view = views.get(i);
            if (view.getTag() instanceof TitleAction) {
                final TitleAction action = (TitleAction) view.getTag();
                if (action.getActionId() != CommonConstant.INVALID_ID && action.getActionType() == TitleAction.ACTION_CLICK && mTitleActionSubject != null) {
                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTitleActionSubject.fireAction(action);
                        }
                    });
                }
            }
            mLeftContainer.addView(view, view.getLayoutParams());
        }
    }

    private void applyCenterAction(List<TitleAction> actions) {
        List<View> views = mTitleActionParse.parseTitleActions(getContext(), actions);
        if (CollectionUtil.isArrayEmpty(views)) {
            return ;
        }
        int size = views.size();
        View view;
        for (int i = 0; i < size; i ++) {
            view = views.get(i);
            mCenterContainer.addView(view, view.getLayoutParams());
        }
    }

    private void applyRightAction(List<TitleAction> actions) {
        List<View> views = mTitleActionParse.parseTitleActions(getContext(), actions);
        if (CollectionUtil.isArrayEmpty(views)) {
            return ;
        }
        int size = views.size();
        View view;
        for (int i = 0; i < size; i ++) {
            view = views.get(i);
            mRightContainer.addView(view, view.getLayoutParams());
        }
    }

    protected int getUserMinimumHeight() {
        return DeviceUtil.dp2px(getContext().getResources(), DEFAULT_HEIGHT);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setChildrenAlpha(float i) {
        mLeftContainer.setAlpha(i);
        mCenterContainer.setAlpha(i);
        mRightContainer.setAlpha(i);
    }

    @Override
    public void setTitleActionSubject(SingleTitleActionSubject subject) {
        mTitleActionSubject = subject;
    }

    @Override
    public void offsetTitle(float positionOffset) {
        int startLeft = (getWidth() - mCenterContainer.getWidth()) / 2;
        int endLeft = getWidth() - mRightContainer.getWidth() - mCenterContainer.getWidth();
        int currentScrolled = mCenterContainer.getLeft() - startLeft;
        int shouldScrolled = (int) ((endLeft - startLeft) * (1 - positionOffset));
        mCenterContainer.offsetLeftAndRight(shouldScrolled - currentScrolled);
    }
}
