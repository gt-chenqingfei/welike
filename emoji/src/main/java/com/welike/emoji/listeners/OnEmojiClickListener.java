package com.welike.emoji.listeners;

import android.support.annotation.NonNull;
import com.welike.emoji.EmojiImageView;
import com.welike.emoji.emoji.Emoji;

public interface OnEmojiClickListener {
  void onEmojiClick(@NonNull EmojiImageView emoji, @NonNull Emoji imageView);
}
