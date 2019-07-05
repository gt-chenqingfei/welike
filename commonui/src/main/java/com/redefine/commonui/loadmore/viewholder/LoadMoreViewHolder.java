package com.redefine.commonui.loadmore.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.redefine.commonui.R;
import com.redefine.commonui.loadmore.bean.LoadMoreBean;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by MR on 2018/1/15.
 */

public class LoadMoreViewHolder extends BaseRecyclerViewHolder<LoadMoreBean> {
    private final View mLoadMoreProgress;
    private final ImageView mLoadMoreNoMore;
    private final TextView mLoadMoreText;
    protected final View mContainer;

    private final String mLoadingText;
    private final String mNoMoreText;
    private final String mLoadErrorText;

    public LoadMoreViewHolder(View itemView) {
        super(itemView);
        mLoadMoreProgress = itemView.findViewById(R.id.load_more_progress);
        mLoadMoreNoMore = itemView.findViewById(R.id.load_more_no_more);
        mLoadMoreText = itemView.findViewById(R.id.load_more_text);
        mContainer = itemView.findViewById(R.id.load_more_container);

        mLoadingText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON,"loading");
        mNoMoreText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON,"no_more");
        mLoadErrorText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON,"load_error");
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
                mLoadMoreProgress.setVisibility(View.INVISIBLE);
                mLoadMoreNoMore.setVisibility(View.VISIBLE);
                mLoadMoreNoMore.setImageResource(R.drawable.common_load_more_no_more);
                mLoadMoreText.setText(mNoMoreText);
                break;
            case LOADING:
                mContainer.setVisibility(View.VISIBLE);
                mLoadMoreProgress.setVisibility(View.VISIBLE);
                mLoadMoreNoMore.setVisibility(View.INVISIBLE);
                mLoadMoreText.setText(mLoadingText);
                break;
            case LOADED_ERROR:
                mContainer.setVisibility(View.VISIBLE);
                mLoadMoreProgress.setVisibility(View.INVISIBLE);
                mLoadMoreNoMore.setVisibility(View.VISIBLE);
                mLoadMoreNoMore.setImageResource(R.drawable.common_load_more_error);
                mLoadMoreText.setText(mLoadErrorText);
                break;
        }
    }
}
