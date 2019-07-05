package com.redefine.commonui.share.system;

import com.redefine.commonui.share.IBuilder;
import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by gongguan on 2018/3/20.
 */

public class SystemBuilder implements IBuilder<SystemShareModel> {
    private ShareModel mShareModel;

    public SystemBuilder(ShareModel shareModel) {
        mShareModel = shareModel;
    }

    @Override
    public SystemShareModel getPackageShareModel() {
        return null;
    }
}
