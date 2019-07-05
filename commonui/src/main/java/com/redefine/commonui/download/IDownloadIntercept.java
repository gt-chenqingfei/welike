package com.redefine.commonui.download;

import com.redefine.commonui.share.CommonListener;

public interface IDownloadIntercept {

    void intercept(String url, String filePath, CommonListener<String> commonListener);
}
