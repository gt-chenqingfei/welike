package com.redefine.foundation.io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liwenbo on 2018/3/12.
 */

public class FileStreamManager {
    public byte[] readFile(File file) {
        FileInputStream inputStream = null;
        byte[] bytes = null;
        try {

            inputStream = new FileInputStream(file);
            int length = inputStream.available();
            bytes = new byte[length];
            int count = inputStream.read(bytes);
            close(inputStream);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
        }
        return bytes;
    }

    public boolean writeFile(File file, byte[] bytes) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            close(outputStream);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(outputStream);
        }
        return false;
    }

    public boolean deleteFiles(File file) {
        if (file == null) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        return deleteDirWithFile(file);
    }

    private boolean deleteDirWithFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return dir.delete();
        }
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDirWithFile(file);
            }
        }
        return true;
    }

    public void close(Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Throwable e) {
                e.printStackTrace();
                // do nothing
            }
        }
    }

    public boolean writeBitmap(File file, Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }
        FileOutputStream out = null;
        try {
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                return false;
            }
            if (!file.exists() && !file.createNewFile()) {
                return false;
            }
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return true;
        } catch (Throwable e) {
            // do nothing
        } finally {
            close(out);
        }
        return false;
    }

    public boolean saveFrontCameraBitmap(File file, byte[] data) {
        if (data == null) {
            return false;
        }
        File tempFile = new File(file.getAbsolutePath() + ".tmp");
        try {
            writeFile(tempFile, data);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap realBitmap = bitmap;
            try {
                realBitmap = rotatingFrontImage(tempFile.getAbsolutePath(), bitmap);
            } catch (Throwable e) {
                // do nothing
            }
            if (bitmap != realBitmap) {
                bitmap.recycle();
            }
            writeBitmap(file, realBitmap);
            tempFile.deleteOnExit();
            return true;
        } catch (Throwable e) {
            // do nothing
        }
        return false;
    }

    public boolean saveBackgroundCameraBitmap(File file, byte[] data) {
        if (data == null) {
            return false;
        }
        File tempFile = new File(file.getAbsolutePath() + ".tmp");
        try {
            writeFile(tempFile, data);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap realBitmap = bitmap;
            try {
                realBitmap = rotatingBackgroundImage(tempFile.getAbsolutePath(), bitmap);
            } catch (Throwable e) {
                // do nothing
            }
            if (bitmap != realBitmap) {
                bitmap.recycle();
            }
            writeBitmap(file, realBitmap);
            tempFile.deleteOnExit();
            return true;
        } catch (Throwable e) {
            // do nothing
        }
        return false;
    }

    private Bitmap rotatingBackgroundImage(String fileName, Bitmap bitmap) throws IOException {
        ExifInterface srcExif = new ExifInterface(fileName);

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
        matrix.postRotate( angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private Bitmap rotatingFrontImage(String fileName, Bitmap bitmap) throws IOException {
        ExifInterface srcExif = new ExifInterface(fileName);

        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 90;
                break;
        }
        matrix.postScale(-1, 1);//镜像水平翻转
        matrix.postRotate( angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public File copyFileToFile(File source, File target) {
        if (source == null || target == null || !source.exists()) {
            return target;
        }

        //获得原文件流
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            File parent = target.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean isSuccess = parent.mkdirs();
                if (!isSuccess) {
                    return target;
                }
            }
            if (!target.exists()) {
                boolean isSuccess = target.createNewFile();
                if (!isSuccess) {
                    return target;
                }
            }
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            byte[] data = new byte[1024];
//            while (inputStream.read(data) != -1) {
//                outputStream.write(data);
//            }
            int len;
            while ((len = inputStream.read(data)) > 0) {
                outputStream.write(data, 0, len);
            }
            close(inputStream);
            close(outputStream);
            return target;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(outputStream);
        }
        return target;
    }

    public boolean writeBitmap(File file, Bitmap bitmap, boolean isRecycleBitmap) {
        if (bitmap == null) {
            return false;
        }
        FileOutputStream out = null;
        try {
            boolean isSuccess = file.createNewFile();
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            if (isRecycleBitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return true;
        } catch (Throwable e) {
            // do nothing
        } finally {
            close(out);
        }
        return false;
    }

    public boolean writeStream(File file, InputStream stream) {

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            byte[] data = new byte[1024];
            while (stream.read(data) != -1) {
                outputStream.write(data);
            }
            close(stream);
            close(outputStream);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(stream);
            close(outputStream);
        }
        return false;
    }

    public Bitmap readBitmap(File result) {

        try {
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(result.getAbsolutePath());
            return bitmap;
        } catch (Throwable e) {
            // do nothing
        }
        return null;
    }
}
