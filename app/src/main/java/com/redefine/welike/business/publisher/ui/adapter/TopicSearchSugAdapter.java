package com.redefine.welike.business.publisher.ui.adapter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.BaseRecyclerAdapter;
import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.management.PublishAnalyticsManager;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.publisher.ui.viewholder.TopicSearchItemViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.TopicSearchTitleNewViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.TopicSearchTitleViewHolder;
import com.redefine.welike.statistical.EventLog1;

/**
 * Created by liwenbo on 2018/4/10.
 */

public class TopicSearchSugAdapter extends BaseRecyclerAdapter<BaseRecyclerViewHolder> {

    private final String mNewTopicTitle;
    private final String mRecommendTopicTitle;
    private final String mTextNumberSpace, newMark;
    private TopicSearchSugBean mTopicSearchSugBean;

    private static final int TOPIC_SUG_TITLE_TYPE = 0;
    private static final int TOPIC_SUG_TITLE_NEW_TYPE = 1;
    private static final int TOPIC_SUG_ITEM_TYPE = 2;
    private static final int TOPIC_SUG_ITEM_NEW_TYPE = 3;
    private LayoutInflater mLayoutInflater;
    private String mSearchQuery;
    private HeaderAndFooterRecyclerViewAdapter.OnItemClickListener mListener;

    public TopicSearchSugAdapter() {
        mNewTopicTitle = ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "new_topic_title");
        mRecommendTopicTitle = ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "recommend_topic_title");
        mTextNumberSpace = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "with_text_numbers_and_spaces");
        newMark = ResourceTool.getString(ResourceTool.ResourceFileEnum.TOPIC, "new_topic");
    }

    public void setData(String searchQuery, TopicSearchSugBean topicSearchSugBean) {
        mSearchQuery = searchQuery;
        mTopicSearchSugBean = topicSearchSugBean;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder viewHolder = null;
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        switch (viewType) {
            case TOPIC_SUG_TITLE_TYPE:
                viewHolder = new TopicSearchTitleViewHolder(mLayoutInflater.inflate(R.layout.topic_search_title_layout, parent, false));
                break;
            case TOPIC_SUG_TITLE_NEW_TYPE:
                TopicSearchTitleNewViewHolder holder = new TopicSearchTitleNewViewHolder(mLayoutInflater.inflate(R.layout.topic_search_title_add_layout, parent, false));
                holder.setSubTitle(mTextNumberSpace);
                viewHolder = holder;
                break;
            case TOPIC_SUG_ITEM_NEW_TYPE:
                TopicSearchItemViewHolder holder2 = new TopicSearchItemViewHolder(mLayoutInflater.inflate(R.layout.topic_search_sug_item_layout, parent, false));
                holder2.markNew(newMark);
                viewHolder = holder2;
                break;
            case TOPIC_SUG_ITEM_TYPE:
            default:
                viewHolder = new TopicSearchItemViewHolder(mLayoutInflater.inflate(R.layout.topic_search_sug_item_layout, parent, false));
                break;
        }
        return viewHolder;
    }


    public void setOnItemClickListener(HeaderAndFooterRecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseRecyclerViewHolder holder, final int position) {
        if (holder instanceof TopicSearchItemViewHolder) {
            ((TopicSearchItemViewHolder) holder).setSearchQuery(mSearchQuery);
            ((TopicSearchItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(holder, getItem(position));
                        PublishAnalyticsManager.Companion.getInstance().obtainCurrentModel().setTopicSource(EventLog1.Publish.TopicSource.NEW_TOPIC);
                    }
                }
            });
        }
        Object o = getItem(position);
        holder.bindViews(this, o);
//        if (o instanceof String && TextUtils.equals((String) o, mNewTopicTitle) && holder instanceof TopicSearchTitleViewHolder) {
//            ((TopicSearchTitleViewHolder) holder).setSubTitle(mTextNumberSpace);
//        }

    }

    @Override
    public int getItemViewType(int position) {
        if (CollectionUtil.isEmpty(mTopicSearchSugBean.mTopics)) {
            // 一个Title，一个newTopic
            if (position == 0) {
                return TOPIC_SUG_TITLE_TYPE;
            } else {
                return TOPIC_SUG_ITEM_NEW_TYPE;
            }
        } else if (mTopicSearchSugBean.newTopic == null || TextUtils.isEmpty(mTopicSearchSugBean.newTopic.name)) {
            // 一个Title， 一个 recommend
            if (position == 0) {
                return TOPIC_SUG_TITLE_TYPE;
            } else {
                return TOPIC_SUG_ITEM_TYPE;
            }
        } else {
            // 两个Title， 一个newTopic
            if (position == 0) {
                return TOPIC_SUG_TITLE_TYPE;
            } else if (position == 1) {
                return TOPIC_SUG_ITEM_NEW_TYPE;
            } else if (position == 2) {
                return TOPIC_SUG_TITLE_TYPE;
            } else {
                return TOPIC_SUG_ITEM_TYPE;
            }
        }
    }

    public Object getItem(int position) {
        if (CollectionUtil.isEmpty(mTopicSearchSugBean.mTopics)) {
            // 一个Title，一个newTopic
            if (position == 0) {
                return mNewTopicTitle;
            } else {
                return mTopicSearchSugBean.newTopic;
            }
        } else if (mTopicSearchSugBean.newTopic == null || TextUtils.isEmpty(mTopicSearchSugBean.newTopic.name)) {
            // 两个Title， 一个newTopic
            if (position == 0) {
                return mRecommendTopicTitle;
            } else {
                return mTopicSearchSugBean.mTopics.get(position - 1);
            }
        } else {
            // 两个Title， 一个newTopic
            if (position == 0) {
                return mNewTopicTitle;
            } else if (position == 1) {
                return mTopicSearchSugBean.newTopic;
            } else if (position == 2) {
                return mRecommendTopicTitle;
            } else {
                return mTopicSearchSugBean.mTopics.get(position - 3);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mTopicSearchSugBean == null || (mTopicSearchSugBean.newTopic == null && CollectionUtil.isEmpty(mTopicSearchSugBean.mTopics))) {
            return 0;
        } else if (CollectionUtil.isEmpty(mTopicSearchSugBean.mTopics)) {
            // 一个Title，一个newTopic
            return 2;
        } else if (mTopicSearchSugBean.newTopic == null || TextUtils.isEmpty(mTopicSearchSugBean.newTopic.name)) {
            // 两个Title， 一个newTopic
            return mTopicSearchSugBean.mTopics.size() + 1;
        } else {
            return mTopicSearchSugBean.mTopics.size() + 3;
        }
    }

    public TopicSearchSugBean.TopicBean getNewTopic() {
        return mTopicSearchSugBean != null ? mTopicSearchSugBean.newTopic : null;
    }
}
