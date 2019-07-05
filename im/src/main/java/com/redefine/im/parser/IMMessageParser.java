package com.redefine.im.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.im.bean.IMSession;
import com.redefine.im.bean.IMMessageBase;
import com.redefine.im.bean.IMPicMessage;
import com.redefine.im.bean.IMSystemMessage;
import com.redefine.im.bean.IMTextMessage;
import com.redefine.im.service.socket.protocol.BibiProtoApplication;
import com.redefine.im.service.socket.protocol.ImPacketType;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liubin on 2018/2/2.
 */

public class IMMessageParser {
    private static final String KEY_IM_LIST = "list";
    private static final String KEY_IM_CURSOR = "lastVersion";
    private static final String KEY_IM_HAS_MORE = "hasMore";
    private static final String KEY_IM_SESSION_ID = "id";
    private static final String KEY_IM_SESSION_TYPE = "type";
    private static final String KEY_IM_SESSION_NICK = "sessionNickName";
    private static final String KEY_IM_SESSION_HEAD = "sessionImageUrl";
    private static final String KEY_IM_SESSION_TIME = "created";
    private static final String KEY_IM_SESSION_MEMBER = "members";
    private static final String KEY_IM_SESSION_TYPE_SINGLE = "SINGLE";
    private static final String KEY_IM_SESSION_ENABLE_CHAT = "enableChat";
    private static final String KEY_IM_SESSION_VISABLE_CHAT = "visableChat";
    private static final String KEY_IM_SESSION_USER_ID = "id";
    private static final String KEY_IM_SESSION_GREET = "isGreet";
    private static final String KEY_IM_MESSAGE_ID = "messageId";
    private static final String KEY_IM_MESSAGE_TYPE = "messageType";
    private static final String KEY_IM_MESSAGE_TIME = "createtime";
    private static final String KEY_IM_MESSAGE_CONTENT = "content";
    private static final String KEY_IM_MESSAGE_SESSION_ID = "conversionId";
    private static final String KEY_IM_MESSAGE_SESSION_NAME = "groupName";
    private static final String KEY_IM_MESSAGE_SESSION_HEAD = "groupUrl";
    private static final String KEY_IM_MESSAGE_SENDER_UID = "fromUid";
    private static final String KEY_IM_MESSAGE_SENDER_NICK = "senderName";
    private static final String KEY_IM_MESSAGE_SENDER_HEAD = "senderUrl";
    private static final String KEY_IM_MESSAGE_CLASSIFIED = "classified";
    private static final String KEY_IM_MESSAGE_NOTICE_TYPE = "noticeType";
    private static final String KEY_IM_MESSAGE_GREET = "isGreet";
    private static final String KEY_IM_MESSAGE_REMOTE_UID = "remoteUid";
    private static final String KEY_IM_MESSAGE_REMOTE_NAME = "remoteName";
    private static final String KEY_IM_MESSAGE_REMOTE_URL = "remoteUrl";

    public static List<IMMessageBase> parseMessages(JSONObject result) {
        List<IMMessageBase> messages = null;
        if (result != null) {
            JSONArray messagesJSON = result.getJSONArray(KEY_IM_LIST);
            if (messagesJSON != null && messagesJSON.size() > 0) {
                messages = new ArrayList<>();
                for (Iterator iterator = messagesJSON.iterator(); iterator.hasNext();) {
                    JSONObject messageJSON = (JSONObject)iterator.next();
                    IMMessageBase message = parseMessage(messageJSON);
                    if (message != null) {
                        messages.add(message);
                    }
                }
            }
        }
        return messages;
    }

    public static String parseCursor(JSONObject result) {
        String cursor = null;
        if (result != null) {
            try {
                cursor = result.getString(KEY_IM_CURSOR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }

    public static boolean parseLast(JSONObject result) {
        if (result != null) {
            boolean hasMore = result.getBooleanValue(KEY_IM_HAS_MORE);
            if (hasMore) return false;
            return true;
        }
        return false;
    }

    public static IMSession parseSession(JSONObject sessionJSON) {
        if (sessionJSON != null) {
            String sid = sessionJSON.getString(KEY_IM_SESSION_ID);
            String sessionType = sessionJSON.getString(KEY_IM_SESSION_TYPE);
            String nick = sessionJSON.getString(KEY_IM_SESSION_NICK);
            String head = sessionJSON.getString(KEY_IM_SESSION_HEAD);
            long time = sessionJSON.getLongValue(KEY_IM_SESSION_TIME);
            boolean enableChat = true;
            try {
                enableChat = sessionJSON.getBoolean(KEY_IM_SESSION_ENABLE_CHAT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean visableChat = true;
            try {
                visableChat = sessionJSON.getBoolean(KEY_IM_SESSION_VISABLE_CHAT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean isGreet = true;
            try {
                isGreet = sessionJSON.getBoolean(KEY_IM_SESSION_GREET);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte sType = IMSession.SESSION_TYPE_SINGLE;
            if (TextUtils.equals(sessionType, KEY_IM_SESSION_TYPE_SINGLE)) {
                JSONArray membersJSON = sessionJSON.getJSONArray(KEY_IM_SESSION_MEMBER);
                if (membersJSON != null) {
                    Account account = AccountManager.getInstance().getAccount();
                    if (account != null) {
                        String singleUid = null;
                        for (Iterator iterator = membersJSON.iterator(); iterator.hasNext();) {
                            JSONObject member = (JSONObject) iterator.next();
                            String uid = member.getString(KEY_IM_SESSION_USER_ID);
                            if (!TextUtils.equals(uid, account.getUid())) {
                                singleUid = uid;
                                break;
                            }
                        }
                        IMSession session = new IMSession(sid);
                        session.setNickName(nick);
                        session.setHead(head);
                        session.setSingleUid(singleUid);
                        session.setTime(time);
                        session.setUnreadCount(0);
                        session.setSessionType(sType);
                        session.setEnableChat(enableChat);
                        session.setVisableChat(visableChat);
                        session.setGreet(isGreet);

                        return session;
                    }
                }
            }
        }
        return null;
    }

    public static IMMessageBase parseMessage(JSONObject messageJSON) {
        if (messageJSON == null) return null;

        IMMessageBase message = null;
        byte msgType = IMMessageBase.MESSAGE_TYPE_UNKNOWN;
        int mType = messageJSON.getIntValue(KEY_IM_MESSAGE_TYPE);
        if (mType == ImPacketType.PROTOCOL_MSG_TEXT) {
            msgType = IMMessageBase.MESSAGE_TYPE_TXT;
        } else if (mType == ImPacketType.PROTOCOL_MSG_PIC) {
            msgType = IMMessageBase.MESSAGE_TYPE_PIC;
        } else if (mType == ImPacketType.PROTOCOL_MSG_NOTICE) {
            int noticeType = messageJSON.getIntValue(KEY_IM_MESSAGE_NOTICE_TYPE);
            if (noticeType == BibiProtoApplication.NoticeMessage.ActionType.Normal.getNumber()) {
                msgType = IMMessageBase.MESSAGE_TYPE_SYSTEM;
            }
        }
        String content = null;
        try {
            content = messageJSON.getString(KEY_IM_MESSAGE_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (msgType == IMMessageBase.MESSAGE_TYPE_TXT) {
            IMTextMessage textMessage = new IMTextMessage();
            textMessage.setText(content);
            message = textMessage;
        } else if (msgType == IMMessageBase.MESSAGE_TYPE_PIC) {
            IMPicMessage picMessage = new IMPicMessage();
            picMessage.setPicUrl(content);
            picMessage.setPicLargeUrl(content);
            message = picMessage;
        } else if (msgType == IMMessageBase.MESSAGE_TYPE_SYSTEM) {
            int classified = messageJSON.getIntValue(KEY_IM_MESSAGE_CLASSIFIED);
            if (classified == BibiProtoApplication.MessageClassified.P2P.getNumber()) {
                IMSystemMessage systemMessage = new IMSystemMessage();
                systemMessage.setText(content);
                message = systemMessage;
            }
        }

        if (message != null) {
            if (msgType == IMMessageBase.MESSAGE_TYPE_SYSTEM) {
                String mid = messageJSON.getString(KEY_IM_MESSAGE_ID);
                long time = messageJSON.getLongValue(KEY_IM_MESSAGE_TIME);
                String sid = messageJSON.getString(KEY_IM_MESSAGE_SESSION_ID);
                int classified = messageJSON.getIntValue(KEY_IM_MESSAGE_CLASSIFIED);
                byte sType = IMSession.SESSION_TYPE_SINGLE;
                if (classified == BibiProtoApplication.MessageClassified.P2P.getNumber()) {
                    sType = IMSession.SESSION_TYPE_SINGLE;
                }
                String senderUid = messageJSON.getString(KEY_IM_MESSAGE_SENDER_UID);
                String remoteUid = messageJSON.getString(KEY_IM_MESSAGE_REMOTE_UID);
                boolean enableChat = true;
                try {
                    enableChat = messageJSON.getBoolean(KEY_IM_SESSION_ENABLE_CHAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                boolean visableChat = true;
                try {
                    visableChat = messageJSON.getBoolean(KEY_IM_SESSION_VISABLE_CHAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                boolean isGreet = true;
                try {
                    isGreet = messageJSON.getBoolean(KEY_IM_MESSAGE_GREET);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                message.setMid(mid);
                message.setSid(sid);
                message.setSessionNickName("unknown");
                message.setSessionHead(null);
                message.setSessionType(sType);
                message.setSessionVisableChat(visableChat);
                message.setSessionEnableChat(enableChat);
                message.setSenderUid(senderUid);
                message.setSenderNickName("unknown");
                message.setSenderHead(null);
                message.setGreet(isGreet);
                message.setRemoteUid(remoteUid);
                message.setStatus(IMMessageBase.MESSAGE_STATUS_RECEIVED);
                message.setTime(time);
            } else {
                String mid = messageJSON.getString(KEY_IM_MESSAGE_ID);
                long time = messageJSON.getLongValue(KEY_IM_MESSAGE_TIME);
                String sid = messageJSON.getString(KEY_IM_MESSAGE_SESSION_ID);
                String sessionNickName = messageJSON.getString(KEY_IM_MESSAGE_SESSION_NAME);
                String sessionHead = messageJSON.getString(KEY_IM_MESSAGE_SESSION_HEAD);
                int classified = messageJSON.getIntValue(KEY_IM_MESSAGE_CLASSIFIED);
                byte sType = IMSession.SESSION_TYPE_SINGLE;
                if (classified == BibiProtoApplication.MessageClassified.P2P.getNumber()) {
                    sType = IMSession.SESSION_TYPE_SINGLE;
                }
                String senderUid = messageJSON.getString(KEY_IM_MESSAGE_SENDER_UID);
                String senderNickName = messageJSON.getString(KEY_IM_MESSAGE_SENDER_NICK);
                String senderHead = messageJSON.getString(KEY_IM_MESSAGE_SENDER_HEAD);
                String remoteUid = messageJSON.getString(KEY_IM_MESSAGE_REMOTE_UID);
                boolean enableChat = true;
                try {
                    enableChat = messageJSON.getBoolean(KEY_IM_SESSION_ENABLE_CHAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                boolean visableChat = true;
                try {
                    visableChat = messageJSON.getBoolean(KEY_IM_SESSION_VISABLE_CHAT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                boolean isGreet = true;
                try {
                    isGreet = messageJSON.getBoolean(KEY_IM_MESSAGE_GREET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String remoteNickName = null;
                try {
                    remoteNickName = messageJSON.getString(KEY_IM_MESSAGE_REMOTE_NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String remoteUrl = null;
                try {
                    remoteUrl = messageJSON.getString(KEY_IM_MESSAGE_REMOTE_URL);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                message.setMid(mid);
                message.setSid(sid);
                message.setSessionNickName(sessionNickName);
                message.setSessionHead(sessionHead);
                message.setSessionType(sType);
                message.setSessionVisableChat(visableChat);
                message.setSessionEnableChat(enableChat);
                message.setSenderUid(senderUid);
                message.setRemoteUid(remoteUid);
                message.setSenderNickName(senderNickName);
                message.setSenderHead(senderHead);
                message.setGreet(isGreet);
                if (TextUtils.equals(message.getSenderUid(), AccountManager.getInstance().getAccount().getUid())) {
                    message.setStatus(IMMessageBase.MESSAGE_STATUS_SENT);
                    message.setSessionNickName(remoteNickName);
                    message.setSessionHead(remoteUrl);
                } else {
                    message.setStatus(IMMessageBase.MESSAGE_STATUS_RECEIVED);
                }
                message.setTime(time);
            }
        }
        return message;
    }

    public static String buildSessionId(String uid1, String uid2) {
        int uid1Int = Integer.parseInt(uid1);
        int uid2Int = Integer.parseInt(uid2);
        if (uid1Int < uid2Int) {
            return uid1 + "-_-!!!" + uid2;
        } else {
            return uid2 + "-_-!!!" + uid1;
        }
    }

}
