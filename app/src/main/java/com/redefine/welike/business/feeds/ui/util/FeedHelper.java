package com.redefine.welike.business.feeds.ui.util;

import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.player.VideoStateHelper;
import com.redefine.multimedia.player.constant.PlayerConstant;
import com.redefine.welike.business.feeds.management.bean.ForwardPost;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/6.
 */

public class FeedHelper {

    private static final long MM = 1000 * 1000 * 1000;
    private static final long M = 1000 * 1000;
    private static final long K = 1000;

    public static String getForwardCount(long count) {
        return getCommonCount(count);
    }

    public static String getLikeCount(long count) {
        return getCommonCount(count);
    }

    public static String getCommentCount(long count) {
        return getCommonCount(count);
    }

    private static String getCommonCount(long count) {
        if (count >= 10 * MM) {
            return (count / MM) + "MM";
        } else if (count >= 10 * M) {
            return (count / M) + "M";
        } else if (count >= 10 * K) {
            return (count / K) + "K";
        } else {
            return count + "";
        }
    }

    public static boolean shouldShowShareMenu(PostBase postBase) {
        if (postBase == null) {
            return false;
        }
        if (postBase instanceof VideoPost && !TextUtils.equals(((VideoPost) postBase).getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
            return true;
        }
        if (postBase instanceof ForwardPost) {
            PostBase rootPost = ((ForwardPost) postBase).getRootPost();
            if (rootPost == null) {
                return false;
            }
            if (rootPost instanceof VideoPost && !TextUtils.equals(((VideoPost) rootPost).getVideoSite(), PlayerConstant.VIDEO_SITE_YOUTUBE)) {
                return true;
            }
        }
        return false;
    }

//    public static JSONArray getDataForEvent(List<PostBase> postBases) {
//        if (CollectionUtil.isEmpty(postBases)) {
//            return null;
//        }
//        JSONArray data = new JSONArray();
//        for (PostBase postBase : postBases) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("post_id", postBase.getPid());
//            jsonObject.put("la", postBase.getLanguage());
//            jsonObject.put("tags", postBase.getTags());
//            data.add(jsonObject);
//        }
//        return data;
//    }

    public static JSONArray getDataForEvent(List<PostBase> postBases) {
        if (CollectionUtil.isEmpty(postBases)) {
            return new JSONArray();
        }
        JSONArray data = new JSONArray();
        for (PostBase postBase : postBases) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("post_id", postBase.getPid());
                jsonObject.put("la", postBase.getLanguage());
                jsonObject.put("tags", VideoStateHelper.toStringArrayString(postBase.getTags()));
                data.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static List<PostBase> subPosts(List<PostBase> postBases, int subSize) {
        if (CollectionUtil.isEmpty(postBases) || subSize <= 0) {
            return new ArrayList<>();
        }

        subSize = Math.min(CollectionUtil.getCount(postBases), subSize);
        List<PostBase> subList = new ArrayList<>();
        for (int i = 0; i < subSize; i++) {
            subList.add(postBases.get(i));
        }
        return subList;
    }

    public static String getRootPostUid(PostBase postBase) {
        if (postBase == null) {
            return null;
        }
        if (postBase instanceof ForwardPost) {
            PostBase rootPost = ((ForwardPost) postBase).getRootPost();
            if (rootPost != null) {
                return rootPost.getUid();
            }
        }
        return null;
    }

    public static String getRootPostId(PostBase postBase) {
        if (postBase == null) {
            return null;
        }
        if (postBase instanceof ForwardPost) {
            PostBase rootPost = ((ForwardPost) postBase).getRootPost();
            if (rootPost != null) {
                return rootPost.getPid();
            }
        }
        return null;
    }

    public static String getRootPostLanguage(PostBase postBase) {
        if (postBase == null) {
            return null;
        }
        if (postBase instanceof ForwardPost) {
            PostBase rootPost = ((ForwardPost) postBase).getRootPost();
            if (rootPost != null) {
                return rootPost.getLanguage();
            }
        }
        return null;
    }

    public static String[] getRootPostTags(PostBase postBase) {
        if (postBase == null) {
            return null;
        }
        if (postBase instanceof ForwardPost) {
            PostBase rootPost = ((ForwardPost) postBase).getRootPost();
            if (rootPost != null) {
                return rootPost.getTags();
            }
        }
        return null;
    }

    public static String getRootOrPostUid(PostBase postBase) {
        if (postBase == null) {
            return null;
        }
        if (postBase instanceof ForwardPost) {
            PostBase rootPost = ((ForwardPost) postBase).getRootPost();
            if (rootPost != null) {
                return rootPost.getUid();
            }
            return null;
        }
        return postBase.getUid();
    }
}
