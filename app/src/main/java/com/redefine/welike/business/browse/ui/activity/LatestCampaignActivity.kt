package com.redefine.welike.business.browse.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.redefine.commonui.activity.BaseActivity
import com.redefine.commonui.enums.PageStatusEnum
import com.redefine.foundation.framework.Event
import com.redefine.welike.R
import com.redefine.welike.base.constant.EventIdConstant
import com.redefine.welike.base.constant.RouteConstant
import com.redefine.welike.business.browse.management.bean.Campaign
import com.redefine.welike.business.browse.ui.adapter.BrowseLatestCampaignAdapter
import com.redefine.welike.business.browse.ui.viewmodel.BrowseLatestViewModel
import kotlinx.android.synthetic.main.activity_latest_campaign_layout.*
import org.greenrobot.eventbus.EventBus


@Route(path = RouteConstant.PATH_LATEST_CAMPAIGN)
class LatestCampaignActivity : BaseActivity() {


    companion object {
        fun launcher() {
            EventBus.getDefault().post(Event(EventIdConstant.LAUNCH_BROWSE_LATEST_CAMPAIGN))
        }
    }

    private var campaignAdapter: BrowseLatestCampaignAdapter? = null

    private lateinit var latestViewModel: BrowseLatestViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_campaign_layout)
        latestViewModel = ViewModelProviders.of(this).get(BrowseLatestViewModel::class.java)


        setViewEvent()

        setViewModel()

        latestViewModel.refresh()

    }


    private fun setViewEvent() {

        common_back_btn.setOnClickListener({ finish() })

        latest_campaign_recycler_view.layoutManager = LinearLayoutManager(this)

        campaignAdapter = BrowseLatestCampaignAdapter()

        latest_campaign_recycler_view.adapter = campaignAdapter
        topic_landing_refresh_layout.setOnRefreshListener({
            latestViewModel.refresh()
        })

    }

    private fun setViewModel() {

        latestViewModel.compaigns.observe(this, Observer<ArrayList<Campaign>> {
            if (it == null) return@Observer
            campaignAdapter?.setCampaigns(it)

            campaignAdapter?.noMore()
        })



        latestViewModel.pageStatus.observe(this, Observer<PageStatusEnum> {
            if (it == null) return@Observer
            when (it) {
                PageStatusEnum.ERROR -> {
                    if (campaignAdapter?.realItemCount == 0) {
                        showErrorView()
                    } else {
                        showContentView()
                    }
                    topic_landing_refresh_layout.finishRefresh()
                }
                PageStatusEnum.CONTENT -> {
                    if (campaignAdapter?.realItemCount == 0) {
                        showEmptyView()
                    } else {
                        showContentView()
                    }
                    topic_landing_refresh_layout.finishRefresh()
                }

                PageStatusEnum.LOADING -> if (campaignAdapter?.realItemCount == 0) showLoading()
            }

        })
    }

    fun showLoading() {
        common_loading_view?.visibility = (View.VISIBLE)
        common_empty_view?.visibility = (View.INVISIBLE)
        common_error_view?.visibility = (View.INVISIBLE)
    }

    fun showErrorView() {
        common_loading_view?.visibility = (View.INVISIBLE)
        common_error_view?.visibility = (View.VISIBLE)
        common_empty_view?.visibility = (View.INVISIBLE)

    }

    fun showEmptyView() {
        common_loading_view?.visibility = (View.INVISIBLE)
        common_empty_view?.visibility = (View.VISIBLE)
        common_error_view?.visibility = (View.INVISIBLE)
    }

    fun showContentView() {
        common_loading_view?.visibility = (View.INVISIBLE)
        common_empty_view?.visibility = (View.INVISIBLE)
        common_error_view?.visibility = (View.INVISIBLE)
    }


}