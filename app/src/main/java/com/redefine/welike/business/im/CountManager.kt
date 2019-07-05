package com.redefine.welike.business.im

import com.alibaba.fastjson.JSONObject
import com.redefine.im.room.ASDao
import com.redefine.im.room.AccountSetting
import com.redefine.im.room.DatabaseCenter
import com.redefine.im.room.SessionDao
import com.redefine.im.threadTry
import com.redefine.im.w
import com.redefine.welike.MyApplication
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.business.message.management.bean.NotificationCount
import com.redefine.welike.business.message.management.request.NotificationsCountRequest

object CountManager {

    private var asDAO: ASDao? = null
    private var sessionDao: SessionDao? = null

    fun init() {
        asDAO = DatabaseCenter.database?.accountSettingDao()
        sessionDao = DatabaseCenter.database?.sessionDao()
        asDAO?.getSetting().let {
            if (it == null) {
                w("CountManager.INIT add new AccountSetting")
                var uid = ""
                AccountManager.getInstance().account?.uid?.let { uid = it }
                asDAO?.addNew(AccountSetting(uid, "", 0, 0, 0))
            }
        }
    }

    /**
     * 获取 Like 数量
     */
    fun getLikeCount(): Int {
        var result = 0
        asDAO?.getSetting()?.likeCount?.let { result = it }
        return result
    }

    fun getMentionCount(): Int {
        var result = 0
        asDAO?.getSetting()?.mentionCount?.let { result = it }
        return result
    }

    fun getCommnetCount(): Int {
        var result = 0
        asDAO?.getSetting()?.commentCount?.let { result = it }
        return result
    }

  /*  fun getPushCount(): Int {
        var result = 0
        asDAO?.getSetting()?.pushCount?.let { result = it }
        return result
    }*/

    fun getEventCounts(): AccountSetting? {
        return asDAO?.getSetting()
    }

   /* fun setPushCount(value: Int) {
        asDAO?.setPushCount(value)
        AllCountManager.notifyChanged()
    }*/

    fun setLikeCount(value: Int) {
        asDAO?.setLikeCount(value)
        AllCountManager.notifyChanged()
    }

    fun setCommentCount(value: Int) {
        asDAO?.setCommnetCount(value)
        AllCountManager.notifyChanged()
    }

    fun setMentionCount(value: Int) {
        asDAO?.setMentionCount(value)
        AllCountManager.notifyChanged()
    }

    fun getMessageUnreadCount(): Int {
        var result = 0
        sessionDao?.getUnreadCount()?.let { result = it.total }
        return result
    }

//    fun setLikeCount(value: Int, after: (() -> Unit)? = null) {
//
//    }
//
//    fun setMentionCount(value: Int, after: (() -> Unit)? = null) {
//        asDAO?.setMentionCount(value);after?.invoke()
//    }
//
//    fun setCommnetCount(value: Int, after: (() -> Unit)? = null) {
//        asDAO?.setCommnetCount(value);after?.invoke()
//    }

    private fun saveCount(mention: Int, comment: Int, like: Int,push:Int) {
        asDAO?.let { dao ->
            dao.getSetting().let {
                it.mentionCount = mention
                it.commentCount = comment
                it.likeCount = like
               // it.pushCount=push
                dao.save(it)
            }
        }
    }

    fun reload() {
        AccountManager.getInstance().account?.let {
            NotificationsCountRequest(MyApplication.getAppContext(), it.uid).req(object : RequestCallback {
                override fun onError(request: BaseRequest?, errCode: Int) {

                }

                override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                    threadTry {
                        val counts = NotificationCount.parse(result)
                        saveCount(counts.mention, counts.comment, counts.like,0)
                        AllCountManager.notifyChanged()
                    }
                }

            })
        }
    }
}