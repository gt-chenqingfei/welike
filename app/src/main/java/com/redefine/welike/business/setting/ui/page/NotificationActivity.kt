package com.redefine.welike.business.setting.ui.page

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.widget.switchbtn.SwitchButton
import com.redefine.foundation.framework.Event
import com.redefine.welike.R
import com.redefine.welike.base.SpManager
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.business.mysetting.management.MutePushBean
import com.redefine.welike.business.mysetting.management.MutePushOptions
import com.redefine.welike.business.mysetting.ui.vm.NotificationOptionViewModel
import com.redefine.welike.kext.translate
import kotlinx.android.synthetic.main.notification_setting_page.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by mengnan on 2018/5/8.
 */
@Route(path = RouteConstant.PATH_NOTIFICATION_SETTING)
class NotificationActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener, SpManager.SPDataListener {

    private var mMuteStartTextTv: TextView? = null
    private var mMuteEndTextTv: TextView? = null
    private lateinit var mRepostSw: SwitchButton
    private lateinit var mCommentSw: SwitchButton
    private lateinit var mLikeSw: SwitchButton
    private lateinit var mImSw: SwitchButton
    private lateinit var mNewPostSw: SwitchButton
    private lateinit var mScheduledSw: SwitchButton
    private lateinit var mFollowSw: SwitchButton
    private lateinit var mImStangerSw: SwitchButton
    private var mMuteTime: LinearLayout? = null
    private var mScrollView: ScrollView? = null
    private var notificationViewModel: NotificationOptionViewModel? = null
    private val mutePushOptions by lazy { MutePushOptions() }
    private var mStartTime: String? = null
    private var mEndTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SpManager.getInstance().register(this)
        notificationViewModel = ViewModelProviders.of(this).get(NotificationOptionViewModel::class.java)
        setContentView(R.layout.notification_setting_page)
        initView()
    }

    fun initView() {
        findViewById<View>(R.id.iv_common_back).setOnClickListener {
            setOptionsFromSp()
            mStartTime = SpManager.Setting.getNotificationStartTime(applicationContext)
            mEndTime = SpManager.Setting.getNotificationEndTime(applicationContext)
            val limitTime = "$mStartTime-$mEndTime"
            mutePushOptions.timeLimit = limitTime
            notificationViewModel?.changeOption(mutePushOptions)
            finish()
        }
        findViewById<TextView>(R.id.tv_common_title).text = "notification_setting".translate()
        tv_notification_repost.text = "mute_at".translate()
        tv_notification_comment.text = "mute_comment".translate()
        tv_notification_like.text = "mute_like".translate()
        tv_notification_im_message.text = "mute_im_message".translate()
        tv_notification_follow.text = "mute_follow".translate()
        tv_notification_stranger_im_message.text = "mute_stranger".translate()
        tv_notification_new_post.text = "mute_new_post".translate()
        tv_notification_scheduled.text = "mut_scheduled".translate()
        tv_mute_start.text = "mute_start".translate()
        tv_mute_end.text = "mute_end".translate()
        tv_notification_reminder_tips.text = "notification_reminder".translate()
        tv_notification_im_message_tips.text = "mute_im_message_tips".translate()
        tv_notification_new_post_tips.text = "mute_new_post_tips".translate()
        tv_notification_scheduled_tips.text = "mute_scheduled_tips".translate()

        mMuteStartTextTv = tv_mute_start_text
        mMuteEndTextTv = tv_mute_end_text

        mRepostSw = switch_notification_repost
        mCommentSw = switch_notification_comment
        mLikeSw = switch_notification_like
        mImSw = switch_notification_im_message
        mImStangerSw = switch_notification_stranger_im_message
        mFollowSw = switch_notification_follow
        mNewPostSw = switch_notification_new_post
        mScheduledSw = switch_notification_scheduled

        mMuteTime = ll_quite_time
        ll_quite_time.setOnClickListener {
            EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_QUITE_HOURS_PAGE, null))
        }
        mScrollView = push_scroll
        initSwitchButton()

        notificationViewModel?.getOption()

    }


    private fun initSwitchButton() {
        mutePushOptions.switchs.clear()

        val isMuteAt = SpManager.Setting.getNotificationMuteAt(applicationContext)
        mRepostSw.isChecked = isMuteAt
        mRepostSw.setOnCheckedChangeListener(this)


        val isMuteComment = SpManager.Setting.getNotificationMuteComment(applicationContext)
        mCommentSw.isChecked = isMuteComment
        mCommentSw.setOnCheckedChangeListener(this)
        val isMuteLike = SpManager.Setting.getNotificationMuteLike(applicationContext)
        mLikeSw.isChecked = isMuteLike
        mLikeSw.setOnCheckedChangeListener(this)
        val isMuteIm = SpManager.Setting.getNotificationMuteIm(applicationContext)
        mImSw.isChecked = isMuteIm
        mImSw.setOnCheckedChangeListener(this)

        val isMuteStangerIm = SpManager.Setting.getNotificationMuteStagnerIm(applicationContext)
        mImStangerSw.isChecked = isMuteStangerIm
        mImStangerSw.setOnCheckedChangeListener(this)
        val isMuteFollow = SpManager.Setting.getNotificationMuteFollow(applicationContext)
        mFollowSw.isChecked = isMuteFollow
        mFollowSw.setOnCheckedChangeListener(this)

        val isMuteNewPost = SpManager.Setting.getNotificationMuteNewPost(applicationContext)
        mNewPostSw.isChecked = isMuteNewPost
        mNewPostSw.setOnCheckedChangeListener(this)
        val isScheduled = SpManager.Setting.getNotificationMuteScheduled(applicationContext)
        mScheduledSw.isChecked = isScheduled
        mScheduledSw.setOnCheckedChangeListener(this)

        changeMuteTimeVisibility(isScheduled)


        val atBean = MutePushBean()
        atBean.value = if (isMuteAt) "1" else "2"

        val comBean = MutePushBean()
        comBean.value = if (isMuteComment) "1" else "2"

        val likeBean = MutePushBean()
        likeBean.value = if (isMuteLike) "1" else "2"

        val friendBean = MutePushBean()
        friendBean.value = if (isMuteIm) "1" else "2"

        val strangerBean = MutePushBean()
        strangerBean.value = if (isMuteStangerIm) "1" else "2"

        val newpostBean = MutePushBean()
        newpostBean.value = if (isMuteNewPost) "1" else "2"

        val timeLinmitBean = MutePushBean()
        timeLinmitBean.value = if (isScheduled) "1" else "2"

        val newFollow = MutePushBean()
        newFollow.value = if (isMuteFollow) "1" else "2"


        notificationViewModel?.apply {
            muteAt.observe(this@NotificationActivity, Observer { aBoolean ->
                if (aBoolean == null) return@Observer

                if (aBoolean != mRepostSw.isChecked) {
                    mRepostSw.isChecked = aBoolean
                    SpManager.Setting.setNotificationMuteAt(aBoolean, applicationContext)
                }
            })

            muteComment.observe(this@NotificationActivity, Observer { aBoolean ->
                if (aBoolean == null) return@Observer

                if (aBoolean != mCommentSw.isChecked) {
                    mCommentSw.isChecked = aBoolean
                    SpManager.Setting.setNotificationMuteComment(aBoolean, applicationContext)
                }
            })
            muteLike.observe(this@NotificationActivity, Observer { aBoolean ->
                if (aBoolean == null) return@Observer

                if (aBoolean != mLikeSw.isChecked) {
                    mLikeSw.isChecked = aBoolean
                    SpManager.Setting.setNotificationMuteLike(aBoolean, applicationContext)
                }
            })

            muteFriend.observe(this@NotificationActivity, Observer { aBoolean ->
                if (aBoolean == null) return@Observer

                if (aBoolean != mImSw.isChecked) {
                    mImSw.isChecked = aBoolean
                    SpManager.Setting.setNotificationMuteIm(aBoolean, applicationContext)
                }
            })

            muteStranger.observe(this@NotificationActivity, Observer { aBoolean ->
                if (aBoolean == null) return@Observer

                if (aBoolean != mImStangerSw.isChecked) {
                    mImStangerSw.isChecked = aBoolean
                    SpManager.Setting.setNotificationMuteStangerIm(aBoolean, applicationContext)
                }
            })
            muteNewFollower.observe(this@NotificationActivity, Observer { aBoolean ->
                if (aBoolean == null) return@Observer

                if (aBoolean != mFollowSw.isChecked) {
                    mFollowSw.isChecked = aBoolean
                    SpManager.Setting.setNotificationMuteFollow(aBoolean, applicationContext)
                }
            })
            muteNewPost.observe(this@NotificationActivity, Observer { aBoolean ->
                if (aBoolean == null) return@Observer

                if (aBoolean != mNewPostSw.isChecked) {
                    mNewPostSw.isChecked = aBoolean
                    SpManager.Setting.setNotificationMuteNewPost(aBoolean, applicationContext)
                }
            })
            muteScheduled.observe(this@NotificationActivity, Observer { aBoolean ->
                if (aBoolean == null) return@Observer

                if (aBoolean != mScheduledSw.isChecked) {
                    mScheduledSw.isChecked = aBoolean
                    SpManager.Setting.setNotificationMuteScheduled(aBoolean, applicationContext)
                }
            })

            muteLimitTime.observe(this@NotificationActivity, Observer { s ->
                if (TextUtils.isEmpty(s)) return@Observer
                val str = s!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (str.size == 2) {
                    mStartTime = str[0]
                    mEndTime = str[1]
                    SpManager.Setting.setNotificationStartTime(mStartTime, applicationContext)
                    SpManager.Setting.setNotificationEndTime(mEndTime, applicationContext)
                    mMuteStartTextTv?.text = mStartTime
                    mMuteEndTextTv?.text = mEndTime

                }
            })
        }



        atBean.type = 1
        mutePushOptions.switchs.add(atBean)

        comBean.type = 2
        mutePushOptions.switchs.add(comBean)

        likeBean.type = 3
        mutePushOptions.switchs.add(likeBean)

        friendBean.type = 4
        mutePushOptions.switchs.add(friendBean)

        strangerBean.type = 5
        mutePushOptions.switchs.add(strangerBean)

        newpostBean.type = 6
        mutePushOptions.switchs.add(newpostBean)

        timeLinmitBean.type = 7
        mutePushOptions.switchs.add(timeLinmitBean)

        newFollow.type = 8
        mutePushOptions.switchs.add(newFollow)

        val startTime = SpManager.Setting.getNotificationStartTime(applicationContext)
        val endTime = SpManager.Setting.getNotificationEndTime(applicationContext)
        val limitTime = "$startTime-$endTime"
        mutePushOptions.timeLimit = limitTime
        mutePushOptions.timeZone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
    }

    private fun changeMuteTimeVisibility(isShow: Boolean) {
        if (isShow) {
            showMuteTime()
        } else {
            hideMuteTime()
        }
    }

    private fun showMuteTime() {
        val startTime = SpManager.Setting.getNotificationStartTime(applicationContext)
        val endTime = SpManager.Setting.getNotificationEndTime(applicationContext)
        mMuteStartTextTv?.text = startTime
        mMuteEndTextTv?.text = endTime
        mMuteTime?.visibility = View.VISIBLE
        Handler().post { mScrollView!!.fullScroll(ScrollView.FOCUS_DOWN) }

    }

    private fun hideMuteTime() {
        mMuteTime?.visibility = View.GONE
    }

    private fun setOptionsFromSp() {
        mutePushOptions.switchs[0].value = if (SpManager.Setting.getNotificationMuteAt(applicationContext)) "1" else "2"
        mutePushOptions.switchs[1].value = if (SpManager.Setting.getNotificationMuteComment(applicationContext)) "1" else "2"
        mutePushOptions.switchs[2].value = if (SpManager.Setting.getNotificationMuteLike(applicationContext)) "1" else "2"
        mutePushOptions.switchs[3].value = if (SpManager.Setting.getNotificationMuteIm(applicationContext)) "1" else "2"
        mutePushOptions.switchs[4].value = if (SpManager.Setting.getNotificationMuteStagnerIm(applicationContext)) "1" else "2"
        mutePushOptions.switchs[5].value = if (SpManager.Setting.getNotificationMuteNewPost(applicationContext)) "1" else "2"
        mutePushOptions.switchs[6].value = if (SpManager.Setting.getNotificationMuteScheduled(applicationContext)) "1" else "2"
        mutePushOptions.switchs[7].value = if (SpManager.Setting.getNotificationMuteFollow(applicationContext)) "1" else "2"

    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        setOptionsFromSp()
        when (buttonView.id) {
            R.id.switch_notification_repost -> {
                mutePushOptions.switchs[0].value = if (isChecked) "1" else "2"
                SpManager.Setting.setNotificationMuteAt(isChecked, applicationContext)
            }
            R.id.switch_notification_comment -> {
                mutePushOptions.switchs[1].value = if (isChecked) "1" else "2"
                SpManager.Setting.setNotificationMuteComment(isChecked, applicationContext)
            }
            R.id.switch_notification_like -> {
                mutePushOptions.switchs[2].value = if (isChecked) "1" else "2"
                SpManager.Setting.setNotificationMuteLike(isChecked, applicationContext)
            }
            R.id.switch_notification_im_message -> {
                mutePushOptions.switchs[3].value = if (isChecked) "1" else "2"
                SpManager.Setting.setNotificationMuteIm(isChecked, applicationContext)
            }
            R.id.switch_notification_new_post -> {
                mutePushOptions.switchs[5].value = if (isChecked) "1" else "2"
                SpManager.Setting.setNotificationMuteNewPost(isChecked, applicationContext)
            }
            R.id.switch_notification_scheduled -> {
                changeMuteTimeVisibility(isChecked)
                mutePushOptions.switchs[6].value = if (isChecked) "1" else "2"
                SpManager.Setting.setNotificationMuteScheduled(isChecked, applicationContext)
            }
            R.id.switch_notification_stranger_im_message -> {
                mutePushOptions.switchs[4].value = if (isChecked) "1" else "2"
                SpManager.Setting.setNotificationMuteStangerIm(isChecked, applicationContext)
            }
            R.id.switch_notification_follow -> {
                mutePushOptions.switchs[7].value = if (isChecked) "1" else "2"
                SpManager.Setting.setNotificationMuteFollow(isChecked, applicationContext)
            }
            else -> {
            }
        }
        notificationViewModel?.changeOption(mutePushOptions)

    }

    override fun onSPDataChanged(spTypeName: String, spKeyName: String) {
        if (TextUtils.equals(spTypeName, SpManager.sharePreferencesSettingName) && TextUtils.equals(spKeyName, SpManager.notificationStartTime)) {
            val startTime = SpManager.Setting.getNotificationStartTime(applicationContext)
            mMuteStartTextTv?.text = startTime
        }

        if (TextUtils.equals(spTypeName, SpManager.sharePreferencesSettingName) && TextUtils.equals(spKeyName, SpManager.notificationEndTime)) {
            val endTime = SpManager.Setting.getNotificationEndTime(applicationContext)
            mMuteEndTextTv?.text = endTime
        }
        setOptionsFromSp()
        mStartTime = SpManager.Setting.getNotificationStartTime(applicationContext)
        mEndTime = SpManager.Setting.getNotificationEndTime(applicationContext)
        val limitTime = "$mStartTime-$mEndTime"
        mutePushOptions.timeLimit = limitTime
        //local control
    }

    override fun onDestroy() {
        super.onDestroy()
        SpManager.getInstance().unregister(this)
    }
}
