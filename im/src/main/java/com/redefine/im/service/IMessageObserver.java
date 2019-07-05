package com.redefine.im.service;

import android.content.Context;

import com.redefine.im.bean.IMMessageBase;

/**
 * Created by liubin on 2018/2/5.
 */

public interface IMessageObserver {

    void prepare(Context context);
    void connect(String token, String uid);
    void disconnect();
    void setListener(MessageObserverListener listener);
    void sendMessage(IMMessageBase message);
    void createSingleSession(String uid, final String reqId, final IMServiceCallback callback);
    void createCustomerSession(final String reqId, final IMServiceCallback callback);

}
