package com.redefine.welike.business.search.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.bean.BaseHeaderBean;
import com.redefine.welike.MyApplication;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.search.ui.interfaces.ISearchSugOpListener;

/**
 * Created by liwenbo on 2018/3/14.
 */

public class SearchHistoryTitleViewHolder extends BaseSearchSugViewHolder<BaseHeaderBean> {

    private final View mDeleteAllBtn;
    private final TextView mTitle;
    private final ISearchSugOpListener mListener;

    public SearchHistoryTitleViewHolder(View itemView, ISearchSugOpListener searchSugOpListener) {
        super(itemView);
        mListener = searchSugOpListener;
        mDeleteAllBtn = itemView.findViewById(R.id.search_history_delete_all_btn);
        mTitle = itemView.findViewById(R.id.search_history_title_text);
        mTitle.setText(MyApplication.getAppContext().getResources().getString(R.string.recent_search));
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, BaseHeaderBean feedBase) {
        super.bindViews(adapter, feedBase);
        mDeleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.clearAllHistory();
            }
        });
    }
}
