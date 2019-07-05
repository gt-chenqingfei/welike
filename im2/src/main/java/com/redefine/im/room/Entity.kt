package com.redefine.im.room

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by daining on 2018/5/5.
 */
//@Entity

//accountSetting.addStringProperty("uid").unique().primaryKey();
//accountSetting.addStringProperty("imMessageCursor");
//accountSetting.addIntProperty("mentionMsgUnReadCount");
//accountSetting.addIntProperty("commentMsgUnReadCount");
//accountSetting.addIntProperty("likeMsgUnReadCount");


@Entity(tableName = "ACCOUNT_SETTING")
data class AccountSetting(
        @PrimaryKey
        @ColumnInfo(name = "UID")
        var uid: String,

        @ColumnInfo(name = "IM_MESSAGE_CURSOR")
        var imCursor: String?,

        @ColumnInfo(name = "MENTION_MSG_UN_READ_COUNT")
        var mentionCount: Int?,

        @ColumnInfo(name = "COMMENT_MSG_UN_READ_COUNT")
        var commentCount: Int?,

        @ColumnInfo(name = "LIKE_MSG_UN_READ_COUNT")
        var likeCount: Int?

     /*   @ColumnInfo(name = "PUSH_MSG_UN_READ_COUNT")
        var pushCount: Int?*/


)

@Entity
data class MESSAGE(
        @PrimaryKey
        @ColumnInfo(name = "MID")
        var mid: String,

        @ColumnInfo(name = "SID")
        var sid: String,

        @ColumnInfo(name = "SESSION_NICK")
        var sessionNick: String,

        @ColumnInfo(name = "SESSION_HEAD")
        var sessionHead: String?,

        @ColumnInfo(name = "SENDER_UID")
        var senderUid: String,

        @ColumnInfo(name = "SENDER_NICK")
        var senderNick: String,

        @ColumnInfo(name = "SENDER_HEAD")
        var senderHead: String?,

        @ColumnInfo(name = "SESSION_TYPE")
        var sessionType: Int,

        @ColumnInfo(name = "STATUS")
        var status: Int,

        @ColumnInfo(name = "TIME")
        var time: Long,

        @ColumnInfo(name = "TYPE")
        var type: Int,

        @ColumnInfo(name = "TEXT")
        var text: String?,

        @ColumnInfo(name = "THUMB")
        var thumb: String?,

        @ColumnInfo(name = "PIC")
        var pic: String?,

        @ColumnInfo(name = "AUDIO")
        var audio: String?,

        @ColumnInfo(name = "VIDEO")
        var video: String?,

        @ColumnInfo(name = "FILE_NAME")
        var fileName: String?,

        @ColumnInfo(name = "IS_GREET")
        var isGreet: Int? // true=2, false =1, none = 0
)


@SuppressLint("ParcelCreator")
@Parcelize
@Entity
data class SESSION(
        @PrimaryKey
        @ColumnInfo(name = "SID")
        var sid: String,

        @ColumnInfo(name = "SESSION_NICK")
        var sessionNice: String,

        @ColumnInfo(name = "SESSION_HEAD")
        var sessionHead: String?,

        @ColumnInfo(name = "SINGLE_UID")
        var sessionUid: String?,

        @ColumnInfo(name = "MSG_TYPE")
        var msgType: Int?,

        @ColumnInfo(name = "ENABLE_CHAT")
        var enableChat: Int?,

        @ColumnInfo(name = "VISABLE_CHAT")
        var visableChat: Int?,

        @ColumnInfo(name = "IS_GREET")
        var isGreet: Int?,

        @ColumnInfo(name = "SESSION_TYPE")
        var sessionType: Int,

        @ColumnInfo(name = "TIME")
        var time: Long,

        @ColumnInfo(name = "UNREAD_COUNT")
        var unreadCount: Int,

        @ColumnInfo(name = "CONTENT")
        var content: String?

) : Parcelable

fun MESSAGE.clone() = MESSAGE(mid, sid, sessionNick, sessionHead, senderUid, senderNick, senderHead, sessionType, status, time, type, text, thumb, pic, audio, video, fileName, isGreet)

data class MessageSession(
        @Embedded
        val message: MESSAGE,

        val sSid: String,
        val sName: String?,
        val sHead: String?,
        val sUnread: Int,
        val sIsGreet: Int
)

data class MessageCount(
        val total: Int
)

