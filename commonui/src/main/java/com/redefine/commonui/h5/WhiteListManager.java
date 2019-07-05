package com.redefine.commonui.h5;

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nianguowang on 2018/5/14
 */
public class WhiteListManager {

    private Set<String> mWhiteList;

    public WhiteListManager() {
        mWhiteList = new HashSet<>();
        initWhiteList();
    }

    private void initWhiteList() {
        mWhiteList.add("welike.in");
    }

    /**
     * host是否在白名单中
     * @param host
     * @return 在白名单中为true，不在白名单中为false。
     */
    public boolean inWhiteList(String host) {
        if(TextUtils.isEmpty(host)) {
            return false;
        }
        for (String s : mWhiteList) {
            if(host.endsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
