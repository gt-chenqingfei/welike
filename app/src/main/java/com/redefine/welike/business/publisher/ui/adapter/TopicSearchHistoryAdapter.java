package com.redefine.welike.business.publisher.ui.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.BaseRecyclerAdapter;
import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.base.dao.welike.TopicSearchHistory;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.management.bean.HotTopics;
import com.redefine.welike.business.publisher.ui.viewholder.TopicSearchHotTopicItemViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.TopicSearchItemHisViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.TopicSearchTipsViewHolder;
import com.redefine.welike.business.publisher.ui.viewholder.TopicSearchTitleViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/4/10.
 */

public class TopicSearchHistoryAdapter extends BaseRecyclerAdapter<BaseRecyclerViewHolder> {

    private static final int SEARCH_HISTORY_TIPS_TYPE = 0;
    private static final int SEARCH_HISTORY_RECENT_TITLE_TYPE = 1;
    private static final int SEARCH_HISTORY_RECENT_ITEM = 2;
    private static final int SEARCH_HISTORY_HOT_TOPIC_TITLE_ITEM = 3;
    private static final int SEARCH_HISTORY_HOT_TOPIC_ITEM = 4;

    private final List<TopicSearchHistory> mData = new ArrayList<>();
    private final List<HotTopics.HotTopic> mTopics = new ArrayList<>();
    private final String mTopicSearchTitle;
    private final String mTipsText;
    private final String mHotTopicTitle;
    private LayoutInflater mLayoutInflater;
    private HeaderAndFooterRecyclerViewAdapter.OnItemClickListener mListener;

    public TopicSearchHistoryAdapter() {
        mTopicSearchTitle = ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "recent_topic_title_text");
        mTipsText = ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "topic_search_tips");
        mHotTopicTitle = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON, "hot");
    }

    @NonNull
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        if (viewType == SEARCH_HISTORY_TIPS_TYPE) {
            return new TopicSearchTipsViewHolder(mLayoutInflater.inflate(R.layout.topic_search_tips_layout, parent, false));
        } else if (viewType == SEARCH_HISTORY_RECENT_TITLE_TYPE) {
            return new TopicSearchTitleViewHolder(mLayoutInflater.inflate(R.layout.topic_search_title_layout, parent, false));
        } else if (viewType == SEARCH_HISTORY_RECENT_ITEM) {
            return new TopicSearchItemHisViewHolder(mLayoutInflater.inflate(R.layout.topic_search_sug_item_layout, parent, false));
        } else if (viewType == SEARCH_HISTORY_HOT_TOPIC_TITLE_ITEM) {
            return new TopicSearchTipsViewHolder(mLayoutInflater.inflate(R.layout.topic_search_title_layout, parent, false));
        } else if (viewType == SEARCH_HISTORY_HOT_TOPIC_ITEM) {
            return new TopicSearchHotTopicItemViewHolder(mLayoutInflater.inflate(R.layout.topic_search_hot_topic_item_layout, parent, false));
        }
        return new TopicSearchItemHisViewHolder(mLayoutInflater.inflate(R.layout.topic_search_sug_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseRecyclerViewHolder holder, final int position) {
        holder.bindViews(this, getItem(position));
        if (holder instanceof TopicSearchItemHisViewHolder || holder instanceof TopicSearchHotTopicItemViewHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(holder, getItem(position));
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(HeaderAndFooterRecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return SEARCH_HISTORY_TIPS_TYPE;
        }
        position--;
        if (mTopics.size() > 0) {
            // add  topics and topic title
            if (position < mTopics.size() + 1) {
                if (position == 0) {
                    return SEARCH_HISTORY_HOT_TOPIC_TITLE_ITEM;
                }

                return SEARCH_HISTORY_HOT_TOPIC_ITEM;
            }
            position -= (mTopics.size() + 1);
        }

        if (mData.size() > 0) {
            // add search history and history title
            if (position < mData.size() + 1) {
                if (position == 0) {
                    return SEARCH_HISTORY_RECENT_TITLE_TYPE;
                }
                return SEARCH_HISTORY_RECENT_ITEM;
            }

        }
        return SEARCH_HISTORY_RECENT_ITEM;
    }

    public Object getItem(int position) {
        if (position == 0) {
            return mTipsText;
        }
        position--;
        if (mTopics.size() > 0) {
            // add  topics and topic title
            if (position < mTopics.size() + 1) {
                if (position == 0) {
                    return mHotTopicTitle;
                }
                position--;
                return mTopics.get(position);
            }
            position -= (mTopics.size() + 1);
        }

        if (mData.size() > 0) {
            // add search history and history title
            if (position < mData.size() + 1) {
                if (position == 0) {
                    return mTopicSearchTitle;
                }
                position--;
                return mData.get(position);
            }

        }
        return null;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        // add tips
        count += 1;
        if (mTopics.size() > 0) {
            // add  topics and topic title
            count += mTopics.size() + 1;
        }
        if (mData.size() > 0) {
            // add search history and history title
            count += mData.size() + 1;
        }
        return count;
    }

    public void setData(List<TopicSearchHistory> topicSearchHistories) {
        mData.clear();
        if (!CollectionUtil.isEmpty(topicSearchHistories)) {
            mData.addAll(topicSearchHistories);
        }
        notifyDataSetChanged();
    }

    public void setTopics(List<HotTopics.HotTopic> shortCutTopics) {
        mTopics.clear();
        if (!CollectionUtil.isEmpty(shortCutTopics)) {
            mTopics.addAll(shortCutTopics);
        }
        notifyDataSetChanged();
    }
}
