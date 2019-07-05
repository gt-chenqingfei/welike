package com.redefine.commonui.share;

import android.content.Context;

import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by nianguowang on 2018/8/31
 */
public class EmptyMenuShareManager extends DefaultShareMenuManager {

    public EmptyMenuShareManager(SharePackageFactory.SharePackage sharePackage) {
        super(sharePackage);
    }

    @Override
    public void shareTo(Context context, ShareModel shareModel, CommonListener<String> listener) {
        // do nothing
    }
}
