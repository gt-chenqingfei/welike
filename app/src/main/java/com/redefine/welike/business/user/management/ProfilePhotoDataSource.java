package com.redefine.welike.business.user.management;

import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;

import java.util.List;

/**
 * Created by nianguowang on 2018/10/19
 */
public class ProfilePhotoDataSource {

    private List<AttachmentBase> mData;

    private ProfilePhotoDataSource() {}

    private static class ProfilePhotoDataSourceHolder {
        private static final ProfilePhotoDataSource sInstance = new ProfilePhotoDataSource();
    }

    public static ProfilePhotoDataSource getInstance() {
        return ProfilePhotoDataSourceHolder.sInstance;
    }

    public void setData(List<AttachmentBase> list) {
        mData = list;
    }

    public List<AttachmentBase> getData() {
        return mData;
    }
}

