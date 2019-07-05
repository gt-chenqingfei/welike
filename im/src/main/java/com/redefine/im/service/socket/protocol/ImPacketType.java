package com.redefine.im.service.socket.protocol;

public final class ImPacketType {
    // 连接协议包
    public static final short PROTOCOL_CONN = 0x0000;

    // 连接确认包
    public static final short PROTOCOL_CONN_ACK = 0x0001;

    // 连接心跳包
    public static final short PROTOCOL_CONN_HEARTBEAT = 0x0002;

    // 用户sync
    public static final short PROTOCOL_SYNC = 0x0003;

    // 用户sync_ack
    public static final short PROTOCOL_SYNC_ACK = 0x0004;

    // 用户sync_fin
    public static final short PROTOCOL_FIN = 0x0005;

    // 用户sync_fin_ack
    public static final short PROTOCOL_FIN_ACK = 0x0006;

    // 用户发送消息ack
    public static final short PROTOCOL_MSG_ACK = 0x0007;

    // 用户有新消息的提醒
    public static final short PROTOCOL_NEW_MSG_NOTIFY = 0x0008;

    // connector发出来踢人的消息
    public static final short PROTOCOL_KICK_USER = 0x0009;

    // 用户已读消息
    public static final short PROTOCOL_MESSAGE_READ = 0x000A;

    // 用户已读消息反馈
    public static final short PROTOCOL_MESSAGE_READ_ACK = 0x000B;

    // 登出协议
    public static final short PROTOCOL_LOG_OUT = 0x000C;

    // 用户消息协议
    public static final short PROTOCOL_MSG_TEXT = 0x0100;

    public static final short PROTOCOL_MSG_AUDIO = 0x0101;

    public static final short PROTOCOL_MSG_VIDEO = 0x0102;

    public static final short PROTOCOL_MSG_PIC = 0x0103;

    public static final short PROTOCOL_MSG_EMOTION = 0x0104;

    public static final short PROTOCOL_MSG_NOTICE = 0x0105;

    public static final short PROTOCOL_MSG_NEWS = 0x0106;

    public static final short PROTOCOL_MSG_RECALLED = 0x0107;

    // 消息状态协议额
    public static final short PROTOCOL_MSG_STATUS_READ = 0x0200;

    // 发起视频请求
    public static final short PROTOCOL_MEETING_REQ = 0x0300;

    // 如果用户不在线 直接发送ack
    public static final short PROTOCOL_MEETING_REQ_ACK = 0x0301;

    // 发起视频请求
    public static final short PROTOCOL_ONLINE_REQ = 0x0302;

    // 如果用户不在线 直接发送ack
    public static final short PROTOCOL_ONLINE_REQ_ACK = 0x0303;

    // 发起直播请求
    public static final short PROTOCOL_BROADCAST_REQ = 0x0304;

    // 如果用户不在线 直接发送ack
    public static final short PROTOCOL_BROADCAST_REQ_ACK = 0x0305;

}
