package com.redefine.im.service;

import com.redefine.im.bean.IMMessageBase;

import java.util.List;

/**
 * Created by liubin on 2018/2/5.
 */

public interface MessageObserverListener {

    void onAuthSuccessed();
    void onTokenInvalid();
    void onReceiveMessages(List<IMMessageBase> messages);
    void onMessageSendResult(String oldMid, IMMessageBase message, int errCode);
    void onMessageNotificatinsCountChanged();

}
