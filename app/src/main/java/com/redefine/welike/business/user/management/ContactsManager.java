package com.redefine.welike.business.user.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.ScheduleService;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.cache.ContactsCache;
import com.redefine.welike.business.user.management.parser.UserParser;
import com.redefine.welike.business.user.management.request.ContactsListRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by liubin on 2018/1/21.
 */

public class ContactsManager implements FollowUserManager.FollowUserCallback, UserDetailManager.UserDetailCallback {
    private ContactsCache contactsCache;
    private UserDetailManager userDetailManager;

    private static class ContactsManagerHolder {
        public static ContactsManager instance = new ContactsManager();
    }

    private ContactsManager() {
        contactsCache = new ContactsCache();
        FollowUserManager.getInstance().register(this);
    }

    public static ContactsManager getInstance() { return ContactsManagerHolder.instance; }

    private class ContactRefreshAllTask implements ScheduleService.RetryTask {

        @Override
        public boolean runTask() {
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {
                ContactsListRequest request = new ContactsListRequest(account.getUid(), MyApplication.getAppContext());
                try {
                    JSONObject result = request.req();
                    if (result != null) {
                        if (result.get(BaseRequest.ERROR_CODE_KEY) == null) {
                            List<User> contacts = UserParser.parseUsers(result);
                            contactsCache.updateAll(contacts);
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    public void refreshAll() {
        if (contactsCache.contactsCount() == 0) {
            ScheduleService.getInstance().startRetry(new ContactRefreshAllTask(), 5, TimeUnit.SECONDS);
        } else {
            contactsCache.load();
        }
    }

    public void modifyContact(User contact) {
        if (contact != null) {
            contactsCache.modifyContact(contact);
        }
    }

    public void atContact(User contact) {
        if (contact != null) {
            contactsCache.atContact(contact);
        }
    }

    public List<User> listAllContacts() {
        return contactsCache.listAllContacts();
    }

    public List<User> listRecentContacts() {
        return contactsCache.listRecentContacts();
    }

    public List<User> searchContactsWithKeyword(String keyword) {
        return contactsCache.searchContactsWithKeyword(keyword);
    }

    public Map<String, User> getUsersStatus(Set<String> uids) {
        return contactsCache.getUsersStatus(uids);
    }

    public User getContact(String uid) {
        return contactsCache.getContact(uid);
    }

    public boolean hasContact(String uid) { return contactsCache.hasContact(uid); }

    public void clear() { contactsCache.clear(); }

    @Override
    public void onFollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            if (userDetailManager == null) {
                userDetailManager = new UserDetailManager();
                userDetailManager.setDetailListener(this);
            }
            userDetailManager.loadContactDetail(uid);
        }
    }

    @Override
    public void onUnfollowCompleted(String uid, int errCode) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            contactsCache.removeContact(uid);
        }
    }

    @Override
    public void onContactDetailCompleted(UserDetailManager manager, User user, int errCode) {
        if (userDetailManager == manager) {
            userDetailManager = null;
            if (user != null) {
                contactsCache.insertContact(user);
            }
        }
    }

}
