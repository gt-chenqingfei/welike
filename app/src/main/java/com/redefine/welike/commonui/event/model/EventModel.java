package com.redefine.welike.commonui.event.model;

import com.redefine.welike.commonui.event.commonenums.PostType;
import com.redefine.welike.commonui.event.commonenums.ShareChannel;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by nianguowang on 2018/11/3
 */
public class EventModel {
    public ShareChannel shareChannel;
    public EventLog1.Share.ContentType contentType;
    public PostType postType;
    public EventLog1.Share.VideoPostType videoPostType;
    public EventLog1.Share.ShareFrom shareFrom;
    public EventLog1.Share.PopPage popPage;
    public String poolCode;
    public String operationType;
    public String source;
    public String postId;
    public String postLanguage;
    public String[] postTags;

    public String postUid;
    public String rootPostId;
    public String rootPostUid;
    public String rootPostLanguage;
    public String[] rootPostTags;
    public String sequenceId;
    public String reclogs;

    public EventModel(EventLog1.Share.ContentType contentType, PostType postType,
                      EventLog1.Share.VideoPostType videoPostType, EventLog1.Share.ShareFrom shareFrom,
                      EventLog1.Share.PopPage popPage, String poolCode, String operationType, String source,
                      String postId, String postLanguage, String[] postTags, String postUid, String rootPostId,
                      String rootPostUid, String rootPostLanguage, String[] rootPostTags, String sequenceId) {

        this(contentType, postType, videoPostType, shareFrom, popPage, poolCode, operationType, source, postId, postLanguage, postTags, postUid,
                rootPostId, rootPostUid, rootPostLanguage, rootPostTags, sequenceId, null);

    }


    public EventModel(EventLog1.Share.ContentType contentType, PostType postType,
                      EventLog1.Share.VideoPostType videoPostType, EventLog1.Share.ShareFrom shareFrom,
                      EventLog1.Share.PopPage popPage, String poolCode, String operationType, String source,
                      String postId, String postLanguage, String[] postTags, String postUid, String rootPostId,
                      String rootPostUid, String rootPostLanguage, String[] rootPostTags, String sequenceId, String reclogs) {
        this.contentType = contentType;
        this.postType = postType;
        this.videoPostType = videoPostType;
        this.shareFrom = shareFrom;
        this.popPage = popPage;
        this.poolCode = poolCode;
        this.operationType = operationType;
        this.source = source;
        this.postId = postId;
        this.postLanguage = postLanguage;
        this.postTags = postTags;
        this.postUid = postUid;
        this.rootPostId = rootPostId;
        this.rootPostUid = rootPostUid;
        this.rootPostLanguage = rootPostLanguage;
        this.rootPostTags = rootPostTags;
        this.sequenceId = sequenceId;
        this.reclogs = reclogs;
    }
}
