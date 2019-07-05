package com.redefine.welike.business.browse.ui.adapter;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.loadmore.adapter.LoadMoreFooterRecyclerAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.framework.Event;
import com.redefine.welike.R;
import com.redefine.welike.base.constant.EventIdConstant;
import com.redefine.welike.business.browse.management.bean.Campaign;
import com.redefine.welike.business.feeds.ui.viewholder.FeedLoadMoreViewHolder;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.topic.ui.constant.TopicConstant;
import com.redefine.welike.statistical.EventConstants;
import com.redefine.welike.statistical.EventLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class BrowseLatestCampaignAdapter extends LoadMoreFooterRecyclerAdapter<BaseHeaderBean, Campaign> {

    public BrowseLatestCampaignAdapter() {
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return campaigns.size();
    }

    private ArrayList<Campaign> campaigns = new ArrayList<>();

    public void setCampaigns(ArrayList<Campaign> campaigns) {
        this.campaigns = campaigns;
        notifyDataSetChanged();
    }


    @Override
    protected BaseRecyclerViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new FeedLoadMoreViewHolder(mInflater.inflate(R.layout.feed_load_more_layout, parent, false));
    }

    @Override
    protected BaseRecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_browse_latest_campaign_layout, null);
        return new CampaignViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(@NonNull BaseRecyclerViewHolder holder, int position) {

        CampaignViewHolder viewHolder = (CampaignViewHolder) holder;
        final Campaign campaign = campaigns.get(position);

        viewHolder.pic.setImageURI(campaign.getBannerUrl());

        viewHolder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(campaign.getId())) return;
                Bundle bundle = new Bundle();
                TopicSearchSugBean.TopicBean bean = new TopicSearchSugBean.TopicBean();
                bean.name = campaign.getName();
                bean.id = campaign.getId();
                bundle.putSerializable(TopicConstant.BUNDLE_KEY_TOPIC, bean);
                EventBus.getDefault().post(new Event(EventIdConstant.LAUNCH_TOPIC_LANDING_PAGE, bundle));
                EventLog.UnLogin.report20(EventConstants.UNLOGIN_DISCOVERACTION_LATEST_CAMPAIGN);
            }
        });
    }


    @Override
    protected Campaign getRealItem(int position) {
        return campaigns.get(position);
    }

    class CampaignViewHolder extends BaseRecyclerViewHolder {

        SimpleDraweeView pic;


        CampaignViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.iv_campaign_pic);
        }
    }

}
