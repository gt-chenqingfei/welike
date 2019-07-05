package com.redefine.welike.business.publisher.ui.component

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.im.threadUIDelay
import com.redefine.welike.R
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.common.mission.MissionManager
import com.redefine.welike.business.common.mission.TipType
import com.redefine.welike.business.publisher.api.bean.CommonTopic
import com.redefine.welike.business.publisher.api.bean.SuperTopic
import com.redefine.welike.business.publisher.api.bean.Topic
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.ui.dialog.OnSuperTopicChoiceListener
import com.redefine.welike.business.publisher.ui.dialog.SuperTopicChosenDialog
import com.redefine.welike.business.publisher.viewmodel.PublishPostViewModel
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import kotlinx.android.synthetic.main.layout_publish_post_editor_topic.view.*

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */

@LayoutResource(layout = R.layout.layout_publish_post_editor_topic)
class EditorTopicView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs)
        , OnSuperTopicChoiceListener {

    private lateinit var mViewModel: PublishPostViewModel
    var superTopicCount: Int = 0
    override fun onCreateView() {
        mViewModel = ViewModelProviders.of(activityContext).get(PublishPostViewModel::class.java)

        publish_add_topic.setOnClickListener {
            performTopicClick()
        }

        publish_add_topic_tip.setOnClickListener {
            publish_add_topic_tip.visibility = View.GONE
        }

        mViewModel.mMenuStateLiveData.observe(activityContext, Observer { menuState ->

            menuState?.let {
                superTopicCount = it.superTopicCount
                publish_add_topic.isSelected = it.topicCount >= GlobalConfig.FEED_MAX_TOPIC
                if (mViewModel.isRestore) {
                    publish_add_topic_tip.visibility = View.GONE
                }
            }
        })

        if (MissionManager.checkTip(TipType.EDITOR_SHOW)) {
            publish_add_topic_tip.visibility = View.VISIBLE
            MissionManager.showTip(TipType.EDITOR_SHOW)
        } else {
            publish_add_topic_tip.visibility = View.GONE
        }

    }

    fun hideTip() {
        publish_add_topic_tip.visibility = View.GONE
    }

    fun performTopicClick() {

        if (publish_add_topic.isSelected) {
            ToastUtils.showShort(ResourceTool.getString("topic_count_over_limit"))
            return
        }

        hideTip()
        InputMethodUtil.hideInputMethod(activityContext.currentFocus)
        SuperTopicChosenDialog.showDialog(context as AppCompatActivity, this, superTopicCount == 0)
        PublishAnalyticsManager.getInstance().obtainCurrentModel().topicSource = EventLog1.Publish.TopicSource.FROM_POSTER

        PublishAnalyticsManager.getInstance().obtainCurrentModel().proxy.report17()
    }

    override fun onSuperTopicChoice(topic: Topic?) {
        topic ?: return

        var commonTopicId: String? = null
        var superTopicId: String? = null
        if (topic is SuperTopic) {
            mViewModel.updateSuperTopic(topic)
            superTopicId = topic.id
        } else if (topic is CommonTopic) {
            mViewModel.updateCommonTopic(topic = topic)
            commonTopicId = topic.id
        }

        threadUIDelay(200) {
            InputMethodUtil.showInputMethod(activityContext.currentFocus)
        }
        EventLog.Publish.report25(commonTopicId, superTopicId, PublisherEventManager.INSTANCE.add_topic_type)

    }

}