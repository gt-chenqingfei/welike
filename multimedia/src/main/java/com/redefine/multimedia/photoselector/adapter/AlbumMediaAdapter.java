package com.redefine.multimedia.photoselector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redefine.commonui.loadmore.adapter.BaseRecyclerAdapter;
import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.entity.IncapableCause;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.model.SelectedItemCollection;
import com.redefine.multimedia.photoselector.viewholder.AlbumCameraViewHolder;
import com.redefine.multimedia.photoselector.viewholder.AlbumMediaFooterViewHolder;
import com.redefine.multimedia.photoselector.viewholder.AlbumMediaViewHolder;
import com.redefine.multimedia.photoselector.viewholder.BaseAlbumItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AlbumMediaAdapter extends BaseRecyclerAdapter<BaseAlbumItemViewHolder> {

    private static final int VIEW_TYPE_CAPTURE = 1;
    private static final int VIEW_TYPE_MEDIA = 2;
    private static final int VIEW_TYPE_FOOTER = 3;
    private final SelectedItemCollection mSelectedCollection;
    private final LayoutInflater mInflater;
    private final ImagePickConfig mConfig;
    private CheckStateListener mCheckStateListener;
    private OnMediaClickListener mOnMediaClickListener;
    private final ArrayList<Item> mItems = new ArrayList<>();
    boolean isVideoSelect;

    public AlbumMediaAdapter(Context context, SelectedItemCollection selectedCollection, ImagePickConfig config) {
        if (selectedCollection.getmItems() != null && !selectedCollection.getmItems().isEmpty()) {
            isVideoSelect = selectedCollection.asList().get(0).isVideo();
        }
        mSelectedCollection = selectedCollection;
        mInflater = LayoutInflater.from(context);
        mConfig = config;
    }

    public void setItems(List<Item> items) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(mItems, items), true);
        mItems.clear();
        if (!CollectionUtil.isEmpty(items)) {
            mItems.addAll(items);
        }
        diffResult.dispatchUpdatesTo(this);

    }

    @Override
    public BaseAlbumItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CAPTURE) {
            return new AlbumCameraViewHolder(mInflater.inflate(R.layout.media_camera_item_layout, parent, false));
        } else if (viewType == VIEW_TYPE_MEDIA) {
            return new AlbumMediaViewHolder(mInflater.inflate(R.layout.media_item_layout, parent, false));
        } else if (viewType == VIEW_TYPE_FOOTER) {
            return new AlbumMediaFooterViewHolder(mInflater.inflate(R.layout.media_item_footer_layout, parent, false));
        }
        return new AlbumMediaFooterViewHolder(mInflater.inflate(R.layout.media_item_footer_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BaseAlbumItemViewHolder holder, int position) {
        if (holder instanceof AlbumMediaFooterViewHolder) {
            return;
        }
        final Item item = mItems.get(position);
        holder.bindViews(item, mSelectedCollection, mConfig, isVideoSelect);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnMediaClickListener != null) {
                    mOnMediaClickListener.onMediaClick(holder.getAdapterPosition(), item);
                }

                if (holder instanceof AlbumMediaViewHolder) {
                    isVideoSelect = item.isVideo();
                    onCheckViewClicked(item, holder);
                }
            }
        });

//        if (holder instanceof AlbumMediaViewHolder) {
//            ((AlbumMediaViewHolder) holder).mCheckLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onCheckViewClicked(item, holder);
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 4;
    }

    public void onCheckViewClicked(Item item, RecyclerView.ViewHolder holder) {
        if (mConfig.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum == -1) {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged(item);
                }
            } else {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged(item);
            }
        } else {
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                notifyCheckStateChanged(item);
            } else {
                if (assertAddSelection(holder.itemView.getContext(), item)) {
                    mSelectedCollection.add(item);
                    notifyCheckStateChanged(item);
                }
            }
        }
    }

    private void notifyCheckStateChanged(Item item) {
        List<Item> list = mSelectedCollection.asList();
        if (CollectionUtil.isEmpty(list)) {
            list = new ArrayList<>();
        }
        list.add(item);

//        for (Item i : list) {
//            int index = mItems.indexOf(i);
//            if (index != -1) {
//                notifyItemChanged(index);
//            }
//        }
        notifyDataSetChanged();
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= getItemCount() - 4) {
            return VIEW_TYPE_FOOTER;
        } else {
            return mItems.get(position).isCapture() ? VIEW_TYPE_CAPTURE : VIEW_TYPE_MEDIA;
        }
    }

    private boolean assertAddSelection(Context context, Item item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }


    public void registerCheckStateListener(CheckStateListener listener) {
        mCheckStateListener = listener;
    }

    public void unregisterCheckStateListener() {
        mCheckStateListener = null;
    }

    public void registerOnMediaClickListener(OnMediaClickListener listener) {
        mOnMediaClickListener = listener;
    }

    public void unregisterOnMediaClickListener() {
        mOnMediaClickListener = null;
    }

    public ArrayList<Item> getItems() {
        return new ArrayList<>(mItems);
    }

    public interface CheckStateListener {
        void onUpdate();
    }

    public interface OnMediaClickListener {
        void onMediaClick(int adapterPosition, Item item);
    }

    public static class DiffCallBack extends DiffUtil.Callback {
        private List<Item> mOldDataList, mNewDataList;

        public DiffCallBack(List<Item> mOldDataList, List<Item> mNewDataList) {
            this.mOldDataList = mOldDataList;
            this.mNewDataList = mNewDataList;
        }

        @Override
        public int getOldListSize() {
            return mOldDataList != null ? mOldDataList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return mNewDataList != null ? mNewDataList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(mOldDataList.get(oldItemPosition).filePath, mNewDataList.get(newItemPosition).filePath);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }
    }
}
