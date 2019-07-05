package com.redefine.welike.statistical.task;

import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.statistical.EventConstants;

/**
 * Created by nianguowang on 2018/5/3
 */
public abstract class BaseStrategyRunnable implements Runnable {

    protected boolean mStop;

    public synchronized boolean isStop() {
        return mStop;
    }

    public synchronized void stop() {
        mStop = true;
    }

    /**
     * 切换到新接口之后，数据结构也变了；
     * 此方法是为了兼容老的数据结构。
     * @param jo
     */
    protected void filter(JSONObject jo) {
        if (jo == null) {
            return;
        }
        jo.remove(EventConstants.KEY_OS);
        jo.remove(EventConstants.KEY_OS_VER);
        jo.remove(EventConstants.KEY_SDK_VER);
        jo.remove(EventConstants.KEY_TZ);
        jo.remove(EventConstants.KEY_CHANNEL);
        jo.remove(EventConstants.KEY_MODEL);
        jo.remove(EventConstants.KEY_VENDOR);
        jo.remove(EventConstants.KEY_APP_KEY);
        jo.remove(EventConstants.KEY_IDFA);
        jo.remove(EventConstants.KEY_MARKET_SOURCE);
        jo.remove(EventConstants.KEY_APPFLYERS_SOURCE);
        jo.remove(EventConstants.KEY_UID_SOURCE);
        jo.remove(EventConstants.KEY_CHANNEL_SOURCE);
        jo.remove(EventConstants.KEY_IP);
        jo.remove(EventConstants.KEY_OPEN_SOURCE);
        jo.remove(EventConstants.KEY_TEST_AREA);
        jo.remove(EventConstants.KEY_LOG_EXTRA);
//        jo.remove(EventConstants.KEY_IS_LOGIN);
//        jo.remove(EventConstants.KEY_LA);
        jo.remove(EventConstants.KEY_GAID);
        jo.remove(EventConstants.KEY_ABT);
        jo.remove(EventConstants.KEY_DEVICE_ID);
        jo.remove(EventConstants.KEY_VERSION_NAME);
        jo.remove(EventConstants.KEY_VERSION_CODE);
        jo.remove(EventConstants.KEY_ISP);
        jo.remove(EventConstants.KEY_LOCALE);
        jo.remove(EventConstants.KEY_COUNTRY);
        jo.remove(EventConstants.KEY_RESOLUTION);
        jo.remove(EventConstants.KEY_DPI);
        jo.remove(EventConstants.KEY_NET);
        jo.remove(EventConstants.KEY_MAC);
    }

}
