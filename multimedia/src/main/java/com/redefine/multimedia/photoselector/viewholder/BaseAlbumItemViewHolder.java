package com.redefine.multimedia.photoselector.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.model.SelectedItemCollection;

public class BaseAlbumItemViewHolder extends RecyclerView.ViewHolder {

    public BaseAlbumItemViewHolder(View itemView) {
        super(itemView);
    }

    public void bindViews(Item cursor, SelectedItemCollection mSelectedCollection, ImagePickConfig mSelectionSpec,boolean isVideoSelect) {

    }
}
