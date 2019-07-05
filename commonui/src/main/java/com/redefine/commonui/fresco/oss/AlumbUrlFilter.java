package com.redefine.commonui.fresco.oss;

/**
 * Created by liwenbo on 2018/2/24.
 */

public class AlumbUrlFilter extends BaseUrlFilter {

    @Override
    protected String doFilter(String url, int width, int height) {
        return url;
    }

    @Override
    public String doFilter(String url) {
        return url;
    }
}
