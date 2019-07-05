package com.redefine.im

import android.content.Context
import android.util.Log
import com.google.protobuf.GeneratedMessageV3
import com.redefine.im.remoting.PacketWrapper
import com.redefine.im.remoting.protocol.ImPacket
import com.redefine.im.room.MESSAGE
import com.redefine.im.room.SESSION
import com.redefine.im.service.socket.protocol.BibiProtoApplication
import io.netty.buffer.Unpooled
import io.reactivex.android.schedulers.AndroidSchedulers
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

fun d(content: String, tag: String = "DDAI") {
    if (BuildConfig.DEBUG) {
        Log.d(tag, content)
    }
}

fun w(content: String, tag: String = "DDAI") {
    if (BuildConfig.DEBUG) {
        Log.w(tag, content)
    }
}

fun e(content: String, tag: String = "DDAI") {
    if (BuildConfig.DEBUG) {
        Log.e(tag, content)
    }
}

fun Context.getAppVersion(): Int {
    try {
        val info = packageManager.getPackageInfo(packageName, 0)
        return info.versionCode
    } catch (e: Exception) {
    }
    return 1
}

fun threadTry(t: () -> Unit) {
    thread {
        try {
            t.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun threadUITry(t: () -> Unit) {
    AndroidSchedulers.mainThread().scheduleDirect {
        try {
            t.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun threadUIDelay(time: Long, t: () -> Unit) {
    AndroidSchedulers.mainThread().scheduleDirect({
        try {
            t.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, time, TimeUnit.MILLISECONDS)
}

fun SocketChannel.readTo(dataBuffer: ArrayList<ByteArray>, bufferSize: Int = 1024) {
    val buffer = ByteBuffer.allocate(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        dataBuffer.add(Arrays.copyOfRange(buffer.array(), 0, bytes))
        bytes = read(buffer)
    }
}

fun GeneratedMessageV3.getImPacket(_type: Short): ImPacket {
    return ImPacket().apply {
        type = _type
        version = PacketWrapper.PROTOCOL_VERSION
        data = Unpooled.wrappedBuffer(toByteArray())
    }
}

fun BibiProtoApplication.TextMessage.toMessage(): MESSAGE {
    return MESSAGE(
            header.messageId,
            buildSessionId(header.fromUid, header.remoteUid),
            "",
            "",
            header.fromUid,
            header.senderName,
            header.senderUrl,
            1,
            Constants.MESSAGE_STATUS_RECEIVED,
            header.createtime,
            Constants.MESSAGE_TYPE_TXT,
            text,
            "",
            "",
            "",
            "",
            "",
            if (header.isGreet) 2 else 1)
}

fun BibiProtoApplication.TextMessage.toSession(): SESSION {
    return SESSION(
            buildSessionId(header.fromUid, header.remoteUid),
            header.groupName,
            header.groupUrl,
            header.fromUid,
            1,
            if (header.enableChat) 1 else 0,
            if (header.visableChat) 1 else 0,
            0,
            1,
            0L,
            0,
            "")
}

fun BibiProtoApplication.CardMessage.toMessage(): MESSAGE {
    return MESSAGE(
            header.messageId,
            buildSessionId(header.fromUid, header.remoteUid),
            "",
            "",
            header.fromUid,
            header.senderName,
            header.senderUrl,
            1,
            Constants.MESSAGE_STATUS_RECEIVED,
            header.createtime,
            Constants.MESSAGE_TYPE_CARD,
            content,
            "",
            "",
            "",
            "",
            "",
            if (header.isGreet) 2 else 1)
}

fun BibiProtoApplication.CardMessage.toSession(): SESSION {
    return SESSION(
            buildSessionId(header.fromUid, header.remoteUid),
            header.groupName,
            header.groupUrl,
            header.fromUid,
            1,
            if (header.enableChat) 1 else 0,
            if (header.visableChat) 1 else 0,
            0,
            1,
            0L,
            0,
            "")
}

fun BibiProtoApplication.PicMessage.toMessage(): MESSAGE {
    return MESSAGE(
            header.messageId,
            buildSessionId(header.fromUid, header.remoteUid),
            "",
            "",
            header.fromUid,
            header.senderName,
            header.senderUrl,
            1,
            Constants.MESSAGE_STATUS_RECEIVED,
            header.createtime,
            Constants.MESSAGE_TYPE_PIC,
            "",
            coverUri,
            picUri,
            "",
            "",
            "",
            if (header.isGreet) 2 else 1)
}

fun BibiProtoApplication.PicMessage.toSession(): SESSION {
    return SESSION(
            buildSessionId(header.fromUid, header.remoteUid),
            header.groupName,
            header.groupUrl,
            header.fromUid,
            1,
            if (header.enableChat) 1 else 0,
            if (header.visableChat) 1 else 0,
            0,
            1,
            0L,
            0,
            "")
}

fun BibiProtoApplication.NoticeMessage.toMessage(): MESSAGE {
    val content = if (actionsCount > 0) {
        getActions(0).text
    } else {
        ""
    }
    return MESSAGE(
            header.messageId,
            buildSessionId(header.fromUid, header.remoteUid),
            "",
            "",
            header.fromUid,
            header.senderName,
            header.senderUrl,
            1,
            Constants.MESSAGE_STATUS_RECEIVED,
            header.createtime,
            Constants.MESSAGE_TYPE_SYSTEM,
            content,
            "",
            "",
            "",
            "",
            "",
            if (header.isGreet) 2 else 1)
}

fun BibiProtoApplication.NoticeMessage.toSession(): SESSION {
    return SESSION(
            buildSessionId(header.fromUid, header.remoteUid),
            header.groupName,
            header.groupUrl,
            header.fromUid,
            1,
            if (header.enableChat) 1 else 0,
            if (header.visableChat) 1 else 0,
            0,
            1,
            0L,
            0,
            "")
}

fun buildSessionId(uid1: String, uid2: String): String {
    val uid1Int = Integer.parseInt(uid1)
    val uid2Int = Integer.parseInt(uid2)
    return if (uid1Int < uid2Int) {
        "$uid1-_-!!!$uid2"
    } else {
        "$uid2-_-!!!$uid1"
    }
}

fun generateUUID(): String {
    return UUID.randomUUID().toString().replace("-", "")
}
