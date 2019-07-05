package com.redefine.welike.statistical.manager;

import com.redefine.welike.statistical.EventLog;

import java.util.List;

/**
 * Created by nianguowang on 2018/8/1
 */
public enum InterestAndRecommendCardEventManager {
    INSTANCE;

    private boolean hasShowInterest, hasShowRecommond;
    private List<String> interest_name;
    private List<String> follow_list;
    private int follow_number;
    private int portrait_click;
    private int button_type;
    private int from_page;
    private int sex;

    public void setInterest_name(List<String> interest_name) {
        this.interest_name = interest_name;
    }

    public void setFollow_list(List<String> follow_list) {
        this.follow_list = follow_list;
    }

    public void setFollow_number(int follow_number) {
        this.follow_number = follow_number;
    }

    public void addPortrait_click() {
        this.portrait_click++;
    }

    public void setButton_type(int button_type) {
        this.button_type = button_type;
    }

    public void setFrom_page(int from_page) {
        this.from_page = from_page;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void report1() {
        if (!hasShowInterest) {
            hasShowInterest = true;
            EventLog.InterestAndRecommondCard.report1(from_page);
        }
    }

    public void report2() {
        EventLog.InterestAndRecommondCard.report2(interest_name == null ? "" : interest_name.toString());
    }

    public void report3() {
        EventLog.InterestAndRecommondCard.report3(interest_name == null ? "" : interest_name.toString());
    }

    public void report4() {
        EventLog.InterestAndRecommondCard.report4(interest_name == null ? "" : interest_name.toString());
    }

    public void report5() {
        EventLog.InterestAndRecommondCard.report5(interest_name == null ? "" : interest_name.toString(),
                follow_list == null ? "" : follow_list.toString(), follow_number, portrait_click);
    }

    public void report6() {
        if (!hasShowRecommond) {
            hasShowRecommond = true;
            EventLog.InterestAndRecommondCard.report6();
        }
    }

    public void report7() {
        EventLog.InterestAndRecommondCard.report7(button_type);
    }

    public void report8() {
        EventLog.InterestAndRecommondCard.report8(sex);
    }

}
