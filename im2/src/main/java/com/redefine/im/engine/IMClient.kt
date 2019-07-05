package com.redefine.im.engine

import android.util.Log
import android.util.Log.w
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.redefine.im.d
import com.redefine.im.e
import com.redefine.im.remoting.PacketWrapper
import com.redefine.im.remoting.protocol.ImPacket
import com.redefine.im.remoting.protocol.ImPacketHeader
import com.redefine.im.remoting.protocol.ImPacketType
import com.redefine.im.service.socket.protocol.BibiProtoApplication
import com.redefine.im.service.socket.protocol.BibiProtocol
import com.redefine.im.threadTry
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import io.netty.handler.timeout.IdleStateHandler
import java.io.IOException
import java.net.InetSocketAddress


class IMClient(val host: String, val port: Int, val prototcol: EngineProtocol) {

    var channel: Channel? = null
    var bootstrap: Bootstrap? = null
    private var eventLoopGroup: NioEventLoopGroup? = null


    @Throws(IOException::class, InterruptedException::class)
    fun init() {
        eventLoopGroup = NioEventLoopGroup(10)
        bootstrap = Bootstrap().group(eventLoopGroup)
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_TIMEOUT, 60)
                .handler(ClientPipeline(prototcol))
    }

    @Throws(Exception::class)
    fun connect() {
        d("connect")
        channel = bootstrap!!.connect(host, port).sync().channel()
    }

    fun disconnect() {
        d("disconnect")
        channel?.let {
            if (it.isOpen) {
                it.close()
            }
        }
    }
}


class ClientPipeline(val prototcol: EngineProtocol) : ChannelInitializer<SocketChannel>() {

    @Throws(Exception::class)
    override fun initChannel(socketChannel: SocketChannel) {
        try {
            val pipeline = socketChannel.pipeline()
            //connector的个中 pipe, \r\n\n 的分包
            pipeline.addLast("frame-decoder", LengthFieldBasedFrameDecoder(16 * 1024, 0, 4, 0, 4))
            pipeline.addLast("client-decoder", BibiDecoder())
            pipeline.addLast("client-encoder", BibiEncoder())
            pipeline.addLast("timeout", IdleStateHandler(10, 10, 10))
            pipeline.addLast("client-bibi-remote", IMHandler(prototcol))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

/**
 *
 * 基于分割的帧包解析
 */
class BibiDecoder : ByteToMessageDecoder() {

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        try {
            val addr = ctx.channel().remoteAddress() as InetSocketAddress
//            d(String.format("BibiDecoder|Trace|decode|%s:%s|%s", addr.hostName, addr.port, msg.readableBytes()))

            if (msg.readableBytes() > 0) {
                //解析出我们需要的bibiPacket
                val packet = ImPacket.unmarshal(msg)
                out.add(packet)
            } else {
                //读超时读取到的空的数据包
//                d(String.format("t BibiDecoder|ERROR|decode|%s", msg))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Created by blackbeans on 10/19/15.
 */
class BibiEncoder : MessageToByteEncoder<ImPacket>() {

    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, msg: ImPacket, out: ByteBuf) {
        d("DDAI", "BibiEncoder ")
        try {
            msg.marshal(out)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

fun showLog(o: Any): String =
        GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().create().toJson(o)

/**
 * Netty 使用的 ChannelInboundHandlerAdapter。用来接受Netty 传出的数据，根据
 */
class IMHandler(private val prototcol: EngineProtocol) : ChannelInboundHandlerAdapter() {
    var idle = 0

    override fun channelActive(ctx: ChannelHandlerContext?) {
        e("channelActive")
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        e("channelInactive!!!")
        prototcol.disconnect()
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any?) {
        //处理心跳
        if (evt is IdleStateEvent) {
            //写空闲连接空闲关闭的包，并关闭
            if (evt.state() == IdleState.WRITER_IDLE) {
                val heartbeat = BibiProtocol.Heartbeat.newBuilder()
                        .setVersion(1).setTimestamp(System.currentTimeMillis()).build()

                val packet = PacketWrapper.wrapHeartBeat(ImPacketHeader(), heartbeat)
                ctx.writeAndFlush(packet)
                d("userEventTriggered|Send Heartbeat|....")
            }
            if (evt.state() == IdleState.READER_IDLE) {
                if (idle++ > 10) {
                    prototcol.timeout()
                }
            }
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        threadTry {
            val packet = msg as ImPacket
            when {
                packet.type == ImPacketType.PROTOCOL_CONN_ACK -> {
                    val connAck = BibiProtocol.ConnStatusPacket.parseFrom(packet.data.array())
                    com.redefine.im.w("Connection Client Accept MsgBody:[${showLog(connAck.feedback)}]")
                    when {
                        connAck.code == 200 -> prototcol.connAck()
                        connAck.code in listOf(403, 407, 408, 409) -> prototcol.kickOut()
                        else -> prototcol.disconnect()
                    }
                }
                packet.type == ImPacketType.PROTOCOL_CONN_HEARTBEAT -> {
                    idle = 0
                    val hb = BibiProtocol.Heartbeat.parseFrom(packet.data.array())
//                    d("Connection Client Heartbeat MsgBody:[${showLog(hb)}]")
                }
                packet.type == ImPacketType.PROTOCOL_MSG_ACK -> {
                    val hb = BibiProtoApplication.MessageArrivalAck.parseFrom(packet.data.array())
                    com.redefine.im.w("Connection Client msg_ack MsgBody:[${showLog(hb)}]")
                    prototcol.msgAck(hb)
                }
                packet.type == ImPacketType.PROTOCOL_SYNC_ACK -> {
                    val hb = BibiProtoApplication.SyncDataPacket.parseFrom(packet.data.array())
                    prototcol.syncAck(hb)
                    e("Connection Client SYNC_ACK MsgBody:[${showLog(hb)}]")
                }
                packet.type == ImPacketType.PROTOCOL_NEW_MSG_NOTIFY -> {
                    e("Connection Client PROTOCOL_NEW_MSG_NOTIFY MsgBody:[]")
                    val hb = BibiProtoApplication.NewMessageNotify.parseFrom(packet.data.array())
                    prototcol.notifyNew(hb)
                }
            }
        }
    }
}