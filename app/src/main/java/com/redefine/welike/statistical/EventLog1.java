package com.redefine.welike.statistical;

import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.commonui.event.commonenums.BooleanValue;
import com.redefine.welike.commonui.event.commonenums.FeedButtonFrom;
import com.redefine.welike.commonui.event.commonenums.PostType;
import com.redefine.welike.commonui.event.commonenums.ResultEnum;
import com.redefine.welike.commonui.event.commonenums.ShareChannel;
import com.redefine.welike.commonui.event.commonenums.SocialMedia;
import com.redefine.welike.commonui.event.commonenums.UserType;
import com.redefine.welike.statistical.pagename.PageNameManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by nianguowang on 2018/11/1
 */
public class EventLog1 {
    private static final String TAG = "EventLog1";

    /**
     * Document address : https://shimo.im/sheet/HQKwKYqGSPsuXc2O/AxMVE
     * 5001014
     * action	1、当选择语言动作产生时上报（所有参数）
     * la	选中菜单语言	1、en 2、gu 3、hi
     * button_from	1、home页 2、setting页-language
     */
    public static class Language {

        public enum ButtonFrom {
            HOME(1), SETTING(2);

            private int from;

            ButtonFrom(int from) {
                this.from = from;
            }

            public int getFrom() {
                return from;
            }
        }

        static String id = "5001008";

        public static void report1(String language, ButtonFrom buttonFrom) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("la", language);
                if (buttonFrom != null) {
                    data.put("button_from", buttonFrom.getFrom());
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Document address : https://shimo.im/sheet/HQKwKYqGSPsuXc2O/AxMVE
     * <p>
     * 5001015
     * action	1、授权页面展示（isAuthorized，button_from）
     * 2、添加好友页面展示（button_from）
     * 3、点击follow按钮（to_user_id, from_user_id）
     * 4、点击invite邀请好友按钮（status）
     * <p>
     * isAuthorized	1、授权 2、未授权
     * status	1、发送成功 2、发送失败
     * from_user_id	产生动作的userid
     * to_user_id	被产生动作的userid
     * button_from	1、Home页 2、message页 3、me页面 4,Follow 5,others
     */
    public static class AddFriend {

        static String id = "5001023";

        public enum Authorized {
            AUTHORIZED(1), UNAUTHORIZED(2);
            private int value;

            Authorized(int value) {
                this.value = value;
            }
        }

        public enum Status {
            SUCCESS(1), FAIL(2);
            private int value;

            Status(int value) {
                this.value = value;
            }
        }

        public enum ButtonFrom {
            HOME(1), MESSAGE(2), ME(3), FOLLOW(4), OTHER(5), HORIZONTAL_CARD(6);
            private int value;

            ButtonFrom(int value) {
                this.value = value;
            }
        }

        public static void report1(Authorized authorized, ButtonFrom buttonFrom) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (authorized != null) {
                    data.put("isAuthorized", authorized.value);
                }
                if (buttonFrom != null) {
                    data.put("button_from", buttonFrom.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(ButtonFrom buttonFrom) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                if (buttonFrom != null) {
                    data.put("button_from", buttonFrom.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(String toUserId, String fromUserId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("to_user_id", toUserId);
                data.put("from_user_id", fromUserId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(Status status) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                if (status != null) {
                    data.put("status", status.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Document address : https://shimo.im/sheet/HQKwKYqGSPsuXc2O/AxMVE
     * 5001001
     * action	1、当关注动作产生时上报（所有参数）
     * 2、用户取消关注时上报 （所有参数）
     * button_from 见文档
     * from_user_id	产生动作的userid
     * to_user_id	被产生动作的userid
     * follow_type	选择的类别
     * post_id	关注按钮所在post的ID
     * pool_code	1、运营池 2、热门池 3、内容成长池（投稿池） 4、广告内容 5、兴趣探索内容
     */
    public static class Follow {

        static String id = "5001001";

        public static void report1(String buttonFrom, String fromUserId, String toUserId, String followType, String postId,
                                   String poolCode, String operationType, String postLanguage, String[] postTags, String sequenceId, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("button_from", buttonFrom);
                data.put("from_user_id", fromUserId);
                data.put("to_user_id", toUserId);
                data.put("follow_type", followType);
                data.put("post_id", postId);
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLanguage);
                data.put("post_tags", postTags);
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String buttonFrom, String fromUserId, String toUserId, String followType, String postId,
                                   String poolCode, String operationType, String postLanguage, String[] postTags, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("button_from", buttonFrom);
                data.put("from_user_id", fromUserId);
                data.put("to_user_id", toUserId);
                data.put("follow_type", followType);
                data.put("post_id", postId);
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLanguage);
                data.put("post_tags", postTags);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(String buttonFrom, String uid, String postId,
                                   String poolCode, String operationType, String postLanguage, String[] postTags) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("button_from", buttonFrom);
                data.put("uid", uid);
                data.put("post_id", postId);
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLanguage);
                data.put("post_tags", postTags);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(String buttonFrom, String toUserId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("button_from", buttonFrom);
                data.put("to_user_id", toUserId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6(String buttonFrom, String fromUserId, String toUserId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("button_from", buttonFrom);
                data.put("from_user_id", fromUserId);
                data.put("to_user_id", toUserId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(String buttonFrom) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("button_from", buttonFrom);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9(String buttonFrom) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                data.put("button_from", buttonFrom);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Document address : https://shimo.im/sheet/HQKwKYqGSPsuXc2O/AxMVE
     * 5001020
     * 日活统计
     */
    public static class LaunchApp {
        static String id = "5001020";

        public enum FromPush {
            FROM_OTHER(1), FROM_PUSH(2);
            private int value;

            FromPush(int value) {
                this.value = value;
            }
        }

        public static void report1(FromPush fromPush) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (fromPush != null) {
                    data.put("open_source", fromPush.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Document address : https://shimo.im/sheet/HQKwKYqGSPsuXc2O/AxMVE
     * 5001021
     * 1.下拉刷新
     * 2.上划加载
     * 3.打开默认刷新
     * 4.点按钮刷新
     */
    public static class FeedRefresh {
        static String id = "5001021";

        public static void report1(String type, int listTotal, int hot, int operation, int news, String posts, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("type", type);
                data.put("listTotal", listTotal);
                data.put("posts", posts);
                data.put("sequenceId", sequenceId);
                data.put("hot", hot);
                data.put("operation", operation);
                data.put("news", news);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String type, int listTotal, int hot, int operation, int news, String posts, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("type", type);
                data.put("listTotal", listTotal);
                data.put("posts", posts);
                data.put("sequenceId", sequenceId);
                data.put("hot", hot);
                data.put("operation", operation);
                data.put("news", news);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(String type, int listTotal, int hot, int operation, int news, String posts, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("type", type);
                data.put("listTotal", listTotal);
                data.put("posts", posts);
                data.put("sequenceId", sequenceId);
                data.put("hot", hot);
                data.put("operation", operation);
                data.put("news", news);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(String type, int listTotal, int hot, int operation, int news, String posts, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("type", type);
                data.put("listTotal", listTotal);
                data.put("posts", posts);
                data.put("sequenceId", sequenceId);
                data.put("hot", hot);
                data.put("operation", operation);
                data.put("news", news);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Document address : https://shimo.im/sheet/HQKwKYqGSPsuXc2O/AxMVE
     * 5001022
     * 1 收到FCM消息(push_type,seqid)
     * 2 解析正常并处理(push_type,seqid,result)
     * 3 点击(push_type,seqid,result)
     */
    public static class Push {
        static String id = "5001022";

        public enum Result {
            POP(1), FOREGROUND_NOT_POP(2), TITLE_NONE_NOT_POP(3), UNFOLLOW_NOT_POP(4);

            private int value;

            Result(int value) {
                this.value = value;
            }
        }

        public enum PushChannel {
            UT(1), FIREBASE(2);

            private int value;

            PushChannel(int value) {
                this.value = value;
            }
        }

        public static void report1(int pushType, String seqId, String businessType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("push_type", pushType);
                data.put("seqid", seqId);
                data.put("businessType", businessType);
                EventManager.getInstance().addRuntimeEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(int pushType, String seqId, Result result, String businessType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("push_type", pushType);
                data.put("seqid", seqId);
                data.put("businessType", businessType);

                if (result != null) {
                    data.put("result", result.value);
                }
                EventManager.getInstance().addRuntimeEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(int pushType, String seqId, Result result, PushChannel push_channel, String businessType,String schemeUrl) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("push_type", pushType);
                data.put("seqid", seqId);
                data.put("businessType", businessType);
                data.put("schemeUrl",schemeUrl);
                if (null != push_channel) {
                    data.put("push_channel", push_channel.value);
                }
                if (result != null) {
                    data.put("result", result.value);
                }
                EventManager.getInstance().addRuntimeEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Document address : https://shimo.im/sheet/HQKwKYqGSPsuXc2O/AxMVE
     * 5001022
     * 1、当分享动作产生时上报（所有参数）
     * 2、分享二维码生成（loading_time） --只针对Xender分享
     * 3、Done按钮点击 (所有参数)   --只针对Xender分享
     * <p>
     * share_channel	1、分享到whatsapp 2、分享到facebook 3、分享到instagram 4、分享到instgram 5、copylink 6、其他
     * content_type	1、post 2、shareProfile 3、shareApp 4、shareTopic 5、shareH5page
     * post_type	1、视频类 2、图文类post 3、文字类post 4、长文 5、投票 6、post status类 7、其他类型
     * videopost_type	1、视频文件 2、post链接
     * sharefrom	1、来自post卡片右下角分享按钮 2、来自全屏视频播放页分享按钮 3、postdetail页右下角分享按钮 4、来自各种页面的分享弹窗
     * popfrom	1、postdetail页分享弹窗 2、post卡片分享弹窗 3、profile页分享弹窗 4、shareapp分享弹窗 5、topic页分享弹窗 6、H5page分享弹窗
     * is_login	0、未登录状态 1、已登录状态
     * is_success	0、分享失败 1、分享成功 2、unknow
     */
    public static class Share {
        static String id = "5001004";

        public enum ContentType {
            POST(1), APP(2), PROFILE(4), TOPIC(5), H5(7), APK(8);
            private int value;

            ContentType(int value) {
                this.value = value;
            }
        }

        public enum VideoPostType {
            VIDEO_FILE(1), POST_LINK(2);
            private int value;

            VideoPostType(int value) {
                this.value = value;
            }
        }

        public enum ShareFrom {
            POST_CARD(1), VIDEO_DETAIL(2), POST_DETAIL(3), DIALOG(4), ARTICLE(5), PHOTO_LIST(6), OTHER(7);
            private int value;

            ShareFrom(int value) {
                this.value = value;
            }
        }

        public enum PopPage {
            POST_DETAIL(1), POST_CARD(2), PROFILE(3), APP(4), TOPIC(5), H5(6), OTHER(7);
            private int value;

            PopPage(int value) {
                this.value = value;
            }
        }

        /**
         * 产生一次分享行为
         *
         * @param shareChannel
         * @param contentType
         * @param postType
         * @param videoPostType
         * @param shareFrom
         * @param popPage
         * @param isSuccess
         */
        public static void report1(ShareChannel shareChannel, ContentType contentType, PostType postType, VideoPostType videoPostType,
                                   ShareFrom shareFrom, PopPage popPage, ResultEnum isSuccess, String poolCode, String operationType,
                                   String source, String postId, String postLanguage, String[] postTags, String postUid, String rootPostId,
                                   String rootPostUid, String rootPostLanguage, String[] rootPostTags, String sequenceId, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (shareChannel != null) {
                    data.put("share_channel", shareChannel.getValue());
                }
                if (contentType != null) {
                    data.put("content_type", contentType.value);
                }
                if (postType != null) {
                    data.put("post_type", postType.getValue());
                }
                if (videoPostType != null) {
                    data.put("videopost_type", videoPostType.value);
                }
                if (shareFrom != null) {
                    data.put("share_from", shareFrom.value);
                }
                if (popPage != null) {
                    data.put("pop_page", popPage.value);
                }
                if (isSuccess != null) {
                    data.put("is_success", isSuccess.getValue());
                }
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("source", source);
                data.put("post_id", postId);
                data.put("post_la", postLanguage);
                data.put("post_tags", postTags);
                data.put("post_uid", postUid);
                data.put("rootpost_id", rootPostId);
                data.put("rootpost_uid", rootPostUid);
                data.put("rootpost_la", rootPostLanguage);
                data.put("rootpost_tags", rootPostTags);
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 1.流内曝光：在流中，单个post卡片≥2/3的区域在用户屏幕中展示一次（可停留也可不停留），则针对该用户 (全部参数)
     * 2.detail页内曝光：post detai页面每展示一次 （可停留页可不停留，不传view source）
     */
    public static class FeedExpose {
        static String id = "5001013";

        public static void report1(String postUid, String rootpostUid, String post_id, String rootPostId, PostType postType, String source, String poolCode, String operationType,
                                   String postLa, String[] postTags, String rootPostLa, String[] rootPostTags, String sequenceId, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("post_uid", postUid);
                data.put("rootpost_uid", rootpostUid);
                data.put("post_id", post_id);
                data.put("rootpost_id", rootPostId);
                if (postType != null) {
                    data.put("post_type", postType.getValue());
                }
                data.put("view_source", source);
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("rootpost_la", rootPostLa);
                data.put("rootpost_tags", rootPostTags);
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String postUid, String rootpostUid, String post_id, String rootPostId, PostType postType, String poolCode, String operationType,
                                   String postLa, String[] postTags, String rootPostLa, String[] rootPostTags, String sequenceId, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("post_uid", postUid);
                data.put("rootpost_uid", rootpostUid);
                data.put("post_id", post_id);
                data.put("rootpost_id", rootPostId);
                if (postType != null) {
                    data.put("post_type", postType.getValue());
                }
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("rootpost_la", rootPostLa);
                data.put("rootpost_tags", rootPostTags);
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1.流内阅读：在流中，单个post卡片≥2/3的区域在用户屏幕中展示一次（在屏幕有停留超过1秒），则针对该用户 (全部参数)
     * 2.detail页内阅读：post detai页面每展示一次 （屏幕停留时候超过1s,不传view source）
     * 3.博文点击（全部参数）
     */
    public static class FeedView {
        static String id = "5001005";

        public static enum FeedClickArea {
            AVATAR(1), MORE(2), TEXT(3), PIC(4), VIDEO(5), POLL(6);
            private int value;

            FeedClickArea(int value) {
                this.value = value;
            }
        }

        public static void report1(String postUid, String rootpostUid, String post_id, String rootPostId, PostType postType, String source,
                                   long viewTime, String poolCode, String operationType, String postLa, String[] postTags, String rootPostLa,
                                   String[] rootPostTags, String sequenceId, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("view_time", viewTime);
                data.put("post_uid", postUid);
                data.put("rootpost_uid", rootpostUid);
                data.put("post_id", post_id);
                data.put("rootpost_id", rootPostId);
                if (postType != null) {
                    data.put("post_type", postType.getValue());
                }
                data.put("view_source", source);
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("rootpost_la", rootPostLa);
                data.put("rootpost_tags", rootPostTags);
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String postUid, String rootpostUid, String post_id, String rootPostId, PostType postType, long viewTime,
                                   String poolCode, String operationType, String postLa, String[] postTags, String rootPostLa, String[] rootPostTags, String sequenceId, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("view_time", viewTime);
                data.put("post_uid", postUid);
                data.put("rootpost_uid", rootpostUid);
                data.put("post_id", post_id);
                data.put("rootpost_id", rootPostId);
                if (postType != null) {
                    data.put("post_type", postType.getValue());
                }
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("rootpost_la", rootPostLa);
                data.put("rootpost_tags", rootPostTags);
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(String postUid, String rootpostUid, String post_id, String rootPostId, PostType postType, String source, String poolCode,
                                   String operationType, String postLa, String[] postTags, String rootPostLa, String[] rootPostTags, FeedClickArea clickArea, String sequenceId, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("post_uid", postUid);
                data.put("rootpost_uid", rootpostUid);
                data.put("post_id", post_id);
                data.put("rootpost_id", rootPostId);
                if (postType != null) {
                    data.put("post_type", postType.getValue());
                }
                data.put("view_source", source);
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("rootpost_la", rootPostLa);
                data.put("rootpost_tags", rootPostTags);
                if (clickArea != null) {
                    data.put("click_area", clickArea.value);
                }
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5001002
     * 1、当评论动作产生时上报（ 所有参数）
     * 2、当转发动作产生时上报（所有参数）
     */
    public static class FeedForment {

        static String id = "5001002";

        public static void report1(PostType postType, String source, FeedButtonFrom buttonFrom, String postId,
                                   String poolCode, String operationType, String postLanguage, String[] postTags, String rootPostLa, String[] rootPostTags, String contentUid, String sequenceId
                , String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (postType != null) {
                    data.put("post_type", postType.getValue());
                }
                data.put("source", source);
                if (buttonFrom != null) {
                    data.put("button_from", buttonFrom.getValue());
                }
                data.put("post_id", postId);
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLanguage);
                data.put("post_tags", postTags);
                data.put("rootpost_la", rootPostLa);
                data.put("rootpost_tags", rootPostTags);
                data.put("content_uid", contentUid);
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(PostType postType, String source, FeedButtonFrom buttonFrom, String postId,
                                   String poolCode, String operationType, String postLanguage, String[] postTags, String rootPostLa, String[] rootPostTags, String contentUid, String sequenceId, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                if (postType != null) {
                    data.put("post_type", postType.getValue());
                }
                data.put("source", source);
                if (buttonFrom != null) {
                    data.put("button_from", buttonFrom.getValue());
                }
                data.put("post_id", postId);
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLanguage);
                data.put("post_tags", postTags);
                data.put("rootpost_la", rootPostLa);
                data.put("rootpost_tags", rootPostTags);
                data.put("content_uid", contentUid);
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5001003
     * 1、当点赞动作产生时上报（所有参数）
     */
    public static class FeedLike {
        static String id = "5001003";

        public static void report1(PostType postType, String source, FeedButtonFrom buttonFrom, String postId, String postUid, String poolCode, String operationType,
                                   String postLanguage, String[] postTags, String contentUid, String rootPostLanguage, String[] rootPostTags, String sequenceId, String reclogs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (postType != null) {
                    data.put("post_type", postType.getValue());
                }
                data.put("source", source);
                if (buttonFrom != null) {
                    data.put("button_from", buttonFrom.getValue());
                }
                data.put("post_id", postId);
                data.put("post_uid", postUid);
                data.put("strategy", poolCode);
                data.put("operationType", operationType);
                data.put("post_la", postLanguage);
                data.put("content_uid", contentUid);
                data.put("post_tags", postTags);
                data.put("rootpost_la", rootPostLanguage);
                data.put("rootpost_tags", rootPostTags);
                data.put("sequenceId", sequenceId);
                data.put("reclogs", reclogs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1	在发现页展示引导发post status动画提示
     * 2	post status页面展示（from）
     * 3	用户对图片进行了编辑（buttontype）
     * 4	publish按钮点击
     * 5    点击下载
     * 6    点击上传图片
     * 7    点击emoji
     * 8    点击send按钮
     * from	页面显示来源	1、引导动画 2、发布器中的post status按钮
     * buttontype	按钮类型	1、shuffle text 2、shuffle image 3、点击页面中央的文字区域，编辑文案
     */
    public static class PostStatus {
        static String id = "5001006";

        public enum ButtonFrom {
            GUIDE_ANIMATION(1), PUBLISHER_ENTRANCE(2);
            private int value;

            ButtonFrom(int value) {
                this.value = value;
            }
        }

        public enum ButtonType {
            SHUFFLE_TEXT(1), SHUFFLE_IMAGE(2), EDIT_TEXT(3);
            private int value;

            ButtonType(int value) {
                this.value = value;
            }
        }

        public static void report1() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(ButtonFrom from) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                if (from != null) {
                    data.put("from", from.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(ButtonType buttonType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                if (buttonType != null) {
                    data.put("buttontype", buttonType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(BooleanValue textChanged, String text, String imageId, String categoryID, String categoryName) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                if (textChanged != null) {
                    data.put("textchanged", textChanged.getValue());
                }
                data.put("text", text);
                data.put("imageid", imageId);
                data.put("categoryID", categoryID);
                data.put("categoryName", categoryName);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * https://shimo.im/sheet/HQKwKYqGSPsuXc2O/awuaT
     * action
     * 1	进入搜索推荐页
     * 2	搜索结果展示
     * 3	点击进入搜索详情
     */
    public static class Search {
        static String id = "5001012";

        public enum FromPage {
            ROUTE(1), DISCOVER(2), SEARCH(3), TRENDING_TOPIC(4), FEED(5);
            private int value;

            FromPage(int value) {
                this.value = value;
            }
        }

        public static void report1(FromPage fromPage) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (fromPage != null) {
                    data.put("from_page", fromPage.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String searchKey) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("search_key", searchKey);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(String searchKey, String userId, String postId, String postLa, String[] postTags, String sequenceId, int rank) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("search_key", searchKey);
                data.put("user_uid", userId);
                data.put("post_id", postId);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("sequenceId", sequenceId);
                data.put("rank", rank);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5001007
     * 1、暂停
     * 2、播放
     * 3、点击下载按钮
     * 4、视频下载成功
     * 5、流内自动播放（play_duration, duration_time, post_uid, uid, rootpoost_id, first_audio_time, first_video_time, paly_source）
     * 6、点击播放器按钮
     * 7、详情页视频播放（全部参数）
     * 8、声音按钮点击（mute_type）
     */
    public static class Video {
        static String id = "5001007";

        public enum ButtonType {
            AVATAR(1), TEXT(2), ROTATE(3), CLOSE(4);
            private int value;

            ButtonType(int value) {
                this.value = value;
            }
        }

        public static void report1() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(String postId, String postUid, String postLa, String[] postTags) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("post_id", postId);
                data.put("post_uid", postUid);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5(Map<String, String> logs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.putAll(logs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6(ButtonType buttonType, String postId, String postUid, String postLa, String[] postTags) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                if (buttonType != null) {
                    data.put("button_type", buttonType.value);
                }
                data.put("post_id", postId);
                data.put("post_uid", postUid);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7(Map<String, String> logs) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                data.putAll(logs);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(int mute_type, String postId, String postUid, String postLa, String[] postTags) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("mute_type", mute_type);
                data.put("post_id", postId);
                data.put("post_uid", postUid);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5001024
     * 1  页面展示（activityname）
     * 2 页面切换时（activityname,staytime）
     */
    public static class ShowPage {
        static String id = "5001024";

        public static void report1(String activityName) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("activityname", PageNameManager.INSTANCE.getActivityAlias(activityName));
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String activityName, long stayTime) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("activityname", PageNameManager.INSTANCE.getActivityAlias(activityName));
                data.put("staytime", stayTime);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1	profile页面展示(from_page)
     * 2	点击icon（icon_type）
     * 3	点击认证
     * 4	点击兴趣
     * 5	点击following
     * 6	点击followers
     * 7	点击edit profile
     * 8	点击更多按钮（more_type）
     * 9	点击follow
     * 10	点击message
     * 11	申请的snake bar展示（user_type，icon_type）
     * 12	点击snake bar的apply（user_type，icon_type）
     * 13	点击post(指点击ProfilePhotoPreviewActivity页下面的Post内容）
     * 14	点击likes（user_type）（指点击Profile页面的Like或者album Tab）
     * 15	用户点击图片（指点击客人态相册流里面的某个图片）
     * 16	左右切换图片（指在ProfilePhotoPreviewActivity页面左右切换图片）
     * <p>
     * icon_type	icon类型	1.Facebook 2.Twitter 3.YouTube
     * from_page	页面来源	1.Me页面
     * 2.其他
     * more_type	更多里的选项类型	1.share 2.其他
     */
    public static class Profile {

        static String id = "5001015";

        public enum FromPage {
            ME(1), OTHER(2);
            private int value;

            FromPage(int value) {
                this.value = value;
            }
        }

        public enum MoreType {
            SHARE(1), OTHER(2);
            private int value;

            MoreType(int value) {
                this.value = value;
            }
        }

        public static void report1(FromPage fromPage, UserType userType, String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (fromPage != null) {
                    data.put("from_page", fromPage.value);
                }
                if (userType != null) {
                    data.put("user_type", userType.getValue());
                }
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(SocialMedia iconType, UserType userType, String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                if (iconType != null) {
                    data.put("icon_type", iconType.getValue());
                }
                if (userType != null) {
                    data.put("user_type", userType.getValue());
                }
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(MoreType moreType, String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                if (moreType != null) {
                    data.put("more_type", moreType.value);
                }
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report10(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 10);
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report11(UserType userType, SocialMedia iconType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                if (userType != null) {
                    data.put("user_type", userType.getValue());
                }
                if (iconType != null) {
                    data.put("icon_type", iconType.getValue());
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report12(UserType userType, SocialMedia iconType, String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                if (userType != null) {
                    data.put("user_type", userType.getValue());
                }
                if (iconType != null) {
                    data.put("icon_type", iconType.getValue());
                }
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report13() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 13);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report14(UserType userType, String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 14);
                if (userType != null) {
                    data.put("user_type", userType.getValue());
                }
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report15() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 15);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report16() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 16);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * click_type
     * 1、language
     * 2、notification
     * 3、privacy
     * 4、block
     * 5、check for updates
     * 6、clear cache
     * 7、logout
     */
    public static class SettingPage {

        static String id = "5001041";

        public enum ClickType {
            LANGUAGE(1), NOTIFICATION(2), PRIVACY(3), BLOCK(4), CHECKORUPDATES(5), CLEARCACHE(6), LOGOUT(7);
            private int value;

            ClickType(int value) {
                this.value = value;
            }
        }

        public static void report1(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String uid, ClickType clickType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                if (clickType != null) {
                    data.put("click_type", clickType.value);
                }
                data.put("profile_uid", uid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 1	点击profile
     * 2	点击following
     * 3	点击followers
     * 4	点击posts
     * 5	点击任务
     * 6	点击认证
     * 7	点击new friends
     * 8	点击my likes
     * 9	点击Share app
     * 10	点击Feedback
     * 11	点击setting
     * 12	点击草稿箱
     * 13   Me页面展示
     */
    public static class MainMe {

        static String id = "5001016";

        public static void report1() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report10() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 10);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report11() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report12() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report13() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 13);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1	edit profile页面展示
     * 2	点击换头像
     * 3	点击换名字
     * 4	点击换性别
     * 5	点击编辑bio
     * 6	点击选兴趣
     * 7	点击icon（icon_type）
     * 8	申请的snake bar展示（icon_type）
     * 9	点击snake bar上的apply按钮（icon_type）
     * 10	Link页的展示（icon_type）
     * 11	点击submit(icon_type)
     * 12	结果返回（icon_type，result）
     * icon类型	1.Facebook 2.Instagram 3.YouTube
     * 提交结果	1.成功 2.失败
     */
    public static class EditProfile {

        static String id = "5001017";

        public static void report1() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7(SocialMedia iconType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                if (iconType != null) {
                    data.put("icon_type", iconType.getValue());
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(SocialMedia iconType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                if (iconType != null) {
                    data.put("icon_type", iconType.getValue());
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9(SocialMedia iconType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                if (iconType != null) {
                    data.put("icon_type", iconType.getValue());
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report10(SocialMedia iconType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 10);
                if (iconType != null) {
                    data.put("icon_type", iconType.getValue());
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report11(SocialMedia iconType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                if (iconType != null) {
                    data.put("icon_type", iconType.getValue());
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report12(SocialMedia iconType, ResultEnum result) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                if (iconType != null) {
                    data.put("icon_type", iconType.getValue());
                }
                if (result != null) {
                    data.put("result", result.getValue());
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新装统计
     */
    public static class InstallApp {

        static String id = "5001010";

        /**
         * 新装统计
         *
         * @param install_type   1.首次安装 2.升级安装
         * @param install_source 1.apk安装 2.GP安装
         */
        public static void report1(int install_type, int install_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("install_type", install_type);
                data.put("install_source", install_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1、闪屏展示（id，islogin）
     * 2、闪屏点击（id，islogin）
     */
    public static class Splash {

        static String mId = "5001009";

        /**
         * 1、闪屏展示（id，islogin）
         *
         * @param id      闪屏的id
         * @param islogin 用户是否已经登录
         */
        public static void report1(String id, boolean islogin) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("id", id);
                data.put("islogin", islogin);
                EventManager.getInstance().addEvent(mId, data);
                LogUtil.d(TAG, mId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2、闪屏点击（id，islogin）
         *
         * @param id      闪屏的id
         * @param islogin 用户是否已经登录
         */
        public static void report2(String id, boolean islogin) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("id", id);
                data.put("islogin", islogin);
                EventManager.getInstance().addEvent(mId, data);
                LogUtil.d(TAG, mId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5001014
     * 1	login页面展示（page_source， language, if_tc_installed，login_verify_type，account_status, page_type）
     * 2	登录方式按钮的点击（login_source,login_verify_type, page_type）
     * 3	第三方登录回调（login_source, isNewUser, return_result）
     * 4	用户手动左右滑动次数?
     * 5	手机号输入页面展示（page_source，if_tc_installed, input_way, page_type,login_verify_type,account_status）
     * 6	下一步按钮的点击（phone_number,page_source,isNewUser）
     * 7	第三方登录按钮的点击（login_source,from_page,login_verify_type, account_status）
     * 8	第三方登录回调（login_source；return_result）
     * 9	验证码页面的展示（isNewUser，page_source, page_status, request_way, verify_type, if_tc_installed）
     * 10	第三方登录按钮的点击（login_source, from_page）
     * 11	第三方登录回调（login_source；return_result, page_status）
     * 12	resend按钮点击(isNewUser, phone_number, SMS_send，page_status, request_way, verify_type，)
     * 13	change按钮点击（isNewUser, phone_number, SMS_send, page_status, if_tc_installed, input_way)
     * 14	输入完验证码提交服务端验证(phone_number, isNewUser, verify_type,SMS_send, SMS_check, page_status，page_source, stay_time, if_tc_installed，)
     * 18	登录成功（全部参数，包括isNewUser，login_verify_type,page_type，account_status）
     * 15	填写个人资料页面展示(phone_number; verify_type; SMS_sen, page_source，stay_time)
     * 16	skip(phone_number;verify_type;SMS_send，account_status)
     * 17	资料填写完成点击下一步(nickname,nickname_check,phone_number;verify_type;SMS_send,stay_time)
     * 19	注册完成（以上参数都要有）
     * 20	填写名字页面展示（account_status,page_source）
     * 21	点击名字输入框（account_stauts,page_source）
     * 22	点击进入下一步（nickname,isNewUser，account_status,page_source）
     */
    public static class Login {
        static String id = "5001014";
    }

    /**
     * 5001019
     * 1、banner所在页展示（page_name）
     * 2、banner点击(所有参数)
     */
    public static class Banner {
        static String id = "5001019";

        public enum PageName {
            HOME(1), DISCOVER(2);
            private int value;

            PageName(int value) {
                this.value = value;
            }
        }

        public static void report1(PageName pageName) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("page_name", pageName.value);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(PageName pageName, long bannerId, String bannerLa) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("page_name", pageName.value);
                data.put("banner_id", bannerId);
                data.put("banner_la", bannerLa);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Discover {
        static String id = "5001011";

        public enum TopicSource {
            DISCOVER(1), TRENDING_TOPICS(2);
            private int value;

            TopicSource(int value) {
                this.value = value;
            }
        }

        public static void report1() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String topicId, TopicSource topicSource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("topic_id", topicId);
                if (topicSource != null) {
                    data.put("topic_source", topicSource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class DiscoverTopic {
        static String id = "5001031";

        public static void report1(String topicId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("topic_id", topicId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String topicId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("topic_id", topicId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Publish {
        static String id = "5001018";

        public enum Source {
            MAIN_PAGE(1), POST_CARD(2), POST_DETAIL(3), COMMENT_DETAIL(4), DRAFT(5), TOPIC_OR_SUPER_TOPIC(6), COMMENT_EMOJI(7), ARTICLE(8);
            private int value;

            Source(int value) {
                this.value = value;
            }
        }

        public enum MainSource {
            OTHER(0), HOME(1), DISCOVER(2), MESSAGE(3), ME(4), OUT_SHARE(5);
            private int value;

            MainSource(int value) {
                this.value = value;
            }
        }

        public enum PageType {
            NEW_POST(1), COMMENT(2), REPOST(3), REPLY(4), POST_STATUS(5);
            private int value;

            public int getValue() {
                return value;
            }

            PageType(int value) {
                this.value = value;
            }
        }

        public enum ExistState {
            SEND(1), SAVE_DRAFT(2), DISCARD(3);
            private int value;

            ExistState(int value) {
                this.value = value;
            }
        }

        public enum PollTime {
            ONE_DAY(1), THREE_DAY(2), ONE_WEEK(3), ONE_MONTH(4), NO_LIMIT(5);
            private int value;

            PollTime(int value) {
                this.value = value;
            }
        }

        public enum TopicSource {
            FROM_TOPIC(1), FROM_POSTER(2), CHOOSE_SUG_TOPIC(3), NEW_TOPIC(4);
            private int value;

            TopicSource(int value) {
                this.value = value;
            }
        }

        public enum ReSendFrom {
            FAILURE_STATUS_BAR(1), DRAFT(2);
            private int value;

            public int getValue() {
                return value;
            }

            ReSendFrom(int value) {
                this.value = value;
            }
        }

        public static void report1(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(Source source, MainSource mainSource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5(Source source, MainSource mainSource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report10(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 10);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report11(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report12(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report13(Source source, MainSource mainSource, PageType pageType, ExistState existState, int wordsNum, int pictureNum, String pictureSize,
                                    long pictureUploadTime, int videoNum, long videoSize, long videoConvertTime, long videoUploadTime, int linkNum, BooleanValue pollType,
                                    int pollNum, PollTime pollTime, int emojiNum, int atNum, int topicNum, TopicSource topicSource, BooleanValue alosRepost, BooleanValue alsoComment,
                                    String postId, String repostId, String topicId, String community, String draftId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 13);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (existState != null) {
                    data.put("exit_state", existState.value);
                }
                data.put("words_num", wordsNum);
                data.put("picture_num", pictureNum);
                data.put("picture_size", pictureSize);
                data.put("picture_upload_time", pictureUploadTime);
                data.put("video_num", videoNum);
                data.put("video_size", videoSize);
                data.put("video_convert_time", videoConvertTime);
                data.put("video_upload_time", videoUploadTime);
                data.put("web_link", linkNum);
                if (pollType != null) {
                    data.put("poll_type", pollType.getValue());
                }
                data.put("poll_num", pollNum);
                if (pollTime != null) {
                    data.put("poll_time", pollTime.value);
                }
                data.put("emoji_num", emojiNum);
                data.put("at_num", atNum);
                data.put("topic_num", topicNum);
                if (topicSource != null) {
                    data.put("topic_source", topicSource.value);
                }
                if (alosRepost != null) {
                    data.put("also_repost", alosRepost.getValue());
                }
                if (alsoComment != null) {
                    data.put("also_comment", alsoComment.getValue());
                }
                data.put("post_id", postId);
                data.put("repost_id", repostId);
                data.put("topic_id", topicId);
                data.put("community", community);
                data.put("draft_id", draftId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report14(Source source, MainSource mainSource, PageType pageType, ExistState existState, int wordsNum, int pictureNum, String pictureSize,
                                    long pictureUploadTime, int videoNum, long videoSize, long videoConvertTime, long videoUploadTime, int linkNum, BooleanValue pollType,
                                    int pollNum, PollTime pollTime, int emojiNum, int atNum, int topicNum, TopicSource topicSource, BooleanValue alosRepost, BooleanValue alsoComment,
                                    String postId, String repostId, String topicId, String community) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 14);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (existState != null) {
                    data.put("exit_state", existState.value);
                }
                data.put("words_num", wordsNum);
                data.put("picture_num", pictureNum);
                data.put("picture_size", pictureSize);
                data.put("picture_upload_time", pictureUploadTime);
                data.put("video_num", videoNum);
                data.put("video_size", videoSize);
                data.put("video_convert_time", videoConvertTime);
                data.put("video_upload_time", videoUploadTime);
                data.put("web_link", linkNum);
                if (pollType != null) {
                    data.put("poll_type", pollType.getValue());
                }
                data.put("poll_num", pollNum);
                if (pollTime != null) {
                    data.put("poll_time", pollTime.value);
                }
                data.put("emoji_num", emojiNum);
                data.put("at_num", atNum);
                data.put("topic_num", topicNum);
                if (topicSource != null) {
                    data.put("topic_source", topicSource.value);
                }
                if (alosRepost != null) {
                    data.put("also_repost", alosRepost.getValue());
                }
                if (alsoComment != null) {
                    data.put("also_comment", alsoComment.getValue());
                }
                data.put("post_id", postId);
                data.put("repost_id", repostId);
                data.put("topic_id", topicId);
                data.put("community", community);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report15(Source source, MainSource mainSource, PageType pageType, ExistState existState, int wordsNum, int pictureNum, String pictureSize,
                                    long pictureUploadTime, int videoNum, long videoSize, long videoConvertTime, long videoUploadTime, int linkNum, BooleanValue pollType,
                                    int pollNum, PollTime pollTime, int emojiNum, int atNum, int topicNum, TopicSource topicSource, BooleanValue alosRepost, BooleanValue alsoComment,

                                    String postId, String repostId, String topicId, String community, String postLa, ArrayList<String> postTags, String contentUid, String uploadType,
                                    String sequenceId, int retryTimes, String draftId
        ) {
            JSONObject data = new JSONObject();
            try {

                data.put("action", 15);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (existState != null) {
                    data.put("exit_state", existState.value);
                }

                if (!TextUtils.isEmpty(postLa)) {
                    data.put("post_la", postLa);
                }

                if (postTags != null && postTags.size() > 0) {

                    String tagArray[] = new String[postTags.size()];
                    for (int i = 0; i < postTags.size(); i++) {
                        tagArray[i] = postTags.get(i);
                    }
                    data.put("post_tags", tagArray);
                }
                data.put("sequenceId", sequenceId);
                data.put("upload_type", uploadType);
                data.put("content_uid", contentUid);
                data.put("words_num", wordsNum);
                data.put("picture_num", pictureNum);
                data.put("picture_size", pictureSize);
                data.put("picture_upload_time", pictureUploadTime);
                data.put("video_num", videoNum);
                data.put("video_size", videoSize);
                data.put("video_convert_time", videoConvertTime);
                data.put("video_upload_time", videoUploadTime);
                data.put("web_link", linkNum);
                if (pollType != null) {
                    data.put("poll_type", pollType.getValue());
                }
                data.put("poll_num", pollNum);
                if (pollTime != null) {
                    data.put("poll_time", pollTime.value);
                }
                data.put("emoji_num", emojiNum);
                data.put("at_num", atNum);
                data.put("topic_num", topicNum);
                if (topicSource != null) {
                    data.put("topic_source", topicSource.value);
                }
                if (alosRepost != null) {
                    data.put("also_repost", alosRepost.getValue());
                }
                if (alsoComment != null) {
                    data.put("also_comment", alsoComment.getValue());
                }
                data.put("post_id", postId);
                data.put("repost_id", repostId);
                data.put("topic_id", topicId);
                data.put("community", community);
                data.put("retry_times", retryTimes);
                data.put("draft_id", draftId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report16(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 16);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report17(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 17);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report18(Source source, MainSource mainSource, PageType pageType, TopicSource topicSource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 18);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (topicSource != null) {
                    data.put("topic_source", topicSource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report19(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 19);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report20(Source source, MainSource mainSource, PageType pageType, TopicSource topicSource, String topicId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 20);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (topicSource != null) {
                    data.put("topic_source", topicSource.value);
                }
                data.put("topic_id", topicId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report21(Source source, MainSource mainSource, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 21);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report22(Source source, MainSource mainSource, PageType pageType, ExistState existState, int wordsNum, int pictureNum, String pictureSize,
                                    long pictureUploadTime, int videoNum, long videoSize, long videoConvertTime, long videoUploadTime, int linkNum, BooleanValue pollType,
                                    int pollNum, PollTime pollTime, int emojiNum, int atNum, int topicNum, TopicSource topicSource, BooleanValue alosRepost, BooleanValue alsoComment,
                                    String postId, String repostId, String topicId, String community,
                                    String uploadType, String sequenceId, int errorCode, String errorMessage, int retryTimes, String draftId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 22);
                if (source != null) {
                    data.put("source", source.value);
                }
                if (mainSource != null) {
                    data.put("main_source", mainSource.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (existState != null) {
                    data.put("exit_state", existState.value);
                }
                data.put("sequenceId", sequenceId);
                data.put("words_num", wordsNum);
                data.put("picture_num", pictureNum);
                data.put("picture_size", pictureSize);
                data.put("picture_upload_time", pictureUploadTime);
                data.put("video_num", videoNum);
                data.put("video_size", videoSize);
                data.put("video_convert_time", videoConvertTime);
                data.put("video_upload_time", videoUploadTime);
                data.put("web_link", linkNum);
                data.put("upload_type", uploadType);
                if (pollType != null) {
                    data.put("poll_type", pollType.getValue());
                }
                data.put("poll_num", pollNum);
                if (pollTime != null) {
                    data.put("poll_time", pollTime.value);
                }
                data.put("emoji_num", emojiNum);
                data.put("at_num", atNum);
                data.put("topic_num", topicNum);
                if (topicSource != null) {
                    data.put("topic_source", topicSource.value);
                }
                if (alosRepost != null) {
                    data.put("also_repost", alosRepost.getValue());
                }
                if (alsoComment != null) {
                    data.put("also_comment", alsoComment.getValue());
                }
                data.put("post_id", postId);
                data.put("repost_id", repostId);
                data.put("topic_id", topicId);
                data.put("community", community);
                data.put("error_code", errorCode);
                data.put("error_msg", errorMessage);
                data.put("retry_times", retryTimes);
                data.put("draft_id", draftId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report34(String draftId, ReSendFrom from) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 34);
                data.put("resent_from", from.getValue());
                data.put("draft_id", draftId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report35(String draftId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 35);
                data.put("draft_id", draftId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5001088
     * action	1、屏蔽用户
     * 2、取消屏蔽
     */
    public static class BlockUser {
        static String id = "5001088";

        public static void report1(String blockUid, String buttonFrom, String postId, String postLa, String[] postTags, String sessionId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("blocked_uid", blockUid);
                data.put("bottom_from", buttonFrom);
                data.put("post_id", postId);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("psession_id", sessionId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String blockUid, String buttonFrom, String postId, String postLa, String[] postTags, String sessionId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("blocked_uid", blockUid);
                data.put("bottom_from", buttonFrom);
                data.put("post_id", postId);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("psession_id", sessionId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Setting {
        static String id = "5001034";

        public static void report1(BooleanValue conform) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (conform != null) {
                    data.put("click_result", conform.getValue());
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /////////////////////////////////////实时上传 begin///////////////////////////////


    /**
     * https://shimo.im/sheet/HQKwKYqGSPsuXc2O/TNH1R
     * 5001089	action	1. 用户一次对APP对使用终止时上报
     */
    public static class StayTime {
        static String id = "5001089";

        public static void report1(long stayTime) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("stay_time", stayTime);
                EventManager.getInstance().addRuntimeEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * https://shimo.im/sheet/HQKwKYqGSPsuXc2O/ZoqnH
     * 1、当进程被拉活时上报（type）
     */
    public static class KeepAlive {
        static String id = "5001038";

        public enum KeepAliveType {
            BY_SELF(0), BY_VIDMATE(1);
            private int type;

            KeepAliveType(int type) {
                this.type = type;
            }

            public int getType() {
                return type;
            }
        }

        public static void report1(KeepAliveType type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (type != null) {
                    data.put("type", type.getType());
                }
                EventManager.getInstance().addRuntimeEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class FaceToFace {
        static String id = "5001036";

        public static void report1() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(int isAuthorized) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("isAuthorized", isAuthorized);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(int display_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("display_type", display_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(String toUid, String fromUid, int followStatus) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("to_user_id", toUid);
                data.put("from_user_id", fromUid);
                data.put("follow_status", followStatus);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5(int freshTime) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("fresh_time", freshTime);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * https://shimo.im/sheet/HQKwKYqGSPsuXc2O/QDrvq
     * 5001089	action	1. 用户一次对APP对使用终止时上报
     */
    public static class DeviceStatus {
        static String id = "5001046";

        public static String ACTION = "action";
        public static String ACTION_DEVICE = "device";
        public static String MEMORY_TOTAL = "memory_total";
        public static String MEMORY_FREE = "memory_free";
        public static String ROM_FREE = "rom_free";
        public static String ROM_TOTAL = "rom_total";

        public static void report1(Bundle bundle) {
            JSONObject data = new JSONObject();
            try {

                Set<String> keySet = bundle.keySet();

                for (String key : keySet) {
                    data.put(key, bundle.get(key));
                }

                EventManager.getInstance().addRuntimeEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
