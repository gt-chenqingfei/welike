package com.redefine.welike.business.publisher.ui.component

import android.arch.lifecycle.Observer
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.redefine.commonui.fresco.loader.LinkThumbUrlLoader
import com.redefine.welike.R
import com.redefine.welike.base.GlobalConfig
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.feeds.management.bean.*
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.viewmodel.PublishForwardViewModel
import kotlinx.android.synthetic.main.layout_publish_comment_editor_forward.view.*

/**
 * @author qingfei.chen
 * @date 2018/11/7
 * Copyright (C) 2018 redefine , Inc.
 */

@LayoutResource(layout =R.layout.layout_publish_comment_editor_forward)
class EditorForwardView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs) {
    override fun onCreateView() {
    }

    fun attach(viewModel: PublishForwardViewModel) {
        viewModel.mForwardPostLiveData.observe(activityContext, Observer {
            handleForwardCard(it)
        })
    }


    private fun handleForwardCard(postBase: PostBase?) {
        if (postBase == null) {
            handleErrorForwardCard()
        } else if (postBase is ForwardPost) {
            if (postBase.isForwardDeleted || postBase.rootPost == null) {
                handleErrorForwardCard()
            } else {
                handleNormalForwardCard(postBase.rootPost)
            }
        } else {
            handleNormalForwardCard(postBase)
        }

    }

    private fun handleNormalForwardCard(postCard: PostBase?) {
        postCard ?: return
        forward_nick.text = postCard.nickName
        var imageUrl: String? = null
        var cardContent: String? = null
        if (postCard.type == PostBase.POST_TYPE_PIC && postCard is PicPost) {
            val info = postCard.getPicInfo(0)
            if (info != null) {
                imageUrl = info.thumbUrl
            }
        } else if (postCard.type == PostBase.POST_TYPE_LINK && postCard is LinkPost) {
            imageUrl = postCard.linkThumbUrl
        } else if (postCard.type == PostBase.POST_TYPE_VIDEO && postCard is VideoPost) {
            imageUrl = postCard.coverUrl
        }
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = postCard.headUrl
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            forward_video_cover.visibility = View.VISIBLE
            LinkThumbUrlLoader.getInstance().loadLinkThumbUrl(forward_video_cover, imageUrl)
        } else {
            forward_video_cover.visibility = View.GONE
        }

        cardContent = postCard.summary

        forward_nick.text = GlobalConfig.AT + postCard.nickName
        forward_rich_text.richProcessor.setRichContent(cardContent, postCard.richItemList)
    }

    private fun handleErrorForwardCard() {
        forward_nick.text = ""
        forward_video_cover.setImageResource(R.drawable.forward_card_delete_img)
        forward_rich_text.text = ResourceTool.getString("forward_feed_delete_content")
    }
}