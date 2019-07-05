package com.redefine.welike.business.search.ui.viewholder;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;

/**
 * Created by liwenbo on 2018/3/14.
 */

public class BaseSearchSugViewHolder<T> extends BaseRecyclerViewHolder<T> {

    protected String mCurrentSearchQuery;

    public BaseSearchSugViewHolder(View itemView) {
        super(itemView);
    }

    public void setSearchQuery(String currentSearchQuery) {
        mCurrentSearchQuery = currentSearchQuery;
    }

    public Spannable addSpan(String content, String keyword) {
        if (TextUtils.isEmpty(keyword) || TextUtils.isEmpty(content)) {
            if(TextUtils.isEmpty(content)){
                return new SpannableStringBuilder("");

            }else {
                return new SpannableStringBuilder(content);

            }
        }
        String contentLow = content.toLowerCase();
        String keyWordLow = keyword.toLowerCase();
        if (!TextUtils.isEmpty(keyword)&&contentLow.startsWith(keyWordLow)) {
            ForegroundColorSpan span = new ForegroundColorSpan(itemView.getResources().getColor(R.color.color_48779D));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
            spannableStringBuilder.setSpan(span, 0, keyword.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableStringBuilder;
        }
        return new SpannableStringBuilder(content);
    }
}
