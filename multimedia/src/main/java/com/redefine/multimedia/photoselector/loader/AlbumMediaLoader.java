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
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.photoselector.util.MediaStoreCompat;

/**
 * Load images and videos into a single cursor.
 */
public class AlbumMediaLoader extends CursorLoader {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration",
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT};

    // === params for album ALL && showSingleMediaType: false ===
    private static final String SELECTION_ALL =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND "
                    + MediaStore.Video.VideoColumns.DURATION + ">? AND " + MediaStore.Video.VideoColumns.DURATION + "<?)";

    // ===========================================================

    // === params for album ALL && showSingleMediaType: true ===
    private static final String SELECTION_ALL_FOR_PIC_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?";

    private static final String SELECTION_ALL_FOR_VIDEO_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND ("
                    + MediaStore.Video.VideoColumns.DURATION + ">? AND " + MediaStore.Video.VideoColumns.DURATION + "<?)";

    private static String[] getSelectionArgsForPicMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    private static String[] getSelectionArgsForVideoMediaType(int mediaType, ImagePickConfig config) {
        return new String[]{String.valueOf(mediaType), String.valueOf(config.minDuration), String.valueOf(config.maxDuration)};
    }
    // =========================================================

    // === params for ordinary album && showSingleMediaType: false ===
    private static final String SELECTION_ALBUM =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND "
                    + MediaStore.Video.VideoColumns.DURATION +">? AND "+ MediaStore.Video.VideoColumns.DURATION + "<?)"
                    + " AND "
                    + " bucket_id=?";

    private static String[] getSelectionAlbumArgs(String albumId, ImagePickConfig config) {
        return new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                String.valueOf(config.minDuration),
                String.valueOf(config.maxDuration),
                albumId
        };
    }
    // ===============================================================

    // === params for ordinary album && showSingleMediaType: true ===
    private static final String SELECTION_ALBUM_FOR_PIC_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?";

    private static final String SELECTION_ALBUM_FOR_VIDEO_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + MediaStore.Video.VideoColumns.DURATION +">? AND "+ MediaStore.Video.VideoColumns.DURATION + "<?"
                    + " AND bucket_id=?";

    private static String[] getSelectionAlbumArgsForPicMediaType(int mediaType, String albumId) {
        return new String[]{String.valueOf(mediaType), albumId};
    }
    private static String[] getSelectionAlbumArgsForVideoMediaType(int mediaType, String albumId, ImagePickConfig config) {
        return new String[]{String.valueOf(mediaType), String.valueOf(config.minDuration), String.valueOf(config.maxDuration), albumId};
    }
    // ===============================================================

    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";
    private final boolean mEnableCapture;

    private AlbumMediaLoader(Context context, String selection, String[] selectionArgs, boolean capture) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
        mEnableCapture = capture;
    }

    public static CursorLoader newInstance(Context context, Album album, ImagePickConfig config) {
        String selection;
        String[] selectionArgs;
        boolean enableCapture;

        if (album.isAll()) {
            if (config.onlyShowImages()) {
                selection = SELECTION_ALL_FOR_PIC_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForPicMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else if (config.onlyShowVideos()) {
                selection = SELECTION_ALL_FOR_VIDEO_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForVideoMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, config);
            } else {
                selection = SELECTION_ALL;
                selectionArgs = getSelectionArgsForMixMediaType(config);
            }
            enableCapture = config.capture;
        } else {
            if (config.onlyShowImages()) {
                selection = SELECTION_ALBUM_FOR_PIC_MEDIA_TYPE;
                selectionArgs = getSelectionAlbumArgsForPicMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                        album.getId());
            } else if (config.onlyShowVideos()) {
                selection = SELECTION_ALBUM_FOR_VIDEO_MEDIA_TYPE;
                selectionArgs = getSelectionAlbumArgsForVideoMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                        album.getId(), config);
            } else {
                selection = SELECTION_ALBUM;
                selectionArgs = getSelectionAlbumArgs(album.getId(), config);
            }
            enableCapture = false;
        }
        return new AlbumMediaLoader(context, selection, selectionArgs, enableCapture);
    }

    private static String[] getSelectionArgsForMixMediaType(ImagePickConfig config) {
        return new String[] {
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                String.valueOf(config.minDuration),
                String.valueOf(config.maxDuration)
        };
    }

    @Override
    public Cursor loadInBackground() {
        Cursor result = super.loadInBackground();
        if (!mEnableCapture || !MediaStoreCompat.hasCameraFeature(getContext())) {
            return result;
        }
        MatrixCursor dummy = new MatrixCursor(PROJECTION);
        dummy.addRow(new Object[]{Item.ITEM_ID_CAPTURE, Item.ITEM_DISPLAY_NAME_CAPTURE, "","", 0, 0, 0, 0});
        return new MergeCursor(new Cursor[]{dummy, result});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
