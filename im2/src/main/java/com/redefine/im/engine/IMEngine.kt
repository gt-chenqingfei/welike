package com.redefine.im.engine

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.os.Build
import android.util.Log.i
import com.google.protobuf.GeneratedMessageV3
import com.redefine.foundation.utils.CommonHelper
import com.redefine.im.*
import com.redefine.im.remoting.protocol.ImPacketType
import com.redefine.im.service.socket.protocol.BibiProtoApplication
import com.redefine.im.service.socket.protocol.BibiProtocol
import com.redefine.welike.base.URLCenter
import com.redefine.welike.base.profile.AccountManager
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread


/**
 * Keep Connection. Create, Retry, Send Data.
 * Created by daining on 2018/5/5.
 */
class IMEngine(val context: Application) {

    companion object {

        @Volatile
        private var INSTANCE: IMEngine? = null

        fun getInstance(app: Application): IMEngine = INSTANCE
                ?: IMEngine(app).also { INSTANCE = it }

    }

    fun init(_entrance: IEngine, _listener: EngineListener, after: () -> Unit) {
        thread {
            var uid = ""
            AccountManager.getInstance().account?.uid?.let { uid = it }
//            DatabaseCenter.init(context, uid)
            w("ENGINE INIT uid=$uid")
            entrance = _entrance
            listener = _listener
//            listener.init()
            after.invoke()
        }
    }

    /**
     * 当前连接状态。
     */
    val connectState = MutableLiveData<ConnState>().also { it.postValue(ConnState.NONE) }

    private var imClient: IMClient? = null
    private var uid: String? = null
    private var token: String? = null
    private var language: String? = null

    private var isStop = false

    private var checkThread: CheckThread? = null

    lateinit var entrance: IEngine
    lateinit var listener: EngineListener

    val appType by lazy { CommonHelper.getAppType(context) }
    //    var countListener: CountChangedListener? = null
//    var kickOutListener: KickOutListener? = null

    fun start(_uid: String, _token: String, _language: String) {
        d("engine. start : uid=$_uid  token=$_token  language=$_language")
//        if (!uid.equals(_uid)) { //如果 账号切换，切换DB
//            var uid = ""
//            AccountManager.getInstance().account?.uid?.let { uid = it }
//            DatabaseCenter.init(context, _uid)
//            listener.onAccountChanged(_uid)
//        }
        uid = _uid
        token = _token
        language = _language
        isStop = false
        checkThread?.stop = true
        checkThread = CheckThread { tryConnect() }.also { it.start() }
    }


    fun stop() {
        d("stop")
        cleanSync()
        isStop = true
        checkThread?.stop = true
        imClient?.disconnect()
    }

    //内部连接函数
    private fun tryConnect() {
        try {
            i("DDAI", "tryConnect = ${(!isStop && connectState.value == ConnState.NONE)} @isStop=$isStop @connectState.value=${connectState.value}")
            if (!isStop && (connectState.value == null || connectState.value == ConnState.NONE)) {
                initClient()
                imClient?.connect()// 此方法会阻塞
                conn()//发送连接信息
            }
        } catch (e: Exception) {
            e.printStackTrace()
            connectState.postValue(ConnState.NONE)
        }
    }

    class CheckThread(val wrok: () -> Unit) : Thread() {
        val interval = 10L

        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var stop = false

        fun retry() {
            try {
                lock.lock()
                condition.signal()
            } finally {
                lock.unlock()
            }
        }

        override fun run() {
            try {
                lock.lock()
                while (!stop) {
                    wrok.invoke()
                    condition.await(interval, TimeUnit.SECONDS)
                }
            } finally {
                lock.unlock()
            }
        }
    }

    /**
     * 处理协议级别，相关逻辑。分发业务逻辑到外面处理。
     */
    private val engineProtocol: EngineProtocol by lazy {
        object : EngineProtocol {
            override fun syncAck(data: BibiProtoApplication.SyncDataPacket) {
                val lv = data.syncMarksList.firstOrNull {
                    it.classified == BibiProtoApplication.MessageClassified.P2P
                }?.lastVersion
                //notify server.
                finishSync(lv ?: 0)
                dispatchMsg(data)
                if (data.hasMore) {
                    addSync()
                }
            }

            override fun notifyNew(data: BibiProtoApplication.NewMessageNotify) {
                var notifySync = false
                var notifyEvent = false
                data.classifiedList.forEach {
                    if (it == BibiProtoApplication.MessageClassified.P2P) {
                        notifySync = true
                    } else if (it == BibiProtoApplication.MessageClassified.NEWS) {
                        notifyEvent = true
                    } else if (it == BibiProtoApplication.MessageClassified.All) {
                        notifySync = true
                        notifyEvent = true
                    }
                }
                if (notifySync) {
                    addSync()
                }
                if (notifyEvent) {
                    refreshEventCount()
                }
            }

            override fun connAck() {
                connectState.postValue(ConnState.CONNECTED)
                addSync()
            }

            override fun msgAck(data: BibiProtoApplication.MessageArrivalAck) {
                dispatchMsgAck(data)
            }

            override fun disconnect() {
                e("engineProtocol.disconnect")
                connectState.postValue(ConnState.NONE)
                cleanSync()
                imClient?.disconnect()
                listener.onDisconnect()
            }

            override fun timeout() {
                e("engineProtocol.timeout")
                connectState.postValue(ConnState.NONE)
                cleanSync()
                imClient?.disconnect()
                listener.onDisconnect()
            }

            override fun kickOut() {
                e("engineProtocol.kickOut")
                connectState.postValue(ConnState.NONE)
                stop()
                listener.onKickout()
//                kickOutListener?.onIMTokenInvalid()
            }

        }
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun initClient() {
        d("init")
        if (imClient == null) {
            imClient = IMClient(URLCenter.getHostIM(), 8200, engineProtocol).also { it.init() }
        }
    }

    private fun conn() {
        d("conn")
        connectState.postValue(ConnState.CONNECTING)
        val packet = BibiProtocol.ConnMeta.newBuilder()
                .setUid(uid)
                .setDeviceType(BibiProtocol.ConnMeta.DeviceType.Android)
                .setDeviceInfo(Build.MODEL)
                .setSessionId(token)
                .setAppType(BibiProtocol.AppType.forNumber(appType))
                .setVersion(context.getAppVersion()).build()
                .getImPacket(ImPacketType.PROTOCOL_CONN)
        imClient!!.channel!!.writeAndFlush(packet)
    }

    private var syncing = false //正在syncing
    private var waiting = false

    private fun cleanSync() {
        syncing = false
        waiting = false
    }

    private fun addSync() {
        if (syncing) {
            waiting = true
        } else {
            syncing = true
            sync()
        }
    }

    private fun sync() {
        d("sync")
//        val classifieds = ArrayList()
//        classifieds.add(BibiProtoApplication.MessageClassified.P2P)
        val packet = BibiProtoApplication.SyncPacket.newBuilder()
                .addAllClassified(listOf(BibiProtoApplication.MessageClassified.P2P))
                .setFromUid(uid)
                .setSeqId(1L)
                .setAppType(BibiProtocol.AppType.forNumber(appType))
                .build()
                .getImPacket(ImPacketType.PROTOCOL_SYNC)

//        val packet = BibiProtocol.ConnMeta.newBuilder()
//                .setUid(uid)
//                .setDeviceType(BibiProtocol.ConnMeta.DeviceType.Android)
//                .setDeviceInfo(Build.MODEL)
//                .setSessionId(token)
//                .setVersion(context.getAppVersion()).build()
//                .getImPacket(ImPacketType.PROTOCOL_SYNC)
        imClient?.channel?.writeAndFlush(packet)
    }

    private fun finishSync(lv: Long) {
        d("finishSync")
        val mark = BibiProtoApplication.SyncDataPacket.SyncMark.newBuilder()
                .setClassified(BibiProtoApplication.MessageClassified.P2P)
                .setAppType(BibiProtocol.AppType.forNumber(appType))
                .setLastVersion(lv)
                .build()
        val packet = BibiProtoApplication.SyncDataFin.newBuilder()
                .addSyncMarks(mark).build()
                .getImPacket(ImPacketType.PROTOCOL_FIN)
        imClient?.channel?.writeAndFlush(packet)
    }

    fun sendMessage(message: BibiProtoApplication.TextMessage) {
        d("sendMessage")
        val packet = message.getImPacket(ImPacketType.PROTOCOL_MSG_TEXT)
        imClient?.channel?.writeAndFlush(packet)
    }

    fun sendMessage(message: BibiProtoApplication.PicMessage) {
        d("sendMessage")
        val packet = message.getImPacket(ImPacketType.PROTOCOL_MSG_PIC)
        imClient?.channel?.writeAndFlush(packet)
    }

    private fun dispatchMsgAck(data: BibiProtoApplication.MessageArrivalAck) {
        d("dispatchMsgAck")
        listener.onSendAck(data)
    }

    private fun dispatchMsg(data: BibiProtoApplication.SyncDataPacket) {
        d("dispatchMsg")
        val pool = ArrayList<GeneratedMessageV3>()
        for (entry in data.dataList) {
            if (entry.type.compareTo(ImPacketType.PROTOCOL_MSG_TEXT) == 0) {
                try {
                    val textMsg = BibiProtoApplication.TextMessage.parseFrom(entry.body)
                    pool.add(textMsg)
                    w("dispatchMsg  MSG_TEXT :[${showLog(textMsg)}]")
                } catch (e: Exception) {
                }

            }
            if (entry.type.compareTo(ImPacketType.PROTOCOL_MSG_CARD) == 0) {
                try {
                    val textMsg = BibiProtoApplication.CardMessage.parseFrom(entry.body)
                    pool.add(textMsg)
                    w("dispatchMsg  MSG_TEXT :[${showLog(textMsg)}]")
                } catch (e: Exception) {
                }

            }
//            if (entry.type.compareTo(ImPacketType.PROTOCOL_MSG_NEWS) == 0) {
//                try {
//                    val textMsg = BibiProtoApplication.News.parseFrom(entry.body)
//                    pool.add(textMsg)
//                    w("dispatchMsg  PROTOCOL_MSG_NEWS :[${showLog(textMsg)}]")
//                } catch (e: Exception) {
//                }
//
//            }
            if (entry.type.compareTo(ImPacketType.PROTOCOL_MSG_PIC) == 0) {
                try {
                    val textMsg = BibiProtoApplication.PicMessage.parseFrom(entry.body)
                    pool.add(textMsg)
                    w("dispatchMsg  PROTOCOL_MSG_PIC :[${showLog(textMsg)}]")
                } catch (e: Exception) {
                }

            }
            if (entry.type.compareTo(ImPacketType.PROTOCOL_MSG_NOTICE) == 0) {
                try {
                    val textMsg = BibiProtoApplication.NoticeMessage.parseFrom(entry.body)
                    pool.add(textMsg)
                    w("dispatchMsg  PROTOCOL_MSG_NOTICE :[${showLog(textMsg)}]")
                } catch (e: Exception) {
                }

            }
        }
        listener.onMessage(pool)
        syncing = false
        if (waiting) {
            waiting = false
            addSync()
        }
    }

    private fun refreshEventCount() {
        d("refreshEventCount")
        listener.onEvent()
    }

}

/**
 * state of connection.
 */
enum class ConnState {
    NONE, CONNECTING, CONNECTED
}