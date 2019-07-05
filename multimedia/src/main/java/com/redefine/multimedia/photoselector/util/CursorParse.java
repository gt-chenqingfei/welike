package com.redefine.multimedia.photoselector.util;

import android.database.Cursor;
import android.text.TextUtils;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.photoselector.entity.Album;
import com.redefine.multimedia.photoselector.entity.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CursorParse {
    public static List<Album> parse(Cursor data) {
        List<Album> albums = new ArrayList<>();
        if (data != null) {
            int count = data.getCount();
            if (count > 0) {
                data.moveToFirst();
                do {

                    Album album = Album.valueOf(data);
                    albums.add(album);
                } while (data.moveToNext());
            }
        }
        return albums;
    }

    public static List<Item> parseItems(Cursor data) {
        List<Item> items = new ArrayList<>();
        if (data != null) {
            int count = data.getCount();
            if (count > 0) {
                data.moveToFirst();
                do {

                    Item album = Item.valueOf(data);
                    items.add(album);
                } while (data.moveToNext());
            }
        }
        return items;
    }

    public static List<Item> filterNoneFile(List<Item> list) {
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        List<Item> result = new ArrayList<>();
        for (Item item : list) {
            if (item.isCapture()) {
                result.add(item);
                continue;
            }
            if (!TextUtils.isEmpty(item.filePath) && new File(item.filePath).exists()) {
                result.add(item);
            }
        }
        return result;
    }
}
