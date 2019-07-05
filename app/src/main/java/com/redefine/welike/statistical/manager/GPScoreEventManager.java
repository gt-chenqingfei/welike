package com.redefine.welike.statistical.manager;

import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/8/24
 */
public enum GPScoreEventManager {

    INSTANCE;

    private boolean hasShowRate, hasShowFeedback;
    private int clickRateCount;
    private int rate;
    private int return_result;
    private int action_type;

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setReturn_result(int return_result) {
        this.return_result = return_result;
    }

    public void setAction_type(int action_type) {
        this.action_type = action_type;
    }

    public void onClickRate() {
        if (clickRateCount == 0) {
            report2();
            clickRateCount++;
        } else {
            report3();
        }
    }

    public void report1() {
        if (!hasShowRate) {
            EventLog.GPScore.report1(action_type);
            hasShowRate = true;
        }
    }

    public void report2() {
        EventLog.GPScore.report2(rate, action_type);
    }

    public void report3() {
        EventLog.GPScore.report3(rate, action_type);
    }

    public void report4() {
        EventLog.GPScore.report4(rate, action_type);
    }

    public void report5() {
        EventLog.GPScore.report5(return_result, action_type);
    }

    public void report6() {
        if (!hasShowFeedback) {
            EventLog.GPScore.report6(rate, action_type);
            hasShowFeedback = true;
        }
    }
}
