package com.redefine.welike.business.feeds.management.parser;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.redefine.richtext.RichItem;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.business.feeds.management.FeedsStatusObserver;
import com.redefine.welike.business.feeds.management.bean.ActiveAttachment;
import com.redefine.welike.business.feeds.management.bean.AdAttachment;
import com.redefine.welike.business.feeds.management.bean.ArtPost;
import com.redefine.welike.business.feeds.management.bean.ArticleInfo;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.HeaderAttachment;
import com.redefine.welike.business.feeds.management.bean.LinkPost;
import com.redefine.welike.business.feeds.management.bean.PicInfo;
import com.redefine.welike.business.feeds.management.bean.PicPost;
import com.redefine.welike.business.feeds.management.bean.PollInfo;
import com.redefine.welike.business.feeds.management.bean.PollItemInfo;
import com.redefine.welike.business.feeds.management.bean.PollParticipants;
import com.redefine.welike.business.feeds.management.bean.PollPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.TextPost;
import com.redefine.welike.business.feeds.management.bean.TopicCardInfo;
import com.redefine.welike.business.feeds.management.bean.UserHonor;
import com.redefine.welike.business.feeds.management.bean.VideoInfo;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.location.management.bean.Location;
import com.redefine.welike.business.user.management.ContactsManager;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by MR on 2018/1/20.
 */

public class PostsDataSourceParser {
    public static final String KEY_POST_ID = "id";

    /**
     * posts
     **/
    private static final String KEY_POST_POSTS_LIST = "list";
    private static final String KEY_POST_CURSOR = "cursor";
    private static final String KEY_POST_SEQUENCE_ID = "sequenceId";
    /**
     * post
     **/
    private static final String KEY_POST_CONTENT = "content";
    private static final String KEY_POST_SUMMARY = "summary";
    private static final String KEY_POST_FROM = "source";
    private static final String KEY_POST_LIKE = "liked";
    private static final String KEY_POST_FORWARD_POST_COUNT = "forwardedPostsCount";
    private static final String KEY_POST_LIKE_USER_COUNT = "likedUsersCount";
    private static final String KEY_POST_CREATE_TIME = "created";
    private static final String KEY_POST_COMMENT_COUNT = "commentsCount";
    private static final String KEY_POST_READ_COUNT = "readCount";
    private static final String KEY_POST_SHARE_COUNT = "shareCount";
    private static final String KEY_POST_DELETE = "deleted";
    private static final String KEY_POST_EXP = "exp";
    private static final String KEY_POST_LOCATION = "location";
    private static final String KEY_POST_LOCATION_PLACE_ID = "placeId";
    private static final String KEY_POST_LOCATION_PLACE_NAME = "placeName";
    private static final String KEY_POST_LOCATION_LAT = "lat";
    private static final String KEY_POST_LOCATION_LNG = "lon";
    private static final String KEY_POST_STRATEGY = "strategy";
    private static final String KEY_POST_OPERATION_TYPE = "operationType";
    private static final String KEY_POST_LANGUAGE = "language";
    private static final String KEY_POST_TAGS = "tags";
    private static final String KEY_ORIGIN = "origin";

    /**
     * forward post
     **/
    private static final String KEY_POST_FORWARD_POST = "forwardPost";
    /**
     * attachment
     **/
    private static final String KEY_POST_ATTACHMENTS = "attachments";
    private static final String KEY_POST_ATTACHMENTS_TYPE = "type";
    private static final String KEY_POST_ATTACHMENTS_SOURCE = "source";
    private static final String KEY_POST_ATTACHMENTS_ORIGINAL_IMAGE_URL = "original_image_url";
    private static final String KEY_POST_ATTACHMENTS_DOWNLOAD_URL = "download_url";
    private static final String KEY_POST_ATTACHMENTS_SITE = "site";
    private static final String KEY_POST_ATTACHMENTS_VIDEO_COVER = "icon";
    private static final String KEY_POST_ATTACHMENTS_PIC_WIDTH = "image-width";
    private static final String KEY_POST_ATTACHMENTS_VIDEO_WIDTH = "video-width";
    private static final String KEY_POST_ATTACHMENTS_PIC_HEIGHT = "image-height";
    private static final String KEY_POST_ATTACHMENTS_VIDEO_HEIGHT = "video-height";
    /**
     * poll 相关
     */
    public static final String KEY_POST_ATTACHMENTS_POOL = "poll";
    private static final String KEY_POST_ATTACHMENTS_POOL_CREATE_TIME = "created";
    public static final String KEY_POST_ATTACHMENTS_POOL_ID = "id";
    private static final String KEY_POST_ATTACHMENTS_POOL_EXPIRED = "expired";
    private static final String KEY_POST_ATTACHMENTS_POOL_EXPIRED_TIME = "expiredTime";
    private static final String KEY_POST_ATTACHMENTS_POOL_EXPIRED_POLL = "expiredPoll";
    private static final String KEY_POST_ATTACHMENTS_POOL_TOTAL_COUNT = "totalCount";
    private static final String KEY_POST_ATTACHMENTS_POOL_CHOICES = "choices";
    private static final String KEY_POST_ATTACHMENTS_POOL_ITEM_DES = "choiceName";
    private static final String KEY_POST_ATTACHMENTS_POOL_ITEM_IMG = "choiceImageUrl";
    private static final String KEY_POST_ATTACHMENTS_POOL_ITEM_COUNT = "choiceCount";
    private static final String KEY_POST_ATTACHMENTS_POOL_ITEM_SELECTED = "selected";
    private static final String KEY_POST_ATTACHMENTS_POOL_ITEM_ID = "id";
    private static final String KEY_POST_ATTACHMENTS_POOL_CHECK_OPTION = "checkOption";
    private static final String KEY_POST_ATTACHMENTS_POOL_VISIBALE_OPTION = "visibilityOption";
    private static final String KEY_POST_ATTACHMENTS_POOL_DESC = "pollDesc";
    private static final String KEY_POST_ATTACHMENTS_POOL_HOT = "hotPoll";
    private static final String KEY_POST_ATTACHMENTS_POOL_PARTICIPANT = "participants";
    private static final String KEY_POST_ATTACHMENTS_POOL_POLLED = "isPoll";//
    private static final String KEY_POST_ATTACHMENTS_POOL_USER = "user";

    /**
     * user
     **/
    private static final String KEY_POST_USER = "user";

    private static final String ATTACHMENT_RICH_TYPE_MENTION = RichItem.RICH_TYPE_MENTION;
    private static final String ATTACHMENT_RICH_TYPE_TOPIC = RichItem.RICH_TYPE_TOPIC;
    private static final String ATTACHMENT_RICH_TYPE_SUPER_TOPIC = RichItem.RICH_TYPE_SUPER_TOPIC;
    private static final String ATTACHMENT_RICH_TYPE_LINK = RichItem.RICH_TYPE_LINK;
    private static final String ATTACHMENT_RICH_TYPE_ARTICLE = RichItem.RICH_TYPE_ARTICLE;
    private static final String ATTACHMENT_RICH_TYPE_IMAGE = RichItem.RICH_TYPE_INNER_IMAGE;


    /**
     * topic card info
     **/
    private static final String KEY_POST_TOPIC_CARD = "topiccard";

    private static final String ATTACHMENT_TOPIC_CARD_URL = "url";
    private static final String ATTACHMENT_TOPIC_CARD_NAME = "name";
    private static final String ATTACHMENT_TOPIC_CARD_INTRODUCE = "introduce";
    private static final String ATTACHMENT_TOPIC_CARD_NUMS = "nums";

    /**
     * article
     **/
    private static final String KEY_POST_ARTICLE = "article";
    private static final String KEY_POST_COMMUNITY = "community";
    private static final String KEY_POST_ACTIVE = "activity";

    /**
     * reclogs 万能参数
     */

    private static final String KEY_POST_RECLOGS = "reclogs";


    public static ArrayList<PostBase> parsePosts(JSONObject result) {
        ArrayList<PostBase> posts = null;
        if (result != null) {
            JSONArray postsJSON = result.getJSONArray(KEY_POST_POSTS_LIST);
            String sequenceId = result.getString(KEY_POST_SEQUENCE_ID);
            if (postsJSON != null && postsJSON.size() > 0) {
                posts = new ArrayList<>();
                for (Iterator iterator = postsJSON.iterator(); iterator.hasNext(); ) {
                    JSONObject postJSON = (JSONObject) iterator.next();
                    try {
                        PostBase base = parsePostBase(postJSON);
                        if (base != null) {
                            base.setSequenceId(sequenceId);
                            posts.add(base);
                        }
                    } catch (Throwable throwable) {
                        // do nothing
                    }
                }
            }
        }
        return posts;
    }

    public static List<PostBase> parsePostList(JSONArray list) {
        List<PostBase> posts = null;
        if (list != null && list.size() > 0) {
            posts = new ArrayList<>();
            for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                JSONObject postJSON = (JSONObject) iterator.next();
                try {
                    PostBase base = parsePostBase(postJSON);
                    if (base != null) {
                        posts.add(base);
                    }
                } catch (Throwable throwable) {
                    // do nothing
                }
            }
        }
        return posts;
    }

    @Nullable
    public static String parseCursor(JSONObject result) {
        if (result != null) {
            return result.getString(KEY_POST_CURSOR);
        }
        return null;
    }

    public static String parseSequenceId(JSONObject result) {
        if (result != null) {
            return result.getString(KEY_POST_SEQUENCE_ID);
        }
        return null;
    }

    /**
     * 统一的feed解析入口
     *
     * @return
     */
    public static PostBase parsePostBase(JSONObject postJSON) {
        PostBase postBase = null;

        if (!postJSON.containsKey(KEY_POST_CREATE_TIME)) return null;

        List<PicInfo> picInfoList = null;
        List<RichItem> richItemList = null;
        String firstLink = null;
        String firstLinkTitle = null;
        String firstLinkIcon = null;
        String downloadUrl = null;
        String videoUrl = null;
        String videoCoverUrl = null;
        String videoSite = null;
        int videoWidth = 0;
        int videoHeight = 0;

        PollInfo pollInfo = null;
        ArticleInfo articleInfo = null;


        //new!  2018/11/06 by honlin
        AdAttachment adAttachment = null;
        HeaderAttachment headerAttachment = null;
        ActiveAttachment activeAttachment = null;
        Gson gson = new Gson();


        TopicCardInfo topicCardInfo = null;
       /* TopicCardInfo topicCardInfo=new TopicCardInfo();
        topicCardInfo.test();*/

        if (postJSON.containsKey(KEY_POST_ARTICLE)) {
            articleInfo = getArtInfo(postJSON.getJSONObject(KEY_POST_ARTICLE));

        }

        if (postJSON.containsKey(KEY_POST_COMMUNITY)) {
            topicCardInfo = getTopicCardInfo(postJSON.getJSONObject(KEY_POST_COMMUNITY));

        }


        if (postJSON.containsKey(KEY_POST_ACTIVE)) {

            try {
                activeAttachment = gson.fromJson(postJSON.getString(KEY_POST_ACTIVE), ActiveAttachment.class);

                if (System.currentTimeMillis() > activeAttachment.getEndTime()) {
                    activeAttachment = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //todo

        if (postJSON.containsKey(KEY_POST_ATTACHMENTS)) {
            // 有附件
            JSONArray attachmentsJSON = postJSON.getJSONArray(KEY_POST_ATTACHMENTS);
            for (int i = 0; i < attachmentsJSON.size(); i++) {
                JSONObject attObj = attachmentsJSON.getJSONObject(i);
                String type = attObj.getString(KEY_POST_ATTACHMENTS_TYPE);
                if (TextUtils.equals(type, GlobalConfig.ATTACHMENT_POLL_TYPE)) {
                    pollInfo = getPollInfo(attObj.getJSONObject(KEY_POST_ATTACHMENTS_POOL));
                    if (pollInfo == null) return null;

                } else if (TextUtils.equals(type, GlobalConfig.ATTACHMENT_PIC_TYPE)) {
                    // type为图片 区分
                    PicInfo picInfo = new PicInfo();
                    if (attObj.containsKey(KEY_POST_ATTACHMENTS_ORIGINAL_IMAGE_URL))
                        picInfo.setPicUrl(attObj.getString(KEY_POST_ATTACHMENTS_ORIGINAL_IMAGE_URL));
                    else
                        picInfo.setPicUrl(attObj.getString(KEY_POST_ATTACHMENTS_SOURCE));
                    picInfo.setThumbUrl(attObj.getString(KEY_POST_ATTACHMENTS_SOURCE));
                    picInfo.setWidth(attObj.getIntValue(KEY_POST_ATTACHMENTS_PIC_WIDTH));
                    picInfo.setHeight(attObj.getIntValue(KEY_POST_ATTACHMENTS_PIC_HEIGHT));
                    if (picInfoList == null) {
                        picInfoList = new ArrayList<>();
                    }
                    picInfoList.add(picInfo);
                } else if (TextUtils.equals(type, GlobalConfig.ATTACHMENT_AD_CARD)) {
                    try {
                        adAttachment = gson.fromJson(attObj.toJSONString(), AdAttachment.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (TextUtils.equals(type, GlobalConfig.ATTACHMENT_HEADER_CARD)) {
                    try {
                        headerAttachment = gson.fromJson(attObj.toJSONString(), HeaderAttachment.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (TextUtils.equals(type, GlobalConfig.ATTACHMENT_TOPIC_CARD)) {
                    // type topic card info
                   /* topicCardInfo=new TopicCardInfo();
                    topicCardInfo.setImageUrl(attObj.getString(ATTACHMENT_TOPIC_CARD_URL));
                    topicCardInfo.setTopicName(attObj.getString(ATTACHMENT_TOPIC_CARD_NAME));
                    topicCardInfo.setTopicIntroduce(attObj.getString(ATTACHMENT_TOPIC_CARD_INTRODUCE));
                    topicCardInfo.setTopicNums(attObj.getIntValue(ATTACHMENT_TOPIC_CARD_NUMS));*/


                } else if (TextUtils.equals(type, GlobalConfig.ATTACHMENT_VIDEO_TYPE)) {
                    // type为视频类型
                    videoUrl = attObj.getString(KEY_POST_ATTACHMENTS_SOURCE);
                    videoCoverUrl = attObj.getString(KEY_POST_ATTACHMENTS_VIDEO_COVER);
                    downloadUrl = attObj.getString(KEY_POST_ATTACHMENTS_DOWNLOAD_URL);
                    videoWidth = attObj.getIntValue(KEY_POST_ATTACHMENTS_VIDEO_WIDTH);
                    videoHeight = attObj.getIntValue(KEY_POST_ATTACHMENTS_VIDEO_HEIGHT);
                    if (attObj.containsKey(KEY_POST_ATTACHMENTS_SITE)) {
                        videoSite = attObj.getString(KEY_POST_ATTACHMENTS_SITE);
                    }
                } else if (TextUtils.equals(type, ATTACHMENT_RICH_TYPE_MENTION) ||
                        TextUtils.equals(type, ATTACHMENT_RICH_TYPE_TOPIC) ||
                        TextUtils.equals(type, ATTACHMENT_RICH_TYPE_LINK) ||
                        TextUtils.equals(type, ATTACHMENT_RICH_TYPE_ARTICLE) ||
                        TextUtils.equals(type, ATTACHMENT_RICH_TYPE_SUPER_TOPIC)) {
                    // type为rich
                    String source = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_SOURCE);
                    String id = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_ID);
                    int index = attObj.getIntValue(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_INDEX);
                    int length = attObj.getIntValue(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_LENGTH);
                    String target = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_TARGET);
                    String display = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_DISPLAY);
                    String title = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_TITLE);
                    String icon = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_ICON);
                    if (TextUtils.equals(type, ATTACHMENT_RICH_TYPE_LINK)) {
                        if (TextUtils.isEmpty(firstLinkTitle)) {
                            firstLinkTitle = title;
                            firstLinkIcon = icon;
                            firstLink = target;
                        }
                    }

                  /*  if (TextUtils.equals(type, ATTACHMENT_RICH_TYPE_TOPIC)) {
                        if (null != MyApplication.mTopicInfos && MyApplication.mTopicInfos.size() > 0) {
                            for (String key : MyApplication.mTopicInfos.keySet()) {
                                if (TextUtils.equals(key, id) && null == topicCardInfo) {
                                    topicCardInfo = MyApplication.mTopicInfos.get(key);
                                }
                            }
                        }
                    }*/

                    RichItem richItem = new RichItem();
                    richItem.type = type;
                    richItem.source = source;
                    richItem.id = id;
                    richItem.index = index;
                    richItem.length = length;
                    richItem.target = target;
                    if (TextUtils.equals(type, ATTACHMENT_RICH_TYPE_LINK)) {
                        if (TextUtils.isEmpty(title)) {
                            richItem.display = display;
                        } else {
                            String linkDisplay = title;
                            if (linkDisplay.length() > 32) {
                                linkDisplay = linkDisplay.substring(0, 32) + "...";
                            }
                            richItem.display = "•" + linkDisplay;
                        }
                    } else {
                        richItem.display = display;
                    }
                    richItem.title = title;
                    richItem.icon = icon;
                    if (richItemList == null) {
                        richItemList = new ArrayList<>();
                    }
                    richItemList.add(richItem);
                }
            }
        }

        if (postJSON.containsKey(KEY_POST_FORWARD_POST)) {
            // 转发post，获取子post
            JSONObject forwardJson = postJSON.getJSONObject(KEY_POST_FORWARD_POST);
            boolean isDeleted = forwardJson.getBoolean(KEY_POST_DELETE);
            if (!isDeleted) {
                PostBase child = parsePostBase(forwardJson);
                if (child != null) {
                    ForwardPost forwardPost = new ForwardPost();
                    forwardPost.setRootPost(child);
                    postBase = forwardPost;
                } else {
                    return null;
                }
            } else {
                ForwardPost forwardPost = new ForwardPost();
                forwardPost.setForwardDeleted(true);
                postBase = forwardPost;
            }
        }

        if (pollInfo != null) {
            if (postBase == null) {
                postBase = new PollPost();
            }
            if (postBase instanceof PollPost) {
                ((PollPost) postBase).mPollInfo = pollInfo;
                if (headerAttachment != null) {//add && poll
                    postBase.setHotPoll(false);
                } else
                    postBase.setHotPoll(pollInfo.hotPoll);
                postBase.setPollParticipants(pollInfo.pollParticipants);
            }
        } else if (picInfoList != null) {
            if (postBase == null) {
                postBase = new PicPost();
            }
            if (postBase instanceof PicPost) {
                PicPost picPost = (PicPost) postBase;
                for (PicInfo picInfo : picInfoList) {
                    picPost.addPicInfo(picInfo);
                }
            }
        } else if (!TextUtils.isEmpty(videoUrl)) {
            if (postBase == null) {
                postBase = new VideoPost();
            }
            if (postBase instanceof VideoPost) {
                VideoPost videoPost = (VideoPost) postBase;
                videoPost.setVideoUrl(videoUrl);
                videoPost.setCoverUrl(videoCoverUrl);
                videoPost.setDownloadUrl(downloadUrl);
                videoPost.setVideoSite(videoSite);
                videoPost.setHeight(videoHeight);
                videoPost.setWidth(videoWidth);
            }
        } else if (null != articleInfo) {
            if (postBase == null) {
                postBase = new ArtPost();
            }
        }


        if (postBase == null) {
            if (!TextUtils.isEmpty(firstLink) && !TextUtils.isEmpty(firstLinkTitle)) {
                LinkPost linkPost = new LinkPost();
                linkPost.setLinkUrl(firstLink);
                linkPost.setLinkTitle(firstLinkTitle);
                linkPost.setLinkThumbUrl(firstLinkIcon);
                postBase = linkPost;
            } else {
                postBase = new TextPost();
            }
        }


        if (postBase instanceof LinkPost) {
            if (activeAttachment != null || (null != topicCardInfo))
                postBase = new TextPost();
        }

        if (activeAttachment != null) {
            postBase.setActiveAttachment(activeAttachment);
        }

        if (adAttachment != null) {

            postBase.setAdAttachment(adAttachment);

        }

        if (headerAttachment != null) {

            postBase.setHeaderAttachment(headerAttachment);

        }


        postBase.setArticleInfo(articleInfo);

        if (postJSON.containsKey(KEY_POST_USER)) {
            JSONObject userObj = postJSON.getJSONObject(KEY_POST_USER);
            User user = UserParser.parseUser(userObj);
            postBase.setUid(user.getUid());
            postBase.setNickName(user.getNickName());
            postBase.setHeadUrl(user.getHeadUrl());
            postBase.setFollowing(user.isFollowing());
            postBase.setVip(user.getVip());
            postBase.setCurLevel(user.getCurLevel());
            postBase.setBlock(user.isBlock());
            try {
                JSONArray ja = userObj.getJSONArray("userhonors");
                if (ja != null) {
                    String jsonString = ja.toJSONString();
                    List<UserHonor> list = new Gson().fromJson(jsonString, new TypeToken<List<UserHonor>>() {
                    }.getType());
                    if (list != null) {
                        postBase.userhonors.addAll(list);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        postBase.setText(postJSON.getString(KEY_POST_CONTENT));
        postBase.setSummary(postJSON.getString(KEY_POST_SUMMARY));
        boolean hot = false;
        boolean isUserTop = false;
        try {
            hot = postJSON.getBoolean("hotPost");
        } catch (Exception e) {
        }
        try {
            isUserTop = postJSON.getBoolean("isTop");
        } catch (Exception e) {
        }
        postBase.setHot(hot);
        postBase.setTop(isUserTop);
        postBase.setRichItemList(richItemList);
        postBase.setPid(postJSON.getString(KEY_POST_ID));
        boolean like = false;
        try {
            if (postJSON.containsKey(KEY_POST_LIKE))
                like = postJSON.getBoolean(KEY_POST_LIKE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        postBase.setLike(like);

        long exp = 0;
        try {
            exp = postJSON.getLongValue(KEY_POST_EXP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        postBase.setSuperLikeExp(exp);
        boolean deleted = false;
        try {
            deleted = postJSON.getBoolean(KEY_POST_DELETE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long readCount = 0;
        try {
            readCount = postJSON.getLong(KEY_POST_READ_COUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }


        long shareCount = 0;
        try {
            shareCount = postJSON.getLong(KEY_POST_SHARE_COUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String strategy = null;
        try {
            strategy = postJSON.getString(KEY_POST_STRATEGY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String operationType = null;
        try {
            operationType = postJSON.getString(KEY_POST_OPERATION_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String language = null;
        try {
            language = postJSON.getString(KEY_POST_LANGUAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String origin = null;
        try {
            origin = postJSON.getString(KEY_ORIGIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] tags = null;
        try {
            JSONArray tagArray = postJSON.getJSONArray(KEY_POST_TAGS);
            if (tagArray != null) {
                tags = new String[tagArray.size()];
                for (int i = 0; i < tagArray.size(); i++) {
                    String tag = (String) tagArray.get(i);
                    tags[i] = tag;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String shareFrom = null;
        try {
            shareFrom = postJSON.getString("shareSource");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (postJSON.containsKey(KEY_POST_RECLOGS)) {
            postBase.setReclogs(postJSON.get(KEY_POST_RECLOGS).toString());
        }

        postBase.setShareSource(shareFrom);
        postBase.setReadCount(readCount);
        postBase.setDeleted(deleted);
        postBase.setForwardCount(postJSON.getIntValue(KEY_POST_FORWARD_POST_COUNT));
        postBase.setLikeCount(postJSON.getLongValue(KEY_POST_LIKE_USER_COUNT));
        postBase.setTime(postJSON.getLongValue(KEY_POST_CREATE_TIME));
        postBase.setCommentCount(postJSON.getIntValue(KEY_POST_COMMENT_COUNT));
        postBase.setFrom(postJSON.getString(KEY_POST_FROM));
        postBase.setStrategy(strategy);
        postBase.setOperationType(operationType);
        postBase.setLanguage(language);
        postBase.origin = origin;
        postBase.setTags(tags);
        postBase.setShareCount(shareCount);
        if (null != topicCardInfo) {
            postBase.setTopicCardInfo(topicCardInfo);
            postBase.setShowTopicCard(true);
        } else {
            postBase.setShowTopicCard(false);
        }

        if (postJSON.containsKey(KEY_POST_LOCATION)) {
            JSONObject locJSON = postJSON.getJSONObject(KEY_POST_LOCATION);
            if (locJSON != null) {
                try {
                    String placeId = locJSON.getString(KEY_POST_LOCATION_PLACE_ID);
                    String placeName = locJSON.getString(KEY_POST_LOCATION_PLACE_NAME);
                    double lat = locJSON.getDoubleValue(KEY_POST_LOCATION_LAT);
                    double lng = locJSON.getDoubleValue(KEY_POST_LOCATION_LNG);
                    Location location = new Location();
                    location.setPlaceId(placeId);
                    location.setPlace(placeName);
                    location.setLongitude(lng);
                    location.setLatitude(lat);
                    postBase.setLocation(location);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return postBase;
    }

    public static TopicCardInfo getTopicCardInfo(JSONObject jsonObject) {
        TopicCardInfo topicCardInfo = new TopicCardInfo();
        try {
            String communityName = jsonObject.getString("communityName");
            topicCardInfo.setTopicName(communityName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Integer postsCount = jsonObject.getIntValue("postsCount");
            topicCardInfo.setTopicNums(postsCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String description = jsonObject.getString("description");
            topicCardInfo.setTopicIntroduce(description);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String id = jsonObject.getString("id");
            topicCardInfo.setId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return topicCardInfo;

    }

    public static ArticleInfo getArtInfo(JSONObject jsonObject) {
        ArticleInfo articleInfo = new ArticleInfo();
        List<RichItem> richItemList = new ArrayList<>();
        JSONObject userObj = jsonObject.getJSONObject(KEY_POST_USER);
        User user = UserParser.parseUser(userObj);
        if (null != user) {
            articleInfo.setUser(user);
        }
        articleInfo.setTitle(jsonObject.getString("title"));
        articleInfo.setContent(jsonObject.getString("content"));
        articleInfo.setCover(jsonObject.getString("cover"));
        articleInfo.setCreated(jsonObject.getLong("created"));
        if (jsonObject.containsKey(KEY_POST_ATTACHMENTS)) {
            JSONArray attachmentsJSON = jsonObject.getJSONArray(KEY_POST_ATTACHMENTS);
            for (int i = 0; i < attachmentsJSON.size(); i++) {
                JSONObject attObj = attachmentsJSON.getJSONObject(i);
                String type = attObj.getString(KEY_POST_ATTACHMENTS_TYPE);

                if (TextUtils.equals(type, GlobalConfig.ATTACHMENT_VIDEO_TYPE)) {
                    // type为视频类型
                    VideoInfo videoInfo = new VideoInfo();

                    videoInfo.videoUrl = attObj.getString(KEY_POST_ATTACHMENTS_SOURCE);
                    videoInfo.coverUrl = attObj.getString(KEY_POST_ATTACHMENTS_VIDEO_COVER);
                    videoInfo.width = attObj.getIntValue(KEY_POST_ATTACHMENTS_VIDEO_WIDTH);
                    videoInfo.height = attObj.getIntValue(KEY_POST_ATTACHMENTS_VIDEO_HEIGHT);
                    if (attObj.containsKey(KEY_POST_ATTACHMENTS_SITE)) {
                        videoInfo.videoSite = attObj.getString(KEY_POST_ATTACHMENTS_SITE);

                    }
                    articleInfo.setVideoInfo(videoInfo);
                    if (TextUtils.isEmpty(articleInfo.getCover())) {
                        articleInfo.setCover(videoInfo.coverUrl);
                    }
                }


                if (TextUtils.equals(type, ATTACHMENT_RICH_TYPE_MENTION) ||
                        TextUtils.equals(type, ATTACHMENT_RICH_TYPE_TOPIC) ||
                        TextUtils.equals(type, ATTACHMENT_RICH_TYPE_LINK) ||
                        TextUtils.equals(type, ATTACHMENT_RICH_TYPE_IMAGE) ||
                        TextUtils.equals(type, ATTACHMENT_RICH_TYPE_SUPER_TOPIC)) {
                    RichItem richItem = makeArticleRichItem(attObj, type);
                    richItemList.add(richItem);

                    if (TextUtils.equals(type, ATTACHMENT_RICH_TYPE_IMAGE) && TextUtils.isEmpty(articleInfo.getCover())) {
                        articleInfo.setCover(richItem.icon);
                    }
                }


            }
            articleInfo.setRichItemList(richItemList);
        }


        return articleInfo;
    }


    private static RichItem makeArticleRichItem(JSONObject attObj, String type) {


        String source = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_SOURCE);
        String id = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_ID);
        int index = attObj.getIntValue(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_INDEX);
        int length = attObj.getIntValue(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_LENGTH);
        String target = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_TARGET);
        String display = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_DISPLAY);
        String title = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_TITLE);
        String icon = attObj.getString(RichTextContentParser.RICH_TEXT_ATTACHMENT_KEY_ICON);
        int width = attObj.getIntValue(KEY_POST_ATTACHMENTS_PIC_WIDTH);
        int height = attObj.getIntValue(KEY_POST_ATTACHMENTS_PIC_HEIGHT);

        RichItem richItem = new RichItem();
        richItem.type = type;
        richItem.source = source;
        richItem.id = id;
        richItem.index = index;
        richItem.length = length;
        richItem.target = target;
        richItem.width = width;
        richItem.height = height;
        if (TextUtils.equals(type, ATTACHMENT_RICH_TYPE_LINK)) {
            if (TextUtils.isEmpty(title)) {
                richItem.display = display;
            } else {
                String linkDisplay = title;
                if (linkDisplay.length() > 32) {
                    linkDisplay = linkDisplay.substring(0, 32) + "...";
                }
                richItem.display = linkDisplay;
            }
        } else {
            richItem.display = display;
        }
        richItem.title = title;
        richItem.icon = icon;


        return richItem;

    }

    @Nullable
    public static PollInfo getPollInfo(JSONObject pollJson) {
        PollInfo pollInfo;
        pollInfo = new PollInfo();
        pollInfo.pollId = pollJson.getString(KEY_POST_ATTACHMENTS_POOL_ID);
        pollInfo.createTime = pollJson.getLongValue(KEY_POST_ATTACHMENTS_POOL_CREATE_TIME);
        pollInfo.expired = pollJson.getLongValue(KEY_POST_ATTACHMENTS_POOL_EXPIRED);
        pollInfo.expiredTime = pollJson.getLongValue(KEY_POST_ATTACHMENTS_POOL_EXPIRED_TIME);
        pollInfo.expirePoll = pollJson.getBooleanValue(KEY_POST_ATTACHMENTS_POOL_EXPIRED_POLL);
//
        pollInfo.checkOption = pollJson.getString(KEY_POST_ATTACHMENTS_POOL_CHECK_OPTION);
        pollInfo.visibilityOption = pollJson.getString(KEY_POST_ATTACHMENTS_POOL_VISIBALE_OPTION);
        pollInfo.pollDesc = pollJson.getString(KEY_POST_ATTACHMENTS_POOL_DESC);
//        JSONObject user = pollJson.getJSONObject(KEY_POST_ATTACHMENTS_POOL_USER);
//        if (user != null) {
//            pollInfo.userBase = new User();
//            pollInfo.userBase.setUid(user.getString(UserParser.USER_ID));
//        }
        if (null != pollJson.getBoolean(KEY_POST_ATTACHMENTS_POOL_HOT)) {
            pollInfo.hotPoll = pollJson.getBoolean(KEY_POST_ATTACHMENTS_POOL_HOT);
        } else {
            pollInfo.hotPoll = false;
        }
        pollInfo.totalCount = pollJson.getIntValue(KEY_POST_ATTACHMENTS_POOL_TOTAL_COUNT);
        pollInfo.pollItemInfoList = new ArrayList<>();
        JSONArray participants = pollJson.getJSONArray(KEY_POST_ATTACHMENTS_POOL_PARTICIPANT);
        JSONArray jsonArray = pollJson.getJSONArray(KEY_POST_ATTACHMENTS_POOL_CHOICES);
        if (participants != null && participants.size() > 0) {
            for (int j = 0; j < participants.size(); j++) {
                PollParticipants pollParticipants = new PollParticipants();
                pollParticipants.id = participants.getJSONObject(j).getString("id");
                pollParticipants.name = participants.getJSONObject(j).getString("nickName");
                pollInfo.pollParticipants.add(pollParticipants);
            }

        }
        if (jsonArray == null || jsonArray.size() < GlobalConfig.MIN_POLL_ITEM_SIZE) {
            return null;
        }
        PollItemInfo info;
        JSONObject pollItemInfoJson;
        for (int j = 0; j < jsonArray.size(); j++) {
            info = new PollItemInfo();
            pollItemInfoJson = jsonArray.getJSONObject(j);
            info.pollItemText = pollItemInfoJson.getString(KEY_POST_ATTACHMENTS_POOL_ITEM_DES);
            info.pollItemPic = pollItemInfoJson.getString(KEY_POST_ATTACHMENTS_POOL_ITEM_IMG);
            if (TextUtils.isEmpty(info.pollItemPic))
                pollInfo.poolType = PollInfo.FEED_POLL_TYPE_TEXT;
            info.id = pollItemInfoJson.getString(KEY_POST_ATTACHMENTS_POOL_ITEM_ID);
            if (pollItemInfoJson.containsKey(KEY_POST_ATTACHMENTS_POOL_ITEM_COUNT)) {
                info.choiceCount = pollItemInfoJson.getIntValue(KEY_POST_ATTACHMENTS_POOL_ITEM_COUNT);
            }
            if (pollItemInfoJson.containsKey(KEY_POST_ATTACHMENTS_POOL_ITEM_SELECTED)) {
                info.isSelected = pollItemInfoJson.getBooleanValue(KEY_POST_ATTACHMENTS_POOL_ITEM_SELECTED);
                pollInfo.isPoll = info.isSelected;
            }
            pollInfo.pollItemInfoList.add(info);
        }
        return pollInfo;
    }

    public static List<PostBase> filterPosts(List<PostBase> source, List<PostBase> filter) {
        List<PostBase> target = new ArrayList<>();
        for (PostBase p : source) {
            if (!containInPostBaseList(p.getPid(), filter)) {
                target.add(p);
            }
        }
        return target;
    }

    public static List<FeedsStatusObserver.FeedStatus> parseFeedStatusList(JSONObject result) {
        List<FeedsStatusObserver.FeedStatus> feedStatusList = null;
        if (result != null) {
            JSONArray feedsStatusListJSON = result.getJSONArray(KEY_POST_POSTS_LIST);
            if (feedsStatusListJSON != null && feedsStatusListJSON.size() > 0) {
                feedStatusList = new ArrayList<>();
                for (Iterator iterator = feedsStatusListJSON.iterator(); iterator.hasNext(); ) {
                    JSONObject feedStatusJSON = (JSONObject) iterator.next();
                    FeedsStatusObserver.FeedStatus status = parseFeedStatus(feedStatusJSON);
                    if (status != null) {
                        feedStatusList.add(status);
                    }
                }
            }
        }
        return feedStatusList;
    }

    public static void updateFeedsFollowStatus(List<PostBase> posts, Set<String> uids) {
        if (posts.size() > 0) {
            Map<String, User> usersMap = ContactsManager.getInstance().getUsersStatus(uids);
            for (PostBase p : posts) {
                p.setFollowing(false);
                User u1 = usersMap.get(p.getUid());
                if (u1 != null) {
                    p.setFollowing(true);
                    p.setNickName(u1.getNickName());
                    p.setHeadUrl(u1.getHeadUrl());
                }
                if (p instanceof ForwardPost) {
                    ForwardPost fpost = (ForwardPost) p;
                    PostBase rpost = fpost.getRootPost();
                    if (rpost != null && !fpost.isForwardDeleted()) {
                        rpost.setFollowing(false);
                        User u2 = usersMap.get(rpost.getUid());
                        if (u2 != null) {
                            rpost.setFollowing(true);
                            rpost.setNickName(u2.getNickName());
                            rpost.setHeadUrl(u2.getHeadUrl());
                        }
                    }
                }
            }
        }
    }

    private static FeedsStatusObserver.FeedStatus parseFeedStatus(JSONObject feedStatusJSON) {
        String pid;
        long likeCount = 0;
        int commentCount = 0;
        int forwardCount = 0;
        long exp = 0;
        boolean following = false;
        boolean liked = false;
        pid = feedStatusJSON.getString(KEY_POST_ID);
        try {
            likeCount = feedStatusJSON.getLongValue(KEY_POST_LIKE_USER_COUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            commentCount = feedStatusJSON.getIntValue(KEY_POST_COMMENT_COUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            forwardCount = feedStatusJSON.getIntValue(KEY_POST_FORWARD_POST_COUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            exp = feedStatusJSON.getLongValue(KEY_POST_EXP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (feedStatusJSON.containsKey(KEY_POST_USER)) {
            JSONObject userObj = feedStatusJSON.getJSONObject(KEY_POST_USER);
            User user = UserParser.parseUser(userObj);
            following = user.isFollowing();
        }
        try {
            liked = feedStatusJSON.getBoolean(KEY_POST_LIKE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FeedsStatusObserver.FeedStatus feedStatus = new FeedsStatusObserver.FeedStatus();
        feedStatus.setPid(pid);
        feedStatus.setLikeCount(likeCount);
        feedStatus.setCommentCount(commentCount);
        feedStatus.setForwardCount(forwardCount);
        feedStatus.setFollowing(following);
        feedStatus.setLike(liked);
        feedStatus.setSuperLikeExp(exp);

        return feedStatus;
    }

    private static boolean containInPostBaseList(String pid, List<PostBase> list) {
        for (PostBase p : list) {
            if (TextUtils.equals(pid, p.getPid())) {
                return true;
            }
        }
        return false;
    }

}
