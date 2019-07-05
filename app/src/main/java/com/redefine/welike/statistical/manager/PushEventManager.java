package com.redefine.welike.statistical.manager;

import com.redefine.welike.base.track.AFGAEventManager;
import com.redefine.welike.base.track.TrackerConstant;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by nianguowang on 2018/10/30
 */
public class PushEventManager {

    public static void onPushArrive(int pushType, String seqId, String businessType) {
        EventLog1.Push.report1(pushType, seqId, businessType);
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_PUSH_ARRIVE);
    }

    public static void onPushDisplay(int pushType, String seqId, EventLog1.Push.Result result, String businessType) {
        EventLog1.Push.report2(pushType, seqId, result, businessType);
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_PUSH_DISPLAY);
    }

    public static void onPushOpen(int pushType, String seqId, EventLog1.Push.Result result, EventLog1.Push.PushChannel channel, String businessType,String schemeUrl) {
        EventLog1.Push.report3(pushType, seqId, result, channel, businessType,schemeUrl);
        AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_PUSH_OPEN);
    }
}
