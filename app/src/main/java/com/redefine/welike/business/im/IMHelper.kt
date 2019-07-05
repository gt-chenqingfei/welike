package com.redefine.welike.business.im

import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.redefine.im.*
import com.redefine.im.engine.ApplySessionCustomerRequest
import com.redefine.im.engine.IMEngine
import com.redefine.im.room.*
import com.redefine.welike.MyApplication
import com.redefine.welike.base.LanguageSupportManager
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.business.im.model.CardInfo
import com.redefine.welike.common.Life

object IMHelper {
    private lateinit var sessionDao: SessionDao
    private lateinit var messageDao: MessageDao

    val engine: IMEngine by lazy { IMEngine.getInstance(MyApplication.getApp()) }
    val wrapper: EngineWrapper by lazy { EngineWrapper(engine) }
    /**
     * 初始化 IM 全部
     */
    fun init() {
        Life.regListener { event ->
            when (event) {
                Life.LIFE_LOGIN -> {
                    d("IMHelper login")
                    threadTry {
                        reset()
                        startIM(engine)
                        AllCountManager.notifyChanged()
                    }
                }
                Life.LIFE_LOGOUT -> {
                    d("IMHelper logout")
                    threadTry { engine.stop() }
                }
            }
        }
        threadTry {
            reset()
            engine.init(wrapper, wrapper) {
                //try to start im engine.
                startIM(engine)
            }
        }
    }


    /**
     * 删除 Session
     */
    fun removeSession(sid: String) {
        threadTry {
            //remove session.
            sessionDao.remove(sid)
            //remove messages.
            messageDao.removeBySid(sid)
            //notify unread count changed.
            AllCountManager.notifyChanged()
        }

    }

    /**
     * 设置 当前的 session
     */
    fun setCurrentSession(sid: String) {
        wrapper.setCurrentSession(sid)
        if (sid.isNotEmpty()) {
            threadTry {
                sessionDao.setRead(sid)
                AllCountManager.notifyChanged()
            }
        }
    }

    fun retry(session: SESSION, message: MESSAGE) = wrapper.retry(session, message)

    fun sendText(session: SESSION, content: String, from: String) = wrapper.sendText(session, content, from)

    fun sendImage(session: SESSION, path: String, from: String) = wrapper.sendImage(session, path, from)

    /**
     * 进入客服
     */
    fun getCustomerSession(success: (SESSION?) -> Unit, error: (Int) -> Unit) {
        ApplySessionCustomerRequest(MyApplication.getAppContext()).let {
            it.req(object : RequestCallback {
                override fun onError(request: BaseRequest?, errCode: Int) {
                    error.invoke(errCode)
                }

                override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                    it.parseSession(result).let {
                        if (it == null) {
                            error.invoke(0)
                        } else {
                            success.invoke(it)
                        }
                    }
                }
            })
        }
    }

    /**
     * 获取session，从 DB 获取，如果没有就创建&存入 DB
     */
    fun getSession(targetUid: String, targetName: String, targetAvatar: String, t: (SESSION) -> Unit) {
        // check session
        threadTry {
            val sid = buildSessionId(AccountManager.getInstance().account?.uid?:"", targetUid)
            var session = sessionDao.getSESSION(sid)
            d("getSession = $session")
            if (session == null) {
                session = SESSION(
                        sid = sid,
                        sessionNice = targetName,
                        sessionHead = targetAvatar,
                        sessionUid = targetUid,
                        msgType = Constants.MESSAGE_TYPE_TXT,
                        enableChat = 1,
                        visableChat = 1,
                        isGreet = 0,
                        sessionType = 1,
                        time = 0L,
                        unreadCount = 0,
                        content = "")
                sessionDao.addNew(session)
            }
            t.invoke(session)
        }
    }

    fun getSession(sid: String, t: (SESSION?) -> Unit) {
        // check session
        threadTry {
            t.invoke(sessionDao.getSESSION(sid))
        }
    }

    fun parser(jsonString: String): CardInfo = Gson().fromJson(jsonString, CardInfo::class.java)

    // All private functions.
    private fun startIM(engine: IMEngine) {
        d("IMHelper auto start")
        AccountManager.getInstance().account?.let {
            val uid = it.uid
            val token = it.accessToken
            wrapper.changedAccount(uid)
            if (!uid.isNullOrEmpty() && !token.isNullOrEmpty()) {
                val language = LanguageSupportManager.getInstance().currentMenuLanguageType
                language?.let { engine.start(uid, token, it) }

            }
        }
    }

    private fun reset() {
        var uid = ""
        AccountManager.getInstance().account?.let { uid = it.uid }
        //reset db
        DatabaseCenter.init(MyApplication.getAppContext(), uid)

        wrapper.changedAccount(uid)
        CountManager.init()
        DatabaseCenter.database?.let {
            messageDao = it.messageDao()
            sessionDao = it.sessionDao()
        }
    }

    /**
     * 获取 指定session id下的 最后一条成功的message
     */
    fun getMissingStatus(ids: List<String>, callback: () -> Unit) {
        threadTry {
            val result = messageDao.getLastSuccess(ids, listOf(Constants.MESSAGE_STATUS_SENT, Constants.MESSAGE_STATUS_RECEIVED))
            w("getMissingStatus = $result ")
            result?.forEach {

            }
        }
    }

}