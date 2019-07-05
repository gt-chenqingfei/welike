package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.publisher.management.bean.DraftBase;
import com.redefine.welike.business.publisher.ui.adapter.IDraftOperationListener;
import com.redefine.welike.common.util.DateTimeUtil;

/**
 * Created by liwenbo on 2018/3/19.
 */

public class TextDraftViewHolder extends BaseDraftViewHolder<DraftBase> {

    private final RichTextView mDraftRichText;
    private final TextView mTime;

    public TextDraftViewHolder(View inflate, IDraftOperationListener listener) {
        super(inflate, listener);
        mDraftRichText = itemView.findViewById(R.id.text_draft_rich_text);
        mTime = itemView.findViewById(R.id.draft_item_time);
    }


    @Override
    public void bindViews(RecyclerView.Adapter adapter, DraftBase feedBase) {
        super.bindViews(adapter, feedBase);
        mDraftRichText.getRichProcessor().setRichContent(feedBase.getContent().summary, feedBase.getContent().richItemList);
        mTime.setText(DateTimeUtil.INSTANCE.formatPostPublishTime(mTime.getResources(), feedBase.getTime()));
    }
}
