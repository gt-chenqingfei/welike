package com.redefine.welike.event;

import com.redefine.welike.base.track.LogEvent;

/**
 * Created by liwenbo on 2018/3/16.
 */

public interface ILogDispatcher {

    void handleLogMessage(LogEvent event);
}
