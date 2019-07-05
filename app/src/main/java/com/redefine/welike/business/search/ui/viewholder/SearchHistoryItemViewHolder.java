package com.redefine.welike.business.search.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.SugResult;
import com.redefine.welike.business.search.ui.interfaces.ISearchSugOpListener;

/**
 * Created by liwenbo on 2018/3/14.
 */

public class SearchHistoryItemViewHolder extends BaseSearchSugViewHolder<SugResult> {
    private final ImageView mDeleteBtn;
    private final TextView mSearchHistoryText;
    private final ISearchSugOpListener mListener;
    private final View mDivider;

    public SearchHistoryItemViewHolder(View itemView, ISearchSugOpListener listener) {
        super(itemView);
        mDeleteBtn = itemView.findViewById(R.id.search_history_delete_btn);
        mSearchHistoryText = itemView.findViewById(R.id.search_history_item);
        mDivider = itemView.findViewById(R.id.search_his_divider);
        mListener = listener;
        itemView.setBackgroundResource(R.color.white);
    }

    @Override
    public void setSearchQuery(String currentSearchQuery) {
        super.setSearchQuery(currentSearchQuery);
        if (TextUtils.isEmpty(currentSearchQuery)) {
            mDeleteBtn.setImageResource(R.drawable.search_history_delete_icon);
        } else {
            mDeleteBtn.setImageResource(R.drawable.search_sug_arrow_icon);
        }
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final SugResult searchHistory) {
        super.bindViews(adapter, searchHistory);
        if (searchHistory.getObj() instanceof String) {
            mSearchHistoryText.setText(addSpan((String) searchHistory.getObj(), mCurrentSearchQuery));
            if (TextUtils.isEmpty(mCurrentSearchQuery)) {
                mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.deleteHistory((String) searchHistory.getObj());
                        }
                    }
                });
            } else {
                mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.copyToEdit((String) searchHistory.getObj());
                        }
                    }
                });
            }
        }
    }

    public void showFullDivider(boolean b) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mDivider.getLayoutParams();
        int margin = b ? 0 : ScreenUtils.dip2Px(36);
        if (layoutParams != null) {
            layoutParams.leftMargin = margin;
            mDivider.setLayoutParams(layoutParams);
        }
    }
}
