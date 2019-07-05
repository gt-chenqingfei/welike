package com.redefine.welike.business.mysetting.management;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengnan on 2018/6/6.
 **/
public class MutePushOptions implements Serializable{
    private static final long serialVersionUID = 8421318469277994654L;
    @SerializedName("timeZone")
    private String timeZone;
    @SerializedName("timeLimit")
    private String timeLimit;
    @SerializedName("switchs")
    private List<MutePushBean>switchs=new ArrayList<>();

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<MutePushBean> getSwitchs() {
        return switchs;
    }

    public void setSwitchs(List<MutePushBean> switchs) {
        this.switchs = switchs;
    }
}
