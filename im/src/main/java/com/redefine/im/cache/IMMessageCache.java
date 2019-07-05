package com.redefine.im.cache;

import android.text.TextUtils;

import com.redefine.im.bean.IMAudioMessage;
import com.redefine.im.bean.IMMessageBase;
import com.redefine.im.bean.IMPicMessage;
import com.redefine.im.bean.IMSession;
import com.redefine.im.bean.IMSystemMessage;
import com.redefine.im.bean.IMTextMessage;
import com.redefine.im.bean.IMVideoMessage;
import com.redefine.welike.base.dao.im.DaoSession;
import com.redefine.welike.base.dao.im.Message;
import com.redefine.welike.base.dao.im.MessageDao;
import com.redefine.welike.base.dao.im.Session;
import com.redefine.welike.base.dao.im.SessionDao;
import com.redefine.welike.base.profile.AccountManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liubin on 2018/2/2.
 */

public class IMMessageCache {
    private MessageDao messageDao;
    private SessionDao sessionDao;
    private DaoSession daoSession;
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();


    public interface IMMessageCacheReceivedCallback {

        void onReceivedMessagesInserted(List<IMMessageBase> messages, Set<String> sids, boolean last);

    }

    public interface IMMessageCacheSendCallback {

        void onSendMessageInserted(IMMessageBase message, String sid);

    }

    public interface IMMessageCacheMessagesInSessionCallback {

        void onMessagesInSession(List<IMMessageBase> messages);

    }

    public interface IMMessageCacheClearSessionUnreadCountCallback {

        void onClearSessionUnreadCount(int count);

    }

    public interface IMMessageCacheSingleSessionCallback {

        void onGetSingleSession(IMSession session);

    }

    public interface IMMessageCacheSessionsCallback {

        void onListSessions(List<IMSession> sessions);

    }

    public interface IMMessageCacheSessionsCountCallback {

        void onSessionsCount(int count);

    }

    private static class IMMessageCacheHolder {
        public static IMMessageCache instance = new IMMessageCache();
    }

    private IMMessageCache() {
        daoSession = IMMultiAccountDBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            messageDao = daoSession.getMessageDao();
            sessionDao = daoSession.getSessionDao();
        }
    }

    public static IMMessageCache getInstance() { return IMMessageCacheHolder.instance; }

    public void insertReceivedMessages(final List<IMMessageBase> messages, final boolean last, final IMMessageCacheReceivedCallback callback) {
        if (daoSession != null) {
            singleThreadExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    daoSession.runInTx(new Runnable() {

                        @Override
                        public void run() {
                            runInsertReceivedMessages(messages, last, callback);
                        }

                    });
                }

            });
        }
    }

    public void sendMessage(final IMMessageBase message, final IMSession session, final IMMessageCacheSendCallback callback) {
        if (daoSession != null) {
            singleThreadExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    daoSession.runInTx(new Runnable() {

                        @Override
                        public void run() {
                            runSendMessage(message, session, callback);
                        }

                    });
                }

            });
        }
    }

//    public synchronized void deleteMessage(IMMessageBase message) {
//        if (messageDao != null) {
//            try {
//                messageDao.queryBuilder().where(MessageDao.Properties.Mid.eq(message.getMid())).buildDelete().executeDeleteWithoutDetachingEntities();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void refreshNetworkMessage(final String oldMid, final String newMid, final long messageTime, final boolean isGreet, final byte status) {
        if (daoSession != null) {
            singleThreadExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    daoSession.runInTx(new Runnable() {

                        @Override
                        public void run() {
                            runRefreshNetworkMessage(oldMid, newMid, messageTime, isGreet, status);
                        }

                    });
                }

            });
        }
    }

    public void listNewMessagesInSession(final String sid, final int countOnePage, final IMMessageCacheMessagesInSessionCallback callback) {
        if (messageDao == null) {
            if (callback != null) {
                callback.onMessagesInSession(null);
            }
        }

        singleThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                List<IMMessageBase> messages = new ArrayList<>();
                try {
                    List<Message> dbMessages = messageDao.queryBuilder().where(MessageDao.Properties.Sid.eq(sid)).orderDesc(MessageDao.Properties.Time).limit(countOnePage).build().list();
                    if (dbMessages != null && dbMessages.size() > 0) {
                        for (Message dbMessage : dbMessages) {
                            IMMessageBase message = parseDBToMessage(dbMessage);
                            if (message != null) {
                                messages.add(message);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.onMessagesInSession(messages);
                }
            }

        });
    }

    public void listHisMessagesInSession(final String sid, final String cursorMid, final int countOnePage, final IMMessageCacheMessagesInSessionCallback callback) {
        if (messageDao == null) {
            if (callback != null) {
                callback.onMessagesInSession(null);
            }
        }

        singleThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                List<IMMessageBase> messages = new ArrayList<>();
                try {
                    Message cursorMessage = messageDao.queryBuilder().where(MessageDao.Properties.Mid.eq(cursorMid)).build().unique();
                    if (cursorMessage != null) {
                        List<Message> dbMessages = messageDao.queryBuilder().where(MessageDao.Properties.Time.le(cursorMessage.getTime()), MessageDao.Properties.Sid.eq(sid)).orderDesc(MessageDao.Properties.Time).limit(countOnePage + 1).build().list();
                        if (dbMessages != null && dbMessages.size() > 0) {
                            boolean begin = false;
                            for (Message dbMessage : dbMessages) {
                                if (begin) {
                                    IMMessageBase message = parseDBToMessage(dbMessage);
                                    if (message != null) {
                                        messages.add(message);
                                    }
                                } else if (TextUtils.equals(dbMessage.getMid(), cursorMessage.getMid())) {
                                    begin = true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.onMessagesInSession(messages);
                }
            }

        });
    }

    public void deleteSession(IMSession session) {
        if (daoSession != null) {
            final Session dbSession = parseSessionToDB(session);
            singleThreadExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    daoSession.runInTx(new Runnable() {

                        @Override
                        public void run() {
                            runDeleteSession(dbSession);
                        }

                    });
                }

            });
        }
    }

    public void clearSessionUnreadCount(final String sid, final IMMessageCacheClearSessionUnreadCountCallback callback) {
        singleThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                int count = 0;
                if (sessionDao != null) {
                    try {
                        Session dbSession = sessionDao.queryBuilder().where(SessionDao.Properties.Sid.eq(sid)).build().unique();
                        if (dbSession != null) {
                            count = -dbSession.getUnreadCount();
                            dbSession.setUnreadCount(0);
                            sessionDao.update(dbSession);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.onClearSessionUnreadCount(count);
                }
            }

        });
    }

    public void getSingleChatSession(final String uid, final IMMessageCacheSingleSessionCallback callback) {
        if (sessionDao != null && messageDao != null) {
            singleThreadExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    IMSession session = null;
                    Session dbSession = sessionDao.queryBuilder().where(SessionDao.Properties.SessionType.eq(IMSession.SESSION_TYPE_SINGLE),
                            SessionDao.Properties.SingleUid.eq(uid)).build().unique();
                    if (dbSession != null) {
                        session = parseDBToSession(dbSession);
                    }
                    if (callback != null) {
                        callback.onGetSingleSession(session);
                    }
                }

            });
        }
    }

    public void listAllSessions(final boolean greet, final IMMessageCacheSessionsCallback callback) {
        if (sessionDao == null) {
            if (callback != null) {
                callback.onListSessions(null);
            }
        }

        singleThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                List<IMSession> sessions = new ArrayList<>();
                try {
                    IMSession stranger = null;
                    if (!greet) {
                        Session ss = sessionDao.queryBuilder().where(SessionDao.Properties.IsGreet.eq(true)).orderDesc(SessionDao.Properties.Time).limit(1).build().unique();
                        if (ss != null) {
                            boolean hasUnread = strangersHasUnread();
                            stranger = new IMSession(IMSession.STRANGER_SESSION_SID);
                            stranger.setSessionType(IMSession.SESSION_TYPE_STRANGER);
                            stranger.setTime(ss.getTime());
                            if (hasUnread) {
                                stranger.setUnreadCount(1);
                            } else {
                                stranger.setUnreadCount(0);
                            }
                        }
                    }
                    List<Session> dbSessions = sessionDao.queryBuilder().where(SessionDao.Properties.IsGreet.eq(greet)).orderDesc(SessionDao.Properties.Time).build().list();
                    if (dbSessions != null && dbSessions.size() > 0) {
                        for (Session s : dbSessions) {
                            if (stranger != null) {
                                if (s.getTime() < stranger.getTime()) {
                                    sessions.add(stranger);
                                    stranger = null;
                                }
                            }
                            IMSession session = parseDBToSession(s);
                            sessions.add(session);
                        }
                        if (stranger != null) {
                            sessions.add(stranger);
                        }
                    } else if (stranger != null) {
                        sessions.add(stranger);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (callback != null) {
                    callback.onListSessions(sessions);
                }
            }

        });
    }

    public void countAllSessions(final boolean greet, final IMMessageCacheSessionsCountCallback callback) {
        if (sessionDao == null) {
            if (callback != null) {
                callback.onSessionsCount(0);
            }
        }
        singleThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                int count = 0;
                try {
                    count = (int)sessionDao.queryBuilder().where(SessionDao.Properties.IsGreet.eq(greet)).buildCount().count();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.onSessionsCount(count);
                }
            }

        });
    }

    public void getSessions(final Set<String> sids, final IMMessageCacheSessionsCallback callback) {
        if (sessionDao == null) {
            if (callback != null) {
                callback.onListSessions(null);
            }
        }
        singleThreadExecutor.execute(new Runnable() {

            @Override
            public void run() {
                List<IMSession> sessions = new ArrayList<>();
                try {
                    List<Session> dbSessions = sessionDao.queryBuilder().where(SessionDao.Properties.Sid.in(sids)).build().list();
                    for (Session dbSession : dbSessions) {
                        IMSession session = parseDBToSession(dbSession);
                        sessions.add(session);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.onListSessions(sessions);
                }
            }

        });
    }

    public void clear() {
        if (sessionDao != null && messageDao != null) {
            singleThreadExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        sessionDao.deleteAll();
                        messageDao.deleteAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        }
    }

    private synchronized void runSendMessage(IMMessageBase message, IMSession session, IMMessageCacheSendCallback callback) {
        if (messageDao != null && sessionDao != null) {
            Message dbMessage = parseMessageToDB(message);
            try {
                messageDao.insert(dbMessage);
                Session dbSession = sessionDao.queryBuilder().where(SessionDao.Properties.Sid.eq(dbMessage.getSid())).build().unique();
                if (dbSession == null) {
                    dbSession = parseSessionToDB(session);
                }
                if (dbSession.getTime() < dbMessage.getTime()) {
                    dbSession.setMsgType(dbMessage.getType());
                    dbSession.setContent(dbMessage.getText());
                    dbSession.setTime(dbMessage.getTime());
                    sessionDao.insertOrReplace(dbSession);
                    if (callback != null) {
                        callback.onSendMessageInserted(message, dbMessage.getSid());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void runInsertReceivedMessages(List<IMMessageBase> messages, boolean last, IMMessageCacheReceivedCallback callback) {
        if (messageDao != null && sessionDao != null) {
            final List<Message> dbMessages = new ArrayList<>();
            for (IMMessageBase msg : messages) {
                Message dbMessage = parseMessageToDB(msg);
                dbMessages.add(dbMessage);
            }

            Map<String, Message> sessionsMap = new HashMap<>();
            Map<String, Integer> sessionsUnreadCount = new HashMap<>();
            Set<String> sids = new HashSet<>();
            List<IMMessageBase> filterMessages = new ArrayList<>();
            filterMessages.addAll(messages);
            for (Message msg : dbMessages) {
                try {
                    messageDao.insert(msg);
                    if (msg.getType() == IMMessageBase.MESSAGE_TYPE_SYSTEM && msg.getSessionType() == IMSession.SESSION_TYPE_SINGLE) continue;
                    sids.add(msg.getSid());
                    Message preMsg = sessionsMap.get(msg.getSid());
                    if (preMsg != null) {
                        if (preMsg.getTime() < msg.getTime()) {
                            sessionsMap.put(msg.getSid(), msg);
                        }
                    } else {
                        sessionsMap.put(msg.getSid(), msg);
                    }
                    Integer unreadCount = sessionsUnreadCount.get(msg.getSid());
                    if (unreadCount != null) {
                        unreadCount += 1;
                    } else {
                        unreadCount = 1;
                    }
                    sessionsUnreadCount.put(msg.getSid(), unreadCount);
                } catch (Exception e) {
                    e.printStackTrace();
                    removeMessageInList(msg.getMid(), filterMessages);
                }
            }

            List<Session> existedSessionList = sessionDao.queryBuilder().where(SessionDao.Properties.Sid.in(sids)).build().list();
            for (Map.Entry<String, Message> entry : sessionsMap.entrySet()) {
                String sid = entry.getKey();
                Message msg = entry.getValue();
                IMMessageBase mm = findMessagesWithMid(msg.getMid(), messages);
                if (mm == null) continue;
                Session dbSession = findDBSessionInList(sid, existedSessionList);
                if (dbSession == null) {
                    dbSession = new Session();
                    dbSession.setSid(sid);
                }
                dbSession.setEnableChat(mm.isSessionEnableChat());
                dbSession.setVisableChat(mm.isSessionVisableChat());
                dbSession.setIsGreet(mm.isGreet());
                dbSession.setSessionNick(msg.getSessionNick());
                dbSession.setSessionHead(msg.getSessionHead());
                if (msg.getSessionType() == IMSession.SESSION_TYPE_SINGLE) {
                    if (TextUtils.equals(msg.getSenderUid(), AccountManager.getInstance().getAccount().getUid()))
                    {
                        dbSession.setSingleUid(mm.getRemoteUid());
                    } else {
                        dbSession.setSingleUid(mm.getSenderUid());
                    }
                    dbSession.setSessionType(IMSession.SESSION_TYPE_SINGLE);
                }
                long pretime = dbSession.getTime();
                if (pretime < msg.getTime()) {
                    dbSession.setMsgType(msg.getType());
                    dbSession.setTime(msg.getTime());
                    dbSession.setContent(msg.getText());
                }
                int newUnreadCount = sessionsUnreadCount.get(sid);
                dbSession.setUnreadCount(dbSession.getUnreadCount() + newUnreadCount);
                sessionDao.insertOrReplace(dbSession);
            }
            if (callback != null) {
                callback.onReceivedMessagesInserted(filterMessages, sids, last);
            }
        } else {
            if (callback != null) {
                callback.onReceivedMessagesInserted(null, null, last);
            }
        }
    }

    private synchronized void runRefreshNetworkMessage(String oldMid, String newMid, long messageTime, boolean isGreet, byte status) {
        if (messageDao != null) {
            try {
                Message oldMsg = messageDao.queryBuilder().where(MessageDao.Properties.Mid.eq(oldMid)).build().unique();
                if (oldMsg != null) {
                    Message dbMessage = new Message();
                    dbMessage.setMid(newMid);
                    dbMessage.setSid(oldMsg.getSid());
                    dbMessage.setSessionNick(oldMsg.getSessionNick());
                    dbMessage.setSessionHead(oldMsg.getSessionHead());
                    dbMessage.setSenderUid(oldMsg.getSenderUid());
                    dbMessage.setSenderNick(oldMsg.getSenderNick());
                    dbMessage.setSenderHead(oldMsg.getSenderHead());
                    dbMessage.setType(oldMsg.getType());
                    dbMessage.setSessionType(oldMsg.getSessionType());
                    dbMessage.setStatus(status);
                    dbMessage.setTime(messageTime);
                    dbMessage.setText(oldMsg.getText());
                    dbMessage.setThumb(oldMsg.getThumb());
                    dbMessage.setPic(oldMsg.getPic());
                    dbMessage.setAudio(oldMsg.getAudio());
                    dbMessage.setVideo(oldMsg.getVideo());
                    dbMessage.setFileName(oldMsg.getFileName());

                    messageDao.delete(oldMsg);
                    messageDao.insertOrReplace(dbMessage);

                    Session session = sessionDao.queryBuilder().where(SessionDao.Properties.Sid.eq(dbMessage.getSid())).build().unique();
                    if (session != null) {
                        session.setIsGreet(isGreet);
                        sessionDao.update(session);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void runDeleteSession(Session session) {
        if (messageDao != null && sessionDao != null) {
            String sid = session.getSid();
            sessionDao.delete(session);
            messageDao.queryBuilder().where(MessageDao.Properties.Sid.eq(sid)).buildDelete().executeDeleteWithoutDetachingEntities();
        }
    }

    private boolean strangersHasUnread() {
        boolean has = false;
        if (sessionDao != null) {
            int count = (int)sessionDao.queryBuilder().where(SessionDao.Properties.IsGreet.eq(true), SessionDao.Properties.UnreadCount.gt(0)).buildCount().count();
            if (count > 0) {
                has = true;
            }
        }
        return has;
    }

    private static Message parseMessageToDB(IMMessageBase message) {
        Message dbMessage = new Message();
        dbMessage.setMid(message.getMid());
        dbMessage.setSid(message.getSid());
        dbMessage.setSessionNick(message.getSessionNickName());
        dbMessage.setSessionHead(message.getSessionHead());
        dbMessage.setSessionType(message.getSessionType());
        dbMessage.setSenderUid(message.getSenderUid());
        dbMessage.setSenderNick(message.getSenderNickName());
        dbMessage.setSenderHead(message.getSenderHead());
        if (message.getStatus() == IMMessageBase.MESSAGE_STATUS_SENDING) {
            dbMessage.setStatus(IMMessageBase.MESSAGE_STATUS_FAILED);
        } else {
            dbMessage.setStatus(message.getStatus());
        }
        dbMessage.setTime(message.getTime());
        dbMessage.setType(message.getType());
        if (message.getType() == IMMessageBase.MESSAGE_TYPE_TXT) {
            IMTextMessage textMessage = (IMTextMessage)message;
            dbMessage.setText(textMessage.getText());
        } else if (message.getType() == IMMessageBase.MESSAGE_TYPE_PIC) {
            IMPicMessage picMessage = (IMPicMessage)message;
            dbMessage.setThumb(picMessage.getPicUrl());
            dbMessage.setPic(picMessage.getPicLargeUrl());
            dbMessage.setFileName(picMessage.getLocalFileName());
        } else if (message.getType() == IMMessageBase.MESSAGE_TYPE_AUDIO) {
            IMAudioMessage audioMessage = (IMAudioMessage)message;
            dbMessage.setAudio(audioMessage.getAudioUrl());
            dbMessage.setFileName(audioMessage.getLocalFileName());
        } else if (message.getType() == IMMessageBase.MESSAGE_TYPE_VIDEO) {
            IMVideoMessage videoMessage = (IMVideoMessage)message;
            dbMessage.setThumb(((IMVideoMessage) message).getThumb());
            dbMessage.setVideo(videoMessage.getVideo());
            dbMessage.setFileName(videoMessage.getLocalFileName());
        } else if (message.getType() == IMMessageBase.MESSAGE_TYPE_SYSTEM) {
            IMSystemMessage systemMessage = (IMSystemMessage)message;
            dbMessage.setText(systemMessage.getText());
        }
        if (dbMessage.getType() != IMMessageBase.MESSAGE_TYPE_UNKNOWN) {
            return dbMessage;
        }
        return null;
    }

    private static IMMessageBase parseDBToMessage(Message dbMessage) {
        IMMessageBase message = null;
        byte type = dbMessage.getType();
        if (type == IMMessageBase.MESSAGE_TYPE_TXT) {
            IMTextMessage textMessage = new IMTextMessage();
            textMessage.setText(dbMessage.getText());
            message = textMessage;
        } else if (type == IMMessageBase.MESSAGE_TYPE_PIC) {
            IMPicMessage picMessage = new IMPicMessage();
            picMessage.setPicUrl(dbMessage.getThumb());
            picMessage.setPicLargeUrl(dbMessage.getPic());
            picMessage.setLocalFileName(dbMessage.getFileName());
            message = picMessage;
        } else if (type == IMMessageBase.MESSAGE_TYPE_AUDIO) {
            IMAudioMessage audioMessage = new IMAudioMessage();
            audioMessage.setAudioUrl(dbMessage.getAudio());
            audioMessage.setLocalFileName(dbMessage.getFileName());
            message = audioMessage;
        } else if (type == IMMessageBase.MESSAGE_TYPE_VIDEO) {
            IMVideoMessage videoMessage = new IMVideoMessage();
            videoMessage.setVideo(dbMessage.getVideo());
            videoMessage.setThumb(dbMessage.getThumb());
            videoMessage.setLocalFileName(dbMessage.getFileName());
            message = videoMessage;
        } else if (type == IMMessageBase.MESSAGE_TYPE_SYSTEM) {
            IMSystemMessage systemMessage = new IMSystemMessage();
            systemMessage.setText(dbMessage.getText());
            message = systemMessage;
        }
        if (message != null) {
            message.setMid(dbMessage.getMid());
            message.setSid(dbMessage.getSid());
            message.setSessionNickName(dbMessage.getSessionNick());
            message.setSessionHead(dbMessage.getSessionHead());
            message.setSessionType(dbMessage.getSessionType());
            message.setSenderUid(dbMessage.getSenderUid());
            message.setSenderNickName(dbMessage.getSenderNick());
            message.setSenderHead(dbMessage.getSenderHead());
            message.setStatus(dbMessage.getStatus());
            message.setTime(dbMessage.getTime());
        }
        return message;
    }

    private static Session parseSessionToDB(IMSession session) {
        Session dbSession = new Session();
        dbSession.setSid(session.getSid());
        dbSession.setSessionNick(session.getNickName());
        dbSession.setSessionHead(session.getHead());
        dbSession.setSingleUid(session.getSingleUid());
        dbSession.setMsgType(session.getMsgType());
        dbSession.setSessionType(session.getSessionType());
        dbSession.setTime(session.getTime());
        dbSession.setEnableChat(session.isEnableChat());
        dbSession.setVisableChat(session.isVisableChat());
        dbSession.setUnreadCount(session.getUnreadCount());
        dbSession.setContent(session.getContent());
        dbSession.setIsGreet(session.isGreet());
        return dbSession;
    }

    private static IMSession parseDBToSession(Session dbSession) {
        IMSession session = new IMSession(dbSession.getSid());
        session.setNickName(dbSession.getSessionNick());
        session.setHead(dbSession.getSessionHead());
        session.setSingleUid(dbSession.getSingleUid());
        session.setMsgType(dbSession.getMsgType());
        if (dbSession.getSessionType() != 0) {
            session.setSessionType(dbSession.getSessionType());
        } else {
            session.setSessionType(IMSession.SESSION_TYPE_SINGLE);
        }
        session.setTime(dbSession.getTime());
        session.setEnableChat(dbSession.getEnableChat());
        session.setVisableChat(dbSession.getVisableChat());
        session.setUnreadCount(dbSession.getUnreadCount());
        session.setContent(dbSession.getContent());
        session.setGreet(dbSession.getIsGreet());
        return session;
    }

    private static Session findDBSessionInList(String sid, List<Session> dbSessionList) {
        if (dbSessionList != null && dbSessionList.size() > 0) {
            for (Session dbSession : dbSessionList) {
                if (TextUtils.equals(dbSession.getSid(), sid)) {
                    return dbSession;
                }
            }
        }
        return null;
    }

    private static void removeMessageInList(String mid, List<IMMessageBase> messages) {
        int idx = -1;
        for (IMMessageBase message : messages) {
            idx++;
            if (TextUtils.equals(message.getMid(), mid)) {
                break;
            }
        }
        if (idx != -1) {
            messages.remove(idx);
        }
    }

    private static IMMessageBase findMessagesWithMid(String mid, List<IMMessageBase> messages) {
        int idx = -1;
        if (messages != null && messages.size() > 0) {
            for (IMMessageBase message : messages) {
                idx++;
                if (TextUtils.equals(message.getMid(), mid)) {
                    break;
                }
            }
            if (idx != -1) {
                return messages.get(idx);
            }
        }
        return null;
    }


    public void update() {
        daoSession = IMMultiAccountDBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            messageDao = daoSession.getMessageDao();
            sessionDao = daoSession.getSessionDao();
        }
    }

}
