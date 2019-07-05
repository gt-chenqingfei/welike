package com.redefine.commonui.fresco.oss;

/**
 * Created by liwenbo on 2018/2/24.
 */

public interface IUrlFilter {

    String filter(String url);

    boolean canFilter(String url);

    String filter(String url, int width, int height);
}
