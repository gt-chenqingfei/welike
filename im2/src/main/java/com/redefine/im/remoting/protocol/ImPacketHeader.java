package com.redefine.im.remoting.protocol;

import com.redefine.im.remoting.util.QPacketUtils;

import io.netty.buffer.ByteBuf;

import java.io.Serializable;


/**
 * bibipacket的Header
 * <p/>
 * 总报文长度(不包含该字段4B) 序号ID 确认包Id 包类型 协议版本号 ------------------------------------
 * ----------------------------------------
 * |total_length(4byte)|seqId(8byte)|ackId(8byte) |type (2byte)|version(1byte)|
 * -------------------------------------------------- --------------------------
 * <p/>
 * <p/>
 */
public class ImPacketHeader implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public static final int HEADER_LENGTH = 2 + 8 + 8 + 1 + 4;

	private short type;

	// 请求序号
	private long seqId = 0L;

	// 确认包的序号ID -1 则为没有确认包
	private long ackId = -1L;

	// 协议版本号
	private byte version;

	// 接受者的id
	private String toId = "";

	// 发送者的id
	private String fromId = "";

	public short getType() {
		return type;
	}

	public ImPacketHeader(ImPacketHeader header) {
		super();
		this.type = header.getType();
		this.seqId = header.getSeqId();
		this.ackId = header.getAckId();
		this.version = header.getVersion();
		this.toId = header.getToId();
		this.fromId = header.getFromId();
	}

	public ImPacketHeader() {
	}

	public void setType(short type) {
		this.type = type;
	}

	public long getSeqId() {
		return seqId;
	}

	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}

	public long getAckId() {
		return ackId;
	}

	public void setAckId(long ackId) {
		this.ackId = ackId;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	/**
	 * 反序列化一下header2
	 *
	 * @param buff
	 * @return
	 */
	public static ImPacketHeader unmarshalHeader(ImPacketHeader header,
	                                             ByteBuf buff) {

		// 1.读取seqId
		long seqId = buff.readLong();
		// 2.读取seqId
		long ackId = buff.readLong();
		// 3.读取数据为类型
		short type = buff.readShort();
		// 4.读取数据包协议版本号
		byte version = buff.readByte();

		header.seqId = seqId;
		header.ackId = ackId;
		header.type = type;
		header.version = version;

		return header;
	}

	/**
	 * 序列化header
	 *
	 * @param header
	 * @param buff
	 */
	public static void marshalHeader(ImPacketHeader header, ByteBuf buff) {

		// 将头部的数据写入
		buff.writeLong(header.seqId);
		buff.writeLong(header.ackId);
		buff.writeShort(header.type);
		buff.writeByte(header.version);

		return;
	}

	/**
	 * 构建响应的头部
	 *
	 * @return
	 */
	public ImPacketHeader buildRespHeader() {
		ImPacketHeader header = new ImPacketHeader();
		header.setSeqId(QPacketUtils.nextSeqId());
		header.setAckId(this.getSeqId());
		header.setType(this.getType());
		header.setVersion(this.getVersion());
		header.setToId(this.fromId);
		header.setFromId(this.toId);

		return header;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ImPacketHeader header = (ImPacketHeader) o;

		if (type != header.type)
			return false;
		if (seqId != header.seqId)
			return false;
		if (ackId != header.ackId)
			return false;
		return version == header.version;

	}

	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	@Override
	public int hashCode() {
		int result = type;
		result = 31 * result + (int) (seqId ^ (seqId >>> 32));
		result = 31 * result + (int) (ackId ^ (ackId >>> 32));
		result = 31 * result + version;
		return result;
	}

	@Override
	public String toString() {
		return "BibiPacketHeader [type=" + type + ", seqId=" + seqId
				+ ", ackId=" + ackId + ", version=" + version + ", toId="
				+ toId + ", fromId=" + fromId + "]";
	}

}
