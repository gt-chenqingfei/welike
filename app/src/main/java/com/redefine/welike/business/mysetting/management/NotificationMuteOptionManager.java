package com.redefine.welike.business.mysetting.management;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.redefine.welike.MyApplication;
import com.redefine.welike.base.request.BaseRequest;
import com.redefine.welike.base.request.RequestCallback;
import com.redefine.welike.business.mysetting.management.request.GetPushOptionRequest;
import com.redefine.welike.business.mysetting.management.request.PostPushOptionRequest;
import com.redefine.welike.business.mysetting.ui.listener.INotificationOptionListener;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by mengnan on 2018/6/6.
 **/
public class NotificationMuteOptionManager {

    private static class NotificationMuteManagerHolder {
        public static NotificationMuteOptionManager instance = new NotificationMuteOptionManager();
    }

    public static NotificationMuteOptionManager getInstance() {
        return NotificationMuteManagerHolder.instance;
    }

    private INotificationOptionListener listener;

    public void setListener(INotificationOptionListener listener) {
        this.listener = listener;
    }

    public void check() {
        GetPushOptionRequest request = new GetPushOptionRequest(MyApplication.getAppContext());

        try {
            request.check(requestCallback);
        } catch (Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onOptionChanged(null);
                }
            });
        }
    }

    public void changeMute(MutePushOptions options) {
        PostPushOptionRequest request = new PostPushOptionRequest(MyApplication.getAppContext());

        try {
            request.postPushOptions(options, requestCallback);
        } catch (Exception e) {
            e.printStackTrace();
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onOptionChanged(null);
                }
            });
        }
    }

    private RequestCallback requestCallback = new RequestCallback() {
        @Override
        public void onError(BaseRequest request, int errCode) {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onOptionChanged(null);
                }
            });

        }

        @Override
        public void onSuccess(BaseRequest request, JSONObject result) throws Exception {

            final JSONObject result1 = result;

            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {

                    if (null != result1) {

                           boolean isDefault = result1.getBooleanValue("defaultSettings");

                        if (result1.containsKey("switchs")&&!isDefault) {
                            MutePushOptions mutePushOptions;
                            Gson gson = new Gson();
                            mutePushOptions = gson.fromJson(result1.toJSONString(), MutePushOptions.class);
                            listener.onOptionChanged(mutePushOptions);

                        } else {
                            listener.onOptionChanged(null);
                        }

                    } else {

                        listener.onOptionChanged(null);
                    }


                }
            });
        }
    };


}
