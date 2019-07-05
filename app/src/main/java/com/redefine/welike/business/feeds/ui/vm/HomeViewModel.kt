package com.redefine.welike.business.feeds.ui.vm

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.base.profile.bean.UserBase
import com.redefine.welike.business.feeds.management.provider.IInterestCategoryCallback
import com.redefine.welike.business.feeds.management.provider.InterestCategoryProvider
import com.redefine.welike.frameworkmvvm.AndroidViewModel

/**
 * @author redefine honlin
 * @Date on 2018/10/25
 * @Description
 */
class HomeViewModel(application: Application) : AndroidViewModel(application),IInterestCategoryCallback {

    //数据加载
    private val mInterestCategoryProvider = InterestCategoryProvider()
    private var isInterestRefreshFinish = false

    val interests = MutableLiveData<List<UserBase.Intrest>>()

    override fun onInterestCategorySuccess(result: MutableList<UserBase.Intrest>?) {
        if (!CollectionUtil.isEmpty(result) && !isInterestRefreshFinish) {
            isInterestRefreshFinish = true
            interests.postValue(result)
        }
    }

    override fun onInterestCategoryFail() {
        isInterestRefreshFinish = false
        interests.postValue(null)
    }

    fun init() {
        refresh()
    }

    fun refresh() {
        if (!isInterestRefreshFinish) {
            mInterestCategoryProvider.provider(this)
        }
    }


}