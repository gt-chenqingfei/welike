package com.redefine.welike.business.feeds.management.bean;

import com.redefine.foundation.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2018/1/6.
 */

public class PicPost extends PostBase {

    private static final long serialVersionUID = -2472944911923895121L;
    private List<PicInfo> picInfoList;

    public PicPost() {
        type = POST_TYPE_PIC;
        picInfoList = new ArrayList<>();
    }

    public void addPicInfo(PicInfo picInfo) { picInfoList.add(picInfo); }

    public int picCount() {
        return picInfoList.size();
    }

    public List<PicInfo> listPicInfo() {
        return picInfoList;
    }

    public PicInfo getPicInfo(int pos) {
        if (pos >= 0 && pos < picInfoList.size()) {
            return picInfoList.get(pos);
        }
        return null;
    }

    public void addPicInfos(List<PicInfo> picInfoList) {
        if (CollectionUtil.isEmpty(picInfoList)) {
            return;
        }
        this.picInfoList.addAll(picInfoList);
    }
}
