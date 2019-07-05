package com.redefine.im.bean;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.redefine.foundation.framework.ScheduleService;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.im.cache.ImAccountSettingCache;
import com.redefine.im.parser.IMMessageParser;
import com.redefine.im.service.request.PullOfflineMessagesRequest;
import com.redefine.welike.base.request.BaseRequest;

import java.util.List;

/**
 * Created by liubin on 2018/2/4.
 */

public class IMOfflineMessagesTask implements ScheduleService.RetryTask {
    private Context context;
    private OfflineMessagesCallack listener;

    public interface OfflineMessagesCallack {

        void onOfflineMessages(List<IMMessageBase> messages, boolean last);

    }

    @Override
    public boolean runTask() {
//        String messagesCursorstamp = ImAccountSettingCache.getInstance().getImMessageStamp();
        String messagesCursorstamp = "";

        boolean last;
        boolean successed = true;
        PullOfflineMessagesRequest request;
        do {
            LogUtil.d("welike-im", "IMOfflineMessagesTask-runTask iteration messagesCursorstamp = " + messagesCursorstamp);
            request = new PullOfflineMessagesRequest(messagesCursorstamp, context);
            try {
                JSONObject result = request.req();
                if (result != null) {
                    if (result.get(BaseRequest.ERROR_CODE_KEY) == null) {
                        messagesCursorstamp = IMMessageParser.parseCursor(result);
                        last = IMMessageParser.parseLast(result);
                        List<IMMessageBase> messages = IMMessageParser.parseMessages(result);
                        int messagesCount = 0;
                        if (messages != null) {
                            messagesCount = messages.size();
                        }
                        LogUtil.d("welike-im", "IMOfflineMessagesTask-runTask iteration network ok messagesCursorstamp = " + messagesCursorstamp + " last =" + String.valueOf(last) + " messagesCount = " + messagesCount);
                        if (listener != null) {
                            listener.onOfflineMessages(messages, last);
                        }
                    } else {
                        LogUtil.d("welike-im", "IMOfflineMessagesTask-runTask iteration network failed1");
                        successed = false;
                        break;
                    }
                } else {
                    LogUtil.d("welike-im", "IMOfflineMessagesTask-runTask iteration network failed2");
                    successed = false;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d("welike-im", "IMOfflineMessagesTask-runTask iteration network failed3");
                successed = false;
                break;
            }
        } while (!last);

        if (successed) {
            LogUtil.d("welike-im", "IMOfflineMessagesTask-runTask successed write messagesCursorstamp");
            if (!TextUtils.isEmpty(messagesCursorstamp)) {
                ImAccountSettingCache.getInstance().setImMessageStamp(messagesCursorstamp);
            }
        }

        return successed;
    }

    public IMOfflineMessagesTask(Context context) {
        this.context = context;
    }

    public void setListener(OfflineMessagesCallack listener) { this.listener = listener; }

}
