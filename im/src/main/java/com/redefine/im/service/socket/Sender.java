package com.redefine.im.service.socket;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.im.service.socket.protocol.ImPacketType;
import com.redefine.im.service.socket.protocol.Packer;
import com.redefine.welike.base.ErrorCode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class Sender {
    private Packer packer;
    private Connector connector;
    private SenderCallback listener;

    public interface SenderCallback {

        void onSenderResults(Connector connector, List<SocketData> results);
        void onSenderConnectorFailed(Connector connector, SocketData socketData, int errCode);

    }

    public Sender(Packer packer) {
        this.packer = packer;
    }

    public void reset(Connector connector) {
        this.connector = connector;
    }

    public void setListener(SenderCallback listener) {
        this.listener = listener;
    }

    public void send(final SocketData socketData, ScheduledExecutorService threadPool) {
        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                doSend(socketData);
            }

        });
    }

    public void receivedInterceptor(List<SocketData> socketDataList) {
        if (socketDataList != null && socketDataList.size() > 0) {
            LogUtil.d("welike-im", "Sender-receivedInterceptor socketDataList size = " + String.valueOf(socketDataList.size()));
            List<SocketData> results = null;
            for (int i = 0; i < socketDataList.size(); i++) {
                SocketData socketData = socketDataList.get(i);
                if (socketData.getType() == ImPacketType.PROTOCOL_MSG_ACK) {
                    if (results == null) {
                        results = new ArrayList<>();
                    }
                    results.add(socketData);
                }
            }
            if (results != null && results.size() > 0) {
                socketDataList.removeAll(results);
                LogUtil.d("welike-im", "Sender-receivedInterceptor results size = " + String.valueOf(results.size()));
            }
            if (listener != null) {
                listener.onSenderResults(connector, results);
            }
        }
    }

    public void doSend(SocketData socketData) {
        LogUtil.d("welike-im", "Sender-send socketData type = " + String.valueOf(socketData.getType()));
        if (connector != null && connector.getStatus() == Connector.CONNECTOR_STATUS_READY) {
            try {
                connector.write(ByteBuffer.wrap(packer.pack(socketData)));
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d("welike-im", "Sender-send failed");
                if (listener != null) {
                    listener.onSenderConnectorFailed(connector, socketData, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            LogUtil.d("welike-im", "Sender-send failed2");
            if (listener != null) {
                listener.onSenderConnectorFailed(connector, socketData, ErrorCode.ERROR_IM_SOCKET_CLOSED);
            }
        }
    }

}
