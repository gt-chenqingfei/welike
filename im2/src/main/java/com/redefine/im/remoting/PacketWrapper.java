package com.redefine.im.remoting;



import com.redefine.im.remoting.protocol.ImPacket;
import com.redefine.im.remoting.protocol.ImPacketHeader;
import com.redefine.im.remoting.protocol.ImPacketType;
import com.redefine.im.service.socket.protocol.BibiProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * bibi packet的网络包
 * Created by blackbeans on 10/21/15.
 */
public final class PacketWrapper  {

    //当前协议包的版本1
    public static final byte PROTOCOL_VERSION = 1;

    /**
     * 封装connAck的包
     * @param connstatus
     * @return
     */
    public static ImPacket wrapConnStatus(ImPacketHeader header, BibiProtocol.ConnStatusPacket connstatus){
        ImPacket packet = new ImPacket();
        if (null != header){
          packet.setHeader(header.buildRespHeader());
        }
        packet.setType(ImPacketType.PROTOCOL_CONN_ACK);
        packet.setVersion(PROTOCOL_VERSION);

        ByteBuf buf = Unpooled.wrappedBuffer(connstatus.toByteArray());
        packet.setData(buf);
        return packet;
    }


    /**
     * 封装kick的包
     * @param kickPacket
     * @param header
     * @return
     */
    public static ImPacket wrapKick(ImPacketHeader header,BibiProtocol.KickPacket kickPacket){
        ImPacket packet = new ImPacket(header.buildRespHeader());
        packet.setType(ImPacketType.PROTOCOL_KICK_USER);
        packet.setVersion(PROTOCOL_VERSION);
        ByteBuf buf = Unpooled.wrappedBuffer(kickPacket.toByteArray());
        packet.setData(buf);
        return packet;
    }

    /**
     * 封装conn的心跳包
     * @param heartbeat
     * @param header
     * @return
     */
    public static ImPacket wrapHeartBeat(ImPacketHeader header,BibiProtocol.Heartbeat heartbeat){
        ImPacket packet = new ImPacket(header.buildRespHeader());
        packet.setType(ImPacketType.PROTOCOL_CONN_HEARTBEAT);
        packet.setVersion(PROTOCOL_VERSION);
        ByteBuf buf = Unpooled.wrappedBuffer(heartbeat.toByteArray());
        packet.setData(buf);
        return packet;
    }
}