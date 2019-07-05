package com.redefine.richtext;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by MR on 2018/1/23.
 */

public class RichItem implements Parcelable, Comparable<RichItem>, Serializable {
    public static final String RICH_TYPE_MENTION = "MENTION";
    public static final String RICH_TYPE_TOPIC = "TOPIC";
    public static final String RICH_TYPE_SUPER_TOPIC = "SUPER_TOPIC";
    public static final String RICH_TYPE_LINK = "LINK";
    public static final String RICH_TYPE_INNER_IMAGE = "IMAGE";
    public static final String RICH_TYPE_ARTICLE = "ARTICLE";
    public static final String RICH_TYPE_MORE = "MORE";
    public static final String RICH_TYPE_LESS = "LESS";

    public static final String RICH_SUB_TYPE_EMOTION = "emotion";
    public static final String RICH_SUB_TYPE_PIC = "pic";
    private static final long serialVersionUID = -7397945400530933630L;

    public String type;
    public String source;
    public String id;
    public int index;
    public int length;
    public String target;
    public String display;
    public String title;
    public String icon;
    public int width;
    public int height;
    public String classify;
    public long size;
    public String mimeType;
    public long duration;

    protected RichItem(Parcel in) {
        type = in.readString();
        source = in.readString();
        id = in.readString();
        index = in.readInt();
        length = in.readInt();
        target = in.readString();
        display = in.readString();
        title = in.readString();
        icon = in.readString();
        width = in.readInt();
        height = in.readInt();
        classify = in.readString();
        size = in.readLong();
        mimeType = in.readString();
        duration = in.readLong();
    }

    public static final Creator<RichItem> CREATOR = new Creator<RichItem>() {
        @Override
        public RichItem createFromParcel(Parcel in) {
            return new RichItem(in);
        }

        @Override
        public RichItem[] newArray(int size) {
            return new RichItem[size];
        }
    };

    public RichItem() {

    }

    @Override
    public int compareTo(@NonNull RichItem o) {
        return index - o.index;
    }

    public boolean isAtItem() { return TextUtils.equals(type, RICH_TYPE_MENTION); }

    public boolean isTopicItem() {
        return TextUtils.equals(type, RICH_TYPE_TOPIC);
    }

    public boolean isSuperTopicItem() {
        return TextUtils.equals(type, RICH_TYPE_SUPER_TOPIC);
    }

    public boolean isLinkItem() {
        return TextUtils.equals(type, RICH_TYPE_LINK);
    }

    public boolean isInnerImageItem() {
        return TextUtils.equals(type, RICH_TYPE_INNER_IMAGE);
    }

    public boolean isMoreItem() {
        return TextUtils.equals(type, RICH_TYPE_MORE);
    }
    public boolean isLessItem() {
        return TextUtils.equals(type, RICH_TYPE_LESS);
    }

    public boolean isArticleItem() {
        return TextUtils.equals(type, RICH_TYPE_ARTICLE);
    }
    public RichItem copyRichItem() {
        RichItem richItem = new RichItem();
        richItem.type = this.type;
        richItem.source = this.source;
        richItem.id = this.id;
        richItem.index = this.index;
        richItem.length = this.length;
        richItem.target = this.target;
        richItem.display = this.display;
        richItem.title = this.title;
        richItem.icon = this.icon;
        richItem.width = this.width;
        richItem.height = this.height;
        richItem.classify = this.classify;
        richItem.size = this.size;
        richItem.mimeType = this.mimeType;
        richItem.duration = this.duration;
        return richItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(source);
        dest.writeString(id);
        dest.writeInt(index);
        dest.writeInt(length);
        dest.writeString(target);
        dest.writeString(display);
        dest.writeString(title);
        dest.writeString(icon);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(classify);
        dest.writeLong(size);
        dest.writeString(mimeType);
        dest.writeLong(duration);
    }
}
