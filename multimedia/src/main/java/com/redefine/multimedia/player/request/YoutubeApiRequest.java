package com.redefine.multimedia.player.request;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.common.util.JsonUtils;
import com.redefine.foundation.http.BaseHttpReq;
import com.redefine.foundation.http.HttpCallback;
import com.redefine.foundation.http.HttpGetReq;
import com.redefine.foundation.http.HttpManager;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.foundation.utils.NetWorkUtil;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class YoutubeApiRequest {

    private static final String TAG = "YoutubeApiRequest";
    private Call call;

    public YoutubeApiRequest() {
    }

    public void getYoutubeUrl(Context context, String youTubeId, YoutubeApiManager.IRequestCallback callback) {
        if (!NetWorkUtil.isNetWorkConnected(context)) {
            callback.onError(ErrorCode.ERROR_NETWORK_INVALID);
            return;
        }
        if (!TextUtils.isEmpty(youTubeId)) {
            requestYoutubeAPI(youTubeId, callback);
        } else {
            callback.onError(ErrorCode.ERROR_NETWORK_PARAMS);
        }
    }

    public String getYoutubeApiUrl(String youtubeId) {
        return "http://www.youtube.com/get_video_info?video_id=" + youtubeId;
    }

    private void requestYoutubeAPI(final String youtubeId, final YoutubeApiManager.IRequestCallback callback) {
        try {
            Request request = new okhttp3.Request.Builder().get().url(getYoutubeApiUrl(youtubeId)).build();
            call = HttpManager.getInstance().getHttpClient().newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(ErrorCode.ERROR_NETWORK_INVALID);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response res) {
                    try {
                        if (res.body() == null) {
                            callback.onError(ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND);
                            return ;
                        }
                        YoutubeVideoInfo videoInfo = videoInfoAnalytics(new String(res.body().bytes(), "UTF-8"), youtubeId);
                        if (videoInfo != null) {
                            callback.onSuccess(videoInfo);
                        } else {
                            callback.onError(ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND);
                        }
                    } catch (Throwable e) {
                        callback.onError(ErrorCode.ERROR_NETWORK_OBJECT_NOT_FOUND);
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(ErrorCode.ERROR_NETWORK_INVALID);
        }
    }


    private YoutubeVideoInfo videoInfoAnalytics(String videoContent, String youtubeId) throws Exception {
        LogUtil.d(TAG, "videoInfoAnalytics videoContent = " + videoContent);
        if (videoContent.length() <= 2048) {
            return null;
        }
        if (!videoContent.contains("url_encoded_fmt_stream_map")) {
            return null;
        }

        YoutubeVideoInfo youtubeVideoInfo = new YoutubeVideoInfo();

        String url_encoded_fmt_stream_map = "";
        String adaptive_fmts = "";
        String[] dataArray = videoContent.split("&");
        for (int i = 0; i < dataArray.length; ++i) {
            String obj = dataArray[i];
            String[] tmpArray = obj.split("=");
            if (2 != tmpArray.length) continue;
            tmpArray[1] = URLDecoder.decode(tmpArray[1], "utf-8");
            if ("title".equals(tmpArray[0])) {
                youtubeVideoInfo.title = tmpArray[1];
            } else if ("view_count".equals(tmpArray[0])) {
                try {
                    youtubeVideoInfo.view_count = Integer.parseInt(tmpArray[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if ("length_seconds".equals(tmpArray[0])) {
                try {
                    youtubeVideoInfo.duration = Long.parseLong(tmpArray[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if ("url_encoded_fmt_stream_map".equals(tmpArray[0])) {
                url_encoded_fmt_stream_map = tmpArray[1];
            } else if ("adaptive_fmts".equals(tmpArray[0])) {
                adaptive_fmts = tmpArray[1];
            } else if ("author".equals(tmpArray[0])) {
                youtubeVideoInfo.author = tmpArray[1];
            }
        }

        if (0 == url_encoded_fmt_stream_map.length() && 0 == adaptive_fmts.length()) {
            return null;
        }

        youtubeVideoInfo.check_type = "youtube";
        youtubeVideoInfo.id = youtubeId;
        youtubeVideoInfo.url = "https://www.youtube.com/watch?v=" + youtubeId;

        String[] arr;
        if (url_encoded_fmt_stream_map.length() > 0) {
            arr = url_encoded_fmt_stream_map.split(",");
        } else {
            arr = adaptive_fmts.split(",");
        }

        for (int j = 0; j < arr.length; ++j) {
            String[] arr1 = arr[j].split("&");
            int itag = 0;
            String VideoItemUrl = "";
            for (int k = 0; k < arr1.length; ++k) {
                String[] arr2 = arr1[k].split("=");
                if (2 != arr2.length) continue;
                if ("itag".equals(arr2[0])) {
                    try {
                        itag = Integer.parseInt(arr2[1]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if ("s".equals(arr2[0]) || "signature".equals(arr2[0])) {
                    // need sign
                    // String sign = arr2[1];
                    //Analytics.Instacnce().sendAction(Analytics.aPreloadParse, "action", "analytics_fail", "video_id", mYTVideoId, "err_type", "sign");
                    return null;
                } else if ("url".equals(arr2[0])) {
                    VideoItemUrl = URLDecoder.decode(arr2[1], "utf-8");
                }
            }

            YouTubeAnalytics.MateFormat tmp_format = YouTubeAnalytics.getInstance().getFormat(itag);

            if (tmp_format != null && tmp_format.canList) {
                YoutubeVideoItem tmp_video_file = new YoutubeVideoItem();
                tmp_video_file.f_id = youtubeVideoInfo.id + "_" + tmp_format.itag;
                tmp_video_file.format = tmp_format.format;
                tmp_video_file.width = tmp_format.width;
                tmp_video_file.height = tmp_format.height;
                tmp_video_file.quality = tmp_format.itag;
                tmp_video_file.title = tmp_format.title;
                tmp_video_file.url = VideoItemUrl;
                youtubeVideoInfo.files.add(tmp_video_file);
            }
        }

        return youtubeVideoInfo;
    }
}
