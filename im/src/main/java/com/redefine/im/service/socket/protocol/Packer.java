package com.redefine.im.service.socket.protocol;

import com.redefine.foundation.utils.LogUtil;
import com.redefine.im.service.socket.SocketData;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Packer {
    private final AtomicLong seqId = new AtomicLong(0);
    private final static int HEADER_LEN = 8 + 8 + 2 + 1 + 4;
    private final static byte VERSION = 1;

    public byte[] pack(SocketData socketData) {
        int len = HEADER_LEN + socketData.getData().length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + HEADER_LEN + socketData.getData().length);
        byteBuffer.putInt(len);
        socketData.setReqId(seqId.getAndIncrement());
        byteBuffer.putLong(socketData.getReqId());
        socketData.setAckId(-1);
        byteBuffer.putLong(-1);
        byteBuffer.putShort(socketData.getType());
        byteBuffer.put(VERSION);
        byteBuffer.putInt(socketData.getData().length);
        byteBuffer.put(socketData.getData());
        return byteBuffer.array();
    }

    public List<SocketData> unpack(List<byte[]> dataList) {
        List<SocketData> socketDataList = null;

        LogUtil.d("welike-im", "Packer-unpack dataList count = " + String.valueOf(dataList.size()));

        int count = 0;
        for (int i = 0; i < dataList.size(); i++) {
            count += dataList.get(i).length;
        }
        LogUtil.d("welike-im", "Packer-unpack byteBuffer size = " + String.valueOf(count));
        ByteBuffer byteBuffer = ByteBuffer.allocate(count);
        try {
            for (int i = 0; i < dataList.size(); i++) {
                byteBuffer.put(dataList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        byteBuffer.flip();
        int oneLen = byteBuffer.getInt();
        int lastLen = count - 4;
        int parseIdx = 0;
        LogUtil.d("welike-im", "Packer-unpack byteBuffer parseIdx = " + String.valueOf(parseIdx) + " lastLen = " + String.valueOf(lastLen) + " oneLen = " + String.valueOf(oneLen));
        while (oneLen <= lastLen) {
            if (socketDataList == null) {
                socketDataList = new ArrayList<>();
            }
            SocketData socketData = new SocketData();
            long reqId = byteBuffer.getLong();
            long ackId = byteBuffer.getLong();
            short type = byteBuffer.getShort();
            byte version = byteBuffer.get();
            int dataLen = byteBuffer.getInt();
            byte[] data = new byte[dataLen];
            byteBuffer.get(data);
            if (version == VERSION) {
                socketData.setReqId(reqId);
                socketData.setAckId(ackId);
                socketData.setType(type);
                socketData.setData(data);
                socketDataList.add(socketData);
            }

            int remaining = byteBuffer.remaining();
            LogUtil.d("welike-im", "Packer-unpack byteBuffer remaining = " + String.valueOf(remaining));
            if (remaining >= 4) {
                parseIdx++;
                oneLen = byteBuffer.getInt();
                lastLen = byteBuffer.remaining();
                LogUtil.d("welike-im", "Packer-unpack byteBuffer parseIdx = " + String.valueOf(parseIdx) + " lastLen = " + String.valueOf(lastLen) + " oneLen = " + String.valueOf(oneLen));
            } else {
                break;
            }
        }

        if (socketDataList != null && socketDataList.size() > 0) {
            int socketDataLen = 0;
            for (SocketData d : socketDataList) {
                socketDataLen += d.getData().length + HEADER_LEN + 4;
            }
            int dLen = 0;
            parseIdx = 0;
            while (true) {
                LogUtil.d("welike-im", "Packer-unpack remove dataList iteration = " + String.valueOf(parseIdx) + " dataList size = " + String.valueOf(dataList.size()));
                if (dataList.size() > 0) {
                    byte[] block = dataList.get(0);
                    dLen += block.length;
                    LogUtil.d("welike-im", "Packer-unpack remove dataList iteration = " + String.valueOf(parseIdx) + " dLen = " + String.valueOf(dLen) + " socketDataLen = " + String.valueOf(socketDataLen));
                    if (dLen <= socketDataLen) {
                        dataList.remove(0);
                    } else {
                        break;
                    }
                } else {
                    break;
                }
                parseIdx++;
            }
        }

        int socketDataListSize = 0;
        if (socketDataList != null) {
            socketDataListSize = socketDataList.size();
        }
        LogUtil.d("welike-im", "Packer-unpack end socketDataList size = " + String.valueOf(socketDataListSize));
        return socketDataList;
    }

}
