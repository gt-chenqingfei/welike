package com.redefine.richtext.helper;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.redefine.richtext.RichContent;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.block.BlockFactory;
import com.redefine.richtext.processor.RichEditTextProcessor;
import com.redefine.richtext.span.EmojiSpan;
import com.redefine.richtext.span.RichSpan;
import com.redefine.richtext.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/4/17.
 */

public class RichTextHelper {
    private static final int EMOJI_LENGTH = 2;
    private static final int LINK_LENGTH = 10;
    private static final int TOPIC_LENGTH = 4;
    private static final int SUPER_TOPIC_LENGTH = 4;
    private static final int MENTION_LENGTH = 4;
    private static final int ARTICLE_LENGTH = 5;
    private static final int UN_SUPPORT_LENGTH = 1;

    public static final String article_ = "\uD83D\uDCC4";
    public static final String inner_image_dot = "\uD83D\uDDBC";


    public static RichContent createRichContent(String uid, String nick) {
        Block block = BlockFactory.createMention(nick, uid);
        RichContent richContent = new RichContent();
        richContent.text = block.blockText;
        richContent.summary = block.blockText;
        richContent.richItemList = new ArrayList<>();
        richContent.richItemList.add(block.richItem);
        return richContent;
    }

    // merge 普通文本和富文本
    public static RichContent mergeRichText(RichContent content, String text) {
        RichContent richContent = new RichContent();
        if (content == null) {
            richContent.text = text;
            richContent.summary = text;
            return richContent;
        }

        if (TextUtils.isEmpty(text)) {
            return content.copyRichContent();
        }

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content.text);
        spannableStringBuilder.append(text);
        richContent.text = spannableStringBuilder.toString();
        richContent.summary = spannableStringBuilder.toString();
        richContent.richItemList = content.copyRichItemList();
        return richContent;
    }

    // merge 普通文本和富文本
    public static RichContent mergeRichText(String text, RichContent content) {
        RichContent richContent = new RichContent();
        if (content == null) {
            richContent.text = text;
            richContent.summary = text;
            return richContent;
        }

        if (TextUtils.isEmpty(text)) {
            return content.copyRichContent();
        }

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        int length = spannableStringBuilder.length();
        spannableStringBuilder.append(content.text);
        richContent.text = spannableStringBuilder.toString();
        richContent.summary = spannableStringBuilder.toString();
        if (CollectionUtil.isEmpty(content.richItemList)) {
            return richContent;
        }
        // 需要矫正comment的富文本
        ArrayList<RichItem> richItemList = new ArrayList<>();

        RichItem temp;
        // 需要深拷贝，因为修改了index
        for (RichItem item : content.richItemList) {
            temp = item.copyRichItem();
            temp.index = item.index + length;
            richItemList.add(temp);
        }
        richContent.richItemList = richItemList;
        return richContent;
    }

    // merge 两个富文本
    public static RichContent mergeRichText(RichContent text, RichContent content) {

        if (content == null && text != null) {
            return text.copyRichContent();
        } else if (content != null && text == null) {
            return content.copyRichContent();
        } else if (content == null && text == null) {
            return new RichContent();
        }

        RichContent richContent = new RichContent();

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text.text);
        int length = spannableStringBuilder.length();
        spannableStringBuilder.append(content.text);
        richContent.text = spannableStringBuilder.toString();
        richContent.summary = spannableStringBuilder.toString();
        richContent.richItemList = text.copyRichItemList();
        if (CollectionUtil.isEmpty(content.richItemList)) {
            return richContent;
        }
        if (richContent.richItemList == null) {
            richContent.richItemList = new ArrayList<>();
        }
        RichItem temp;

        // 需要深拷贝，因为修改了index
        for (RichItem item : content.richItemList) {
            temp = item.copyRichItem();
            temp.index = item.index + length;
            richContent.richItemList.add(temp);
        }
        return richContent;
    }


    public static int getRichTextLength(Spannable editor) {

        RichSpan[] spans = editor.getSpans(0, editor.length(), RichSpan.class);
        EmojiSpan[] emojiSpans = editor.getSpans(0, editor.length(), EmojiSpan.class);
        int length = editor.length();
        if (!CollectionUtil.isEmpty(emojiSpans)) {
            for (EmojiSpan span : emojiSpans) {
                int start = editor.getSpanStart(span);
                int end = editor.getSpanEnd(span);
                length = length - end + start + EMOJI_LENGTH;
            }
        }

        if (!CollectionUtil.isEmpty(spans)) {
            for (RichSpan span : spans) {
                int start = editor.getSpanStart(span);
                int end = editor.getSpanEnd(span);
                length = length - end + start + getRichTextLength(span);
            }
        }
        return length;
    }

    private static int getRichTextLength(RichSpan span) {

        if (span.getBlock() == null || span.getRichItem() == null) {
            return 0;
        }
        RichItem richItem = span.getRichItem();
        if (richItem.isAtItem()) {
            return MENTION_LENGTH;
        } else if (richItem.isLinkItem()) {
            return LINK_LENGTH;
        } else if (richItem.isTopicItem()) {
            return TOPIC_LENGTH;
        } else if (richItem.isSuperTopicItem()) {
            return SUPER_TOPIC_LENGTH;
        } else if (richItem.isInnerImageItem()) {
            return 1;
        } else if (richItem.isArticleItem()) {
            return ARTICLE_LENGTH;
        } else {
            return 0;
        }
    }

    public static int getRichTextLength(RichEditTextProcessor.Range range) {

        if (range.type == RichEditTextProcessor.Range.COMMON) {
            return range.end - range.start;
        }
        if (range.type == RichEditTextProcessor.Range.EMOJI) {
            return EMOJI_LENGTH;
        } else if (range.type == RichEditTextProcessor.Range.LINK) {
            return LINK_LENGTH;
        } else if (range.type == RichEditTextProcessor.Range.TOPIC) {
            return TOPIC_LENGTH;
        } else if (range.type == RichEditTextProcessor.Range.MENTION) {
            return MENTION_LENGTH;
        } else if (range.type == RichEditTextProcessor.Range.ARTICLE) {
            return ARTICLE_LENGTH;
        } else {
            // 不支持类型暂返回1
            return UN_SUPPORT_LENGTH;
        }
    }

}
