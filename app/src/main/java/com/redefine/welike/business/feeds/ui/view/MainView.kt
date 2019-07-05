package com.redefine.welike.business.feeds.ui.view

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.redefine.commonui.widget.RedPointCircleBgImageView
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.R
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.base.track.AFGAEventManager
import com.redefine.welike.base.track.TrackerConstant
import com.redefine.welike.business.common.GuideManager
import com.redefine.welike.business.common.mission.MissionManager
import com.redefine.welike.business.common.mission.MissionType
import com.redefine.welike.business.common.mission.TipType
import com.redefine.welike.business.easypost.management.EasyPostManager
import com.redefine.welike.business.feeds.ui.constant.FeedConstant
import com.redefine.welike.business.feeds.ui.contract.IMianContract
import com.redefine.welike.business.feeds.ui.fragment.MainFragmentPageSwitcher
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.FeedPoster
import com.redefine.welike.business.publisher.management.PublishStateBean
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.handler.PublishMessage
import com.redefine.welike.business.publisher.ui.activity.PostStatusStater
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter
import com.redefine.welike.commonui.event.expose.MainTabDisplayManager
import com.redefine.welike.guide.TourGuide
import com.redefine.welike.kext.translate
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import com.redefine.welike.statistical.manager.PostStatusEventManager
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main2.view.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 18/1/8
 *
 *
 * Description : TODO
 *
 *
 * Creation    : 18/1/8
 * Author      : liwenbo0328@163.com
 * History     : Creation, 18/1/8, liwenbo, Create the file
 * ****************************************************************************
 */

class MainView(//    private final CircleProgressView mMainProgressView;
        private val mRootView: View, val mPageConfig: Bundle, saveState: Bundle?, val activity: Activity) : IMianContract.IMainView, View.OnClickListener {
    private val mMainHomeTab: View
    private val mMainDiscoverTab: View
    private val mMainMessageTab: View
    private val mMainMeTab: View
    private val mMainPublishTab: View
    //    private val mMainAddTab: LottieAnimationView
    private val mFragmentSwitcher: MainFragmentPageSwitcher
    //    private val cross: LottieAnimationView
    private val mPageContainer: ViewGroup
    private val mMainTabRedPoint: RedPointCircleBgImageView
    private val mMybRedPoint: RedPointCircleBgImageView
    private val mMyDistoryTabPoint: RedPointCircleBgImageView
    private val mHomeTab: ImageView
    private val mHomeRefresh: ImageView
    private var mPresenter: IMianContract.IMainPresenter? = null
    private val mTasks = HashMap<String, Float>()

    private var mLastSelectView: View? = null
    private var mLastSelectTabView: TextView? = null
    private val btnPublish: LottieAnimationView? = null
    private val tabHome: TextView
    private val tabDiscover: TextView
    private val tabMessage: TextView
    private val tabMe: TextView
    //    private val lottiePublish: LottieAnimationView
    private val tvPublish: View
    private val tvPublish1: TextView
//    private val tvPublish2: TextView

    internal var guide: TourGuide? = null
    private val observer: Observer<PublishStateBean>


    private val allProgress: Int
        get() {
            val process = mTasks.values
            var allProcess = 0
            for (f in process) {
                allProcess += Math.round(f)
            }
            return if (CollectionUtil.isEmpty(process)) {
                0
            } else allProcess / process.size
        }

    init {
        mMainHomeTab = mRootView.main_bottom_tab_home
        mMainDiscoverTab = mRootView.main_bottom_tab_discover
        mMainMessageTab = mRootView.main_bottom_tab_message
        mMainPublishTab = mRootView.xxx
        mMainMeTab = mRootView.main_bottom_tab_me
//        lottiePublish = mRootView.lottie_publish
        tvPublish = mRootView.lottie_publish_tv
        tvPublish1 = mRootView.lottie_publish_tv1
//        tvPublish2 = mRootView.lottie_publish_tv2

        mMainTabRedPoint = mRootView.main_bottom_message_red_point
        mMybRedPoint = mRootView.main_bottom_my_red_point
        mMyDistoryTabPoint = mRootView.main_bottom_discover_red_point
        mHomeTab = mRootView.home_tab
        mHomeRefresh = mRootView.home_refresh

        tabHome = mRootView.home_tab_title
        tabDiscover = mRootView.home_tab_discover_title
        tabMessage = mRootView.home_tab_message_title
        tabMe = mRootView.home_tab_me_title

        tabHome.text = "tab_home".translate()
        tabDiscover.text = "tab_discovery".translate()
        tabMessage.text = "tab_message".translate()
        tabMe.text = "tab_me".translate()

//        cross = mRootView.iv_publish_cross
//        cross.setAnimation("cross.json")
//        cross.repeatCount = LottieDrawable.INFINITE
//        mMainAddTab = mRootView.main_btn_publish
//        mMainAddTab.setAnimation("publish.json")

        mFragmentSwitcher = MainFragmentPageSwitcher(activity)

        mPageContainer = mRootView.main_page_content

        mMainHomeTab.setOnClickListener(this)
        mMainDiscoverTab.setOnClickListener(this)
        mMainMessageTab.setOnClickListener(this)
        mMainMeTab.setOnClickListener(this)
        mMainPublishTab.setOnClickListener(this)
        //        mMainAddTab.setOnClickListener(this);

        val account = AccountManager.getInstance().account

//        if (account != null && account.followUsersCount <= 1) {
//        } else {
//            defaultIndexPosition = MainFragmentPageSwitcher.FRAGMENT_DISCOVER_POSITION
//        }

        var index = MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION

        if (mPageConfig != null) {
            index = mPageConfig.getInt(FeedConstant.KEY_MAIN_PAGE_INDEX, MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION)
            if (index == MainFragmentPageSwitcher.FRAGMENT_DISCOVER_POSITION && saveState != null) {
                if (saveState.containsKey(FeedConstant.KEY_MAIN_PAGE_INDEX)) {
                    index = saveState.getInt(FeedConstant.KEY_MAIN_PAGE_INDEX, MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION)
                }
            }
        }
        observer = Observer {
            e("Publish", "publish_state:" + it?.state)
            if (it?.state == PublishMessage.PublishState.SUCCESS) {
                selectTab(MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION)
                mFragmentSwitcher.refreshHomeFollowing()
            } else if (it?.state == PublishMessage.PublishState.START) {
                selectTab(MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION)
                mFragmentSwitcher.showHomeFolowing()
            }
        }
        selectTab(index)
        registerPublishObserver()
    }

    private fun showHomeRefresh(isShowRefresh: Boolean) {
        mHomeRefresh.visibility = if (isShowRefresh) View.VISIBLE else View.GONE
        mHomeTab.visibility = if (isShowRefresh) View.INVISIBLE else View.VISIBLE
        tabHome.text = (if (isShowRefresh) "tab_refresh" else "tab_home").translate()
    }

    private fun checkEasyPost() {
        if (EasyPostManager.checkShow()) {
            tvPublish.visibility = View.VISIBLE
            tvPublish1.text = "image_status".translate(ResourceTool.ResourceFileEnum.PUBLISH)
//            tvPublish2.text = "notify_easy_post2".translate(ResourceTool.ResourceFileEnum.PUBLISH)
//            lottiePublish.visibility = View.VISIBLE
//            lottiePublish.setAnimation("easypost.json")
//            lottiePublish.setAnimation("easypost.json")
//            lottiePublish.repeatCount = -1
//            lottiePublish.playAnimation()
            tvPublish.setOnClickListener {
                tvPublish.visibility = View.GONE
                tvPublish.visibility = View.GONE
//                EasyPostActivity.launch(tvPublish.context, PostStatusModel(EventLog1.PostStatus.ButtonFrom.GUIDE_ANIMATION))
                PostStatusStater.startActivityFromGuide(tvPublish.context)
            }
            PostStatusEventManager.INSTANCE.setFrom(EventConstants.POST_STATUS.FROM_LOTTIE)
            PostStatusEventManager.INSTANCE.report1()
            EventLog1.PostStatus.report1()
        }
    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.main_bottom_tab_home -> {
                mPresenter!!.onTabChange(false)

                selectTab(MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION)
                checkEasyPost()
            }
            R.id.main_bottom_tab_discover -> {
                mPresenter!!.onTabChange(true)

                selectTab(MainFragmentPageSwitcher.FRAGMENT_DISCOVER_POSITION)

                showHomeRefresh(false)

                updateMyDistoryPoint(false)

                checkEasyPost()
            }
            R.id.main_bottom_tab_message -> {
                mPresenter!!.onTabChange(false)
                selectTab(MainFragmentPageSwitcher.FRAGMENT_MESSAGE_POSITION)
                showHomeRefresh(false)
                checkEasyPost()
            }
            R.id.main_bottom_tab_me -> {
                mPresenter!!.onTabChange(false)
                selectTab(MainFragmentPageSwitcher.FRAGMENT_MINE_POSITION)
                showHomeRefresh(false)

                //bad place to add code.
                if (MissionManager.checkTip(TipType.ME_POST)) {
                    guide = MissionManager.showTip(TipType.ME_POST, view.context as Activity)
                    AndroidSchedulers.mainThread().scheduleDirect {
                        if (guide != null) {
                            guide!!.playOn(mMainPublishTab)
                        }
                    }
                    //                    }, 1, TimeUnit.SECONDS);
                }
                checkEasyPost()
            }
            R.id.xxx -> {
                if (guide != null) {
                    EventLog.Guide.report(GuideManager.ME_POST)
                    guide!!.cleanUp()
                    guide = null
                }
//                cross.visibility = View.INVISIBLE
//                EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_PUBLISH_SHORT_CUT_ENTRANCE_PAGE))
                //val draftId = EditorActivity.launchPostActivity(view.context)
                PublishPostStarter.startActivityFromMain(view.context, getMainSource())

                setMainSource()
                AFGAEventManager.getInstance().sendEvent(TrackerConstant.EVENT_POSTER)
            }
        }//updateMyHomeTab(false);
    }

    private fun setMainSource() {
        PublisherEventManager.INSTANCE.source = 1
        if (mLastSelectView === mMainHomeTab) {
            PublisherEventManager.INSTANCE.main_source = 1
        } else if (mLastSelectView === mMainDiscoverTab) {
            PublisherEventManager.INSTANCE.main_source = 2
        } else if (mLastSelectView === mMainMessageTab) {
            PublisherEventManager.INSTANCE.main_source = 3
        } else if (mLastSelectView === mMainMeTab) {
            PublisherEventManager.INSTANCE.main_source = 4
        }
    }

    private fun getMainSource(): EventLog1.Publish.MainSource {
        return when {
            mLastSelectView === mMainHomeTab -> EventLog1.Publish.MainSource.HOME
            mLastSelectView === mMainDiscoverTab -> EventLog1.Publish.MainSource.DISCOVER
            mLastSelectView === mMainMessageTab -> EventLog1.Publish.MainSource.MESSAGE
            mLastSelectView === mMainMeTab -> EventLog1.Publish.MainSource.ME
            else -> EventLog1.Publish.MainSource.OTHER
        }
    }

//    private fun startCross() {
////        cross.playAnimation()
//    }
//
//    private fun stopCross() {
////        cross.cancelAnimation()
////        cross.progress = 0f
////        mMainAddTab.progress = 0f
//    }

    private fun setCrossProgress(progress: Int) {

    }

    private fun selectTab(index: Int) {
        if (mLastSelectView != null) {
            mLastSelectView!!.isSelected = false
            mLastSelectTabView!!.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)

        }
        when (index) {
            MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION -> {
                if (mLastSelectView === mMainHomeTab) {
                    // 选中状态下点击homeTab
                    mFragmentSwitcher.refreshTab(MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION)
                    showHomeRefresh(false)
                }
                mMainHomeTab.isSelected = true
                tabHome.isSelected = true
                mLastSelectView = mMainHomeTab
                mLastSelectTabView = tabHome
            }
            MainFragmentPageSwitcher.FRAGMENT_DISCOVER_POSITION -> {
                if (mLastSelectView === mMainDiscoverTab) {
                    // 选中状态下点击discoverTab
                    mFragmentSwitcher.refreshTab(MainFragmentPageSwitcher.FRAGMENT_DISCOVER_POSITION)
                }
                mMainDiscoverTab.isSelected = true
                tabDiscover.isSelected = true

                mLastSelectView = mMainDiscoverTab
                mLastSelectTabView = tabDiscover
                AndroidSchedulers.mainThread().scheduleDirect({ MissionManager.notifyEvent(MissionType.DISCOVERY_TAB) }, 1, TimeUnit.SECONDS)
            }
            MainFragmentPageSwitcher.FRAGMENT_MESSAGE_POSITION -> {
                mMainMessageTab.isSelected = true
                tabMessage.isSelected = true
                mLastSelectView = mMainMessageTab
                mLastSelectTabView = tabMessage
            }
            MainFragmentPageSwitcher.FRAGMENT_MINE_POSITION -> {
                mMainMeTab.isSelected = true
                tabMe.isSelected = true
                mLastSelectView = mMainMeTab
                mLastSelectTabView = tabMe
            }
        }

        mLastSelectTabView!!.typeface = Typeface.defaultFromStyle(Typeface.BOLD)

        mFragmentSwitcher.setCurrentItem(mPageContainer, index, mPageConfig)

    }


    private fun registerPublishObserver() {
        FeedPoster.getInstance().mSendStateLiveData.observe(activity as AppCompatActivity, observer)
    }

    override fun getView(): View {
        return mRootView
    }

    override fun updateRedPoint(isShowRedPoint: Boolean) {
        mMainTabRedPoint.setOnlyShowRedPoint(isShowRedPoint)
    }

    override fun updateMyRedPoint(show: Boolean) {
        mMybRedPoint.setOnlyShowRedPoint(show)
    }

    override fun updateMyHomeTab(showRefresh: Boolean) {
        showHomeRefresh(showRefresh)
    }


    override fun updateMyDistoryPoint(show: Boolean) {
        mMyDistoryTabPoint.setOnlyShowRedPoint(show)
    }

    override fun switchToPageIndex(fragmentDiscoverPosition: Int) {
        if (mLastSelectView === mMainHomeTab && fragmentDiscoverPosition == 0) return
        selectTab(fragmentDiscoverPosition)
    }

    override fun refreshUserTask(message: Message) {
        mFragmentSwitcher.refreshUserTask(message)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        mFragmentSwitcher.onActivityResult(requestCode, resultCode, data)

    }

    override fun onActivityPause() {
        mFragmentSwitcher.onActivityPause()
        MainTabDisplayManager.getInstance().onMainActivityPause()
    }

    override fun onActivityResume() {
        mFragmentSwitcher.onActivityResume()
        MainTabDisplayManager.getInstance().onMainActivityResume()
    }

    override fun attach() {
        //        if (GuideManager.INSTANCE.check(GuideManager.HOME_DISCOVERY)) {
        //            if (mRootView.getContext() instanceof Activity) {
        //                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        GuideManager.INSTANCE.show(GuideManager.HOME_DISCOVERY, (Activity) mRootView.getContext()).playOn(mMainDiscoverTab);
        //                    }
        //                }, 1, TimeUnit.SECONDS);
        //            }
        //        }
    }

    override fun setPresenter(presenter: IMianContract.IMainPresenter) {
        mPresenter = presenter
    }

    override fun showGuide(guide: String) {
        //        if (mRootView.getContext() instanceof Activity) {
        //            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
        //                @Override
        //                public void run() {
        //                    tourGuide = GuideManager.INSTANCE.show(guide, (Activity) mRootView.getContext());
        //                    tourGuide.playOn(mMainDiscoverTab);
        //                }
        //            });
        //        }
    }

    override fun isDiscoveryTab(): Boolean {
        return mLastSelectView === mMainDiscoverTab
    }

    override fun destroy() {
        mFragmentSwitcher.destroy(mPageContainer)
        FeedPoster.getInstance().mSendStateLiveData.removeObserver(observer)
    }

    override fun onPageStateChanged(oldPageState: Int, pageState: Int) {
        mFragmentSwitcher.onPageStateChanged(oldPageState, pageState)
//        if (pageState == BasePage.PAGE_STATE_SHOW) {
//            cross.visibility = View.VISIBLE
//        } else {
//            //try to clean guide
//            //            if (tourGuide != null) {
//            //                tourGuide.cleanUp();
//            //            }
//        }
    }


    override fun switchToHomePageIndex(refresh: Boolean) {

        if (mLastSelectView !== mMainHomeTab)
            selectTab(MainFragmentPageSwitcher.FRAGMENT_HOME_POSITION)

        mFragmentSwitcher.showHomeForYou(refresh)


    }
}