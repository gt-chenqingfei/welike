package com.redefine.im.service.socket;

import android.content.Context;
import android.text.TextUtils;

import com.redefine.foundation.framework.ScheduleService;
import com.redefine.foundation.utils.CommonHelper;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.im.service.socket.protocol.BibiProtocol;
import com.redefine.im.service.socket.protocol.ImPacketType;
import com.redefine.im.service.socket.protocol.Packer;
import com.redefine.welike.base.ErrorCode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class Heartbeat {
    private final static int TIMEOUT = 5;
    private Connector connector;
    private Packer packer;
    private HeartbeatCallback listener;
    private String taskId;
    private boolean notReceived;
    private Disposable timeout;
    private Context context;

    public interface HeartbeatCallback {

        void onHeartbeatConnectorFailed(Connector connector, int errCode);

    }

    private class HeartbeatTask implements Runnable {

        @Override
        public void run() {
            sendHeartbeat();
        }

    }

    public Heartbeat(Packer packer, Context context) {
        this.packer = packer;
        this.context = context;
    }

    public void setListener(HeartbeatCallback listener) {
        this.listener = listener;
    }

    public void start(Connector connector) {
        LogUtil.d("welike-im", "Heartbeat-start");
        this.connector = connector;
        notReceived = false;
        if (timeout != null) {
            timeout.dispose();
            timeout = null;
        }
        taskId = ScheduleService.getInstance().startRepeat(new HeartbeatTask(), 30, TimeUnit.SECONDS);
    }

    public void stop() {
        LogUtil.d("welike-im", "Heartbeat-stop taskid = " + taskId);
        connector = null;
        if (!TextUtils.isEmpty(taskId)) {
            ScheduleService.getInstance().cancel(taskId);
        }
    }

    public void receivedInterceptor(List<SocketData> socketDataList) {
        if (socketDataList != null && socketDataList.size() > 0) {
            LogUtil.d("welike-im", "Heartbeat-receivedInterceptor socketDataList size = " + String.valueOf(socketDataList.size()));
            List<SocketData> results = null;
            for (int i = 0; i < socketDataList.size(); i++) {
                SocketData socketData = socketDataList.get(i);
                if (socketData.getType() == ImPacketType.PROTOCOL_CONN_HEARTBEAT) {
                    LogUtil.d("welike-im", "Heartbeat-receivedInterceptor has heartbeat result");
                    notReceived = false;
                    if (timeout != null) {
                        timeout.dispose();
                        timeout = null;
                    }
                    if (results == null) {
                        results = new ArrayList<>();
                    }
                    results.add(socketData);
                }
            }
            if (results != null && results.size() > 0) {
                LogUtil.d("welike-im", "Heartbeat-receivedInterceptor heartbeat result count = " + results.size());
                socketDataList.removeAll(results);
            }
        }
    }

    private void sendHeartbeat() {
        LogUtil.d("welike-im", "Heartbeat-sendHeartbeat");
        if (connector == null) return;
        if (connector.getChannel() != null) {
            BibiProtocol.Heartbeat heartbeat = BibiProtocol.Heartbeat.newBuilder().setVersion(CommonHelper.getAppVersion(context)).setTimestamp(new Date().getTime()).build();
            SocketData socketData = new SocketData();
            socketData.setType(ImPacketType.PROTOCOL_CONN_HEARTBEAT);
            socketData.setData(heartbeat.toByteArray());
            try {
                connector.getChannel().write(ByteBuffer.wrap(packer.pack(socketData)));
                notReceived = true;
                LogUtil.d("welike-im", "Heartbeat-sendHeartbeat write heartbeat ok");
                timeout = AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d("welike-im", "Heartbeat-sendHeartbeat timeout notReceived = " + String.valueOf(notReceived));
                        if (notReceived) {
                            if (listener != null) {
                                listener.onHeartbeatConnectorFailed(connector, ErrorCode.ERROR_IM_SOCKET_TIMEOUT);
                            }
                            stop();
                        }
                    }

                }, TIMEOUT, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d("welike-im", "Heartbeat-sendHeartbeat write heartbeat failed1");
                if (listener != null) {
                    listener.onHeartbeatConnectorFailed(connector, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            LogUtil.d("welike-im", "Heartbeat-sendHeartbeat write heartbeat failed2");
            if (listener != null) {
                listener.onHeartbeatConnectorFailed(connector, ErrorCode.ERROR_IM_SOCKET_CLOSED);
            }
        }
    }

}
