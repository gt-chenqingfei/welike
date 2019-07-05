package com.redefine.welike.business.contact.view

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.redefine.commonui.enums.PageLoadMoreStatusEnum
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.foundation.language.LocalizationManager
import com.redefine.welike.base.URLCenter
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.base.track.AFGAEventManager
import com.redefine.welike.base.track.TrackerConstant
import com.redefine.welike.business.common.ToastBusinessManager
import com.redefine.welike.business.contact.bean.Friend
import com.redefine.welike.business.contact.bean.FriendList
import com.redefine.welike.business.contact.bean.clone
import com.redefine.welike.business.contact.request.FollowAllRequest
import com.redefine.welike.business.contact.request.GetFriendsRequest
import com.redefine.welike.business.contact.request.SafeUploadContactsRequest
import com.redefine.welike.business.user.management.request.FollowUserRequest
import com.redefine.welike.business.user.management.request.UnfollowUserRequest
import com.redefine.welike.business.user.ui.page.UserHostPage
import com.redefine.welike.kext.translate
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import kotlin.concurrent.thread

class ContactViewModel(app: Application) : AndroidViewModel(app) {
    //state for page.
    val state = MutableLiveData<PageStatusEnum>()

    //Loading
    val loading = MutableLiveData<PageLoadMoreStatusEnum>().also { it.value = PageLoadMoreStatusEnum.NONE }
    //data for UI.
    val data = MutableLiveData<ArrayList<Friend>>().apply {
        this.value = arrayListOf(FriendFaceToFaceHeader(), InviteFriendHeader())
    }
    private val pureData = ArrayList<Friend>() // Original data.
    // used for load more.
    var loadCursor = ""

    val permissionRunner = MutableLiveData<Boolean>().also { it.value = false }

    fun init() {
        uploadContacts()
    }

    fun refresh() {
        uploadContacts()
    }

    private fun uploadContacts() {
//        if (!EasyPermissions.hasPermissions(getApplication(), Manifest.permission.READ_CONTACTS)) {
//            permissionRunner.postValue(true)
//            return
//        }

        thread {
            state.postValue(PageStatusEnum.LOADING)
            val context = getApplication<Application>()
            val map = HashMap<String, String>()
            val cursor = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER),
                    null, null, null)
            cursor?.let {
                val nameID = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberID = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (it.moveToNext()) {
                    val name = it.getString(nameID)
                    val phoneNumber = it.getString(numberID)
                    if (!name.isNullOrEmpty() && !phoneNumber.isNullOrEmpty()) {
                        map[name] = phoneNumber
                    }
                }
                it.close()
            }
            if (map.size < 1) {
                state.postValue(PageStatusEnum.EMPTY)
            } else {
                SafeUploadContactsRequest(context, map).req(object : RequestCallback {
                    override fun onError(request: BaseRequest?, errCode: Int) {
                        state.postValue(PageStatusEnum.ERROR)
                    }

                    override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                        load(context)
                    }
                })
            }
        }
    }

    private fun load(context: Context) {
        state.postValue(PageStatusEnum.LOADING)
        loadCursor = ""
        GetFriendsRequest(context).req(object : RequestCallback {
            override fun onError(request: BaseRequest?, errCode: Int) {
                state.postValue(PageStatusEnum.ERROR)
            }

            override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                result?.let {
                    val list = Gson().fromJson<FriendList>(result.toJSONString(), FriendList::class.java)
                    loadCursor = list.cursor ?: ""
                    pureData.clear()
                    pureData.addAll(list.list)
                    data.postValue(addHeader(pureData))
                    if (loadCursor.isEmpty()) {
                        loading.postValue(PageLoadMoreStatusEnum.NO_MORE)
                    }
                }
                state.postValue(PageStatusEnum.CONTENT)
            }
        })
    }

    fun loadMore() {
        loading.postValue(PageLoadMoreStatusEnum.LOADING)
        GetFriendsRequest(getApplication(), loadCursor).req(object : RequestCallback {
            override fun onError(request: BaseRequest?, errCode: Int) {
                loading.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
            }

            override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                result?.let {
                    val list = Gson().fromJson<FriendList>(result.toJSONString(), FriendList::class.java)
                    loadCursor = list.cursor ?: ""
                    loading.postValue(PageLoadMoreStatusEnum.FINISH)
                    pureData.addAll(list.list)
                    data.postValue(addHeader(pureData))
                    if (loadCursor.isEmpty()) {
                        loading.postValue(PageLoadMoreStatusEnum.NO_MORE)
                    }
                }
            }
        })
    }

    private fun addHeader(data: ArrayList<Friend>): ArrayList<Friend> {
        val news = ArrayList<Friend>()
        data.forEach { news.add(it.clone()) }
        val count = news.count { it.registed }
        news.insertAt(FriendHeader(count, true)) { it.registed }
        news.insertAt(FriendHeader(0, false)) { !it.registed }
        news.add(0, FriendFaceToFaceHeader())
        news.add(1, InviteFriendHeader())
        return news
    }

    private inline fun <T> ArrayList<T>.insertAt(t: T, predicate: (T) -> Boolean) = indexOfFirst(predicate).let { if (it > -1) add(it, t) }


    private var followAllWorking = false
    /**
     * follow all friend. if nobody need to follow, just return.
     */
    fun followAll() {
        if (pureData.count { it.registed && !it.follow } < 1) {//if nobody need to follow.
            return
        }
        if (followAllWorking) {
            return
        }
        followAllWorking = true
        state.postValue(PageStatusEnum.LOADING)
        FollowAllRequest(getApplication()).req(object : RequestCallback {
            override fun onError(request: BaseRequest?, errCode: Int) {
                state.postValue(PageStatusEnum.ERROR)
                followAllWorking = false
            }

            override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                load(getApplication())
                followAllWorking = false
            }
        })
    }

    fun follow(friend: Friend, doFollow: Boolean) {

        friend.id?.let {
            if (!wording.contains(it)) {
                wording.add(it)
                //do follow
                if (doFollow) {
                    FollowUserRequest(it, getApplication()).req(object : RequestCallback {
                        override fun onError(request: BaseRequest?, errCode: Int) {
                            updateFriend(friend)
                            Handler(Looper.getMainLooper()).post {
                                ToastBusinessManager().onFollowCompleted("", errCode)
                            }
                        }

                        override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                            updateAccount(true)
                            friend.follow = true
                            updateFriend(friend)
                            AccountManager.getInstance().account?.let {
                                AFGAEventManager.getInstance().sendAFEvent(TrackerConstant.EVENT_FOLLOW)
                                EventLog1.Follow.report1(EventConstants.FEED_PAGE_CONTACT, it.uid, friend.id, null, null, null, null, null, null, null,null)
                            }
                        }
                    })
                } else {
                    UnfollowUserRequest(it, getApplication()).req(object : RequestCallback {
                        override fun onError(request: BaseRequest?, errCode: Int) {
                            updateFriend(friend)
                        }

                        override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
                            updateAccount(false)
                            friend.follow = false
                            updateFriend(friend)
                            AccountManager.getInstance().account?.let {
                                EventLog1.Follow.report2(EventConstants.FEED_PAGE_CONTACT, it.uid, friend.id, null, null, null, null, null, null,null)
                            }
                        }
                    })
                }

            }
        }
    }

    private fun updateFriend(friend: Friend) {
        wording.remove(friend.id)
        pureData.find { it.id == friend.id }?.let {
            it.follow = friend.follow
        }
        data.postValue(addHeader(pureData))
    }

    fun invite(friend: Friend) {
        try {
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${friend.phone}"))
                    .also {
                        it.putExtra("sms_body", "contact_share_pre".translate() + getShareUrl())
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    .let { getApplication<Application>().startActivity(it) }
            EventLog1.AddFriend.report4(EventLog1.AddFriend.Status.SUCCESS)
            EventLog.Share.report6(friend.phone)
        } catch (e: Exception) {
        }
    }

    fun showProfile(friend: Friend) {
        if (friend.registed && !friend.id.isNullOrEmpty()) {
            UserHostPage.launch(true, friend.id)
        }
    }

    fun onClickFaceToFace() {
        EventLog1.FaceToFace.report1()
        ARouter.getInstance().build(RouteConstant.FACE_TO_FACE_PATH).navigation()
    }

    //当前操作的 IDs.
    private val wording = ArrayList<String>()

    //shit
    private fun updateAccount(increase: Boolean) {
        val account = AccountManager.getInstance().account
        if (account != null) {
            account.followUsersCount = if (increase) {
                account.followedUsersCount + 1
            } else {
                val followedCount = account.followedUsersCount - 1
                if (followedCount < 0) 0 else followedCount
            }
            AccountManager.getInstance().updateAccount(account)
        }
    }

    private fun getShareUrl(): String {
        val account = AccountManager.getInstance().account
        account?.let {
            val language = LocalizationManager.getInstance().currentLanguage
            return "${URLCenter.getHostM()}download/?pid=share&c=sms&af_adset=7&af_sub1=${account.uid}&af_sub2=&lang=${language
                    ?: ""}"
        }
        return ""
    }

    fun denied() {
        state.postValue(PageStatusEnum.ERROR)
    }
}

class FriendHeader(var count: Int, var register: Boolean) : Friend("", "", "", "", "", false, false, register, 1, 1)

class FriendFaceToFaceHeader: Friend("FriendFaceToFace", "", "", "", "", false, false, false, 1, 1)
class InviteFriendHeader: Friend("InviteFriendHeader", "", "", "", "", false, false, false, 1, 1)
