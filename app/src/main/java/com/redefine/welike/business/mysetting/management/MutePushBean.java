package com.redefine.welike.business.mysetting.management;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mengnan on 2018/6/6.
 **/
public class MutePushBean implements Serializable{
    private static final long serialVersionUID = 2725849988017048658L;
    @SerializedName("type")
    private int type;
    @SerializedName("value")
    private String value;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
