package com.redefine.welike.business.user.ui.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.business.browse.management.request.RecommendUserRequest2
import com.redefine.welike.business.user.management.bean.RecommendUser1
import com.redefine.welike.business.user.management.bean.RecommendUserList1

/**
 * @author redefine honlin
 * @Date on 2018/11/28
 * @Description
 */

class RecommendUserViewModel2 : AndroidViewModel {

    var recommedUsers = MutableLiveData<List<RecommendUser1>>()

    var mPageStatus = MutableLiveData<PageStatusEnum>()

    var mCode = MutableLiveData<Int>()

    var followUsers = MutableLiveData<java.util.ArrayList<RecommendUser1>>()

    val hasFollowUserList = ArrayList<RecommendUser1>()


    var isLoading = false

    constructor(application: Application) : super(application)

    fun tryRefresh() {

        if (isLoading) return

        tryLoadRecommendUser(1)
    }

    fun tryLoadMore(pageNumber: Int) {
        if (isLoading) return

        tryLoadRecommendUser(pageNumber + 2)
    }

    fun tryLoadRecommendUser(pageIndex: Int) {

        isLoading = true

        try {
            mPageStatus.postValue(PageStatusEnum.LOADING)
            val request = RecommendUserRequest2(getApplication())
            request.request(pageIndex, object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {
                    mPageStatus.postValue(PageStatusEnum.ERROR)
                    mCode.postValue(errCode)
                    recommedUsers.postValue(ArrayList())
                    isLoading = false
                }

                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    val gson = Gson()

                    if (!TextUtils.isEmpty(result.getString("list"))) {
                        val recommendUserList = gson.fromJson(result.toString(), RecommendUserList1::class.java)
                        recommedUsers.postValue(recommendUserList.list)
                    } else recommedUsers.postValue(ArrayList())
                    mPageStatus.postValue(PageStatusEnum.CONTENT)
                    isLoading = false
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
            recommedUsers.postValue(ArrayList())
            mPageStatus.postValue(PageStatusEnum.ERROR)
            isLoading = false
        }
    }


    fun addFollowUser(user: RecommendUser1) {

        var tmpList = ArrayList<RecommendUser1>()


        when (user.following) {
            true -> {
                hasFollowUserList.add(0, user)
            }

            false -> {

                hasFollowUserList.forEach {

                    if (TextUtils.equals(it.uid, user.uid)) {
                        tmpList.add(it)
                    }
                }
                hasFollowUserList.removeAll(tmpList)
            }
        }

        followUsers.postValue(hasFollowUserList)

    }

}
