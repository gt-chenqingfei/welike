package com.redefine.multimedia.photoselector.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.entity.Album;
import com.redefine.multimedia.photoselector.viewholder.AlbumViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends BaseAdapter {

    private String mSelectAlbumValue;
    private LayoutInflater mLayoutInflater;
    private final List<Album> mAlbums = new ArrayList<>();

    public void setAlbums(List<Album> albums) {
        mAlbums.clear();
        if (!CollectionUtil.isEmpty(albums)) {
            mAlbums.addAll(albums);
        }
        notifyDataSetChanged();
    }

    public void setmSelectAlbumValue(String mSelectAlbumValue) {
        this.mSelectAlbumValue = mSelectAlbumValue;
    }

    @Override
    public int getCount() {
        return mAlbums.size();
    }

    @Override
    public Object getItem(int position) {
        return mAlbums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        AlbumViewHolder albumViewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.album_list_item, parent, false);
            albumViewHolder = new AlbumViewHolder(convertView);
            convertView.setTag(albumViewHolder);
        } else {
            albumViewHolder = (AlbumViewHolder) convertView.getTag();
        }
        albumViewHolder.bindViews(this, mAlbums.get(position),mSelectAlbumValue);
        return convertView;
    }
}
