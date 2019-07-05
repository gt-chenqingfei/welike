package com.redefine.im.remoting.protocol;

/**
 * 通讯包反序列化失败的exception
 *
 * @author <a href="mailto:yan.yongshuai@immomo.com">阎勇帅</a>
 * @create 2018-03-02 上午10:17
 **/
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

	public static final short PROTOCOL_MSG_CARD = 0x0108;

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

	public static String parseTypeName(int bibiPacketType) {
		String packetMehod = "WRONG METHOD";

		switch (bibiPacketType) {
			case ImPacketType.PROTOCOL_MEETING_REQ_ACK:
				packetMehod = "MEETING_REQ_ACK";
				break;
			case ImPacketType.PROTOCOL_MEETING_REQ:
				packetMehod = "MEETING_REQ";
				break;
			case ImPacketType.PROTOCOL_MESSAGE_READ_ACK:
				packetMehod = "MESSAGE_READ_ACK";
				break;
			case ImPacketType.PROTOCOL_MESSAGE_READ:
				packetMehod = "MESSAGE_READ";
				break;
			case ImPacketType.PROTOCOL_KICK_USER:
				packetMehod = "KICK_USER";
				break;
			case ImPacketType.PROTOCOL_CONN:
				packetMehod = "CONN";
				break;
			case ImPacketType.PROTOCOL_CONN_ACK:
				packetMehod = "CONN_ACK";
				break;
			case ImPacketType.PROTOCOL_CONN_HEARTBEAT:
				packetMehod = "HEARTBEAT";
				break;
			case ImPacketType.PROTOCOL_SYNC:
				packetMehod = "SYNC";
				break;
			case ImPacketType.PROTOCOL_SYNC_ACK:
				packetMehod = "SYNC_ACK";
				break;
			case ImPacketType.PROTOCOL_FIN:
				packetMehod = "SYNC_FIN";
				break;

			case ImPacketType.PROTOCOL_FIN_ACK:
				packetMehod = "SYNC_FIN_ACK";
				break;

			case ImPacketType.PROTOCOL_MSG_ACK:
				packetMehod = "MSG_ACK";
				break;

			case ImPacketType.PROTOCOL_NEW_MSG_NOTIFY:
				packetMehod = "NEW_MSG_NOTIFY";
				break;
			case ImPacketType.PROTOCOL_MSG_TEXT:
				packetMehod = "MSG_TEXT";
				break;
			case ImPacketType.PROTOCOL_MSG_CARD:
				packetMehod = "MSG_CARD";
				break;
			case ImPacketType.PROTOCOL_MSG_AUDIO:
				packetMehod = "MSG_AUDIO";
				break;
			case ImPacketType.PROTOCOL_MSG_VIDEO:
				packetMehod = "MSG_VIDEO";
				break;
			case ImPacketType.PROTOCOL_MSG_PIC:
				packetMehod = "MSG_PIC";
				break;
			case ImPacketType.PROTOCOL_MSG_EMOTION:
				packetMehod = "MSG_Emotion";
				break;
			case ImPacketType.PROTOCOL_MSG_NOTICE:
				packetMehod = "MSG_Notice";
				break;
			case ImPacketType.PROTOCOL_MSG_STATUS_READ:
				packetMehod = "MSG_STATUS_READ";
				break;
			case ImPacketType.PROTOCOL_BROADCAST_REQ:
				packetMehod = "BROADCAST_REQ";
				break;
			case ImPacketType.PROTOCOL_BROADCAST_REQ_ACK:
				packetMehod = "BROADCAST_REQ_ACK";
				break;
			case ImPacketType.PROTOCOL_ONLINE_REQ:
				packetMehod = "ONLINE_REQ";
				break;
			case ImPacketType.PROTOCOL_ONLINE_REQ_ACK:
				packetMehod = "ONLINE_REQ_ACK";
				break;

			default:
				break;
		}
		return packetMehod;
	}

}
