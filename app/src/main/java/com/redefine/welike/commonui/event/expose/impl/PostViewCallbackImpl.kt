package com.redefine.welike.commonui.event.expose.impl

import android.view.View
import com.redefine.foundation.utils.LogUtil
import com.redefine.welike.business.feeds.management.bean.PostBase
import com.redefine.welike.commonui.event.expose.ExposeEventReporter
import com.redefine.welike.commonui.event.expose.base.IDataProvider
import com.redefine.welike.commonui.event.expose.base.IItemVisibleCallback

/**
 * Created by nianguowang on 2019/1/9
 */
class PostViewCallbackImpl : IItemVisibleCallback<List<PostBase>> {
    private var dataProvider: IDataProvider<List<PostBase>>? = null
    private var viewSource: String? = null
    private var oldSource: String? = null
    private var hasHeader: Boolean = false
    private val postMap = mutableMapOf<PostBase, Long>()

    override fun bindDataAndSource(dataProvider: IDataProvider<List<PostBase>>?, oldSource: String?) {
        this.oldSource = oldSource
        dataProvider?.let {
            this.dataProvider = dataProvider
            viewSource = dataProvider.source
            hasHeader = dataProvider.hasHeader()
        }

    }

    override fun updateSource(source: String, oldSource: String?) {
        this.viewSource = source
        this.oldSource = oldSource
    }

    override fun onItemVisible(layoutPosition: Int, view: View?, showRatio: Float) {
        dataProvider?.data?.let {
            var pos = layoutPosition
            if (hasHeader) {
                pos--
            }
            if (pos <0 || pos >= it.size) {
                return
            }
            val postBase = it[pos]
            postMap[postBase] = System.currentTimeMillis()
        }
    }

    override fun onItemInvisible(layoutPosition: Int, view: View?) {
        dataProvider?.data?.let {
            var pos = layoutPosition
            if (hasHeader) {
                pos--
            }
            if (pos <0 || pos >= it.size) {
                return
            }
            val postBase = it[pos]
            if (postMap[postBase] != null) {
                val viewTime = System.currentTimeMillis() - postMap[postBase]!!
                if (viewTime > 500) {
                    ExposeEventReporter.reportPostView(postBase, viewSource, oldSource, viewTime)
                }
            } else {
                LogUtil.d("wng_", "${postBase.pid} be invisible, but not have visible!!")
            }
        }
    }

    override fun getRatioForExpose(): Float {
        return 0.5F
    }

}
