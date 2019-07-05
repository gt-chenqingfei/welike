package com.redefine.welike.business.publisher.ui.component

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.redefine.commonui.fresco.loader.VideoCoverUrlLoader
import com.redefine.foundation.utils.InputMethodUtil
import com.redefine.multimedia.photoselector.entity.Item
import com.redefine.multimedia.photoselector.util.PathUtils
import com.redefine.welike.R
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.viewmodel.PublishPostViewModel
import com.redefine.welike.commonui.photoselector.PhotoSelector
import kotlinx.android.synthetic.main.layout_publish_post_editor_video.view.*

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */

@LayoutResource(layout = R.layout.layout_publish_post_editor_video)
class EditorVideoView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs) {
    override fun onCreateView() {
        val viewModel = ViewModelProviders.of(activityContext).get(PublishPostViewModel::class.java)
        viewModel.mVideoLiveData.observe(activityContext, android.arch.lifecycle.Observer {
            fillData(it)
        })

        editor_video_thumb.setOnClickListener {
            PhotoSelector.launchPlayPostVideo(activityContext, PathUtils.getPath(activityContext,
                    viewModel.mVideoLiveData.value?.contentUri))
            InputMethodUtil.hideInputMethod(activityContext.currentFocus)
        }

        editor_video_delete_btn.setOnClickListener {
            viewModel.updateVideo(null)
        }
    }

    private fun fillData(video: Item?) {
        if (video == null) {
            this.visibility = View.GONE
            return
        }
        this.visibility = View.VISIBLE

        val coverPath = if (!TextUtils.isEmpty(video.coverPath)) video.coverPath else video.filePath
        VideoCoverUrlLoader.getInstance().loadVideoThumbFile(editor_video_container,
                editor_video_thumb, coverPath, video.width, video.height)
    }
}