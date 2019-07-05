package com.redefine.welike.business.browse.ui.viewmodel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.text.TextUtils
import android.util.Log

import com.alibaba.fastjson.JSONObject
import com.redefine.commonui.enums.PageLoadMoreStatusEnum
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.foundation.language.LocalizationManager
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.im.threadTry
import com.redefine.im.threadUITry
import com.redefine.welike.MyApplication
import com.redefine.welike.base.LanguageSupportManager
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.request.BaseRequest
import com.redefine.welike.base.request.RequestCallback
import com.redefine.welike.business.assignment.management.BannerManagement
import com.redefine.welike.business.assignment.management.bean.Banner
import com.redefine.welike.business.browse.management.bean.InterestPost
import com.redefine.welike.business.browse.management.request.*
import com.redefine.welike.business.feeds.management.DiscoverDataSourceCache
import com.redefine.welike.business.feeds.management.bean.DiscoverHeader
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.feeds.management.parser.PostsDataSourceParser
import com.redefine.welike.business.feeds.management.request.RisingFeedRequest
import com.redefine.welike.frameworkmvvm.AndroidViewModel
import com.redefine.welike.kext.spFireOnce
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.manager.PostEventManager

import java.util.ArrayList

enum class DiscoverType {
    Discover, SkipDiscover, Rising, SkipRising
}

class BrowseDiscoverCViewModel(application: Application) : AndroidViewModel(application) {

    val pageStatus = MutableLiveData<PageStatusEnum>()
    val banners = MutableLiveData<List<Banner>>()
    private val mBannerManager = BannerManagement()

    private var videoCursor: String? = null
    private var isShowInterest = false
    @Volatile
    private var isLoading = false


    private val mDiscoverDataSourceCache = DiscoverDataSourceCache()

    val posts = MutableLiveData<ArrayList<PostBase>>()

    val morePosts = MutableLiveData<ArrayList<PostBase>>()

    private val mLoadMoreStatus = MutableLiveData<PageLoadMoreStatusEnum>()

    private val mPageRefreshCount = MutableLiveData<Int>()

    private val interestPost = MutableLiveData<InterestPost>()

    private val interestIds = ArrayList<String>()

    var forwardUrl = ""

    var contentType = DiscoverType.Discover //0 is Discover HashTag, 1 is skip rising, 2 is rising

    fun refresh() {
        pageStatus.postValue(PageStatusEnum.LOADING)
        mBannerManager.loadSkipBanner { bannerList, _ -> banners.postValue(bannerList) }
        when (contentType) {
            DiscoverType.Discover -> loadTopics()
            DiscoverType.SkipDiscover -> loadSkipTopics()
            DiscoverType.Rising -> loadRising()
            DiscoverType.SkipRising -> loadSkipRising()
        }

        if (forwardUrl.isEmpty()) {
            getForwardURL()
        }
    }

    fun onHisLoad() {
        when (contentType) {
            DiscoverType.Discover -> loadmoreTopics()
            DiscoverType.SkipDiscover -> loadmoreSkipTopics()
            DiscoverType.Rising -> loadmoreRising()
            DiscoverType.SkipRising -> loadmoreSkipRising()
        }
    }

    private fun loadRising() {
        Log.d("DDAI", "API = loadRising")
        if (isLoading) return
        try {
            isLoading = true
            videoCursor = ""
            pageStatus.postValue(PageStatusEnum.LOADING)

            val interests = AccountManager.getInstance().account?.intrests
            if (interests != null && interests.isNotEmpty()) {
                interestIds.clear()
                for (interest in interests) {
                    interestIds.add(interest.iid)
                }
            }
            Log.d("DDAI", "")
            RisingFeedRequest(getApplication()).request(videoCursor, interestIds, object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {
                    isLoading = false
                    mPageRefreshCount.postValue(-1)
                    pageStatus.postValue(PageStatusEnum.ERROR)
                }

                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    isLoading = false
                    val target = PostsDataSourceParser.parsePosts(result)
                    videoCursor = PostsDataSourceParser.parseCursor(result) ?: ""
                    if (videoCursor.isNullOrEmpty()) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
                    }

                    if (!CollectionUtil.isEmpty(target)) {
                        posts.postValue(target)
                        pageStatus.postValue(PageStatusEnum.CONTENT)
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH)
                    } else {
                        pageStatus.postValue(PageStatusEnum.EMPTY)
                        mPageRefreshCount.postValue(0)
                    }
                    PostEventManager.INSTANCE.type = EventConstants.FEED_DISCOVER_LATEST.toString()
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_LATEST)
                    PostEventManager.INSTANCE.sendEventStrategy(target)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
            mPageRefreshCount.postValue(-1)
            pageStatus.postValue(PageStatusEnum.ERROR)
        }
    }

    private fun loadmoreRising() {
        Log.d("DDAI", "API = loadmoreRising")
        if (isLoading) return

        if (TextUtils.isEmpty(videoCursor)) {
            pageStatus.postValue(PageStatusEnum.CONTENT)
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
        } else {
            try {
                isLoading = true
                RisingFeedRequest(getApplication()).request(videoCursor, interestIds, object : RequestCallback {
                    override fun onError(request: BaseRequest, errCode: Int) {
                        isLoading = false
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
                    }

                    @Throws(Exception::class)
                    override fun onSuccess(request: BaseRequest, result: JSONObject) {
                        isLoading = false
                        val temPosts = PostsDataSourceParser.parsePosts(result)
//                        val posts = mDiscoverDataSourceCache.filterPosts(temPosts, BrowseConstant.INTEREST_LATEST)
                        videoCursor = PostsDataSourceParser.parseCursor(result)
//                        mDiscoverDataSourceCache.addHotHisData(posts)
//                        mDiscoverDataSourceCache.addHistoryInterestsPost(BrowseConstant.INTEREST_LATEST, posts)
                        PostEventManager.INSTANCE.type = EventConstants.FEED_DISCOVER_LATEST.toString()
                        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_LATEST)
                        PostEventManager.INSTANCE.action = EventConstants.PULL_UP_REFRESH
                        PostEventManager.INSTANCE.sendEventStrategy(temPosts)
//                        val data = morePosts.value ?: ArrayList()
//                        morePosts.postValue(data.also { data.addAll(temPosts) })
//                        pageStatus.postValue(PageStatusEnum.CONTENT)

                        if (TextUtils.isEmpty(videoCursor) || temPosts.isEmpty()) {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
                        } else {
                            morePosts.postValue(temPosts)
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH)
                        }

                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
                mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
            }

        }
    }

    private fun loadSkipRising() {
        Log.d("DDAI", "API = loadSkipRising")
        if (isLoading) return
        try {
            isLoading = true
            videoCursor = ""
            pageStatus.postValue(PageStatusEnum.LOADING)
            FeedsRequest(MyApplication.getAppContext()).tryFeeds(videoCursor, interestIds, object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {
                    isLoading = false
                    mPageRefreshCount.postValue(-1)
                    pageStatus.postValue(PageStatusEnum.ERROR)
                }

                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    isLoading = false
                    val target = PostsDataSourceParser.parsePosts(result)
                    videoCursor = PostsDataSourceParser.parseCursor(result)
                    if (videoCursor == null) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
                    }
                    if (!CollectionUtil.isEmpty(target)) {
                        posts.postValue(target)
                        pageStatus.postValue(PageStatusEnum.CONTENT)
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH)
                    } else {
                        pageStatus.postValue(PageStatusEnum.EMPTY)
                        mPageRefreshCount.postValue(0)
                    }
                    PostEventManager.INSTANCE.type = EventConstants.FEED_PAGE_UNLOGIN_LATEST
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_LATEST)
                    PostEventManager.INSTANCE.sendEventStrategy(target)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
            mPageRefreshCount.postValue(-1)
            pageStatus.postValue(PageStatusEnum.ERROR)
        }

    }


    private fun loadmoreSkipRising() {
        Log.d("DDAI", "API = loadmoreSkipRising")
        if (isLoading) return

        if (TextUtils.isEmpty(videoCursor)) {
            pageStatus.postValue(PageStatusEnum.CONTENT)
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
        } else {
            val feedsRequest = FeedsRequest(MyApplication.getAppContext())
            try {
                isLoading = true
                feedsRequest.tryFeeds(videoCursor, interestIds, object : RequestCallback {
                    override fun onError(request: BaseRequest, errCode: Int) {
                        isLoading = false
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
                    }

                    @Throws(Exception::class)
                    override fun onSuccess(request: BaseRequest, result: JSONObject) {
                        isLoading = false
                        val temPosts = PostsDataSourceParser.parsePosts(result)
//                        val posts = mDiscoverDataSourceCache.filterPosts(temPosts, BrowseConstant.INTEREST_LATEST)
                        videoCursor = PostsDataSourceParser.parseCursor(result)
//                        mDiscoverDataSourceCache.addHotHisData(posts)
//                        mDiscoverDataSourceCache.addHistoryInterestsPost(BrowseConstant.INTEREST_LATEST, posts)
                        PostEventManager.INSTANCE.type = EventConstants.FEED_PAGE_UNLOGIN_LATEST
                        PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_LATEST)
                        PostEventManager.INSTANCE.action = EventConstants.PULL_UP_REFRESH
                        PostEventManager.INSTANCE.sendEventStrategy(temPosts)
//                        val data = morePosts.value ?: ArrayList()
//                        morePosts.postValue(data.also { data.addAll(temPosts) })
//                        pageStatus.postValue(PageStatusEnum.CONTENT)

                        if (TextUtils.isEmpty(videoCursor) || temPosts.isEmpty()) {
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
                        } else {
                            morePosts.postValue(temPosts)
                            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH)
                        }

                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
                mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
            }

        }
    }

    var page = 1

    private fun loadTopics() {
        Log.d("DDAI", "API = loadTopics")
        page = 1
        if (isLoading) return
        try {
            isLoading = true
            pageStatus.postValue(PageStatusEnum.LOADING)
            DiscoverTopicsRequest(MyApplication.getAppContext()).tryFeeds(1, 1, object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {
                    isLoading = false
                    mPageRefreshCount.postValue(-1)
                    pageStatus.postValue(PageStatusEnum.ERROR)
                }

                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    isLoading = false
                    val target = parserTopicList(result)
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_LATEST)
                    if (!CollectionUtil.isEmpty(target)) {
                        posts.postValue(target)
                        pageStatus.postValue(PageStatusEnum.CONTENT)
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH)
                    } else {
                        pageStatus.postValue(PageStatusEnum.EMPTY)
                        mPageRefreshCount.postValue(0)
                    }
                    PostEventManager.INSTANCE.type = EventConstants.FEED_DISCOVER_LATEST.toString()
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_LATEST)
                    PostEventManager.INSTANCE.sendEventStrategy(target)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
            mPageRefreshCount.postValue(-1)
            pageStatus.postValue(PageStatusEnum.ERROR)
        }

    }

    private fun loadmoreTopics() {
        if (isLoading) return
        try {
            isLoading = true
            DiscoverTopicsRequest(MyApplication.getAppContext()).tryFeeds(-1, page, object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {
                    isLoading = false
                    mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
                }

                @Throws(Exception::class)
                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    page++
                    isLoading = false
                    val temPosts = parserTopicList(result)
//                        videoCursor = PostsDataSourceParser.parseCursor(result)

                    if (temPosts.isEmpty()) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
                    } else {
                        morePosts.postValue(temPosts)
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH)
                    }
                    PostEventManager.INSTANCE.type = EventConstants.FEED_DISCOVER_LATEST.toString()
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_DISCOVER_LATEST)
                    PostEventManager.INSTANCE.action = EventConstants.PULL_UP_REFRESH
                    PostEventManager.INSTANCE.sendEventStrategy(temPosts)
//                    val data = morePosts.value ?: ArrayList()
//                    pageStatus.postValue(PageStatusEnum.CONTENT)

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
        }
    }

    private fun loadSkipTopics() {
        page = 1
        if (isLoading) return
        try {
            isLoading = true
            pageStatus.postValue(PageStatusEnum.LOADING)
            DiscoverSkipTopicsRequest(MyApplication.getAppContext()).tryFeeds(1, 1, object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {
                    isLoading = false
                    mPageRefreshCount.postValue(-1)
                    pageStatus.postValue(PageStatusEnum.ERROR)
                }

                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    isLoading = false
                    val target = parserTopicList(result)
                    if (!CollectionUtil.isEmpty(target)) {
                        posts.postValue(target)
                        pageStatus.postValue(PageStatusEnum.CONTENT)
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH)
                    } else {
                        pageStatus.postValue(PageStatusEnum.EMPTY)
                        mPageRefreshCount.postValue(0)
                    }
                    PostEventManager.INSTANCE.type = EventConstants.FEED_PAGE_UNLOGIN_LATEST
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_LATEST)
                    PostEventManager.INSTANCE.sendEventStrategy(target)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
            mPageRefreshCount.postValue(-1)
            pageStatus.postValue(PageStatusEnum.ERROR)
        }

    }

    private fun loadmoreSkipTopics() {
        if (isLoading) return
        try {
            isLoading = true
            DiscoverSkipTopicsRequest(MyApplication.getAppContext()).tryFeeds(-1, page, object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {
                    isLoading = false
                    mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
                }

                @Throws(Exception::class)
                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    page++
                    isLoading = false
                    val temPosts = parserTopicList(result)
//                        videoCursor = PostsDataSourceParser.parseCursor(result)

                    if (temPosts.isEmpty()) {
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.NO_MORE)
                    } else {
                        morePosts.postValue(temPosts)
                        mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.FINISH)
                    }
                    PostEventManager.INSTANCE.type = EventConstants.FEED_PAGE_UNLOGIN_LATEST
                    PostEventManager.INSTANCE.setOldType(EventConstants.FEED_PAGE_OLD_UNLOGIN_LATEST)
                    PostEventManager.INSTANCE.action = EventConstants.PULL_UP_REFRESH
                    PostEventManager.INSTANCE.sendEventStrategy(temPosts)
//                    val data = morePosts.value ?: ArrayList()
//                    pageStatus.postValue(PageStatusEnum.CONTENT)

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
            mLoadMoreStatus.postValue(PageLoadMoreStatusEnum.LOAD_ERROR)
        }
    }

    fun getLoadMoreStatus(): MutableLiveData<PageLoadMoreStatusEnum> {
        return mLoadMoreStatus
    }

    fun getInterestPost(): MutableLiveData<InterestPost> {
        return interestPost
    }

    private fun getForwardURL() {
        if (AccountManager.getInstance().isLoginComplete) {
            TrendingPeopleRequest(getApplication()).request(object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {

                }

                @Throws(Exception::class)
                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    forwardUrl = result.getString("forwardUrl")
                }
            })
        } else {
            TrendingPeopleRequest1(getApplication()).request(object : RequestCallback {
                override fun onError(request: BaseRequest, errCode: Int) {

                }

                @Throws(Exception::class)
                override fun onSuccess(request: BaseRequest, result: JSONObject) {
                    forwardUrl = result.getString("forwardUrl")
                }
            })
        }

    }

    private fun parserTopicList(result: JSONObject): ArrayList<PostBase> {
        val list = ArrayList<PostBase>()
        try {
            result.getJSONArray("list").let { ja ->
                for (x in 0 until ja.size) {
                    list.addAll(parserDiscoverHeader(ja.getJSONObject(x)))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    val test = ArrayList<PostBase>()

    private fun parserDiscoverHeader(result: JSONObject): ArrayList<PostBase> {
        val list = ArrayList<PostBase>()
        val bean = DiscoverHeader()
        bean.type = DiscoverHeader.TYPE
        try {
            org.json.JSONObject(result.toString()).let { json ->
                bean.id = json.optInt("id")
                bean.hot = json.optInt("hot")
                bean.weight = json.optInt("weight")
                bean.status = json.optInt("status")
                bean.createTime = json.optLong("createTime")
                bean.updateTime = json.optLong("updateTime")
                bean.postCount = json.optInt("postCount")
                bean.topicId = json.optString("topicId")
                bean.topicName = json.optString("topicName")
                bean.lanCode = json.optString("lanCode")
                bean.iconUrl = json.optString("iconUrl")
                bean.rmdWords = json.optString("rmdWords")
                bean.operator = json.optString("operator")
                list.add(bean)
            }
            try {
                list.addAll(PostsDataSourceParser.parsePostList(result.getJSONArray("posts")))
//                val x = (PostsDataSourceParser.parsePostList(result.getJSONArray("posts")))
//                list.addAll(x)
//                if (test.size < 3) {
//                    test.addAll(x)
//                }
//                if (x.size < 1) {
//                    list.addAll(test)
//                }
            } catch (e: Exception) {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list

    }

    fun checkGuide(work: () -> Unit) {
        threadTry {
            if (!checkShowRising()) {
                val sp = MyApplication.getAppContext().getSharedPreferences("DISCOVER_GUIDE", Context.MODE_PRIVATE)
                spFireOnce(sp, "DISCOVER_GUIDE") {
                    threadUITry { work() }
                }
            }
        }
    }

    fun checkShowRising(): Boolean {
        val language = LanguageSupportManager.getInstance().currentMenuLanguageType
        return !(TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_ENG) || TextUtils.equals(language, LocalizationManager.LANGUAGE_TYPE_HINDI))
    }
}
