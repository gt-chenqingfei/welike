package com.redefine.multimedia.photoselector.entity;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.redefine.multimedia.photoselector.util.PhotoMetadataUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * MIME Type enumeration to restrict selectable media on the selection activity. Matisse only supports images and
 * videos.
 * <p>
 * Good example of mime types Android supports:
 * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java
 */
public enum MimeType {

    // ============== images ==============
    IMAGE("image/*", arraySetOf(
            "jpg",
            "jpeg"
    )),
    JPEG("image/jpeg", arraySetOf(
            "jpg",
            "jpeg"
    )),
    PNG("image/png", arraySetOf(
            "png"
    )),
    GIF("image/gif", arraySetOf(
            "gif"
    )),
    BMP("image/x-ms-bmp", arraySetOf(
            "bmp"
    )),
    WEBP("image/webp", arraySetOf(
            "webp"
    )),

    // ============== videos ==============
    MPEG("video/mpeg", arraySetOf(
            "mpeg",
            "mpg"
    )),
    MP4("video/mp4", arraySetOf(
            "mp4",
            "m4v"
    )),
    QUICKTIME("video/quicktime", arraySetOf(
            "mov"
    )),
    THREEGPP("video/3gpp", arraySetOf(
            "3gp",
            "3gpp"
    )),
    THREEGPP2("video/3gpp2", arraySetOf(
            "3g2",
            "3gpp2"
    )),
    MKV("video/x-matroska", arraySetOf(
            "mkv"
    )),
    WEBM("video/webm", arraySetOf(
            "webm"
    )),
    TS("video/mp2ts", arraySetOf(
            "ts"
    )),
    AVI("video/avi", arraySetOf(
            "avi"
    ));

    private final String mMimeTypeName;
    private final Set<String> mExtensions;

    MimeType(String mimeTypeName, Set<String> extensions) {
        mMimeTypeName = mimeTypeName;
        mExtensions = extensions;
    }

    public static EnumSet<MimeType> ofAll() {
        return EnumSet.allOf(MimeType.class);
    }

    public static EnumSet<MimeType> of(MimeType type, MimeType... rest) {
        return EnumSet.of(type, rest);
    }

    public static EnumSet<MimeType> ofImage() {
        return EnumSet.of(IMAGE, JPEG, PNG, GIF, BMP, WEBP);
    }

    public static EnumSet<MimeType> ofStaticImage() {
        return EnumSet.of(IMAGE, JPEG, PNG, GIF, BMP);
    }

    public static EnumSet<MimeType> ofVideo() {
        return EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI);
    }

    private static Set<String> arraySetOf(String... suffixes) {
        return new ArraySet<>(Arrays.asList(suffixes));
    }

    @Override
    public String toString() {
        return mMimeTypeName;
    }

    public boolean checkType(ContentResolver resolver, Uri uri) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        if (uri == null) {
            return false;
        }
        String type = map.getExtensionFromMimeType(resolver.getType(uri));
        String path = null;
        // lazy load the path and prevent resolve for multiple times
        boolean pathParsed = false;
        for (String extension : mExtensions) {
            if (extension.equals(type)) {
                return true;
            }
            if (!pathParsed) {
                // we only resolve the path for one time
                path = PhotoMetadataUtils.getPath(resolver, uri);
                if (!TextUtils.isEmpty(path)) {
                    path = path.toLowerCase(Locale.US);
                }
                pathParsed = true;
            }
            if (path != null && path.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGif(String thumbUrl) {
        if (TextUtils.isEmpty(thumbUrl)) {
            return false;
        }
        if (thumbUrl.endsWith("/")) {
            thumbUrl = thumbUrl.substring(0, thumbUrl.length() - 1);
        }
        if (TextUtils.isEmpty(thumbUrl)) {
            return false;
        }
        for (String extension : GIF.mExtensions) {
            if (thumbUrl.endsWith(extension.toLowerCase()) || thumbUrl.endsWith(extension.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWebP(String thumbUrl) {
        if (TextUtils.isEmpty(thumbUrl)) {
            return false;
        }
        if (thumbUrl.endsWith("/")) {
            thumbUrl = thumbUrl.substring(0, thumbUrl.length() - 1);
        }
        if (TextUtils.isEmpty(thumbUrl)) {
            return false;
        }
        for (String extension : WEBP.mExtensions) {
            if (thumbUrl.endsWith(extension.toLowerCase()) || thumbUrl.endsWith(extension.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取图片后缀
     *
     * @param path
     * @return
     */
    public static String getLastImgType(String path) {
        try {
            String[] urlArray = path.split("/");
            String fileName = urlArray[urlArray.length - 1];
            int index = fileName.lastIndexOf(".");
            if (index > 0) {
                String imageType = fileName.substring(index, fileName.length());
                imageType = TextUtils.isEmpty(imageType) ? ".png" : imageType.toLowerCase();
                switch (imageType) {
                    case ".png":
                    case ".jpg":
                    case ".jpeg":
                    case ".bmp":
                    case ".gif":
                    case ".webp":
                        return imageType;
                    default:
                        return ".png";
                }
            } else {
                return ".png";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ".png";
        }
    }

    /**
     * 获取图片后缀
     *
     * @param path
     * @return
     */
    public static String getLastVideoType(String path) {
        try {
            String[] urlArray = path.split("/");
            String fileName = urlArray[urlArray.length - 1];
            int index = fileName.lastIndexOf(".");
            if (index > 0) {
                String imageType = fileName.substring(index, fileName.length());
                imageType = TextUtils.isEmpty(imageType) ? ".mp4" : imageType.toLowerCase();
                switch (imageType) {
                    case ".mp4":
                    case ".mpeg":
                    case ".mpg":
                    case ".m4v":
                    case ".mov":
                    case ".3gp":
                    case ".3gpp":
                    case ".3g2":
                    case ".3gpp2":
                    case ".mkv":
                    case ".webm":
                    case ".ts":
                    case ".avi":
                        return imageType;
                    default:
                        return ".mp4";
                }
            } else {
                return ".mp4";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ".mp4";
        }
    }
}
