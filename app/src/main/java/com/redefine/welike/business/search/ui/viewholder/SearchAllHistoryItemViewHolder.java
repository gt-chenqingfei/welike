package com.redefine.welike.business.search.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.search.ui.adapter.SearchSugAdapter;
import com.redefine.welike.business.search.ui.bean.SearchFootBean;

import java.util.List;

/**
 * Created by liwenbo on 2018/3/14.
 */

public class SearchAllHistoryItemViewHolder extends BaseSearchSugViewHolder<SearchFootBean> {

    private final TextView mSearchAllHistory;
    private final TextView mTopicTitle;
    private final TextView mTopic1, mTopic2, mTopic3, mTopic4, mTopic5, mMoreTopicsText;
    private final View mAllTopicContainer, mTopicContainer2, mMoreTopics;
    private final SearchSugAdapter.OnTopicClickListener mTopicClickListener;
    private View divider;

    public SearchAllHistoryItemViewHolder(View itemView, SearchSugAdapter.OnTopicClickListener listener) {
        super(itemView);
        mTopicClickListener = listener;
        mSearchAllHistory = itemView.findViewById(R.id.search_all_history);
        mAllTopicContainer = itemView.findViewById(R.id.topic_layout);
        divider = itemView.findViewById(R.id.common_shadow);
        mTopicTitle = itemView.findViewById(R.id.search_topic_title);
        mTopic1 = itemView.findViewById(R.id.search_hot_topic1);
        mTopic2 = itemView.findViewById(R.id.search_hot_topic2);
        mTopic3 = itemView.findViewById(R.id.search_hot_topic3);
        mTopic4 = itemView.findViewById(R.id.search_hot_topic4);
        mTopic5 = itemView.findViewById(R.id.search_hot_topic5);
        mMoreTopics = itemView.findViewById(R.id.search_more_topics);
        mMoreTopicsText = itemView.findViewById(R.id.search_more_topics_text);
        mTopicContainer2 = itemView.findViewById(R.id.search_topic_container2);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, SearchFootBean bean) {
        mSearchAllHistory.setVisibility(bean.isShowMoreHistory() ? View.VISIBLE : View.GONE);
        mSearchAllHistory.setText(bean.getMoreHistoryText());
        List<TopicSearchSugBean.TopicBean> topicBeans = bean.getTopicBeans();
        if (CollectionUtil.isEmpty(topicBeans)) {
            mAllTopicContainer.setVisibility(View.GONE);
        } else {
            mTopicTitle.setText(bean.getTopicText());
            if (topicBeans.size() < 3) {
                mAllTopicContainer.setVisibility(View.GONE);
            } else {
                mAllTopicContainer.setVisibility(View.VISIBLE);
                mTopic1.setText(topicBeans.get(0).name);
                mTopic1.setTag(topicBeans.get(0));
                mTopic2.setText(topicBeans.get(1).name);
                mTopic2.setTag(topicBeans.get(1));
                mMoreTopicsText.setText(MyApplication.getAppContext().getResources().getString(R.string.more_upper));
                if (topicBeans.size() >= 5) {
                    mTopicContainer2.setVisibility(View.VISIBLE);
                    mTopic3.setText(topicBeans.get(2).name);
                    mTopic3.setTag(topicBeans.get(2));
                    mTopic4.setText(topicBeans.get(3).name);
                    mTopic4.setTag(topicBeans.get(3));
                    mTopic5.setText(topicBeans.get(4).name);
                    mTopic5.setTag(topicBeans.get(4));
                } else {
                    mTopicContainer2.setVisibility(View.GONE);
                    mTopic5.setText(topicBeans.get(2).name);
                    mTopic5.setTag(topicBeans.get(2));
                }

            }
        }
        mSearchAllHistory.setOnClickListener(mListener);
        mTopic1.setOnClickListener(mListener);
        mTopic2.setOnClickListener(mListener);
        mTopic3.setOnClickListener(mListener);
        mTopic4.setOnClickListener(mListener);
        mTopic5.setOnClickListener(mListener);
        mMoreTopics.setOnClickListener(mListener);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mSearchAllHistory) {
                if (mTopicClickListener != null) {
                    mTopicClickListener.onMoreHistoryClick();
                }
            } else if (v == mMoreTopics) {
                if (mTopicClickListener != null) {
                    mTopicClickListener.onMoreTopicClick();
                }
            } else if (v == mTopic1) {
                Object tag = mTopic1.getTag();
                if (mTopicClickListener != null && tag instanceof TopicSearchSugBean.TopicBean) {
                    mTopicClickListener.onTopicClick((TopicSearchSugBean.TopicBean) tag);
                }
            } else if (v == mTopic2) {
                Object tag = mTopic2.getTag();
                if (mTopicClickListener != null && tag instanceof TopicSearchSugBean.TopicBean) {
                    mTopicClickListener.onTopicClick((TopicSearchSugBean.TopicBean) tag);
                }
            } else if (v == mTopic3) {
                Object tag = mTopic3.getTag();
                if (mTopicClickListener != null && tag instanceof TopicSearchSugBean.TopicBean) {
                    mTopicClickListener.onTopicClick((TopicSearchSugBean.TopicBean) tag);
                }
            } else if (v == mTopic4) {
                Object tag = mTopic4.getTag();
                if (mTopicClickListener != null && tag instanceof TopicSearchSugBean.TopicBean) {
                    mTopicClickListener.onTopicClick((TopicSearchSugBean.TopicBean) tag);
                }
            } else if (v == mTopic5) {
                Object tag = mTopic5.getTag();
                if (mTopicClickListener != null && tag instanceof TopicSearchSugBean.TopicBean) {
                    mTopicClickListener.onTopicClick((TopicSearchSugBean.TopicBean) tag);
                }
            }
        }
    };
}
