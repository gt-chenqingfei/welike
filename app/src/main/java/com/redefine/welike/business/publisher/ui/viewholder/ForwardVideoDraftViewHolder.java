package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.LinkThumbUrlLoader;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.feeds.management.bean.PostBase;
import com.redefine.welike.business.feeds.management.bean.VideoPost;
import com.redefine.welike.business.publisher.management.bean.DraftForwardPost;
import com.redefine.welike.business.publisher.ui.adapter.IDraftOperationListener;
import com.redefine.welike.common.util.DateTimeUtil;

/**
 * Created by liwenbo on 2018/3/19.
 */

public class ForwardVideoDraftViewHolder extends BaseDraftViewHolder<DraftForwardPost> {
    private final RichTextView mDraftTextView;
    private final SimpleDraweeView mDraftCover;
    private final TextView mDraftNick;
    private final RichTextView mForwardTextView;
    private final TextView mTime;

    public ForwardVideoDraftViewHolder(View inflate, IDraftOperationListener listener) {
        super(inflate, listener);
        mDraftTextView = itemView.findViewById(R.id.text_draft_rich_text);
        mDraftCover = itemView.findViewById(R.id.video_draft_cover);
        mDraftNick = itemView.findViewById(R.id.draft_item_nick);
        mForwardTextView = itemView.findViewById(R.id.forward_text_draft_rich_text);
        mTime = itemView.findViewById(R.id.draft_item_time);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, DraftForwardPost feedBase) {
        super.bindViews(adapter, feedBase);
        mDraftTextView.getRichProcessor().setRichContent(feedBase.getContent().summary, feedBase.getContent().richItemList);
        PostBase postBase = feedBase.getRootPost();
        if (postBase instanceof VideoPost && !TextUtils.isEmpty(((VideoPost) postBase).getCoverUrl())) {
            LinkThumbUrlLoader.getInstance().loadLinkThumbUrl(mDraftCover, (((VideoPost) postBase).getCoverUrl()));
        }
        mDraftNick.setText(feedBase.getRootPost().getNickName());
        mForwardTextView.getRichProcessor().setRichContent(feedBase.getRootPost().getSummary(), feedBase.getRootPost().getRichItemList());
        mTime.setText(DateTimeUtil.INSTANCE.formatPostPublishTime(mTime.getResources(), feedBase.getTime()));
        mForwardTextView.setMovementMethod(null);
    }
}
