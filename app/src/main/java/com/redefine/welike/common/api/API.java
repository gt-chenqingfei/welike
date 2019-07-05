package com.redefine.welike.common.api;

import com.redefine.sunny.ApiWrapper;

/**
 * Created by n.d on 2017/7/12.
 */

public class API {
    private static IAPI iapi;

    public static IAPI me(){
        if (iapi == null) {
            iapi = ApiWrapper.create(IAPI.class);
        }
        return iapi;
    }
}
