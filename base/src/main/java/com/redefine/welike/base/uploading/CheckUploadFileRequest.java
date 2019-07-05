package com.redefine.welike.base.uploading;

import android.content.Context;

import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.welike.base.BuildConfig;
import com.redefine.welike.base.URLCenter;
import com.redefine.welike.base.request.BaseRequest;

/**
 * Created by liubin on 2018/3/19.
 */

public class CheckUploadFileRequest extends BaseRequest {
    private static final String CHECK_UPLOAD_KEY_OBJ_KEY = "key";

    public CheckUploadFileRequest(String objectKey, Context context) {
        super(BaseHttpReq.REQUEST_METHOD_GET, context);

        setHost(URLCenter.getHost() + "file/fileinfo", true);
        setParam(CHECK_UPLOAD_KEY_OBJ_KEY, objectKey);
    }

}
