package com.redefine.im.service.socket;

import android.content.Context;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.im.service.socket.protocol.Packer;
import com.redefine.welike.base.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ConnectionManager implements Connector.ConnectorCallback, Listener.ListenerCallback, Sender.SenderCallback, Heartbeat.HeartbeatCallback {
    private Connector connector;
    private final Listener connectorListener;
    private final Sender connectorSender;
    private final Heartbeat heartbeat;
    private final Packer packer = new Packer();
    private String token;
    private String uid;
    private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);
    private final List<ConnectionManagerCallback> listeners = new ArrayList<>();
    private Disposable reconnectDisposable;
    private Context context;

    public ConnectionManager(Context context) {
        connector = new Connector(threadPool, packer, context);
        connector.setListener(this);
        connectorListener = new Listener(packer);//WTF???
        connectorSender = new Sender(packer);//WTF???
        heartbeat = new Heartbeat(packer, context);
        this.context = context;
    }

    public void register(ConnectionManagerCallback listener) {
        synchronized (listeners) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    public void unregister(ConnectionManagerCallback listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void connect(String token, String uid) {
        LogUtil.d("welike-im", "ConnectionManager-connect");
        if (threadPool == null) {
            threadPool = Executors.newScheduledThreadPool(4);
        }
        if (connector == null) {
            connector = new Connector(threadPool, packer, context);
        }
        this.token = token;
        this.uid = uid;
        connector.connect(token, uid);
    }

    public void send(final SocketData socketData) {
        LogUtil.d("welike-im", "ConnectionManager-send");
        if (connector != null) {
            if (connector.getStatus() == Connector.CONNECTOR_STATUS_READY) {
                connectorSender.send(socketData, threadPool);
            } else {
                synchronized (listeners) {
                    LogUtil.d("welike-im", "ConnectionManager-send failed2 listeners size = " + listeners.size());
                    for (int i = 0; i < listeners.size(); i++) {
                        final ConnectionManagerCallback callback = listeners.get(i);
                        callback.onConnectionMessageSentFailed(connector.getChannelId(), socketData, ErrorCode.ERROR_IM_AUTH_NOT_READY);
                    }
                }
                disconnect();
                retryConnect();
            }
        } else {
            synchronized (listeners) {
                LogUtil.d("welike-im", "ConnectionManager-send failed1 listeners size = " + listeners.size());
                for (int i = 0; i < listeners.size(); i++) {
                    final ConnectionManagerCallback callback = listeners.get(i);
                    callback.onConnectionMessageSentFailed(null, socketData, ErrorCode.ERROR_NETWORK_INVALID);
                }
            }
            disconnect();
            retryConnect();
        }
    }

    public void disconnect() {
        LogUtil.d("welike-im", "ConnectionManager-disconnect");
        if (connector != null) {
            heartbeat.setListener(null);
            heartbeat.stop();
            connectorListener.setListener(null);
            connectorListener.stop();
            threadPool.shutdown();
            threadPool = null;
            connector.disconnect();
            connector.setListener(null);
            connector = null;
            connectorSender.reset(null);
        }
    }

    @Override
    public void onConnectorStatusChanged(Connector connector, int status, int errCode) {
        if (this.connector == connector) {
            if (errCode == ErrorCode.ERROR_SUCCESS) {
                if (status == Connector.CONNECTOR_STATUS_CONNECTED) {
                    LogUtil.d("welike-im", "ConnectionManager-onConnectorStatusChanged connected");
                    connectorListener.setListener(this);
                    connectorListener.start(connector, threadPool);
                    heartbeat.setListener(this);
                    heartbeat.start(connector);
                } else if (status == Connector.CONNECTOR_STATUS_READY) {
                    LogUtil.d("welike-im", "ConnectionManager-onConnectorStatusChanged auth ok");
                    connectorSender.reset(connector);
                    connectorSender.setListener(this);
                    if (threadPool != null) {
                        final String channelId = connector.getChannelId();
                        threadPool.execute(new Runnable() {

                            @Override
                            public void run() {
                                synchronized (listeners) {
                                    for (int i = 0; i < listeners.size(); i++) {
                                        ConnectionManagerCallback callback = listeners.get(i);
                                        callback.onConnectionChannelReady(channelId);
                                    }
                                }
                            }

                        });
                    }
                }
            } else {
                LogUtil.d("welike-im", "ConnectionManager-onConnectorStatusChanged error = " + String.valueOf(errCode));
                disconnect();
                if (errCode == ErrorCode.ERROR_NETWORK_AUTH_NOT_MATCH) {
                    LogUtil.d("welike-im", "ConnectionManager-onConnectorStatusChanged token invalid");
                    if (threadPool != null) {
                        threadPool.execute(new Runnable() {

                            @Override
                            public void run() {
                                synchronized (listeners) {
                                    for (int i = 0; i < listeners.size(); i++) {
                                        ConnectionManagerCallback callback = listeners.get(i);
                                        callback.onConnectionTokenInvalid();
                                    }
                                }
                            }

                        });
                    }
                } else {
                    retryConnect();
                }
            }
        }
    }

    @Override
    public void onListenerReceived(Connector connector, final List<SocketData> socketDataList) {
        if (this.connector == connector) {
            threadPool.execute(new Runnable() {

                @Override
                public void run() {
                    LogUtil.d("welike-im", "ConnectionManager-onListenerReceived 1");
                    ConnectionManager.this.connector.receivedInterceptor(socketDataList);
                    LogUtil.d("welike-im", "ConnectionManager-onListenerReceived 2");
                    ConnectionManager.this.connectorSender.receivedInterceptor(socketDataList);
                    LogUtil.d("welike-im", "ConnectionManager-onListenerReceived 3");
                    ConnectionManager.this.heartbeat.receivedInterceptor(socketDataList);
                    LogUtil.d("welike-im", "ConnectionManager-onListenerReceived 4");
                    if (socketDataList.size() > 0) {
                        synchronized (listeners) {
                            for (int i = 0; i < listeners.size(); i++) {
                                ConnectionManagerCallback callback = listeners.get(i);
                                callback.onConnectionChannelReceived(ConnectionManager.this.connector.getChannelId(), socketDataList);
                            }
                        }
                    }
                }

            });
        }
    }

    @Override
    public void onListenerConnectorFailed(Connector connector, int errCode) {
        if (this.connector == connector) {
            LogUtil.d("welike-im", "ConnectionManager-onListenerConnectorFailed error = " + String.valueOf(errCode));
            disconnect();
            retryConnect();
        }
    }

    @Override
    public void onSenderConnectorFailed(Connector connector, final SocketData socketData, final int errCode) {
        if (this.connector == connector) {
            LogUtil.d("welike-im", "ConnectionManager-onSenderConnectorFailed error = " + String.valueOf(errCode));
            synchronized (listeners) {
                for (int i = 0; i < listeners.size(); i++) {
                    final ConnectionManagerCallback callback = listeners.get(i);
                    final String channelId = connector.getChannelId();
                    callback.onConnectionMessageSentFailed(channelId, socketData, errCode);
                }
            }
            disconnect();
            retryConnect();
        }
    }

    @Override
    public void onSenderResults(Connector connector, final List<SocketData> results) {
        if (this.connector == connector) {
            if (results != null) {
                LogUtil.d("welike-im", "ConnectionManager-onSenderResults results size = " + String.valueOf(results.size()));
            } else {
                LogUtil.d("welike-im", "ConnectionManager-onSenderResults results size = 0");
            }
            if (results != null && results.size() > 0) {
                if (threadPool != null) {
                    final String channelId = connector.getChannelId();
                    threadPool.execute(new Runnable() {

                        @Override
                        public void run() {
                            synchronized (listeners) {
                                for (int i = 0; i < listeners.size(); i++) {
                                    ConnectionManagerCallback callback = listeners.get(i);
                                    callback.onConnectionMessagesSentResult(channelId, results);
                                }
                            }
                        }

                    });
                }
            }
        }
    }

    @Override
    public void onHeartbeatConnectorFailed(Connector connector, int errCode) {
        if (this.connector == connector) {
            LogUtil.d("welike-im", "ConnectionManager-onHeartbeatConnectorFailed error = " + String.valueOf(errCode));
            if (threadPool != null) {
                final String channelId = connector.getChannelId();
                threadPool.execute(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (listeners) {
                            for (int i = 0; i < listeners.size(); i++) {
                                ConnectionManagerCallback callback = listeners.get(i);
                                callback.onConnectionMessageSentFailed(channelId, null, ErrorCode.ERROR_IM_SOCKET_CLOSED);
                            }
                        }
                    }

                });
            }
            disconnect();
            retryConnect();
        }
    }

    private void retryConnect() {
        LogUtil.d("welike-im", "ConnectionManager-retryConnect");
        if (reconnectDisposable == null) {
            LogUtil.d("welike-im", "ConnectionManager-retryConnect doRetryConnect after 2 sec");
            reconnectDisposable = AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {

                @Override
                public void run() {
                    reconnectDisposable = null;
                    doRetryConnect();
                }

            }, 2, TimeUnit.SECONDS);
        }
    }

    private void doRetryConnect() {
        LogUtil.d("welike-im", "ConnectionManager-doRetryConnect");
        if (threadPool == null) {
            threadPool = Executors.newScheduledThreadPool(4);
        }
        if (connector == null) {
            connector = new Connector(threadPool, packer, context);
            connector.setListener(this);
        }
        connector.connect(token, uid);
    }

}
