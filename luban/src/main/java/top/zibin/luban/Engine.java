package top.zibin.luban;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Responsible for starting compress and managing active and cached resources.
 */
class Engine {
    private ExifInterface srcExif;
    private InputStreamProvider srcImg;
    private File tagImg;
    private int srcWidth;
    private int srcHeight;

    Engine(InputStreamProvider srcImg, File tagImg, boolean ignoreRotate) throws IOException {
        if (!ignoreRotate && Checker.SINGLE.isJPG(srcImg.getPath())) {
            this.srcExif = new ExifInterface(srcImg.open());
        }
        this.tagImg = tagImg;
        this.srcImg = srcImg;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeStream(srcImg.open(), null, options);
        this.srcWidth = options.outWidth;
        this.srcHeight = options.outHeight;
    }

    private int computeSize() {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    private Bitmap rotatingImage(Bitmap bitmap) {
        if (srcExif == null) return bitmap;

        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
        }

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    WrapSizeFile compress(int compressQuality) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize();
        WrapSizeFile sizeFile = new WrapSizeFile();

        Bitmap tagBitmap = BitmapFactory.decodeStream(srcImg.open(), null, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Bitmap rotateBitmap = rotatingImage(tagBitmap);

        if (rotateBitmap != tagBitmap) {
            tagBitmap.recycle();
        }

        sizeFile.file = tagImg;
        sizeFile.width = rotateBitmap.getWidth();
        sizeFile.height = rotateBitmap.getHeight();
        rotateBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, stream);
        rotateBitmap.recycle();

        boolean isSuccess = true;
        if (!tagImg.exists()) {
            File parent = tagImg.getParentFile();
            if (parent != null && !parent.exists()) {
                isSuccess = parent.mkdirs();
            }
            isSuccess &= tagImg.createNewFile();
        }

        if (!isSuccess) {
            return sizeFile;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tagImg);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
            stream.close();
        } finally {
            if (fos != null) {
                fos.close();
            }
            stream.close();
        }
        return sizeFile;
    }
}