package com.redefine.welike.business.publisher.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.LocalImageLoader;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.richtext.RichTextView;
import com.redefine.welike.R;
import com.redefine.welike.base.util.TimeUtil;
import com.redefine.welike.business.publisher.management.bean.DraftPost;
import com.redefine.welike.business.publisher.ui.adapter.IDraftOperationListener;
import com.redefine.welike.common.util.DateTimeUtil;

/**
 * Created by liwenbo on 2018/3/19.
 */

public class PicDraftViewHolder extends BaseDraftViewHolder<DraftPost> {

    private final RichTextView mDraftRichText;
    private final SimpleDraweeView mDraftImg;
    private final TextView mTime;

    public PicDraftViewHolder(View inflate, IDraftOperationListener listener) {
        super(inflate, listener);
        mDraftRichText = itemView.findViewById(R.id.text_draft_rich_text);
        mDraftImg = itemView.findViewById(R.id.pic_draft_img);
        mTime = itemView.findViewById(R.id.draft_item_time);
    }

    @Override
    public void bindViews(RecyclerView.Adapter adapter, DraftPost feedBase) {
        super.bindViews(adapter, feedBase);
        mDraftRichText.getRichProcessor().setRichContent(feedBase.getContent().summary, feedBase.getContent().richItemList);
        if (!CollectionUtil.isEmpty(feedBase.getPicDraftList())) {
            LocalImageLoader.getInstance().loadFixedSizeImageFromLocal(mDraftImg, feedBase.getPicDraftList().get(0).getLocalFileName());
        }
        mTime.setText(DateTimeUtil.INSTANCE.formatPostPublishTime(mTime.getResources(), feedBase.getTime()));
    }
}
