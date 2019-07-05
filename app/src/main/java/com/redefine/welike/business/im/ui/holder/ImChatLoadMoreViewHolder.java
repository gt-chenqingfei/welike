package com.redefine.welike.business.im.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.commonui.loadmore.bean.LoadMoreBean;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;

/**
 * Created by liwenbo on 2018/2/11.
 */

public class ImChatLoadMoreViewHolder extends BaseRecyclerViewHolder<LoadMoreBean> {

    private final View mLoadMoreProgress;
    private final ImageView mLoadMoreNoMore;
    private final TextView mLoadMoreText;
    private final String mLoadingText;
    private final String mNoMoreText;
    private final View mContainer;

    public ImChatLoadMoreViewHolder(View itemView) {
        super(itemView);
        mLoadMoreProgress = itemView.findViewById(R.id.load_more_progress);
        mLoadMoreNoMore = itemView.findViewById(R.id.load_more_no_more);
        mLoadMoreText = itemView.findViewById(R.id.load_more_text);
        mContainer = itemView.findViewById(R.id.load_more_container);
        mLoadingText = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "im_chat_loading_text");
        mNoMoreText = ResourceTool.getString(ResourceTool.ResourceFileEnum.IM, "im_chat_no_more_text");
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, LoadMoreBean loadMoreBean) {
        super.bindViews(adapter, loadMoreBean);
        if (loadMoreBean == null) {
            return ;
        }
        super.bindViews(adapter, loadMoreBean);
        switch (loadMoreBean.getState()) {
            case NONE:
            case LOADED_ERROR:
                mContainer.setVisibility(View.GONE);
                break;
            case NO_MORE:
                mContainer.setVisibility(View.VISIBLE);
                mLoadMoreProgress.setVisibility(View.INVISIBLE);
                mLoadMoreNoMore.setVisibility(View.VISIBLE);
                mLoadMoreText.setText(mNoMoreText);
                break;
            case LOADING:
                mContainer.setVisibility(View.VISIBLE);
                mLoadMoreProgress.setVisibility(View.VISIBLE);
                mLoadMoreNoMore.setVisibility(View.INVISIBLE);
                mLoadMoreText.setText(mLoadingText);
                break;
        }
    }
}
