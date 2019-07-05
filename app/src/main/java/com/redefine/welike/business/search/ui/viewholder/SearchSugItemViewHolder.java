package com.redefine.welike.business.search.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.SugResult;
import com.redefine.welike.business.search.ui.interfaces.ISearchSugOpListener;

/**
 * Created by liwenbo on 2018/3/14.
 */

public class SearchSugItemViewHolder extends BaseSearchSugViewHolder<SugResult> {
    private final View mSugBtn;
    private final TextView mSearchSugText;
    private final ISearchSugOpListener mListener;
    public SearchSugItemViewHolder(View itemView, ISearchSugOpListener listener) {

        super(itemView);
        mSugBtn = itemView.findViewById(R.id.search_sug_btn);
        mSearchSugText = itemView.findViewById(R.id.search_sug_item);
        mListener = listener;
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final SugResult sug) {
        super.bindViews(adapter, sug);
        if (sug.getObj() instanceof String) {
            mSearchSugText.setText(addSpan((String) sug.getObj(), mCurrentSearchQuery));
            mSugBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.copyToEdit((String) sug.getObj());
                    }
                }
            });
        }

    }
}
