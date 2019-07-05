package com.redefine.richtext.drawee;

/**
 * Name: RichDraweeViewFactory
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-08-09 12:07
 */
public class RichDraweeViewFactory {

    public static IDraweeDelegateView createDraweeView(IRichDraweeSpanView spanView) {
        return new DraweeDelegateTextView(spanView);
    }

    public static IDraweeDelegateView createDraweeEditView(IRichDraweeSpanView spanView) {
        return new DraweeDelegateEditView(spanView);
    }
}
