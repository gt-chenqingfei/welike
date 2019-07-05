package com.redefine.welike.business.message.management.bean;

/**
 * Created by daining on 2018/4/23.
 */

public class KMNotify {
    public String id;
    public long created;
    public String type;
    public String action;
    public KMUser source;
    public KMMagic content;
    public KMUser target;
}
