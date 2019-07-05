package com.redefine.welike.business.feeds.management.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mengnan on 2018/5/25.
 **/
public class TopicCardInfo implements Serializable {
    private static final long serialVersionUID = 1667916077707684401L;
    @SerializedName("bannerUrl")
    public String imageUrl;
    @SerializedName("topicName")
    public String topicName;
    @SerializedName("description")
    public String topicIntroduce;
    @SerializedName("postsCount")
    public int topicNums;
    @SerializedName("id")
    public String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicIntroduce() {
        return topicIntroduce;
    }

    public void setTopicIntroduce(String topicIntroduce) {
        this.topicIntroduce = topicIntroduce;
    }

    public int getTopicNums() {
        return topicNums;
    }

    public void setTopicNums(int topicNums) {
        this.topicNums = topicNums;
    }

    public void test() {
        this.topicName = "this is a test topic";
        this.topicIntroduce = "this is a test topic introduce infothis is a test topic introduce this is a test";
        this.topicNums = 101;
        this.imageUrl = "http://img3.redocn.com/tupian/20150312/haixinghezhenzhubeikeshiliangbeijing_3937174.jpg";
    }
}
