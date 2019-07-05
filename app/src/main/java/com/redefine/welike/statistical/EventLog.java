package com.redefine.welike.statistical;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.language.LocalizationManager;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.business.common.GuideManager;
import com.redefine.welike.common.util.DateTimeUtil;
import com.redefine.welike.commonui.event.commonenums.PostType;
import com.redefine.welike.statistical.pagename.PageNameManager;

import java.util.Map;

/**
 * Created by daining on 2018/4/12.
 */

public class EventLog {

    private static final String TAG = "EventLog";

    /**
     * 1  选择语言页面展示
     * 2  点击选择语言(language)
     * 3  选择语言点击下一步(language)
     * 4  手机号注册登陆页面展示(page_source)
     * 5  输入完手机号点击下一步(phone,page_source)
     * 6  验证码输入(phone,page_source)
     * 7  resend按钮点击(phone,page_source,SMS_send)
     * 8  输入完验证码提交服务端验证(phone,page_source,SMS_send)
     * 9  资料页展示(phone,page_source,SMS_send)
     * 10  头像选择点击
     * 11  图片选择器展示
     * 12  图片选择确认(head,photo)
     * 13  昵称输入
     * 14  性别选择
     * 15  资料填写完成点击下一步(head,nickname,nickname_check,head,photo)
     * 16  兴趣选择页展示(vertical_list)
     * 17  兴趣选择页点击下一步(vertical,vertical_list)
     * 18  推荐人页展示(follow_list)
     * 19 推荐人页面点击全选按钮
     * 20 推荐人页点击下一步(follow_list,following)
     * 21 注册成功跳转主界面(全部附加字段)
     *
     * @deprecated @see{@link RegisterAndLogin}
     */
    @Deprecated
    public static class Login {
        static String id = "0101001";

        public enum FromPage {

            PAGE_THIRD_LOGIN(1), PAGE_MOBILE(2), PAGE_OTP(3);

            private int fromPage;

            FromPage(int fromPage) {
                this.fromPage = fromPage;
            }

            public int getFromPage() {
                return fromPage;
            }

        }

        /**
         * 1  选择语言页面展示
         */
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

        /**
         * 2 点击选择语言(language)
         *
         * @param language en / hi
         */
        public static void report2(String language) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("language", language);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 3 选择语言点击下一步(language)
         *
         * @param language en / hi
         */
        public static void report3(String language) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("language", language);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 4  手机号注册登陆页面展示(page_source)
         *
         * @param page_source 1  语言选择页
         *                    2  注销及token超时
         */
        public static void report4(int page_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("page_source", page_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 5  输入完手机号点击下一步(phone,page_source)
         *
         * @param phone       手机号
         * @param page_source 1  语言选择页
         *                    2  注销及token超时
         */
        public static void report5(String phone, int page_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("phone", phone);
                data.put("page_source", page_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 6  验证码输入(phone,page_source)
         *
         * @param phone       用户手机号码，排除+91
         * @param page_source 1  语言选择页
         *                    2  注销及token超时
         */
        public static void report6(String phone, int page_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("phone", phone);
                data.put("page_source", page_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 7  resend按钮点击(phone,page_source,SMS_send)
         *
         * @param phone       用户手机号码，排除+91
         * @param page_source 1  语言选择页
         *                    2  注销及token超时
         * @param SMS_send    在上报时机时调用了多少次发短信接口就写多少
         */
        public static void report7(String phone, int page_source, int SMS_send) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                data.put("phone", phone);
                data.put("page_source", page_source);
                data.put("SMS_send", SMS_send);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 8  输入完验证码提交服务端验证(phone,page_source,SMS_send)
         *
         * @param phone       用户手机号码，排除+91
         * @param page_source 1  语言选择页
         *                    2  注销及token超时
         * @param SMS_send    在上报时机时调用了多少次发短信接口就写多少
         */
        public static void report8(String phone, int page_source, int SMS_send) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("phone", phone);
                data.put("page_source", page_source);
                data.put("SMS_send", SMS_send);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 9  资料页展示(phone,page_source,SMS_send)
         *
         * @param phone
         * @param page_source
         * @param SMS_send
         */
        public static void report9(String phone, int page_source, int SMS_send) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                data.put("phone", phone);
                data.put("page_source", page_source);
                data.put("SMS_send", SMS_send);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 10  头像选择点击
         */
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

        /**
         * 11  图片选择器展示
         */
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

        /**
         * 12  图片选择确认(head,photo)
         *
         * @param head  0 无头像
         *              1 默认头像1
         *              2 默认头像2
         *              3 默认头像3
         *              4 默认头像4
         *              5 相册选择
         *              6 相册拍照选择
         * @param photo 1 直接使用图片
         *              2 选图片并剪裁
         *              3 拍照直接使用
         *              4 拍照并剪裁
         */
        public static void report12(int head, int photo) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 13  昵称输入
         */
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

        /**
         * 14  性别选择
         */
        public static void report14() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 14);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 15  资料填写完成点击下一步(head,nickname,nickname_check,head,photo)
         *
         * @param nickname       在上报的时机输入的用户名是什么就报什么
         * @param nickname_check 在上报时机时调用了多少次昵称检查并返回不通过的结果就写多少
         * @param head           0 无头像
         *                       1 默认头像1
         *                       2 默认头像2
         *                       3 默认头像3
         *                       4 默认头像4
         *                       5 相册选择
         *                       6 相册拍照选择
         * @param photo          1 直接使用图片
         *                       2 选图片并剪裁
         *                       3 拍照直接使用
         *                       4 拍照并剪裁
         */
        public static void report15(String nickname, int nickname_check, int head, int photo) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 15);
                data.put("nickname", nickname);
                data.put("nickname_check", nickname_check);
                data.put("head", head);
                data.put("photo", photo);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 16  兴趣选择页展示(vertical_list)
         *
         * @param vertical_list 兴趣标签列表的总个数
         * @param login_source  登陆来源：1，手机号登陆；2，Facebook登陆
         */
        public static void report16(int vertical_list, int login_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 16);
                data.put("vertical_list", vertical_list);
                data.put("login_source", login_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 17  兴趣选择页点击下一步(vertical,vertical_list)
         *
         * @param vertical      兴趣标签勾选的个数
         * @param vertical_list 兴趣标签列表的总个数
         * @param login_source  登陆来源：1，手机号登陆；2，Facebook登陆
         */
        public static void report17(int vertical, int vertical_list, int login_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 17);
                data.put("vertical", vertical);
                data.put("vertical_list", vertical_list);
                data.put("login_source", login_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 18  推荐人页展示(follow_list)
         *
         * @param follow_list  推荐关注人列表下发的个数
         * @param login_source 登陆来源：1，手机号登陆；2，Facebook登陆
         */
        public static void report18(int follow_list, int login_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 18);
                data.put("follow_list", follow_list);
                data.put("login_source", login_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 19 推荐人页面点击全选按钮
         *
         * @param login_source 登陆来源：1，手机号登陆；2，Facebook登陆
         */
        public static void report19(int login_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 19);
                data.put("login_source", login_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 20 推荐人页点击下一步(follow_list,following)
         *
         * @param following    关注的人勾选的个数
         * @param follow_list  推荐关注人列表下发的个数
         * @param login_source 登陆来源：1，手机号登陆；2，Facebook登陆
         */
        public static void report20(int following, int follow_list, int login_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 20);
                data.put("following", following);
                data.put("follow_list", follow_list);
                data.put("login_source", login_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 21 注册成功跳转主界面(全部附加字段)
         *
         * @param language       en/hi
         * @param login_source   1 手机号登陆, 2 Facebook登陆
         * @param page_source    1  语言选择页
         *                       2  注销及token超时
         * @param phone_load     能读取到用户手机号码直接记在这里，排除+91，双卡的中间以|分割
         * @param phone          用户手机号码，排除+91
         * @param phone_source   1 用户选择
         *                       2 用户输入
         * @param SMS_send       在上报时机时调用了多少次发短信接口就写多少
         * @param nickname       在上报的时机输入的用户名是什么就报什么
         * @param nickname_check 在上报时机时调用了多少次昵称检查并返回不通过的结果就写多少
         * @param head           0 无头像
         *                       1 默认头像1
         *                       2 默认头像2
         *                       3 默认头像3
         *                       4 默认头像4
         *                       5 相册选择
         *                       6 相册拍照选择
         * @param photo          1 直接使用图片
         *                       2 选图片并剪裁
         *                       3 拍照直接使用
         *                       4 拍照并剪裁
         * @param vertical       兴趣标签勾选的个数
         * @param vertical_list  兴趣标签列表的总个数
         * @param following      关注的人勾选的个数
         * @param follow_list    推荐关注人列表下发的个数
         */
        public static void report21(String language, int login_source, int page_source,
                                    String phone_load, String phone, int phone_source,
                                    int SMS_send, String nickname, int nickname_check,
                                    int head, int photo, int vertical, int SMS_check,
                                    int vertical_list, int following, int follow_list) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 21);
                data.put("language", language);
                data.put("login_source", login_source);
                data.put("page_source", page_source);
                data.put("phone_load", phone_load);
                data.put("phone", phone);
                data.put("phone_source", phone_source);
                data.put("SMS_send", SMS_send);
                data.put("nickname", nickname);
                data.put("nickname_check", nickname_check);
                data.put("head", head);
                data.put("photo", photo);
                data.put("vertical", vertical);
                data.put("SMS_check", SMS_check);
                data.put("vertical_list", vertical_list);
                data.put("following", following);
                data.put("follow_list", follow_list);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 22 验证码页面展示（page_source）
         * 已废弃，跟6重复。
         *
         * @param page_source 1  语言选择页
         *                    2  注销及token超时
         */
        @Deprecated
        public static void report22(int page_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 22);
                data.put("page_source", page_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 23 Facebook登录按钮点击（page_source）
         *
         * @param page_source 1  语言选择页
         *                    2  注销及token超时
         * @param from_page   1 第三方登陆页 2 手机号输入页 3 验证码输入页
         */
        public static void report23(int page_source, int from_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 23);
                data.put("page_source", page_source);
                data.put("from_page", from_page);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 24 Facebook登录失败toast弹出（page_source）
         *
         * @param page_source 1  语言选择页
         *                    2  注销及token超时
         * @param from_page   1 第三方登陆页 2 手机号输入页 3 验证码输入页
         */
        public static void report24(int page_source, int from_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 24);
                data.put("page_source", page_source);
                data.put("from_page", from_page);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 25 google账号登录按钮点击(page_source）
         *
         * @param page_source
         */
        public static void report25(int page_source, int from_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 25);
                data.put("page_source", page_source);
                data.put("from_page", from_page);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 26 google账号登录失败（page_source）
         *
         * @param page_source
         * @param errorCode   失败的错误码
         */
        public static void report26(int page_source, int errorCode, int from_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 26);
                data.put("page_source", page_source);
                data.put("from_page", from_page);
                data.put("errorCode", errorCode);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 27 选择兴趣和推荐关注人页面点击重试按钮（page_source,login_source）
         *
         * @param page_source
         * @param login_source
         */
        public static void report27(int page_source, int login_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 27);
                data.put("page_source", page_source);
                data.put("login_source", login_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 28 免登录发现页展示（page_source）
         *
         * @param page_source
         */
        public static void report28(int page_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 28);
                data.put("page_source", page_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 29 免登陆发现页内按钮点击 （button_type）
         *
         * @param button_type 0 关注按钮
         *                    1 头像按钮
         *                    2 post卡片
         *                    3 tab切换
         *                    4 转发
         *                    5 评论
         *                    6 赞
         *                    7 分享
         *                    8 hashtag
         */
        public static void report29(int button_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 29);
                data.put("button_type", button_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 30 Facebook登录已取消（page_source）
         *
         * @param page_source
         */
        public static void report30(int page_source, int from_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 30);
                data.put("page_source", page_source);
                data.put("from_page", from_page);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 31 Google登录已取消（page_source）
         *
         * @param page_source
         */
        public static void report31(int page_source, int from_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 31);
                data.put("page_source", page_source);
                data.put("from_page", from_page);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 32 truecaller登录按钮点击（page_source）
         *
         * @param page_source
         */
        public static void report32(int page_source, int from_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 32);
                data.put("page_source", page_source);
                data.put("from_page", from_page);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 33 truecaller登录失败（page_source）
         *
         * @param page_source
         */
        public static void report33(int page_source, int from_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 33);
                data.put("page_source", page_source);
                data.put("from_page", from_page);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 1 发布器界面展示(source,main_source,page_type)
     * 2 相机按钮点击(source,main_source,page_type)
     * 3 @按钮点击(source,main_source,page_type)
     * 4 链接按钮点击(source,main_source,page_type)
     * 5 location按钮点击(source,main_source,page_type)
     * 6 @人搜索页展示(source,main_source,page_type,at_source)
     * 7 @人有输入(source,main_source,page_type,at_source)
     * 8 @人Search online(source,main_source,page_type,at_source)
     * 9 @人结果点击(source,main_source,page_type,at_source)
     * 10 表情按钮点击(source,main_source,page_type)
     * 11 链接按钮点击-链接输入框展示(source,main_source,page_type,at_source)
     * 12 链接输入框点击确认,链接格式错误(source,main_source,page_type,at_source)
     * 13 链接输入框点击确认,链接格式正确(source,main_source,page_type,at_source)
     * 14 点击SEND按钮发布(全部字段)
     * 15 发布器界面退出(全部字段)
     */
    public static class Publish {
        static String id = "0102001";

        /**
         * 1 发布器界面展示(source,main_source,page_type)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report1(int source, int main_source, int page_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2 相机按钮点击(source,main_source,page_type)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report2(int source, int main_source, int page_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 3 @按钮点击(source,main_source,page_type)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report3(int source, int main_source, int page_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 4 链接按钮点击(source,main_source,page_type)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report4(int source, int main_source, int page_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 5 location按钮点击(source,main_source,page_type)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report5(int source, int main_source, int page_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 6 @人搜索页展示(source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report6(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 7 @人有输入(source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report7(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 8 @人Search online(source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report8(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 9 @人结果点击(source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report9(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 10 表情按钮点击(source,main_source,page_type)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report10(int source, int main_source, int page_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 10);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 11 链接按钮点击-链接输入框展示(source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report11(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 12 链接输入框点击确认,链接格式错误(source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report12(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 13 链接输入框点击确认,链接格式正确(source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report13(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 13);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 14 点击SEND按钮发布(全部字段)
         *
         * @param source        1.从主界面进入
         *                      2.从card进入 转发、评论
         *                      3.从post detail页进入 转发、评论
         *                      4.从comment detail页进入 转发、评论
         *                      5.来自草稿箱来源的进入发布器次数
         * @param main_source   0 非主界面进入发布器
         *                      1 HOME
         *                      2 Discover
         *                      3 Message
         *                      4 Me
         * @param page_type     1 New post发布器
         *                      2 Comment发布器
         *                      3 Repost发布器
         * @param exit_state    1 点击Send发布退出
         *                      2 点击草稿箱并退出
         *                      3 点击返回按钮退出
         * @param words_num     用户输入多少个字节就算多少个字节，不算字符数，算字节数
         * @param picture_num   图片个数，没有图片为0，最大9
         * @param video_num     视频个数，没有视频为0，最大为1
         * @param web_link      链接个数，没有链接为空
         * @param web_link_type 1 已解析小卡,2 未解析
         * @param emoji_num     几个表情就是多少 没有表情为0
         * @param at_num        没有@人为0
         * @param at_source     1 New post发布器
         *                      2 Comment发布器
         *                      3 Repost发布器
         * @param also_repost   0 未勾选,1 勾选
         * @param also_comment  0 未勾选,1 勾选
         * @param post_id       新发布post id(在发送成功时上报)
         * @param repost_id     评论或转发时,带上评论所在的postid或被转发的postid
         */
        public static void report14(int source, int main_source, int page_type,
                                    int exit_state, int words_num, int picture_num,
                                    int video_num, int web_link, int web_link_type,
                                    int emoji_num, int at_num, int at_source,
                                    int also_repost, int also_comment, String post_id, String repost_id,
                                    int poll_type, int poll_num, int poll_time, int topic_num,
                                    int topic_source, String topic_id) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 14);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("exit_state", exit_state);
                data.put("words_num", words_num);
                data.put("picture_num", picture_num);
                data.put("video_num", video_num);
                data.put("web_link", web_link);
                data.put("web_link_type", web_link_type);
                data.put("emoji_num", emoji_num);
                data.put("at_num", at_num);
                data.put("at_source", at_source);
                data.put("also_repost", also_repost);
                data.put("also_comment", also_comment);
                data.put("post_id", post_id);
                data.put("repost_id", repost_id);
                data.put("poll_type", poll_type);
                data.put("poll_num", poll_num);
                data.put("poll_time", poll_time);
                data.put("topic_num", topic_num);
                data.put("topic_source", topic_source);
                data.put("topic_id", topic_id);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 15 发布器界面退出(全部字段)
         *
         * @param source        1.从主界面进入
         *                      2.从card进入 转发、评论
         *                      3.从post detail页进入 转发、评论
         *                      4.从comment detail页进入 转发、评论
         *                      5.来自草稿箱来源的进入发布器次数
         * @param main_source   0 非主界面进入发布器
         *                      1 HOME
         *                      2 Discover
         *                      3 Message
         *                      4 Me
         * @param page_type     1 New post发布器
         *                      2 Comment发布器
         *                      3 Repost发布器
         * @param exit_state    1 点击Send发布退出
         *                      2 点击草稿箱并退出
         *                      3 点击返回按钮退出
         * @param words_num     用户输入多少个字节就算多少个字节，不算字符数，算字节数
         * @param picture_num   图片个数，没有图片为0，最大9
         * @param video_num     视频个数，没有视频为0，最大为1
         * @param web_link      链接个数，没有链接为空
         * @param web_link_type 1 已解析小卡,2 未解析
         * @param emoji_num     几个表情就是多少 没有表情为0
         * @param at_num        没有@人为0
         * @param at_source     1 New post发布器
         *                      2 Comment发布器
         *                      3 Repost发布器
         * @param also_repost   0 未勾选,1 勾选
         * @param also_comment  0 未勾选,1 勾选
         * @param post_id       新发布post id(在发送成功时上报)
         * @param repost_id     评论或转发时,带上评论所在的postid或被转发的postid
         */
        public static void report15(int source, int main_source, int page_type,
                                    int exit_state, int words_num, int picture_num,
                                    int video_num, int web_link, int web_link_type,
                                    int emoji_num, int at_num, int at_source,
                                    int also_repost, int also_comment, String post_id, String repost_id,
                                    int poll_type, int poll_num, int poll_time, int topic_num,
                                    int topic_source, String topic_id) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 15);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("exit_state", exit_state);
                data.put("words_num", words_num);
                data.put("picture_num", picture_num);
                data.put("video_num", video_num);
                data.put("web_link", web_link);
                data.put("web_link_type", web_link_type);
                data.put("emoji_num", emoji_num);
                data.put("at_num", at_num);
                data.put("at_source", at_source);
                data.put("also_repost", also_repost);
                data.put("also_comment", also_comment);
                data.put("post_id", post_id);
                data.put("repost_id", repost_id);
                data.put("poll_type", poll_type);
                data.put("poll_num", poll_num);
                data.put("poll_time", poll_time);
                data.put("topic_num", topic_num);
                data.put("topic_source", topic_source);
                data.put("topic_id", topic_id);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 16 点击SEND按钮并且Post发布成功(全部字段)
         *
         * @param source        1.从主界面进入
         *                      2.从card进入 转发、评论
         *                      3.从post detail页进入 转发、评论
         *                      4.从comment detail页进入 转发、评论
         *                      5.来自草稿箱来源的进入发布器次数
         * @param main_source   0 非主界面进入发布器
         *                      1 HOME
         *                      2 Discover
         *                      3 Message
         *                      4 Me
         * @param page_type     1 New post发布器
         *                      2 Comment发布器
         *                      3 Repost发布器
         * @param exit_state    1 点击Send发布退出
         *                      2 点击草稿箱并退出
         *                      3 点击返回按钮退出
         * @param words_num     用户输入多少个字节就算多少个字节，不算字符数，算字节数
         * @param picture_num   图片个数，没有图片为0，最大9
         * @param video_num     视频个数，没有视频为0，最大为1
         * @param web_link      链接个数，没有链接为空
         * @param web_link_type 1 已解析小卡,2 未解析
         * @param emoji_num     几个表情就是多少 没有表情为0
         * @param at_num        没有@人为0
         * @param at_source     1 New post发布器
         *                      2 Comment发布器
         *                      3 Repost发布器
         * @param also_repost   0 未勾选,1 勾选
         * @param also_comment  0 未勾选,1 勾选
         * @param post_id       新发布post id(在发送成功时上报)
         * @param repost_id     评论或转发时,带上评论所在的postid或被转发的postid
         * @param poll_type     1. 无图 2. 有图
         * @param poll_num      投票有多少选项，数字
         * @param poll_time     1. 1天   2. 3天   3. 1周   4. 1月   5. 不限期
         * @param topic_num     topic的数量
         * @param topic_source  1.话题自带topic
         *                      2.任务自带topic
         *                      3.发布器推荐topic
         *                      4.手动输入选择topic
         *                      5.用户新建topic
         * @param topic_id      话题id, 超过一个用逗号隔开
         */
        public static void report16(int source, int main_source, int page_type,
                                    int exit_state, int words_num, int picture_num,
                                    int video_num, int web_link, int web_link_type,
                                    int emoji_num, int at_num, int at_source,
                                    int also_repost, int also_comment, String post_id, String repost_id,
                                    int poll_type, int poll_num, int poll_time, int topic_num,
                                    int topic_source, String topic_id, String picture_size,
                                    int picture_upload_time, float video_size, int video_duration,
                                    int video_convert_time, int video_upload_time) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 16);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("exit_state", exit_state);
                data.put("words_num", words_num);
                data.put("picture_num", picture_num);
                data.put("video_num", video_num);
                data.put("web_link", web_link);
                data.put("web_link_type", web_link_type);
                data.put("emoji_num", emoji_num);
                data.put("at_num", at_num);
                data.put("at_source", at_source);
                data.put("also_repost", also_repost);
                data.put("also_comment", also_comment);
                data.put("post_id", post_id);
                data.put("repost_id", repost_id);
                data.put("poll_type", poll_type);
                data.put("poll_num", poll_num);
                data.put("poll_time", poll_time);
                data.put("topic_num", topic_num);
                data.put("topic_source", topic_source);
                data.put("topic_id", topic_id);
                data.put("picture_size", picture_size);
                data.put("video_duration", video_duration);
                data.put("picture_upload_time", picture_upload_time);
                data.put("video_size", video_size);
                data.put("video_convert_time", video_convert_time);
                data.put("video_upload_time", video_upload_time);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 17 投票按钮点击 (source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report17(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 17);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 18 发布器导航页推荐话题点击 （topic_id)
         *
         * @param topic_id 话题id, 超过一个用逗号隔开
         */
        public static void report18(String topic_id) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 18);
                data.put("topic_id", topic_id);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 19 话题按钮（#）点击 (source,main_source,page_type)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         */
        public static void report19(int source, int main_source, int page_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 19);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 20 #话题搜索页展示(source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器e
         */
        public static void report20(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 20);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 21 #话题有输入(source,main_source,page_type,at_source)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器e
         */
        public static void report21(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 21);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 22 #话题结果点击(source,main_source,page_type,at_source, topic_id)
         *
         * @param source      1.从主界面进入
         *                    2.从card进入 转发、评论
         *                    3.从post detail页进入 转发、评论
         *                    4.从comment detail页进入 转发、评论
         *                    5.来自草稿箱来源的进入发布器次数
         * @param main_source 0 非主界面进入发布器
         *                    1 HOME
         *                    2 Discover
         *                    3 Message
         *                    4 Me
         * @param page_type   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器
         * @param at_source   1 New post发布器
         *                    2 Comment发布器
         *                    3 Repost发布器e
         * @param topic_id    话题id, 超过一个用逗号隔开
         */
        public static void report22(int source, int main_source, int page_type, int at_source, String topic_id) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 22);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                data.put("topic_id", topic_id);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 23 #搜索页热门话题点击(source,main_source,page_type,at_source, topic_id)
         *
         * @param source
         * @param main_source
         * @param page_type
         * @param at_source
         * @param topic_id
         */
        public static void report23(int source, int main_source, int page_type, int at_source, String topic_id) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 23);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                data.put("topic_id", topic_id);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 24 Add topic 按钮点击 (source,main_source,page_type,at_source)
         *
         * @param source
         * @param main_source
         * @param page_type
         * @param at_source
         */
        public static void report24(int source, int main_source, int page_type, int at_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 24);
                data.put("source", source);
                data.put("main_source", main_source);
                data.put("page_type", page_type);
                data.put("at_source", at_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 25 add topic 页面返回（topic_id, community，add_topic_type）
         *
         * @param topic_id
         * @param community
         * @param add_topic_type
         */
        public static void report25(String topic_id, String community, int add_topic_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 25);
                data.put("topic_id", topic_id);
                data.put("community", community);
                data.put("add_topic_type", add_topic_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1 收到FCM消息(push_type,seqid)
     * 2 解析正常并处理(push_type,seqid,result)
     * 3 点击(push_type,seqid,result)
     */
    public static class Push {
        static String id = "0104001";

        /**
         * 1 收到FCM消息(push_type,seqid)
         *
         * @param push_type 1 @
         *                  2 转发评论
         *                  3 评论博文
         *                  4 评论回复
         *                  5 回复评论
         *                  6 点赞博文
         *                  7 点赞回复
         *                  8 点赞评论
         *                  9 关注
         *                  10 IM消息
         * @param seqid     push的id
         */
        public static void report1(int push_type, String seqid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("push_type", push_type);
                data.put("seqid", seqid);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2 解析正常并处理(push_type,seqid,result)
         *
         * @param push_type 1 @
         *                  2 转发评论
         *                  3 评论博文
         *                  4 评论回复
         *                  5 回复评论
         *                  6 点赞博文
         *                  7 点赞回复
         *                  8 点赞评论
         *                  9 关注
         *                  10 IM消息
         * @param seqid     push的id
         * @param result    1 弹出 2 主界面前台不弹出 3 XXX原因不弹出
         */
        public static void report2(int push_type, String seqid, int result) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("push_type", push_type);
                data.put("seqid", seqid);
                data.put("result", result);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 3 点击(push_type,seqid,result)
         *
         * @param push_type 1 @
         *                  2 转发评论
         *                  3 评论博文
         *                  4 评论回复
         *                  5 回复评论
         *                  6 点赞博文
         *                  7 点赞回复
         *                  8 点赞评论
         *                  9 关注
         *                  10 IM消息
         * @param seqid     push的id
         * @param result    1 弹出 2 主界面前台不弹出 3 XXX原因不弹出
         */
        public static void report3(int push_type, String seqid, int result) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("push_type", push_type);
                data.put("seqid", seqid);
                data.put("result", result);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1 收到FCM消息(push_type,seqid)
     * 2 解析正常并处理(push_type,seqid,result)
     * 3 点击(push_type,seqid,result)
     */
    public static class Guide {
        static String id = "0106001";

        public static void report(@GuideManager.GuideType String type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("type", type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1 页面展示（activity name）
     * 2 页面切换 （activity name，stay time）
     */
    public static class ShowPage {

        static String id = "0105001";

        /**
         * 1 页面展示（activity name）
         *
         * @param activityName 页面名称
         */
        public static void report1(String activityName) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("activityName", PageNameManager.INSTANCE.getActivityAlias(activityName));
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2 页面切换
         *
         * @param activityName 页面名称
         * @param staytime     页面停留时长
         */
        public static void report2(String activityName, long staytime) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("activityName", PageNameManager.INSTANCE.getActivityAlias(activityName));
                data.put("staytime", staytime);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1.下拉刷新
     * 2.上划加载
     * 3.打开默认刷新
     * 4.点按钮刷新
     */
    public static class Feed {

        public static enum FeedSource {
            MAIN(1), DISCOVER_HOT(2), DISCOVER_LATEST(3), SEARCH_LATEST(4), SEARCH_POST(5), LOCATION_HOT(6), LOCATION_LATEST(7), TOPIC_HOT(8), TOPIC_LATEST(9), MINE_PROFILE_POST(10), MINE_PROFILE_LIKE(11), CUSTOM_PROFILE_POST(12), CUSTOM_PROFILE_LIKE(13), ME_MY_LIKE(14), POST_DETAIL(15), HOT_24_ASSIGNMENT(16), TOPIC_TOP(17);

            private final int mIndex;

            FeedSource(int i) {
                mIndex = i;
            }

            public int getIndex() {
                return mIndex;
            }
        }

        static String refreshId = "0105002";

        /**
         * 1.下拉刷新
         *
         * @param type       1.主信息流
         *                   2.发现hot
         *                   3.发现latest
         *                   4.搜索页latest
         *                   5.搜索页posts
         *                   6.location
         *                   7.个人posts
         *                   8.个人profile-mylike
         *                   9.me页-mylike
         * @param list_total 下发的博文条数
         * @param read_total 已消费的博文数
         */
        public static void report1(String type, int list_total, int read_total) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                EventManager.getInstance().addEvent(refreshId, data);
                LogUtil.d(TAG, refreshId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report1(String type, int list_total, int read_total, int hot, int operation, int newCount, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                data.put("hot", hot);
                data.put("operation", operation);
                data.put("new", newCount);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(refreshId, data);
                LogUtil.d(TAG, refreshId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2.上划加载
         *
         * @param type       1.主信息流
         *                   2.发现hot
         *                   3.发现latest
         *                   4.搜索页latest
         *                   5.搜索页posts
         *                   6.location
         *                   7.个人posts
         *                   8.个人profile-mylike
         *                   9.me页-mylike
         * @param list_total 下发的博文条数
         * @param read_total 已消费的博文数
         */
        public static void report2(String type, int list_total, int read_total) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                EventManager.getInstance().addEvent(refreshId, data);
                LogUtil.d(TAG, refreshId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String type, int list_total, int read_total, int hot, int operation, int newCount, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                data.put("hot", hot);
                data.put("operation", operation);
                data.put("new", newCount);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(refreshId, data);
                LogUtil.d(TAG, refreshId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 3.打开默认刷新
         *
         * @param type       1.主信息流
         *                   2.发现hot
         *                   3.发现latest
         *                   4.搜索页latest
         *                   5.搜索页posts
         *                   6.location
         *                   7.个人posts
         *                   8.个人profile-mylike
         *                   9.me页-mylike
         * @param list_total 下发的博文条数
         * @param read_total 已消费的博文数
         */
        public static void report3(String type, int list_total, int read_total) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                EventManager.getInstance().addEvent(refreshId, data);
                LogUtil.d(TAG, refreshId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(String type, int list_total, int read_total, int hot, int operation, int newCount, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                data.put("hot", hot);
                data.put("operation", operation);
                data.put("new", newCount);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(refreshId, data);
                LogUtil.d(TAG, refreshId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 4.点按钮刷新
         *
         * @param type       1.主信息流
         *                   2.发现hot
         *                   3.发现latest
         *                   4.搜索页latest
         *                   5.搜索页posts
         *                   6.location
         *                   7.个人posts
         *                   8.个人profile-mylike
         *                   9.me页-mylike
         * @param list_total 下发的博文条数
         * @param read_total 已消费的博文数
         */
        public static void report4(String type, int list_total, int read_total) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                EventManager.getInstance().addEvent(refreshId, data);
                LogUtil.d(TAG, likeId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(String type, int list_total, int read_total, int hot, int operation, int newCount, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                data.put("hot", hot);
                data.put("operation", operation);
                data.put("new", newCount);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(refreshId, data);
                LogUtil.d(TAG, refreshId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        static String likeId = "0105003";

        /**
         * 1.全局点赞（点按和长按释放后上报该post的id，以及用户的exp）
         *
         * @param postId    post的id
         * @param exp       用户的exp
         * @param exp_end
         * @param source
         * @param post_type
         */
        public static void report5(String postId, long exp, long exp_end, int source, int post_type, String strategy, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("postId", postId);
                data.put("exp", exp);
                data.put("exp_end", exp_end);
                data.put("source", source);
                data.put("post_type", post_type);
                data.put("strategy", strategy);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(likeId, data);
                LogUtil.d(TAG, likeId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        static String commentId = "0105005";

        /**
         * 1.点击转发按钮
         *
         * @param source    进入转发发布器的来源
         *                  1.主信息流
         *                  2.发现hot
         *                  3.发现latest
         *                  4.搜索页latest
         *                  5.搜索页posts
         *                  6.location流-hot
         *                  7.location流-latest
         *                  8.话题流-hot
         *                  9.话题流-latest
         *                  10.个人profile主人态-myposts
         *                  11.个人profile主人态-mylike
         *                  12.个人profile客人态-myposts
         *                  13.个人profile客人态-mylike
         *                  14.me页-mylike
         *                  ---------------------
         *                  15.Postdetail页-底部按钮
         *                  16.postdetail页-转发列表
         *                  17.postdetail页-评论列表
         *                  ----------------------
         *                  18.commentdetail页-底部按钮
         *                  20.commentdetail页-评论列表
         * @param post_id   被转发博文的id
         * @param post_type
         */
        public static void report6(int source, String post_id, int post_type, String strategy, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("post_id", post_id);
                data.put("source", source);
                data.put("post_type", post_type);
                data.put("strategy", strategy);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(commentId, data);
                LogUtil.d(TAG, commentId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2.点击评论按钮
         *
         * @param source
         * @param post_id
         * @param post_type
         */
        public static void report7(int source, String post_id, int post_type, String strategy, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("post_id", post_id);
                data.put("source", source);
                data.put("post_type", post_type);
                data.put("strategy", strategy);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(commentId, data);
                LogUtil.d(TAG, commentId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2.点击post卡片（非互动区域，包含event下所有参数）
         *
         * @param source
         * @param post_id
         */
        static String POST_CLICK_ID = "0105008";

        public static void report8(String source,
                                   String post_uid, String rootpost_uid,
                                   String post_id, String rootpost_id,
                                   int click_area,
                                   int post_type, String strategy, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("post_id", post_id);
                data.put("post_uid", post_uid);
                data.put("rootpost_uid", rootpost_uid);
                data.put("rootpost_id", rootpost_id);
                data.put("click_area", click_area);
                data.put("click_source", source);
                data.put("post_type", post_type);
                data.put("strategy", strategy);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(POST_CLICK_ID, data);
                LogUtil.d(TAG, POST_CLICK_ID + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        static String POST_VIEW_ID = "0105007";

        /**
         * 9.点击post卡片（非互动区域，包含event下所有参数）
         *
         * @param post_id
         * @param view_time 展示时长 毫秒
         */
        public static void report9(String post_id, String source, long view_time) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("view_time", view_time);
                data.put("post_id", post_id);
                data.put("view_source", source);
                EventManager.getInstance().addEvent(POST_VIEW_ID, data);
                LogUtil.d(TAG, POST_VIEW_ID + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9(String postUid, String rootpostUid, String post_id, String rootPostId, PostType postType, String source,
                                   long viewTime, String poolCode, String operationType, String postLa, String[] postTags, String rootPostLa, String[] rootPostTags, String sequenceId) {
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
                EventManager.getInstance().addEvent(POST_VIEW_ID, data);
                LogUtil.d(TAG, POST_VIEW_ID + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 9.点击post卡片（非互动区域，包含event下所有参数）
         *
         * @param post_id
         * @param view_time 展示时长 毫秒
         */
        public static void report10(String post_id, String source, long view_time) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("view_time", view_time);
                data.put("post_id", post_id);
                data.put("view_source", source);
                EventManager.getInstance().addEvent(POST_VIEW_ID, data);
                LogUtil.d(TAG, POST_VIEW_ID + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report10(String postUid, String rootpostUid, String post_id, String rootPostId, PostType postType, long viewTime,
                                   String poolCode, String operationType, String postLa, String[] postTags, String rootPostLa, String[] rootPostTags, String sequenceId) {
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
                EventManager.getInstance().addEvent(POST_VIEW_ID, data);
                LogUtil.d(TAG, POST_VIEW_ID + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 日活统计
     */
    public static class LaunchApp {

        static String id = "0101002";

        /**
         * 日活统计
         *
         * @param open_source 1.图标点开
         *                    2.push点开
         *                    3.后台返回
         *                    4.其他link跳转
         */
        public static void report1(int open_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("open_source", open_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 应用处于前台
         */
        public static void report2() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
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

        static String id = "0101003";

        /**
         * 新装统计
         *
         * @param install_type   1.首次安装 2.升级安装
         * @param install_source 1.apk安装 2.GP安装
         */
        public static void report1(int install_type, int install_source) {
            JSONObject data = new JSONObject();
            try {
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
     * 1 分享卡片展示
     * 2 分享渠道点击
     */
    public static class Share {
        static String id = "0101005";

        /**
         * 1 分享卡片展示
         *
         * @param source 1 post detail
         *               2 shareapp
         *               3 webview
         *               4 profile
         *               5 Topic
         */
        public static void report1(int source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("source", source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2 分享渠道点击
         *
         * @param channel 1 whatsapp 2 instagram 3 facebook 4 copylink 5 more 6 cancel
         * @param source  1 post detail
         *                2 shareapp
         *                3 webview
         *                4 profile
         *                5 Topic
         */
        public static void report2(int channel, int source, int channel_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("channel", channel);
                data.put("source", source);
                data.put("channel_page", channel_page);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 3 点击卡片左下角的分享到whatsapp按钮（frompage）
         *
         * @param frompage 是从哪个页面触发的（post detail； profile；topic、unloginhome）	1、home 2、profile 3、发现页feed流中点击（垂类tab,例如3_1） 4、topic 5、其他
         */
        public static void report3(String frompage, int channel_page, String feedname, int postType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("frompage", frompage);
                data.put("channel_page", channel_page);
                data.put("feedname", feedname);
                data.put("post_type", postType);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 4 点击分享选项（share_type,source）
         *
         * @param shareType
         * @param source
         */
        public static void report4(int shareType, int source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("share_type", shareType);
                data.put("source", source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 5 点击全屏视频页分享按钮
         */
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

        /**
         * 6 邀请联系人
         */
        public static void report6(String mobile) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("mobile", mobile);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * share_channel	1、分享到whatsapp 2、分享到facebook 3、分享到instagram 4、分享到instgram 5、copylink 6、其他
     * content_type	1、post 2、shareProfile 3、shareApp 4、shareTopic 5、shareH5page
     * post_type	1、视频类 2、图文类post 3、文字类post 4、长文 5、投票 6、post status类 7、其他类型
     * videopost_type	1、视频文件 2、post链接
     * sharefrom	1、来自post卡片右下角分享按钮 2、来自全屏视频播放页分享按钮 3、postdetail页右下角分享按钮 4、来自各种页面的分享弹窗
     * popfrom	1、postdetail页分享弹窗 2、post卡片分享弹窗 3、profile页分享弹窗 4、shareapp分享弹窗 5、topic页分享弹窗 6、H5page分享弹窗
     * is_login	0、未登录状态 1、已登录状态
     * is_success	0、分享失败 1、分享成功 2、unknow
     */
    public static class NewShare {
        static String mId = "0102002";

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
        public static void report1(int shareChannel, int contentType, int postType, int videoPostType, int shareFrom, int popPage, int isSuccess) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("share_channel", shareChannel);
                data.put("content_type", contentType);
                data.put("post_type", postType);
                data.put("videopost_type", videoPostType);
                data.put("share_from", shareFrom);
                data.put("pop_page", popPage);
                data.put("is_success", isSuccess);
                EventManager.getInstance().addEvent(mId, data);
                LogUtil.d(TAG, mId + data.toString());
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

        static String mId = "0107001";

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
     * 新装统计
     */
    public static class VIDEO {

        static String id = "0109001";

        /**
         * 视频播放首帧时间
         *
         * @param url
         * @param videoFirstFrame
         * @param audioFirstFrame
         * @param playerType
         */
        public static void report1(String url, String videoFirstFrame, String audioFirstFrame
                , String connectStartTime, String connectReadyTime, String exitTime, String playerType) {
            JSONObject data = new JSONObject();
            try {
                data.put("url", url);
                data.put("time", DateTimeUtil.INSTANCE.formatLogDateTime(System.currentTimeMillis()));
                if (AccountManager.getInstance().getAccount() != null) {
                    data.put("dn", AccountManager.getInstance().getAccount().getUid());
                }
                data.put("video_first_frame", videoFirstFrame);
                data.put("audio_first_frame", audioFirstFrame);
                data.put("connect_start_time", connectStartTime);
                data.put("connect_ready_time", connectReadyTime);
                data.put("exit_time", exitTime);
                data.put("player_type", playerType);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(Map<String, String> logs) {
            JSONObject data = new JSONObject();
            try {
                data.putAll(logs);
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

        public static void report6(int button_type, String postId, String postUid, String postLa, String[] postTags) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("button_type", button_type);
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
     * 1 Message页面展示
     * 2 @入口点击
     * 3 Comment入口点击
     * 4 Like入口点击
     * 5 Stranger入口点击
     * 6 IM会话点击
     */
    public static class IM {

        static String eventId = "0103001";

        /**
         * 1 Message页面展示
         *
         * @param mention_num  @红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param comment_num  comment红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param like_num     like红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param stranger_num 陌生人分组是否有红点,0 无红点,1 有红点
         */
        public static void report1(int mention_num, int comment_num, int like_num,
                                   int stranger_num) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("mention_num", mention_num);
                data.put("comment_num", comment_num);
                data.put("like_num", like_num);
                data.put("stranger_num", stranger_num);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2 @入口点击
         *
         * @param mention_num  @红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param comment_num  comment红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param like_num     like红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param stranger_num 陌生人分组是否有红点,0 无红点,1 有红点
         */
        public static void report2(int mention_num, int comment_num, int like_num,
                                   int stranger_num) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("mention_num", mention_num);
                data.put("comment_num", comment_num);
                data.put("like_num", like_num);
                data.put("stranger_num", stranger_num);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 3 Comment入口点击
         *
         * @param mention_num  @红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param comment_num  comment红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param like_num     like红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param stranger_num 陌生人分组是否有红点,0 无红点,1 有红点
         */
        public static void report3(int mention_num, int comment_num, int like_num,
                                   int stranger_num) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("mention_num", mention_num);
                data.put("comment_num", comment_num);
                data.put("like_num", like_num);
                data.put("stranger_num", stranger_num);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 4 Like入口点击
         *
         * @param mention_num  @红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param comment_num  comment红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param like_num     like红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param stranger_num 陌生人分组是否有红点,0 无红点,1 有红点
         */
        public static void report4(int mention_num, int comment_num, int like_num,
                                   int stranger_num) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("mention_num", mention_num);
                data.put("comment_num", comment_num);
                data.put("like_num", like_num);
                data.put("stranger_num", stranger_num);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 5 Stranger入口点击
         *
         * @param mention_num  @红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param comment_num  comment红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param like_num     like红点展示数量, 0 无红点,N 具体红点展示数 最大99
         * @param stranger_num 陌生人分组是否有红点,0 无红点,1 有红点
         */
        public static void report5(int mention_num, int comment_num, int like_num,
                                   int stranger_num) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("mention_num", mention_num);
                data.put("comment_num", comment_num);
                data.put("like_num", like_num);
                data.put("stranger_num", stranger_num);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 6 IM会话点击
         *
         * @param chat_num    点进去IM会话时  当前会话的红点数,0 无红点,N 具体红点展示数 最大99
         * @param chat_uid    IM会话对方的uid
         * @param chat_source IM会话来源 1 Message页面,2 Stranger页
         * @param chat_state  会话关系状态 1 陌生人,2 好友
         */
        public static void report6(int chat_num, String chat_uid,
                                   int chat_source, int chat_state) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("chat_num", chat_num);
                data.put("chat_uid", chat_uid);
                data.put("chat_source", chat_source);
                data.put("chat_state", chat_state);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(String pushId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("pushId", pushId);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        static String sendId = "01030022";

        public static void report9(String last_message, int last_message_type, int message_type,
                                   int emoji_num, String pre_uid, int word_num, int chat_state,
                                   int chat_locate, int photo_num, int photo_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("last_message", last_message);
                data.put("last_message_type", last_message_type);
                data.put("message_type", message_type);
                data.put("emoji_num", emoji_num);
                data.put("pre_uid", pre_uid);
                data.put("word_num", word_num);
                data.put("chat_state", chat_state);
                data.put("chat_locate", chat_locate);
                data.put("photo_num", photo_num);
                data.put("photo_source", photo_source);
                EventManager.getInstance().addEvent(sendId, data);
                LogUtil.d(TAG, sendId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 1、引导横条展示（uid）
     * 2、引导横条点击（uid）
     */
    public static class InterestGuide {

        static String eventId = "0106002";

        public static void report1(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("uid", uid);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("uid", uid);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1. 选兴趣页面展示
     * 2. save 点击
     * 3. 返回点击
     */
    public static class InterestPage {
        static String eventId = "0111002";

        public static void report1(int source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("source", source);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(int source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("source", source);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(int source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("source", source);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 免登陆流程打点：
     * 1、app初次启动白屏阶段
     * 2、启动引导页显示
     * 3、启动引导页底部Get started按钮点击
     * 4、新的选择语言页展示
     * 5、用户在此页面中第一次点击语言选择的卡片
     * 6、点击语言选择页的确定按钮
     * 7、免登陆首页的展示
     * 8、免登陆页的feed流下拉刷新操作
     * 9、免登陆页的上拉继续加载操作
     * 10、免登陆页垂类tab的切换
     * 11、免登陆post detail页的展示
     * 12、免登陆profile页的展示
     * 13、免登陆topic页的展示
     * 14、在免登陆状态下，触发引导登陆snakebar
     * 15、点击免登陆首页的登陆按钮
     * <p>
     * 19、免登陆发现页的展示
     * 20、发现页用户行为
     */
    public static class UnLogin {
        static String eventId = "1011111";

        public static void report1() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 3、启动引导页底部Get started按钮点击
         *
         * @param swiptimes 卡片切换过几次
         */
        public static void report3(int swiptimes) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("swiptimes", swiptimes);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 5、用户在此页面中第一次点击语言选择的卡片
         *
         * @param firstclicklanguage 初次点击的语言是哪种,传具体的语言名称
         */
        public static void report5(String firstclicklanguage) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("firstclicklanguage", firstclicklanguage);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 6、点击语言选择页的确定按钮
         *
         * @param language 1、En 2、Hindi
         */
        public static void report6(String language, String firstClickLanguage) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("language", language);
                data.put("firstclicklanguage", firstClickLanguage);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7(String language, int pagesource, String firstClickLanguage) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                data.put("language", language);
                data.put("pagesource", pagesource);
                data.put("firstclicklanguage", firstClickLanguage);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(String feedname) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("feedname", feedname);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9(String feedname) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                data.put("feedname", feedname);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report10(String feedname) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 10);
                data.put("feedname", feedname);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 11、免登陆post detail页的展示
         *
         * @param feedname 当前操作的feed流的名字
         */
        public static void report11(String feedname) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                data.put("feedname", feedname);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 12、免登陆profile页的展示
         *
         * @param nickname
         * @param uid
         */
        public static void report12(String nickname, String uid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                data.put("nickname", nickname);
                data.put("uid", uid);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 13、免登陆topic页的展示
         *
         * @param topicname 真实的topicname
         */
        public static void report13(String topicname) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 13);
                data.put("topicname", topicname);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 14、在免登陆状态下，触发引导登陆snakebar
         *
         * @param actiontype 1、转发 2、评论 3、赞 4、关注 5、发im消息 6、其他
         * @param frompage   1、home 2、profile 3、postdetail 4、topic
         */
        public static void report14(int actiontype, int frompage) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 14);
                data.put("actiontype", actiontype);
                data.put("frompage", frompage);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report15() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 15);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 16、点击分享按钮
         *
         * @param frompage
         */
        public static void report16(int frompage, String feedname) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 16);
                data.put("frompage", frompage);
                data.put("feedname", feedname);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 17、点击引导登录snakebar上的登录按钮
         *
         * @param actiontype
         * @param frompage
         */
        public static void report17(int actiontype, int frompage) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 17);
                data.put("actiontype", actiontype);
                data.put("frompage", frompage);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 18、渠道素材标签获取参数上报
         *
         * @param campaign
         * @param adset
         * @param adset_id
         * @param adgroup
         * @param ad_id
         */
        public static void report18(String campaign, String adset, String adset_id, String adgroup, String ad_id) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 18);
                data.put("campaign", campaign);
                data.put("adset", adset);
                data.put("adset_id", adset_id);
                data.put("adgroup", adgroup);
                data.put("ad_id", ad_id);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 18、免登陆发现页的展示
         */
        public static void report19() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 19);
                data.put("language", LocalizationManager.getInstance().getCurrentLanguage());
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        /**
         * 20、发现页用户行为
         *
         * @param clickType 点击行为类别
         */
        public static void report20(int clickType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 20);
                data.put("discoveraction", clickType);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 21、dynamic link打点需求
         *
         * @param url
         */
        public static void report21(String url) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 21);
                data.put("URL", url);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 22、点赞
         *
         * @param frompage
         * @param feedname
         */
        public static void report22(int frompage, String feedname) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 22);
                data.put("frompage", frompage);
                data.put("feedname", feedname);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 23、评论
         *
         * @param frompage
         */
        public static void report23(int frompage) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 23);
                data.put("frompage", frompage);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 24、关注
         *
         * @param frompage
         * @param followedid
         */
        public static void report24(int frompage, String followedid) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 24);
                data.put("frompage", frompage);
                data.put("followedid", followedid);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 25、免登录message页面展示
         *
         * @param language
         * @param pageSource
         */
        public static void report25(String language, int pageSource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 25);
                data.put("language", language);
                data.put("page_source", pageSource);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 26、免登录Me点击
         *
         * @param language
         * @param pageSource
         */
        public static void report26(String language, int pageSource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 26);
                data.put("language", language);
                data.put("page_source", pageSource);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 27、点击底部login按钮
         *
         * @param language
         */
        public static void report27(String language) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 27);
                data.put("language", language);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public static void report30() {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 30);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report31(int selectType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 31);
                data.put("selectType", selectType);
                EventManager.getInstance().addEvent(eventId, data);
                LogUtil.d(TAG, eventId + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 1.下拉刷新
     * 2.上划加载
     * 3.打开默认刷新
     * 4.点按钮刷新
     * 5.点击discover页的话题上报（topic_id)
     */
    public static class Discover {
        static String id = "0105006";

        /**
         * 5.点击discover页的话题上报（topic_id)
         *
         * @param topicId
         */
        public static void report5(String topicId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("topicId", topicId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        /**
         * 1.下拉刷新
         *
         * @param type       垂类的label
         * @param list_total
         * @param read_total
         */
        public static void report1(String type, int list_total, int read_total) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 2.上划加载
         *
         * @param type       垂类的label
         * @param list_total
         * @param read_total
         */
        public static void report2(String type, int list_total, int read_total) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 3.打开默认刷新
         *
         * @param type       垂类的label
         * @param list_total
         * @param read_total
         */
        public static void report3(String type, int list_total, int read_total) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 4.点按钮刷新
         *
         * @param type       垂类的label，未登陆页带有vidmate前缀，例如：vidmate——Sports
         * @param list_total
         * @param read_total
         */
        public static void report4(String type, int list_total, int read_total) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("type", type);
                data.put("list_total", list_total);
                data.put("read_total", read_total);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 1	login页面展示（isLogoff）
     * 2	登录方式按钮的点击（login_source）
     * 3	第三方登录回调（login_source；return_result）
     * 4	用户手动左右滑动次数?
     * 5	手机号输入页面展示（isLogoff）
     * 6	下一步按钮的点击（phone_number）
     * 7	第三方登录按钮的点击（login_source）
     * 8	第三方登录回调（login_source；return_result）
     * 9	验证码页面的展示（isNewUser，isLogoff ,page_status）
     * 10	第三方登录按钮的点击（login_source, page_status）
     * 11	第三方登录回调（login_source；return_result, page_status）
     * 12	resend按钮点击(isNewUser, phone_number, SMS_send，page_status)
     * 13	change按钮点击（isNewUser, phone_number, SMS_send, page_status)
     * 14	输入完验证码提交服务端验证(phone_number, isNewUser, verify_type,SMS_send, page_status)
     * 15	填写个人资料页面展示(phone_number;verify_type;SMS_send,isLogoff)
     * 16	skip(phone_number;verify_type;SMS_send)
     * 17	资料填写完成点击下一步(nickname,nickname_check,phone_number;verify_type;SMS_send)
     * 18	登录成功（全部参数，包括isNewUser）
     * <p>
     * login_source	登陆来源	1 手机号登陆 2 Facebook登陆 3 Google账号登录 4 truecaller登录
     * return_result	回调参数	0 失败 1 成功 2 取消
     * isNewUser	是否是新用户	0=新用户 1=老用户
     * language	语言	1、En 2、Hindi
     * phone_number	手机号码	用户手机号码，排除+91
     * SMS_send	短信验证码发送次数	在上报时机时调用了多少次发短信接口就写多少
     * SMS_check	验证码验证的次数	提交服务器验证码是否正确的次数
     * nickname	昵称	在上报的时机输入的用户名是什么就报什么
     * nickname_check	昵称检查器不通过次数	在上报时机时调用了多少次昵称检查并返回不通过的结果就写多少
     * verify_type	验证码种类	0=sms 1=voice
     * isLogoff	是否注销过	0=没注销过=新安装 1=注销过=注销后重新登陆
     * page_status	验证码页面	0=短信验证码页面 1=语音验证码页面
     *
     * @deprecated see {@link RegisterAndLogin}
     */
    @Deprecated
    public static class NewLogin {
        static String id = "0101006";

        /**
         * 1	login页面展示（isLogoff）
         *
         * @param isLogoff 0=没注销过=新安装,1=注销过=注销后重新登陆
         */
        public static void report1(int isLogoff, int language) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("isLogoff", isLogoff);
                data.put("language", language);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(int login_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("login_source", login_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(int login_source, int return_result) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("login_source", login_source);
                data.put("return_result", return_result);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(int count) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("count", count);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5(int isLogoff) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("isLogoff", isLogoff);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6(String phone_number) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("phone_number", phone_number);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7(int login_source) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                data.put("login_source", login_source);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(int login_source, int return_result) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("login_source", login_source);
                data.put("return_result", return_result);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9(int isNewUser, int isLogoff, int page_status) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                data.put("isNewUser", isNewUser);
                data.put("isLogoff", isLogoff);
                data.put("page_status", page_status);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report10(int login_source, int page_status) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 10);
                data.put("login_source", login_source);
                data.put("page_status", page_status);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report11(int login_source, int return_result, int page_status) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                data.put("login_source", login_source);
                data.put("return_result", return_result);
                data.put("page_status", page_status);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report12(int isNewUser, String phone_number, int SMS_send, int page_status) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                data.put("isNewUser", isNewUser);
                data.put("phone_number", phone_number);
                data.put("SMS_send", SMS_send);
                data.put("page_status", page_status);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report13(int isNewUser, String phone_number, int SMS_send, int page_status) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 13);
                data.put("isNewUser", isNewUser);
                data.put("phone_number", phone_number);
                data.put("SMS_send", SMS_send);
                data.put("page_status", page_status);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report14(int isNewUser, String phone_number, int SMS_send, int page_status, int verify_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 14);
                data.put("isNewUser", isNewUser);
                data.put("phone_number", phone_number);
                data.put("SMS_send", SMS_send);
                data.put("page_status", page_status);
                data.put("verify_type", verify_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report15(String phone_number, int SMS_send, int verify_type, int isLogoff) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 15);
                data.put("phone_number", phone_number);
                data.put("SMS_send", SMS_send);
                data.put("verify_type", verify_type);
                data.put("isLogoff", isLogoff);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report16(String phone_number, int SMS_send, int verify_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 16);
                data.put("phone_number", phone_number);
                data.put("SMS_send", SMS_send);
                data.put("verify_type", verify_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report17(String nickname, int nickname_check, String phone_number, int verify_type, int SMS_send) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 17);
                data.put("nickname", nickname);
                data.put("nickname_check", nickname_check);
                data.put("phone_number", phone_number);
                data.put("SMS_send", SMS_send);
                data.put("verify_type", verify_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report18(int login_source, int return_result, int isNewUser, String phone_number, int SMS_send, int SMS_check,
                                    String nickname, int nickname_check, int verify_type, int isLogoff, int page_status, int language) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 18);
                data.put("login_source", login_source);
                data.put("return_result", return_result);
                data.put("isNewUser", isNewUser);
                data.put("nickname", nickname);
                data.put("nickname_check", nickname_check);
                data.put("phone_number", phone_number);
                data.put("SMS_send", SMS_send);
                data.put("SMS_check", SMS_check);
                data.put("verify_type", verify_type);
                data.put("isLogoff", isLogoff);
                data.put("page_status", page_status);
                data.put("language", language);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 完成登陆注册流程进入到MainActivity
         *
         * @param login_source
         * @param return_result
         * @param isNewUser
         * @param phone_number
         * @param SMS_send
         * @param SMS_check
         * @param nickname
         * @param nickname_check
         * @param verify_type
         * @param isLogoff
         * @param page_status
         * @param language
         */
        public static void report19(int login_source, int return_result, int isNewUser, String phone_number, int SMS_send, int SMS_check,
                                    String nickname, int nickname_check, int verify_type, int isLogoff, int page_status, int language) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 19);
                data.put("login_source", login_source);
                data.put("return_result", return_result);
                data.put("isNewUser", isNewUser);
                data.put("nickname", nickname);
                data.put("nickname_check", nickname_check);
                data.put("phone_number", phone_number);
                data.put("SMS_send", SMS_send);
                data.put("SMS_check", SMS_check);
                data.put("verify_type", verify_type);
                data.put("isLogoff", isLogoff);
                data.put("page_status", page_status);
                data.put("language", language);
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
     * from	页面显示来源	1、引导动画 2、发布器中的post status按钮
     * buttontype	按钮类型	1、shuffle text 2、shuffle image 3、点击页面中央的文字区域，编辑文案
     */
    public static class PostStatus {
        static String id = "0101007";

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

        public static void report2(int from) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("from", from);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(int buttontype) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("buttontype", buttontype);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(int textchanged, String text, String imageid, String categoryID, String categoryName) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("textchanged", textchanged);
                data.put("text", text);
                data.put("imageid", imageid);
                data.put("categoryID", categoryID);
                data.put("categoryName", categoryName);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5(int from) {
            JSONObject data = new JSONObject();
            try {
                data.put("from", from);
                data.put("action", 5);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6(int from) {
            JSONObject data = new JSONObject();
            try {
                data.put("from", from);
                data.put("action", 6);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7(int from) {
            JSONObject data = new JSONObject();
            try {
                data.put("from", from);
                data.put("action", 7);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1	select interests模块展示（from_page）
     * 2	点击关闭按钮（interests_name）
     * 3	点击按兴趣确定按钮（interests_name）
     * 4	全屏推荐关注页面展示（interests_name）
     * 5	完成选择，点击follow按钮（interests_name, follow_list, follow_number，portrait_click）
     * 6	recommend for you模块展示
     * 7	点击recommend for you的按钮（button_type）
     * 8	选择性别(选择的性别)
     * <p>
     * interests_name	选择的兴趣	选择兴趣的id，多个分号隔开
     * follow_list	选择的类别	类别id分别打上来，多个分号隔开
     * follow_number	选择的数量
     * portrait_click	头像点击数量
     * button_type	按钮类型	1 follow 2 unfollow 3 portrait 4 more 5 cancel
     * from_page		1 免登陆主页面 2 登陆home页
     */
    public static class InterestAndRecommondCard {

        static String id = "0101008";

        public static void report1(int from_page) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("from_page", from_page);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(String interests_name) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("interests_name", interests_name);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(String interests_name) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("interests_name", interests_name);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(String interests_name) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("interests_name", interests_name);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5(String interests_name, String follow_list, int follow_number, int portrait_click) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("interests_name", interests_name);
                data.put("follow_list", follow_list);
                data.put("follow_number", follow_number);
                data.put("portrait_click", portrait_click);
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

        public static void report7(int button_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                data.put("button_type", button_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(int sex) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("sex", sex);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1	搜索页面展示(from_page)
     * 2	点击topic话题(topic_name，from_page)
     * 3	搜索结果页展示（search_key,from_page）
     * <p>
     * topic_name	话题的名字
     * from_page	页面来源	1 已登陆 2 未登陆
     * search_key	搜索关键字
     */
    public static class Search {
        static String id = "0101009";

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

        public static void report2(int from_page, String topic_name) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("from_page", from_page);
                data.put("topic_name", topic_name);
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

        public static void report4(String search_key) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("search_key", search_key);
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

        static String id = "0101012";

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

        static String id = "0101011";

        public static void report1(int from_page, int user_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("from_page", from_page);
                data.put("user_type", user_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(int icon_type, int user_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("icon_type", icon_type);
                data.put("user_type", user_type);
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

        public static void report8(int more_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("more_type", more_type);
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

        public static void report11(int user_type, int icon_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                data.put("user_type", user_type);
                data.put("icon_type", icon_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report12(int user_type, int icon_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                data.put("user_type", user_type);
                data.put("icon_type", icon_type);
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

        public static void report14(int userType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 14);
                data.put("user_type", userType);
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

        static String id = "0101013";

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

        public static void report7(int icon_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                data.put("icon_type", icon_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(int icon_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                data.put("icon_type", icon_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9(int icon_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                data.put("icon_type", icon_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report10(int icon_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 10);
                data.put("icon_type", icon_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report11(int icon_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                data.put("icon_type", icon_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report12(int icon_type, int result) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                data.put("icon_type", icon_type);
                data.put("result", result);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 1	评分卡片展示（action_type）
     * 2	第一次点击评分(rate, action_type)
     * 3	第二次点击评分(rate, action_type)
     * 3	feedback卡片展示（rate, action_type）
     * 4	点击feedback按钮（rate, action_type）
     * 5	跳转gp页面（return_result, action_type）
     * 6	评分成功卡片展示（action_type）
     * 评分	1.评一分 2.评两分 3.评三分 4评四分 5.评五分
     * 跳转gp的返回值，是否跳转成功了，是否存在	0 失败 1 成功 2 无gp
     * 通过哪种操作触发了评分引导的展示	1.刷新 2.分享 3.发布post
     */
    public static class GPScore {

        static String id = "0101010";

        public static void report1(int action_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("action_type", action_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(int rate, int action_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("rate", rate);
                data.put("action_type", action_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(int rate, int action_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("rate", rate);
                data.put("action_type", action_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report4(int rate, int action_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 4);
                data.put("rate", rate);
                data.put("action_type", action_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5(int return_result, int action_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                data.put("return_result", return_result);
                data.put("action_type", action_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6(int rate, int action_type) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                data.put("rate", rate);
                data.put("action_type", action_type);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <<<<<<< HEAD
     * 5001014
     * <p>
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
    public static class RegisterAndLogin {

        static String id = "5001014";

        public enum PageSource {
            LOGIN_BUTTON(1), LOGOUT(2), FORCE_POP_LOGIN(3), ADD_FRIEND(4), FOLLOW(5),
            FORWARD(6), LIKE(7), COMMENT(8), PUBLISH(9), CHAT(10), PROFILE_MESSAGE(11), OTHER(12);
            private int value;

            PageSource(int value) {
                this.value = value;
            }
        }

        public enum LoginVerifyType {
            LINK_ACCOUNT(1), LOGIN_ACCOUNT(2), LINK_ACCOUNT_ME(3);
            private int value;

            LoginVerifyType(int value) {
                this.value = value;
            }
        }

        public enum AccountStatus {
            NOT_LOGIN(1), LOGIN_UNVERIFY(2), HAS_LOGIN(3);
            private int value;

            AccountStatus(int value) {
                this.value = value;
            }
        }

        public enum PageType {
            FULL_SCREEN(1), DIALOG(2);
            private int value;

            PageType(int value) {
                this.value = value;
            }
        }

        public enum LoginSource {
            PHONE_NUMBER(1), FACEBOOK(2), GOOGLE(3), TRUE_CALLER(4);
            private int value;

            LoginSource(int value) {
                this.value = value;
            }
        }

        public enum ReturnResult {
            INVALID(-1), FAIL(0), SUCCESS(1), CANCEL(2);
            private int value;

            ReturnResult(int value) {
                this.value = value;
            }
        }

        public enum NewUser {
            INVALID(-1), NEW_USER(0), OLD_USER(1);
            private int value;

            NewUser(int value) {
                this.value = value;
            }
        }

        public enum VerifyType {
            SMS(0), VOICE(1);
            private int value;

            VerifyType(int value) {
                this.value = value;
            }
        }

        public enum TcInstalled {
            YES(1), NO(2);
            private int value;

            TcInstalled(int value) {
                this.value = value;
            }
        }

        public enum InputWay {
            INPUT(1), CLICK_LIST(2);
            private int value;

            InputWay(int value) {
                this.value = value;
            }
        }

        public enum RequestWay {
            AUTO_REQUEST(1), CLICK_RESEND(2);
            private int value;

            RequestWay(int value) {
                this.value = value;
            }
        }

        public enum FromPage {
            LOGIN_HOME(1), MOBILE_INPUT(2), VERIFY_CODE(3);
            private int value;

            FromPage(int value) {
                this.value = value;
            }
        }

        public enum PageStatus {
            SMS(0), VOICE(1);
            private int value;

            PageStatus(int value) {
                this.value = value;
            }
        }

        public enum VerifySource {
            DIALOG_LINK(1), MAIN_ME_LINK(2), INVALID(0);
            private int value;

            VerifySource(int value) {
                this.value = value;
            }
        }

        public static void report1(PageSource pageSource, String language, TcInstalled ifTcInstalled, LoginVerifyType loginVerifyType, AccountStatus accountStatus, PageType pageType) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                data.put("language", language);
                if (ifTcInstalled != null) {
                    data.put("if_tc_installed", ifTcInstalled);
                }
                if (loginVerifyType != null) {
                    data.put("login_verify_type", loginVerifyType.value);
                }
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
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

        public static void report2(LoginSource loginSource, LoginVerifyType loginVerifyType, PageType pageType, VerifySource verifySource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                if (loginSource != null) {
                    data.put("login_source", loginSource.value);
                }
                if (loginVerifyType != null) {
                    data.put("login_verify_type", loginVerifyType.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (verifySource != null) {
                    data.put("verify_source", verifySource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(LoginSource loginSource, NewUser newUser, ReturnResult returnResult) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                if (loginSource != null) {
                    data.put("login_source", loginSource.value);
                }
                if (newUser != null) {
                    data.put("isNewUser", newUser.value);
                }
                if (returnResult != null) {
                    data.put("return_result", returnResult.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report5(PageSource pageSource, TcInstalled tcInstalled, InputWay inputWay, PageType pageType, LoginVerifyType loginVerifyType, AccountStatus accountStatus) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 5);
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                if (tcInstalled != null) {
                    data.put("if_tc_installed", tcInstalled.value);
                }
                if (inputWay != null) {
                    data.put("input_way", inputWay.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (loginVerifyType != null) {
                    data.put("login_verify_type", loginVerifyType.value);
                }
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report6(String phoneNumber, PageSource pageSource, NewUser newUser) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 6);
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                data.put("phoneNumber", phoneNumber);
                if (newUser != null) {
                    data.put("isNewUser", newUser.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report7(LoginSource loginSource, FromPage fromPage, LoginVerifyType loginVerifyType, AccountStatus accountStatus) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 7);
                if (loginSource != null) {
                    data.put("login_source", loginSource.value);
                }
                if (fromPage != null) {
                    data.put("from_page", fromPage.value);
                }
                if (loginVerifyType != null) {
                    data.put("login_verify_type", loginVerifyType.value);
                }
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report8(LoginSource loginSource, ReturnResult returnResult) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 8);
                if (loginSource != null) {
                    data.put("login_source", loginSource.value);
                }
                if (returnResult != null) {
                    data.put("return_result", returnResult.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report9(NewUser newUser, PageSource pageSource, RequestWay requestWay, VerifyType verifyType, TcInstalled tcInstalled) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 9);
                if (newUser != null) {
                    data.put("isNewUser", newUser.value);
                }
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                if (requestWay != null) {
                    data.put("request_way", requestWay.value);
                }
                if (verifyType != null) {
                    data.put("verify_type", verifyType.value);
                }
                if (tcInstalled != null) {
                    data.put("if_tc_installed", tcInstalled.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report10(LoginSource loginSource, FromPage fromPage) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 10);
                if (loginSource != null) {
                    data.put("login_source", loginSource.value);
                }
                if (fromPage != null) {
                    data.put("from_page", fromPage.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report11(LoginSource loginSource, ReturnResult returnResult, PageStatus pageStatus) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 11);
                if (loginSource != null) {
                    data.put("login_source", loginSource.value);
                }
                if (returnResult != null) {
                    data.put("return_result", returnResult.value);
                }
                if (pageStatus != null) {
                    data.put("page_status", pageStatus.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report12(NewUser newUser, String phoneNumber, int smsSend, PageStatus pageStatus, int stayTime, TcInstalled tcInstalled) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 12);
                if (newUser != null) {
                    data.put("isNewUser", newUser.value);
                }
                data.put("phone_number", phoneNumber);
                data.put("SMS_send", smsSend);
                if (pageStatus != null) {
                    data.put("page_status", pageStatus.value);
                }
                data.put("stay_time", stayTime);
                if (tcInstalled != null) {
                    data.put("if_tc_installed", tcInstalled.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report13(NewUser newUser, String phoneNumber, int smsSend, PageStatus pageStatus, TcInstalled tcInstalled, InputWay inputWay) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 13);
                if (newUser != null) {
                    data.put("isNewUser", newUser.value);
                }
                data.put("phone_number", phoneNumber);
                data.put("SMS_send", smsSend);
                if (pageStatus != null) {
                    data.put("page_status", pageStatus.value);
                }
                if (tcInstalled != null) {
                    data.put("if_tc_installed", tcInstalled.value);
                }
                if (inputWay != null) {
                    data.put("input_way", inputWay.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report14(NewUser newUser, String phoneNumber, int smsSend, PageStatus pageStatus, TcInstalled tcInstalled, InputWay inputWay) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 14);
                if (newUser != null) {
                    data.put("isNewUser", newUser.value);
                }
                data.put("phone_number", phoneNumber);
                data.put("SMS_send", smsSend);
                if (pageStatus != null) {
                    data.put("page_status", pageStatus.value);
                }
                if (tcInstalled != null) {
                    data.put("if_tc_installed", tcInstalled.value);
                }
                if (inputWay != null) {
                    data.put("input_way", inputWay.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report15(String phoneNumber, int smsSend, VerifyType verifyType, PageSource pageSource, int stayTime) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 15);
                if (verifyType != null) {
                    data.put("verify_type", verifyType.value);
                }
                data.put("phone_number", phoneNumber);
                data.put("SMS_send", smsSend);
                data.put("stay_time", stayTime);
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report16(String phoneNumber, int smsSend, VerifyType verifyType, AccountStatus accountStatus) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 16);
                if (verifyType != null) {
                    data.put("verify_type", verifyType.value);
                }
                data.put("phone_number", phoneNumber);
                data.put("SMS_send", smsSend);
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report17(String nickName, int nickNameCheck, String phoneNumber, VerifyType verifyType, int smsSend, int stayTime) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 17);
                if (verifyType != null) {
                    data.put("verify_type", verifyType.value);
                }
                data.put("phone_number", phoneNumber);
                data.put("SMS_send", smsSend);
                data.put("nick_name", nickName);
                data.put("nickname_check", nickNameCheck);
                data.put("stay_time", stayTime);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report18(String nickName, int nickNameCheck, String phoneNumber, VerifyType verifyType, int smsSend, int stayTime,
                                    LoginSource loginSource, ReturnResult returnResult, NewUser newUser, String language, int smsCheck,
                                    PageSource pageSource, PageStatus pageStatus, TcInstalled tcInstalled, InputWay inputWay, RequestWay requestWay,
                                    FromPage fromPage, LoginVerifyType loginVerifyType, AccountStatus accountStatus, PageType pageType, VerifySource verifySource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 18);
                if (verifyType != null) {
                    data.put("verify_type", verifyType.value);
                }
                data.put("phone_number", phoneNumber);
                data.put("SMS_send", smsSend);
                data.put("nick_name", nickName);
                data.put("nickname_check", nickNameCheck);
                data.put("stay_time", stayTime);
                if (loginSource != null) {
                    data.put("login_source", loginSource.value);
                }
                if (returnResult != null) {
                    data.put("return_result", returnResult.value);
                }
                if (newUser != null) {
                    data.put("isNewUser", newUser.value);
                }
                data.put("language", language);
                data.put("SMS_check", smsCheck);
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                if (pageStatus != null) {
                    data.put("page_status", pageStatus.value);
                }
                if (tcInstalled != null) {
                    data.put("if_tc_installed", tcInstalled.value);
                }
                if (inputWay != null) {
                    data.put("input_way", inputWay.value);
                }
                if (requestWay != null) {
                    data.put("request_way", requestWay.value);
                }
                if (fromPage != null) {
                    data.put("from_page", fromPage.value);
                }
                if (loginVerifyType != null) {
                    data.put("login_verify_type", loginVerifyType.value);
                }
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (verifySource != null) {
                    data.put("verify_source", verifySource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report19(String nickName, int nickNameCheck, String phoneNumber, VerifyType verifyType, int smsSend, int stayTime,
                                    LoginSource loginSource, ReturnResult returnResult, NewUser newUser, String language, int smsCheck,
                                    PageSource pageSource, PageStatus pageStatus, TcInstalled tcInstalled, InputWay inputWay, RequestWay requestWay,
                                    FromPage fromPage, LoginVerifyType loginVerifyType, AccountStatus accountStatus, PageType pageType, VerifySource verifySource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 19);
                if (verifyType != null) {
                    data.put("verify_type", verifyType.value);
                }
                data.put("phone_number", phoneNumber);
                data.put("SMS_send", smsSend);
                data.put("nick_name", nickName);
                data.put("nickname_check", nickNameCheck);
                data.put("stay_time", stayTime);
                if (loginSource != null) {
                    data.put("login_source", loginSource.value);
                }
                if (returnResult != null) {
                    data.put("return_result", returnResult.value);
                }
                if (newUser != null) {
                    data.put("isNewUser", newUser.value);
                }
                data.put("language", language);
                data.put("SMS_check", smsCheck);
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                if (pageStatus != null) {
                    data.put("page_status", pageStatus.value);
                }
                if (tcInstalled != null) {
                    data.put("if_tc_installed", tcInstalled.value);
                }
                if (inputWay != null) {
                    data.put("input_way", inputWay.value);
                }
                if (requestWay != null) {
                    data.put("request_way", requestWay.value);
                }
                if (fromPage != null) {
                    data.put("from_page", fromPage.value);
                }
                if (loginVerifyType != null) {
                    data.put("login_verify_type", loginVerifyType.value);
                }
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                if (pageType != null) {
                    data.put("page_type", pageType.value);
                }
                if (verifySource != null) {
                    data.put("verify_source", verifySource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report20(AccountStatus accountStatus, PageSource pageSource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 20);
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report21(AccountStatus accountStatus, PageSource pageSource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 21);
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report22(AccountStatus accountStatus, PageSource pageSource, String nickName, NewUser newUser) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 22);
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                data.put("nickname", nickName);
                if (newUser != null) {
                    data.put("isNewUser", newUser.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report23(PageSource pageSource, AccountStatus accountStatus) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 23);
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report24(PageSource pageSource, AccountStatus accountStatus, VerifySource verifySource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 24);
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                if (verifySource != null) {
                    data.put("verify_source", verifySource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report25(AccountStatus accountStatus, VerifySource verifySource, PageSource pageSource, LoginSource loginSource) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 25);
                if (accountStatus != null) {
                    data.put("account_status", accountStatus.value);
                }
                if (pageSource != null) {
                    data.put("page_source", pageSource.value);
                }
                if (verifySource != null) {
                    data.put("verify_source", verifySource.value);
                }
                if (loginSource != null) {
                    data.put("login_source", loginSource.value);
                }
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * action	1、活动卡片展示（全部参数）
     * 2、活动卡片点击（全部参数）
     * 3、活动卡片跳转后页面展示（全部参数）
     * card_id	活动卡片ID
     * post_id	活动卡片所属post的ID
     */
    public static class ActiveCard {
        static String id = "5001025";

        public static void report1(int cardId, String postId, String postLa, String[] postTags, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 1);
                data.put("card_id", cardId);
                data.put("post_id", postId);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report2(int cardId, String postId, String postLa, String[] postTags, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 2);
                data.put("card_id", cardId);
                data.put("post_id", postId);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static void report3(int cardId, String postId, String postLa, String[] postTags, String sequenceId) {
            JSONObject data = new JSONObject();
            try {
                data.put("action", 3);
                data.put("card_id", cardId);
                data.put("post_id", postId);
                data.put("post_la", postLa);
                data.put("post_tags", postTags);
                data.put("sequenceId", sequenceId);
                EventManager.getInstance().addEvent(id, data);
                LogUtil.d(TAG, id + data.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
