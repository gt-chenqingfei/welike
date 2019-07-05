package com.redefine.im.cache;

import android.text.TextUtils;

import com.redefine.foundation.framework.BroadcastManagerBase;
import com.redefine.im.BuildConfig;
import com.redefine.im.engine.CountManager;
import com.redefine.welike.base.constant.MessageIdConstant;
import com.redefine.welike.base.dao.im.AccountSetting;
import com.redefine.welike.base.dao.im.AccountSettingDao;
import com.redefine.welike.base.dao.im.DaoSession;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Handle this counts:
 * mention, like , comment
 */
public class ImAccountSettingCache extends BroadcastManagerBase {

    //    private AccountSettingDao settingDao;
//    private DaoSession daoSession;
    public static final String SETTING_NAME = "im_setting";
    public static final String MENTION_COUNT_KEY = "mentionCountKey";
    public static final String COMMENT_COUNT_KEY = "commentCountKey";
    public static final String LIKE_COUNT_KEY = "likeCountKey";
    public static final String IM_MESSAGES_STAMP_KEY = "messagesStamp";

    private static class ImAccountSettingCacheHolder {
        public static ImAccountSettingCache instance = new ImAccountSettingCache();

        private ImAccountSettingCacheHolder() {

        }
    }

    private ImAccountSettingCache() {
//        daoSession = IMMultiAccountDBStore.getInstance().getDaoSession();
//        settingDao = daoSession.getAccountSettingDao();
    }

    public static ImAccountSettingCache getInstance() {
        return ImAccountSettingCache.ImAccountSettingCacheHolder.instance;
    }

    public void setLikeMessageCount(final int count) {
        CountManager.INSTANCE.setCommnetCount(count, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                broadcast(SETTING_NAME, LIKE_COUNT_KEY);
                return null;
            }
        });
    }


    public int getLikeMessageCount() {
        Account account = AccountManager.getInstance().getAccount();
        if (account == null || TextUtils.isEmpty(account.getUid())) {
            return 0;
        }
        return CountManager.INSTANCE.getLikeCount();
    }

    public void setMentionMessageCount(final int count) {
        CountManager.INSTANCE.setMentionCount(count, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                broadcast(SETTING_NAME, MENTION_COUNT_KEY);
                return null;
            }
        });
    }

    public int getMentionMessageCount() {
        Account account = AccountManager.getInstance().getAccount();
        if (account == null || TextUtils.isEmpty(account.getUid())) {
            return 0;
        }
        return CountManager.INSTANCE.getMentionCount();
    }


    public void setCommentMessageCount(final int count) {
        CountManager.INSTANCE.setMentionCount(count, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                broadcast(SETTING_NAME, COMMENT_COUNT_KEY);
                return null;
            }
        });
    }

    public int getCommentMessageCount() {
        Account account = AccountManager.getInstance().getAccount();
        if (account == null || TextUtils.isEmpty(account.getUid())) {
            return 0;
        }
        return CountManager.INSTANCE.getCommnetCount();
    }


    public void setImMessageStamp(final String messagesStamp) {
    }


    public void register(IAccountSettingChangeListener listener) {
        super.register(listener);
    }

    public void unregister(IAccountSettingChangeListener listener) {
        super.unregister(listener);
    }


    public String getImMessageStamp() {
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
    }

}
