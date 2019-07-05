package com.redefine.welike.business.search.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.welike.R;
import com.redefine.welike.business.feeds.management.bean.SugResult;
import com.redefine.welike.business.search.ui.bean.MovieSugBean;
import com.redefine.welike.business.search.ui.interfaces.ISearchSugOpListener;

public class SearchSugMoiveItemViewHolder extends BaseSearchSugViewHolder<SugResult> {
    private TextView movieContent;
    private  View mSugBtn;
    private final ISearchSugOpListener mListener;

    public SearchSugMoiveItemViewHolder(View itemView, ISearchSugOpListener listener) {
        super(itemView);
        movieContent=itemView.findViewById(R.id.movie_content);
        mSugBtn = itemView.findViewById(R.id.search_sug_btn);
        mListener = listener;

    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, final SugResult data) {
        super.bindViews(adapter, data);
        if (data.getObj() instanceof MovieSugBean) {
            movieContent.setText(addSpan( ((MovieSugBean)data.getObj()).getShowText(), mCurrentSearchQuery));
            mSugBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.copyToEdit(((MovieSugBean)data.getObj()).getShowText());
                    }
                }
            });
        }
    }
}
