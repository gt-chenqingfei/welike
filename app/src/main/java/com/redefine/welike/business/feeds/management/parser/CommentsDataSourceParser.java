package com.redefine.welike.business.feeds.management.parser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.richtext.RichItem;
import com.redefine.welike.business.feeds.management.bean.Comment;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.user.management.bean.User;
import com.redefine.welike.business.user.management.parser.UserParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liubin on 2018/1/22.
 */

public class CommentsDataSourceParser {
    private static final String KEY_COMMENT_COMMENTS_LIST = "list";
    private static final String KEY_COMMENT_CURSOR = "cursor";

    private static final String KEY_COMMENT_ID = "id";
    private static final String KEY_COMMENT_COMMENT = "comment";
    private static final String KEY_COMMENT_POST = "post";
    private static final String KEY_COMMENT_TIME = "created";
    private static final String KEY_COMMENT_CONTENT = "content";
    private static final String KEY_COMMENT_LIKE = "liked";
    private static final String KEY_COMMENT_DELETE = "deleted";
    private static final String KEY_COMMENT_LIKE_COUNT = "likedUsersCount";
    private static final String KEY_COMMENT_ATTACHMENTS = "attachments";
    private static final String KEY_COMMENT_REPLIES_COUNT = "repliesCount";

    private static final String KEY_COMMENT_USER = "user";

    private static final String KEY_COMMENT_REPLIES = "replies";

    public static List<Comment> parseComments(JSONObject result) {
        List<Comment> comments = null;
        if (result != null) {
            JSONArray commentsJSON = result.getJSONArray(KEY_COMMENT_COMMENTS_LIST);
            if (commentsJSON != null && commentsJSON.size() > 0) {
                comments = new ArrayList<>();
                for (Iterator iterator = commentsJSON.iterator(); iterator.hasNext();) {
                    JSONObject commentJSON = (JSONObject)iterator.next();
                    Comment comment = parseComment(commentJSON, null);
                    if (comment != null) {
                        comments.add(comment);
                    }
                }
            }
        }
        return comments;
    }

    public static String parseCursor(JSONObject result) {
        if (result != null) {
            return result.getString(KEY_COMMENT_CURSOR);
        }
        return null;
    }

    /**
     * pid需要干掉
     * @param commentJSON
     * @param pid
     * @return
     */
    public static Comment parseComment(JSONObject commentJSON, String pid) {
        String cid = commentJSON.getString(KEY_COMMENT_ID);
        String jsonPid = pid;
        if (TextUtils.isEmpty(jsonPid)) {
            JSONObject postObject = commentJSON.getJSONObject(KEY_COMMENT_POST);
            if (postObject != null) {
                jsonPid = postObject.getString(PostsDataSourceParser.KEY_POST_ID);
            } else {
                JSONObject commentObject = commentJSON.getJSONObject(KEY_COMMENT_COMMENT);
                if (commentObject != null) {
                    postObject = commentObject.getJSONObject(KEY_COMMENT_POST);
                    if (postObject != null) {
                        jsonPid = postObject.getString(PostsDataSourceParser.KEY_POST_ID);
                    }
                }
            }
        }
        long time = commentJSON.getLongValue(KEY_COMMENT_TIME);
        String content = commentJSON.getString(KEY_COMMENT_CONTENT);
        int repliesCount = 0;
        try {
            repliesCount = commentJSON.getIntValue(KEY_COMMENT_REPLIES_COUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<RichItem> richItemList = null;
        JSONArray richItemListArr = commentJSON.getJSONArray(KEY_COMMENT_ATTACHMENTS);
        if (richItemListArr != null) {
            richItemList = RichTextContentParser.parserRichJSONArray(richItemListArr);
        }

        JSONObject userJSON = commentJSON.getJSONObject(KEY_COMMENT_USER);
        User user = UserParser.parseUser(userJSON);
        String uid = user.getUid();
        String nickName = user.getNickName();
        String head = user.getHeadUrl();

        Comment comment = new Comment();
        comment.setCid(cid);
        comment.setPid(jsonPid);
        comment.setUid(uid);
        comment.setNickName(nickName);
        comment.setHead(head);
        comment.setFollowing(user.isFollowing());
        comment.setFollower(user.isFollower());
        comment.setTime(time);
        comment.setContent(content);
        comment.setLikeCount(commentJSON.getLongValue(KEY_COMMENT_LIKE_COUNT));
        boolean like = false;
        try {
            like = commentJSON.getBoolean(KEY_COMMENT_LIKE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        comment.setLike(like);
        boolean isDeleted = false;
        try {
            isDeleted = commentJSON.getBoolean(KEY_COMMENT_DELETE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        comment.setDeleted(isDeleted);
        comment.setChildrenCount(repliesCount);
        comment.setRichItemList(richItemList);
        comment.setVip(user.getVip());
        comment.setCurLevel(user.getCurLevel());

        JSONArray repliesJSON = commentJSON.getJSONArray(KEY_COMMENT_REPLIES);
        if (repliesJSON != null && repliesJSON.size() > 0) {
            for (Iterator childIterator = repliesJSON.iterator(); childIterator.hasNext();) {
                JSONObject replyJSON = (JSONObject)childIterator.next();
                Comment reply = parseComment(replyJSON, pid);
                if (reply != null) {
                    comment.addChild(reply);
                }
            }
        }
        return comment;
    }

    public static PostBase parsePostForComment(JSONObject commentJSON) {
        JSONObject postJSON = null;
        try {
            postJSON = commentJSON.getJSONObject(KEY_COMMENT_POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (postJSON != null) {
            return PostsDataSourceParser.parsePostBase(postJSON);
        }
        return null;
    }

    public static PostBase parsePostForReply(JSONObject replyJSON) {
        JSONObject postJSON = null;
        try {
            JSONObject commentJSON = replyJSON.getJSONObject(KEY_COMMENT_COMMENT);
            postJSON = commentJSON.getJSONObject(KEY_COMMENT_POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (postJSON != null) {
            return PostsDataSourceParser.parsePostBase(postJSON);
        }
        return null;
    }

    public static Comment parseCommentForReply(JSONObject replyJSON) {
        JSONObject commentJSON = null;
        try {
            commentJSON = replyJSON.getJSONObject(KEY_COMMENT_COMMENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (commentJSON != null) {
            return parseComment(commentJSON, null);
        }
        return null;
    }

}
