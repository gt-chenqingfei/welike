package com.redefine.welike.business.user.management.cache;

import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.base.DBStore;
import com.redefine.welike.base.dao.welike.DaoSession;
import com.redefine.welike.base.dao.welike.UserDao;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liubin on 2018/1/24.
 */

public class ContactsCache {
    private DaoSession daoSession;
    private UserDao userDao;
    private final List<User> contacts = new ArrayList<>();

    public ContactsCache() {
        daoSession = DBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            userDao = daoSession.getUserDao();
        }
    }

    public synchronized int contactsCount() {
        if (userDao == null) return 0;
        return (int)userDao.queryBuilder().buildCount().count();
    }

    public void load() {
        if (userDao == null) return;

        synchronized (this) {
            contacts.clear();
            List<com.redefine.welike.base.dao.welike.User> users = userDao.queryBuilder().orderAsc(UserDao.Properties.Created).build().list();
            for (com.redefine.welike.base.dao.welike.User user : users) {
                User contact = new User();
                contact.setUid(user.getUid());
                contact.setNickName(user.getNick());
                contact.setHeadUrl(user.getHead());
                contact.setSex(user.getSex());
                contact.setVip(user.getVip());
                contacts.add(contact);
            }
        }
    }

    public List<User> listAllContacts() {
        return contacts;
    }

    public synchronized List<User> listRecentContacts() {
        if (userDao == null) return null;

        List<User> userList = new ArrayList<>();
        List<com.redefine.welike.base.dao.welike.User> l = userDao.queryBuilder().orderDesc(UserDao.Properties.AtTime).limit(5).build().list();
        if (l != null && l.size() > 0) {
            for (com.redefine.welike.base.dao.welike.User user : l) {
                if (user.getAtTime() > 0) {
                    User contact = new User();
                    contact.setUid(user.getUid());
                    contact.setNickName(user.getNick());
                    contact.setHeadUrl(user.getHead());
                    contact.setSex(user.getSex());
                    contact.setVip(user.getVip());
                    userList.add(contact);
                }
            }
        }
        return userList;
    }

    public synchronized List<User> searchContactsWithKeyword(String keyword) {
        if (TextUtils.isEmpty(keyword)) return null;
        if (userDao == null) return null;

        List<User> userList = new ArrayList<>();
        List<com.redefine.welike.base.dao.welike.User> l = userDao.queryBuilder().where(UserDao.Properties.Nick.like("%" + keyword + "%")).orderAsc(UserDao.Properties.Created).build().list();
        if (l != null && l.size() > 0) {
            for (com.redefine.welike.base.dao.welike.User user : l) {
                User contact = new User();
                contact.setUid(user.getUid());
                contact.setNickName(user.getNick());
                contact.setHeadUrl(user.getHead());
                contact.setSex(user.getSex());
                contact.setVip(user.getVip());
                userList.add(contact);
            }
        }
        return userList;
    }

    public User getContact(String uid) {
        if (userDao == null) return null;

        com.redefine.welike.base.dao.welike.User user;
        synchronized (this) {
            user = userDao.queryBuilder().where(UserDao.Properties.Uid.eq(uid)).build().unique();
        }
        if (user == null) {
            return null;
        }
        User contact = new User();
        contact.setUid(user.getUid());
        contact.setNickName(user.getNick());
        contact.setHeadUrl(user.getHead());
        contact.setSex(user.getSex());
        contact.setVip(user.getVip());
        return contact;
    }

    public void updateAll(List<User> allContacts) {
        if (userDao == null) return;

        contacts.clear();
        if (CollectionUtil.isEmpty(allContacts)) {
            return ;
        }
        contacts.addAll(allContacts);
        if (contacts.size() > 0) {
            final List<com.redefine.welike.base.dao.welike.User> users = new ArrayList<>();
            synchronized (this) {
                for (User contact : contacts) {
                    com.redefine.welike.base.dao.welike.User user = new com.redefine.welike.base.dao.welike.User();
                    user.setUid(contact.getUid());
                    user.setNick(contact.getNickName());
                    user.setHead(contact.getHeadUrl());
                    user.setSex(contact.getSex());
                    user.setCreated(contact.getCreatedTime());
                    user.setAtTime(0L);
                    user.setVip(contact.getVip());
                    users.add(user);
                }
            }
            daoSession.runInTx(new Runnable() {

                @Override
                public void run() {
                    updateAllContactsInDB(users);
                }

            });
        }
    }

    public void insertContact(User contact) {
        synchronized (this) {
            contacts.add(contact);
        }
        com.redefine.welike.base.dao.welike.User user = new com.redefine.welike.base.dao.welike.User();
        user.setUid(contact.getUid());
        user.setNick(contact.getNickName());
        user.setHead(contact.getHeadUrl());
        user.setSex(contact.getSex());
        user.setCreated(contact.getCreatedTime());
        user.setVip(contact.getVip());
        user.setAtTime(0L);

        insertContactInDB(user);
    }

    public void modifyContact(User contact) {
        boolean has = false;
        synchronized (this) {
            for (User c : contacts) {
                if (TextUtils.equals(c.getUid(), contact.getUid())) {
                    c.setNickName(contact.getNickName());
                    c.setHeadUrl(contact.getHeadUrl());
                    c.setPostsCount(contact.getPostsCount());
                    c.setFollowUsersCount(contact.getFollowUsersCount());
                    c.setFollowedUsersCount(contact.getFollowedUsersCount());
                    c.setVip(contact.getVip());
                    has = true;
                    break;
                }
            }
        }

        if (has) {
            modifyContactInDB(contact);
        }
    }

    public synchronized void atContact(User contact) {
        if (userDao == null) return;

        com.redefine.welike.base.dao.welike.User dbUser = userDao.queryBuilder().where(UserDao.Properties.Uid.eq(contact.getUid())).build().unique();
        if (dbUser != null) {
            dbUser.setAtTime(new Date().getTime());
            try {
                userDao.update(dbUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeContact(String uid) {
        User contact = null;
        int idx = -1;
        synchronized (this) {
            for (int i = 0; i < contacts.size(); i++) {
                User c = contacts.get(i);
                if (TextUtils.equals(c.getUid(), uid)) {
                    contact = c;
                    idx = i;
                    break;
                }
            }
            if (idx >= 0) {
                contacts.remove(idx);
            }
        }

        if (contact != null) {
            removeContactInDB(contact.getUid());
        }
    }

    public Map<String, User> getUsersStatus(Set<String> uids) {
        if (uids != null && uids.size() > 0) {
            Map<String, User> usersMap = new HashMap<>();
            synchronized (this) {
                for (User c : contacts) {
                    if (containInUids(c.getUid(), uids)) {
                        usersMap.put(c.getUid(), c);
                    }
                }
            }
            return usersMap;
        }
        return null;
    }

    public synchronized boolean hasContact(String uid) {
        try {
            long count = userDao.queryBuilder().where(UserDao.Properties.Uid.eq(uid)).buildCount().count();
            return (count > 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized void clear() {
        if (userDao == null) return;

        try {
            userDao.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        contacts.clear();
    }

    private boolean containInUids(String uid, Set<String> uids) {
        for (String u : uids) {
            if (TextUtils.equals(uid, u)) {
                return true;
            }
        }
        return false;
    }

    private synchronized void updateAllContactsInDB(final List<com.redefine.welike.base.dao.welike.User> users) {
        userDao.deleteAll();
        for (com.redefine.welike.base.dao.welike.User user : users) {
            try {
                userDao.insert(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void insertContactInDB(com.redefine.welike.base.dao.welike.User user) {
        if (userDao == null) return;
        try {
            userDao.insert(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void removeContactInDB(String uid) {
        if (userDao == null) return;
        try {
            userDao.queryBuilder().where(UserDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void modifyContactInDB(User user) {
        if (userDao == null) return;

        com.redefine.welike.base.dao.welike.User dbUser = userDao.queryBuilder().where(UserDao.Properties.Uid.eq(user.getUid())).build().unique();
        if (dbUser != null) {
            dbUser.setNick(user.getNickName());
            dbUser.setHead(user.getHeadUrl());
            dbUser.setSex(user.getSex());
        } else {
            dbUser = new com.redefine.welike.base.dao.welike.User();
            dbUser.setUid(user.getUid());
            dbUser.setNick(user.getNickName());
            dbUser.setHead(user.getHeadUrl());
            dbUser.setSex(user.getSex());
            dbUser.setCreated(user.getCreatedTime());
            dbUser.setAtTime(0L);
            dbUser.setVip(user.getVip());
        }
        try {
            userDao.insertOrReplace(dbUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
