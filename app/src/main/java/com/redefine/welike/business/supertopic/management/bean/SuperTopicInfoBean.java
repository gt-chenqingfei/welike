package com.redefine.welike.business.supertopic.management.bean;

import com.redefine.commonui.loadmore.bean.BaseHeaderBean;

public class SuperTopicInfoBean extends BaseHeaderBean {
    private String info;

    public SuperTopicInfoBean(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

}
