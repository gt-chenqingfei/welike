package com.redefine.im.service;

import com.redefine.im.bean.IMSession;

/**
 * Created by liubin on 2018/2/7.
 */

public interface IMServiceCallback {

    void onSessionCreated(IMSession session, String reqId, int errCode);

}
