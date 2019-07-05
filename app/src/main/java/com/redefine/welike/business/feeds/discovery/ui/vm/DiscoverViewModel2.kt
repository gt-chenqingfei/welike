package com.redefine.welike.business.feeds.discovery.ui.vm

//import com.redefine.welike.business.feeds.discovery.ui.page.NewFeedPage
//import com.redefine.welike.business.feeds.discovery.ui.page.NewFeedPage
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.base.ErrorCode
import com.redefine.welike.base.profile.bean.UserBase
import com.redefine.welike.business.assignment.management.AssignmentTopUserReviewManagement
import com.redefine.welike.business.assignment.management.BannerManagement
import com.redefine.welike.business.assignment.management.bean.Banner
import com.redefine.welike.business.assignment.management.bean.TopUserShakeBean
import com.redefine.welike.business.feeds.management.HotFeedTopicManager
import com.redefine.welike.business.feeds.management.provider.IInterestCategoryCallback
import com.redefine.welike.business.feeds.management.provider.InterestCategoryProvider
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean
import com.redefine.welike.statistical.manager.PostEventManager


class DiscoverViewModel2(application: Application) : AndroidViewModel(application),
        BannerManagement.BannerManagementCallback,
        AssignmentTopUserReviewManagement.AssignmentTopUserReviewManagementCallback,
        IInterestCategoryCallback,
        HotFeedTopicManager.HotFeedTopicCallback {

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


    override fun onBannerManagementEnd(bannerList: MutableList<Banner>?, errCode: Int) {
        bannerList?.let {
            banners.postValue(it)
        }
    }

    override fun onAssignmentTopUserReviewManagementEnd(bean: TopUserShakeBean?, errCode: Int) {
        if (errCode == ErrorCode.ERROR_SUCCESS) {
            topUsers.postValue(bean)
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
        PostEventManager.INSTANCE.action = 3
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