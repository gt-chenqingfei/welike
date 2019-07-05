package com.redefine.welike.business.publisher.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.redefine.commonui.dialog.OnMenuItemClickListener
import com.redefine.richtext.RichContent
import com.redefine.richtext.processor.IRichEditTextProcessor
import com.redefine.welike.R
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.business.publisher.management.PublisherEventManager
import com.redefine.welike.business.publisher.management.bean.DraftBase
import com.redefine.welike.business.publisher.ui.activity.DraftActivity
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.ui.dialog.DraftSaveMenuDialog
import com.redefine.welike.business.publisher.viewmodel.AbsPublishViewModel
import kotlinx.android.synthetic.main.toolbar_publish_common.view.*

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */

@LayoutResource(layout = R.layout.toolbar_publish_common)
class ToolbarCommonView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs),
        View.OnClickListener {

    lateinit var mViewModel: AbsPublishViewModel<*>

    lateinit var richTextProcessor: IRichEditTextProcessor

    override fun onClick(v: View?) {
        when (v) {
            tv_btn_draft_save ->
                showDraftSaveDialog()

        }
    }

    override fun onCreateView() {
        tv_btn_draft_save.isEnabled = false
        initListener()
    }

    private fun initListener() {
        iv_btn_back.setOnClickListener(this)
        tv_btn_draft_save.setOnClickListener(this)
    }

    fun <T : DraftBase> attach(title: String, viewModel: AbsPublishViewModel<T>,
                               richTextProcessor: IRichEditTextProcessor) {
        mViewModel = viewModel
        tv_title.text = title
        this.richTextProcessor = richTextProcessor

//        mViewModel.mDraftSaveBtnStateLiveData.observe(activityContext, Observer {
//            tv_btn_draft_save.isEnabled = it ?: false
//        })
    }

    fun getSubmitText(isNeedFilterLineFlag: Boolean, summaryLimit: Int, contentLimit: Int):
            RichContent {
        val richContent = richTextProcessor.getRichContent(isNeedFilterLineFlag,
                summaryLimit, contentLimit)
//        calculateEventData(richContent)
        return richContent
    }

    private fun showDraftSaveDialog() {
        if (!AccountManager.getInstance().isLogin) {
            mViewModel.updateLoginState(false)
            return
        }

        DraftSaveMenuDialog(OnMenuItemClickListener {
            when (it?.menuId) {
                DraftSaveMenuDialog.MENU_ITEM_SAVE -> {
                    PublisherEventManager.INSTANCE.exit_state = 2
                    mViewModel.saveDraft(getSubmitText(false,
                            GlobalConfig.SUMMARY_LIMIT, GlobalConfig.FEED_MAX_LENGTH))
                    activityContext.finish()
                    DraftActivity.launch(activityContext)
                }
                DraftSaveMenuDialog.MENU_ITEM_DISCARD ->
                    activityContext.finish()

            }
        }, activityContext).showDialog()
    }
}