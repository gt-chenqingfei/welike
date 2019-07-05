package com.redefine.richtext.span;

import android.view.View;

/**
 * Name: IClickableSpan
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-07-17 00:13
 */
public interface IClickableSpan {
    public void onClick(View widget);

    void setPressed(boolean b);
}
