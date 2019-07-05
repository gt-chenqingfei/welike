package com.redefine.welike.business.feeds.ui.viewholder

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.redefine.commonui.fresco.loader.HeadUrlLoader
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder
import com.redefine.foundation.framework.Event
import com.redefine.im.room.SESSION
import com.redefine.welike.R
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.profile.AccountManager
import com.redefine.welike.base.profile.bean.Account
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.business.feeds.management.bean.DiscoverHeader
import com.redefine.welike.business.feeds.management.bean.GPScorePost
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.im.IMHelper
import com.redefine.welike.business.im.ui.constant.ImConstant
import com.redefine.welike.business.startup.management.HalfLoginManager
import com.redefine.welike.business.startup.management.constant.RegisteredConstant
import com.redefine.welike.common.ScoreManager
import com.redefine.welike.commonui.util.ToastUtils
import com.redefine.welike.statistical.EventLog
import com.redefine.welike.statistical.EventLog1
import com.redefine.welike.statistical.bean.RegisterAndLoginModel
import com.redefine.welike.statistical.manager.GPScoreEventManager

import org.greenrobot.eventbus.EventBus

import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by MR on 2018/1/30.
 */

class DiscoverStickyHeaderHolder(itemView: View) : BaseRecyclerViewHolder<PostBase>(itemView) {

    private val title: TextView = itemView.findViewById(R.id.discover_header_title)
    private val desc: TextView = itemView.findViewById(R.id.discover_header_desc)
    private val image: SimpleDraweeView = itemView.findViewById(R.id.discover_header_image)

    private var headerData: DiscoverHeader? = null

    override fun bindViews(adapter: RecyclerView.Adapter<*>, data: PostBase) {
        super.bindViews(adapter, data)

        (data as DiscoverHeader).let {
            headerData = it
            title.text = it.topicName.trimStart("#"[0])
            desc.text = it.rmdWords
            if (it.iconUrl.isNullOrEmpty()) {
                image.visibility = View.INVISIBLE
            } else {
                image.visibility = View.VISIBLE
                HeadUrlLoader.getInstance().loadHeaderUrl2(image, it.iconUrl)
            }
            itemView.setBackgroundResource(it.getBackground())
            EventLog1.DiscoverTopic.report2(it.topicId)
        }

    }


}

fun DiscoverHeader.getBackground(): Int {
    return when (backgroudAt) {
        1 -> R.drawable.discover_header_bg1
        2 -> R.drawable.discover_header_bg2
        3 -> R.drawable.discover_header_bg3
        else -> R.drawable.discover_header_bg4
    }
}
