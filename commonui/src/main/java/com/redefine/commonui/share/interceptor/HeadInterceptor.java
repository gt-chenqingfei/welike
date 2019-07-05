package com.redefine.commonui.share.interceptor;

import com.redefine.commonui.share.SharePackageFactory;
import com.redefine.commonui.share.sharemedel.ShareModel;

/**
 * Created by nianguowang on 2018/6/21
 */
public class HeadInterceptor extends AbstractInterceptor {
    @Override
    public void handle(ShareModel shareModel, SharePackageFactory.SharePackage channel) {
        doNext(shareModel, channel);
    }
}
