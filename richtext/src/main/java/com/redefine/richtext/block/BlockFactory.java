package com.redefine.richtext.block;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.style.URLSpan;

import com.redefine.richtext.R;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.helper.RichTextHelper;

/**
 * Created by MR on 2018/1/24.
 */

public class BlockFactory {

    public static Block getBlockByUrlSpan(String webLink, Editable editor, URLSpan span) {

        Block block = new Block();
        block.blockText = webLink;
        block.richItem = new RichItem();
        block.richItem.source = span.getURL();
        block.richItem.target = span.getURL();
        block.richItem.length = block.blockText.length();
        block.richItem.index = editor.getSpanStart(span);
        block.richItem.type = RichItem.RICH_TYPE_LINK;
        block.richItem.display = block.blockText;
        return block;
    }

    public static Block createMention(String nickName, String uid) {
        Block block = new Block();
        block.blockText = "@" + nickName;
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_MENTION;
        block.richItem.source = block.blockText;
        block.richItem.display = block.blockText;
        block.richItem.length = block.blockText.length();
        block.richItem.id = uid;
        return block;
    }

    public static Block createLink(String webLink, String name, String url) {
        Block block = new Block();
        if (!TextUtils.isEmpty(name)) {
            block.blockText = name + " ";
        } else {
            block.blockText = webLink + " ";
        }
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_LINK;
        block.richItem.source = url;
        block.richItem.target = url;
        block.richItem.display = block.blockText;
        block.richItem.length = block.blockText.length() - 1;
        if (!TextUtils.isEmpty(name)) {
            block.richItem.title = name;
        }
        return block;
    }

    public static Block createMore(String display) {
        Block block = new Block();
        block.blockText = display;
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_MORE;
        block.richItem.source = display;
        block.richItem.target = display;
        block.richItem.display = block.blockText;
        block.richItem.length = block.blockText.length();
        return block;
    }

    public static Block createLess(String display) {
        Block block = new Block();
        block.blockText = display;
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_LESS;
        block.richItem.source = display;
        block.richItem.target = display;
        block.richItem.display = block.blockText;
        block.richItem.length = block.blockText.length();
        return block;
    }

    public static Block createTopic(String name, String id) {
        Block block = new Block();
        block.blockText = "#" + name;
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_TOPIC;
        block.richItem.source = block.blockText;
        block.richItem.display = block.blockText;
        block.richItem.length = block.blockText.length();
        block.richItem.id = id;
        return block;
    }


    public static Block createSuperTopic(String id, String name, String tag) {
        Block block = new Block();
        block.blockText = name + " ";
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_SUPER_TOPIC;
        block.richItem.source = block.blockText;
        block.richItem.display = block.blockText;
        block.richItem.classify = tag;
        block.richItem.length = block.blockText.length() - 1;
        block.richItem.id = id;
        return block;
    }

    public static Block createTopicWithOutFlag(String id, String name, String labelId) {
        Block block = new Block();
        block.blockText = name;
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_TOPIC;
        block.richItem.source = block.blockText;
        block.richItem.display = block.blockText;
        block.richItem.classify = labelId;
        block.richItem.length = block.blockText.length();
        block.richItem.id = id;
        return block;
    }

    public static Block createTopicWithOutFlag(String name, String id) {
        Block block = new Block();
        block.blockText = name;
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_TOPIC;
        block.richItem.source = block.blockText;
        block.richItem.display = block.blockText;
        block.richItem.length = block.blockText.length();
        block.richItem.id = id;
        return block;
    }

    public static Block createMentionWithOutFlag(String userId, String mention) {
        Block block = new Block();
        block.blockText = mention;
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_MENTION;
        block.richItem.source = block.blockText;
        block.richItem.display = block.blockText;
        block.richItem.length = block.blockText.length();
        block.richItem.id = userId;
        return block;
    }

    public static Block createInnerImageBlock(Context context, String classify, int width, int height, String icon, long fileSize, String mimeType) {
        Block block = new Block();
        block.blockText = RichTextHelper.inner_image_dot;
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_INNER_IMAGE;
        block.richItem.source = icon;
        block.richItem.display = block.blockText;
        block.richItem.length = block.blockText.length();
        block.richItem.id = "";
        block.richItem.classify = classify;
        block.richItem.width = width;
        block.richItem.height = height;
        block.richItem.icon = icon;
        block.richItem.mimeType = mimeType;
        block.richItem.size = fileSize;
        return block;
    }

    public static Block createArticle(Context context, String title) {
        Block block = new Block();
        block.blockText = RichTextHelper.article_ + title + " ";
        block.richItem = new RichItem();
        block.richItem.type = RichItem.RICH_TYPE_ARTICLE;
        block.richItem.source = block.blockText;
        block.richItem.display = block.blockText;
        block.richItem.length = block.blockText.length() - 1;
        return block;
    }
}
