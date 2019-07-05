package com.redefine.welike.business.feeds.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.bean.LoadMoreBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;

/**
 * Created by liwenbo on 2018/2/12.
 */

public class FeedLoadMoreViewHolder extends BaseRecyclerViewHolder<LoadMoreBean> {

    private final View mLoadMoreProgress;
    private final TextView mLoadMoreText;
    private final View mContainer;

    public FeedLoadMoreViewHolder(View itemView) {
        super(itemView);
        mLoadMoreProgress = itemView.findViewById(R.id.load_more_progress);
        mLoadMoreText = itemView.findViewById(R.id.load_more_text);
        mContainer = itemView.findViewById(R.id.load_more_container);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, LoadMoreBean loadMoreBean) {
        super.bindViews(adapter, loadMoreBean);
        if (loadMoreBean == null) {
            return;
        }
        super.bindViews(adapter, loadMoreBean);
        switch (loadMoreBean.getState()) {
            case NONE:
                mContainer.setVisibility(View.GONE);
                break;
            case NO_MORE:
                mContainer.setVisibility(View.VISIBLE);
                mLoadMoreProgress.setVisibility(View.GONE);
                mLoadMoreText.setText(R.string.feed_foot_no_more_text);
                mContainer.getLayoutParams().height = ScreenUtils.dip2Px(mContainer.getContext(), 72);
                mContainer.setLayoutParams(mContainer.getLayoutParams());

                break;
            case LOADING:
                mContainer.setVisibility(View.VISIBLE);
                mLoadMoreProgress.setVisibility(View.VISIBLE);
                mLoadMoreText.setText(R.string.feed_foot_loading_text);
                break;
            case LOADED_ERROR:
                mContainer.setVisibility(View.VISIBLE);
                mLoadMoreProgress.setVisibility(View.GONE);
                mLoadMoreText.setText(R.string.feed_foot_load_error_text);
                break;
        }
    }
}
