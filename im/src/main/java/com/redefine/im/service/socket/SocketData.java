package com.redefine.im.service.socket;

public class SocketData {
    private long reqId;
    private long ackId;
    private short type;
    private byte[] data;

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public long getReqId() {
        return reqId;
    }

    public void setReqId(long reqId) {
        this.reqId = reqId;
    }

    public long getAckId() {
        return ackId;
    }

    public void setAckId(long ackId) {
        this.ackId = ackId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
