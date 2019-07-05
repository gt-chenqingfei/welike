package com.redefine.welike.business.publisher.ui.component

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import com.redefine.foundation.utils.CollectionUtil
import com.redefine.welike.R
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.viewmodel.PublishPostViewModel
import kotlinx.android.synthetic.main.layout_publish_post_editor_container.view.*
import kotlinx.android.synthetic.main.layout_publish_post_editor_input.view.*

/**
 * @author qingfei.chen
 * @date 2018/11/8
 * Copyright (C) 2018 redefine , Inc.
 */
@LayoutResource(layout = R.layout.layout_publish_post_editor_container)
class EditorPostContainer(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs), View.OnTouchListener {
    private lateinit var mViewModel: PublishPostViewModel

    override fun onCreateView() {
        editor_scroll_view.setOnTouchListener(this)
        mViewModel = ViewModelProviders.of(activityContext).get(PublishPostViewModel::class.java)
        et_publish_post_editor_component.attach(editor_post_text_limit_warn, mViewModel)

        mViewModel.mScrollToBottomLiveData.observe(activityContext, Observer {
            editor_scroll_view.fullScroll(ScrollView.FOCUS_DOWN)
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (!CollectionUtil.isEmpty(mViewModel.mPollLiveData.value)) {
            // 当前在投票状态
            return false
        }
        if (event?.action == MotionEvent.ACTION_UP
                || event?.action == MotionEvent.ACTION_CANCEL) {


            KeyboardUtil.showKeyboard(et_publish_editor)
            publish_add_topic_component.hideTip()
        }
        return false
    }


}