package com.welike.emoji;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.welike.emoji.emoji.Emoji;
import com.welike.emoji.listeners.OnEmojiClickListener;
import com.welike.emoji.listeners.OnEmojiLongClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.welike.emoji.Utils.checkNotNull;

final class EmojiArrayAdapter extends ArrayAdapter<Emoji> {
    @Nullable
    private final VariantEmoji variantManager;

    @Nullable
    private final OnEmojiClickListener listener;
    @Nullable
    private final OnEmojiLongClickListener longListener;

    EmojiArrayAdapter(@NonNull final Context context, @NonNull final Emoji[] emojis, @Nullable final VariantEmoji variantManager,
                      @Nullable final OnEmojiClickListener listener, @Nullable final OnEmojiLongClickListener longListener) {
        super(context, 0, new ArrayList<>(Arrays.asList(emojis)));

        this.variantManager = variantManager;
        this.listener = listener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        EmojiImageView image = (EmojiImageView) convertView;

        final Context context = getContext();

        if (image == null) {
            image = (EmojiImageView) LayoutInflater.from(context).inflate(com.welike.emoji.R.layout.emoji_item, parent, false);

            image.setOnEmojiClickListener(listener);
            image.setOnEmojiLongClickListener(longListener);
        }

        final Emoji emoji = checkNotNull(getItem(position), "emoji == null");
        final Emoji variantToUse = variantManager == null ? emoji : variantManager.getVariant(emoji);
        image.setEmoji(variantToUse);
        return image;
    }

    void updateEmojis(final Collection<Emoji> emojis) {
        clear();
        addAll(emojis);
        notifyDataSetChanged();
    }
}
