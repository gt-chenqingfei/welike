package com.redefine.welike.business.videoplayer.management.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.welike.business.browse.management.bean.Attachment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;
import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;
import com.redefine.welike.business.videoplayer.management.bean.ImageAttachment;
import com.redefine.welike.business.videoplayer.management.bean.VideoAttachment;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nianguowang on 2018/9/25
 */
public class AttachmentParser {

    private static final String KEY_POST_POSTS_LIST = "list";
    private static final String KEY_POST_CURSOR = "cursor";

    /**
     * attachment
     */
    private static final String KEY_POST_ATTACHMENTS_ID = "id";
    private static final String KEY_POST_ATTACHMENTS_TYPE = "type";
    private static final String KEY_POST_ATTACHMENTS_SOURCE = "source";
    private static final String KEY_POST_ATTACHMENTS_CREATE_TIME = "created";
    private static final String KEY_POST_ATTACHMENTS_DOWNLOAD_URL = "download_url";
    private static final String KEY_POST_ATTACHMENTS_SITE = "site";
    private static final String KEY_POST_ATTACHMENTS_VIDEO_COVER = "icon";
    private static final String KEY_POST_ATTACHMENTS_PIC_WIDTH = "image-width";
    private static final String KEY_POST_ATTACHMENTS_VIDEO_WIDTH = "video-width";
    private static final String KEY_POST_ATTACHMENTS_PIC_HEIGHT = "image-height";
    private static final String KEY_POST_ATTACHMENTS_VIDEO_HEIGHT = "video-height";

    /**
     * forward post
     **/
    private static final String KEY_POST_FORWARD_POST = "forwardPost";

    /**
     * post
     **/
    private static final String KEY_CONTENT = "content";
    private static final String KEY_POST_SUMMARY = "summary";
    private static final String KEY_POST_ID = "id";
    private static final String KEY_POST_FROM = "source";
    private static final String KEY_POST_LIKE = "liked";
    private static final String KEY_POST_FORWARD_POST_COUNT = "forwardedPostsCount";
    private static final String KEY_POST_CREATE_TIME = "created";
    private static final String KEY_POST_COMMENT_COUNT = "commentsCount";
    private static final String KEY_POST_SHARE_COUNT = "shareCount";
    private static final String KEY_POST_LIKE_COUNT = "likeCount";
    private static final String KEY_POST_DELETE = "deleted";
    private static final String KEY_POST_LANGUAGE = "language";
    private static final String KEY_POST_ORIGIN = "origin";
    private static final String KEY_POST_LAST_UPDATE_TIME = "lastUpdateTime";
    private static final String KEY_POST_TAGS = "tags";
    private static final String KEY_POST_COMMUNITY = "community";
    private static final String KEY_POST_COMMUNITY_ID = "id";

    /**
     * user
     **/
    private static final String KEY_POST_USER = "user";
    private static final String KEY_POST_USER_ID = "id";
    private static final String KEY_POST_USER_FOLLOW = "follow";

    public static ArrayList<AttachmentBase> parseAttachments(JSONObject result) {
        ArrayList<AttachmentBase> posts = null;
        if (result != null) {
            JSONArray postsJSON = result.getJSONArray(KEY_POST_POSTS_LIST);
            if (postsJSON != null && postsJSON.size() > 0) {
                posts = new ArrayList<>();
                for (Iterator iterator = postsJSON.iterator(); iterator.hasNext(); ) {
                    JSONObject postJSON = (JSONObject) iterator.next();
                    try {
                        AttachmentBase base = parseAttachment(postJSON);
                        if (base != null) {
                            posts.add(base);
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
        return posts;
    }

    public static String parseCursor(JSONObject result) {
        if (result != null) {
            return result.getString(KEY_POST_CURSOR);
        }
        return null;
    }

    public static AttachmentBase parseAttachment(JSONObject postJSON) {
        AttachmentBase attachmentBase = null;

        String aid = null;
        String pid = null;
        String uid = null;
        long attachmentCreatedTime = 0;
        String content = null;
        String source = null;
        long postCreatedTime = 0;
        boolean deleted = false;
        String summary = null;
        int forwardedPostsCount = 0;
        int commentsCount = 0;
        String[] tags = null;
        String language = null;
        int origin = 0;
        long lastUpdateTime = 0;
        String community = null;
        String type = null;

        boolean like = false;
        String headUrl = null;
        String nickName = null;
        boolean following = false;
        long likeCount = 0;
        long shareCount = 0;

        try {
            aid = postJSON.getString(KEY_POST_ATTACHMENTS_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            attachmentCreatedTime = postJSON.getLongValue(KEY_POST_ATTACHMENTS_CREATE_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            type = postJSON.getString(KEY_POST_ATTACHMENTS_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (postJSON.containsKey(KEY_CONTENT)) {
            JSONObject post = postJSON.getJSONObject(KEY_CONTENT);
            try {
                content = post.getString(KEY_CONTENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                source = post.getString(KEY_POST_FROM);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                pid = post.getString(KEY_POST_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                postCreatedTime = post.getLong(KEY_POST_CREATE_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                deleted = post.getBoolean(KEY_POST_DELETE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                summary = post.getString(KEY_POST_SUMMARY);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                forwardedPostsCount = post.getIntValue(KEY_POST_FORWARD_POST_COUNT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                commentsCount = post.getIntValue(KEY_POST_COMMENT_COUNT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                language = post.getString(KEY_POST_LANGUAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                origin = post.getIntValue(KEY_POST_ORIGIN);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                lastUpdateTime = post.getLongValue(KEY_POST_LAST_UPDATE_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                like = post.getBooleanValue(KEY_POST_LIKE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                likeCount = post.getLongValue(KEY_POST_LIKE_COUNT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                shareCount = post.getLongValue(KEY_POST_SHARE_COUNT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (post.containsKey(KEY_POST_USER)) {
                JSONObject userObj = post.getJSONObject(KEY_POST_USER);
                User user = UserParser.parseUser(userObj);
                uid = user.getUid();
                following = user.isFollowing();
                nickName = user.getNickName();
                headUrl = user.getHeadUrl();
            }
            if (post.containsKey(KEY_POST_TAGS)) {
                try {
                    JSONArray jsonArray = post.getJSONArray(KEY_POST_TAGS);
                    tags = new String[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        tags[i] = jsonArray.getString(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (post.containsKey(KEY_POST_COMMUNITY)) {
                JSONObject jsonCommunity = post.getJSONObject(KEY_POST_COMMUNITY);
                try {
                    community = jsonCommunity.getString(KEY_POST_COMMUNITY_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (TextUtils.equals(type, AttachmentBase.ATTACHMENT_TYPE_VIDEO)) {
            if (attachmentBase == null) {
                attachmentBase = new VideoAttachment();
            }
            if (attachmentBase instanceof VideoAttachment) {
                VideoAttachment videoAttachment = (VideoAttachment) attachmentBase;
                String videoUrl = null;
                try {
                    videoUrl = postJSON.getString(KEY_POST_ATTACHMENTS_SOURCE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String coverUrl = null;
                try {
                    coverUrl = postJSON.getString(KEY_POST_ATTACHMENTS_VIDEO_COVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String downloadUrl = null;
                try {
                    downloadUrl = postJSON.getString(KEY_POST_ATTACHMENTS_DOWNLOAD_URL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String videoWidth = null;
                try {
                    videoWidth = postJSON.getString(KEY_POST_ATTACHMENTS_VIDEO_WIDTH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String videoHeight = null;
                try {
                    videoHeight = postJSON.getString(KEY_POST_ATTACHMENTS_VIDEO_HEIGHT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                videoAttachment.setCoverUrl(coverUrl);
                videoAttachment.setVideoUrl(videoUrl);
                videoAttachment.setDownloadUrl(downloadUrl);
                videoAttachment.setVideoWidth(videoWidth);
                videoAttachment.setVideoHeight(videoHeight);
            }
        } else if (TextUtils.equals(type, AttachmentBase.ATTACHMENT_TYPE_IMAGE)) {
            if (attachmentBase == null) {
                attachmentBase = new ImageAttachment();
            }
            if (attachmentBase instanceof ImageAttachment) {
                ImageAttachment imageAttachment = (ImageAttachment) attachmentBase;
                String imageUrl = null;
                try {
                    imageUrl = postJSON.getString(KEY_POST_ATTACHMENTS_SOURCE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int imageWidth = 0;
                try {
                    imageWidth = postJSON.getIntValue(KEY_POST_ATTACHMENTS_PIC_WIDTH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int imageHeight = 0;
                try {
                    imageHeight = postJSON.getIntValue(KEY_POST_ATTACHMENTS_PIC_HEIGHT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageAttachment.setImageUrl(imageUrl);
                imageAttachment.setImageWidth(imageWidth);
                imageAttachment.setImageHeight(imageHeight);
            }
        }

        if (attachmentBase != null) {
            attachmentBase.setAid(aid);
            attachmentBase.setAttachmentCreatedTime(attachmentCreatedTime);
            attachmentBase.setCommentsCount(commentsCount);
            attachmentBase.setCommunity(community);
            attachmentBase.setContent(content);
            attachmentBase.setDeleted(deleted);
            attachmentBase.setForwardedPostsCount(forwardedPostsCount);
            attachmentBase.setLanguage(language);
            attachmentBase.setLastUpdateTime(lastUpdateTime);
            attachmentBase.setOrigin(origin);
            attachmentBase.setPostCreatedTime(postCreatedTime);
            attachmentBase.setPid(pid);
            attachmentBase.setSource(source);
            attachmentBase.setSummary(summary);
            attachmentBase.setTags(tags);
            attachmentBase.setUid(uid);
            attachmentBase.setLike(like);
            attachmentBase.setLikeCount(likeCount);
            attachmentBase.setShareCount(shareCount);
            attachmentBase.setFollowing(following);
            attachmentBase.setHeadUrl(headUrl);
            attachmentBase.setNickName(nickName);
        }
        return attachmentBase;
    }
}
