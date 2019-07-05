package com.redefine.im.service.socket;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.im.service.socket.protocol.Packer;
import com.redefine.welike.base.ErrorCode;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class Listener {
    private final int BUFFER_LEN = 1024;
    private Packer packer;
    private Selector readSelector;
    private final List<byte[]> queue = new ArrayList<>();
    private ListenerCallback listener;

    public interface ListenerCallback {

        void onListenerReceived(Connector connector, List<SocketData> socketDataList);
        void onListenerConnectorFailed(Connector connector, int errCode);

    }

    public Listener(Packer packer) {
        this.packer = packer;
    }

    public void setListener(ListenerCallback listener) {
        this.listener = listener;
    }

    public void start(final Connector connector, ScheduledExecutorService threadPool) {
        LogUtil.d("welike-im", "Listener-start");
        try {
            if (readSelector != null) {
                readSelector.close();
                readSelector = null;
            }
            readSelector = Selector.open();
            connector.getChannel().register(readSelector, SelectionKey.OP_READ);
        } catch (Exception e) {
            e.printStackTrace();
        }
        queue.clear();
        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LEN);
                try {
                    while (readSelector != null && readSelector.select() > 0) {
                        for (SelectionKey sk : readSelector.selectedKeys()) {
//                            if (sk.isReadable()) {
//                                LogUtil.d("welike-im", "Listener-start read buffer");
//                                int count;
//                                SocketChannel sc = (SocketChannel)sk.channel();
//                                do {
//                                    buffer.clear();
//                                    count = sc.read(buffer);
//                                    if (count > 0) {
//                                        queue.add(Arrays.copyOfRange(buffer.array(), 0, count));
//                                    }
//                                } while (count == BUFFER_LEN);
//
//                                if (queue.size() > 0) {
//                                    List<SocketData> list = packer.unpack(queue);
//                                    if (list != null && list.size() > 0) {
//                                        if (listener != null) {
//                                            listener.onListenerReceived(connector, list);
//                                        }
//                                    }
//                                }
//                                sk.interestOps(SelectionKey.OP_READ);
//                            }
                            readSelector.selectedKeys().remove(sk);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    stop();
                    queue.clear();
                    if (listener != null) {
                        listener.onListenerConnectorFailed(connector, ErrorCode.networkExceptionToErrCode(e));
                    }
                    return;
                }
                queue.clear();
            }

        });
    }

    public void stop() {
        LogUtil.d("welike-im", "Listener-stop");
        if (readSelector != null) {
            try {
                readSelector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            readSelector = null;
        }
    }

}
