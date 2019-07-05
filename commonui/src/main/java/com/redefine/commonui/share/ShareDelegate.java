package com.redefine.commonui.share;

import com.redefine.foundation.utils.CollectionUtil;

import java.util.LinkedList;

/**
 * Created by nianguowang on 2018/5/22
 */
public enum  ShareDelegate {

    INSTANCE;

    public static final int CHANNEL_FACEBOOK = 1;
    public static final int CHANNEL_MORE = 2;
    public static final int CHANNEL_INSTAGRAM = 3;
    public static final int CHANNEL_WHATSAPP = 4;
    public static final int CHANNEL_COPY_LINK = 5;
    public static final int CHANNEL_CANCEL = 6;
    public static final int CHANNEL_WHATSAPP_DIRECTLY = 7;

    private LinkedList<ShareCallback> mListeners = new LinkedList<>();

    public interface ShareCallback {
        /**
         * When click share channel
         * @param channel  1 whatsapp
         *                  2 instagram
         *                  3 facebook
         *                  4 copylink
         *                  5 more
         *                  6 cancel
         *                  7 直接分享到WhatsApp
         * @param from 1 post detail
         *             2 shareapp
         *             3 webview
         *             4 profile
         *             5 Topic
         */
        void onShareToChannel(SharePackageFactory.SharePackage channel, int from);

        /**
         * when share activity show
         * @param from 1 post detail
         *             2 shareapp
         *             3 webview
         *             4 profile
         *             5 Topic
         */
        void onSharePageShow(int from);

    }

    private ShareCallback mShareCallback;

    /**
     *
     * @param from 1博文页 2分享APP 3任务页,4 个人详情,5 Topic
     */
    public void shareTo(SharePackageFactory.SharePackage channel, int from) {
        if (!CollectionUtil.isEmpty(mListeners)) {
            for (ShareCallback listener : mListeners) {
                listener.onShareToChannel(channel, from);
            }
        }
    }

    public void shareActivityShow(int from) {
        if (!CollectionUtil.isEmpty(mListeners)) {
            for (ShareCallback listener : mListeners) {
                listener.onSharePageShow(from);
            }
        }
    }

    public void registerShareCallback(ShareCallback callback) {
        if (!mListeners.contains(callback)) {
            mListeners.add(callback);
        }
    }

    public void unregisterShareCallback(ShareCallback callback) {
        mListeners.remove(callback);
    }
}
