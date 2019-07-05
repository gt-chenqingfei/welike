package com.redefine.multimedia.photoselector.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.entity.Album;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.entity.SelectionSpec;
import com.redefine.multimedia.photoselector.model.AlbumCollection;
import com.redefine.multimedia.photoselector.model.AlbumMediaCollection;
import com.redefine.multimedia.photoselector.model.SelectedItemCollection;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class ImagePickViewModel extends AndroidViewModel implements AlbumCollection.AlbumCallbacks, AlbumMediaCollection.AlbumMediaCallbacks {

    private final AlbumCollection mAlbumCollection = new AlbumCollection();
    private final SelectedItemCollection mSelectedCollection;
    private final AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
    private MutableLiveData<List<Item>> mediatorLiveData = new MediatorLiveData<>();
    private MutableLiveData<List<Album>> albumLiveData = new MediatorLiveData<>();

    public ImagePickViewModel(@NonNull Application application) {
        super(application);
        mSelectedCollection = new SelectedItemCollection(application);
    }


    public void onCreate(Activity activity, Bundle savedInstanceState, ImagePickConfig config) {

        mAlbumCollection.onCreate(activity, this, config);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
    }

    @Override
    public void onAlbumLoad(final List<Album> albums) {
        albumLiveData.postValue(albums);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mAlbumCollection.onDestroy();
        mAlbumMediaCollection.onDestroy();
    }

    @Override
    public void onAlbumReset() {

    }

    public AlbumCollection getAlbumCollection() {
        return mAlbumCollection;
    }

    public SelectedItemCollection getSelectedCollection() {
        return mSelectedCollection;
    }

    public void onLoadAlbumMedias(Activity activity, Album album, ImagePickConfig config) {

        mAlbumMediaCollection.onCreate(activity, this, config);
        mAlbumMediaCollection.load(album, config.capture && album.isAll());
    }

    @Override
    public void onAlbumMediaLoad(List<Item> items) {
        mediatorLiveData.postValue(items);
    }

    @Override
    public void onAlbumMediaReset() {
        mediatorLiveData.postValue(null);
    }

    public MutableLiveData<List<Item>> getAlbumMediaCursor() {
        return mediatorLiveData;
    }

    public MutableLiveData<List<Album>> getAlbumCursor() {
        return albumLiveData;
    }
}
