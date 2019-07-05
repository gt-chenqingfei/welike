package com.redefine.welike.statistical.manager;

import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/7/30
 */
public enum PostStatusEventManager {
    INSTANCE;

    private int from;
    private int buttontype;
    private int textchanged;
    private String text;
    private String imageid;
    private String categoryId;
    private String categoryName;

    public void setFrom(int from) {
        this.from = from;
    }

    public void setButtontype(int buttontype) {
        this.buttontype = buttontype;
    }

    public void setTextchanged(int textchanged) {
        this.textchanged = textchanged;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageid(String imageUrl) {
        this.imageid = imageUrl;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTextchanged() {
        return textchanged;
    }

    public String getText() {
        return text;
    }

    public String getImageid() {
        return imageid;
    }

    public void report1() {
        EventLog.PostStatus.report1();
    }

    public void report2() {
        EventLog.PostStatus.report2(from);
    }

    public void report3() {
        EventLog.PostStatus.report3(buttontype);
    }

    public void report4() {
        EventLog.PostStatus.report4(textchanged, text, imageid, categoryId, categoryName);
    }

    public void report5() {
        EventLog.PostStatus.report5(from);
    }

    public void report6() {
        EventLog.PostStatus.report6(from);
    }

    public void report7() {
        EventLog.PostStatus.report7(from);
    }
}
