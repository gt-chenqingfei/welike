package com.welike.emoji.listeners;

import android.support.annotation.NonNull;
import com.welike.emoji.EmojiImageView;
import com.welike.emoji.emoji.Emoji;

public interface OnEmojiLongClickListener {
  void onEmojiLongClick(@NonNull EmojiImageView view, @NonNull Emoji emoji);
}
