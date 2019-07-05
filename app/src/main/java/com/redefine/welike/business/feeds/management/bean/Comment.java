package com.redefine.welike.business.feeds.management.bean;

import com.redefine.richtext.RichItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2018/1/13.
 */

public class Comment implements Serializable {
    private static final long serialVersionUID = 1941271092223651878L;
    private String cid;
    private String pid;
    private String uid;
    private String nickName;
    private String head;
    private boolean following;
    private boolean follower;
    private long time;
    private String content;
    private long likeCount;
    private boolean like;
    private boolean deleted;
    private int childrenCount;
    private List<RichItem> richItemList;
    private List<Comment> children;
    private int vip;
    private int curLevel;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public boolean isFollowing() { return following; }

    public void setFollowing(boolean following) { this.following = following; }

    public boolean isFollower() { return follower; }

    public void setFollower(boolean follower) { this.follower = follower; }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public List<RichItem> getRichItemList() { return richItemList; }

    public void setRichItemList(List<RichItem> richItemList) { this.richItemList = richItemList; }

    public List<Comment> getChildren() {
        return children;
    }

    public void setChildren(List<Comment> children) {
        this.children = children;
    }

    public void addChild(Comment child) {
        if (child != null && child.getChildrenCount() == 0) {
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(child);
        }
    }

    public void addChild(int index, Comment child) {
        if (child != null && child.getChildrenCount() == 0) {
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(index, child);
        }
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getCurLevel() {
        return curLevel;
    }

    public void setCurLevel(int curLevel) {
        this.curLevel = curLevel;
    }
}
