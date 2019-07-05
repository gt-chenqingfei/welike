package com.redefine.im.remoting.protocol;

import com.redefine.im.remoting.IQueue;
import com.redefine.im.remoting.util.QPacketUtils;

import java.io.Serializable;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


/**
 * <p/>
 * 总报文长度(不包含该字段4B) 序号ID 确认包Id 包类型 协议版本号 数据包长度 数据 ----------------------------
 * -------------------------------------------------------------------
 * |total_length(4byte)|seqId(8byte)|ackId(8byte) |type
 * (2byte)|version(1byte)|length(4byte)|data| ------------------------------
 * -----------------------------------------------------------------
 * <p/>
 */
public class ImPacket extends QPacketUtils implements Serializable {

	private static final long serialVersionUID = 1L;

	private ImPacketHeader header;

	// 包真是协议数据类型
	private ByteBuf data;

	// 需要被消费的queue
	private volatile IQueue queue;

	public ImPacket() {
		this.header = new ImPacketHeader();
	}

	public ImPacket(ImPacketHeader header) {
		this.header = header;
	}

	public void setHeader(ImPacketHeader header) {
		this.header = header;
	}

	public void attachQueue(IQueue queue) {
		this.queue = queue;
	}

	public void detachQueue() {
		this.queue = null;
	}

	public IQueue getQueue() {
		return this.queue;
	}

	public void setSeqId(long seqId) {
		this.header.setSeqId(seqId);
	}

	public long getSeqId() {
		return this.header.getSeqId();
	}

	public long getAckId() {
		return this.header.getAckId();
	}

	public void setAckId(long ackId) {
		this.header.setAckId(ackId);
	}

	public short getType() {
		return this.header.getType();
	}

	public void setType(short type) {
		this.header.setType(type);
	}

	public byte getVersion() {
		return this.header.getVersion();
	}

	public void setVersion(byte version) {
		this.header.setVersion(version);
	}

	public ImPacketHeader getHeader() {
		return header;
	}

	public ByteBuf getData() {
		return data;
	}

	public void setData(ByteBuf data) {
		this.data = data;
	}

	/**
	 * 反序列化
	 * 
	 * @param buffer
	 * @return
	 */
	public static ImPacket unmarshal(byte[] buffer) {
		return unmarshal(Unpooled.wrappedBuffer(buffer));
	}

	/**
	 * 解析packet
	 * 
	 * @param buff
	 * @return
	 */
	public static ImPacket unmarshal(ByteBuf buff) {

		ImPacket packet = new ImPacket();
		ImPacketHeader.unmarshalHeader(packet.header, buff);
		// 数据包的长度
		int length = buff.readInt();
		// 4.读取真实的数据包
		ByteBuf buf = buff.readBytes(length);
		packet.data = buf;

		return packet;
	}

	// 序列化数据
	public void marshal(ByteBuf buff) {
		// 写入报文长度
		buff.writeInt(ImPacketHeader.HEADER_LENGTH
				+ this.data.readableBytes());

		// 写入我们的bibipacket的报文数据
		ImPacketHeader.marshalHeader(this.header, buff);
		buff.writeInt(this.data.readableBytes());
		buff.writeBytes(this.data);
	}

	/**
	 * 获取当前包的长度包括FromUid和ToUid
	 * 
	 * @return
	 */
	public int getLengthWithId() {
		return ImPacketHeader.HEADER_LENGTH + 4 + 4
				+ this.header.getToId().length()
				+ this.header.getFromId().length() + this.data.readableBytes();
	}

}
