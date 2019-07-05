package com.redefine.multimedia.photoselector.model;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.entity.Album;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.loader.AlbumMediaLoader;
import com.redefine.multimedia.photoselector.util.CursorParse;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class AlbumMediaCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private static final String ARGS_ALBUM = "args_album";
    private static final String ARGS_ENABLE_CAPTURE = "args_enable_capture";
    private Context mContext;
    private LoaderManager mLoaderManager;
    private AlbumMediaCallbacks mCallbacks;
    private ImagePickConfig mConfig;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mContext == null || mCallbacks == null) {
            return null;
        }

        Album album = args.getParcelable(ARGS_ALBUM);
        if (album == null) {
            return null;
        }

        return AlbumMediaLoader.newInstance(mContext, album, mConfig);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        final List<Item> list = CursorParse.parseItems(data);
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                List<Item> result = CursorParse.filterNoneFile(list);
                if (mCallbacks != null) {
                    mCallbacks.onAlbumMediaLoad(result);
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mCallbacks != null) {
            mCallbacks.onAlbumMediaReset();
        }
    }

    public void onCreate(@NonNull Activity context, @NonNull AlbumMediaCallbacks callbacks, ImagePickConfig config) {
        mContext = context;
        mLoaderManager = context.getLoaderManager();
        mCallbacks = callbacks;
        mConfig = config;
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mCallbacks = null;
    }

    public void load(@Nullable Album target) {
        load(target, false);
    }

    public void load(@Nullable Album target, boolean enableCapture) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture);
        mLoaderManager.restartLoader(LOADER_ID, args, this);
    }

    public interface AlbumMediaCallbacks {

        void onAlbumMediaLoad(List<Item> itemList);

        void onAlbumMediaReset();
    }
}
