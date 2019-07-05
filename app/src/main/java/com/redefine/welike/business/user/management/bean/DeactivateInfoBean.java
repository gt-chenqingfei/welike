package com.redefine.welike.business.user.management.bean;

import java.util.List;

/**
 * Created by honglin on 2018/5/16.
 */

public class DeactivateInfoBean {

    private String detentionDesc;
    private String relationShipDesc;
    private String activeDesc;
    private List<String> followings;


    public String getDetentionDesc() {
        return detentionDesc;
    }

    public void setDetentionDesc(String detentionDesc) {
        this.detentionDesc = detentionDesc;
    }

    public String getRelationShipDesc() {
        return relationShipDesc;
    }

    public void setRelationShipDesc(String relationShipDesc) {
        this.relationShipDesc = relationShipDesc;
    }

    public String getActiveDesc() {
        return activeDesc;
    }

    public void setActiveDesc(String activeDesc) {
        this.activeDesc = activeDesc;
    }

    public List<String> getFollowings() {
        return followings;
    }

    public void setFollowings(List<String> followings) {
        this.followings = followings;
    }

    @Override
    public String toString() {
        return "DeactivateInfoBean{" +
                "detentionDesc='" + detentionDesc + '\'' +
                ", relationShipDesc='" + relationShipDesc + '\'' +
                ", activeDesc='" + activeDesc + '\'' +
                ", followings=" + followings +
                '}';
    }
}
