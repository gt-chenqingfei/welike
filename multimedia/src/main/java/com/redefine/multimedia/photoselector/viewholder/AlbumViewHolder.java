package com.redefine.multimedia.photoselector.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.redefine.commonui.fresco.loader.NineGridUrlLoader;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.entity.Album;

public class AlbumViewHolder {

    private final TextView mDisplayName;
    private final TextView mAlbumMedias;
    private final SimpleDraweeView mAlbumCover;
    private final View itemView;

    public AlbumViewHolder(View itemView) {
        this.itemView = itemView;
        mDisplayName = itemView.findViewById(R.id.album_name);
        mAlbumMedias = itemView.findViewById(R.id.album_media_count);
        mAlbumCover = itemView.findViewById(R.id.album_cover);
        itemView = itemView.findViewById(R.id.album_root_view);
    }


    public void bindViews(BaseAdapter adapter, Album data, String mSelectAlbumValue) {
        mDisplayName.setText(data.getDisplayName());
        if (TextUtils.equals(data.getDisplayName(), mSelectAlbumValue)) {
            itemView.setBackgroundResource(R.color.common_color_f8f8f8);
        } else {
            itemView.setBackgroundResource(R.color.white);
        }
        mAlbumMedias.setText(String.valueOf(data.getCount()));
        NineGridUrlLoader.getInstance().loadNineGridFile(mAlbumCover, data.getCoverPath());
    }
}
