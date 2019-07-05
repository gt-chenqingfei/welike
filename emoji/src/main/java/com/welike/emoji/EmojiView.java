package com.welike.emoji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.welike.emoji.emoji.EmojiCategory;
import com.welike.emoji.listeners.OnEmojiBackspaceClickListener;
import com.welike.emoji.listeners.OnEmojiClickListener;
import com.welike.emoji.listeners.OnEmojiLongClickListener;
import com.welike.emoji.listeners.RepeatListener;

import java.util.concurrent.TimeUnit;

@SuppressLint("ViewConstructor")
public final class EmojiView extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static final long INITIAL_INTERVAL = TimeUnit.SECONDS.toMillis(1) / 2;
    private static final int NORMAL_INTERVAL = 50;


    private final ImageView[] emojiTabs;
    private final EmojiPagerAdapter emojiPagerAdapter;

    @Nullable
    OnEmojiBackspaceClickListener onEmojiBackspaceClickListener;

    private int emojiTabLastSelectedIndex = -1;

    public EmojiView(final Context context, final OnEmojiClickListener onEmojiClickListener,
                     final OnEmojiLongClickListener onEmojiLongClickListener,
                     @NonNull final RecentEmoji recentEmoji,
                     @NonNull final VariantEmoji variantManager, final int keyboardHeight) {
        super(context);

        View.inflate(context, R.layout.emoji_view, this);


        setOrientation(VERTICAL);
        setBackgroundColor(ContextCompat.getColor(context, R.color.emoji_background));


        final ViewPager emojisPager = findViewById(R.id.emojis_pager);
        final LinearLayout emojisTab = findViewById(R.id.emojis_tab);
        if (keyboardHeight > 0) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) emojisPager.getLayoutParams();
            params.height = keyboardHeight - emojisTab.getLayoutParams().height;
            emojisPager.setLayoutParams(params);
        }


        emojisPager.addOnPageChangeListener(this);

        final EmojiCategory[] categories = EmojiManager.getInstance().getCategories();

        emojiTabs = new ImageView[categories.length + 2];
        emojiTabs[0] = inflateButton(context, R.drawable.emoji_ic_vector_recent, emojisTab);
        for (int i = 0; i < categories.length; i++) {
            emojiTabs[i + 1] = inflateButton(context, categories[i].getIcon(), emojisTab);
        }
        emojiTabs[emojiTabs.length - 1] = inflateButton(context, R.drawable.emoji_ic_vector_backspace, emojisTab);

        handleOnClicks(emojisPager);

        emojiPagerAdapter = new EmojiPagerAdapter(onEmojiClickListener, onEmojiLongClickListener, recentEmoji, variantManager);
        emojisPager.setAdapter(emojiPagerAdapter);

        final int startIndex = emojiPagerAdapter.numberOfRecentEmojis() > 0 ? 0 : 1;
        emojisPager.setCurrentItem(startIndex);
        onPageSelected(startIndex);
    }

    private void handleOnClicks(final ViewPager emojisPager) {
        for (int i = 0; i < emojiTabs.length - 1; i++) {
            emojiTabs[i].setOnClickListener(new EmojiTabsClickListener(emojisPager, i));
        }

        emojiTabs[emojiTabs.length - 1].setOnTouchListener(new RepeatListener(INITIAL_INTERVAL, NORMAL_INTERVAL, new OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (onEmojiBackspaceClickListener != null) {
                    onEmojiBackspaceClickListener.onEmojiBackspaceClick(view);
                }
            }
        }));
    }

    public void setOnEmojiBackspaceClickListener(@Nullable final OnEmojiBackspaceClickListener onEmojiBackspaceClickListener) {
        this.onEmojiBackspaceClickListener = onEmojiBackspaceClickListener;
    }

    private ImageView inflateButton(final Context context, @DrawableRes final int icon, final ViewGroup parent) {
        final ImageView button = (ImageView) LayoutInflater.from(context).inflate(R.layout.emoji_category, parent, false);

        button.setImageDrawable(AppCompatResources.getDrawable(context, icon));

        parent.addView(button);

        return button;
    }

    @Override
    public void onPageSelected(final int i) {
        if (emojiTabLastSelectedIndex != i) {
            if (i == 0) {
                emojiPagerAdapter.invalidateRecentEmojis();
            }

            if (emojiTabLastSelectedIndex >= 0 && emojiTabLastSelectedIndex < emojiTabs.length) {
                emojiTabs[emojiTabLastSelectedIndex].setSelected(false);
            }

            emojiTabs[i].setSelected(true);
            emojiTabLastSelectedIndex = i;
        }
    }

    @Override
    public void onPageScrolled(final int i, final float v, final int i2) {
        // No-op.
    }

    @Override
    public void onPageScrollStateChanged(final int i) {
        // No-op.
    }

    static class EmojiTabsClickListener implements OnClickListener {
        private final ViewPager emojisPager;
        private final int position;

        EmojiTabsClickListener(final ViewPager emojisPager, final int position) {
            this.emojisPager = emojisPager;
            this.position = position;
        }

        @Override
        public void onClick(final View v) {
            emojisPager.setCurrentItem(position);
        }
    }
}
