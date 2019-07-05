package com.redefine.welike.business.publisher.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.publisher.api.TopicApiService
import com.redefine.welike.business.publisher.api.bean.Topic
import com.redefine.welike.business.publisher.api.bean.TopicCategory
import com.redefine.welike.net.RetrofitManager
import com.redefine.welike.net.subscribeExt
import io.reactivex.schedulers.Schedulers

/**
 *
 * Name: SuperTopicChosenViewModel
 * Author: liwenbo
 * Email:
 * Comment: //TODO
 * Date: 2018-09-04 14:49
 *
 */

class SuperTopicChosenViewModel(application: Application) : AndroidViewModel(application) {

    val categories = MutableLiveData<List<TopicCategory>>()
    val topics = MutableLiveData<MutableMap<String, List<Topic>>>()

    val pageState = MutableLiveData<PageStatusEnum>()

    val topicPageState = MutableLiveData<TopicPageState>()

    private val topicApi = RetrofitManager.getInstance().retrofit.create(TopicApiService::class.java)

    fun requestCategories() {
        pageState.postValue(PageStatusEnum.LOADING)

        var api = topicApi.getTopicCategories()

        if (!AccountManager.getInstance().isLogin) {
            api = topicApi.getSkipTopicCategories()
        }
        api.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribeExt({
            if (it.code == ErrorCode.ERROR_NETWORK_SUCCESS) {
                if (CollectionUtil.isEmpty(it.result)) {
                    categories.postValue(mutableListOf())
                    pageState.postValue(PageStatusEnum.EMPTY)
                } else {
                    pageState.postValue(PageStatusEnum.CONTENT)
                    categories.postValue(it.result)
                }
            } else {
                pageState.postValue(PageStatusEnum.ERROR)
            }
        }, {
            pageState.postValue(PageStatusEnum.ERROR)
        })
    }

    fun requestCategoryItems(labelId: String) {
        topicPageState.postValue(TopicPageState(labelId, PageStatusEnum.LOADING))
        var api = topicApi.getTopic(labelId)
        if (!AccountManager.getInstance().isLogin) {
            api = topicApi.getSkipTopic(labelId)
        }
        api.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribeExt({
            if (it.code == ErrorCode.ERROR_NETWORK_SUCCESS) {
                var map = topics.value
                if (map == null) {
                    map = hashMapOf()
                }
                if (CollectionUtil.isEmpty(it.result)) {
                    map[labelId] = mutableListOf()
                    topicPageState.postValue(TopicPageState(labelId, PageStatusEnum.EMPTY))
                } else {
                    it.result!!.forEach {
                        it.labelId = labelId
                    }
                    map[labelId] = it.result!!
                    topicPageState.postValue(TopicPageState(labelId, PageStatusEnum.CONTENT))
                }
                topics.postValue(map)
            } else {
                topicPageState.postValue(TopicPageState(labelId, PageStatusEnum.ERROR))
            }
        }, {
            topicPageState.postValue(TopicPageState(labelId, PageStatusEnum.ERROR))
        })
    }

    class TopicPageState(val labelId: String, val pageStatusEnum: PageStatusEnum)
}