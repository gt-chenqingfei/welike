package com.redefine.im.service.socket;

import android.content.Context;

import com.redefine.foundation.utils.CommonHelper;
import com.redefine.foundation.utils.LogUtil;
import com.redefine.im.BuildConfig;
import com.redefine.im.service.socket.protocol.BibiProtocol;
import com.redefine.im.service.socket.protocol.ImPacketType;
import com.redefine.im.service.socket.protocol.Packer;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.LanguageSupportManager;
import com.redefine.welike.base.URLCenter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class Connector {
    public static final int CONNECTOR_STATUS_CLOSE = 0;
    public static final int CONNECTOR_STATUS_CONNECTING = 1;
    public static final int CONNECTOR_STATUS_CONNECTED = 2;
    public static final int CONNECTOR_STATUS_AUTHING = 3;
    public static final int CONNECTOR_STATUS_READY = 4;
    private static final int CONNECTOR_SOCKET_TIMEOUT = 5000;
    private String channelId;
    private SocketChannel channel;
    private String token;
    private String uid;
    private final Packer packer;
    private int status;
    private ConnectorCallback listener;
    private ScheduledExecutorService threadPool;
    private Context context;

    interface ConnectorCallback {

        void onConnectorStatusChanged(Connector connector, int status, int errCode);

    }

    public Connector(ScheduledExecutorService threadPool, Packer packer, Context context) {
        status = CONNECTOR_STATUS_CLOSE;
        channelId = CommonHelper.generateUUID();
        this.threadPool = threadPool;
        this.packer = packer;
        this.context = context;
    }

    public void setListener(ConnectorCallback listener) {
        this.listener = listener;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public String getChannelId() {
        return channelId;
    }

    public int getStatus() {
        return status;
    }

    public void connect(String token, String uid) {
        LogUtil.d("welike-im", "Connector-connect token = " + token + " uid = " + uid + " status = " + String.valueOf(status));
        if (status != CONNECTOR_STATUS_CLOSE) return;

        this.token = token;
        this.uid = uid;
        status = CONNECTOR_STATUS_CONNECTING;
        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                doConnect();
            }

        });

    }

    public void receivedInterceptor(List<SocketData> socketDataList) {
        if (socketDataList != null && socketDataList.size() > 0) {
            LogUtil.d("welike-im", "Connector-receivedInterceptor socketDataList size =" + String.valueOf(socketDataList.size()));
            List<SocketData> connStatusList = null;
            for (int i = 0; i < socketDataList.size(); i++) {
                SocketData socketData = socketDataList.get(i);
                if (socketData.getType() == ImPacketType.PROTOCOL_CONN_ACK) {
                    if (connStatusList == null) {
                        connStatusList = new ArrayList<>();
                    }
                    connStatusList.add(socketData);
                    BibiProtocol.ConnStatusPacket connAck = null;
                    try {
                        connAck = BibiProtocol.ConnStatusPacket.parseFrom(socketData.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (connAck != null) {
                        int errCode = connAck.getCode();
                        LogUtil.d("welike-im", "Connector-receivedInterceptor connAck errCode =" + String.valueOf(errCode));
                        if (errCode == 200) {
                            if (status == CONNECTOR_STATUS_AUTHING) {
                                status = CONNECTOR_STATUS_READY;
                                if (listener != null) {
                                    listener.onConnectorStatusChanged(this, status, ErrorCode.ERROR_SUCCESS);
                                }
                            }
                        } else if (errCode == 403 || errCode == 407 || errCode == 408) {
                            if (listener != null) {
                                listener.onConnectorStatusChanged(this, status, ErrorCode.ERROR_NETWORK_AUTH_NOT_MATCH);
                            }
                        } else {
                            if (listener != null) {
                                listener.onConnectorStatusChanged(this, status, ErrorCode.ERROR_IM_SOCKET_CLOSED);
                            }
                        }
                    } else {
                        if (listener != null) {
                            listener.onConnectorStatusChanged(this, status, ErrorCode.ERROR_IM_SOCKET_CLOSED);
                        }
                    }
                }
            }
            if (connStatusList != null && connStatusList.size() > 0) {
                LogUtil.d("welike-im", "Connector-receivedInterceptor connStatusList size =" + String.valueOf(connStatusList.size()));
                socketDataList.removeAll(connStatusList);
            }
        }
    }

    public void disconnect() {
        LogUtil.d("welike-im", "Connector-disconnect status =" + String.valueOf(status));
        if (status == CONNECTOR_STATUS_CLOSE) return;
        threadPool = null;
        doDisconnect();
    }

    public void write(ByteBuffer buffer) throws Exception {
        if (channel != null) {
            LogUtil.d("welike-im", "Connector-write channel has status = " + String.valueOf(status));
        } else {
            LogUtil.d("welike-im", "Connector-write channel null status = " + String.valueOf(status));
        }
        if (channel != null && status != CONNECTOR_STATUS_CONNECTING && status != CONNECTOR_STATUS_CLOSE) {
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        } else {
            LogUtil.d("welike-im", "Connector-write failed");
            if (listener != null) {
                listener.onConnectorStatusChanged(this, status, ErrorCode.ERROR_IM_SOCKET_CLOSED);
            }
        }
    }

    private synchronized void doConnect() {
        LogUtil.d("welike-im", "Connector-doConnect status = " + String.valueOf(status));
        clear();
        try {
            channel = SocketChannel.open();
            channel.socket().setSoTimeout(CONNECTOR_SOCKET_TIMEOUT);
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(URLCenter.getHostIM(), 8200));
            while (!channel.finishConnect()) {
                Thread.sleep(10);
            }
            status = CONNECTOR_STATUS_CONNECTED;
            LogUtil.d("welike-im", "Connector-doConnect successed");
            if (listener != null) {
                listener.onConnectorStatusChanged(this, status, ErrorCode.ERROR_SUCCESS);
            }
            auth();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("welike-im", "Connector-doConnect failed");
            if (listener != null) {
                listener.onConnectorStatusChanged(this, status, ErrorCode.networkExceptionToErrCode(e));
            }
        }
    }

    private void auth() {
        LogUtil.d("welike-im", "Connector-auth status = " + String.valueOf(status));
        if (status == CONNECTOR_STATUS_CONNECTED) {
            status = CONNECTOR_STATUS_AUTHING;
            threadPool.execute(new Runnable() {

                @Override
                public void run() {
                    doAuth();
                }

            });

        }
    }

    private synchronized void doAuth() {
        LogUtil.d("welike-im", "Connector-doAuth status = " + String.valueOf(status));
        BibiProtocol.ConnMeta connMeta = null;
        String language = LanguageSupportManager.getInstance().getCurrentMainLanguageType();
        try {
            connMeta = BibiProtocol.ConnMeta.newBuilder()
                    .setUid(uid)
                    .setDeviceType(BibiProtocol.ConnMeta.DeviceType.Android)
                    .setDeviceInfo(CommonHelper.getDeviceModel())
                    .setSessionId(token)
                    .setLa(language)
                    .setVersion(CommonHelper.getAppVersion(context)).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connMeta != null) {
            SocketData socketData = new SocketData();
            socketData.setType(ImPacketType.PROTOCOL_CONN);
            socketData.setData(connMeta.toByteArray());
            try {
                write(ByteBuffer.wrap(packer.pack(socketData)));
                LogUtil.d("welike-im", "Connector-doAuth write auth ok");
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d("welike-im", "Connector-doAuth write auth failed");
                if (listener != null) {
                    listener.onConnectorStatusChanged(this, status, ErrorCode.networkExceptionToErrCode(e));
                }
            }
        } else {
            LogUtil.d("welike-im", "Connector-doAuth connMeta build failed");
            if (listener != null) {
                listener.onConnectorStatusChanged(this, status, ErrorCode.ERROR_IM_AUTH_NOT_READY);
            }
        }
    }

    private void doDisconnect() {
        LogUtil.d("welike-im", "Connector-doDisconnect status =" + String.valueOf(status));
        clear();
        if (status != CONNECTOR_STATUS_CLOSE) {
            status = CONNECTOR_STATUS_CLOSE;
            if (listener != null) {
                listener.onConnectorStatusChanged(this, status, ErrorCode.ERROR_SUCCESS);
            }
        }
    }

    private void clear() {
        LogUtil.d("welike-im", "Connector-clear");
        if (channel != null) {
            try {
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            channel = null;
        }
    }

}
