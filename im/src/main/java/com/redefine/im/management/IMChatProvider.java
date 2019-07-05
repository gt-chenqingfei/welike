package com.redefine.im.management;

import android.text.TextUtils;

import com.redefine.foundation.framework.SingleListenerManagerBase;
import com.redefine.im.bean.IMSession;
import com.redefine.im.bean.IMMessageBase;
import com.redefine.im.service.IMService;
import com.redefine.welike.base.ErrorCode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by liubin on 2018/2/6.
 */

public class IMChatProvider extends SingleListenerManagerBase implements IIMChatProvider, IMMessageSender.IMMessageSenderCallback {
    private String sid;
    private IMSessionHisProvider sessionHisProvider = new IMSessionHisProvider();
    private IMMessageSender sender;
    private IMManager imManager;

    public interface IMChatProviderCallback {

        void onMessageSendResult(String oldMid, IMMessageBase message, int errCode);

        void onMessageSendAttachmentProcess(String mid, float process);

        void onReceivedSessionMessages(String sid, List<IMMessageBase> messages);

        void onRefreshSessionMessages(String sid, List<IMMessageBase> messages);

        void onHisSessionMessages(String sid, List<IMMessageBase> messages);

    }

    IMChatProvider(IMManager imManager) {
        this.imManager = imManager;
    }

    public void init(IMService imService) {
        sender = new IMMessageSender(imService);
        sender.setListener(this);
    }

    void close() {
        closeService();
        sender = null;
    }

    @Override
    public void cancelAllSendingMessages(String sid) {
        if (sender != null) {
            sender.cancelAllSendingMessages(sid);
        }
    }

    /**
     *  WTF?
     */
    @Override
    public void startService(String sid, IMChatProviderCallback listener) {
        if (listener != null) {
            this.sid = sid;
        } else {
            this.sid = null;
        }
        super.setListener(listener);
    }

    @Override
    public void closeService() {
        sid = null;
        super.setListener(null);
    }

    @Override
    public void refreshMessagesInSession(final String sid) {
        sessionHisProvider.setSessionId(sid);
        sessionHisProvider.refreshMessages(new IMSessionHisProvider.IMSessionHisProviderRefreshMessagesCallback() {

            @Override
            public void onRefreshMessagesInSession(final List<IMMessageBase> messages) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                    @Override
                    public void run() {
                        IMChatProviderCallback listener = (IMChatProviderCallback) getListener();
                        if (listener != null) {
                            listener.onRefreshSessionMessages(sid, messages);
                        }
                    }

                });
            }

        });
    }

    @Override
    public void hisMessagesInSession(final String sid) {
        sessionHisProvider.setSessionId(sid);
        sessionHisProvider.hisMessages(new IMSessionHisProvider.IMSessionHisProviderHisMessagesCallback() {

            @Override
            public void onHisMessagesInSession(final List<IMMessageBase> messages) {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        IMChatProviderCallback listener = (IMChatProviderCallback) getListener();
                        if (listener != null) {
                            listener.onHisSessionMessages(sid, messages);
                        }
                    }

                });
            }

        });
    }

    //    @Override
//    public void removeMessage(IMMessageBase message) {
//        IMMessageCache.getInstance().deleteMessage(message);
//    }
    @Override
    public void sendMessage(final IMMessageBase message, IMSession session, String form) {
        String id = session.getSingleUid() == null ? "" : session.getSingleUid();
        if (!TextUtils.isEmpty(form) && UniqueFilter.INSTANCE.check(id)) {
            message.form = form;
            UniqueFilter.INSTANCE.record(id);
        }
        sendMessage(message, session);
    }

    @Override
    public void sendMessage(final IMMessageBase message, IMSession session) {
        if (sender != null) {
            final int errCode = sender.sendMessage(message, session);
            if (errCode != ErrorCode.ERROR_SUCCESS) {
                final String oldMid = message.getMid();
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        IMChatProviderCallback listener = (IMChatProviderCallback) getListener();
                        if (listener != null) {
                            listener.onMessageSendResult(oldMid, message, errCode);
                        }
                    }
                });
            }
        } else {
            final String oldMid = message.getMid();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    IMChatProviderCallback listener = (IMChatProviderCallback) getListener();
                    if (listener != null) {
                        listener.onMessageSendResult(oldMid, message, ErrorCode.ERROR_IM_SERVICE_MISS);
                    }
                }
            });
        }
    }

    @Override
    public void onUploadMessageAttachmentProcess(IMMessageBase message, final float process) {
        final String mid = message.getMid();
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                IMChatProviderCallback listener = (IMChatProviderCallback) getListener();
                if (listener != null) {
                    listener.onMessageSendAttachmentProcess(mid, process);
                }
            }

        });
    }

    @Override
    public void onUploadMessageAttachmentFailed(IMMessageBase message) {
        callMessageSendResult(message.getMid(), message, ErrorCode.ERROR_IM_SEND_MSG_RESOURCE_FAILED);
    }

    @Override
    public void onSendMessageSessionUpdated(IMMessageBase message, String sid) {
        imManager.handleSendMessageSessionUpdated(sid);
    }

    String startSid() {
        return sid;
    }

    void handleReceivedMessages(List<IMMessageBase> messages) {
        callReceivedMessages(messages);
    }

    void handleMessageSendResult(String oldMid, IMMessageBase message, int errCode) {
        if (sender != null) {
            sender.handleMessageSendResult(oldMid, message, errCode);
        }
        callMessageSendResult(oldMid, message, errCode);
    }

    private void callReceivedMessages(final List<IMMessageBase> messages) {
        if (!TextUtils.isEmpty(sid)) {
            final List<IMMessageBase> sidMessages = filterMessages(sid, messages);
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    IMChatProviderCallback listener = (IMChatProviderCallback) getListener();
                    if (listener != null) {
                        listener.onReceivedSessionMessages(sid, sidMessages);
                    }
                }

            });
        }
    }

    private void callMessageSendResult(final String oldMid, final IMMessageBase message, final int errCode) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                IMChatProviderCallback listener = (IMChatProviderCallback) getListener();
                if (listener != null) {
                    listener.onMessageSendResult(oldMid, message, errCode);
                }
            }

        });
    }

    private static List<IMMessageBase> filterMessages(String sid, List<IMMessageBase> allMessages) {
        List<IMMessageBase> messages = new ArrayList<>();
        for (IMMessageBase message : allMessages) {
            if (TextUtils.equals(message.getSid(), sid)) {
                messages.add(message);
            }
        }
        return messages;
    }

}
