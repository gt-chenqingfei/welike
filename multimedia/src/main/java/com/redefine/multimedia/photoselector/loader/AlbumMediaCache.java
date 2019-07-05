package com.redefine.multimedia.photoselector.loader;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.photoselector.entity.Item;

import java.util.ArrayList;
import java.util.List;

public class AlbumMediaCache {

    private List<Item> mItems = new ArrayList<>();

    private AlbumMediaCache() {

    }

    public static class AlbumMediaCacheHolder {
        private static final AlbumMediaCache INSTANCE = new AlbumMediaCache();

        private AlbumMediaCacheHolder() {

        }
    }

    public static AlbumMediaCache getInstance() {
        return AlbumMediaCacheHolder.INSTANCE;
    }

    public List<Item> getCacheItems() {
        return mItems;
    }

    public List<Item> setCacheItems(List<Item> items) {
        mItems.clear();
        if (!CollectionUtil.isEmpty(items)) {
            mItems.addAll(items);
        }
        return mItems;
    }

    public void clearCache() {
        mItems.clear();
    }
}
