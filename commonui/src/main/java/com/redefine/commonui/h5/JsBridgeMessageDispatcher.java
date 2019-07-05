package com.redefine.commonui.h5;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.redefine.commonui.h5.protocol.IJsBridgeDispatcher;
import com.redefine.welike.base.util.UriUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nianguowang on 2018/5/9
 */
public class JsBridgeMessageDispatcher implements IJsBridgeDispatcher {

    private static final String URI_SCHEME = "jsbridge";
    private static final String URI_HOST = "com.redefine.welike";

    private static final String URI_PATH_CLOSE_PAGE = "/closepage";
    private static final String URI_PATH_SHARE = "/share";
    private static final String URI_PATH_START_MISSION = "/startmission";

    private WeLikeJsBridge mJsBridge;
    public JsBridgeMessageDispatcher(WeLikeJsBridge jsBridge) {
        mJsBridge = jsBridge;
    }

    @Override
    public void dispatchMessage(Context context, String message) {
        if(context == null || TextUtils.isEmpty(message) || mJsBridge == null) {
            return;
        }

        Uri uri = Uri.parse(message);
        String scheme = uri.getScheme();
        String host = uri.getHost();
        String path = uri.getPath();
        int port = uri.getPort();//用于给javascript传返回值
        Map<String, String> parameters = UriUtil.splitQueryParametersDecode(uri);

        if(!TextUtils.equals(scheme, URI_SCHEME) || !TextUtils.equals(host, URI_HOST)) {
            return;
        }

        if(TextUtils.equals(path, URI_PATH_CLOSE_PAGE)) {
            mJsBridge.closePage();
        } else if(TextUtils.equals(path, URI_PATH_SHARE)) {
            String title = parameters.get("title");
            String shareUrl = parameters.get("shareUrl");
            mJsBridge.share(title, shareUrl);
        } else if(TextUtils.equals(path, URI_PATH_START_MISSION)) {
            String type = parameters.get("type");
            try {
                Integer typeInt = Integer.valueOf(type);
                mJsBridge.startMission(typeInt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
