package com.redefine.welike.business.user.ui.adapter;

import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.welike.R;
import com.redefine.welike.business.user.ui.viewholder.ProfilePhotoViewHolder;
import com.redefine.welike.business.videoplayer.management.bean.AttachmentBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nianguowang on 2018/10/10
 */
public class ProfilePhotoAdapter extends HeaderAndFooterRecyclerViewAdapter {

    private List<AttachmentBase> mAttachments = new ArrayList<>();
    private OnAttachmentClickListener mListener;

    public void setAttachmentClickListener(OnAttachmentClickListener listener) {
        mListener = listener;
    }

    public interface OnAttachmentClickListener {
        void onAttachmentClick(AttachmentBase attachmentBase);
    }

    public void setData(final List<AttachmentBase> attachmentBases) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mAttachments.size();
            }

            @Override
            public int getNewListSize() {
                return attachmentBases.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return TextUtils.equals(
                        mAttachments.get(oldItemPosition).getAid(),
                        attachmentBases.get(newItemPosition).getAid()
                );
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mAttachments.get(oldItemPosition) == attachmentBases.get(newItemPosition);
            }
        });
        diffResult.dispatchUpdatesTo(new AdapterListUpdateCallback(this));
        mAttachments.clear();
        mAttachments.addAll(attachmentBases);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ProfilePhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_photo_item, null));
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProfilePhotoViewHolder) {
            final AttachmentBase attachmentBase = mAttachments.get(position);
            ((ProfilePhotoViewHolder) holder).bindView(attachmentBase);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onAttachmentClick(attachmentBase);
                    }
                }
            });
        }
    }

    @Override
    protected int getRealItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRealItemCount() {
        return CollectionUtil.getCount(mAttachments);
    }

    @Override
    protected Object getRealItem(int position) {
        return mAttachments.get(position);
    }
}
