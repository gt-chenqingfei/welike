package com.redefine.welike.statistical.manager;

import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/8/21
 */
public enum UnLoginEventManager {
    INSTANCE;

    private String firstClickLanguage;

    public String getFirstClickLanguage() {
        return firstClickLanguage;
    }

    public void setFirstClickLanguage(String firstClickLanguage) {
        this.firstClickLanguage = firstClickLanguage;
    }
    public void report30() {
        EventLog.UnLogin.report30();
    }

    public void report31(int selectType) {
        EventLog.UnLogin.report31(selectType);
    }

}
