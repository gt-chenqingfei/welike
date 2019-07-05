package com.redefine.welike.business.publisher.ui.component

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.redefine.commonui.fresco.loader.LocalImageLoader
import com.redefine.im.d
import com.redefine.richtext.util.CollectionUtil
import com.redefine.welike.R
import com.redefine.welike.business.publisher.e
import com.redefine.welike.business.publisher.management.FeedPoster
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager
import com.redefine.welike.business.publisher.management.PublishManager
import com.redefine.welike.business.publisher.management.bean.DraftPost
import com.redefine.welike.business.publisher.management.cache.DraftCacheWrapper
import com.redefine.welike.business.publisher.management.handler.PublishMessage
import com.redefine.welike.business.publisher.management.task.IPublishTask
import com.redefine.welike.business.publisher.room.Draft
import com.redefine.welike.business.publisher.ui.component.base.BaseLinearContainer
import com.redefine.welike.business.publisher.ui.component.base.LayoutResource
import com.redefine.welike.business.publisher.viewmodel.DraftViewModel
import com.redefine.welike.statistical.EventLog1
import kotlinx.android.synthetic.main.item_publish_state.view.*
import kotlinx.android.synthetic.main.layout_publish_state.view.*

/**
 * @author qingfei.chen
 * @date 2018/12/25
 * Copyright (C) 2018 redefine , Inc.
 */
@LayoutResource(layout = R.layout.layout_publish_state)
class PublishStateView(context: Context?, attrs: AttributeSet?) : BaseLinearContainer(context, attrs), PublishManager.IPublishCallback {

    var mViewMap: HashMap<String, ViewHolder>? = null
    override fun onCreateView() {

        mViewMap = HashMap()

        val mDraftViewModel = ViewModelProviders.of(activityContext).get(DraftViewModel::class.java)
        mDraftViewModel.getUploadingDraft { draftLiveData ->
            draftLiveData.observe(activityContext, Observer<List<Draft>> {
                rv_publish_state.removeAllViews()


                val mDraftPostList: ArrayList<DraftPost> = ArrayList()

                it?.forEach { draft ->
                    val draftPost = DraftCacheWrapper.convertDraftFromLocal(draft) as DraftPost
                    if (draftPost.state != DraftPost.STATE_NONE) {

                        var vh = mViewMap?.get(draftPost.draftId)
                        if (vh != null) {
                            vh.attachToRoot()
                        } else {
                            vh = ViewHolder(context, this)
                        }
                        vh.bindView(draftPost)
                        mViewMap?.put(draftPost.draftId, vh)
                        mDraftPostList.add(draftPost)
                    }
                }

                FeedPoster.getInstance().notifyDraftPostData(mDraftPostList)

                rv_publish_state?.let { v ->
                    if (v.childCount <= 0) {
                        this.visibility = View.GONE
                    } else {
                        this.visibility = View.VISIBLE
                    }
                }
            })

        }


        FeedPoster.getInstance().mProgressLiveData.observe(activityContext, Observer {

            it?.let { bean ->
                try {
                    mViewMap?.get(bean.draftId)?.updateProgress(bean.progress)
                } catch (e: java.lang.Exception) {
                    e("PublishStateView", "Exception:$e")
                }
            }
        })

        FeedPoster.getInstance().mSendStateLiveData.observe(activityContext, Observer { publishState ->
            if (publishState?.state == PublishMessage.PublishState.SUCCESS) {
                mViewMap?.get(publishState.draftId)?.let {
                    mViewMap?.remove(publishState.draftId)
                }
            }
        })

    }


    override fun onPublishStart(task: IPublishTask) {

    }

    override fun onPublishProgress(task: IPublishTask, progress: Int) {
        val draftId = task.getPublishDraft().getDraftSerializable().draftId
        mViewMap?.get(draftId)?.updateProgress(progress)
    }

    override fun onPublishCompleted(task: IPublishTask, publishState: PublishMessage.PublishState?) {

    }

}

class ViewHolder(val context: Context, val root: ViewGroup) {
    val convertView: View = LayoutInflater.from(context).inflate(R.layout.item_publish_state, root, false)
    private var currentProgress = 0
    private var lastTimeMillis = 0L

    init {
        attachToRoot()
    }

    fun attachToRoot() {
        root.rv_publish_state?.addView(convertView)
    }

    fun updateProgress(progress: Int) {
        try {
            currentProgress = progress
            val current = System.currentTimeMillis()
            if (current - lastTimeMillis > 500) {
                lastTimeMillis = current
                convertView.v_publish_state_progress.layoutParams.let {
                    if (it is ConstraintLayout.LayoutParams) {
                        it.matchConstraintPercentWidth = java.lang.Float.valueOf(progress.toFloat()) / 100f
                        d("showProcess" + it.matchConstraintPercentWidth)
                    }
                }
                convertView.v_publish_state_progress.requestLayout()
            }
        } catch (e: Exception) {
            e("PublishStateView", "e:${e}")
        }

    }

    fun bindView(draftPost: DraftPost) {
        updateProgress(currentProgress)
        val draweeHierarchy: GenericDraweeHierarchy = convertView.iv_publish_state.hierarchy
        var filePath: String? = ""
        if (!CollectionUtil.isEmpty(draftPost.pollItemInfos)) {
            draweeHierarchy.setPlaceholderImage(R.drawable.ic_uploading_poll)
        } else if (!CollectionUtil.isEmpty(draftPost.picDraftList)) {
            filePath = draftPost.picDraftList.firstOrNull()?.localFileName
        } else if (draftPost.video != null) {
            filePath = draftPost.video.localFileName
            convertView.iv_publish_state_video_mark.visibility = View.VISIBLE
        } else {
            draweeHierarchy.setPlaceholderImage(R.drawable.ic_uploading_txt)
        }

        when (draftPost.state) {
            DraftPost.STATE_UPLOADING -> {
                convertView.tv_publish_state.setText(R.string.publish_post_uploading)
                convertView.iv_publish_state_refresh.visibility = View.GONE
                convertView.iv_publish_state_close.visibility = View.GONE
                convertView.v_publish_state_progress_bg.setBackgroundColor(context.resources.getColor(R.color.color_fffaf4))
                convertView.v_publish_state_progress.visibility = View.VISIBLE
            }
            DraftPost.STATE_UPLOAD_FAILED -> {
                filePath = ""
                convertView.iv_publish_state_video_mark.visibility = View.GONE
                convertView.iv_publish_state_refresh.visibility = View.VISIBLE
                convertView.iv_publish_state_close.visibility = View.VISIBLE
                convertView.tv_publish_state.setText(R.string.publish_post_error)
                draweeHierarchy.setPlaceholderImage(R.drawable.ic_uploading_error)
                convertView.v_publish_state_progress_bg.setBackgroundColor(context.resources.getColor(R.color.color_ff6a49))
                convertView.v_publish_state_progress.visibility = View.INVISIBLE
            }
            DraftPost.STATE_UPLOAD_SUCCESS -> {
                filePath = ""
                convertView.iv_publish_state_video_mark.visibility = View.GONE
                draweeHierarchy.setPlaceholderImage(R.drawable.ic_uploading_success)
                convertView.tv_publish_state.setText(R.string.publish_post_success)
                convertView.iv_publish_state_refresh.visibility = View.GONE
                convertView.iv_publish_state_close.visibility = View.GONE
                convertView.v_publish_state_progress_bg.setBackgroundColor(context.resources.getColor(R.color.color_fffaf4))
                convertView.v_publish_state_progress.visibility = View.VISIBLE
            }
        }
        convertView.iv_publish_state.hierarchy = draweeHierarchy

        LocalImageLoader.getInstance().loadFixedSizeImageFromLocal(convertView.iv_publish_state, filePath)

        convertView.iv_publish_state_close.setOnClickListener {
            PublishAnalyticsManager.getInstance().obtainEventModel(draftPost.draftId).proxy.report35()
            FeedPoster.getInstance().remove(draftPost)
        }

        convertView.iv_publish_state_refresh.setOnClickListener {
            val m = PublishAnalyticsManager.getInstance().obtainEventModel(draftPost.getDraftId())
            m.reSendFrom = EventLog1.Publish.ReSendFrom.FAILURE_STATUS_BAR
            m.proxy.report34()
            FeedPoster.getInstance().publish(draftPost)
        }
    }
}



