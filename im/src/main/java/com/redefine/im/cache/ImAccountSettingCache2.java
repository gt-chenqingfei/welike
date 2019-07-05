package com.redefine.im.cache;

import android.text.TextUtils;

import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.im.BuildConfig;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.dao.im.AccountSetting;
import com.redefine.welike.base.dao.im.AccountSettingDao;
import com.redefine.welike.base.dao.im.DaoSession;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Handle this counts:
 * mention, like , comment
 */
public class ImAccountSettingCache2 extends BroadcastManagerBase {

    private AccountSettingDao settingDao;
    private DaoSession daoSession;
    public static final String SETTING_NAME = "im_setting";
    public static final String MENTION_COUNT_KEY = "mentionCountKey";
    public static final String COMMENT_COUNT_KEY = "commentCountKey";
    public static final String LIKE_COUNT_KEY = "likeCountKey";
    public static final String IM_MESSAGES_STAMP_KEY = "messagesStamp";

    private static class ImAccountSettingCacheHolder {
        public static ImAccountSettingCache2 instance = new ImAccountSettingCache2();

        private ImAccountSettingCacheHolder() {

        }
    }

    private ImAccountSettingCache2() {
        daoSession = IMMultiAccountDBStore.getInstance().getDaoSession();
        settingDao = daoSession.getAccountSettingDao();
    }

    public static ImAccountSettingCache2 getInstance() {
        return ImAccountSettingCache2.ImAccountSettingCacheHolder.instance;
    }

    public void setLikeMessageCount(final int count) {
        if(BuildConfig.DEBUG){
            return ;
        }
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                Account account = AccountManager.getInstance().getAccount();
                if (account == null || TextUtils.isEmpty(account.getUid())) {
                    return;
                }
                AccountSetting accountSetting = settingDao.queryBuilder().where(AccountSettingDao.Properties.Uid.eq(account.getUid())).build().unique();
                if (accountSetting == null) {
                    accountSetting = new AccountSetting();
                    accountSetting.setUid(account.getUid());
                }
                accountSetting.setLikeMsgUnReadCount(count);
                settingDao.insertOrReplace(accountSetting);
                broadcast(SETTING_NAME, LIKE_COUNT_KEY);
            }
        });

    }


    public int getLikeMessageCount() {
        if(BuildConfig.DEBUG){
            return 0;
        }
        Account account = AccountManager.getInstance().getAccount();
        if (account == null || TextUtils.isEmpty(account.getUid())) {
            return 0;
        }
        AccountSetting setting = settingDao.queryBuilder().where(AccountSettingDao.Properties.Uid.eq(account.getUid())).build().unique();
        if (setting != null && setting.getLikeMsgUnReadCount() != null) {
            return setting.getLikeMsgUnReadCount();
        }
        return 0;
    }

    public void setMentionMessageCount(final int count) {
        if(BuildConfig.DEBUG){
            return ;
        }
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                Account account = AccountManager.getInstance().getAccount();
                if (account == null || TextUtils.isEmpty(account.getUid())) {
                    return;
                }
                AccountSetting accountSetting = settingDao.queryBuilder().where(AccountSettingDao.Properties.Uid.eq(account.getUid())).build().unique();
                if (accountSetting == null) {
                    accountSetting = new AccountSetting();
                    accountSetting.setUid(account.getUid());
                }
                accountSetting.setMentionMsgUnReadCount(count);
                settingDao.insertOrReplace(accountSetting);
                broadcast(SETTING_NAME, MENTION_COUNT_KEY);

            }
        });

    }

    public int getMentionMessageCount() {
        if(BuildConfig.DEBUG){
            return 0;
        }
        Account account = AccountManager.getInstance().getAccount();
        if (account == null || TextUtils.isEmpty(account.getUid())) {
            return 0;
        }
        AccountSetting setting = settingDao.queryBuilder().where(AccountSettingDao.Properties.Uid.eq(account.getUid())).build().unique();
        if (setting != null && setting.getMentionMsgUnReadCount() != null) {
            return setting.getMentionMsgUnReadCount();
        }
        return 0;
    }


    public void setCommentMessageCount(final int count) {
        if(BuildConfig.DEBUG){
            return ;
        }
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                Account account = AccountManager.getInstance().getAccount();
                if (account == null || TextUtils.isEmpty(account.getUid())) {
                    return;
                }
                AccountSetting accountSetting = settingDao.queryBuilder().where(AccountSettingDao.Properties.Uid.eq(account.getUid())).build().unique();
                if (accountSetting == null) {
                    accountSetting = new AccountSetting();
                    accountSetting.setUid(account.getUid());
                }
                accountSetting.setCommentMsgUnReadCount(count);
                settingDao.insertOrReplace(accountSetting);
                broadcast(SETTING_NAME, COMMENT_COUNT_KEY);

            }
        });

    }

    public int getCommentMessageCount() {
        if(BuildConfig.DEBUG){
            return 0;
        }
        //TODO
        Account account = AccountManager.getInstance().getAccount();
        if (account == null || TextUtils.isEmpty(account.getUid())) {
            return 0;
        }
        AccountSetting setting = settingDao.queryBuilder().where(AccountSettingDao.Properties.Uid.eq(account.getUid())).build().unique();
        if (setting != null && setting.getCommentMsgUnReadCount() != null) {
            return setting.getCommentMsgUnReadCount();
        }
        return 0;
    }


    public void setImMessageStamp(final String messagesStamp) {
        if(BuildConfig.DEBUG){
            return ;
        }
        daoSession.runInTx(new Runnable() {
            @Override
            public void run() {
                Account account = AccountManager.getInstance().getAccount();
                if (account == null || TextUtils.isEmpty(account.getUid())) {
                    return;
                }
                AccountSetting accountSetting = settingDao.queryBuilder().where(AccountSettingDao.Properties.Uid.eq(account.getUid())).build().unique();
                if (accountSetting == null) {
                    accountSetting = new AccountSetting();
                    accountSetting.setUid(account.getUid());
                }
                accountSetting.setImMessageCursor(messagesStamp);
                settingDao.insertOrReplace(accountSetting);
                broadcast(SETTING_NAME, IM_MESSAGES_STAMP_KEY);
                android.os.Message message = android.os.Message.obtain();
                message.what = MessageIdConstant.MESSAGE_SYNC_ACCOUNT_PROFILE;
                message.obj = messagesStamp;
                EventBus.getDefault().post(message);
            }
        });

    }


    public void register(IAccountSettingChangeListener listener) {
        super.register(listener);
    }

    public void unregister(IAccountSettingChangeListener listener) {
        super.unregister(listener);
    }


    public String getImMessageStamp() {
//        Account account = AccountManager.getInstance().getAccount();
//        if (account == null || TextUtils.isEmpty(account.getUid())) {
//            return "0";
//        }
//        AccountSetting setting = settingDao.queryBuilder().where(AccountSettingDao.Properties.Uid.eq(account.getUid())).build().unique();
//        if (setting != null) {
//            if (TextUtils.isEmpty(setting.getImMessageCursor())) {
//                return "0";
//            }
//            return setting.getImMessageCursor();
//        }
        return "0";
    }

    public interface IAccountSettingChangeListener {

        void onSettingDataChanged(String spTypeName, String spKeyName);

    }

    private void broadcast(final String name, final String key) {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

            @Override
            public void run() {
                synchronized (listenerRefList) {
                    for (int i = 0; i < listenerRefList.size(); i++) {
                        ListenerRefExt callbackRef = listenerRefList.get(i);
                        Object l = callbackRef.getListener();
                        if (l != null && l instanceof IAccountSettingChangeListener) {
                            IAccountSettingChangeListener listener = (IAccountSettingChangeListener) l;
                            listener.onSettingDataChanged(name, key);
                        }
                    }
                }
            }

        });
    }

    public void update() {
        if(BuildConfig.DEBUG){
            return ;
        }
        daoSession = IMMultiAccountDBStore.getInstance().getDaoSession();
        settingDao = daoSession.getAccountSettingDao();
    }

}
