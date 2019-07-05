package com.redefine.welike.business.publisher.management;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.statistical.EventLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/4/25
 */
public enum PublisherEventManager {

    INSTANCE;

    private static final int KB = 1024;
    private static final int SECOND = 1000;

    private int source;
    private int main_source;
    private int page_type;
    private int exit_state;
    private int words_num;
    private int picture;
    private int video_num;
    private int web_link;
    private int web_link_type;
    private int emoji_num;
    private int at_num;
    private int at_source;
    private int also_repost;
    private int also_comment;
    private String post_id;
    private String repost_id;

    //add since v1.6.1
    private int poll_type;
    private int poll_num;
    private int poll_time = 2;
    private int topic_num;
    private int topic_source;
    private List<String> topic_id = new ArrayList<>();

    //add since v2.2.6
    private List<Float> picture_size = new ArrayList<>();
    private int picture_upload_time;
    private float video_size;
    private int video_duration;

    private int video_convert_time;
    private int video_upload_time;
    private int add_topic_type;

    public void addPicture_size(long picture_size) {
        float size = picture_size / KB;
        this.picture_size.add(size);
    }

    public String getPictrueSize() {
        return picture_size.toString();
    }


    public void setPicture_upload_time(long picture_upload_time) {
        this.picture_upload_time = (int) (picture_upload_time / SECOND);
    }

    public void setVideo_size(long video_size) {
        this.video_size = video_size / KB;
    }

    public void setVideo_duration(long video_duration) {
        this.video_duration = (int) (video_duration / SECOND);
    }

    public void setVideo_convert_time(long video_convert_time) {
        this.video_convert_time = (int) (video_convert_time / SECOND);
    }

    public void setVideo_upload_time(long video_upload_time) {
        this.video_upload_time = (int) (video_upload_time / SECOND);
    }

    public void reset() {
//        source = 0;
//        main_source = 0;
//        page_type = 0;
        exit_state = 0;
        words_num = 0;
        picture = 0;
        video_num = 0;
        web_link = 0;
        web_link_type = 0;
        emoji_num = 0;
        at_num = 0;
//        at_source = 0;
        also_repost = 0;
        also_comment = 0;
        post_id = null;
        repost_id = null;
        poll_type = 0;
        poll_num = 0;
        poll_time = 2;
        topic_num = 0;
        topic_source = 0;
        topic_id.clear();
        picture_upload_time = 0;
        video_size = 0;
        video_duration = 0;
        video_convert_time = 0;
        video_upload_time = 0;
        add_topic_type = 1;
        picture_size.clear();
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getMain_source() {
        return main_source;
    }

    public void setMain_source(int main_source) {
        this.main_source = main_source;
    }

    public int getPage_type() {
        return page_type;
    }

    public void setPage_type(int page_type) {
        this.page_type = page_type;
    }

    public int getExit_state() {
        return exit_state;
    }

    public void setExit_state(int exit_state) {
        this.exit_state = exit_state;
    }

    public int getWords_num() {
        return words_num;
    }

    public void setWords_num(int words_num) {
        this.words_num = words_num;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public int getVideo_num() {
        return video_num;
    }

    public void setVideo_num(int video_num) {
        this.video_num = video_num;
    }

    public int getWeb_link() {
        return web_link;
    }

    public void setWeb_link(int web_link) {
        this.web_link = web_link;
    }

    public int getWeb_link_type() {
        return web_link_type;
    }

    public void setWeb_link_type(int web_link_type) {
        this.web_link_type = web_link_type;
    }

    public int getEmoji_num() {
        return emoji_num;
    }

    public void addEmoji_num() {
        emoji_num++;
    }

    public void removeEmoji_num() {
        emoji_num--;
        if (emoji_num < 0) {
            emoji_num = 0;
        }
    }

    public int getAt_num() {
        return at_num;
    }

    public void setAt_num(int at_num) {
        this.at_num = at_num;
    }

    public int getAt_source() {
        return at_source;
    }

    public void setAt_source(int at_source) {
        this.at_source = at_source;
    }

    public int getAlso_repost() {
        return also_repost;
    }

    public void setAlso_repost(int also_repost) {
        this.also_repost = also_repost;
    }

    public int getAlso_comment() {
        return also_comment;
    }

    public void setAlso_comment(int also_comment) {
        this.also_comment = also_comment;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(JSONObject post) {
        try {
            JSONObject postObj = null;
            JSONObject reply = post.getJSONObject("reply");
            if (reply != null) {
                JSONObject comment = reply.getJSONObject("comment");
                if (comment != null) {
                    postObj = comment.getJSONObject("post");
                }
            } else {
                JSONObject comment = post.getJSONObject("comment");
                JSONObject post1 = post.getJSONObject("post");
                if (post1 == null) {
                    if (comment != null) {
                        postObj = comment.getJSONObject("post");
                    } else {
                        postObj = post.getJSONObject("post");
                    }
                } else {
                    postObj = post1;
                }

            }
            if (postObj != null) {
                this.post_id = postObj.getString("id");
                JSONObject forwardPost = postObj.getJSONObject("forwardPost");
                if (forwardPost != null) {
                    this.repost_id = forwardPost.getString("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("EventLog", "post_id : " + post_id + " , repost_id : " + repost_id);


//        mPublshEventModel.getProxy().report15();
        EventLog.Publish.report16(PublisherEventManager.INSTANCE.getSource(),
                main_source,
                page_type,
                exit_state,
                words_num,
                getPicture(),
                getVideo_num(),
                web_link,
                web_link_type,
                emoji_num,
                at_num,
                at_source,
                also_repost,
                also_comment,
                post_id,
                repost_id,
                poll_type,
                poll_num,
                poll_time,
                topic_num,
                topic_source,
                getTopic_id(),
                picture_size.toString(),
                picture_upload_time,
                video_size,
                video_duration,
                video_convert_time,
                video_upload_time);
    }

    public String getRepost_id() {
        return repost_id;
    }

    public void setRepost_id(String repost_id) {
        this.repost_id = repost_id;
    }

    public int getPoll_type() {
        return poll_type;
    }

    public void setPoll_type(int poll_type) {
        this.poll_type = poll_type;
    }

    public int getPoll_num() {
        return poll_num;
    }

    public void setPoll_num(int poll_num) {
        this.poll_num = poll_num;
    }

    public int getPoll_time() {
        return poll_time;
    }

    public void setPoll_time(int poll_time) {
        this.poll_time = poll_time;
    }

    public String getTopic_id() {
        return topic_id.toString();
    }

    public void addTopic_id(String topic_id) {
        this.topic_id.add(topic_id);
    }

    public void removeTopic_id(String topic_id) {
        this.topic_id.remove(topic_id);
    }

    public int getTopic_num() {
        return topic_num;
    }

    public void setTopic_num(int topic_num) {
        this.topic_num = topic_num;
    }

    public int getTopic_source() {
        return topic_source;
    }

    public void setTopic_source(int topic_source) {
        this.topic_source = topic_source;
    }

    public int getAdd_topic_type() {
        return add_topic_type;
    }

    public void setAdd_topic_type(int add_topic_type) {
        this.add_topic_type = add_topic_type;
    }

    public void report14() {
        EventLog.Publish.report14(PublisherEventManager.INSTANCE.getSource(),
                getMain_source(),
                getPage_type(),
                getExit_state(),
                getWords_num(),
                getPicture(),
                getVideo_num(),
                getWeb_link(),
                getWeb_link_type(),
                getEmoji_num(),
                getAt_num(),
                getAt_source(),
                getAlso_repost(),
                also_comment,
                null,
                null,
                poll_type,
                poll_num,
                poll_time,
                topic_num,
                topic_source,
                getTopic_id());
    }

    public void report15() {

        EventLog.Publish.report15(PublisherEventManager.INSTANCE.getSource(),
                getMain_source(),
                getPage_type(),
                getExit_state(),
                getWords_num(),
                getPicture(),
                getVideo_num(),
                getWeb_link(),
                getWeb_link_type(),
                getEmoji_num(),
                getAt_num(),
                getAt_source(),
                also_repost,
                also_comment,
                getPost_id(),
                getRepost_id(),
                poll_type,
                poll_num,
                poll_time,
                topic_num,
                topic_source,
                getTopic_id());
    }
}
