package com.redefine.welike.base.request;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.redefine.foundation.framework.Event;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.foundation.http.HttpCallback;
import com.redefine.foundation.http.HttpDeleteReq;
import com.redefine.foundation.http.HttpGetReq;
import com.redefine.foundation.http.HttpPostReq;
import com.redefine.foundation.http.HttpPutReq;
import com.redefine.foundation.http.HttpRespFormatException;
import com.redefine.foundation.utils.ChannelHelper;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.foundation.utils.NetWorkUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.URLCenter;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.base.profile.AccountManager;
import com.redefine.welike.base.profile.bean.Account;
import com.redefine.welike.base.util.LocationExt;
import com.redefine.welike.base.util.MemoryExt;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by liubin on 2018/3/19.
 */

public abstract class BaseRequest implements HttpCallback {
    public static final String RESPONSE_KEY_CODE = "code";
    public static final String RESPONSE_KEY_DATA = "result";
    public static final String RESPONSE_KEY_SEQUENCE_ID = "sequenceId";
    public static final String ERROR_CODE_KEY = "errCode";
    protected final BaseHttpReq baseHttpReq;
    protected RequestCallback requestCallback;
    private Context context;
    private long time;
    private String apiName;

    protected BaseRequest(int method, Context context) {
        if (method == BaseHttpReq.REQUEST_METHOD_POST) {
            baseHttpReq = new HttpPostReq();
        } else if (method == BaseHttpReq.REQUEST_METHOD_PUT) {
            baseHttpReq = new HttpPutReq();
        } else if (method == BaseHttpReq.REQUEST_METHOD_DELETE) {
            baseHttpReq = new HttpDeleteReq();
        } else {
            baseHttpReq = new HttpGetReq();
        }
        this.context = context;
        Account account = AccountManager.getInstance().getAccount();
        if (account != null) {
            baseHttpReq.appendHeader("idtoken", account.getUid());
        }
    }

    protected BaseRequest(int method, Context context, boolean isAccount) {
        if (method == BaseHttpReq.REQUEST_METHOD_POST) {
            baseHttpReq = new HttpPostReq();
        } else if (method == BaseHttpReq.REQUEST_METHOD_PUT) {
            baseHttpReq = new HttpPutReq();
        } else if (method == BaseHttpReq.REQUEST_METHOD_DELETE) {
            baseHttpReq = new HttpDeleteReq();
        } else {
            baseHttpReq = new HttpGetReq();
        }
        this.context = context;

        Account account = AccountManager.getInstance().getAccount();
        if (account != null && isAccount) {
            baseHttpReq.appendHeader("idtoken", account.getUid());
        }
    }

    public void req(RequestCallback requestCallback) throws Exception {
        time = new Date().getTime();
        this.requestCallback = requestCallback;
        baseHttpReq.send(this);
    }

    public JSONObject req() throws Exception {
        time = new Date().getTime();
        JSONObject result = baseHttpReq.send();
        if (result != null) {
            int errCode = result.getInteger(RESPONSE_KEY_CODE);
            if (errCode == ErrorCode.ERROR_NETWORK_SUCCESS) {
                try {
                    return result.getJSONObject(RESPONSE_KEY_DATA);
                } catch (Exception e) {
                    throw new Exception(e);
                }
            } else {
                JSONObject err = new JSONObject();
                err.put(ERROR_CODE_KEY, errCode);
                return err;
            }
        } else {
            throw new HttpRespFormatException("invalid json format");
        }
    }

    public void cancel() {
        baseHttpReq.cancel();
        requestCallback = null;
    }

    public void putUserInfo(String key, Object value) {
        baseHttpReq.userInfo.put(key, value);
    }

    public Object getUserInfo(String key) {
        return baseHttpReq.userInfo.get(key);
    }

    @Override
    public void onFailure(BaseHttpReq request, Exception e) {
        if (requestCallback != null) {
            requestCallback.onError(this, ErrorCode.networkExceptionToErrCode(e));
        }
        requestCallback = null;
    }

    @Override
    public void onResponse(BaseHttpReq request, JSONObject result) throws Exception {
        netCostTrack();
        if (result.containsKey(RESPONSE_KEY_CODE)) {
            int errCode = result.getIntValue(RESPONSE_KEY_CODE);
            if (errCode == ErrorCode.ERROR_NETWORK_SUCCESS) {
                String sequenceId = null;
                try {
                    sequenceId = result.getString(RESPONSE_KEY_SEQUENCE_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject res = result.getJSONObject(RESPONSE_KEY_DATA);
                    if (!TextUtils.isEmpty(sequenceId) && res != null) {
                        res.put(RESPONSE_KEY_SEQUENCE_ID, sequenceId);
                    }
                    if (requestCallback != null) {
                        requestCallback.onSuccess(this, res);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (requestCallback != null) {
                        requestCallback.onSuccess(this, null);
                    }
                }
            } else {
                if (requestCallback != null) {
                    requestCallback.onError(this, errCode);
                }
                if (errCode == ErrorCode.ERROR_NETWORK_AUTH_NOT_MATCH) {
                    EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_LOGOUT_EVENT, null));
                }
            }
        } else {
            if (requestCallback != null) {
                requestCallback.onError(this, ErrorCode.ERROR_NETWORK_RESP_INVALID);
            }
        }
        requestCallback = null;
    }

    protected void setHost(String api, boolean auth) {
        setHost(api, URLCenter.getHost(), auth);
    }

    protected void setHost(String api, String host, boolean auth) {
        apiName = api;
        if (baseHttpReq.params != null) {
            baseHttpReq.params.clear();
        }
        baseHttpReq.host = host + api;
        if (auth) {
            Account account = AccountManager.getInstance().getAccount();
            if (account != null) {
                if (baseHttpReq instanceof HttpPostReq) {
                    HttpPostReq httpPostReq = (HttpPostReq) baseHttpReq;
                    httpPostReq.setUrlExtParam("token", account.getAccessToken());
                    httpPostReq.setUrlExtParam("welikeParams", baseParamsBlock(context));
                } else if (baseHttpReq instanceof HttpPutReq) {
                    HttpPutReq httpPutReq = (HttpPutReq) baseHttpReq;
                    httpPutReq.setUrlExtParam("token", account.getAccessToken());
                    httpPutReq.setUrlExtParam("welikeParams", baseParamsBlock(context));
                } else if (baseHttpReq instanceof HttpDeleteReq) {
                    HttpDeleteReq httpDeleteReq = (HttpDeleteReq) baseHttpReq;
                    httpDeleteReq.setUrlExtParam("token", account.getAccessToken());
                    httpDeleteReq.setUrlExtParam("welikeParams", baseParamsBlock(context));
                } else {
                    baseHttpReq.setParam("token", account.getAccessToken());
                    baseHttpReq.setParam("welikeParams", baseParamsBlock(context));
                }
            } else {
                if (baseHttpReq instanceof HttpPostReq) {
                    HttpPostReq httpPostReq = (HttpPostReq) baseHttpReq;
                    httpPostReq.setUrlExtParam("welikeParams", baseParamsBlock(context));
                } else if (baseHttpReq instanceof HttpPutReq) {
                    HttpPutReq httpPutReq = (HttpPutReq) baseHttpReq;
                    httpPutReq.setUrlExtParam("welikeParams", baseParamsBlock(context));
                } else if (baseHttpReq instanceof HttpDeleteReq) {
                    HttpDeleteReq httpDeleteReq = (HttpDeleteReq) baseHttpReq;
                    httpDeleteReq.setUrlExtParam("welikeParams", baseParamsBlock(context));
                } else {
                    baseHttpReq.setParam("welikeParams", baseParamsBlock(context));
                }
            }
        } else {
            if (baseHttpReq instanceof HttpPostReq) {
                HttpPostReq httpPostReq = (HttpPostReq) baseHttpReq;
                httpPostReq.setUrlExtParam("welikeParams", baseParamsBlock(context));
            } else if (baseHttpReq instanceof HttpPutReq) {
                HttpPutReq httpPutReq = (HttpPutReq) baseHttpReq;
                httpPutReq.setUrlExtParam("welikeParams", baseParamsBlock(context));
            } else if (baseHttpReq instanceof HttpDeleteReq) {
                HttpDeleteReq httpDeleteReq = (HttpDeleteReq) baseHttpReq;
                httpDeleteReq.setUrlExtParam("welikeParams", baseParamsBlock(context));
            } else {
                baseHttpReq.setParam("welikeParams", baseParamsBlock(context));
            }
        }
    }

    protected void setGetMethodUrl(String url) {
        baseHttpReq.host = url;
    }

    protected void setUrlExtParam(String key, String value) {
        if (baseHttpReq instanceof HttpPostReq) {
            ((HttpPostReq) baseHttpReq).setUrlExtParam(key, value);
        } else if (baseHttpReq instanceof HttpPutReq) {
            ((HttpPutReq) baseHttpReq).setUrlExtParam(key, value);
        } else if (baseHttpReq instanceof HttpDeleteReq) {
            ((HttpDeleteReq) baseHttpReq).setUrlExtParam(key, value);
        }
    }

    protected void setParam(String key, Object value) {
        baseHttpReq.setParam(key, value);
    }

    protected void setBodyData(String body) {
        baseHttpReq.bodyData = body;
    }

    public static String baseParamsBlock(Context context) {
        String version = CommonHelper.getAppVersionName(context);
        String language = LanguageSupportManager.getInstance().getCurrentMenuLanguageType();
        String languageContent = LanguageSupportManager.getInstance().getCurrentContentLanguageType();
        String languageDevice = LanguageSupportManager.getSystemLanguage(context);
        int versionCode = CommonHelper.getAppVersion(context);
        String isp = CommonHelper.getIsp(context);

        String os = "android";
        String deviceId = CommonHelper.getDeviceId(context);
//        String androidId = CommonHelper.getAndroidID(context);
        String source = CommonHelper.getDeviceModel();
        String channel = CommonHelper.getChannel(context);
        int appType = CommonHelper.getAppType(context);
        String gaid = CommonHelper.GAID;
        String tag = ChannelHelper.getTagString(context);
        String utdid = com.ta.utdid2.device.UTDevice.getUtdid(context);
        String network = NetWorkUtil.getNetworkTypeName(context);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        String resolution = displayMetrics.heightPixels + "*" + displayMetrics.widthPixels;
        double lat = LocationExt.INSTANCE.getLat();
        double lng = LocationExt.INSTANCE.getLng();


        String commonParam = "ve=" + version;
        commonParam = commonParam + "&versionCode=" + versionCode;
        if (!TextUtils.isEmpty(language)) {
            commonParam = commonParam + "&la=" + language;
        }
        if (!TextUtils.isEmpty(utdid)) {
            commonParam = commonParam + "&utdid=" + utdid;
        }
        if (!TextUtils.isEmpty(languageContent)) {
            commonParam = commonParam + "&lac=" + languageContent;
        }
        if (!TextUtils.isEmpty(languageDevice)) {
            commonParam = commonParam + "&lad=" + languageDevice;
        }

        commonParam = commonParam + "&resolution=" + resolution;
        commonParam = commonParam + "&lat=" + lat;
        commonParam = commonParam + "&lng=" + lng;

        commonParam = commonParam + "&os=" + os;
        if (!TextUtils.isEmpty(deviceId)) {
            commonParam = commonParam + "&de=" + deviceId;
        }
        if (!TextUtils.isEmpty(source)) {
            commonParam = commonParam + "&sr=" + source;
        }
//        if (!TextUtils.isEmpty(androidId)) {
//            commonParam = commonParam + "&androidid=" + androidId;
//        }
        if (!TextUtils.isEmpty(channel)) {
            commonParam = commonParam + "&channel=" + channel;
        }
        if (!TextUtils.isEmpty(gaid)) {
            commonParam = commonParam + "&gaid=" + gaid;
        }
        if (!TextUtils.isEmpty(tag)) {
            commonParam = commonParam + "&tag=" + tag;
        }
        if (!TextUtils.isEmpty(network)) {
            commonParam = commonParam + "&network=" + network;
        }
        if (!TextUtils.isEmpty(isp)) {
            commonParam = commonParam + "&isp=" + isp;
        }
        commonParam = commonParam + "&appType=" + appType;
        String params = null;
        try {
            byte[] orgParamsBytes = commonParam.getBytes("utf-8");

            params = new String(Base64.encode(orgParamsBytes, Base64.NO_WRAP), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    private void netCostTrack() {
        if (time > 0) {
            int level;
            long dur = new Date().getTime() - time;
            if (dur > 0) {
                if (dur <= 200) {
                    level = 1;
                } else if (dur <= 500) {
                    level = 2;
                } else if (dur <= 1000) {
                    level = 3;
                } else if (dur <= 3000) {
                    level = 4;
                } else if (dur <= 5000) {
                    level = 5;
                } else if (dur <= 10000) {
                    level = 6;
                } else if (dur <= 20000) {
                    level = 7;
                } else if (dur <= 30000) {
                    level = 8;
                } else if (dur <= 50000) {
                    level = 9;
                } else {
                    level = 10;
                }
                String label = apiName + "[" + NetWorkUtil.getNetworkTypeName(context) + "]";
//                TrackerUtil.getEventTracker().send(new HitBuilders.EventBuilder().setCategory(TrackerConstant.EVENT_TIMING)
//                        .setAction(TrackerConstant.EVENT_TIME_NETWORK)
//                        .setValue(level)
//                        .setLabel(label).build());
            }
        }
    }

}
