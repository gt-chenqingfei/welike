package com.redefine.welike.base.profile.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2018/1/24.
 */

public abstract class UserBase implements Serializable {
    private static final long serialVersionUID = 5778129403039742731L;
    public static final byte MALE = 0;
    public static final byte FEMALE = 1;

    protected String uid;
    protected String nickName;
    protected String headUrl;
    protected byte sex;  //
    protected String introduction;
    protected int postsCount;
    protected int followUsersCount;
    protected int followedUsersCount;
    protected long likedMyPostsCount;
    protected long myLikedPostsCount;
    protected boolean allowUpdateNickName;
    protected boolean allowUpdateSex;
    protected long nextUpdateNickNameDate;
    protected int sexUpdateCount;
    protected int vip;
    protected int status;//1.6.2 账号状态
    protected String sequenceId;
    // 用户等级：0 普通用户，1 Pro用户，2 TopUser
    private int curLevel;
    private int changeNameCount;
    private List<Link> mLinks;

    public static final class Link implements Serializable{
        private long linkId;
        private int linkType;
        private String link;

        public long getLinkId() {
            return linkId;
        }

        public void setLinkId(long linkId) {
            this.linkId = linkId;
        }

        public int getLinkType() {
            return linkType;
        }

        public void setLinkType(int linkType) {
            this.linkType = linkType;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    protected List<Intrest> intrests = new ArrayList<>();


    public static class Intrest implements Serializable {
        private static final long serialVersionUID = 1619101168499591321L;
        private String iid;
        private String icon;
        private String label;
        // 云控是否默认勾选
        private boolean checked;
        //for ui item select
        private boolean isSelected=false;
        private List<Intrest> subInterest;
        private boolean isShowIcon = true;

        public List<Intrest> getSubInterest() {
            return subInterest;
        }

        public void setSubInterest(List<Intrest> subInterest) {
            this.subInterest = subInterest;
        }

        public boolean isShowIcon() {
            return isShowIcon;
        }

        public void setShowIcon(boolean showIcon) {
            isShowIcon = showIcon;
        }

        public boolean getChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getIid() {
            return iid;
        }

        public void setIid(String iid) {
            this.iid = iid;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }


    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public int getFollowUsersCount() {
        return followUsersCount;
    }

    public void setFollowUsersCount(int followUsersCount) {
        this.followUsersCount = followUsersCount;
    }

    public int getFollowedUsersCount() {
        return followedUsersCount;
    }

    public void setFollowedUsersCount(int followedUsersCount) {
        this.followedUsersCount = followedUsersCount;
    }

    public long getLikedMyPostsCount() {
        return likedMyPostsCount;
    }

    public void setLikedMyPostsCount(long likedMyPostsCount) {
        this.likedMyPostsCount = likedMyPostsCount;
    }

    public long getMyLikedPostsCount() {
        return myLikedPostsCount;
    }

    public void setMyLikedPostsCount(long myLikedPostsCount) {
        this.myLikedPostsCount = myLikedPostsCount;
    }

    public boolean isAllowUpdateNickName() {
        return allowUpdateNickName;
    }

    public void setAllowUpdateNickName(boolean allowUpdateNickName) {
        this.allowUpdateNickName = allowUpdateNickName;
    }

    public boolean isAllowUpdateSex() {
        return allowUpdateSex;
    }

    public void setAllowUpdateSex(boolean allowUpdateSex) {
        this.allowUpdateSex = allowUpdateSex;
    }

    public long getNextUpdateNickNameDate() {
        return nextUpdateNickNameDate;
    }

    public void setNextUpdateNickNameDate(long nextUpdateNickNameDate) {
        this.nextUpdateNickNameDate = nextUpdateNickNameDate;
    }

    public int getSexUpdateCount() {
        return sexUpdateCount;
    }

    public void setSexUpdateCount(int sexUpdateCount) {
        this.sexUpdateCount = sexUpdateCount;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public List<Intrest> getIntrests() {
        return intrests;
    }

    public void setIntrests(List<Intrest> intrests) {
        this.intrests = intrests;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCurLevel() {
        return curLevel;
    }

    public void setCurLevel(int curLevel) {
        this.curLevel = curLevel;
    }

    public int getChangeNameCount() {
        return changeNameCount;
    }

    public void setChangeNameCount(int changeNameCount) {
        this.changeNameCount = changeNameCount;
    }

    public List<Link> getLinks() {
        return mLinks;
    }

    public void setLinks(List<Link> mLinks) {
        this.mLinks = mLinks;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }
}
