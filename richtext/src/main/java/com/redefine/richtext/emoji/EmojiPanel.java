package com.redefine.richtext.emoji;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.welike.emoji.EmojiImageView;
import com.welike.emoji.EmojiVariantPopup;
import com.welike.emoji.EmojiView;
import com.welike.emoji.RecentEmoji;
import com.welike.emoji.RecentEmojiManager;
import com.welike.emoji.VariantEmoji;
import com.welike.emoji.VariantEmojiManager;
import com.welike.emoji.emoji.Emoji;
import com.welike.emoji.listeners.OnEmojiBackspaceClickListener;
import com.welike.emoji.listeners.OnEmojiClickListener;
import com.welike.emoji.listeners.OnEmojiLongClickListener;
import com.redefine.richtext.emoji.bean.EmojiBean;

import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * Created by MR on 2018/1/24.
 */

public class EmojiPanel extends LinearLayout implements OnEmojiClickListener, OnEmojiLongClickListener {

    private OnEmojiItemClickListener onEmojiItemClickListener;
    private RecentEmoji recentEmoji;
    private VariantEmoji variantEmoji;
    private EmojiVariantPopup variantPopup;


    public EmojiPanel(Context context) {
        super(context);
        init();
    }

    public EmojiPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmojiPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EmojiPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        this.recentEmoji = new RecentEmojiManager(getContext());
        this.variantEmoji = new VariantEmojiManager(getContext());

        int keyboardHeight = KeyboardUtil.getKeyboardHeight(getContext());

        final EmojiView emojiView = new EmojiView(getContext(), this,
                this, recentEmoji, variantEmoji, keyboardHeight);
        emojiView.setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
            @Override
            public void onEmojiBackspaceClick(final View v) {
                if (onEmojiItemClickListener != null) {
                    onEmojiItemClickListener.onEmojiDelClick();
                }
            }
        });

        variantPopup = new EmojiVariantPopup(this, this);

        this.addView(emojiView);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.recentEmoji.persist();
        this.variantEmoji.persist();
    }

    public void setOnEmojiItemClickListener(OnEmojiItemClickListener listener) {
        this.onEmojiItemClickListener = listener;
    }

    @Override
    public void onEmojiClick(@NonNull EmojiImageView emoji, @NonNull Emoji imageView) {
        recentEmoji.addEmoji(imageView);
        variantEmoji.addVariant(imageView);
        emoji.updateEmoji(imageView);
        Log.e("emoji", "emoji:" + imageView.getUnicode());
        if (this.onEmojiItemClickListener != null) {
            EmojiBean emojiBean = new EmojiBean(imageView.getUnicode(), 0);
            this.onEmojiItemClickListener.onEmojiClick(emojiBean);
        }

        variantPopup.dismiss();
    }

    @Override
    public void onEmojiLongClick(@NonNull EmojiImageView view, @NonNull Emoji emoji) {
        variantPopup.show(view, emoji);
    }

    public static interface OnEmojiItemClickListener {
        void onEmojiClick(EmojiBean emojiBean);

        void onEmojiDelClick();
    }


}
