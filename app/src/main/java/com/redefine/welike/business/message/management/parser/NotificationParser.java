package com.redefine.welike.business.message.management.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PicPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.parser.CommentsDataSourceParser;
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser;
import com.redefine.welike.business.message.management.bean.BusinessNotification;
import com.redefine.welike.business.message.management.bean.CommentNotification;
import com.redefine.welike.business.message.management.bean.NotificationBase;
import com.redefine.welike.business.message.management.bean.PostNotification;
import com.redefine.welike.business.message.management.bean.ReplyNotification;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR on 2018/1/26.
 */

public class NotificationParser {
    private static final String KEY_POST_CURSOR = "cursor";
    private static final String NOTIFICATION_LIST = "list";

    private static final String NOTIFICATION_ITEM_ACTION = "action";
    private static final String NOTIFICATION_ITEM_TYPE = "type";
    private static final String NOTIFICATION_ITEM_SOURCE = "source";
    private static final String NOTIFICATION_ITEM_TARGET = "target";
    private static final String NOTIFICATION_ITEM_CONTENT = "content";

    private static final String NOTIFICATION_ITEM_ID = "id";
    private static final String NOTIFICATION_ITEM_TIME = "created";


    public static String parseCursor(JSONObject result) {
        if (result != null) {
            return result.getString(KEY_POST_CURSOR);
        }
        return null;
    }

    public static List<NotificationBase> parseNotification(JSONObject result) {
        JSONArray jsonArray = result.getJSONArray(NOTIFICATION_LIST);
        if (!CollectionUtil.isEmpty(jsonArray)) {
            List<NotificationBase> list = new ArrayList<NotificationBase>();
            for (int i = 0; i < jsonArray.size(); i++) {
                NotificationBase notificationBase = null;
                try {
                    if (jsonArray.getJSONObject(i).get(NOTIFICATION_ITEM_TYPE).equals("PUSH")) {
                        notificationBase = parseBusinessNotificationImpl(jsonArray.getJSONObject(i));

                    } else {
                        notificationBase = parseNotificationImpl(jsonArray.getJSONObject(i));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (notificationBase != null) {
                    list.add(notificationBase);
                }
            }
            return list;
        }
        return null;
    }

    private static NotificationBase parseBusinessNotificationImpl(JSONObject notification) {
        NotificationBase notificationBase = null;
        String action = notification.getString(NOTIFICATION_ITEM_ACTION);
        JSONObject contentObj = null;
        int type=-1;
        String pushIcon="";
        String pushText="";
        String pushTile="";
        String forwordUrl="";
        String businessImage="";
        String batchId="";

        try {
            contentObj = notification.getJSONObject(NOTIFICATION_ITEM_CONTENT);
            type = contentObj.getInteger("pushType");
            pushIcon=contentObj.getString("pushIcon");
            pushText=contentObj.getString("pushText");
            pushTile=contentObj.getString("pushTitle");
            forwordUrl=contentObj.getString("forwardUrl");
            batchId=contentObj.getString("batchId");

            //  businessImage=contentObj.getString("");

        } catch (Exception e) {
            e.printStackTrace();


        }
        if (null != contentObj && (action.equals("201") || action.equals("202") || action.equals("203"))) {
            notificationBase = new BusinessNotification();
            ((BusinessNotification)notificationBase).setPushIcon(pushIcon);
            ((BusinessNotification)notificationBase).setType(type);
            ((BusinessNotification)notificationBase).setPushText(pushText);
            ((BusinessNotification)notificationBase).setPushTile(pushTile);
            ((BusinessNotification)notificationBase).setForwordUrl(forwordUrl);
            ((BusinessNotification)notificationBase).setBatchId(batchId);
            notificationBase.setTime(notification.getLongValue(NOTIFICATION_ITEM_TIME));
            notificationBase.setAction(notification.getString(NOTIFICATION_ITEM_ACTION));

        }




        return notificationBase;

    }

    private static NotificationBase parseNotificationImpl(JSONObject notification) {
        NotificationBase notificationBase = null;
        String action = notification.getString(NOTIFICATION_ITEM_ACTION);

        if (!checkNotificationAction(action)) return null;
        int exp = 0;
        try {
            exp = notification.getIntValue("exp");
        } catch (Exception e) {
        }
        User source = null;
        try {
            source = UserParser.parseUser(notification.getJSONObject(NOTIFICATION_ITEM_SOURCE));
        } catch (Exception e) {
            // do nothing
        }

        JSONObject contentObj = null;
        boolean like = false;
        try {
            contentObj = notification.getJSONObject(NOTIFICATION_ITEM_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (contentObj != null) {
            if (NotificationBase.isCommentNotification(action)) {
                Comment comment = CommentsDataSourceParser.parseComment(contentObj, null);
                if (comment == null) return null;
                like = comment.isLike();
                PostBase post = CommentsDataSourceParser.parsePostForComment(contentObj);
                if (post == null) return null;
                CommentNotification commentNotification = new CommentNotification();
                commentNotification.setComment(comment);
                commentNotification.setPost(post);
                commentNotification.setUrl(parseURLFromPost(post));
                notificationBase = commentNotification;
            } else if (NotificationBase.isReplyNotification(action)) {
                Comment reply = CommentsDataSourceParser.parseComment(contentObj, null);
                if (reply == null) return null;
                like = reply.isLike();
                Comment comment = CommentsDataSourceParser.parseCommentForReply(contentObj);
                PostBase post = CommentsDataSourceParser.parsePostForReply(contentObj);
                if (post == null) return null;
                ReplyNotification replyNotification = new ReplyNotification();
                replyNotification.setRelpy(reply);
                replyNotification.setComment(comment);
                replyNotification.setPost(post);
                replyNotification.setUrl(parseURLFromPost(post));
                notificationBase = replyNotification;
            } else if (NotificationBase.isPostNotification(action)) {
                PostBase post = PostsDataSourceParser.parsePostBase(contentObj);
                if (post == null) return null;
                like = post.isLike();
                PostNotification postNotification = new PostNotification();
                postNotification.setPost(post);
                postNotification.setUrl(parseURLFromPost(post));
                notificationBase = postNotification;
            }
        }

        if (notificationBase != null) {
            notificationBase.setAction(action);
            notificationBase.exp = exp;
            notificationBase.setLike(like);
            notificationBase.setNid(notification.getString(NOTIFICATION_ITEM_ID));
            notificationBase.setTime(notification.getLongValue(NOTIFICATION_ITEM_TIME));
            try {
                User target = UserParser.parseUser(notification.getJSONObject(NOTIFICATION_ITEM_TARGET));
                notificationBase.setTargetNickName(target.getNickName());
                notificationBase.setTargetUid(target.getUid());
            } catch (Exception e) {
                // do nothing
            }
            if (source != null) {
                notificationBase.setSourceHead(source.getHeadUrl());
                notificationBase.setSourceNickName(source.getNickName());
                notificationBase.setSourceUid(source.getUid());
            }
        }

        return notificationBase;
    }

    private static String parseURLFromPost(PostBase post) {
        if (post instanceof PicPost) {
            PicPost picPost = (PicPost) post;
            if (!CollectionUtil.isEmpty(picPost.listPicInfo())) {
                return picPost.getPicInfo(0).getThumbUrl();
            }
        }
        return null;
    }

    private static boolean checkNotificationAction(String action) {
        if (TextUtils.equals(action, NotificationBase.MENTION_FORWARD_ACTION)) return true;
        if (TextUtils.equals(action, NotificationBase.MENTION_POST_ACTION)) return true;
        if (TextUtils.equals(action, NotificationBase.MENTION_COMMENT_ACTION)) return true;
        if (TextUtils.equals(action, NotificationBase.MENTION_REPLY_ACTION)) return true;
        if (TextUtils.equals(action, NotificationBase.COMMENT_COMMENT_ACTION)) return true;
        if (TextUtils.equals(action, NotificationBase.COMMENT_REPLY_ACTION)) return true;
        if (TextUtils.equals(action, NotificationBase.LIKE_POST_ACTION)) return true;
        if (TextUtils.equals(action, NotificationBase.LIKE_COMMENT_ACTION)) return true;
        if (TextUtils.equals(action, NotificationBase.LIKE_REPLY_ACTION)) return true;
//        if (TextUtils.equals(action, NotificationBase.FORWARD_COMMENT)) return true;
        return false;
    }

}
