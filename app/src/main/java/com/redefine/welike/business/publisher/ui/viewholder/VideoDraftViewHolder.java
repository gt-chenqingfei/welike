package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.LocalImageLoader;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.publisher.management.bean.DraftPost;
import com.redefine.welike.business.publisher.ui.adapter.IDraftOperationListener;
import com.redefine.welike.common.util.DateTimeUtil;

/**
 * Created by liwenbo on 2018/3/19.
 */

public class VideoDraftViewHolder extends BaseDraftViewHolder<DraftPost> {

    private final RichTextView mDraftTextView;
    private final SimpleDraweeView mDraftCover;
    private final TextView mTime;

    public VideoDraftViewHolder(View inflate, IDraftOperationListener listener) {
        super(inflate, listener);
        mDraftTextView = itemView.findViewById(R.id.text_draft_rich_text);
        mDraftCover = itemView.findViewById(R.id.video_draft_cover);
        mTime = itemView.findViewById(R.id.draft_item_time);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, DraftPost feedBase) {
        super.bindViews(adapter, feedBase);
        mDraftTextView.getRichProcessor().setRichContent(feedBase.getContent().summary, feedBase.getContent().richItemList);
        LocalImageLoader.getInstance().loadFixedSizeImageFromLocal(mDraftCover, feedBase.getVideo().getLocalFileName());
        mTime.setText(DateTimeUtil.INSTANCE.formatPostPublishTime(mTime.getResources(), feedBase.getTime()));
    }
}
