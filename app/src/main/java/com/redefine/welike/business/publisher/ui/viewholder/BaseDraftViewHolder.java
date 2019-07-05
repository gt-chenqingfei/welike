package com.redefine.welike.business.publisher.ui.viewholder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.redefine.commonui.loadmore.viewholder.BaseRecyclerViewHolder;
import com.redefine.welike.R;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.business.publisher.management.PublisherEventManager;
import com.redefine.welike.business.publisher.management.bean.DraftBase;
import com.redefine.welike.business.publisher.ui.activity.PublishDraftStarter;
import com.redefine.welike.business.publisher.ui.adapter.IDraftOperationListener;

/**
 * Created by liwenbo on 2018/3/19.
 */

public abstract class BaseDraftViewHolder<T extends DraftBase> extends BaseRecyclerViewHolder<T> {
    private final View mDeleteBtn;
    private final SwipeLayout mSwipeLayout;
    private final View mDraftContent;
    private final IDraftOperationListener mListener;
    private final TextView mReSendBtn;

    public BaseDraftViewHolder(View itemView, IDraftOperationListener listener) {
        super(itemView);
        mListener = listener;
        mSwipeLayout = itemView.findViewById(R.id.draft_item_swipe_layout);
        mSwipeLayout.setClickToClose(true);
        mDraftContent = itemView.findViewById(R.id.draft_content_view);
        mDeleteBtn = itemView.findViewById(R.id.draft_delete_btn);
        mReSendBtn = itemView.findViewById(R.id.draft_resend_btn);
        mReSendBtn.setText(ResourceTool.getString(ResourceTool.ResourceFileEnum.PUBLISH, "editor_resend_btn"));
    }

    @Override
    public void bindViews(final RecyclerView.Adapter adapter, final T draftBase) {
        super.bindViews(adapter, draftBase);
        mDraftContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				PublishDraftStarter.INSTANCE.startActivityWidthDraft(v.getContext(), draftBase);
                PublisherEventManager.INSTANCE.setSource(5);
                PublisherEventManager.INSTANCE.setMain_source(0);

                if (v.getContext() instanceof Activity) {
                    ((Activity) v.getContext()).finish();
                }
            }
        });
        mReSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onResend(draftBase);
                }
            }
        });
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDelete(draftBase);
            }
        });
    }
}
