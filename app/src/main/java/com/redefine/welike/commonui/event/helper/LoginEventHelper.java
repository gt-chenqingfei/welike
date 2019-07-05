package com.redefine.welike.commonui.event.helper;

import com.redefine.welike.business.browse.management.constant.BrowseConstant;
import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/11/22
 */
public class LoginEventHelper {

    public static EventLog.RegisterAndLogin.PageSource convertTypeToPageSource(int type) {
        EventLog.RegisterAndLogin.PageSource pageSource;
        switch (type) {
            case BrowseConstant.TYPE_REPOST:
                pageSource = EventLog.RegisterAndLogin.PageSource.FORWARD;
                break;
            case BrowseConstant.TYPE_COMMENT:
                pageSource = EventLog.RegisterAndLogin.PageSource.COMMENT;
                break;
            case BrowseConstant.TYPE_LIKE:
                pageSource = EventLog.RegisterAndLogin.PageSource.LIKE;
                break;
            case BrowseConstant.TYPE_MESSAGE:
                pageSource = EventLog.RegisterAndLogin.PageSource.CHAT;
                break;
            case BrowseConstant.TYPE_HEAD:
                pageSource = EventLog.RegisterAndLogin.PageSource.OTHER;
                break;
            case BrowseConstant.TYPE_POST:
                pageSource = EventLog.RegisterAndLogin.PageSource.PUBLISH;
                break;
            case BrowseConstant.TYPE_TAB:
                pageSource = EventLog.RegisterAndLogin.PageSource.OTHER;
                break;
            case BrowseConstant.TYPE_SHARE:
                pageSource = EventLog.RegisterAndLogin.PageSource.OTHER;
                break;
            case BrowseConstant.TYPE_TAG:
                pageSource = EventLog.RegisterAndLogin.PageSource.OTHER;
                break;
            case BrowseConstant.TYPE_SEND:
                pageSource = EventLog.RegisterAndLogin.PageSource.PUBLISH;
                break;
            case BrowseConstant.TYPE_UNKOWN:
                pageSource = EventLog.RegisterAndLogin.PageSource.OTHER;
                break;
            default:
                pageSource = EventLog.RegisterAndLogin.PageSource.OTHER;
                break;

        }
        return pageSource;
    }
}
