package com.redefine.welike.business.browse.ui.page

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.arch.lifecycle.Observer
import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.pekingese.pagestack.framework.cache.PageCache
import com.pekingese.pagestack.framework.config.PageConfig
import com.pekingese.pagestack.framework.page.PageName
import com.redefine.foundation.framework.Event
import com.redefine.foundation.utils.ScreenUtils
import com.redefine.welike.R
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.browse.ui.switcher.BrowseFragmentPageSwitcher
import com.redefine.welike.business.browse.ui.viewmodel.BrowseMainViewModel
import com.redefine.welike.business.common.mission.MissionManager
import com.redefine.welike.business.common.mission.MissionType
import com.redefine.welike.business.publisher.ui.activity.PublishPostStarter
import com.redefine.welike.business.startup.management.HalfLoginManager
import com.redefine.welike.business.startup.management.constant.RegisteredConstant
import com.redefine.welike.common.BrowseManager
import com.redefine.welike.common.BrowseSchemeManager
import com.redefine.welike.commonui.view.ActionSnackBar
import com.redefine.welike.frameworkmvvm.BaseLifecyclePageStackManager
import com.redefine.welike.frameworkmvvm.ViewModelProviders
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.bean.RegisterAndLoginModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.browse_main_layout.view.*
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


@PageName("BrowseMainPage")
class BrowseMainPage : BaseBrowseLifecyclePage {


    lateinit var rootView: View

    lateinit var mFragmentSwitcher: BrowseFragmentPageSwitcher

    private var mLastSelectView: View? = null
    private var mLastSelectTabView: TextView? = null

    lateinit var mainViewModel: BrowseMainViewModel

    var isAnimationing = false


    private var mainBottomTabHome: ConstraintLayout? = null
    private var mainBottomTabDiscover: ConstraintLayout? = null
    private var clLoginTab: ConstraintLayout? = null
    private var homeTabTitle: TextView? = null
    private var messageTabTitle: TextView? = null
    private var discoveryTabTitle: TextView? = null
    private var loginView: View? = null

    constructor(stackManager: BaseLifecyclePageStackManager?, config: PageConfig?, cache: PageCache?) : super(stackManager, config, cache) {

    }


    override fun createPageView(container: ViewGroup, saveState: Bundle?): View {

        rootView = mPageStackManager.layoutInflater.inflate(R.layout.browse_main_layout, null)
        rootView.fitsSystemWindows = true
        mFragmentSwitcher = BrowseFragmentPageSwitcher(pageStackManager)
        mainViewModel = ViewModelProviders.of(this).get<BrowseMainViewModel>(BrowseMainViewModel::class.java)
        mFragmentSwitcher.setMainViewModel(mainViewModel)

        rootView.cl_browse_bottom.addView(mPageStackManager.layoutInflater.inflate(R.layout.browse_bottom_layout_1, rootView.cl_browse_bottom, false))
//
        val cl = rootView.main_page_content.layoutParams as ConstraintLayout.LayoutParams

        cl.bottomMargin = ScreenUtils.dip2Px(container.context, 48f)

        rootView.main_page_content.layoutParams = cl

        mainBottomTabHome = rootView.findViewById(R.id.main_bottom_tab_home) as ConstraintLayout
        mainBottomTabDiscover = rootView.findViewById(R.id.main_bottom_tab_discover) as ConstraintLayout
        clLoginTab = rootView.findViewById(R.id.cl_login_tab) as ConstraintLayout
        messageTabTitle = rootView.findViewById(R.id.home_tab_message_title) as TextView
        homeTabTitle = rootView.findViewById(R.id.home_tab_title) as TextView
        discoveryTabTitle = rootView.findViewById(R.id.home_tab_discover_title) as TextView
        loginView =  rootView.findViewById(R.id.ll_bottom_login) as View


        mainBottomTabHome?.setOnClickListener {
            selectTab(BrowseFragmentPageSwitcher.FRAGMENT_HOME_POSITION)
        }
        mainBottomTabDiscover?.setOnClickListener {
            EventLog.UnLogin.report19()
            selectTab(BrowseFragmentPageSwitcher.FRAGMENT_DISCOVER_POSITION)
        }


        val index = BrowseFragmentPageSwitcher.FRAGMENT_HOME_POSITION

        selectTab(index)


        mainViewModel.tabStatus.observe(this, Observer<Boolean> {
            if (it == null) return@Observer


            if (isAnimationing) return@Observer


            when (it) {
                true -> {
                    if (rootView.cl_browse_bottom.visibility != View.VISIBLE) {
                        isAnimationing = true
                        rootView.cl_browse_bottom.visibility = View.VISIBLE

                        showUpAnimation()
                    }
                }

                false -> {
                    if (rootView.cl_browse_bottom.visibility != View.GONE) {
                        isAnimationing = true
                        rootView.gradient_view.visibility = View.GONE
                        showDownAnimation()
                    }
                }
            }

        })

        mainViewModel.getmSnakeBarShouldShow().observe(this, Observer {
            when (it) {
                true -> {
                    ActionSnackBar.getInstance().showLoginSnackBar(rootView,
                            ResourceTool.getString("common_continue_by_login"),
                            ResourceTool.getString("common_login"), 3000, 0) {
                        // TODO: 2018/7/11
                    }
                }
            }
        })
        if (BrowseManager.isHomeTest()) {
            (rootView.findViewById(R.id.tv_lottie_login) as TextView).text = rootView.context.getString(R.string.post)

            clLoginTab?.setOnClickListener {
                PublishPostStarter.startActivityFromBrowseMain(it.context)
                return@setOnClickListener
            }
        }


        loginView?.setOnClickListener {

            BrowseSchemeManager.getInstance().clear()
            EventLog.UnLogin.report15()

            if (HalfLoginManager.getInstancce().isExistAccount) {
                val bundle = Bundle()
                bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LOGIN_BUTTON))
                EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_LOGIN_PAGE, bundle))
            } else {
                val bundle = Bundle()
                bundle.putSerializable(RegisteredConstant.KEY_EVENT_MODEL, RegisterAndLoginModel(EventLog.RegisterAndLogin.PageSource.LOGIN_BUTTON))
                EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_SIGN_LOGIN_PAGE, bundle))
            }
        }
        return rootView
    }

    private fun showDownAnimation() {
        val anim1 = ObjectAnimator.ofFloat(rootView.cl_browse_bottom, "translationY", 0f, ScreenUtils.dip2Px(rootView.context, 48f).toFloat())
        anim1.interpolator = (DecelerateInterpolator())
        val animSet = AnimatorSet()
        animSet.play(anim1)
        animSet.duration = 400
        animSet.start()

        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isAnimationing = false
                rootView.cl_browse_bottom.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {
                isAnimationing = false
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
    }

    private fun showUpAnimation() {

        val anim2 = ObjectAnimator.ofFloat(rootView.cl_browse_bottom, "translationY", ScreenUtils.dip2Px(rootView.context, 48f).toFloat(), 0f)
        anim2.interpolator = AccelerateInterpolator()
        val animSet = AnimatorSet()
        animSet.play(anim2)
        animSet.duration = 400
        animSet.start()
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isAnimationing = false
                rootView.gradient_view.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
                isAnimationing = false
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
    }


    private fun selectTab(index: Int) {
        if (mLastSelectView != null) {
            mLastSelectView!!.isSelected = false
            mLastSelectTabView!!.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)

        }
        when (index) {
            BrowseFragmentPageSwitcher.FRAGMENT_HOME_POSITION -> {
                if (mLastSelectView === mainBottomTabHome) {
                    // 选中状态下点击homeTab
                    mFragmentSwitcher.refreshTab(index)
                }
                mainBottomTabHome?.isSelected = true
                homeTabTitle?.isSelected = true
                mLastSelectView = mainBottomTabHome
                mLastSelectTabView = homeTabTitle
            }
            BrowseFragmentPageSwitcher.FRAGMENT_DISCOVER_POSITION -> {
                if (mLastSelectView === mainBottomTabDiscover) {
                    // 选中状态下点击discoverTab
                    mFragmentSwitcher.refreshTab(index)
                }
                mainBottomTabDiscover?.isSelected = true
                discoveryTabTitle?.isSelected = true

                mLastSelectView = mainBottomTabDiscover
                mLastSelectTabView = discoveryTabTitle
                AndroidSchedulers.mainThread().scheduleDirect({ MissionManager.notifyEvent(MissionType.DISCOVERY_TAB) }, 1, TimeUnit.SECONDS)
            }
        }

        mLastSelectTabView!!.typeface = Typeface.defaultFromStyle(Typeface.BOLD)

        mFragmentSwitcher.setCurrentItem(rootView.main_page_content, index)

    }

    override fun destroy(container: ViewGroup?) {
        super.destroy(container)
        mFragmentSwitcher.destroy(rootView.main_page_content)
    }

    override fun onPageStateChanged(oldPageState: Int, pageState: Int) {
        super.onPageStateChanged(oldPageState, pageState)
        mFragmentSwitcher.onPageStateChanged(oldPageState, pageState)
    }


    override fun onActivityPause() {
        mFragmentSwitcher.onActivityPause()
    }

    override fun onActivityResume() {
        mFragmentSwitcher.onActivityResume()
    }


}