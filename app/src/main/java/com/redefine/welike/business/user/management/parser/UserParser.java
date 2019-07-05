package com.redefine.welike.business.user.management.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.base.profile.bean.UserBase;
import com.redefine.welike.business.feeds.management.bean.UserHonor;
import com.redefine.welike.business.user.management.UserDetailManager;
import com.redefine.welike.business.user.management.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2018/1/21.
 */

public class UserParser {
    public static final String KEY_USER_LIST = "list";
    public static final String KEY_USER_CURSOR = "cursor";
    public static final String KEY_USER_SEQUENCE_ID = "sequenceId";

    public static final String USER_ID = "id";
    public static final String USER_NICK_NAME = "nickName";
    public static final String USER_FOLLOW_COUNT = "followUsersCount";
    public static final String USER_FOLLOWED_COUNT = "followedUsersCount";
    public static final String USER_LIKED_COUNT = "likedCount";
    public static final String USER_LIKE_POSTS_COUNT = "likePostsCount";
    public static final String USER_POSTS_COUNT = "postsCount";
    public static final String USER_SEX = "sex";
    public static final String USER_CREATED_TIME = "created";
    public static final String USER_HEAD = "avatarUrl";
    public static final String USER_FOLLOWING = "follow";
    public static final String USER_FOLLOWER = "followed";
    public static final String USER_BLOCK = "block";
    public static final String USER_INTRO = "introduction";
    public static final String USER_EXP = "exp";
    public static final String USER_LEVEL = "curLevel";
    public static final String USER_CHANGE_NAME_COUNT = "changeNameCount";
    public static final String USER_HONOR_LEVEL = "honorLevel";
    public static final String USER_HONOR_PICURL = "honorPicUrl";

    public static final String USER_ALLOW_UPDATE_NICKNAME = "allowUpdateNickName";
    public static final String USER_ALLOW_UPDATE_SEX = "allowUpdateSex";
    public static final String USER_NEXT_UPDATE_NICKNAME_DATE = "nextUpdateNickNameDate";
    public static final String USER_SEX_UPDATE_COUNT = "sexUpdateCount";
    public static final String USER_VIP = "vip";

    private static final String USER_ALBUM_URL = "source";
    private static final String USER_ALBUM_CONTENT = "content";
    private static final String USER_ALBUM__SOURCE_ID = "id";


    private static final String USER_INTEREST_SOURCE = "interests";
    private static final String USER_INTEREST_SOURCE_ID = "id";
    private static final String USER_INTEREST_SOURCE_NAME = "name";
    private static final String USER_INTEREST_SOURCE_SUBSET = "subset";

    private static final String USER_INFLUENCE_SOURCE = "influences";
    private static final String USER_LINKS_SOURCE = "links";
    private static final String USER_LINKS_SOURCE_ID = "linkId";
    private static final String USER_LINKS_SOURCE_TYPE = "linkType";
    private static final String USER_LINKS_SOURCE_LINK = "link";

    private static final String USER_HONOR_SOURCE = "userhonors";
    private static final String USER_HONOR_SOURCE_type = "type";
    private static final String USER_HONOR_SOURCE_HONOR_PIC = "honorPic";
    private static final String USER_HONOR_SOURCE_FORWARD_URL = "forwardUrl";

    private static final String USER_INTEREST_SOURCE_ICON = "icon";
    public static final String USER_INTEREST_STATUS = "status";

    public static List<User> parseUsers(JSONObject result) {
        List<User> users = null;
        if (result != null) {
            JSONArray usersJSON = result.getJSONArray(KEY_USER_LIST);
            String sequenceId = result.getString(KEY_USER_SEQUENCE_ID);
            users = parseToUsers(usersJSON, sequenceId);
        }
        return users;
    }

    public static List<User> parseToUsers(JSONArray listJSON, String sequenceId) {
        List<User> users = null;
        if (listJSON != null && listJSON.size() > 0) {
            users = new ArrayList<>();
            for (int i = 0; i < listJSON.size(); i++) {
                JSONObject userJSON = listJSON.getJSONObject(i);
                User user = parseUser(userJSON);
                if (user != null) {
                    user.setSequenceId(sequenceId);
                    users.add(user);
                }
            }
        }
        return users;
    }

    public static String parseCursor(JSONObject result) {
        if (result != null) {
            return result.getString(KEY_USER_CURSOR);
        }
        return null;
    }

    public static User parseUser(JSONObject userJSON) {
        if (userJSON == null) return null;

        byte sex = 0;
        String head = null;
        int followUsersCount = 0;
        int followedUsersCount = 0;
        long likedMyPostsCount = 0;
        long myLikedPostsCount = 0;
        int postsCount = 0;
        boolean following = false;
        boolean follower = false;
        boolean block = false;
        String introduction = null;
        String uid = userJSON.getString(USER_ID);
        String nickName = userJSON.getString(USER_NICK_NAME);
        String honorPicUrl = null;
        int honorLevel = 0;
        int vip = 0;
        int status = 0;
        long superLikeExp = 0;
        int curLevel = 0;
        int changeNameCount = 0;

        List<UserBase.Intrest> intrests = new ArrayList<>();
        List<UserBase.Intrest> influences = new ArrayList<>();
        List<UserHonor> userHonors = new ArrayList<>();
        List<User.Link> links = new ArrayList<>();

        try {
            followUsersCount = userJSON.getIntValue(USER_FOLLOW_COUNT);
        } catch (Exception e) {
        }
        try {
            followedUsersCount = userJSON.getIntValue(USER_FOLLOWED_COUNT);
        } catch (Exception e) {
        }
        try {
            likedMyPostsCount = userJSON.getLongValue(USER_LIKED_COUNT);
        } catch (Exception e) {
        }
        try {
            myLikedPostsCount = userJSON.getLongValue(USER_LIKE_POSTS_COUNT);
        } catch (Exception e) {
        }
        try {
            postsCount = userJSON.getIntValue(USER_POSTS_COUNT);
        } catch (Exception e) {
        }
        try {
            sex = userJSON.getByteValue(USER_SEX);
        } catch (Exception e) {
        }
        try {
            head = userJSON.getString(USER_HEAD);
        } catch (Exception e) {
        }
        try {
            following = userJSON.getBoolean(USER_FOLLOWING);
        } catch (Exception e) {
        }
        try {
            follower = userJSON.getBoolean(USER_FOLLOWER);
        } catch (Exception e) {
        }
        try {
            block = userJSON.getBoolean(USER_BLOCK);
        } catch (Exception e) {
        }
        try {
            introduction = userJSON.getString(USER_INTRO);
        } catch (Exception e) {
        }
        try {
            superLikeExp = userJSON.getLongValue(USER_EXP);
        } catch (Exception e) {
        }
        try {
            honorLevel = userJSON.getIntValue(USER_HONOR_LEVEL);
        } catch (Exception e) {
        }
        try {
            honorPicUrl = userJSON.getString(USER_HONOR_PICURL);
        } catch (Exception e) {
        }
        long createdTime = userJSON.getLongValue(USER_CREATED_TIME);

        boolean allowUpdateNickName = false;
        boolean allowUpdateSex = false;
        long nextUpdateNickNameDate = 0;
        int sexUpdateCount = 0;
        try {
            allowUpdateNickName = userJSON.getBooleanValue(USER_ALLOW_UPDATE_NICKNAME);
        } catch (Exception e) {
        }
        try {
            allowUpdateSex = userJSON.getBooleanValue(USER_ALLOW_UPDATE_SEX);
        } catch (Exception e) {
        }
        try {
            nextUpdateNickNameDate = userJSON.getLongValue(USER_NEXT_UPDATE_NICKNAME_DATE);
        } catch (Exception e) {
        }
        try {
            sexUpdateCount = userJSON.getIntValue(USER_SEX_UPDATE_COUNT);
        } catch (Exception e) {
        }
        try {
            vip = userJSON.getIntValue(USER_VIP);
        } catch (Exception e) {
        }
        try {
            status = userJSON.getIntValue(USER_INTEREST_STATUS);
        } catch (Exception e) {
        }
        try {
            curLevel = userJSON.getIntValue(USER_LEVEL);
        } catch (Exception e) {
        }
        try {
            changeNameCount = userJSON.getIntValue(USER_CHANGE_NAME_COUNT);
        } catch (Exception e) {
        }
        try {
            JSONArray jsonArray = userJSON.getJSONArray(USER_INTEREST_SOURCE);
            if(jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    UserBase.Intrest intrest = parseInterest(jo);
                    intrests.add(intrest);
                }
            }
        } catch (Exception e) {
        }

        try {
            JSONArray jsonArray = userJSON.getJSONArray(USER_INFLUENCE_SOURCE);
            if(jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    UserBase.Intrest intrest = parseInterest(jo);
                    influences.add(intrest);
                }
            }
        } catch (Exception e) {
        }

        try {
            JSONArray jsonArray = userJSON.getJSONArray(USER_HONOR_SOURCE);
            if(jsonArray != null) {
                UserHonor userHonor;
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    userHonor = new UserHonor();
                    userHonor.type = jsonObject.getIntValue(USER_HONOR_SOURCE_type);
                    userHonor.honorPic = jsonObject.getString(USER_HONOR_SOURCE_HONOR_PIC);
                    userHonor.forwardUrl = jsonObject.getString(USER_HONOR_SOURCE_FORWARD_URL);
                    userHonors.add(userHonor);
                }
            }
        } catch (Exception e) {
        }

        try {
            JSONArray jsonArray = userJSON.getJSONArray(USER_LINKS_SOURCE);
            if(jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    User.Link link = parseLink(jsonObject);
                    links.add(link);
                }
            }
        } catch (Exception e) {
        }

        User user = new User();
        user.setUid(uid);
        user.setNickName(nickName);
        user.setFollowedUsersCount(followedUsersCount);
        user.setFollowUsersCount(followUsersCount);
        user.setLikedMyPostsCount(likedMyPostsCount);
        user.setMyLikedPostsCount(myLikedPostsCount);
        user.setSex(sex);
        user.setCreatedTime(createdTime);
        user.setPostsCount(postsCount);
        user.setHeadUrl(head);
        user.setFollowing(following);
        user.setFollower(follower);
        user.setBlock(block);
        user.setIntroduction(introduction);
        user.setAllowUpdateNickName(allowUpdateNickName);
        user.setAllowUpdateSex(allowUpdateSex);
        user.setNextUpdateNickNameDate(nextUpdateNickNameDate);
        user.setSexUpdateCount(sexUpdateCount);
        user.setSuperLikeExp(superLikeExp);
        user.setVip(vip);
        user.setStatus(status);
        user.setIntrests(intrests);
        user.setInfluences(influences);
        user.setHonorLevel(honorLevel);
        user.setHonorPicUrl(honorPicUrl);
        user.setUserHonors(userHonors);
        user.setCurLevel(curLevel);
        user.setChangeNameCount(changeNameCount);
        user.setLinks(links);
        return user;
    }

    public static UserBase.Intrest parseInterest(JSONObject result) throws Exception{
        if(result == null) {
            return null;
        }
        UserBase.Intrest intrest = new UserBase.Intrest();
        intrest.setIid(result.getString(USER_INTEREST_SOURCE_ID));
        intrest.setIcon(result.getString(USER_INTEREST_SOURCE_ICON));
        intrest.setLabel(result.getString(USER_INTEREST_SOURCE_NAME));
        List<UserBase.Intrest> subInterests = null;
        JSONArray jsonArray = result.getJSONArray(USER_INTEREST_SOURCE_SUBSET);
        if(jsonArray != null && jsonArray.size() > 0) {
            subInterests = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                UserBase.Intrest subInterest = parseInterest(jsonObject);
                subInterests.add(subInterest);
            }
        }
        intrest.setSubInterest(subInterests);
        return intrest;
    }

    public static User.Link parseLink(JSONObject result) throws Exception {
        if (result == null) {
            return null;
        }
        User.Link link = new User.Link();
        link.setLinkId(result.getLongValue(USER_LINKS_SOURCE_ID));
        link.setLinkType(result.getIntValue(USER_LINKS_SOURCE_TYPE));
        link.setLink(result.getString(USER_LINKS_SOURCE_LINK));
        return link;
    }

    public static List<UserDetailManager.UserAlbumPic> parseUserAlbums(JSONObject result) {
        List<UserDetailManager.UserAlbumPic> albums = null;
        if (result != null) {
            JSONArray albumsJSON = result.getJSONArray(KEY_USER_LIST);
            albums = parseToAlbums(albumsJSON);
        }
        return albums;
    }

    private static List<UserDetailManager.UserAlbumPic> parseToAlbums(JSONArray listJSON) {
        List<UserDetailManager.UserAlbumPic> albums = null;
        if (listJSON != null && listJSON.size() > 0) {
            albums = new ArrayList<>();
            for (int i = 0; i < listJSON.size(); i++) {
                JSONObject albumJSON = listJSON.getJSONObject(i);
                UserDetailManager.UserAlbumPic album = parseAlbum(albumJSON);
                if (album != null) {
                    albums.add(album);
                }
            }
        }
        return albums;
    }

    private static UserDetailManager.UserAlbumPic parseAlbum(JSONObject albumJSON) {
        if (albumJSON == null) return null;

        String id = null;
        String url = null;
        try {
            JSONObject content = albumJSON.getJSONObject(USER_ALBUM_CONTENT);
            id = content.getString(USER_ALBUM__SOURCE_ID);
            url = albumJSON.getString(USER_ALBUM_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(url)) {
            UserDetailManager.UserAlbumPic album = new UserDetailManager.UserAlbumPic();
            album.setId(id);
            album.setThumbnail(url);
            album.setUrl(url);
            return album;
        }
        return null;
    }

}
