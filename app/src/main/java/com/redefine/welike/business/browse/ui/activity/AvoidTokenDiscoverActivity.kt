package com.redefine.welike.business.browse.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.FrameLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.pekingese.pagestack.framework.IPageStackManager
import com.pekingese.pagestack.framework.config.PageConfig
import com.redefine.commonui.activity.BaseActivity
import com.redefine.foundation.framework.Event
import com.redefine.shortcutbadger.ShortcutBadger
import com.redefine.welike.base.SpManager
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.base.track.LogEvent
import com.redefine.welike.business.browse.ui.page.BrowseHomePage
import com.redefine.welike.business.browse.ui.page.BrowseMainPage
import com.redefine.welike.common.BrowseManager
import com.redefine.welike.commonui.event.expose.MainTabDisplayManager
import com.redefine.welike.commonui.framework.PageStackManager
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.event.*
import com.redefine.welike.push.PushIntentWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * Created by honlin on 2018/6/20.
 *
 * no login discover , only hot & latest ,only vidmate
 *
 */
@Route(path = RouteConstant.AVOID_ROUTE_PATH)
class AvoidTokenDiscoverActivity : BaseActivity() {


    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, AvoidTokenDiscoverActivity::class.java)
            context.startActivity(intent)
        }

        fun launch(context: Context, bundle: Bundle) {
            val intent = Intent(context, AvoidTokenDiscoverActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    private var mRootView: FrameLayout? = null

    private var mPageStackManager: IPageStackManager? = null

    private var mPageEventDispatcher: IEventDispatcher? = null

    private var mLogEventDispatcher: ILogDispatcher? = null

    private var mBackPressedCount: Byte = 0
    private var mExitTimer: ExitCountDownTimer? = null

    private var mRouteDispatcher: IRouteDispatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        initEvent()

        if (BrowseManager.isHomeTest())
            mPageStackManager?.pushPage(PageConfig.Builder(BrowseMainPage::class.java).setPageBundle(null).build())
        else {
            mPageStackManager?.pushPage(PageConfig.Builder(BrowseHomePage::class.java).setPageBundle(null).build())
        }

        EventBus.getDefault().register(this)

        mRouteDispatcher = AvoidRouteDispatcher(mPageStackManager)

        AndroidSchedulers.mainThread().scheduleDirect({ mRouteDispatcher?.handleRouteMessage(intent) }, 500, TimeUnit.MILLISECONDS);

        val lastShowSelfStartTime = SpManager.Setting.getNotificationSelfStartTime(this)
        val isTimeAllowShowSelfStart = System.currentTimeMillis() - lastShowSelfStartTime > 60 * 60 * 1000

        if (!SpManager.Setting.getNotificationSelfStart(this) && SpManager.Setting.getNotificationSelfStartCount(this) < 2 && isTimeAllowShowSelfStart) {
            PushIntentWrapper.whiteListMatters(this, "")
        }


    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        mRouteDispatcher?.handleRouteMessage(intent)
    }

    private fun initView() {
        mRootView = FrameLayout(this)
        mRootView?.clipToPadding = false
        mRootView?.fitsSystemWindows = true
        setContentView(mRootView)
    }

    private fun initEvent() {
        mPageStackManager = PageStackManager(this)
        mPageStackManager?.bindPageStack(mRootView)
        mPageEventDispatcher = PageEventDispatcher(mPageStackManager)
        mLogEventDispatcher = LogEventDispatcher()
    }


    override fun onBackPressed() {
        when (mPageStackManager?.onBackPressed()) {
            false -> {
                if (mExitTimer == null) {
                    mExitTimer = ExitCountDownTimer(2000, 500)
                }
                mExitTimer?.start()

                mBackPressedCount++
                if (mBackPressedCount < 2) {
                    ToastUtils.showShort(ResourceTool.getString("click_back_again"))
                } else {
                    super.onBackPressed()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        if (event.id == EventIdConstant.CLEAR_ACTIVITY_STACK_4_LOGIN) {
            mPageEventDispatcher = null
            finish()
        } else
            mPageEventDispatcher?.handleEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogEvent(logEvent: LogEvent) {
        mLogEventDispatcher?.handleLogMessage(logEvent)
    }

    override fun onPause() {
        mPageStackManager?.onActivityPause()
        super.onPause()
        MainTabDisplayManager.getInstance().onMainActivityPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mPageStackManager?.onActivitySaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mPageStackManager?.onActivityResume()
        MainTabDisplayManager.getInstance().onMainActivityResume()
    }

    override fun onStart() {
        super.onStart()
        mPageStackManager?.onActivityStart()
        ShortcutBadger.removeCount(this@AvoidTokenDiscoverActivity)
    }

    override fun onStop() {
        super.onStop()
        mPageStackManager?.onActivityStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPageStackManager?.onActivityDestroy()
        EventBus.getDefault().unregister(this)
    }


    private inner class ExitCountDownTimer
    /**
     * @param millisInFuture    The number of millis in the future from the call
     * to [.start] until the countdown is done and [.onFinish]
     * is called.
     * @param countDownInterval The interval along the way to receive
     * [.onTick] callbacks.
     */
    (millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            mBackPressedCount = 0
        }
    }
}