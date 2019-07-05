package com.redefine.im.management;

import com.redefine.im.bean.IMSession;
import com.redefine.im.bean.IMMessageBase;

import java.util.List;

/**
 * Created by liubin on 2018/2/6.
 */

public interface IIMChatProvider {

    void startService(String sid, IMChatProvider.IMChatProviderCallback listener);

    void closeService();

    void cancelAllSendingMessages(String sid);

    void refreshMessagesInSession(String sid);

    void hisMessagesInSession(String sid);

    //void removeMessage(IMMessageBase message);

    void sendMessage(final IMMessageBase message, IMSession session);

    void sendMessage(final IMMessageBase message, IMSession session, String from);

}
