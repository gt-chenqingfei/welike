package com.redefine.welike.statistical.manager;

import com.redefine.welike.statistical.EventLog;

/**
 * Created by nianguowang on 2018/8/2
 */
public enum SearchEventManager {

    INSTANCE;

    private String topic_name;
    private int from_page;
    private String search_key;

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    public void setFrom_page(int from_page) {
        this.from_page = from_page;
    }

    public void setSearch_key(String search_key) {
        this.search_key = search_key;
    }

    public void report1() {
        EventLog.Search.report1();
    }

    public void report2() {
        EventLog.Search.report2(from_page, topic_name);
    }

    public void report3() {
        EventLog.Search.report3();
    }

    public void report4() {
        EventLog.Search.report4(search_key);
    }
}
