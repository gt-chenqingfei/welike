package com.redefine.commonui.share.instagram;

import com.redefine.commonui.share.IBuilder;
import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by gongguan on 2018/3/20.
 */

public class InstagramBuilder implements IBuilder<InstagramShareModel> {
    private ShareModel mShareModel;

    public InstagramBuilder(ShareModel shareModel) {
        mShareModel = shareModel;
    }

    @Override
    public InstagramShareModel getPackageShareModel() {
        return null;
    }
}
