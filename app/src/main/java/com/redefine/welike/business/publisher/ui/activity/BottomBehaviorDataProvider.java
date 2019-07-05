package com.redefine.welike.business.publisher.ui.activity;

import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.ui.bean.BottomBehaviorItem;

import java.util.ArrayList;
import java.util.List;

public class BottomBehaviorDataProvider {


    public static List<BottomBehaviorItem> getShortCutEntrances() {
        List<BottomBehaviorItem> shortCutItems = new ArrayList<>();

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_ALUMB_ITEM_TYPE, R.drawable.selector_short_cut_alumb,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "short_cut_alumb_name")));
        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_SNAPSHOT_ITEM_TYPE, R.drawable.selector_short_cut_snapshot,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "short_cut_snapshot_name")));
        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_VIDEO_ITEM_TYPE, R.drawable.selector_short_cut_video,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "short_cut_video_name")));

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_POLL_ITEM_TYPE, R.drawable.selector_short_cut_poll,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "short_cut_poll_name")));

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_EASY_POST, R.drawable.selector_short_cut_easy_post,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "image_status")));
        return shortCutItems;
    }


    public static List<BottomBehaviorItem> getPostBehaviorData() {
        List<BottomBehaviorItem> shortCutItems = new ArrayList<>();

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_ALUMB_ITEM_TYPE, R.drawable.selector_short_cut_alumb,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "short_cut_alumb_name")));
        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_SNAPSHOT_ITEM_TYPE, R.drawable.selector_short_cut_snapshot,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "short_cut_snapshot_name")));
        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_VIDEO_ITEM_TYPE, R.drawable.selector_short_cut_video,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "short_cut_video_name")));

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_POLL_ITEM_TYPE, R.drawable.selector_short_cut_poll,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "short_cut_poll_name")));

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_EASY_POST, R.drawable.selector_short_cut_easy_post,
                ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "image_status")));

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_EMOJI_TYPE, R.drawable.editor_selector_emoji,
                ""));

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_AT_TYPE, R.drawable.selector_short_cut_at,
                ""));

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_LINK_TYPE, R.drawable.selector_short_cut_link,
                ""));
        return shortCutItems;
    }

    public static List<BottomBehaviorItem> getCommentBehaviorData() {
        List<BottomBehaviorItem> shortCutItems = new ArrayList<>();

        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_EMOJI_TYPE, R.drawable.editor_selector_emoji, ""));
        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_AT_TYPE, R.drawable.selector_short_cut_at, ""));
        BottomBehaviorItem topic = new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_TOPIC_ITEM_TYPE, R.drawable.editor_selector_hash, "");
        topic.isSelected = false;
        shortCutItems.add(topic);
        shortCutItems.add(new BottomBehaviorItem(BottomBehaviorItem.BEHAVIOR_LINK_TYPE, R.drawable.selector_short_cut_link, ""));

        return shortCutItems;
    }
}
