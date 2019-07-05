package com.redefine.welike.business.feeds.ui.page

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.redefine.foundation.framework.Event
import com.redefine.welike.R
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.business.feeds.ui.adapter.FeedItemFollowAdapter
import com.redefine.welike.business.feeds.ui.constant.FeedConstant
import com.redefine.welike.business.feeds.ui.listener.DefaultFollowClickListener
import com.redefine.welike.business.user.management.bean.RecommendSlideUser
import com.redefine.welike.business.user.ui.activity.RecommendFollowActivity
import com.redefine.welike.business.user.ui.vm.RecommendUserViewModel2
import com.redefine.welike.commonui.event.expose.ItemExposeManager
import com.redefine.welike.commonui.event.expose.impl.ItemExposeCallbackFactory
import com.redefine.welike.statistical.EventConstants
import com.redefine.welike.statistical.EventLog1
import org.greenrobot.eventbus.EventBus

/**
 * Created by nianguowang on 2019/1/15
 */
class HomeBigCardRecomFragment: Fragment() {

//    private lateinit var viewModel : DiscoverListViewModel
    private var recommendUserViewModel: RecommendUserViewModel2? = null
    private lateinit var adapter: FeedItemFollowAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var moreFriend: View
    private lateinit var goTrending: TextView
    private val mItemExposeManager = ItemExposeManager(ItemExposeCallbackFactory.createHorizatalUserCardCallback())

    companion object {
        fun create() : HomeBigCardRecomFragment {
            return HomeBigCardRecomFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(DiscoverListViewModel::class.java)
        recommendUserViewModel = ViewModelProviders.of(this).get(RecommendUserViewModel2::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_home_big_card, container, false)
        recyclerView = view.findViewById(R.id.home_big_card_recycler_view)
        moreFriend = view.findViewById<View>(R.id.home_big_card_more_friend)
        goTrending = view.findViewById(R.id.home_big_card_go_trending)

        initView()
        initObserver()
        return view
    }

    private fun initView() {
        moreFriend.setOnClickListener{
            RecommendFollowActivity.launch()
        }
        goTrending.setOnClickListener {
            EventLog1.Follow.report7()
            val bundle = Bundle()
            bundle.putBoolean(FeedConstant.BUNDLE_KEY_REFRESH_AGAIN, true)
            val event = Event(EventIdConstant.LAUNCH_HOME_TO_FORYOU, bundle)
            EventBus.getDefault().post(event)
        }
        adapter = FeedItemFollowAdapter(EventConstants.FEED_PAGE_FOLLOW_BIG_CARD)
        adapter.setHolderType(FeedItemFollowAdapter.VIEW_HOLDER_TYPE_BIG_CARD)
        adapter.setListener(DefaultFollowClickListener(recyclerView, EventConstants.FEED_PAGE_FOLLOW_BIG_CARD))
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = adapter
        recyclerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener{
            override fun onViewAttachedToWindow(v: View?) {
                mItemExposeManager.onAttach()
            }
            override fun onViewDetachedFromWindow(v: View?) {
                mItemExposeManager.onDetach()
            }
        })

        mItemExposeManager.attach(recyclerView, adapter, null)

        setGoTrendingText()
    }

    private fun setGoTrendingText() {
        val text = getString(R.string.recommend_go_explore)
        val trending = getString(R.string.discover_hot)
        val goTrendingText = String.format(text, trending)

        val start = goTrendingText.indexOf(trending)
        val end = start + trending.length
        val sb = SpannableString(goTrendingText)
        val span = ForegroundColorSpan(resources.getColor(R.color.color_48779D))
        sb.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        goTrending.text = sb
    }

    private fun initObserver() {
        recommendUserViewModel!!.tryRefresh()
        recommendUserViewModel!!.recommedUsers.observe(this, Observer {
            var recommendUsers = ArrayList<RecommendSlideUser>()
            it?.forEach {
                recommendUsers.add(it.toRecommendSlideUser())
            }
            adapter.users = recommendUsers
            mItemExposeManager.onDataLoaded()
        })
    }

    override fun onResume() {
        super.onResume()
        mItemExposeManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        mItemExposeManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mItemExposeManager.onDestroy()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            RecommendFollowActivity.launch()
            mItemExposeManager.onShow()
        } else{
            mItemExposeManager.onHide()
        }
    }
}