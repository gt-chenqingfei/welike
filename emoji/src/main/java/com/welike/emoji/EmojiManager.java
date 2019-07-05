package com.welike.emoji;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.FontRequestEmojiCompatConfig;
import android.support.v4.provider.FontRequest;
import android.text.TextUtils;
import android.util.Log;

import com.welike.emoji.emoji.Emoji;
import com.welike.emoji.emoji.EmojiCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.welike.emoji.Utils.checkNotNull;

/**
 * EmojiManager where an EmojiProvider can be installed for further usage.
 */
public final class EmojiManager {
    private static final EmojiManager instance = new EmojiManager();
    private static final int GUESSED_UNICODE_AMOUNT = 3000;
    private static final int GUESSED_TOTAL_PATTERN_LENGTH = GUESSED_UNICODE_AMOUNT * 4;

    private static final Comparator<String> STRING_LENGTH_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(final String first, final String second) {
            final int firstLength = first.length();
            final int secondLength = second.length();

            return firstLength < secondLength ? 1 : firstLength == secondLength ? 0 : -1;
        }
    };

    private final Map<String, Emoji> emojiMap = new LinkedHashMap<>(GUESSED_UNICODE_AMOUNT);
    private EmojiCategory[] categories;
    private Pattern emojiPattern;
    private Pattern emojiRepetitivePattern;

    private final Map<String, String> supportEmojiMap = new LinkedHashMap<>();

    private EmojiManager() {
        // No instances apart from singleton.
    }

    static EmojiManager getInstance() {
        return instance;
    }

    /**
     * Installs the given EmojiProvider.
     * <p>
     * NOTE: That only one can be present at any time.
     *
     * @param provider the provider that should be installed.
     */
    public static void install(@NonNull final EmojiProvider provider, Context context) {
        FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.init(new FontRequestEmojiCompatConfig(context, fontRequest));

        instance.categories = checkNotNull(provider.getCategories(), "categories == null");
        instance.emojiMap.clear();
        instance.supportEmojiMap.clear();
        instance.initSupportEmojiMap(provider);

        final List<String> unicodesForPattern = new ArrayList<>(GUESSED_UNICODE_AMOUNT);

        final int categoriesSize = instance.categories.length;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < categoriesSize; i++) {
            EmojiCategory category = instance.categories[i];
            final Emoji[] emojis = checkNotNull(category.getEmojis(), "emojies == null");
            List<Emoji> supportEmojiList = new ArrayList();
            final int emojisSize = emojis.length;
            //noinspection ForLoopReplaceableByForEach
            for (int j = 0; j < emojisSize; j++) {
                final Emoji emoji = emojis[j];
                final String unicode = emoji.getUnicode();
                final List<Emoji> variants = emoji.getVariants();

                instance.emojiMap.put(unicode, emoji);
                unicodesForPattern.add(unicode);

                //noinspection ForLoopReplaceableByForEach
                for (int k = 0; k < variants.size(); k++) {
                    final Emoji variant = variants.get(k);
                    final String variantUnicode = variant.getUnicode();

                    instance.emojiMap.put(variantUnicode, variant);
                    unicodesForPattern.add(variantUnicode);
                }

                instance.filterEmoji(supportEmojiList, unicode, emoji, category);
            }
        }


        if (unicodesForPattern.isEmpty()) {
            throw new IllegalArgumentException("Your EmojiProvider must at least have one category with at least one emoji.");
        }

        // We need to sort the unicodes by length so the longest one gets matched first.
        Collections.sort(unicodesForPattern, STRING_LENGTH_COMPARATOR);

        final StringBuilder patternBuilder = new StringBuilder(GUESSED_TOTAL_PATTERN_LENGTH);

        final int unicodesForPatternSize = unicodesForPattern.size();
        for (int i = 0; i < unicodesForPatternSize; i++) {
            patternBuilder.append(Pattern.quote(unicodesForPattern.get(i))).append('|');
        }

        final String regex = patternBuilder.deleteCharAt(patternBuilder.length() - 1).toString();
        instance.emojiPattern = Pattern.compile(regex);
        instance.emojiRepetitivePattern = Pattern.compile('(' + regex + ")+");
    }

    static void destroy() {
        instance.emojiMap.clear();
        instance.categories = null;
        instance.emojiPattern = null;
        instance.emojiRepetitivePattern = null;
    }

    EmojiCategory[] getCategories() {
        verifyInstalled();
        return categories; // NOPMD
    }


    Pattern getEmojiRepetitivePattern() {
        return emojiRepetitivePattern;
    }

    @NonNull
    List<EmojiRange> findAllEmojis(@Nullable final CharSequence text) {
        verifyInstalled();

        final List<EmojiRange> result = new ArrayList<>();

        if (!TextUtils.isEmpty(text)) {
            final Matcher matcher = emojiPattern.matcher(text);

            while (matcher.find()) {
                final Emoji found = findEmoji(text.subSequence(matcher.start(), matcher.end()));

                if (found != null) {
                    result.add(new EmojiRange(matcher.start(), matcher.end(), found));
                }
            }
        }

        return result;
    }

    @Nullable
    Emoji findEmoji(@NonNull final CharSequence candidate) {
        verifyInstalled();

        // We need to call toString on the candidate, since the emojiMap may not find the requested entry otherwise, because
        // the type is different.
        return emojiMap.get(candidate.toString());
    }

    void verifyInstalled() {
        if (categories == null) {
            throw new IllegalStateException("Please install an EmojiProvider through the EmojiManager.install() method first.");
        }
    }

    private void initSupportEmojiMap(EmojiProvider provider) {
        String[] supportList = provider.getSupportEmojiWhiteList();
        if (supportList == null) {
            return;
        }
        for (int i = 0; i < supportList.length; i++) {
            String unicode = supportList[i];
            instance.supportEmojiMap.put(unicode, unicode);
        }
    }

    private void filterEmoji(List<Emoji> supportEmojiList, String unicode, Emoji emoji, EmojiCategory category) {
        if (!instance.supportEmojiMap.isEmpty()) {
            if (instance.supportEmojiMap.containsKey(unicode)) {
                supportEmojiList.add(emoji);
            }

            final Emoji[] emojiSupport = new Emoji[supportEmojiList.size()];
            for (int k = 0; k < supportEmojiList.size(); k++) {
                emojiSupport[k] = supportEmojiList.get(k);
            }
            category.updateFilterEmojis(emojiSupport);
        }
    }
}
