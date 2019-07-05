package com.redefine.im.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

/**
 * Created by daining on 2018/5/5.
 */
@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addNew(message: MESSAGE)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addNews(vararg message: MESSAGE)

    @Delete
    fun remove(message: MESSAGE)

    @Update
    fun save(message: MESSAGE)

    @Query("SELECT * FROM MESSAGE WHERE MID = :mid")
    fun getMessage(mid: String): MESSAGE

    @Query("SELECT * FROM MESSAGE")
    fun getAll(): LiveData<List<MESSAGE>>

    @Query("SELECT MESSAGE.* , SESSION.SID as sSid, SESSION.SESSION_NICK as sName, SESSION.SESSION_HEAD as sHead , SESSION.UNREAD_COUNT as sUnread, SESSION.IS_GREET as sIsGreet FROM MESSAGE,SESSION WHERE MESSAGE.SID = SESSION.SID AND MESSAGE.TYPE <> 6 GROUP BY MESSAGE.SID ORDER BY MESSAGE.TIME DESC")
    fun getSessions(): LiveData<List<MessageSession>>

    @Query("SELECT * FROM MESSAGE WHERE SID = :sid")
    fun getMessages(sid: String): LiveData<List<MESSAGE>>

    @Query("UPDATE MESSAGE SET STATUS = :status , IS_GREET = :greet , TEXT = :content , TYPE = :type WHERE mid = :msgId")
    fun update(msgId: String, status: Int, greet: Int, content: String, type: Int)

    @Query("UPDATE MESSAGE SET STATUS = :status , IS_GREET = :greet WHERE mid = :msgId")
    fun update(msgId: String, status: Int, greet: Int)

    @Query("UPDATE MESSAGE SET STATUS = :status WHERE mid = :msgId")
    fun update(msgId: String, status: Int)

    @Query("SELECT * FROM MESSAGE")
    fun test(): MESSAGE

    @Query("DELETE FROM MESSAGE WHERE SID = :sid")
    fun removeBySid(sid: String)

    @Query("SELECT * FROM MESSAGE WHERE SID IN (:ids) AND STATUS IN (:status) ORDER BY TIME DESC")
    fun getLastSuccess(ids: List<String>, status: List<Int>): List<MESSAGE>
}

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addNew(session: SESSION): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addNews(vararg session: SESSION)

    @Delete
    fun remove(session: SESSION)

    @Query("DELETE FROM SESSION WHERE SID = :sid")
    fun remove(sid: String)

    @Query("SELECT * FROM SESSION WHERE SID = :sid")
    fun getSESSION(sid: String): SESSION

    @Update
    fun save(session: SESSION)

    @Query("SELECT * FROM SESSION")
    fun getAll(): List<SESSION>

    @Query("SELECT * FROM SESSION")
    fun getLiveAll(): LiveData<List<SESSION>>

    @Query("UPDATE SESSION SET IS_GREET = :isGreet WHERE SESSION.SID = :sid")
    fun update(sid: String, isGreet: Int)

    @Query("UPDATE SESSION SET UNREAD_COUNT = UNREAD_COUNT + :unread , SESSION_NICK = :name , SESSION_HEAD = :avatar WHERE SESSION.SID = :sid")
    fun update(sid: String, unread: Int, name: String, avatar: String)

    @Query("UPDATE SESSION SET UNREAD_COUNT = 0 WHERE SESSION.SID = :sid")
    fun setRead(sid: String)

    @Query("SELECT SUM(UNREAD_COUNT) as total FROM SESSION")
    fun getUnreadCount(): MessageCount
}

@Dao
interface ASDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addNew(accountSetting: AccountSetting): Long

    @Query("SELECT * FROM ACCOUNT_SETTING")
    fun getSetting(): AccountSetting

    @Query("SELECT * FROM ACCOUNT_SETTING")
    fun getLiveSetting(): LiveData<AccountSetting>

    @Update
    fun save(setting: AccountSetting)

    @Query("UPDATE ACCOUNT_SETTING SET LIKE_MSG_UN_READ_COUNT = :count")
    fun setLikeCount(count: Int)

    @Query("UPDATE ACCOUNT_SETTING SET MENTION_MSG_UN_READ_COUNT = :count")
    fun setMentionCount(count: Int)

    @Query("UPDATE ACCOUNT_SETTING SET COMMENT_MSG_UN_READ_COUNT = :count")
    fun setCommnetCount(count: Int)

  /*  @Query("UPDATE ACCOUNT_SETTING SET PUSH_MSG_UN_READ_COUNT = :count")
    fun setPushCount(count: Int)*/
}