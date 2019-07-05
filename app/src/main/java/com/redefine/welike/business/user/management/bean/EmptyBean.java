package com.redefine.welike.business.user.management.bean;

/**
 * Created by honglin on 2018/6/29.
 */

public class EmptyBean {

    public EmptyBean(int tye) {
        this.tye = tye;
    }

    public EmptyBean() {
    }

    public static int TYE_INFO = 0;
    public static int TYE_EMPTY = 1;

    private int tye;


    public int getTye() {
        return tye;
    }

    public void setTye(int tye) {
        this.tye = tye;
    }
}
