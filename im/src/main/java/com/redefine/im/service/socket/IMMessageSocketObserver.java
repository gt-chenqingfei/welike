package com.redefine.im.service.socket;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.im.bean.IMMessageBase;
import com.redefine.im.bean.IMPicMessage;
import com.redefine.im.bean.IMSession;
import com.redefine.im.bean.IMSystemMessage;
import com.redefine.im.bean.IMTextMessage;
import com.redefine.im.cache.ImAccountSettingCache;
import com.redefine.im.parser.IMMessageParser;
import com.redefine.im.service.IMServiceCallback;
import com.redefine.im.service.IMessageObserver;
import com.redefine.im.service.MessageObserverListener;
import com.redefine.im.service.request.ApplySessionCustomerRequest;
import com.redefine.im.service.request.CreateSessionRequest;
import com.redefine.im.service.socket.protocol.BibiProtoApplication;
import com.redefine.im.service.socket.protocol.ImPacketType;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IMMessageSocketObserver implements IMessageObserver, ConnectionManagerCallback {
    private Context context;
    private MessageObserverListener listener;
    private ConnectionManager connectionManager;
    private CreateSessionRequest currentCreateSessionRequest;
    private ApplySessionCustomerRequest currentApplySessionCustomerRequest;
    private final Map<String, IMMessageBase> sentMessagesPool = new ConcurrentHashMap<>();

    @Override
    public void prepare(Context context) {
        this.context = context;
        connectionManager = new ConnectionManager(context);
    }

    @Override
    public void connect(String token, String uid) {
        connectionManager.register(this);
        connectionManager.connect(token, uid);
    }

    @Override
    public void disconnect() {
        connectionManager.disconnect();
        connectionManager.unregister(this);
        sentMessagesPool.clear();
    }

    @Override
    public void setListener(MessageObserverListener listener) {
        this.listener = listener;
    }

    @Override
    public void sendMessage(IMMessageBase message) {
        SocketData socketData = new SocketData();
        BibiProtoApplication.MessageClassified msgClassified = BibiProtoApplication.MessageClassified.P2P;
        if (message.getSessionType() == IMSession.SESSION_TYPE_SINGLE) {
            msgClassified = BibiProtoApplication.MessageClassified.P2P;
        }
        if (message instanceof IMTextMessage) {
            socketData.setType(ImPacketType.PROTOCOL_MSG_TEXT);
            BibiProtoApplication.MessageHeader header;
            BibiProtoApplication.MessageHeader.Builder headerBuilder = BibiProtoApplication.MessageHeader.newBuilder().setMessageId(message.getMid())
                    .setClassified(msgClassified).setFromUid(message.getSenderUid()).setRemoteUid(message.getRemoteUid()).setGroupName(message.getSenderNickName() == null ? "" : message.getSenderNickName())
                    .setSenderName(message.getSenderNickName()).setCreatetime(message.getTime()).setType(BibiProtoApplication.MessageHeader.MessageType.Text)
                    .setPersistent(true);
            if (!TextUtils.isEmpty(message.form)) {
                headerBuilder.addProperties(BibiProtoApplication.MessageHeader.Property.newBuilder().setKey("noticeType").setValue("shake"));
            }

            if (!TextUtils.isEmpty(message.getSessionHead()) && !TextUtils.isEmpty(message.getSenderHead())) {
                header = headerBuilder.setGroupUrl(message.getSenderHead()).setSenderUrl(message.getSenderHead()).build();
            } else {
                header = headerBuilder.build();
            }
            BibiProtoApplication.TextMessage textMessage = BibiProtoApplication.TextMessage.newBuilder().setText(((IMTextMessage) message).getText()).setHeader(header).build();
            socketData.setData(textMessage.toByteArray());
        } else if (message instanceof IMPicMessage) {
            socketData.setType(ImPacketType.PROTOCOL_MSG_PIC);
            BibiProtoApplication.MessageHeader header;
            BibiProtoApplication.MessageHeader.Builder headerBuilder = BibiProtoApplication.MessageHeader.newBuilder().setMessageId(message.getMid())
                    .setClassified(msgClassified).setFromUid(message.getSenderUid()).setRemoteUid(message.getRemoteUid()).setGroupName(message.getSenderNickName())
                    .setSenderName(message.getSenderNickName()).setCreatetime(message.getTime()).setType(BibiProtoApplication.MessageHeader.MessageType.Pic)
                    .setPersistent(true);
            if (!TextUtils.isEmpty(message.form)) {
                headerBuilder.addProperties(BibiProtoApplication.MessageHeader.Property.newBuilder().setKey("noticeType").setValue("shake"));
            }
            if (!TextUtils.isEmpty(message.getSessionHead()) && !TextUtils.isEmpty(message.getSenderHead())) {
                header = headerBuilder.setGroupUrl(message.getSenderHead()).setSenderUrl(message.getSenderHead()).build();
            } else {
                header = headerBuilder.build();
            }
            BibiProtoApplication.PicMessage picMessage = BibiProtoApplication.PicMessage.newBuilder().setPicUri(((IMPicMessage) message).getPicLargeUrl())
                    .setCoverUri(((IMPicMessage) message).getPicUrl()).setHeader(header).build();
            socketData.setData(picMessage.toByteArray());
        }
        sentMessagesPool.put(message.getMid(), message);
        connectionManager.send(socketData);
    }

    @Override
    public void createSingleSession(final String uid, final String reqId, final IMServiceCallback callback) {
        if (currentCreateSessionRequest != null) {
            currentCreateSessionRequest.cancel();
        }
        currentCreateSessionRequest = new CreateSessionRequest(uid, context);
        try {
            currentCreateSessionRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    currentCreateSessionRequest = null;
                    if (callback != null) {
                        callback.onSessionCreated(null, reqId, errCode);
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    currentCreateSessionRequest = null;
                    IMSession session = IMMessageParser.parseSession(result);
                    if (callback != null) {
                        callback.onSessionCreated(session, reqId, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            currentCreateSessionRequest = null;
            if (callback != null) {
                callback.onSessionCreated(null, reqId, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void createCustomerSession(final String reqId, final IMServiceCallback callback) {
        if (currentApplySessionCustomerRequest != null) {
            currentApplySessionCustomerRequest.cancel();
        }
        currentApplySessionCustomerRequest = new ApplySessionCustomerRequest(context);
        try {
            currentApplySessionCustomerRequest.req(new RequestCallback() {

                @Override
                public void onError(BaseRequest request, int errCode) {
                    if (callback != null) {
                        callback.onSessionCreated(null, reqId, errCode);
                    }
                }

                @Override
                public void onSuccess(BaseRequest request, JSONObject result) throws Exception {
                    currentApplySessionCustomerRequest = null;
                    IMSession session = IMMessageParser.parseSession(result);
                    if (callback != null) {
                        callback.onSessionCreated(session, reqId, ErrorCode.ERROR_SUCCESS);
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            currentApplySessionCustomerRequest = null;
            if (callback != null) {
                callback.onSessionCreated(null, reqId, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    @Override
    public void onConnectionChannelReady(String channelId) {
        if (listener != null) {
            listener.onAuthSuccessed();
        }
    }

    @Override
    public void onConnectionTokenInvalid() {
        if (listener != null) {
            listener.onTokenInvalid();
        }
    }

    @Override
    public void onConnectionChannelReceived(String channelId, List<SocketData> socketDataList) {
        if (socketDataList != null && socketDataList.size() > 0) {
            boolean needMessagesBox = false;
            boolean needSync = false;
            long lv = -1;
            List<IMMessageBase> messages = new ArrayList<>();
            for (int i = 0; i < socketDataList.size(); i++) {
                SocketData socketData = socketDataList.get(i);
                if (socketData.getType() == ImPacketType.PROTOCOL_NEW_MSG_NOTIFY) {
                    BibiProtoApplication.NewMessageNotify newMessageNotify = null;
                    try {
                        newMessageNotify = BibiProtoApplication.NewMessageNotify.parseFrom(socketData.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (newMessageNotify != null) {
                        List<BibiProtoApplication.MessageClassified> list = newMessageNotify.getClassifiedList();
                        if (list != null && list.size() > 0) {
                            for (int ii = 0; ii < list.size(); ii++) {
                                BibiProtoApplication.MessageClassified cc = list.get(ii);
                                if (cc == BibiProtoApplication.MessageClassified.P2P) {
                                    needSync = true;
                                } else if (cc == BibiProtoApplication.MessageClassified.All) {
                                    needSync = true;
                                    needMessagesBox = true;
                                } else if (cc == BibiProtoApplication.MessageClassified.NEWS) {
                                    needMessagesBox = true;
                                }
                            }
                        }
                    } else {
                        needSync = true;
                    }
                } else if (socketData.getType() == ImPacketType.PROTOCOL_SYNC_ACK) {
                    BibiProtoApplication.SyncDataPacket syncDataPacket = null;
                    try {
                        syncDataPacket = BibiProtoApplication.SyncDataPacket.parseFrom(socketData.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (syncDataPacket != null) {
                        List<BibiProtoApplication.SyncDataPacket.SyncMark> syncMarkList = syncDataPacket.getSyncMarksList();
                        if (syncMarkList != null && syncMarkList.size() > 0) {
                            for (int j = 0; j < syncMarkList.size(); j++) {
                                BibiProtoApplication.SyncDataPacket.SyncMark syncMark = syncMarkList.get(j);
                                long l = syncMark.getLastVersion();
                                if (lv < l) {
                                    lv = l;
                                }
                            }
                        }
                        List<BibiProtoApplication.SyncDataPacket.DataEntry> dataEntries = syncDataPacket.getDataList();
                        if (dataEntries != null && dataEntries.size() > 0) {
                            for (BibiProtoApplication.SyncDataPacket.DataEntry dataEntry : dataEntries) {
                                IMMessageBase message = convertReceivedMessageFromPB(dataEntry);
                                if (message != null) {
                                    messages.add(message);
                                }
                            }
                        }

                        if (syncDataPacket.getHasMore()) {
                            needSync = true;
                        }
                    }
                }
            }

            if (lv != -1) {
                fin(lv);
                ImAccountSettingCache.getInstance().setImMessageStamp(String.valueOf(lv));
            }

            if (messages.size() > 0) {
                if (listener != null) {
                    listener.onReceiveMessages(messages);
                }
            }

            if (needSync) {
                Account account = AccountManager.getInstance().getAccount();
                if (account != null) {
                    List<BibiProtoApplication.MessageClassified> classifieds = new ArrayList<>();
                    classifieds.add(BibiProtoApplication.MessageClassified.All);
                    BibiProtoApplication.SyncPacket header = BibiProtoApplication.SyncPacket.newBuilder().addAllClassified(classifieds).setFromUid(account.getUid()).setSeqId(1L).build();
                    SocketData socketData = new SocketData();
                    socketData.setType(ImPacketType.PROTOCOL_SYNC);
                    socketData.setData(header.toByteArray());
                    connectionManager.send(socketData);
                }
            }

            if (needMessagesBox) {
                if (listener != null) {
                    listener.onMessageNotificatinsCountChanged();
                }
            }
        }
    }

    @Override
    public void onConnectionMessagesSentResult(String channelId, List<SocketData> socketDataList) {
        for (int i = 0; i < socketDataList.size(); i++) {
            SocketData socketData = socketDataList.get(i);
            if (socketData.getType() == ImPacketType.PROTOCOL_MSG_ACK) {
                BibiProtoApplication.MessageArrivalAck msgArrAck = null;
                try {
                    msgArrAck = BibiProtoApplication.MessageArrivalAck.parseFrom(socketData.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msgArrAck != null) {
                    BibiProtoApplication.MessageHeader header = msgArrAck.getHeader();
                    if (header != null) {
                        String oldMid = header.getMessageId();
                        if (!TextUtils.isEmpty(oldMid)) {
                            IMMessageBase msg = sentMessagesPool.remove(oldMid);
                            if (msg != null) {
                                if (msgArrAck.getCode() == 11200) {
                                    IMMessageBase newMessage = msg.copy();
                                    newMessage.setStatus(IMMessageBase.MESSAGE_STATUS_SENT);
                                    newMessage.setGreet(header.getIsGreet());
                                    if (listener != null) {
                                        listener.onMessageSendResult(oldMid, newMessage, ErrorCode.ERROR_SUCCESS);
                                    }
                                } else {
                                    if (listener != null) {
                                        listener.onMessageSendResult(oldMid, msg, ErrorCode.ERROR_IM_SEND_MSG_REFUSE);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onConnectionMessageSentFailed(String channelId, SocketData socketData, int errCode) {
        if (socketData != null) {
            if (socketData.getType() == ImPacketType.PROTOCOL_MSG_TEXT) {
                BibiProtoApplication.TextMessage textMessage = null;
                try {
                    textMessage = BibiProtoApplication.TextMessage.parseFrom(socketData.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (textMessage != null) {
                    BibiProtoApplication.MessageHeader header = textMessage.getHeader();
                    if (header != null) {
                        String oldMid = header.getMessageId();
                        if (!TextUtils.isEmpty(oldMid)) {
                            IMMessageBase msg = sentMessagesPool.remove(oldMid);
                            if (msg != null) {
                                if (listener != null) {
                                    listener.onMessageSendResult(oldMid, msg, errCode);
                                }
                            }
                        }
                    }
                }
            } else if (socketData.getType() == ImPacketType.PROTOCOL_MSG_PIC) {
                BibiProtoApplication.PicMessage picMessage = null;
                try {
                    picMessage = BibiProtoApplication.PicMessage.parseFrom(socketData.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (picMessage != null) {
                    BibiProtoApplication.MessageHeader header = picMessage.getHeader();
                    if (header != null) {
                        String oldMid = header.getMessageId();
                        if (!TextUtils.isEmpty(oldMid)) {
                            IMMessageBase msg = sentMessagesPool.remove(oldMid);
                            if (msg != null) {
                                if (listener != null) {
                                    listener.onMessageSendResult(oldMid, msg, errCode);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (listener != null) {
                listener.onMessageSendResult(null, null, errCode);
            }
        }
    }

    private IMMessageBase convertReceivedMessageFromPB(BibiProtoApplication.SyncDataPacket.DataEntry dataEntry) {
        if (dataEntry.getType() == ImPacketType.PROTOCOL_MSG_TEXT) {
            BibiProtoApplication.TextMessage textMessage = null;
            try {
                textMessage = BibiProtoApplication.TextMessage.parseFrom(dataEntry.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (textMessage != null) {
                BibiProtoApplication.MessageHeader header = textMessage.getHeader();
                if (header != null) {
                    if (header.getClassified() == BibiProtoApplication.MessageClassified.P2P) {
                        IMTextMessage imTextMessage = new IMTextMessage();
                        imTextMessage.setMid(header.getMessageId());
                        imTextMessage.setSid(IMMessageParser.buildSessionId(header.getFromUid(), header.getRemoteUid()));
                        imTextMessage.setSessionNickName(header.getGroupName());
                        imTextMessage.setSessionHead(header.getGroupUrl());
                        imTextMessage.setSessionType(IMSession.SESSION_TYPE_SINGLE);
                        imTextMessage.setSessionEnableChat(header.getEnableChat());
                        imTextMessage.setSessionVisableChat(header.getVisableChat());
                        imTextMessage.setSenderUid(header.getFromUid());
                        imTextMessage.setSenderNickName(header.getSenderName());
                        imTextMessage.setSenderHead(header.getSenderUrl());
                        imTextMessage.setRemoteUid(header.getRemoteUid());
                        imTextMessage.setStatus(IMMessageBase.MESSAGE_STATUS_RECEIVED);
                        imTextMessage.setTime(header.getCreatetime());
                        imTextMessage.setType(IMMessageBase.MESSAGE_TYPE_TXT);
                        imTextMessage.setText(textMessage.getText());
                        imTextMessage.setGreet(header.getIsGreet());
                        return imTextMessage;
                    }
                }
            }
        } else if (dataEntry.getType() == ImPacketType.PROTOCOL_MSG_PIC) {
            BibiProtoApplication.PicMessage picMessage = null;
            try {
                picMessage = BibiProtoApplication.PicMessage.parseFrom(dataEntry.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (picMessage != null) {
                BibiProtoApplication.MessageHeader header = picMessage.getHeader();
                if (header != null) {
                    if (header.getClassified() == BibiProtoApplication.MessageClassified.P2P) {
                        IMPicMessage imPicMessage = new IMPicMessage();
                        imPicMessage.setMid(header.getMessageId());
                        imPicMessage.setSid(IMMessageParser.buildSessionId(header.getFromUid(), header.getRemoteUid()));
                        imPicMessage.setSessionNickName(header.getGroupName());
                        imPicMessage.setSessionHead(header.getGroupUrl());
                        imPicMessage.setSessionType(IMSession.SESSION_TYPE_SINGLE);
                        imPicMessage.setSessionEnableChat(header.getEnableChat());
                        imPicMessage.setSessionVisableChat(header.getVisableChat());
                        imPicMessage.setSenderUid(header.getFromUid());
                        imPicMessage.setSenderNickName(header.getSenderName());
                        imPicMessage.setSenderHead(header.getSenderUrl());
                        imPicMessage.setRemoteUid(header.getRemoteUid());
                        imPicMessage.setStatus(IMMessageBase.MESSAGE_STATUS_RECEIVED);
                        imPicMessage.setTime(header.getCreatetime());
                        imPicMessage.setType(IMMessageBase.MESSAGE_TYPE_PIC);
                        imPicMessage.setPicLargeUrl(picMessage.getPicUri());
                        imPicMessage.setPicUrl(picMessage.getCoverUri());
                        imPicMessage.setGreet(header.getIsGreet());
                        return imPicMessage;
                    }
                }
            }
        } else if (dataEntry.getType() == ImPacketType.PROTOCOL_MSG_NOTICE) {
            BibiProtoApplication.NoticeMessage noticeMessage = null;
            try {
                noticeMessage = BibiProtoApplication.NoticeMessage.parseFrom(dataEntry.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (noticeMessage != null) {
                BibiProtoApplication.MessageHeader header = noticeMessage.getHeader();
                if (header != null) {
                    if (noticeMessage.getActionType() == BibiProtoApplication.NoticeMessage.ActionType.Normal) {
                        if (header.getClassified() == BibiProtoApplication.MessageClassified.P2P) {
                            IMSystemMessage systemMessage = new IMSystemMessage();
                            systemMessage.setMid(header.getMessageId());
                            systemMessage.setSid(IMMessageParser.buildSessionId(header.getFromUid(), header.getRemoteUid()));
                            systemMessage.setSessionNickName("unknown");
                            systemMessage.setSessionType(IMSession.SESSION_TYPE_SINGLE);
                            systemMessage.setSessionEnableChat(header.getEnableChat());
                            systemMessage.setSessionVisableChat(header.getVisableChat());
                            systemMessage.setSenderUid(header.getFromUid());
                            systemMessage.setSenderNickName("unknown");
                            systemMessage.setStatus(IMMessageBase.MESSAGE_STATUS_RECEIVED);
                            systemMessage.setTime(header.getCreatetime());
                            systemMessage.setType(IMMessageBase.MESSAGE_TYPE_SYSTEM);
                            systemMessage.setGreet(header.getIsGreet());
                            int actionCount = noticeMessage.getActionsCount();
                            if (actionCount > 0) {
                                BibiProtoApplication.NoticeMessage.Action action = noticeMessage.getActions(0);
                                systemMessage.setText(action.getText());
                                return systemMessage;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void fin(long lv) {
        BibiProtoApplication.SyncDataPacket.SyncMark mark = BibiProtoApplication.SyncDataPacket.SyncMark.newBuilder().setClassified(BibiProtoApplication.MessageClassified.P2P).setLastVersion(lv).build();
        BibiProtoApplication.SyncDataFin fin = BibiProtoApplication.SyncDataFin.newBuilder().addSyncMarks(mark).build();
        SocketData socketData = new SocketData();
        socketData.setType(ImPacketType.PROTOCOL_FIN);
        socketData.setData(fin.toByteArray());
        connectionManager.send(socketData);
    }

}
