package com.redefine.multimedia.picturelooker.manager;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.redefine.commonui.enums.PageStatusEnum;
import com.redefine.commonui.fresco.loader.BigPicUrlLoader;
import com.redefine.foundation.utils.ScreenUtils;
import com.redefine.multimedia.R;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.multimedia.picturelooker.config.PictureMimeType;
import com.redefine.multimedia.picturelooker.dialog.CustomDialog;
import com.redefine.multimedia.picturelooker.dialog.PictureDialog;
import com.redefine.commonui.watermark.WaterMarkUtil;
import com.redefine.welike.base.GlobalConfig;
import com.redefine.welike.base.constant.PermissionRequestCode;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.resource.ResourceTool;

import java.io.File;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by honglin on 2018/6/4.
 */

public class PhotoPreviewViewModel extends AndroidViewModel {

    private static final String PHOTO_PATH_PREFIX = "Welike_";

    private MutableLiveData<PageStatusEnum> mPageStatus = new MutableLiveData<>();

    private Activity context;

    private Item oldItem;
    private String nickName;

    public PhotoPreviewViewModel(@NonNull Application application) {
        super(application);
    }

    public void setParams(Activity context, String nickName) {
        this.context = context;
        this.nickName = nickName;
    }

    public void setOldItem(Item oldItem) {
        this.oldItem = oldItem;
    }

    public MutableLiveData<PageStatusEnum> getPageStatus() {
        return mPageStatus;
    }

    public void download() {

        if (oldItem == null || TextUtils.isEmpty(oldItem.filePath)) return;
        mPageStatus.postValue(PageStatusEnum.LOADING);
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                boolean isHttp = PictureMimeType.isHttp(oldItem.filePath);
                final String suffix = PictureMimeType.getLastImgType(oldItem.filePath);
//                boolean eqLongImg = PictureMimeType.isLongImg(oldItem.width, oldItem.height, context);
                if (isHttp) {
                    // 网络的URL  首先从缓存cache拿文件
                    ImageRequest imageRequest;
//                    if (eqLongImg)
//                        imageRequest = BigPicUrlLoader.getInstance().getImageLargeRequest(context, oldItem.filePath, oldItem.width, oldItem.height, true);
//                    else
                    imageRequest = BigPicUrlLoader.getInstance().getImageRequest(context, oldItem.filePath, true);
                    CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, null);
                    BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
                    if (resource instanceof FileBinaryResource) {
                        File cacheFile = ((FileBinaryResource) resource).getFile();
                        if (cacheFile != null && cacheFile.exists()) {

                            final File result = WeLikeFileManager.copyToPhotoSaveDir(cacheFile, PHOTO_PATH_PREFIX + System.currentTimeMillis() + suffix);
                            if (result != null && result.exists() && !TextUtils.equals(cacheFile.getAbsolutePath(), result.getAbsolutePath())) {
                                // 保存成功
                                savePhotoSuccess(result);
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
                                final File result = WeLikeFileManager.saveToPhotoSaveDir(bitmap, PHOTO_PATH_PREFIX + System.currentTimeMillis() + suffix, false);
                                if (result != null && result.exists()) {
                                    // 保存成功
                                    savePhotoSuccess(result);
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
                                    final File saveFile = WeLikeFileManager.saveToPhotoSaveDir(is, PHOTO_PATH_PREFIX + System.currentTimeMillis() + suffix);
                                    if (saveFile != null && saveFile.exists()) {
//                                            // 保存成功
                                        savePhotoSuccess(saveFile);
                                        return;
                                    }
                                }
                            }

                            savePhotoFailed();
                        }

                        @Override
                        public void onFailure(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                            savePhotoFailed();
                        }

                        @Override
                        public void onCancellation(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                            savePhotoFailed();
                        }

                        @Override
                        public void onProgressUpdate(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {

                        }
                    }, CallerThreadExecutor.getInstance());

                } else {
                    File cacheFile = new File(oldItem.filePath);
                    final File result = WeLikeFileManager.copyToPhotoSaveDir(cacheFile, System.currentTimeMillis() + suffix);
                    if (result != null && result.exists() && !TextUtils.equals(cacheFile.getAbsolutePath(), result.getAbsolutePath())) {
                        savePhotoSuccess(result);
                    } else {
                        savePhotoFailed();
                    }

                }
            }
        });
    }

    private void savePhotoFailed() {
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                onSaveFailed();
            }
        });
    }

    private void savePhotoSuccess(final File result) {
        // do water mark
        boolean isSuccess = WaterMarkUtil.doWaterMark(getApplication(), result, GlobalConfig.AT + nickName)
                || WaterMarkUtil.doWaterMark(getApplication(), result, GlobalConfig.AT + nickName);
//
        if (isSuccess) {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    onSavePhotoSuccess(context, result);
                }
            });
        } else {
            savePhotoFailed();
        }
        notifySystemMedia(result);
    }

    private void notifySystemMedia(File file) {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSaveFailed() {
        showToast(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_save_error"));
        oldItem = null;
        mPageStatus.postValue(PageStatusEnum.CONTENT);
    }

    private void onSavePhotoSuccess(final Context context, File result) {
        WeLikeFileManager.refreshGallery(context, result.getAbsolutePath());
        showToast(ResourceTool.getString(ResourceTool.ResourceFileEnum.PIC_SELECTOR, "picture_save_success") + "\n" + result.getAbsolutePath());
        mPageStatus.postValue(PageStatusEnum.CONTENT);
        oldItem = null;
    }


    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
