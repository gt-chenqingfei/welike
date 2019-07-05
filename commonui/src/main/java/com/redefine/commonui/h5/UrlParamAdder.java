package com.redefine.commonui.h5;

import android.net.Uri;

import com.redefine.foundation.utils.CollectionUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by nianguowang on 2018/10/23
 */
public class UrlParamAdder {

    private Map<String, String> mToAddParams;

    public UrlParamAdder(Map<String, String> params) {
        this.mToAddParams = params;
    }

    public String addParam(String url) {
        if (mToAddParams == null || mToAddParams.size() == 0) {
            return url;
        }
        Uri parse = Uri.parse(url);

        Map<String, String> originalUrlParams = parseOriginalUrlParams(parse);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(parse.getScheme())
                .authority(parse.getAuthority())
                .path(parse.getPath())
                .encodedFragment(parse.getEncodedFragment());
        Set<Map.Entry<String, String>> originalUrlParamsEntries = originalUrlParams.entrySet();
        for (Map.Entry<String, String> entry : originalUrlParamsEntries) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        Set<Map.Entry<String, String>> toAddParamsEntries = mToAddParams.entrySet();
        for (Map.Entry<String, String> entry : toAddParamsEntries) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build().toString();
    }

    private Map<String, String> parseOriginalUrlParams(Uri uri) {
        Map<String, String> originalUrlParams = new LinkedHashMap<>();
        Set<String> parameterNames = uri.getQueryParameterNames();
        if (CollectionUtil.isEmpty(parameterNames)) {
            return originalUrlParams;
        }
        for (String parameterName : parameterNames) {
            originalUrlParams.put(parameterName, uri.getQueryParameter(parameterName));
        }
        return originalUrlParams;
    }
}
