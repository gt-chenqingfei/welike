package com.redefine.welike.business.assignment.management.bean;

/**
 * Created by liubin on 2018/3/22.
 */

public class ActiveReviewLayout {
    public static final int ACTIVE_REVIEW_LAYOUT_STATE_FINISHED = 1;
    public static final int ACTIVE_REVIEW_LAYOUT_STATE_UNFINISH = 2;
    private String button;
    private String icon;
    private String title;
    private String url;
    private int actionStatus;

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(int actionStatus) {
        this.actionStatus = actionStatus;
    }
}
