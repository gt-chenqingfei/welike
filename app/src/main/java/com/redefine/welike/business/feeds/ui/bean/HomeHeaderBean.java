package com.redefine.welike.business.feeds.ui.bean;

import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.business.assignment.management.bean.Banner;
import com.redefine.welike.business.common.mission.Mission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengnan on 2018/5/9.
 **/
public class HomeHeaderBean extends BaseHeaderBean {
    private List<Banner> mBanner = new ArrayList<>();
    private Mission mCurrentMission;
    private Mission mNewMission;

    public Mission getCurrentMission() {
        return mCurrentMission;
    }

    public void setCurrentMission(Mission mission) {
        this.mCurrentMission = mission;
    }

    public void setNewMission(Mission mission) {
        this.mNewMission = mission;
    }

    public Mission getNewMission() {
        return mNewMission;
    }

    public void setBanner(List<Banner> list) {
        mBanner.clear();
        if (!CollectionUtil.isEmpty(list)) {
            mBanner.addAll(list);
        }
    }
    public List<Banner> getBanner() {
        return mBanner;
    }

}
