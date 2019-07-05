package com.redefine.welike.business.user.management.bean;

import android.support.annotation.NonNull;

import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.feeds.management.bean.UserHonor;
import com.redefine.welike.business.user.ui.view.contactlist.Abbreviated;
import com.redefine.welike.business.user.ui.view.contactlist.ContactsUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liubin on 2018/1/6.
 */

public class User extends UserBase implements Abbreviated, Comparable<User>, Serializable {
    private static final long serialVersionUID = 8237497394902437718L;
    private boolean following;  // 我关注的
    private boolean follower;   // 关注我的
    private boolean block;
    private String mAbbreviation;
    private String mInitial;
    private long createdTime;
    private long superLikeExp;
    private int honorLevel;
    private String honorPicUrl;
    // 用户等级：0 普通用户，1 Pro用户，2 TopUser
    private List<UserHonor> userHonors;
    private List<Intrest> mInfluences;

    public void setNickName(String nickName) {
        super.setNickName(nickName);
        this.mAbbreviation = ContactsUtils.getAbbreviation(nickName);
        this.mInitial = mAbbreviation.substring(0, 1);
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isFollower() {
        return follower;
    }

    public void setFollower(boolean follower) {
        this.follower = follower;
    }

    public long getCreatedTime() { return createdTime; }

    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }

    public long getSuperLikeExp() {
        return superLikeExp;
    }

    public void setSuperLikeExp(long superLikeExp) {
        this.superLikeExp = superLikeExp;
    }

    public int getHonorLevel() {
        return honorLevel;
    }

    public void setHonorLevel(int honorLevel) {
        this.honorLevel = honorLevel;
    }

    public String getHonorPicUrl() {
        return honorPicUrl;
    }

    public void setHonorPicUrl(String honorPicUrl) {
        this.honorPicUrl = honorPicUrl;
    }

    public List<UserHonor> getUserHonors() {
        return userHonors;
    }

    public void setUserHonors(List<UserHonor> userHonors) {
        this.userHonors = userHonors;
    }

    public List<Intrest> getInfluences() {
        return mInfluences;
    }

    public void setInfluences(List<Intrest> influences) {
        this.mInfluences = influences;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    @Override
    public String getInitial() {
        return mInitial;
    }

    @Override
    public int compareTo(@NonNull User o) {
        if (mAbbreviation.equals(o.mAbbreviation)) {
            return 0;
        }
        boolean flag;
        if ((flag = mAbbreviation.startsWith("#")) ^ o.mAbbreviation.startsWith("#")) {
            return flag ? 1 : -1;
        }
        return getInitial().compareTo(o.getInitial());
    }

    public static User convertFromAccount(Account account) {
        User user = new User();
        user.follower = false;
        user.following = false;
        user.uid = account.getUid();
        user.headUrl = account.getHeadUrl();
        user.followedUsersCount = account.getFollowedUsersCount();
        user.followUsersCount = account.getFollowUsersCount();
        user.likedMyPostsCount = account.getLikedMyPostsCount();
        user.introduction = account.getIntroduction();
        user.postsCount = account.getPostsCount();
        user.nickName = account.getNickName();
        user.sex = account.getSex();
        user.intrests = account.getIntrests();
        user.myLikedPostsCount = account.getMyLikedPostsCount();
        user.allowUpdateNickName = account.isAllowUpdateNickName();
        user.nextUpdateNickNameDate = account.getNextUpdateNickNameDate();
        user.allowUpdateSex = account.isAllowUpdateSex();
        user.sexUpdateCount = account.getSexUpdateCount();
        user.setVip(account.getVip());
        return user;
    }



}
