package com.redefine.welike.business.im

import com.google.protobuf.GeneratedMessageV3
import com.redefine.foundation.framework.Event
import com.redefine.im.*
import com.redefine.im.engine.ConnState
import com.redefine.im.engine.EngineListener
import com.redefine.im.engine.IEngine
import com.redefine.im.engine.IMEngine
import com.redefine.im.management.UniqueFilter
import com.redefine.im.room.*
import com.redefine.im.service.socket.protocol.BibiProtoApplication
import com.redefine.im.service.socket.protocol.BibiProtocol
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.io.WeLikeFileManager
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.profile.UploadListener
import com.redefine.welike.base.uploading.UploadingManager
import org.greenrobot.eventbus.EventBus
import java.io.File

class EngineWrapper(val imEngine: IMEngine) : EngineListener, IEngine {


    private var currentUID = ""
    private lateinit var messageDao: MessageDao
    private lateinit var sessionDao: SessionDao
    private val sendingPool = HashSet<String>()

    private var currentSid = ""


    override fun onEvent() {
        CountManager.reload()
    }

    override fun setCurrentSession(sid: String) {
        currentSid = sid
    }

    fun changedAccount(uid: String) {
        w("onAccountChanged = ${currentUID != uid}")
        if (currentUID != uid) {
            currentUID = uid
        }
        DatabaseCenter.database?.let {
            messageDao = it.messageDao()
            sessionDao = it.sessionDao()
        }
    }

//    override fun removeSession(sid: String) {
//        threadTry {
//            //remove session.
//            sessionDao.remove(sid)
//            //remove messages.
//            messageDao.removeBySid(sid)
//            //notify unread count changed.
//            AllCountManager.notifyChanged()
//        }
//
//    }

//    override fun getMessageDAO(): MessageDao = messageDao

    override fun retry(session: SESSION, message: MESSAGE) {
        threadTry {
            if (imEngine.connectState.value == ConnState.CONNECTED) {
                message.status = Constants.MESSAGE_STATUS_SENDING
                messageDao.save(message)
                if (message.type == Constants.MESSAGE_TYPE_TXT) {
                    sendingPool.add(message.sid)
                    imEngine.sendMessage(getTextMessage(session, message, ""))
                } else if (message.type == Constants.MESSAGE_TYPE_PIC) {
                    message.fileName?.let {
                        uploadImageMessage(session, message, "")
                    }
                }
            }
        }
    }

//    override fun init() {
//        DatabaseCenter.database?.let {
//            messageDao = it.messageDao()
//            sessionDao = it.sessionDao()
//        }
//    }


    override fun onMessage(messages: ArrayList<GeneratedMessageV3>) {
        val messagePool = ArrayList<MESSAGE>()
        val sessionPool = ArrayList<SESSION>()
        messages.forEach {
            when (it) {
                is BibiProtoApplication.TextMessage -> {
                    messagePool.add(it.toMessage())
                    val session = it.toSession()
                    if (sessionPool.find { it.sid == session.sid } == null) {
                        sessionPool.add(session)
                    }
                }
                is BibiProtoApplication.PicMessage -> {
                    messagePool.add(it.toMessage())
                    val session = it.toSession()
                    if (sessionPool.find { it.sid == session.sid } == null) {
                        sessionPool.add(session)
                    }
                }
                is BibiProtoApplication.CardMessage -> {
                    messagePool.add(it.toMessage())
                    val session = it.toSession()
                    if (sessionPool.find { it.sid == session.sid } == null) {
                        sessionPool.add(session)
                    }
                }
                is BibiProtoApplication.NoticeMessage -> {
                    messagePool.add(it.toMessage())
//                    val session = it.toSession()
//                    if (sessionPool.find { it.sid == session.sid } == null) {
//                        sessionPool.add(session)
//                    }
                }
            }
        }
        //save messages and sessions
        if (messagePool.isNotEmpty()) {
            messageDao.addNews(*messagePool.toTypedArray())
        }
        //update session unread & info.
        val unreadMap = HashMap<String, Int>()
        messagePool.forEach {
            //set unread count.
            if (it.sid != currentSid) {
                val v = unreadMap[it.sid] ?: 0
                unreadMap[it.sid] = v + 1
            }
        }
        w("save new session : $sessionPool")
        sessionPool.forEach {
            if (sessionDao.addNew(it.copy(unreadCount = unreadMap[it.sid] ?: 0)) < 0) {
                sessionDao.update(it.sid, unreadMap[it.sid] ?: 0, it.sessionNice, it.sessionHead
                        ?: "")
            }
        }
        AllCountManager.notifyChanged()
    }

    override fun onSendAck(data: BibiProtoApplication.MessageArrivalAck) {
        //update Message
        val msgId = data.header.messageId
        val isGreet = if (data.header.isGreet) 2 else 1
        val status = if (data.code == 11200) {
            Constants.MESSAGE_STATUS_SENT
        } else {
            Constants.MESSAGE_STATUS_FAILED
        }
        sendingPool.remove(msgId)
        //update Message
        data.cardMessage.let {
            if (it.isInitialized) {
                messageDao.update(msgId, status, isGreet, it.content, Constants.MESSAGE_TYPE_CARD)
            } else {
                messageDao.update(msgId, status, isGreet)

            }
        }
    }

//    override fun getCustomerSession(success: (SESSION?) -> Unit, error: (Int) -> Unit) {
//        ApplySessionCustomerRequest(imEngine.context).let {
//            it.req(object : RequestCallback {
//                override fun onError(request: BaseRequest?, errCode: Int) {
//                    error.invoke(errCode)
//                }
//
//                override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
//                    success.invoke(it.parseSession(result))
//                }
//            })
//        }
//    }

//    override fun getSession(targetUid: String, targetName: String, targetAvatar: String, t: (SESSION) -> Unit) {
//        // check session
//        threadTry {
//            val sid = buildSessionId(AccountManager.getInstance().account.uid, targetUid)
//            var session = sessionDao.getSESSION(sid)
//            d("getSession = $session")
//            if (session == null) {
//                session = SESSION(
//                        sid = sid,
//                        sessionNice = targetName,
//                        sessionHead = targetAvatar,
//                        sessionUid = targetUid,
//                        msgType = Constants.MESSAGE_TYPE_TXT,
//                        enableChat = 1,
//                        visableChat = 1,
//                        isGreet = 0,
//                        sessionType = 1,
//                        time = 0L,
//                        unreadCount = 0,
//                        content = "")
//                sessionDao.addNew(session)
//            }
//            t.invoke(session)
//        }
//    }

//    override fun getSession(sid: String, t: (SESSION?) -> Unit) {
//        // check session
//        threadTry {
//            t.invoke(sessionDao.getSESSION(sid))
//        }
//    }

    override fun sendText(session: SESSION, content: String, from: String) {
        threadTry {
            var fromValue = ""
            session.sessionUid?.let {
                if (UniqueFilter.check(it)) {
                    UniqueFilter.record(it)
                    fromValue = from
                }
            }
            val message = MESSAGE(
                    generateUUID(),
                    sid = session.sid,
                    sessionNick = session.sessionNice,
                    sessionHead = session.sessionHead,
                    senderUid = AccountManager.getInstance().account?.uid ?: "",
                    senderNick = AccountManager.getInstance().account?.nickName ?: "",
                    senderHead = AccountManager.getInstance().account?.headUrl ?: "",
                    sessionType = session.sessionType,
                    status = Constants.MESSAGE_STATUS_SENDING,
                    time = System.currentTimeMillis(),
                    type = Constants.MESSAGE_TYPE_TXT,
                    text = content,
                    thumb = "",
                    pic = "",
                    audio = "",
                    video = "",
                    fileName = "",
                    isGreet = 0)
            if (imEngine.connectState.value != ConnState.CONNECTED) {
                message.status = Constants.MESSAGE_STATUS_FAILED
                messageDao.addNew(message)
            } else {
                messageDao.addNew(message)
                sendingPool.add(message.sid)
                imEngine.sendMessage(getTextMessage(session, message, fromValue))
            }
        }
    }

    override fun sendImage(session: SESSION, path: String, from: String) {
        threadTry {
            val message = MESSAGE(
                    generateUUID(),
                    sid = session.sid,
                    sessionNick = session.sessionNice,
                    sessionHead = session.sessionHead,
                    senderUid = AccountManager.getInstance().account?.uid ?: "",
                    senderNick = AccountManager.getInstance().account?.nickName ?: "",
                    senderHead = AccountManager.getInstance().account?.headUrl ?: "",
                    sessionType = session.sessionType,
                    status = Constants.MESSAGE_STATUS_SENDING,
                    time = System.currentTimeMillis(),
                    type = Constants.MESSAGE_TYPE_PIC,
                    text = "",
                    thumb = "",
                    pic = "",
                    audio = "",
                    video = "",
                    fileName = path,
                    isGreet = 0)
            uploadImageMessage(session, message, from)
        }
    }

    private fun uploadImageMessage(session: SESSION, message: MESSAGE, from: String) {
        var fromValue = ""
        session.sessionUid?.let {
            if (UniqueFilter.check(it)) {
                UniqueFilter.record(it)
                fromValue = from
            }
        }
        if (imEngine.connectState.value != ConnState.CONNECTED) {
            message.status = Constants.MESSAGE_STATUS_FAILED
            messageDao.addNew(message)
            return
        }
        messageDao.addNew(message)
        //upload image
        threadTry {
            messageDao.addNew(message)
            if (File(message.fileName).exists()) {
                val mainExt = WeLikeFileManager.parseTmpFileSuffix(message.fileName)
                UploadingManager.getInstance().upload(null, message.fileName, mainExt, UploadingManager.UPLOAD_TYPE_IMG, object : UploadListener {
                    override fun onFinish(url: String?) {
                        w("onMessageUploadingTaskEnd")
                        threadTry {
                            message.pic = url ?: ""
                            messageDao.save(message)
                            sendingPool.add(message.sid)
                            imEngine.sendMessage(getPicMessage(session, message, fromValue))
                        }
                    }

                    override fun onFail() {
                        threadTry {
                            message.status = Constants.MESSAGE_STATUS_FAILED
                            messageDao.save(message)
                        }
                    }
                })
            }
        }
    }

    /**
     * translate MESSAGE to TextMessage
     */
    private fun getTextMessage(session: SESSION, message: MESSAGE, from: String): BibiProtoApplication.TextMessage {
        d("getTextMessage")
        return BibiProtoApplication.TextMessage.newBuilder()
                .setText(message.text)
                .setHeader(getHeader(session, message, from, BibiProtoApplication.MessageHeader.MessageType.Text, imEngine.appType))
                .build()
    }

    /**
     * translate MESSAGE to PicMessage
     */
    private fun getPicMessage(session: SESSION, message: MESSAGE, from: String): BibiProtoApplication.PicMessage {
        d("getPicMessage")
        return BibiProtoApplication.PicMessage.newBuilder()
                .setPicUri(message.pic)
                .setCoverUri(message.thumb)
                .setHeader(getHeader(session, message, from, BibiProtoApplication.MessageHeader.MessageType.Pic, imEngine.appType))
                .build()
    }

    /**
     * Pack MessageHeader
     */
    private fun getHeader(session: SESSION, message: MESSAGE, from: String, headerType: BibiProtoApplication.MessageHeader.MessageType, aType: Int): BibiProtoApplication.MessageHeader {
        return BibiProtoApplication.MessageHeader.newBuilder().apply {
            messageId = message.mid
            classified = BibiProtoApplication.MessageClassified.P2P
            fromUid = message.senderUid
            remoteUid = session.sessionUid
            groupName = message.senderNick
            senderName = message.senderNick
            createtime = message.time
            type = headerType
            persistent = true
            appType = BibiProtocol.AppType.forNumber(aType)
//            session.sessionHead?.let {
//                if (it.isNotEmpty()) {
//                    groupUrl = it
//                }
//            }
            message.senderHead?.let {
                if (it.isNotEmpty()) {
                    senderUrl = it
                    groupUrl = it
                }
            }

            if (from.isNotEmpty()) {
                addProperties(BibiProtoApplication.MessageHeader.Property.newBuilder().setKey("noticeType").setValue("shake"))
            }
        }.build()
    }

    override fun onDisconnect() {
        //mark all message in sending pool to fail.
        sendingPool.forEach {
            messageDao.update(it, Constants.MESSAGE_STATUS_FAILED)
        }
    }

    override fun onKickout() {
        EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_LOGOUT_EVENT, null))
    }
}