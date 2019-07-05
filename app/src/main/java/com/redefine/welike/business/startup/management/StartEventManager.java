package com.redefine.welike.business.startup.management;

import com.redefine.foundation.utils.CollectionUtil;

import java.util.List;

public class StartEventManager {

    private StartEventManager() {
    }

    private static class StartEventManagerHolder {
        private static StartEventManager INSTANCE = new StartEventManager();

        private StartEventManagerHolder() {
        }
    }

    public static StartEventManager getInstance() {
        return StartEventManagerHolder.INSTANCE;
    }

    public void reset() {
        language = null;
        login_source = 0;
        page_source = 0;
        phone_load = null;
        phone = null;
        phone_source = 0;
        SMS_check = 0;
        SMS_send = 0;
        nickname = null;
        nickname_check = 0;
        head = 0;
        photo = 0;
        vertical = 0;
        vertical_list = 0;
        following = 0;
        follow_list = 0;
        phone_location = null;
        headUrl = null;
        sex = -1;
    }

    private String language;
    private int login_source;
    private int page_source;
    private String phone_load;
    private String phone;
    private int phone_source;
    private String phone_location;
    private int SMS_send;
    private int SMS_check;
    private String nickname;
    private int nickname_check;
    private int head;
    private int photo;
    private int vertical;
    private int vertical_list;
    private int following;
    private int follow_list;
    private String headUrl ;
    private byte sex ;
    private int vidmate_page_source = 0;
    private String interestId;
    private int actionType;
    private int from_page;

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getPhone_location() {
        return phone_location;
    }

    public void setPhone_location(String phone_location) {
        this.phone_location = phone_location;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getLogin_source() {
        return login_source;
    }

    public void setLogin_source(int login_source) {
        this.login_source = login_source;
    }

    public int getPage_source() {
        return page_source;
    }

    public void setPage_source(int page_source) {
        this.page_source = page_source;
    }

    public String getPhone_load() {
        return phone_load;
    }

    public void setPhone_load(List<String> phone_load) {
        StringBuilder phoneSb = new StringBuilder();
        if (!CollectionUtil.isEmpty(phone_load)) {
            for (String s : phone_load) {
                phoneSb.append(s)
                        .append("|");
            }
        }
        this.phone_load = phoneSb.toString();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = PhoneNumberConveter.convertPhone(phone)[1];
    }

    public int getPhone_source() {
        return phone_source;
    }

    public void setPhone_source(int phone_source) {
        this.phone_source = phone_source;
    }

    public int getSMS_send() {
        return SMS_send;
    }

    public void addSMS_send() {
        SMS_send++;
    }

    public int getSMS_check() {
        return SMS_check;
    }

    public void addSMS_check() {
        SMS_check++;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getNickname_check() {
        return nickname_check;
    }

    public void addNickname_check() {
        nickname_check++;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getVertical() {
        return vertical;
    }

    public void setVertical(int vertical) {
        this.vertical = vertical;
    }

    public int getVertical_list() {
        return vertical_list;
    }

    public void setVertical_list(int vertical_list) {
        this.vertical_list = vertical_list;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollow_list() {
        return follow_list;
    }

    public void setFollow_list(int follow_list) {
        this.follow_list = follow_list;
    }

    public int getVidmate_page_source() {
        return vidmate_page_source;
    }

    public void setVidmate_page_source(int vidmate_page_source) {
        this.vidmate_page_source = vidmate_page_source;
    }

    public String getInterestId() {
        return interestId;
    }

    public void setInterestId(String interestId) {
        this.interestId = interestId;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getFrom_page() {
        return from_page;
    }

    public void setFrom_page(int from_page) {
        this.from_page = from_page;
    }
}
