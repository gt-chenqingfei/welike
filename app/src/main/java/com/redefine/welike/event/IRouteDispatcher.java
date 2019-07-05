package com.redefine.welike.event;

import android.content.Intent;

/**
 * Created by liwenbo on 2018/3/16.
 */

public interface IRouteDispatcher {

    void handleRouteMessage(Intent intent);


    boolean enableTo(Intent intent);


}
