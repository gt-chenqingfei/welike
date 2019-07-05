package com.redefine.commonui.share.whatsapp;

import com.redefine.commonui.share.IBuilder;
import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by gongguan on 2018/3/20.
 */

public class WhatsAppBuilder implements IBuilder<WhatsAppShareModel> {
    private ShareModel mShareModel;

    public WhatsAppBuilder(ShareModel shareModel) {
        mShareModel = shareModel;
    }

    @Override
    public WhatsAppShareModel getPackageShareModel() {
        return null;
    }
}
