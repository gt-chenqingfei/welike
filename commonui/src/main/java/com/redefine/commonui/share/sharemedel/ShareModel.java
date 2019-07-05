package com.redefine.commonui.share.sharemedel;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gongguan on 2018/3/20.
 */

public class ShareModel implements Parcelable {

    public static final int SHARE_MODEL_TYPE_POST = 1;
    public static final int SHARE_MODEL_TYPE_APP = 2;
    public static final int SHARE_MODEL_TYPE_TASK = 3;
    public static final int SHARE_MODEL_TYPE_PROFILE = 4;
    public static final int SHARE_MODEL_TYPE_TOPIC = 5;
    public static final int SHARE_MODEL_TYPE_LBS = 6;
    public static final int SHARE_MODEL_TYPE_VIDEO = 7;
    public static final int SHARE_MODEL_TYPE_FILE = 8;

    private String title;
    private String summary;
    private String h5Link;
    private String content;
    private String imagePath;
    private String imageUrl;
    private String tail;
    private Bitmap bitmap;
    private int shareModelType;
    private String videoUrl;
    private String videoPath;
    private String filePath;

    public ShareModel() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setH5Link(String h5Link) {
        this.h5Link = h5Link;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setShareModelType(int shareModelType) {
        this.shareModelType = shareModelType;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getH5Link() {
        return h5Link;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getImageUrl() {
        return imageUrl;
//        return "https://trello-avatars.s3.amazonaws.com/7e31d93b22142cafe954c8572d642628/30.png";
    }

    public String getTail() {
        return tail;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getShareModelType() {
        return shareModelType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.h5Link);
        dest.writeString(this.content);
        dest.writeString(this.imagePath);
        dest.writeString(this.imageUrl);
        dest.writeString(this.tail);
        dest.writeParcelable(this.bitmap, flags);
        dest.writeInt(this.shareModelType);
        dest.writeString(this.videoUrl);
        dest.writeString(this.videoPath);
        dest.writeString(this.filePath);
    }

    protected ShareModel(Parcel in) {
        this.title = in.readString();
        this.summary = in.readString();
        this.h5Link = in.readString();
        this.content = in.readString();
        this.imagePath = in.readString();
        this.imageUrl = in.readString();
        this.tail = in.readString();
        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.shareModelType = in.readInt();
        this.videoUrl = in.readString();
        this.videoPath = in.readString();
        this.filePath = in.readString();
    }

    public static final Creator<ShareModel> CREATOR = new Creator<ShareModel>() {
        @Override
        public ShareModel createFromParcel(Parcel source) {
            return new ShareModel(source);
        }

        @Override
        public ShareModel[] newArray(int size) {
            return new ShareModel[size];
        }
    };
}
