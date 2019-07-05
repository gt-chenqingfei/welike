package com.redefine.im.service;

import com.redefine.im.bean.IMMessageBase;

import java.util.List;

/**
 * Created by liubin on 2018/2/2.
 */

public interface IMServiceListener {

    void onOfflineMessages(List<IMMessageBase> messages, boolean last);
    void onReceiveMessages(List<IMMessageBase> messages);
    void onMessageSendResult(String oldMid, IMMessageBase message, int errCode);
    void onTokenInvalid();
    void onMessageNotificationCountChanged();

}
