package com.redefine.richtext;

import android.os.Parcel;
import android.os.Parcelable;

import com.redefine.richtext.util.CollectionUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR on 2018/1/23.
 */

public class RichContent implements Parcelable, Serializable {
    private static final long serialVersionUID = -5935907750951394154L;
    public String text;
    public String summary;
    public List<RichItem> richItemList;


    protected RichContent(Parcel in) {
        text = in.readString();
        summary = in.readString();
        richItemList = in.createTypedArrayList(RichItem.CREATOR);
    }

    public static final Creator<RichContent> CREATOR = new Creator<RichContent>() {
        @Override
        public RichContent createFromParcel(Parcel in) {
            return new RichContent(in);
        }

        @Override
        public RichContent[] newArray(int size) {
            return new RichContent[size];
        }
    };

    public RichContent() {

    }

    public RichContent copyRichContent() {
        RichContent richContent = new RichContent();
        richContent.text = text;
        richContent.summary = summary;
        if (richItemList == null) {
            return richContent;
        }
        richContent.richItemList = new ArrayList<>();
        for (RichItem richItem : richItemList) {
            richContent.richItemList.add(richItem.copyRichItem());
        }
        return richContent;
    }

    public boolean hasArticleItem() {
        boolean hasArticleItem = false;

        if (!CollectionUtil.isEmpty(richItemList)) {
            for (RichItem item : richItemList) {
                if (item.isArticleItem()) {
                    hasArticleItem = true;
                }
            }
        }
        return hasArticleItem;
    }

    public List<RichItem> copyRichItemList() {
        if (richItemList == null) {
            return null;
        }
        List<RichItem> richItems = new ArrayList<>();
        for (RichItem richItem : richItemList) {
            richItems.add(richItem.copyRichItem());
        }
        return richItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(summary);
        dest.writeTypedList(richItemList);
    }
}
