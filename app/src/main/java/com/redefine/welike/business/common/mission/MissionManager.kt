package com.redefine.welike.business.common.mission

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.animation.TranslateAnimation
import com.redefine.im.e
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.common.mission.MissionType.*
import com.redefine.welike.common.Life
import com.redefine.welike.commonui.widget.ArrowTextView
import com.redefine.welike.guide.Overlay
import com.redefine.welike.guide.ToolTip
import com.redefine.welike.guide.TourGuide
import com.redefine.welike.hive.CommonListener
import com.redefine.welike.statistical.EventLog

object MissionManager {
    private lateinit var sp: SharedPreferences
    /**
     * Current mission to listen.
     */
    var currentMission: MissionType? = null
    /**
     * Listening Missions.
     */
    var listening = HashSet<MissionType>()

    val liveMission = MutableLiveData<MissionType>()

    private var mePostFlag = true

    var forwardUrl = ""
    /**
     * Missions need Client Listen to report to complete.
     */
    private val reportListen = arrayOf(LIKE, DISCOVERY_TAB, HOT_FEED, RANK, BROWSE_OFFICIAL_ACTIVE,
            AVATAR, INTRODUCTION, TOPIC_NEW_WELIKER, TASK_LIST, COMMENT_NEW_WELIKER,
            LIKE_NEW_WELIKER, FOLLOW_NEW_WELIKER, COMMENT, SHARE_PROFILE, SHARE_POST, FORWARD)

    /**
     * Missions need Client Listen to show Guide.
     */
    private val guideListen = arrayOf(LIKE, DISCOVERY_TAB, HOT_FEED, RANK, BROWSE_OFFICIAL_ACTIVE,
            AVATAR, INTRODUCTION, TOPIC_NEW_WELIKER, TASK_LIST, COMMENT_NEW_WELIKER,
            SHARE_POST, FORWARD)

    fun init(context: Context) {
        sp = context.getSharedPreferences("GUIDE", Context.MODE_PRIVATE)
        Life.regListener { event ->
            if (event == Life.LIFE_LOGIN) {
                reset()
            }
        }
    }

    /**
     * 重制 Guide 的显示时机。
     */
    private fun reset() {
        mePostFlag = true
        sp.edit().clear().apply()
        listening.clear()
        currentMission = null
        fillMission = true
    }

    /**
     *  Invoked by Business.
     */
    fun notifyEvent(event: MissionType) {
        notifyEvent(event, null)
    }

    /**
     *  Invoked by Business.
     */
    fun notifyEvent(event: MissionType, listener: CommonListener<MissionTask>? = null) {
        e("notifyEvent = ${event.name}")
        if (event in listening) {
            listening.remove(event)
//            if (event == LIKE) {
//                val message = Message.obtain()
//                message.what = MessageIdConstant.MESSAGE_NOTIFY_ASSIGNMENT
//                EventBus.getDefault().post(message)
//                Log.e("DDAI", "post(message)")
//            } else {
//            }
            finishMission(event, listener)
        }
    }

    private var latestListenerHash = 0
    /**
     * Load Mission Info
     */
    fun loadMission(listener: CommonListener<MissionTask>? = null) {
        e("loadMission ")
//        listener?.let {
//            if (it.hashCode() == latestListenerHash) {
//                return
//            } else {
//                latestListenerHash = it.hashCode()
//            }
//        }
//        e("loadMission request")
//        AccountManager.getInstance().account?.let {
//            GetMissionRequest(MyApplication.getAppContext(), it.uid).req(object : RequestCallback {
//                override fun onError(request: BaseRequest?, errCode: Int) {
//                    listener?.onError(null)
//                    latestListenerHash = 0
//                }
//
//                override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
//                    latestListenerHash = 0
//                    result?.let {
//                        val detail = Gson().fromJson<MissionTask>(result.toJSONString(), MissionTask::class.java)
//                        listening.clear() // clean the missions .
//                        detail.taskDetail?.list?.forEach {
//                            if (!it.finish) {
//                                it.type?.let { addListen(it) }
//                            }
//                        } //listen all the Missions.
//                        detail.curTaskType?.let { new ->
//                            currentMission?.let { old ->
//                                if (old.value != new) {
//                                    MissionNotificationHelper.showNotification(old.value)
//                                    e("notifyEvent = ${old.name}>${getMissionType(new)?.name}")
//                                }
//                            }
//                            currentMission = getMissionType(new)
//                        }
//                        listener?.onFinish(detail) //callback to UI
//                        forwardUrl = detail.forwardUrl!!
//                    }
//                }
//            })
//        }

    }

    /**
     * for H5
     */
    fun startMission(type: Int, context: Context) {
        startMission(type, context, false)
    }

    /**
     * Invoked by click "start a mission"
     */
    fun startMission(type: Int, context: Context, app: Boolean) {
//        currentGuide = getMissionType(type)
////        currentGuide = BROWSE_OFFICIAL_ACTIVE
//        liveMission.postValue(currentGuide)
//        e("startMission ${currentGuide?.name}")
//        val schema = currentGuide?.let {
//            if (it == MissionType.SHARE_PROFILE) {
//                AccountManager.getInstance().account?.let {
//                    "${currentGuide?.scheme}&postId=${it.uid}&nickName=${it.nickName}&imageUrl=${it.headUrl}"
//                }
//            } else {
//                it.scheme
//            }
//        }
//        if (!app || type != MissionType.LIKE.value) {
//            DefaultUrlRedirectHandler(context).onUrlRedirect(schema)
//        }
    }

    /**
     * add mission to listen.
     */
    private fun addListen(type: Int) {
        MissionType.values().firstOrNull { it.value == type }?.let { addListen(it) }
    }

    private fun addListen(mission: MissionType) {
        if (mission in reportListen) {
            listening.add(mission)
        }
    }

    /**
     * Notify Server to check Mission complete.
     */
    private fun finishMission(mission: MissionType, listener: CommonListener<MissionTask>? = null) {
        e("finishMission ${mission?.name}")
//        AccountManager.getInstance().account?.let {
//            FinishRequest(MyApplication.getAppContext(), mission.value, it.uid).req(object : RequestCallback {
//                override fun onError(request: BaseRequest?, errCode: Int) {
//                }
//
//                override fun onSuccess(request: BaseRequest?, result: JSONObject?) {
//                    listening.remove(mission)
//                    currentGuide = null
//                    loadMission(listener)
//                }
//            })
//        }
    }

    fun sign() {
//        AccountManager.getInstance().account?.let {
//            SignRequest(it.uid, MyApplication.getAppContext()).req(null)
//        }
    }

    private fun getMissionType(type: Int): MissionType? = MissionType.values().firstOrNull { it.value == type }

    /**
     * Current Guide.
     */
    var currentGuide: MissionType? = null

    /**
     * check for missions.
     */
    fun checkGuide(mission: MissionType): Boolean {
        (mission == currentGuide).let {
            e("checkGuide $mission = $it")
            return it
        }
    }
//    fun checkGuide(mission: MissionType): Boolean = mission == HOT_FEED
//        return if (mission == DISCOVERY_TAB) {
//            e("checkGuide $mission = $mission")
//            true
//        } else {
//            false
//        }
//    }

    /**
     * check for common Tip
     */
    fun checkTip(tip: TipType): Boolean {
        return when (tip) {
            TipType.EDITOR_SHOW -> (sp.getInt(tip.name, 0) < 1)
            else -> false
        }
    }

//        return when (tip) {
////            TipType.HOME_VOTE_STUFF -> (currentGuide == null && (sp.getInt(tip.name, 0) < 1))
//            TipType.HOME_VOTE_STUFF -> false
////            TipType.DISCOVERY_SHARE -> (currentGuide == null && (sp.getInt(tip.name, 0) < 1))
//            TipType.DISCOVERY_SHARE -> false
//            TipType.ME_POST -> {
////                val account = AccountManager.getInstance().account
////                return if (account != null && account.postsCount < 1 && mePostFlag) {
////                    mePostFlag = false
////                    true
////                } else {
////                    false
////                }
//                false
//            }
//        }
//    }

//    fun checkTip(tip: TipType): Boolean  = true


    fun showTip(tip: TipType, guide: ArrowTextView, target: View, contentRes: String) {
        when (tip) {
//            TipType.HOME_VOTE_STUFF -> {
//                guide.setOnClickListener { v ->
//                    v.visibility = View.GONE
//                    currentGuide = null
//                }
//                guide.visibility = View.VISIBLE
//                guide.text = ("tip_vote".translate(USER))
//                guide.playUnder(target)
//            }
//            TipType.DISCOVERY_SHARE -> {
//                guide.setOnClickListener { v ->
//                    v.visibility = View.GONE
//                    currentGuide = null
//                }
//                guide.visibility = View.VISIBLE
//                guide.text = ("tip_share".translate(USER))
//                guide.playAbove(target)
//            }
//            TipType.EDITOR_SHOW -> {
//                guide.setOnClickListener { v ->
//                    v.visibility = View.GONE
//                }
//                guide.visibility = View.VISIBLE
//                guide.text = (contentRes.translate())
//                guide.playUnder(target)
//            }
        }
        val time = sp.getInt(tip.name, 0) + 1
        sp.edit().putInt(tip.name, time).apply()
    }

    fun showTip(tip: TipType) {
        val time = sp.getInt(tip.name, 0) + 1
        sp.edit().putInt(tip.name, time).apply()
    }

    fun showTip(tip: TipType, activity: Activity): TourGuide {
        return TourGuide.init(activity).with(TourGuide.Technique.CLICK).also {
            it.setToolTip(ToolTip()
                    .setGravity(Gravity.TOP)
                    .setShadow(true)
                    .setEnterAnimation(TranslateAnimation(0f, 0f, 0f, 0f).apply { fillAfter = true })
                    .setDescription(ResourceTool.getString(ResourceTool.ResourceFileEnum.USER, "guide_4")))
                    .setOverlay(Overlay()
                            .setBackgroundColor(Color.parseColor("#26000000"))
                            .setOnClickListener { v ->
                                it.cleanUp()
                                EventLog.Guide.report(tip.name)
                            }
                    )
        }
    }

    fun showGuide(mission: MissionType, _guide: ArrowTextView?, target: View) {
//        _guide?.let { guide ->
//            e("showGuide ${mission?.name}")
//            when (mission) {
//                LIKE -> {//	送出一个赞			↓点赞按钮
//                    guide.setOnClickListener { v ->
//                        v.visibility = View.GONE
//                        currentGuide = null
//                    }
//                    guide.visibility = View.VISIBLE
//                    guide.text = ("guide_like".translate(USER))
//                    guide.playAbove(target)
//                }
//                SHARE_POST -> {//	分享1条热门post到其他平台			↑分享按钮
//                    guide.setOnClickListener { v -> v.visibility = View.GONE }
//                    guide.text = ("guide_share_post".translate(USER))
//                    guide.visibility = View.VISIBLE
//                    guide.playUnder(target)
//                }
//                RANK -> {//	查看排行榜			↑排行榜
//                    guide.setOnClickListener { v -> v.visibility = View.GONE }
//                    guide.text = ("guide_rank".translate(USER))
//                    guide.visibility = View.VISIBLE
//                    guide.playUnder(target)
//                }
//                BROWSE_OFFICIAL_ACTIVE -> {//	浏览平台活动			↑banner
//                    guide.setOnClickListener { v -> v.visibility = View.GONE }
//                    guide.text = ("guide_banner".translate(USER))
//                    guide.visibility = View.VISIBLE
//                    guide.playUnder(target)
//                }
//                COMMENT_NEW_WELIKER -> {//	在#New Weliker中评论1个post			↓评论按钮
//                    guide.setOnClickListener { v -> v.visibility = View.GONE }
//                    guide.visibility = View.VISIBLE
//                    guide.text = ("guide_comment_new_weliker".translate(USER))
//                    guide.playAbove(target)
//                }
//                FORWARD -> {//	转发			↓转发按钮
//                    guide.setOnClickListener { v -> v.visibility = View.GONE }
//                    guide.visibility = View.VISIBLE
//                    guide.text = ("guide_forward".translate(USER))
//                    guide.playAbove(target)
//                }
//                AVATAR -> {//	完善头像			↑头像编辑
//                    guide.setOnClickListener { v -> v.visibility = View.GONE }
//                    guide.visibility = View.VISIBLE
//                    guide.text = ("guide_avatar".translate(USER))
//                    guide.playUnder(target)
//                }
//                INTRODUCTION -> {//	完善个人签名			↑签名编辑
//                    guide.setOnClickListener { v -> v.visibility = View.GONE }
//                    guide.visibility = View.VISIBLE
//                    guide.text = ("guide_introduce".translate(USER))
//                    guide.playAbove(target)
//                }
//                TASK_LIST -> {//	查看任务列表			↓任务列表
//                    guide.setOnClickListener { v -> v.visibility = View.GONE }
//                    guide.visibility = View.VISIBLE
//                    guide.text = ("guide_task_list".translate(USER))
//                    guide.playAbove(target)
//                }
//                HOT_FEED -> {//	查看热门微博			滚动tip
//                    guide.setOnClickListener { v -> v.visibility = View.GONE }
//                    guide.visibility = View.VISIBLE
//                    guide.text = ("guide_hot_feed".translate(USER))
//                    guide.play(target, 0)
//                }
//            }
//        }
//        if (mission != LIKE) {
//            currentGuide = null
//        }
    }

    fun showGuide(mission: MissionType, guide: View) {
//        if (mission == TOPIC_NEW_WELIKER) {//	发布一条#New Weliker 的post			Cover
//            e("showGuide ${mission?.name}")
//            guide.setOnClickListener { }
//            guide.visibility = View.VISIBLE
//            guide.guide_tip1.text = Html.fromHtml("guide_cover_1_1".translate(USER))
//            guide.guide_tip2.text = Html.fromHtml("guide_cover_1_2".translate(USER))
//            guide.guide_tip3.text = Html.fromHtml("guide_cover_1_3".translate(USER))
//            guide.guide_tip4.text = Html.fromHtml("guide_cover_1_4".translate(USER))
//            val ok = guide.findViewById<TextView>(R.id.guide_ok)
//            ok.text = "common_ok".translate()
//            ok.setOnClickListener {
//                guide.visibility = View.GONE
//                MissionManager.hideGuide(MissionType.TOPIC_NEW_WELIKER)
//            }
//        }
    }

    fun showGuide(mission: MissionType, activity: Activity) {
//        if (mission == DISCOVERY_TAB) {//	探索发现页			Cover
//            e("showGuide ${mission?.name}")
//            val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
//            val contentArea = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
//            val guideView = activity.layoutInflater.inflate(R.layout.guide_cover_discover, null)
//            guideView.setOnClickListener { }
//            guideView.text1.text = "guide_cover_2_1".translate(USER)
//            guideView.text2.text = "guide_cover_2_2".translate(USER)
//            guideView.text3.text = "guide_cover_2_3".translate(USER)
//            guideView.guide_ok2.text = "common_ok".translate()
//            guideView.guide_ok2.setOnClickListener { contentArea.removeView(guideView) }
////            guideView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
////                override fun onGlobalLayout() {
////                    guideView.viewTreeObserver.removeOnGlobalLayoutListener(this)
////                    if ((guideView.y + guideView.height) > contentArea.height) {
////                        guideView.image3.visibility = View.GONE
////                    }
////                }
////            })
//            contentArea.addView(guideView, layoutParams)
//        }
    }

    fun hideGuide(mission: MissionType) {
        e("hideGuide ${mission?.name}")
        if (currentGuide != null && currentGuide == mission) {
            currentGuide = null
        }
    }


    private var fillMission = true

    fun fillMission(): Boolean {
        return fillMission
    }

    fun setFillMission(fillMission: Boolean) {
        this.fillMission = fillMission
    }

}