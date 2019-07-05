package com.redefine.richtext.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

import com.redefine.richtext.emoji.bean.EmojiBean;


/**
 * Created by MR on 2018/1/23.
 */

public class EmojiSpan extends ImageSpan {
    private EmojiBean mEmojiBean;

    public EmojiSpan(Context context, Bitmap b) {
        super(context, b);
    }

    public EmojiSpan(Context context, Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

    public EmojiSpan(Drawable d) {
        super(d);
    }

    public EmojiSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public EmojiSpan(Drawable d, String source) {
        super(d, source);
    }

    public EmojiSpan(Drawable d, String source, int verticalAlignment) {
        super(d, source, verticalAlignment);
    }

    public EmojiSpan(Context context, Uri uri) {
        super(context, uri);
    }

    public EmojiSpan(Context context, Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public EmojiSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public EmojiSpan(Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }


    public EmojiSpan(EmojiBean emojiBean, Drawable drawable) {
        super(drawable);
        mEmojiBean = emojiBean;
    }

    public EmojiBean getEmojiBean() {
        return mEmojiBean;
    }
}
