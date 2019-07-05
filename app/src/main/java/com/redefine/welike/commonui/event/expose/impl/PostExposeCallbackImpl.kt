package com.redefine.welike.commonui.event.expose.impl

import android.support.v7.widget.RecyclerView
import android.view.View
import com.redefine.welike.R
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.business.user.management.bean.FollowPost
import com.redefine.welike.business.user.management.bean.RecommendSlideUser
import com.redefine.welike.business.user.management.bean.RecommendUser
import com.redefine.welike.commonui.event.expose.ExposeEventReporter
import com.redefine.welike.commonui.event.expose.ItemExposeManager
import com.redefine.welike.commonui.event.expose.base.IDataProvider
import com.redefine.welike.commonui.event.expose.base.IItemVisibleCallback
import com.redefine.welike.statistical.EventConstants

/**
 * Created by nianguowang on 2019/1/9
 */
class PostExposeCallbackImpl : IItemVisibleCallback<List<PostBase>> {

    private lateinit var dataProvider: IDataProvider<List<PostBase>>
    private lateinit var viewSource: String

    private val itemExposeManager = ItemExposeManager(ItemExposeCallbackFactory.createHorizatalUserCardCallback())
    private var subDataProvider: IDataProvider<List<RecommendSlideUser>>? = null

    override fun bindDataAndSource(dataProvider: IDataProvider<List<PostBase>>?, oldSource: String?) {
        dataProvider?.let {
            this.dataProvider = dataProvider
            viewSource = dataProvider.source
        }

    }

    override fun updateSource(source: String, oldSource: String?) {
        this.viewSource = source
    }

    override fun onItemVisible(layoutPosition: Int, view: View?, showRatio: Float) {
        dataProvider.data?.let {
            var pos = layoutPosition
            if (dataProvider.hasHeader()) {
                pos--
            }
            if (pos <0 || pos >= it.size) {
                return
            }
            var postBase = it[pos]
            if (postBase is FollowPost) {
                if (subDataProvider == null) {
                    subDataProvider = object :IDataProvider<List<RecommendSlideUser>> {
                        override fun getData(): List<RecommendSlideUser>? {
                            return postBase.list
                        }

                        override fun getSource(): String {
                            when(viewSource) {
                                EventConstants.FEED_PAGE_HOME -> return EventConstants.FEED_PAGE_HOME_CARD_RECOMMEND
                                EventConstants.FEED_PAGE_DISCOVER_FOR_YOU -> return EventConstants.FEED_PAGE_DISCOVER_CARD_RECOMMEND
                                else -> return EventConstants.FEED_PAGE_DISCOVER_CARD_RECOMMEND
                            }
                        }

                        override fun hasHeader(): Boolean {
                            return false
                        }

                    }
                }
                var recyclerView = view!!.findViewById<RecyclerView>(R.id.rv_follow)
                recyclerView?.let {
                    itemExposeManager.attach(it, subDataProvider, null)
                    itemExposeManager.onShow()
                    itemExposeManager.onAttach()
                    itemExposeManager.onResume()
                }

            } else{
                ExposeEventReporter.reportPostExpose(postBase, dataProvider.source)
            }
        }
    }

    override fun onItemInvisible(layoutPosition: Int, view: View?) {
        dataProvider.data.let {
            var pos = layoutPosition
            if (dataProvider.hasHeader()) {
                pos--
            }
            if (pos <0 || pos >= it.size) {
                return
            }
            val postBase = it[pos]
            if (postBase is FollowPost) {
                itemExposeManager.onHide()
                itemExposeManager.onDetach()
                itemExposeManager.onPause()
            }
        }

    }

    override fun getRatioForExpose(): Float {
        return 0.5F
    }

}