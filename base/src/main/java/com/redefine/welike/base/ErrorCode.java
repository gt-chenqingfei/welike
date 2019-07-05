package com.redefine.welike.base;

import com.google.android.gms.common.api.ApiException;
import com.redefine.foundation.http.HttpRespFormatException;
import com.redefine.welike.base.resource.ResourceTool;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by liubin on 2018/1/11.
 */

public class ErrorCode {
    public static final int ERROR_SUCCESS = 0;                           // 成功
    public static final int ERROR_UNKNOWN = -1;                          // 未知错误
    // 网络请求错误码
    public static final int ERROR_NETWORK_SUCCESS = 1000;
    public static final int ERROR_NETWORK_INVALID = -1000;               // 网络无效错误
    public static final int ERROR_NETWORK_RESP_INVALID = -1001;          // 网络返回内容无效
    public static final int ERROR_NETWORK_UPLOAD_FAILED = -1002;         // 上传失败
    public static final int ERROR_NETWORK_TOKEN_INVALID = -1003;         // 访问token失效
    public static final int ERROR_NETWORK_UNKNOWN = 1001;                // 网络未知错误
    public static final int ERROR_NETWORK_PARAMS = 1002;                 // 传递给服务端的参数错误
    public static final int ERROR_NETWORK_INTERNAL = 1003;               // 服务端内部错误
    public static final int ERROR_NETWORK_ILLEGAL_OPERATION = 1004;      // 非法操作 比如更新其他用户的信息
    public static final int ERROR_NETWORK_SMSCODE_REPEATEDLY = 2003;     // 短信验证码发送频繁
    public static final int ERROR_NETWORK_AUTH_NOT_MATCH = 2005;         // Auth跟用户不匹配
    public static final int ERROR_NETWORK_AUTH_TOKEN_INVALID = 2006;     // Auth token无效
    public static final int ERROR_NETWORK_INVALID_API = 2009;            // 无效请求
    public static final int ERROR_NETWORK_AUTH_PASSWORD = 2010;          // 登录的用户名或者密码错误
    public static final int ERROR_NETWORK_APP_FORCE_UPDATE = 2011;       // 接口作废
    public static final int ERROR_NETWORK_APP_NOT_EXIST = 2012;          // 接口版本不存在
    public static final int ERROR_NETWORK_COMMON_PARAMS = 2013;          // 公参不对
    public static final int ERROR_NETWORK_SMS_CODE = 2014;               // 验证码错误
    public static final int ERROR_NETWORK_USER_NOT_FOUND = 3001;         // 未找到用户
    public static final int ERROR_NETWORK_USER_NICKNAME_INVALID = 3002;  // 昵称格式不对
    public static final int ERROR_NETWORK_USER_NICKNAME_USED = 3003;     // 昵称已被使用
    public static final int ERROR_NETWORK_USER_NICKNAME_REFUSED = 3004;  // 昵称不允许被使用
    public static final int ERROR_NETWORK_USER_SEX_M_OUT = 3005;         // 性别修改次数过多
    public static final int ERROR_NETWORK_USER_NICKNAME_M_OUT = 3006;    // 昵称修改频繁
    public static final int ERROR_NETWORK_USER_INTRODUCTION = 3007;      // 介绍格式不对

    public static final int ERROR_NETWORK_USER_CONFLICT = 4002;          // 用户关注或者取关自己
    public static final int ERROR_NETWORK_USER_BLOCK_ME = 4003;          // 我被Block
    public static final int ERROR_NETWORK_USER_BLOCK_USER = 4004;        // 我Block user
    public static final int ERROR_NETWORK_OBJECT_NOT_FOUND = 4001;       // 未找到对象err
    public static final int ERROR_POST_TOP_LIMIT = 4008;                 // 置顶博文超过限制

    public static final int ERROR_NETWORK_USER_DEACTIVATE = 3011;       // 用户注销
    public static final int ERROR_NETWORK_USER_BLOCKED = 3009;          // 用户被系统block
    public static final int ERROR_FOLLOW_LIMIT = 3015;                  // 用户follow数受限
    public static final int ERROR_NETWORK_USER_WAS_USED = 3016;          // 用户被系统block
    public static final int ERROR_NETWORK_USER_WAS_FREQUENTLY = 3017;    // 用户被系统限制 防刷
    public static final int ERROR_NETWORK_LINK_NOT_FOUND = 3013;        // 链接无效
    // 上传错误码
    public static final int ERROR_UPLOADING_OFFSET_OUT = -16001;         // 上传offset超限
    // IM本地错误码
    public static final int ERROR_IM_SERVICE_MISS = -18001;              // 本地IM服务无法获取
    public static final int ERROR_IM_AUTH_NOT_READY = -18002;            // 离线任务未完成，无法鉴权
    public static final int ERROR_IM_DUPLICATE_SEND_MSG = -18003;        // 重复发送消息
    public static final int ERROR_IM_SEND_MSG_RESOURCE_INVALID = -18004; // 发送消息的资源无效
    public static final int ERROR_IM_MSG_NOT_SUPPORT = -18005;           // 不支持的消息类型
    public static final int ERROR_IM_SEND_MSG_RESOURCE_FAILED = -18006;  // 上传资源失败
    public static final int ERROR_IM_SOCKET_CLOSED = -18007;             // 本地IM长链接已关闭
    public static final int ERROR_IM_SEND_MSG_REFUSE = -18008;           // IM发送消息被拒绝
    public static final int ERROR_IM_SOCKET_TIMEOUT = -18009;            // 本地IM长链接超时
    // 本地错误码
    public static final int ERROR_LOGIN_MOBILE_EMPTY = -19001;           // 登录输入的手机号为空
    public static final int ERROR_LOGIN_SMS_EMPTY = -19003;              // 登录输入的短信验证码为空
    public static final int ERROR_LOGIN_FAILED = -19004;                 // 其它登录失败错误
    public static final int ERROR_USERINFO_NICKNAME_EMPTY = -19005;      // 用户昵称为空
    public static final int ERROR_USERINFO_NICKNAME_TOO_SHORT = -19006;  // 昵称太短
    public static final int ERROR_USERINFO_NICKNAME_TOO_LONG = -19007;   // 昵称太长
    public static final int ERROR_USERINFO_HEAD_EMPTY = -19008;          // 用户头像为空
    public static final int ERROR_USERINFO_FAILED = -19009;              // 用户编辑失败
    public static final int ERROR_POST_NOT_FOUND = -19010;               // 用户编辑失败

    public static final int ERROR_GOOGLE_LOGIN_CANCEL = -12501;          // 谷歌登陆取消

    public static int networkExceptionToErrCode(Exception e) {
        if (e != null) {
            int errCode = ErrorCode.ERROR_NETWORK_UNKNOWN;
            if (e instanceof IOException) {
                errCode = ERROR_NETWORK_INVALID;
            } else if (e.getCause() != null && e.getCause() instanceof UnknownHostException) {
                errCode = ERROR_NETWORK_INVALID;
            } else if (e instanceof HttpRespFormatException) {
                errCode = ERROR_NETWORK_RESP_INVALID;
            } else if (e instanceof ApiException) {
                int statusCode = ((ApiException) e).getStatusCode();
                if (statusCode == 12501 || statusCode == 8 || statusCode == 13) {
                    errCode = ERROR_GOOGLE_LOGIN_CANCEL;
                }
            }
            return errCode;
        }
        return ERROR_NETWORK_UNKNOWN;
    }

    public static String showErrCodeText(int errCode) {
        switch (errCode) {
            case ERROR_NETWORK_INVALID:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_network_poor");
            case ERROR_NETWORK_RESP_INVALID:
            case ERROR_NETWORK_UNKNOWN:
            case ERROR_NETWORK_INTERNAL:
            case ERROR_NETWORK_INVALID_API:
            case ERROR_NETWORK_COMMON_PARAMS:
                return ResourceTool.getString("error_not_support_service");
            case ERROR_NETWORK_USER_WAS_USED:
                return ResourceTool.getString("error_user_was_used");
            case ERROR_NETWORK_UPLOAD_FAILED:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_upLoad_failed");
            case ERROR_NETWORK_TOKEN_INVALID:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_authorization_invalid");
            case ERROR_NETWORK_SMSCODE_REPEATEDLY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_regist_verification_application_frequently");
            case ERROR_NETWORK_AUTH_PASSWORD:
            case ERROR_NETWORK_SMS_CODE:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_regist_verification_incorrect");
            case ERROR_NETWORK_AUTH_NOT_MATCH:
            case ERROR_NETWORK_AUTH_TOKEN_INVALID:
            case ERROR_LOGIN_FAILED:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_regist_Login_failed");
            case ERROR_NETWORK_APP_FORCE_UPDATE:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_api_force_update");
            case ERROR_NETWORK_APP_NOT_EXIST:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_api_version_not_exist");
            case ERROR_NETWORK_USER_CONFLICT:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_can_not_follow_yourself");
            case ERROR_NETWORK_USER_NICKNAME_INVALID:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_nickname_invalid");
            case ERROR_NETWORK_USER_NICKNAME_REFUSED:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_nickname_cannot_used");
            case ERROR_NETWORK_USER_NICKNAME_USED:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_nickname_already_exists");
            case ERROR_NETWORK_USER_SEX_M_OUT:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_sex_modify_count_out");
            case ERROR_NETWORK_USER_NICKNAME_M_OUT:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_nickname_modify_count_out");
            case ERROR_IM_SEND_MSG_RESOURCE_INVALID:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_message_attachment_invalid");
            case ERROR_USERINFO_NICKNAME_TOO_SHORT:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_nickname_too_short");
            case ERROR_USERINFO_NICKNAME_TOO_LONG:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_nickname_too_long");
            case ERROR_USERINFO_NICKNAME_EMPTY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_nickname_cannot_empty");
            case ERROR_NETWORK_USER_INTRODUCTION:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_introduction_format");
            case ERROR_POST_NOT_FOUND:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_post_deleted");
            case ERROR_NETWORK_USER_BLOCK_ME:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_user_block_me");
            case ERROR_NETWORK_USER_BLOCK_USER:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_user_block_user");
            case ERROR_NETWORK_USER_DEACTIVATE:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "user_deactivate_message_info");
            case ERROR_NETWORK_USER_BLOCKED:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "user_deactivate_message_info");
            case ERROR_GOOGLE_LOGIN_CANCEL:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.REGISTER, "canceled");
            case ERROR_NETWORK_USER_WAS_FREQUENTLY:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "user_login_frequently");
            case ERROR_NETWORK_ILLEGAL_OPERATION:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "user_illegal_operation");
            case ERROR_FOLLOW_LIMIT:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "error_follow_limit");
            case ERROR_NETWORK_LINK_NOT_FOUND:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "user_link_not_found");
            case ERROR_POST_TOP_LIMIT:
                return ResourceTool.getString(ResourceTool.ResourceFileEnum.ERROR_CODE, "feed_top_limit");
            default:
                return null;
        }
    }

}
