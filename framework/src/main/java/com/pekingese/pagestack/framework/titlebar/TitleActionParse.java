/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/9/30
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/9/30
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/9/30, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.titlebar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pekingese.pagestack.framework.constant.CommonConstant;
import com.pekingese.pagestack.framework.util.CollectionUtil;
import com.pekingese.pagestack.framework.util.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

public class TitleActionParse implements ITitleActionParse {
    @Override
    public List<View> parseTitleActions(Context context, List<TitleAction> actions) {
        if (CollectionUtil.isArrayEmpty(actions)) {
            return null;
        }
        List<View> views = new ArrayList<View>();
        for (TitleAction action : actions) {
            if (action == null || !action.isValid()) {
                continue;
            }
            View view = parseAction(context, action);
            if (view != null) {
                views.add(view);
            }
        }
        return views;
    }

    private View parseAction(Context context, TitleAction action) {
        View view = null;
        if (action.getImageId() != CommonConstant.INVALID_ID) {
            ImageView image = new ImageView(context);
            image.setScaleType(ImageView.ScaleType.CENTER);
            image.setImageResource(action.getImageId());
            view = image;

        } else if (action.getActionTextId() != CommonConstant.INVALID_ID || !TextUtils.isEmpty(action.getActionText())) {
            TextView textView = new TextView(context);
            String text = action.getActionTextId() == CommonConstant.INVALID_ID ? action.getActionText() : context.getString(action.getActionTextId());
            textView.setText(text);
            textView.setGravity(Gravity.CENTER);
            if (action.hasDrawable()) {
                Drawable left = context.getResources().getDrawable(action.getDrawableLeftId());
                Drawable top = context.getResources().getDrawable(action.getDrawableTopId());
                Drawable right = context.getResources().getDrawable(action.getDrawableRightId());
                Drawable bottom = context.getResources().getDrawable(action.getDrawableBottomId());
                textView.setCompoundDrawables(left, top, right, bottom);
                textView.setCompoundDrawablePadding(action.getDrawablePadding());
            }
            view = textView;
        }
        if (view != null) {
            view.setTag(action);
            int width = action.getWidth() > 0 ? DeviceUtil.dp2px(context.getResources(), action.getWidth()) : action.getWidth();
            int height = action.getHeight() > 0 ? DeviceUtil.dp2px(context.getResources(), action.getHeight()) : action.getHeight();
            view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        }
        return view;
    }
}
