package com.redefine.multimedia.photoselector.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.redefine.multimedia.photoselector.entity.MimeType;

import java.util.EnumSet;

public class ImagePickConfig implements Parcelable {

    public static final int MAX_IMAGE_SELECT_COUNT = 9;
    public static final int MAX_VIDEO_SELECT_COUNT = 1;
    public static final int IMAGE_PICK_SPAN_COUNT = 4;

    public boolean isCutPhoto;
    public int maxImageSelectable;
    public int maxVideoSelectable;
    public boolean showSingleMediaType;
    public boolean mediaTypeExclusive;
    public boolean capture;
    public int spanCount;
    public boolean countable;
    public float aspectRatioX;
    public float aspectRatioY;
    public long minDuration;
    public long maxDuration;
    public int maxPicFileSize;
    public int maxVideoFileSize;

    public EnumSet<MimeType> mimeTypeSet;


    public ImagePickConfig() {
        isCutPhoto = false;
        maxImageSelectable = MAX_IMAGE_SELECT_COUNT;
        maxVideoSelectable = MAX_VIDEO_SELECT_COUNT;
        showSingleMediaType = false;
        mediaTypeExclusive = true;
        capture = true;
        spanCount = IMAGE_PICK_SPAN_COUNT;
        countable = true;
        aspectRatioX = 1f;
        aspectRatioY = 1f;
        mimeTypeSet = MimeType.ofAll();
        minDuration = 2 * 1000;
        maxDuration = Long.MAX_VALUE;
        maxPicFileSize = 10 * 1024 * 1024;
        maxVideoFileSize = 100 * 1024 * 1024;
    }


    protected ImagePickConfig(Parcel in) {
        isCutPhoto = in.readByte() != 0;
        maxImageSelectable = in.readInt();
        maxVideoSelectable = in.readInt();
        showSingleMediaType = in.readByte() != 0;
        mediaTypeExclusive = in.readByte() != 0;
        capture = in.readByte() != 0;
        spanCount = in.readInt();
        countable = in.readByte() != 0;
        aspectRatioX = in.readFloat();
        aspectRatioY = in.readFloat();
        mimeTypeSet = (EnumSet<MimeType>) in.readSerializable();
        minDuration = in.readLong();
        maxDuration = in.readLong();
        maxPicFileSize = in.readInt();
        maxVideoFileSize = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isCutPhoto ? 1 : 0));
        dest.writeInt(maxImageSelectable);
        dest.writeInt(maxVideoSelectable);
        dest.writeByte((byte) (showSingleMediaType ? 1 : 0));
        dest.writeByte((byte) (mediaTypeExclusive ? 1 : 0));
        dest.writeByte((byte) (capture ? 1 : 0));
        dest.writeInt(spanCount);
        dest.writeInt((byte) (countable ? 1 : 0));
        dest.writeFloat(aspectRatioX);
        dest.writeFloat(aspectRatioY);
        dest.writeSerializable(mimeTypeSet);
        dest.writeLong(minDuration);
        dest.writeLong(maxDuration);
        dest.writeInt(maxPicFileSize);
        dest.writeInt(maxVideoFileSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImagePickConfig> CREATOR = new Creator<ImagePickConfig>() {
        @Override
        public ImagePickConfig createFromParcel(Parcel in) {
            return new ImagePickConfig(in);
        }

        @Override
        public ImagePickConfig[] newArray(int size) {
            return new ImagePickConfig[size];
        }
    };

    public static ImagePickConfig get() {
        return new ImagePickConfig();
    }

    public ImagePickConfig setIsCutPhoto(boolean b) {
        isCutPhoto = b;
        return this;
    }

    public boolean onlyShowImages() {
        return showSingleMediaType && MimeType.ofImage().containsAll(mimeTypeSet);
    }

    public boolean onlyShowVideos() {
        return showSingleMediaType && MimeType.ofVideo().containsAll(mimeTypeSet);
    }

    public ImagePickConfig setMaxImageSelectable(int maxImageSelectable) {
        this.maxImageSelectable = maxImageSelectable;
        return this;
    }

    public ImagePickConfig setMaxVideoSelectable(int maxVideoSelectable) {
        this.maxVideoSelectable = maxVideoSelectable;
        return this;
    }

    public ImagePickConfig setShowSingleMediaType(boolean showSingleMediaType) {
        this.showSingleMediaType = showSingleMediaType;
        return this;
    }

    public ImagePickConfig setMediaTypeExclusive(boolean mediaTypeExclusive) {
        this.mediaTypeExclusive = mediaTypeExclusive;
        return this;
    }

    public ImagePickConfig setCapture(boolean capture) {
        this.capture = capture;
        return this;
    }

    public ImagePickConfig setCountable(boolean countable) {
        this.countable = countable;
        return this;
    }

    public ImagePickConfig setSpanCount(int spanCount) {
        this.spanCount = spanCount;
        return this;
    }

    public ImagePickConfig setMimeTypeSet(EnumSet<MimeType> mimeTypeSet) {
        this.mimeTypeSet = mimeTypeSet;
        return this;
    }

    public ImagePickConfig setAspectRatioX(float aspectRatioX) {
        this.aspectRatioX = aspectRatioX;
        return this;
    }

    public ImagePickConfig setAspectRatioY(float aspectRatioY) {
        this.aspectRatioY = aspectRatioY;
        return this;
    }
}
