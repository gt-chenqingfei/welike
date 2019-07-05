package com.redefine.welike.business.feeds.management.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.entity.MimeType;
import com.redefine.richtext.RichItem;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.business.publisher.api.bean.AttachmentCategory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liubin on 2018/1/25.
 */

public class RichTextContentParser {
    public static final String RICH_TEXT_ATTACHMENT_KEY_TYPE = "type";
    public static final String RICH_TEXT_ATTACHMENT_KEY_SOURCE = "source";
    public static final String RICH_TEXT_ATTACHMENT_KEY_INDEX = "index";
    public static final String RICH_TEXT_ATTACHMENT_KEY_LENGTH = "length";
    public static final String RICH_TEXT_ATTACHMENT_KEY_TARGET = "target";
    public static final String RICH_TEXT_ATTACHMENT_KEY_DISPLAY = "display";
    public static final String RICH_TEXT_ATTACHMENT_KEY_ID = "richId";
    public static final String RICH_TEXT_ATTACHMENT_KEY_TITLE = "title";
    public static final String RICH_TEXT_ATTACHMENT_KEY_ICON = "icon";
    private static final String RICH_TEXT_ATTACHMENT_KEY_CLASSIFY = "classify";
    private static final String RICH_TEXT_ATTACHMENT_KEY_IMAGE_HEIGHT = "image-height";
    private static final String RICH_TEXT_ATTACHMENT_KEY_IMAGE_WIDTH = "image-width";
    private static final String RICH_TEXT_ATTACHMENT_KEY_VIDEO_WIDTH = "video-width";
    private static final String RICH_TEXT_ATTACHMENT_KEY_VIDEO_HEIGHT = "video-height";

    public static JSONArray parserRichAttachments(List<RichItem> richItemList) {
        if (richItemList != null && richItemList.size() > 0) {
            try {
                int topicCount = 0;
                int superTopic = 0;
                JSONArray attachments = new JSONArray();
                for (RichItem richItem : richItemList) {
                    if (richItem.isTopicItem() || richItem.isSuperTopicItem()) {
                        topicCount++;
                        if (topicCount > GlobalConfig.FEED_MAX_TOPIC) {
                            continue;
                        }
                    }
                    if (richItem.isSuperTopicItem()) {
                        superTopic++;
                        if (superTopic > GlobalConfig.FEED_MAX_SUPER_TOPIC) {
                            continue;
                        }
                    }
                    JSONObject itemJSON = new JSONObject();
                    itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_TYPE, richItem.type);
                    if (!TextUtils.isEmpty(richItem.source)) {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_SOURCE, richItem.source);
                    }
                    if (!TextUtils.isEmpty(richItem.id)) {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_ID, richItem.id);
                    }
                    itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_INDEX, richItem.index);
                    itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_LENGTH, richItem.length);
                    if (!TextUtils.isEmpty(richItem.display)) {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_DISPLAY, richItem.display);
                    }
                    if (!TextUtils.isEmpty(richItem.target)) {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_TARGET, richItem.target);
                    } else {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_TARGET, "");
                    }
                    if (!TextUtils.isEmpty(richItem.title)) {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_TITLE, richItem.title);
                    } else {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_TITLE, "");
                    }
                    if (!TextUtils.isEmpty(richItem.classify)) {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_CLASSIFY, richItem.classify);
                    }
                    if (!TextUtils.isEmpty(richItem.icon)) {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_ICON, richItem.icon);
                    } else {
                        itemJSON.put(RICH_TEXT_ATTACHMENT_KEY_ICON, "");
                    }
                    attachments.add(itemJSON);
                }
                return attachments;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<RichItem> parserRichJSON(String jsonArrayString) {
        if (TextUtils.isEmpty(jsonArrayString)) return null;

        JSONArray jsonArray = null;
        try {
            jsonArray = JSONArray.parseArray(jsonArrayString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parserRichJSONArray(jsonArray);
    }

    public static List<RichItem> parserRichJSONArray(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {
            List<RichItem> richItemList = new ArrayList<>();
            for (Iterator iterator = jsonArray.iterator(); iterator.hasNext(); ) {
                JSONObject itemJSON = (JSONObject) iterator.next();
                try {
                    String type = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_TYPE);
                    if (TextUtils.equals(type, RichItem.RICH_TYPE_ARTICLE) ||
                            TextUtils.equals(type, RichItem.RICH_TYPE_INNER_IMAGE) ||
                            TextUtils.equals(type, RichItem.RICH_TYPE_LESS) ||
                            TextUtils.equals(type, RichItem.RICH_TYPE_MORE) ||
                            TextUtils.equals(type, RichItem.RICH_TYPE_TOPIC) ||
                            TextUtils.equals(type, RichItem.RICH_TYPE_SUPER_TOPIC) ||
                            TextUtils.equals(type, RichItem.RICH_TYPE_LINK) ||
                            TextUtils.equals(type, RichItem.RICH_TYPE_MENTION)) {

                        String source = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_SOURCE);
                        String id = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_ID);
                        int index = itemJSON.getIntValue(RICH_TEXT_ATTACHMENT_KEY_INDEX);
                        int length = itemJSON.getIntValue(RICH_TEXT_ATTACHMENT_KEY_LENGTH);
                        String target = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_TARGET);
                        String display = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_DISPLAY);
                        String title = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_TITLE);
                        String icon = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_ICON);
                        String classify = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_CLASSIFY);

                        RichItem richItem = new RichItem();
                        richItem.type = type;
                        richItem.source = source;
                        richItem.id = id;
                        richItem.index = index;
                        richItem.length = length;
                        richItem.target = target;
                        richItem.display = display;
                        richItem.title = title;
                        richItem.icon = icon;
                        richItem.classify = classify;
                        richItemList.add(richItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return richItemList;
        }
        return null;
    }

    public static List<Item> parserImages(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {
            List<Item> richItemList = new ArrayList<>();
            for (Iterator iterator = jsonArray.iterator(); iterator.hasNext(); ) {
                JSONObject itemJSON = (JSONObject) iterator.next();
                try {
                    String type = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_TYPE);
                    if (TextUtils.equals(type, AttachmentCategory.IMAGE)) {
                        String source = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_SOURCE);
                        int height = itemJSON.getIntValue(RICH_TEXT_ATTACHMENT_KEY_IMAGE_HEIGHT);
                        int width = itemJSON.getIntValue(RICH_TEXT_ATTACHMENT_KEY_IMAGE_WIDTH);
                        Item richItem = new Item(0, source, MimeType.JPEG.toString(), 0, 0, width, height);
                        richItemList.add(richItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return richItemList;
        }
        return null;
    }

    public static Item parserVideo(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {

            for (Iterator iterator = jsonArray.iterator(); iterator.hasNext(); ) {
                JSONObject itemJSON = (JSONObject) iterator.next();
                try {
                    String type = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_TYPE);
                    if (TextUtils.equals(type, AttachmentCategory.VIDEO)) {
                        String source = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_SOURCE);
                        int height = itemJSON.getIntValue(RICH_TEXT_ATTACHMENT_KEY_VIDEO_HEIGHT);
                        int width = itemJSON.getIntValue(RICH_TEXT_ATTACHMENT_KEY_VIDEO_WIDTH);
                        String icon = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_ICON);

                        Item richItem = new Item(0, source, MimeType.MP4.toString(), 0, 0, width, height);
                        richItem.coverPath = icon;
                        return richItem;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
        return null;
    }

    public static JSONObject parsePoll(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {

            for (Iterator iterator = jsonArray.iterator(); iterator.hasNext(); ) {
                JSONObject itemJSON = (JSONObject) iterator.next();
                try {
                    String type = itemJSON.getString(RICH_TEXT_ATTACHMENT_KEY_TYPE);
                    if (TextUtils.equals(type, AttachmentCategory.POLL)) {
                        return itemJSON.getJSONObject(PostsDataSourceParser.KEY_POST_ATTACHMENTS_POOL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
        return null;
    }

}
