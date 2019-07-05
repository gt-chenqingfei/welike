package com.redefine.welike.business.feeds.discovery.ui.vm

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSONObject
import com.google.gson.Gson
import com.redefine.commonui.enums.PageLoadMoreStatusEnum
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.profile.bean.UserBase
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.business.assignment.management.AssignmentTopUserReviewManagement
import com.redefine.welike.business.assignment.management.BannerManagement
import com.redefine.welike.business.assignment.management.bean.Banner
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean
import com.redefine.welike.business.browse.management.bean.Interest
import com.redefine.welike.business.browse.management.bean.InterestPost
import com.redefine.welike.business.browse.management.constant.BrowseConstant
import com.redefine.welike.business.browse.management.request.InterestRequest
import com.redefine.welike.business.browse.management.request.RecommendSlideRequest
import com.redefine.welike.business.browse.management.request.RecommendUserRequest
import com.redefine.welike.business.browse.management.request.VideoFeedRequest2
import com.redefine.welike.business.feeds.discovery.management.request.InterestPostsRequest
import com.redefine.welike.business.feeds.discovery.ui.page.NewFeedFragment
import com.redefine.welike.business.feeds.management.HotFeedTopicManager
import com.redefine.welike.business.feeds.management.RecommendUserManager
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser
import com.redefine.welike.business.feeds.management.provider.IInterestCategoryCallback
import com.redefine.welike.business.feeds.management.provider.InterestCategoryProvider
import com.redefine.welike.business.feeds.management.request.Hot24hFeedRequest
import com.redefine.welike.business.feeds.management.request.RisingFeedRequest
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.business.user.management.bean.*
import com.redefine.welike.frameworkmvvm.AndroidViewModel
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.manager.PostEventManager


class DiscoverViewModel(application: Application) : AndroidViewModel(application), BannerManagement.BannerManagementCallback, AssignmentTopUserReviewManagement.AssignmentTopUserReviewManagementCallback, IInterestCategoryCallback, HotFeedTopicManager.HotFeedTopicCallback {

    //数据加载
    private val mTopicManager = HotFeedTopicManager()
    private val mBannerManager = BannerManagement()
    private val mAssignmentTopUserModel = AssignmentTopUserReviewManagement()
    private val mInterestCategoryProvider = InterestCategoryProvider()
    private var isInterestRefreshFinish = false


    val banners = MutableLiveData<List<Banner>>()
    val topUsers = MutableLiveData<TopUserShakeBean>()
    val suggestTopic = MutableLiveData<List<TopicSearchSugBean.TopicBean>>()
    val interests = MutableLiveData<List<UserBase.Intrest>>()
    val pageRefreshCount = MutableLiveData<Int>()

    // 排行榜的信息
    var topUserShakeBean: TopUserShakeBean? = null


    override fun onBannerManagementEnd(bannerList: MutableList<Banner>?, errCode: Int) {
        bannerList?.let {
            banners.postValue(it)
        }
    }

    override fun onAssignmentTopUserReviewManagementEnd(bean: TopUserShakeBean?, errCode: Int) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            topUsers.postValue(bean)
            topUserShakeBean = bean
        }
    }

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

    override fun onHotFeedTopicCallback(topicBeans: MutableList<TopicSearchSugBean.TopicBean>?, errCode: Int) {
        topicBeans?.let { suggestTopic.postValue(it) }
    }


    fun init() {
        mTopicManager.setListener(this)
        PostEventManager.INSTANCE.action = EventConstants.AUTO_REFRESH
        refresh()
    }

    fun refresh() {
        mTopicManager.request(true)
        mBannerManager.load(this)
        mAssignmentTopUserModel.load(this)
        if (!isInterestRefreshFinish) {
            mInterestCategoryProvider.provider(this)
        }
    }

    fun finishRefresh(count: Int) {
        pageRefreshCount.postValue(count)
    }


}

class DiscoverListViewModel(application: Application) : android.arch.lifecycle.AndroidViewModel(application) {
    var cursor = ""
    var isShowInterest = false

    val recommendData = MutableLiveData<List<RecommendSlideUser>>()
    val data = MutableLiveData<ArrayList<PostBase>>()
    val hisData = MutableLiveData<ArrayList<PostBase>>()
    private val interestIds = java.util.ArrayList<String>()
    val pageStatus = MutableLiveData<PageStatusEnum>()
    val loadMoreStatus = MutableLiveData<PageLoadMoreStatusEnum>()
    val interestPost = MutableLiveData<InterestPost>()
//    val refreshStatus = MutableLiveData<Int>()


    private lateinit var interestId: String

    fun init(id: String) {
        interestId = id
        refresh()
    }

    fun loadRecommendUser() {

        if (isRecommendLoading) return
        isRecommendLoading = true
        RecommendUserManager().tryRefresh(getApplication(), object : RecommendUserManager.OnRecommendUserListener {
            override fun onSuccess(ls: RecommendSlideUserList?) {
                isRecommendLoading = false
                ls?.let {
                    recommendData.postValue(it.list)
                }
            }

            override fun onFail(errCode: Int) {
                isRecommendLoading = false
            }
        })
    }

    fun refresh() {
        if (isHotLoading) {
            return
        }
        try {
            isHotLoading = true

            pageStatus.postValue(PageStatusEnum.LOADING)

            val callback = object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {
                    isHotLoading = false
                    pageStatus.postValue(PageStatusEnum.ERROR)
                }

                @Throws(Exception::class)
                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    isHotLoading = false
                    val posts = PostsDataSourceParser.parsePosts(result)
                    val cursorValue = PostsDataSourceParser.parseCursor(result) ?: ""
                    if (cursorValue.isEmpty()) {
                        loadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
                    } else loadMoreStatus.postValue(PageLoadMoreStatusEnum.NONE)
                    cursor = cursorValue
                    if (!CollectionUtil.isEmpty(posts)) {
                        data.postValue(posts)
                        pageStatus.postValue(PageStatusEnum.CONTENT)
                        when (interestId) {
                            NewFeedFragment.FOR_YOU -> {
                                loadRecommendUser()
                            }
                        }
                    } else {
                        pageStatus.postValue(PageStatusEnum.EMPTY)
                    }
                    when (interestId) {
                        BrowseConstant.INTEREST_VIDEO -> {
                            PostEventManager.INSTANCE.type = EventConstants.FEED_REFRESH_TYPE_DISCOVER + EventConstants.LABEL_VIDEO
                            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_VIDEO)
                        }
                        NewFeedFragment.FOR_YOU -> {
                            PostEventManager.INSTANCE.type = EventConstants.FEED_REFRESH_TYPE_DISCOVER + EventConstants.LABEL_FOR_YOU
                            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_FOR_YOU)
                        }
                        NewFeedFragment.LATEST -> {
                            PostEventManager.INSTANCE.type = EventConstants.FEED_DISCOVER_LATEST.toString()
                            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_LATEST)
                        }
                        else -> {
                            PostEventManager.INSTANCE.type = EventConstants.FEED_REFRESH_TYPE_DISCOVER + interestId
                            PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER + interestId)
                        }
                    }
                    PostEventManager.INSTANCE.sendEventStrategy(posts)
                }
            }
            when (interestId) {
                BrowseConstant.INTEREST_VIDEO -> {
                    val interests = AccountManager.getInstance().account?.intrests
                    if (interests != null && interests.isNotEmpty()) {
                        interestIds.clear()
                        for (interest in interests) {
                            interestIds.add(interest.iid)
                        }
                    }
                    VideoFeedRequest2(getApplication()).request("", interestIds, callback)

                }
                NewFeedFragment.FOR_YOU -> {
                    val interests = AccountManager.getInstance().account?.intrests
                    if (interests != null && interests.isNotEmpty()) {
                        interestIds.clear()
                        for (interest in interests) {
                            interestIds.add(interest.iid)
                        }

                        Hot24hFeedRequest(getApplication()).request("", interestIds, callback)
                    } else {
                        Hot24hFeedRequest(getApplication()).request("", interestIds, callback)
                        if (!isShowInterest) {
                            getInterestList()
                        }
                    }
                }
                NewFeedFragment.LATEST -> {
                    cursor = ""
                    val interests = AccountManager.getInstance().account?.intrests
                    if (interests != null && interests.isNotEmpty()) {
                        interestIds.clear()
                        for (interest in interests) {
                            interestIds.add(interest.iid)
                        }
                        RisingFeedRequest(getApplication()).request(cursor, interestIds, callback)
                    } else {
                        RisingFeedRequest(getApplication()).request(cursor, interestIds, callback)
                    }
                }
                else -> {
                    InterestPostsRequest(interestId, getApplication()).tryFeeds("", callback)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isHotLoading = false
            pageStatus.postValue(PageStatusEnum.ERROR)
        }

    }

    fun loadMore() {
        if (isHotLoading) return

        if (cursor.isEmpty()) {
            pageStatus.postValue(PageStatusEnum.CONTENT)
            loadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
        } else {
            try {
                isHotLoading = true
                val callback = object : RequestCallback {
                    override fun onError(request: BaseRequest, errCode: Int) {
                        isHotLoading = false
                        loadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
                    }

                    @Throws(Exception::class)
                    override fun onSuccess(request: BaseRequest, result: JSONObject) {
                        isHotLoading = false
                        val posts = PostsDataSourceParser.parsePosts(result)
                        cursor = PostsDataSourceParser.parseCursor(result) ?: ""
//                        val list = hisData.value ?: ArrayList()
//                        list.addAll(posts)
                        hisData.postValue(posts)
                        when (interestId) {
                            BrowseConstant.INTEREST_VIDEO -> {
                                PostEventManager.INSTANCE.type = EventConstants.FEED_REFRESH_TYPE_DISCOVER + EventConstants.LABEL_VIDEO
                                PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_VIDEO)
                            }
                            NewFeedFragment.FOR_YOU -> {
                                PostEventManager.INSTANCE.type = EventConstants.FEED_REFRESH_TYPE_DISCOVER + EventConstants.LABEL_FOR_YOU
                                PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_FOR_YOU)
                            }
                            NewFeedFragment.LATEST -> {
                                PostEventManager.INSTANCE.type = EventConstants.FEED_DISCOVER_LATEST.toString()
                                PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_LATEST)
                            }
                            else -> {
                                PostEventManager.INSTANCE.type = EventConstants.FEED_REFRESH_TYPE_DISCOVER + interestId
                                PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER + interestId)
                            }
                        }
                        PostEventManager.INSTANCE.action = EventConstants.PULL_UP_REFRESH
                        PostEventManager.INSTANCE.sendEventStrategy(posts)
                        pageStatus.postValue(PageStatusEnum.CONTENT)

                        if (cursor.isEmpty()) {
                            loadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
                        } else {
                            loadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH)
                        }

                    }
                }
                when (interestId) {
                    NewFeedFragment.FOR_YOU -> {
//                        Hot24hFeedRequest(getApplication()).request(cursor, callback)
//                        NewFeedPage.FOR_YOU -> {
                        Hot24hFeedRequest(getApplication()).request(cursor, interestIds, callback)
//                            >>>>>>> feature/dev_simplify_regist
                    }

                    BrowseConstant.INTEREST_VIDEO -> {
                        val interests = AccountManager.getInstance().account?.intrests
                        if (interests != null && interests.isNotEmpty()) {
                            interestIds.clear()
                            for (interest in interests) {
                                interestIds.add(interest.iid)
                            }
                        }
                        VideoFeedRequest2(getApplication()).request(cursor, interestIds, callback)
                    }
                    NewFeedFragment.LATEST -> {
                        RisingFeedRequest(getApplication()).request(cursor, interestIds, callback)
                    }
                    else -> {
                        InterestPostsRequest(interestId, getApplication()).tryFeeds(cursor, callback)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isHotLoading = false
                loadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
            }
        }
    }

    private fun getInterestList() {

        try {
            InterestRequest(getApplication(), "").req(object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {

                }

                @Throws(Exception::class)
                override fun onSuccess(request: BaseRequest, result: JSONObject) {

                    val gson = Gson()
                    val bean = gson.fromJson(result.toJSONString(), InterestRequestBean::class.java)
                    // TODO: 2018/7/20
                    if (bean.list.size > 0) {
                        val post = InterestPost()
                        post.list = bean.list
                        interestPost.postValue(post)
                        isShowInterest = true
                    }
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun insertInterest2DB(list: List<Interest>) {

//        AccountManager.getInstance().account.intrests = getSelectInterest(list)
//        AccountManager.getInstance().modifyAccount(AccountManager.getInstance().account)
        AccountManager.getInstance().modifyAccountInterest(getSelectInterest(list))
        // TODO: 2018/7/20
        for (interest in list) {
            interestIds.add(interest.id)
        }

//        try {
//
//            RefreshPostCacheRequest(MyApplication.getAppContext(), GoogleUtil.gaid).req()
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        refresh()
    }


    fun getSelectInterest(items: List<Interest>?): List<UserBase.Intrest> {

        val list = java.util.ArrayList<UserBase.Intrest>()
        var intrest2: UserBase.Intrest
        for (obj in items!!) {
            intrest2 = UserBase.Intrest()
            intrest2.label = obj.name
            intrest2.iid = obj.id
            intrest2.icon = ""
            list.add(intrest2)
//                if (obj.subset != null && obj.subset!!.size > 0) {
//                    list.addAll(getSelectInterest(obj.subset))
//                }

        }

        return list
    }


    var isHotLoading = false
    var isRecommendLoading = false
}