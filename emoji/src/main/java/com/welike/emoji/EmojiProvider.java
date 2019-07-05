package com.welike.emoji;

import android.support.annotation.NonNull;

import com.welike.emoji.emoji.EmojiCategory;

/**
 * Interface for a custom emoji implementation that can be used with {@link EmojiManager}.
 */
public interface EmojiProvider {
    /**
     * @return The Array of categories.
     */
    @NonNull
    EmojiCategory[] getCategories();

    /**
     * @return The Array of support emoji list
     */
    String[] getSupportEmojiWhiteList();
}
