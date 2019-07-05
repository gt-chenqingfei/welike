package com.redefine.welike.business.feeds.management.bean;

/**
 * Created by liubin on 2018/3/12.
 */

public class SugResult {
    public static final int SUG_RESULT_TYPE_HIS = 0;
    public static final int SUG_RESULT_TYPE_SUG = 1;


    public static final int SUG_RESULT_CATEGORY_KEYWORD = 0;
    public static final int SUG_RESULT_CATEGORY_USER = 1;
    public static final int SUG_RESULT_CATEGORY_MOVIE = 2;

    private int type; // 0 history; 1 sug
    private int category; // 0 keyword; 1 user
    private Object obj;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

}
