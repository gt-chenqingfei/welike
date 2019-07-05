package com.redefine.multimedia.photoselector.viewholder;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.LocalImageLoader;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.model.SelectedItemCollection;
import com.redefine.welike.base.resource.ResourceTool;
import com.redefine.welike.base.util.TimeUtil;

public class AlbumMediaViewHolder extends BaseAlbumItemViewHolder {

    private final SimpleDraweeView mImageView;
    private final int mColumnSize;
    private final TextView mDuration;
    private final TextView mGifTag;
    private final TextView mLongTag;
    private final String mLongText;
    private final String mGifText;
    public final View mCheckLayout;
    public final View mHolderView;

    public AlbumMediaViewHolder(View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.image_pick_picture);
        mDuration = itemView.findViewById(R.id.image_pick_duration);
        mGifTag = itemView.findViewById(R.id.image_pick_isGif);
        mLongTag = itemView.findViewById(R.id.image_pick_long_chart);
        mCheckLayout = itemView.findViewById(R.id.image_pick_check_layout);
        mHolderView = itemView.findViewById(R.id.item_preview_image_holder);
        mLongText = ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_long_chart");
        mGifText = ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "gif_tag");
        mGifTag.setText(mGifText);
        mLongTag.setText(mLongText);
        mColumnSize = ScreenUtils.getSreenWidth(itemView.getContext()) / 4;
    }

    public void bindViews(Item item, SelectedItemCollection mSelectedCollection, ImagePickConfig config,boolean isSelectVideo) {
        LocalImageLoader.getInstance().loadFileForPicSelectorGrid(mImageView, item.getContentUri(), 100, 100);
        if (item.isVideo()) {
            if (mSelectedCollection.getmItems() == null || mSelectedCollection.getmItems().isEmpty()) {
                mHolderView.setVisibility(View.GONE);
            } else {
                mHolderView.setVisibility(!isSelectVideo ? View.VISIBLE : View.GONE);
            }
            mDuration.setVisibility(View.VISIBLE);
            mDuration.setText(TimeUtil.timeParse(item.duration));
        } else {
            if (mSelectedCollection.getmItems() == null || mSelectedCollection.getmItems().isEmpty()) {
                mHolderView.setVisibility(View.GONE);
            } else {
                mHolderView.setVisibility(isSelectVideo ? View.VISIBLE : View.GONE);
            }
            mDuration.setVisibility(View.GONE);
        }
        mGifTag.setVisibility(item.isGif() ? View.VISIBLE : View.GONE);
        mLongTag.setVisibility(View.GONE);
        setCheckStatus(item, mSelectedCollection, config);
    }

    private void setCheckStatus(Item item, SelectedItemCollection mSelectedCollection, ImagePickConfig config) {
        if (config.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(item);
            if (checkedNum > 0) {
                mCheckLayout.setVisibility(View.VISIBLE);
            } else {
                if (mSelectedCollection.maxSelectableReached(config)) {
                    mCheckLayout.setVisibility(View.GONE);
                } else {
                    mCheckLayout.setVisibility(View.GONE);
                }
            }
        } else {
            boolean selected = mSelectedCollection.isSelected(item);
            if (selected) {
                mCheckLayout.setVisibility(View.VISIBLE);
            } else {
                if (mSelectedCollection.maxSelectableReached(config)) {
                    mCheckLayout.setVisibility(View.GONE);
                } else {
                    mCheckLayout.setVisibility(View.GONE);
                }
            }
        }
    }

}
