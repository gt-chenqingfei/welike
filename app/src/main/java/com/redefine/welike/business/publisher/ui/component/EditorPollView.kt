package com.redefine.welike.business.publisher.ui.component

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.redefine.commonui.dialog.MenuItem
import com.redefine.commonui.dialog.MenuItemIdConstant
import com.redefine.commonui.dialog.SimpleMenuDialog
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.R
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.base.util.TimeUtil
import com.redefine.welike.business.feeds.management.bean.PollItemInfo
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.ui.viewholder.PublishPollImageTextViewHolder
import com.redefine.welike.business.publisher.ui.viewholder.PublishPollTextViewHolder
import com.redefine.welike.business.publisher.viewmodel.PublishPostViewModel
import com.redefine.welike.commonui.event.commonenums.BooleanValue
import com.redefine.welike.statistical.EventLog1
import kotlinx.android.synthetic.main.layout_publish_post_editor_poll.view.*
import java.util.*

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */

@LayoutResource(layout = R.layout.layout_publish_post_editor_poll)
class EditorPollView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs),
        View.OnClickListener, PublishPollImageTextViewHolder.PollItemChangeListener {

    private var isShowPollPicLayout: Boolean = false
    private var mPollImageGridViewHolder: PublishPollImageTextViewHolder? = null
    private var mPollTextViewHolder: PublishPollTextViewHolder? = null
    private var mViewModel: PublishPostViewModel? = null

    override fun onCreateView() {
        mViewModel = ViewModelProviders.of(activityContext).get(PublishPostViewModel::class.java)
        mViewModel?.let { vm ->
            vm.mPollLiveData.observe(activityContext, android.arch.lifecycle.Observer {
                updatePollLayout(vm.pollItems)
                updateExpireTime()
            })
        }

        mPollImageGridViewHolder = PublishPollImageTextViewHolder(activityContext, this)
        mPollTextViewHolder = PublishPollTextViewHolder(activityContext, this)

        publish_poll_add_btn.setOnClickListener(this)
        publish_poll_validity_time_btn.setOnClickListener(this)

        doSwitchPollText()
    }

    private fun updateExpireTime() {
        val viewModel = PublishAnalyticsManager.getInstance().obtainCurrentModel()
        val expireTime = mViewModel?.getPollExpireTime()
        if (expireTime == TimeUtil.DAY_1) {
            publish_poll_validity_time.setText(R.string.one_day)
            PublisherEventManager.INSTANCE.poll_time = 1
            viewModel.pollTime = EventLog1.Publish.PollTime.ONE_DAY
        } else if (expireTime == TimeUtil.DAY_1 * 3) {
            publish_poll_validity_time.setText(R.string.three_day)
            PublisherEventManager.INSTANCE.poll_time = 2
            viewModel.pollTime = EventLog1.Publish.PollTime.THREE_DAY
        } else if (expireTime == TimeUtil.DAY_1 * 7) {
            publish_poll_validity_time.setText(R.string.seven_day)
            PublisherEventManager.INSTANCE.poll_time = 3
            viewModel.pollTime = EventLog1.Publish.PollTime.ONE_WEEK
        } else if (expireTime == TimeUtil.DAY_1 * 30) {
            publish_poll_validity_time.setText(R.string.one_month)
            PublisherEventManager.INSTANCE.poll_time = 4
            viewModel.pollTime = EventLog1.Publish.PollTime.ONE_MONTH
        } else if (expireTime == -1L) {
            publish_poll_validity_time.setText(R.string.no_time_limit)
            PublisherEventManager.INSTANCE.poll_time = 5
            viewModel.pollTime = EventLog1.Publish.PollTime.NO_LIMIT
        }
    }

    private fun updatePollLayout(pollItemInfos: List<PollItemInfo>?) {
        pollItemInfos ?: return

        if (CollectionUtil.isEmpty(pollItemInfos)) {
            this.visibility = View.GONE
            return
        }

        this.visibility = View.VISIBLE
        var hasPic = false
        for (pollItemInfo in pollItemInfos) {
            if (!TextUtils.isEmpty(pollItemInfo.pollItemPic)) {
                hasPic = true
                break
            }
        }
        if (hasPic) {
            switchToPollPicLayout(pollItemInfos)
        } else {
            switchToPollTextLayout(pollItemInfos)
        }

        disableAddBtn(CollectionUtil.getCount(pollItemInfos) >= GlobalConfig.MAX_POLL_ITEM_SIZE)
    }

    private fun switchToPollPicLayout(pollItemInfos: List<PollItemInfo>) {
        switchToPollPicLayout()
        updatePollItems(pollItemInfos)
        PublisherEventManager.INSTANCE.poll_type = 2
        PublishAnalyticsManager.getInstance().obtainCurrentModel().poolType = BooleanValue.YES

    }

    private fun switchToPollTextLayout(pollItemInfos: List<PollItemInfo>) {
        switchToPollTextLayout()
        updatePollItems(pollItemInfos)
        PublisherEventManager.INSTANCE.poll_type = 1
        PublishAnalyticsManager.getInstance().obtainCurrentModel().poolType = BooleanValue.NO
    }


    private fun switchToPollPicLayout() {
        if (isShowPollPicLayout) {
            return
        }
        isShowPollPicLayout = true
        publish_poll_item_container.removeAllViews()
        mPollImageGridViewHolder?.bindGroup(publish_poll_item_container)
    }

    private fun switchToPollTextLayout() {
        if (!isShowPollPicLayout) {
            return
        }
        doSwitchPollText()
    }

    private fun doSwitchPollText() {
        isShowPollPicLayout = false
        publish_poll_item_container.removeAllViews()
        mPollTextViewHolder?.bindGroup(publish_poll_item_container)
    }

    private fun updatePollItems(pollItemInfos: List<PollItemInfo>) {
        if (isShowPollPicLayout) {
            mPollImageGridViewHolder?.bindViews(pollItemInfos)
        } else {
            mPollTextViewHolder?.bindViews(pollItemInfos)
        }
    }

    override fun onClick(v: View) {
        if (v === publish_poll_add_btn) {
//            mView.onAddPollItem()
            mViewModel?.addPoll()
//            cleanGuide()
        } else if (v === publish_poll_validity_time_btn) {
            val menuItems = ArrayList<MenuItem>()
            menuItems.add(MenuItem(MenuItemIdConstant.MENU_ITEM_ONE_DAY, ResourceTool.getString("one_day")))
            menuItems.add(MenuItem(MenuItemIdConstant.MENU_ITEM_THREE_DAY, ResourceTool.getString("three_day")))
            menuItems.add(MenuItem(MenuItemIdConstant.MENU_ITEM_SEVEN_DAY, ResourceTool.getString("seven_day")))
            menuItems.add(MenuItem(MenuItemIdConstant.MENU_ITEM_ONE_MONTH, ResourceTool.getString("one_month")))
            menuItems.add(MenuItem(MenuItemIdConstant.MENU_ITEM_NO_LIMIT, ResourceTool.getString("no_time_limit")))

            SimpleMenuDialog.show(v.context, menuItems) { menuItem ->
                var expireTime = GlobalConfig.DEFAULT_POLL_EXPIRED_TIME
                if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_ONE_DAY) {
                    expireTime = TimeUtil.DAY_1
                } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_THREE_DAY) {
                    expireTime = TimeUtil.DAY_1 * 3
                } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_SEVEN_DAY) {
                    expireTime = TimeUtil.DAY_1 * 7
                } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_ONE_MONTH) {
                    expireTime = TimeUtil.DAY_1 * 30
                } else if (menuItem.menuId == MenuItemIdConstant.MENU_ITEM_NO_LIMIT) {
                    expireTime = -1
                }

                mViewModel?.updatePollExpireTime(expireTime)
                updateExpireTime()
            }
        }
    }

    private fun disableAddBtn(b: Boolean) {
        publish_poll_add_btn.visibility = if (b) View.GONE else View.VISIBLE
    }

    override fun onPollItemDelete(pollItemInfo: PollItemInfo?, position: Int) {
        pollItemInfo?.let {
            mViewModel?.removePoll(pollItemInfo, position)
        }
    }

    override fun onPollEditorChange(pollItemInfo: PollItemInfo, position: Int) {
        mViewModel?.updatePollItemText(pollItemInfo, position)
    }
}