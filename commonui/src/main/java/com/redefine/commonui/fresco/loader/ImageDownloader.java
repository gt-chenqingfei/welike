package com.redefine.commonui.fresco.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.redefine.foundation.io.FileManager;
import com.redefine.welike.base.ErrorCode;
import com.redefine.welike.base.io.WeLikeFileManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by nianguowang on 2018/6/11
 */
public class ImageDownloader {

    private ImageDownloader(){}

    private static class ImageDownloaderHolder {
        private static ImageDownloader INSTANCE = new ImageDownloader();
    }

    public static ImageDownloader getInstance() {
        return ImageDownloaderHolder.INSTANCE;
    }

    public interface ImageDownloadListener {
        void onSuccess(String filePath);
        void onFail(int errorCode);
    }

    public void download(Context context, String imageUrl, final ImageDownloadListener listener) {
        // 网络的URL  首先从缓存cache拿文件
        final String suffix = getLastImgType(imageUrl);
        ImageRequest imageRequest;
        imageRequest = BigPicUrlLoader.getInstance().getImageRequest(context, imageUrl, true);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, null);
        BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
        if (resource instanceof FileBinaryResource) {
            File cacheFile = ((FileBinaryResource) resource).getFile();
            if (cacheFile != null && cacheFile.exists()) {

//                File saveFile = FileManager.getInstance().getFileInAndroidCache(System.currentTimeMillis() + suffix);
                File saveFile = new File(WeLikeFileManager.getSharePicCacheDir(), System.currentTimeMillis() + suffix);
                String imagePath = copyToCacheDir(cacheFile, saveFile);
                if(!TextUtils.isEmpty(imagePath)) {
                    notifySuccess(imagePath, listener);
                    return;
                }
            }
        }
        // 在fresco的内存缓存拿去
        CacheKey bitmapCacheKey = DefaultCacheKeyFactory.getInstance().getBitmapCacheKey(imageRequest, null);
        CloseableReference<CloseableImage> bitmapCloseableReference = ImagePipelineFactory.getInstance().getBitmapMemoryCache().get(bitmapCacheKey);
        if (bitmapCloseableReference != null) {
            CloseableImage image = bitmapCloseableReference.get();
            if (image instanceof CloseableBitmap) {
                Bitmap bitmap = ((CloseableBitmap) image).getUnderlyingBitmap();
                if (bitmap != null) {
                    Bitmap copy = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
//                    File saveFile = FileManager.getInstance().getFileInAndroidCache(System.currentTimeMillis() + suffix);
                    File saveFile = new File(WeLikeFileManager.getSharePicCacheDir(), System.currentTimeMillis() + suffix);
                    String imagePath = saveBitmap(copy, saveFile);
                    if(!TextUtils.isEmpty(imagePath)) {
                        notifySuccess(imagePath, listener);
                        return;
                    }
                }
            }
        }
        DataSource<CloseableReference<PooledByteBuffer>> dataSource = ImagePipelineFactory.getInstance().getImagePipeline().fetchEncodedImage(imageRequest, null);
        dataSource.subscribe(new DataSubscriber<CloseableReference<PooledByteBuffer>>() {
            @Override
            public void onNewResult(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                if (dataSource.isFinished()) {
                    CloseableReference<PooledByteBuffer> ref = dataSource.getResult();
                    if (ref != null) {
                        PooledByteBuffer result = ref.get();
                        InputStream is = new PooledByteBufferInputStream(result);
//                        File saveFile = FileManager.getInstance().getFileInAndroidCache(System.currentTimeMillis() + suffix);
                        File saveFile = new File(WeLikeFileManager.getSharePicCacheDir(), System.currentTimeMillis() + suffix);
                        String imagePath = saveInputStream(is, saveFile);
                        if (!TextUtils.isEmpty(imagePath)) {
                            notifySuccess(imagePath, listener);
                            return;
                        }
                    }
                }

                notifyFail(ErrorCode.ERROR_NETWORK_INVALID, listener);
            }

            @Override
            public void onFailure(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                notifyFail(ErrorCode.ERROR_NETWORK_INVALID, listener);
            }

            @Override
            public void onCancellation(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                notifyFail(ErrorCode.ERROR_NETWORK_INVALID, listener);
            }

            @Override
            public void onProgressUpdate(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {

            }
        }, CallerThreadExecutor.getInstance());
    }

    private void notifySuccess(String filePath, ImageDownloadListener listener) {
        if(listener != null) {
            listener.onSuccess(filePath);
        }
    }

    private void notifyFail(int errorCode, ImageDownloadListener listener) {
        if(listener != null) {
            listener.onFail(errorCode);
        }
    }

    private String saveBitmap(Bitmap bitmap, File file) {

        if (bitmap == null) {
            return null;
        }
        FileOutputStream out = null;
        try {
            checkFile(file);
            out = new FileOutputStream(file);
            if(!bitmap.isRecycled()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
                out.flush();
                bitmap.recycle();
            }
            return file.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
            // do nothing
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    private String saveInputStream(InputStream is, File destFile) {
        if(is == null) {
            return null;
        }
        FileOutputStream outputStream = null;
        try {
            checkFile(destFile);
            outputStream = new FileOutputStream(destFile);
            byte[] data = new byte[1024];
            while (is.read(data) != -1) {
                outputStream.write(data);
            }
            close(is);
            close(outputStream);
            return destFile.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(is);
            close(outputStream);
        }
        return null;
    }

    public String copyToCacheDir(File source, File target) {
        if (source == null || target == null || !source.exists()) {
            return null;
        }

        //获得原文件流
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            File parent = target.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean isSuccess = parent.mkdirs();
                if (!isSuccess) {
                    return null;
                }
            }
            if (!target.exists()) {
                boolean isSuccess = target.createNewFile();
                if (!isSuccess) {
                    return null;
                }
            }
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            byte[] data = new byte[1024];
            while (inputStream.read(data) != -1) {
                outputStream.write(data);
            }
            close(inputStream);
            close(outputStream);
            return target.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(outputStream);
        }
        return null;
    }

    private boolean checkFile(File file) throws Exception{
        File parent = file.getParentFile();
        if(!parent.exists()) {
            if(!parent.mkdirs()) {
                return false;
            }
        }
        if(file.exists()) {
            if(!file.delete()) {
                return false;
            }
        }
        return file.createNewFile();
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

    private String getLastImgType(String path) {
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
}
