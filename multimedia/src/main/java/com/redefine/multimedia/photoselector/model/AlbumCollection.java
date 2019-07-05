package com.redefine.multimedia.photoselector.model;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.entity.Album;
import com.redefine.multimedia.photoselector.loader.AlbumLoader;
import com.redefine.multimedia.photoselector.util.CursorParse;

import java.util.List;

public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private static final String STATE_CURRENT_SELECTION = "state_current_selection";
    private Context mContext;
    private LoaderManager mLoaderManager;
    private AlbumCallbacks mCallbacks;
    private int mCurrentSelection;
    private ImagePickConfig mConfig;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mContext == null || mCallbacks == null) {
            return null;
        }
        return AlbumLoader.newInstance(mContext, mConfig);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mContext == null || mCallbacks == null) {
            return;
        }

        mCallbacks.onAlbumLoad(CursorParse.parse(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mContext == null || mCallbacks == null) {
            return;
        }

        mCallbacks.onAlbumReset();
    }

    public void onCreate(Activity context, AlbumCallbacks callbacks, ImagePickConfig config) {
        mContext = context.getApplicationContext();
        mLoaderManager = context.getLoaderManager();
        mCallbacks = callbacks;
        mConfig = config;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        mCurrentSelection = savedInstanceState.getInt(STATE_CURRENT_SELECTION);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_SELECTION, mCurrentSelection);
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mCallbacks = null;
    }

    public void loadAlbums() {
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public int getCurrentSelection() {
        return mCurrentSelection;
    }

    public void setStateCurrentSelection(int currentSelection) {
        mCurrentSelection = currentSelection;
    }

    public interface AlbumCallbacks {
        void onAlbumLoad(List<Album> cursor);

        void onAlbumReset();
    }
}
