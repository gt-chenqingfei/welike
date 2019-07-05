package com.redefine.richtext.copy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

/**
 * Created by MR on 2018/1/25.
 */

public class RichTextClipboardManager {

    private CharSequence mData;

    private RichTextClipboardManager() {

    }

    private static class RichTextClipboardManagerHolder {
        private static final RichTextClipboardManager INSTANCE = new RichTextClipboardManager();

        private RichTextClipboardManagerHolder() {

        }
    }

    public static RichTextClipboardManager getInstance() {
        return RichTextClipboardManagerHolder.INSTANCE;
    }

    public void copy(Context context, CharSequence charSequence) {
        mData = charSequence;
        ClipData data = ClipData.newPlainText("", charSequence.toString());
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            return ;
        }
        manager.setPrimaryClip(data);

    }

    public CharSequence parse(Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null || manager.getPrimaryClip() == null) {
            if (TextUtils.isEmpty(mData)) {
                return new SpannableStringBuilder();
            }
            return mData;
        }
        if (manager.getPrimaryClip().getItemCount() > 0) {
            CharSequence charSequence = manager.getPrimaryClip().getItemAt(0).getText();
            if (charSequence == null) {
                return new SpannableStringBuilder();
            }
            if (mData != null && TextUtils.equals(charSequence.toString(), mData.toString())) {
                return mData;
            }
            return charSequence;
        }
        return mData;
    }
}
