package com.redefine.welike.event;

import com.redefine.welike.base.track.LogEvent;
import com.redefine.welike.base.track.LogEventConstant;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;
import com.redefine.welike.statistical.EventLog1;
import com.redefine.welike.statistical.manager.NewShareEventManager;

public class LogEventDispatcher implements ILogDispatcher {

    @Override
    public void handleLogMessage(LogEvent event) {
        if (event.id == LogEventConstant.LOG_EVENT_VIDEO_PLAYER) {
            if (event.logs == null) {
                return ;
            }
//            EventLog.VIDEO.report2(event.logs);
            EventLog.VIDEO.report7(event.logs);
            EventLog1.Video.report7(event.logs);
        } else if (event.id == LogEventConstant.LOG_EVENT_VIDEO_DOWNLOAD) {
//            EventLog.VIDEO.report3();
//            EventLog1.Video.report3();
        } else if (event.id == LogEventConstant.LOG_EVENT_VIDEO_DOWNLOAD_SUCCESS) {
//            EventLog.VIDEO.report4();
//            EventLog1.Video.report4();
        } else if (event.id == LogEventConstant.LOG_EVENT_VIDEO_AUTO_PLAY) {
            if (event.logs == null) {
                return;
            }
            EventLog.VIDEO.report5(event.logs);
            EventLog1.Video.report5(event.logs);
        } else if (event.id == LogEventConstant.LOG_EVENT_SHARE_RESULT_SUCCESS) {
            NewShareEventManager.INSTANCE.onShareResult(EventConstants.NEW_SHARE_RESULT_SUCCESS);
        } else if (event.id == LogEventConstant.LOG_EVENT_SHARE_RESULT_FAIL) {
            NewShareEventManager.INSTANCE.onShareResult(EventConstants.NEW_SHARE_RESULT_FAIL);
        } else if (event.id == LogEventConstant.LOG_EVENT_SHARE_RESULT_UNKUNOW) {
            NewShareEventManager.INSTANCE.onShareResult(EventConstants.NEW_SHARE_RESULT_UNKNOW);
        }

    }
}
