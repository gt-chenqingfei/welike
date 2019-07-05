package com.redefine.welike.business.feeds.ui.viewholder;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.commonui.loadmore.bean.LoadMoreBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by nianguowang on 2018/4/17
 */
public class BackToTopViewHolder extends BaseRecyclerViewHolder<LoadMoreBean> {

    private final View mLoadMoreProgress;
    private final ImageView mLoadMoreNoMore;
    private final TextView mLoadMoreText;
    protected final View mContainer;

    private final String mLoadingText;
    private final String mNoMoreText;
    private final String mLoadErrorText;
    public BackToTopViewHolder(View itemView) {
        super(itemView);
        mLoadMoreProgress = itemView.findViewById(com.redefine.commonui.R.id.load_more_progress);
        mLoadMoreNoMore = itemView.findViewById(com.redefine.commonui.R.id.load_more_no_more);
        mLoadMoreText = itemView.findViewById(com.redefine.commonui.R.id.load_more_text);
        mContainer = itemView.findViewById(com.redefine.commonui.R.id.load_more_container);

        mLoadingText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON,"loading");
        mNoMoreText = ResourceTool.getString(ResourceTool.ResourceFileEnum.COMMON,"back_to_top");
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
                mLoadMoreNoMore.setImageResource(R.drawable.back_to_top);
                mLoadMoreText.setText(mNoMoreText);
                mLoadMoreText.setTextColor(Color.parseColor("#2C97ED"));
                break;
            case LOADING:
                mContainer.setVisibility(View.VISIBLE);
                mLoadMoreProgress.setVisibility(View.VISIBLE);
                mLoadMoreNoMore.setVisibility(View.INVISIBLE);
                mLoadMoreText.setText(mLoadingText);
                mLoadMoreText.setTextColor(Color.parseColor("#B4B4B4"));
                break;
            case LOADED_ERROR:
                mContainer.setVisibility(View.VISIBLE);
                mLoadMoreProgress.setVisibility(View.INVISIBLE);
                mLoadMoreNoMore.setVisibility(View.VISIBLE);
                mLoadMoreNoMore.setImageResource(com.redefine.commonui.R.drawable.common_load_more_error);
                mLoadMoreText.setText(mLoadErrorText);
                mLoadMoreText.setTextColor(Color.parseColor("#B4B4B4"));
                break;
        }
    }
}
