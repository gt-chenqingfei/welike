package com.redefine.im.remoting.util;

import com.redefine.im.remoting.protocol.ImPacket;
import com.redefine.im.remoting.protocol.ImPacketHeader;

import java.util.concurrent.atomic.AtomicLong;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * <p/>
 * 序号ID 确认包Id 包类型 协议版本号 UID长度 UID 数据包长度 数据
 * ----------------------------------
 * -------------------------------------------------------------
 * |seqId(8byte)|ackId(8byte) |type
 * (2byte)|version(1byte)|uidLength(4byte)|uid|length(4byte)|data|
 * ----------
 * ----------------------------------------------------------------
 * ---------------------
 * <p/>
 */
public class QPacketUtils {

    private static final byte QPACKET_HEADER_LENGTH = 8 + 8 + 2 + 1 + 4 + 4;

    private static final byte QPACKET_HEADER_UID_SKIP = 8 + 8 + 2 + 1;

    private static final AtomicLong seqId = new AtomicLong(0);

    /**
     * 下一个seqId
     * 
     * @return
     */
    public static long nextSeqId() {
        return seqId.incrementAndGet() % 10000000;
    }

    /**
     * 将队列中的消息编码为bibipacket
     *
     * @param buffer
     * @return
     */
    public static String unmarshalFromId(byte[] buffer) {
        ByteBuf buff = Unpooled.wrappedBuffer(buffer);
        buff.skipBytes(QPACKET_HEADER_UID_SKIP);
        int uidLength = buff.readInt();
        String id = new String(buff.readBytes(uidLength).array());
        return id;
    }

    /**
     * 反序列化
     * 
     * @param buff
     * @return
     */
    public static ImPacket unmarshalQueue(ByteBuf buff) {

        ImPacket packet = new ImPacket();
        //反序列化头部
        ImPacketHeader.unmarshalHeader(packet.getHeader(), buff);

        //5.读取数据长度
        int fromIdLength = buff.readInt();
        String fromId = new String(buff.readBytes(fromIdLength).array());
        packet.getHeader().setFromId(fromId);

        int toIdLength = buff.readInt();
        String toId = new String(buff.readBytes(toIdLength).array());
        packet.getHeader().setToId(toId);

        //5.读取数据长度
        int length = buff.readInt();

        //4.读取真实的数据包
        ByteBuf buf = buff.readBytes(length);
        packet.setData(buf);
        return packet;
    }

    /**
     * 将队列中的消息编码为bibipacket
     *
     * @param buffer
     * @return
     */
    public static ImPacket unmarshalQueue(byte[] buffer) {
        ByteBuf buff = Unpooled.wrappedBuffer(buffer);
        return unmarshalQueue(buff);
    }

    //序列化数据
    public static ByteBuf marshalQueue(ImPacket packet) {
        byte[] fromIdBytes = packet.getHeader().getFromId().getBytes();
        byte[] toIdBytes = packet.getHeader().getToId().getBytes();
        packet.getData().resetReaderIndex();

        ByteBuf buff = Unpooled.buffer(QPACKET_HEADER_LENGTH + fromIdBytes.length + toIdBytes.length + packet.getData().readableBytes());
        //写入我们的bibipacket的报文数据
        //头部序列化进去
        ImPacketHeader.marshalHeader(packet.getHeader(), buff);

        //序列化packet中的数据
        buff.writeInt(fromIdBytes.length);
        buff.writeBytes(fromIdBytes);
        buff.writeInt(toIdBytes.length);
        buff.writeBytes(toIdBytes);
        buff.writeInt(packet.getData().readableBytes());
        buff.writeBytes(packet.getData());
        return buff;
    }
}