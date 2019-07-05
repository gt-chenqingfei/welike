package com.redefine.im.management;

import com.redefine.im.bean.IMMessageBase;
import com.redefine.im.cache.IMMessageCache;
import com.redefine.welike.base.GlobalConfig;

import java.util.Collections;
import java.util.List;

/**
 * Created by liubin on 2018/2/5.
 */

class IMSessionHisProvider {
    private String sid;
    private IMMessageBase cursorMessage;

    public interface IMSessionHisProviderRefreshMessagesCallback {

        void onRefreshMessagesInSession(List<IMMessageBase> messages);

    }

    public interface IMSessionHisProviderHisMessagesCallback {

        void onHisMessagesInSession(List<IMMessageBase> messages);

    }

    void setSessionId(String sid) {
        this.sid = sid;
    }

    void refreshMessages(final IMSessionHisProviderRefreshMessagesCallback callback) {
        cursorMessage = null;
        IMMessageCache.getInstance().listNewMessagesInSession(sid, GlobalConfig.IM_MESSAGES_ONE_PAGE, new IMMessageCache.IMMessageCacheMessagesInSessionCallback() {

            @Override
            public void onMessagesInSession(final List<IMMessageBase> messages) {
                if (messages != null && messages.size() > 0) {
                    cursorMessage = messages.get(messages.size() - 1);
                    Collections.sort(messages);
                }
                if (callback != null) {
                    callback.onRefreshMessagesInSession(messages);
                }

            }

        });
    }

    void hisMessages(final IMSessionHisProviderHisMessagesCallback callback) {
        if (cursorMessage != null) {
            IMMessageCache.getInstance().listHisMessagesInSession(sid, cursorMessage.getMid(), GlobalConfig.IM_MESSAGES_ONE_PAGE, new IMMessageCache.IMMessageCacheMessagesInSessionCallback() {

                @Override
                public void onMessagesInSession(final List<IMMessageBase> messages) {
                    if (messages != null && messages.size() > 0) {
                        cursorMessage = messages.get(messages.size() - 1);
                        Collections.sort(messages);
                    } else {
                        cursorMessage = null;
                    }
                    if (callback != null) {
                        callback.onHisMessagesInSession(messages);
                    }
                }
            });
        } else {
            if (callback != null) {
                callback.onHisMessagesInSession(null);
            }
        }
    }

}
