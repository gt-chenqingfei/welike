package com.redefine.multimedia.photoselector.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.redefine.multimedia.photoselector.config.ImagePickConfig;
import com.redefine.multimedia.photoselector.entity.Album;

/**
 * Load all albums (grouped by bucket_id) into a single cursor.
 */
public class AlbumLoader extends CursorLoader {
    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            COLUMN_COUNT};
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT};

    // === params for showSingleMediaType: false ===
    private static final String SELECTION =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND "
                    + MediaStore.Video.VideoColumns.DURATION + ">? AND " + MediaStore.Video.VideoColumns.DURATION + "<?)"
                    + ") GROUP BY (bucket_id";

    // =============================================

    // === params for showSingleMediaType: true ===
    private static final String SELECTION_FOR_PIC_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + ") GROUP BY (bucket_id";

    private static final String SELECTION_FOR_VIDEO_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND ("
                    + MediaStore.Video.VideoColumns.DURATION + ">? AND " + MediaStore.Video.VideoColumns.DURATION + "<?)"
                    + " GROUP BY (bucket_id";

    // =============================================

    private static final String BUCKET_ORDER_BY = "datetaken DESC";

    private AlbumLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, BUCKET_ORDER_BY);
    }

    public static CursorLoader newInstance(Context context, ImagePickConfig config) {
        String selection;
        String[] selectionArgs;
        if (config.onlyShowImages()) {
            selection = SELECTION_FOR_PIC_MEDIA_TYPE;
            selectionArgs = getSelectionArgsForPicMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
        } else if (config.onlyShowVideos()) {
            selection = SELECTION_FOR_VIDEO_MEDIA_TYPE;
            selectionArgs = getSelectionArgsForVideoMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, config);
        } else {
            selection = SELECTION;
            selectionArgs = getSelectionArgsForMixedMediaType(config);
        }
        return new AlbumLoader(context, selection, selectionArgs);
    }

    private static String[] getSelectionArgsForMixedMediaType(ImagePickConfig config) {
        return new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                String.valueOf(config.minDuration),
                String.valueOf(config.maxDuration)
        };

    }

    private static String[] getSelectionArgsForVideoMediaType(int mediaType, ImagePickConfig config) {
        return new String[]{String.valueOf(mediaType), String.valueOf(config.minDuration), String.valueOf(config.maxDuration)};
    }

    private static String[] getSelectionArgsForPicMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);
        int totalCount = 0;
        String allAlbumCoverPath = "";
        if (albums != null) {
            while (albums.moveToNext()) {
                totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT));
            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
            }
        }
        allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumCoverPath,
                String.valueOf(totalCount)});

        return new MergeCursor(new Cursor[]{allAlbum, albums});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}