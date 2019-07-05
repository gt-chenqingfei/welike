package com.redefine.welike.business.search.ui.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.SugResult;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;
import com.redefine.welike.business.search.ui.bean.SearchFootBean;
import com.redefine.welike.business.search.ui.interfaces.ISearchSugOpListener;
import com.redefine.welike.business.search.ui.viewholder.BaseSearchSugViewHolder;
import com.redefine.welike.business.search.ui.viewholder.SearchAllHistoryItemViewHolder;
import com.redefine.welike.business.search.ui.viewholder.SearchHistoryItemViewHolder;
import com.redefine.welike.business.search.ui.viewholder.SearchHistoryTitleViewHolder;
import com.redefine.welike.business.search.ui.viewholder.SearchSugItemViewHolder;
import com.redefine.welike.business.search.ui.viewholder.SearchSugMoiveItemViewHolder;
import com.redefine.welike.business.search.ui.viewholder.SearchSugUserViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwenbo on 2018/3/14.
 */

public class SearchSugAdapter extends HeaderAndFooterRecyclerViewAdapter<BaseSearchSugViewHolder, BaseHeaderBean, SugResult, SearchFootBean> {

    private static final int SEARCH_TYPE_SUG = 3;
    private static final int SEARCH_TYPE_USER = 2;
    private static final int SEARCH_TYPE_HIS = 1;
    private static final int SEARCH_TYPE_MOVIE = 4;
    private final ISearchSugOpListener mListener;
    private String mCurrentSearchQuery = null;
    private final List<SugResult> mData = new ArrayList<>();
    private final List<SugResult> mSearchHistory = new ArrayList<>();
    private boolean mIsShowAllHistory = true;
    private SearchFootBean mSearchFooterBean = new SearchFootBean();
    private OnTopicClickListener mOnTopicClickListener;

    public SearchSugAdapter(ISearchSugOpListener listener) {
        mListener = listener;
    }

    public void setSearchHistory(List<SugResult> searchHistory, boolean isShowAllHistory) {
        mIsShowAllHistory = isShowAllHistory;
        mSearchHistory.clear();
        if (!CollectionUtil.isEmpty(searchHistory)) {
            mSearchHistory.addAll(searchHistory);
        }
        mCurrentSearchQuery = null;
        mData.clear();
        if (!CollectionUtil.isEmpty(mSearchHistory)) {
            mData.addAll(mSearchHistory);
        }
        if (!CollectionUtil.isEmpty(mData)) {
            if (!hasHeader()) {
                setHeader(new BaseHeaderBean());
            }
            if (mIsShowAllHistory) {
                setShowFooter(true);
            } else {
                setShowFooter(false);
            }
        } else {
            setHeader(null);
            setShowFooter(false);
        }
        notifyDataSetChanged();
    }

    public void setTopicData(List<TopicSearchSugBean.TopicBean> topicBeans) {
        mSearchFooterBean.setTopicBeans(topicBeans);
        setFooter(mSearchFooterBean);
        showFooter();
        notifyItemChanged(getFooterPosition());
    }

    public void showSearchHistory() {
        mCurrentSearchQuery = null;
        mData.clear();
        if (!CollectionUtil.isEmpty(mSearchHistory)) {
            mData.addAll(mSearchHistory);
        }
        if (!CollectionUtil.isEmpty(mData)) {
            if (!hasHeader()) {
                setHeader(new BaseHeaderBean());
            }
            if (mIsShowAllHistory) {
                setShowFooter(true);
            } else {
                setShowFooter(false);
            }
        } else {
            setHeader(null);
            setShowFooter(false);
        }
        notifyDataSetChanged();
    }

    public void showSearchSugList(String searchQuery, List<SugResult> sugList) {
        if (TextUtils.isEmpty(searchQuery)) {
            return;
        }
        mCurrentSearchQuery = searchQuery;
        mData.clear();
        if (!CollectionUtil.isEmpty(sugList)) {
            mData.addAll(sugList);
        }
        setHeader(null);
        setFooter(null);
        notifyDataSetChanged();
    }

    public boolean getShowAllHistory() {
        return mIsShowAllHistory;
    }

    private void setShowFooter(boolean showFooter) {
        mSearchFooterBean.setShowMoreHistory(showFooter);
        setFooter(mSearchFooterBean);
    }

    @Override
    protected BaseSearchSugViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new SearchAllHistoryItemViewHolder(mInflater.inflate(R.layout.search_all_history_layout, parent, false), mOnTopicClickListener);
    }

    @Override
    protected BaseSearchSugViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new SearchHistoryTitleViewHolder(mInflater.inflate(R.layout.search_history_title_layout, parent, false), mListener);

    }

    @Override
    protected void onBindHeaderViewHolder(BaseSearchSugViewHolder holder, int position) {
        holder.bindViews(this, getHeader());
    }

    @Override
    protected void onBindFooterViewHolder(BaseSearchSugViewHolder holder, int position) {
        holder.bindViews(this, getFooter());
    }

    @Override
    protected BaseSearchSugViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SEARCH_TYPE_HIS) {
            return new SearchHistoryItemViewHolder(mInflater.inflate(R.layout.search_his_item, parent, false), mListener);
        } else if (viewType == SEARCH_TYPE_USER) {
            return new SearchSugUserViewHolder(mInflater.inflate(R.layout.search_user_item_layout, parent, false));
        } else if (viewType == SEARCH_TYPE_SUG) {
            return new SearchSugItemViewHolder(mInflater.inflate(R.layout.search_sug_item_layout, parent, false), mListener);
        } else if (viewType == SEARCH_TYPE_MOVIE) {
            return new SearchSugMoiveItemViewHolder(mInflater.inflate(R.layout.search_sug_movie_item_layout, parent, false), mListener);
        }
        return null;
    }

    @Override
    protected void onBindItemViewHolder(BaseSearchSugViewHolder holder, int position) {
        holder.setSearchQuery(mCurrentSearchQuery);
        holder.bindViews(this, mData.get(position));
        if (holder instanceof SearchHistoryItemViewHolder && hasFooter()) {
            ((SearchHistoryItemViewHolder) holder).showFullDivider(position == mData.size() - 1);
        }

    }

    @Override
    protected int getRealItemViewType(int position) {
        SugResult result = mData.get(position);
        if (result.getType() == SugResult.SUG_RESULT_TYPE_HIS) {
            return SEARCH_TYPE_HIS;
        } else if (result.getType() == SugResult.SUG_RESULT_TYPE_SUG) {
            if (result.getCategory() == SugResult.SUG_RESULT_CATEGORY_USER) {
                return SEARCH_TYPE_USER;
            } else if (result.getCategory() == SugResult.SUG_RESULT_CATEGORY_KEYWORD) {
                return SEARCH_TYPE_SUG;
            } else if (result.getCategory() == SugResult.SUG_RESULT_CATEGORY_MOVIE) {
                return SEARCH_TYPE_MOVIE;
            }
        }
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return mData.size();
    }

    @Override
    protected SugResult getRealItem(int position) {
        if (position < 0 || position >= mData.size()) return null;
        return mData.get(position);
    }

    public void delete(String searchHistory) {
        for (int i = 0; i < mData.size(); i++) {
            if (TextUtils.equals(searchHistory, (String) mData.get(i).getObj())) {
                int position = getAdapterItemPosition(i);
                mData.remove(i);
                notifyItemRemoved(position);
                break;
            }
        }
    }

    public void setOnTopicClickListener(OnTopicClickListener listener) {
        mOnTopicClickListener = listener;
    }

    public interface OnTopicClickListener {
        void onTopicClick(TopicSearchSugBean.TopicBean topicBean);

        void onMoreTopicClick();

        void onMoreHistoryClick();
    }
}
