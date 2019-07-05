package com.redefine.im.management;

import com.redefine.im.bean.IMSession;

/**
 * Created by liubin on 2018/2/7.
 */

public interface IMSessionGotoCallback {

    void onGotoSession(IMSession session, int errCode);

}
