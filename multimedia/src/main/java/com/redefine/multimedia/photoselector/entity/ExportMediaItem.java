package com.redefine.multimedia.photoselector.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.redefine.multimedia.photoselector.util.PathUtils;

import java.util.ArrayList;
import java.util.List;

public class ExportMediaItem implements Parcelable {

    public final String mimeType;
    public final String filePath;

    public ExportMediaItem(String mimeType, String filePath) {
        this.mimeType = mimeType;
        this.filePath = filePath;
    }

    protected ExportMediaItem(Parcel in) {
        mimeType = in.readString();
        filePath = in.readString();
    }

    public static final Creator<ExportMediaItem> CREATOR = new Creator<ExportMediaItem>() {
        @Override
        public ExportMediaItem createFromParcel(Parcel in) {
            return new ExportMediaItem(in);
        }

        @Override
        public ExportMediaItem[] newArray(int size) {
            return new ExportMediaItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mimeType);
        dest.writeString(filePath);
    }

    public static List<ExportMediaItem> convert(Context context, ArrayList<Item> items) {
        List<ExportMediaItem> result = new ArrayList<>();
        for (Item item : items) {
            result.add(new ExportMediaItem(item.mimeType, PathUtils.getPath(context, item.getContentUri())));
        }
        return result;
    }
}
