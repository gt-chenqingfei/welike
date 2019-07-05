package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.business.publisher.management.bean.TopicSearchSugBean;

/**
 * Created by liwenbo on 2018/4/11.
 */

public class TopicSearchItemViewHolder extends BaseRecyclerViewHolder<TopicSearchSugBean.TopicBean> {

    protected final TextView mSugText, markView;
    protected String mSearchQuery;

    public TopicSearchItemViewHolder(View itemView) {
        super(itemView);
        mSugText = itemView.findViewById(R.id.item_title);
        markView = itemView.findViewById(R.id.item_mark);
    }

    public void setSearchQuery(String searchQuery) {
        mSearchQuery = searchQuery;
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, TopicSearchSugBean.TopicBean data) {
        super.bindViews(adapter, data);
        mSugText.setText(addSpan(data.name, mSearchQuery));
        if (TextUtils.isEmpty(data.id)) {
            markView.setVisibility(View.VISIBLE);
        } else {
            markView.setVisibility(View.GONE);
        }

    }

    public Spannable addSpan(String content, String keyword) {
        if (TextUtils.isEmpty(keyword) || TextUtils.isEmpty(content)) {
            return new SpannableStringBuilder(content);
        }
        String contentLow = content.toLowerCase();
        String keyWordLow = keyword.toLowerCase();
        if (contentLow.startsWith(keyWordLow)) {
            ForegroundColorSpan span = new ForegroundColorSpan(itemView.getResources().getColor(R.color.main));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
            spannableStringBuilder.setSpan(span, 0, keyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableStringBuilder;
        }
        return new SpannableStringBuilder(content);
    }

    public void markNew(String mark) {
        markView.setText(mark);
    }
}
