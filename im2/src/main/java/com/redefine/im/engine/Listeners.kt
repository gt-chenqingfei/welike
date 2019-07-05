package com.redefine.im.engine

import com.google.protobuf.GeneratedMessageV3
import com.redefine.im.room.MESSAGE
import com.redefine.im.room.MessageDao
import com.redefine.im.room.SESSION
import com.redefine.im.service.socket.protocol.BibiProtoApplication

/**
 * IMEngine使用者 需要实现的Listener，只需要关心 新消息:onMessage() ; 发送消息结果：sendAck()
 */
interface EngineListener {
    /**
     * invoked after received messages.
     */
    fun onMessage(messages: ArrayList<GeneratedMessageV3>)

    /**
     * callback result of sending.
     */
    fun onSendAck(data: BibiProtoApplication.MessageArrivalAck)

//    fun onAccountChanged(uid: String)

    fun onEvent()

//    fun init()

    fun onDisconnect()

    fun onKickout()
}

interface KickOutListener {

    fun onIMTokenInvalid()
}

interface IEngine {

//    fun getCustomerSession(success: (SESSION?) -> Unit, error: (Int) -> Unit)


//    fun getSession(targetUid: String, targetName: String, targetAvatar: String, t: (SESSION) -> Unit)


//    fun getSession(sid: String, t: (SESSION?) -> Unit)


//    fun removeSession(sid: String)

    /**
     * 发送文字消息
     */
    fun sendText(session: SESSION, content: String, from: String)

    /**
     * 发送图片消息
     */
    fun sendImage(session: SESSION, path: String, from: String)

    /**
     * 重试发送消息
     */
    fun retry(session: SESSION, message: MESSAGE)

    /**
     * 设置 当前的 Session.
     */
    fun setCurrentSession(sid: String)
}

/**
 * 根据协议，抽象出的方法。需要客户端处理。
 */
interface EngineProtocol {

    fun syncAck(data: BibiProtoApplication.SyncDataPacket)

    fun notifyNew(data: BibiProtoApplication.NewMessageNotify)

    fun connAck()

    fun msgAck(data: BibiProtoApplication.MessageArrivalAck)

    fun disconnect()

    fun timeout()

    fun kickOut()

}