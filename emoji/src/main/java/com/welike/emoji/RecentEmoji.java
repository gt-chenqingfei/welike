package com.welike.emoji;

import android.support.annotation.NonNull;

import com.welike.emoji.emoji.Emoji;

import java.util.Collection;

/**
 * Interface for providing some custom implementation for recent emojis.
 */
public interface RecentEmoji {
    /**
     * Returns the recent emojis. Could be loaded from a database, shared preferences or just hard
     * coded.<br>
     * <p>
     * This method will be called more than one time hence it is recommended to hold a collection of
     * recent emojis.
     */
    @NonNull
    Collection<Emoji> getRecentEmojis();

    /**
     * Should add the emoji to the recent ones. After calling this method, {@link #getRecentEmojis()}
     * should return the emoji that was just added.
     */
    void addEmoji(@NonNull Emoji emoji);

    /**
     * Should persist all emojis.
     */
    void persist();
}
