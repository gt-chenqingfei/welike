package com.redefine.commonui.share.facebook;


import com.redefine.commonui.share.IBuilder;
import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by gongguan on 2018/3/20.
 */

public class FaceBookBuilder implements IBuilder<FacebookShareModel> {
    private ShareModel mShareModel;

    public FaceBookBuilder(ShareModel shareModel) {
        mShareModel = shareModel;
    }

    @Override
    public FacebookShareModel getPackageShareModel() {
        return null;
    }
}
