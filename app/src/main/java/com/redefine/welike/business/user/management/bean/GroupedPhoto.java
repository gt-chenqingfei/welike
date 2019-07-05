package com.redefine.welike.business.user.management.bean;

import android.support.annotation.NonNull;

import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;

import java.util.List;

/**
 * Created by nianguowang on 2018/10/13
 */
public class GroupedPhoto implements Comparable{

    private int year;
    private int month;
    private String showTime;
    private List<AttachmentBase> attachments;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public List<AttachmentBase> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentBase> attachments) {
        this.attachments = attachments;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        GroupedPhoto obj = (GroupedPhoto) o;
        return obj.year - this.year == 0 ? obj.month - this.month : obj.year - this.year;
    }
}
