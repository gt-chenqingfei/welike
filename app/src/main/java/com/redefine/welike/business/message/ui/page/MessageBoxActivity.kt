package com.redefine.welike.business.message.ui.page

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.loadmore.adapter.EndlessRecyclerOnScrollListener
import com.redefine.commonui.loadmore.adapter.ILoadMoreDelegate
import com.redefine.commonui.widget.EmptyView
import com.redefine.commonui.widget.ErrorView
import com.redefine.commonui.widget.LoadingView
import com.redefine.welike.R
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.base.resource.ResourceTool
import com.redefine.welike.base.resource.ResourceTool.ResourceFileEnum.IM
import com.redefine.welike.business.message.ui.adapter.MessageBoxAdapter
import com.redefine.welike.business.message.ui.constant.MessageConstant
import com.redefine.welike.business.message.ui.contract.IMessageBoxContract
import com.redefine.welike.business.message.ui.presenter.MessageBoxPresenter
import com.redefine.welike.kext.translate
import com.scwang.smartrefresh.layout.api.RefreshLayout
import kotlinx.android.synthetic.main.message_box_layout.*

@Route(path = RouteConstant.PATH_MESSAGE_BOX)
class MessageBoxActivity : BaseActivity(), IMessageBoxContract.IMessageBoxView {

    private var mPresenter: IMessageBoxContract.IMessageBoxPresenter? = null
    private var mFragmentType: String? = null
    private var mRecycler: RecyclerView? = null
    private var mErrorView: ErrorView? = null
    private var mEmptyView: EmptyView? = null
    private var mLoadingView: LoadingView? = null
    private var mRefreshLayout: RefreshLayout? = null
    private var mMagic: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_box_layout)
        val news = intent?.extras?.getInt(MessageConstant.NEWS) ?: 0
        mFragmentType = intent?.extras?.getString(MessageConstant.FRAGMNET_TYPE) ?: ""
        if (mFragmentType == "") {
            mFragmentType = savedInstanceState?.getString(MessageConstant.FRAGMNET_TYPE) ?: MessageConstant.FRAGMNET_ME
        }
        mPresenter = MessageBoxPresenter(mFragmentType, news, this)
        common_back_btn.setOnClickListener { finish() }
        common_title_view.text = when (mFragmentType) {
            MessageConstant.FRAGMNET_COMMENT -> "message_comment_text"
            MessageConstant.FRAGMNET_ME -> "message_mention_text"
            MessageConstant.FRAGMNET_LIKE -> "message_like_text"
            MessageConstant.FRAGMNET_PUSH -> "message_push_text"

            else -> "message_comment_text"
        }.translate(IM)
        mMagic = findViewById(R.id.message_nick_name_layout)

        mErrorView = common_error_view
        mEmptyView = common_empty_view
        mRefreshLayout = message_box_refresh_layout
        mLoadingView = common_loading_view
        mRecycler = message_box_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MessageBoxActivity)
            addOnScrollListener(EndlessRecyclerOnScrollListener(object : ILoadMoreDelegate {
                override fun canLoadMore(): Boolean {
                    return mPresenter?.canLoadMore() ?: false
                }

                override fun onLoadMore() {
                    mPresenter?.onLoadMore()
                }

            }))
        }
        mRefreshLayout?.apply {
            isEnableLoadMore = false
            isEnableOverScrollBounce = false
            setOnRefreshListener { mPresenter?.onRefresh() }
        }
        mEmptyView?.showEmptyImageText(R.drawable.ic_common_empty, ResourceTool
                .getString(ResourceTool.ResourceFileEnum.IM, "message_no_information_text"))

        mErrorView?.setOnErrorViewClickListener {
            showLoading()
            mPresenter?.onRefresh()
        }
        mPresenter?.init()
    }

    companion object {

        fun launch(fragType: String, news: Int) {
//            val bundle = Bundle()
//            bundle.putString(MessageConstant.FRAGMNET_TYPE, fragType)
//            bundle.putInt(MessageConstant.NEWS, news)
//            EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_MESSAGE_LIST_EVENT, bundle))
            ARouter.getInstance().build(RouteConstant.PATH_MESSAGE_BOX)
                    .withString(MessageConstant.FRAGMNET_TYPE, fragType)
                    .withInt(MessageConstant.NEWS, news)
                    .navigation()
        }
    }

    override fun setAdapter(mAdapter: MessageBoxAdapter?) {
        mRecycler?.adapter = mAdapter
    }

    override fun showContent() {
        mLoadingView?.visibility = View.INVISIBLE
        mEmptyView?.visibility = View.INVISIBLE
        mErrorView?.visibility = View.INVISIBLE
    }

    override fun showLoading() {
        mLoadingView?.visibility = View.VISIBLE
        mEmptyView?.visibility = View.INVISIBLE
        mErrorView?.visibility = View.INVISIBLE
    }

    override fun getMagicLength(): Int = mMagic?.measuredWidth ?: 1

    override fun setPresenter(messageBoxPresenter: IMessageBoxContract.IMessageBoxPresenter?) {
    }

    override fun setRefreshEnable(b: Boolean) {
        mRefreshLayout?.isEnableRefresh = b
    }

    override fun showEmptyView() {
        mLoadingView?.visibility = View.INVISIBLE
        mEmptyView?.visibility = View.VISIBLE
        mErrorView?.visibility = View.INVISIBLE
    }

    override fun showErrorView() {
        mLoadingView?.visibility = View.INVISIBLE
        mErrorView?.visibility = View.VISIBLE
        mEmptyView?.visibility = View.INVISIBLE
    }

    override fun finishRefresh() {
        mRefreshLayout?.finishRefresh()
    }

    override fun autoRefresh() {
        mRefreshLayout?.autoRefresh()
    }
}
