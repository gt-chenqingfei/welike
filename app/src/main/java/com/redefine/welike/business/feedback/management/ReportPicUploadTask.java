package com.redefine.welike.business.feedback.management;

import android.content.Context;

import com.redefine.foundation.utils.CollectionUtil;
import com.redefine.multimedia.photoselector.entity.Item;
import com.redefine.welike.base.io.WeLikeFileManager;
import com.redefine.welike.base.uploading.UploadingManager;
import com.redefine.welike.business.feedback.management.bean.ReportModel;
import com.redefine.welike.business.publisher.management.bean.DraftPicAttachment;
import com.redefine.welike.business.publisher.management.bean.PostAttachmentTrans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.WrapSizeFile;

/**
 * Created by nianguowang on 2018/10/15
 */
public class ReportPicUploadTask implements PostAttachmentTrans.PostAttachmentTransCallback {

    private ReportModel mReportModel;
    private Context mContext;
    private ReportPicUploadCallback mCallback;

    private final Map<String, PostAttachmentTrans> picCompressTransArr = new ConcurrentHashMap<>();

    public ReportPicUploadTask(ReportModel model, Context context, ReportPicUploadCallback callback) {
        this.mReportModel = model;
        this.mCallback = callback;
        this.mContext = context;
    }

    @Override
    public void onPostAttachmentProcess(String attachmentId, float process) {

    }

    @Override
    public void onPostAttachmentSave(String attachmentId) {

    }

    @Override
    public void onPostAttachmentCompleted(String attachmentId) {
        PostAttachmentTrans postAttachmentTrans = picCompressTransArr.get(attachmentId);
        String url = postAttachmentTrans.getUrl();
        List<String> images = mReportModel.getImages();
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(url);
        mReportModel.setImages(images);
        picCompressTransArr.remove(attachmentId);
        if (picCompressTransArr.size() == 0) {
            if (mCallback != null) {
                mCallback.onUploadComplete(mReportModel);
            }
        }
    }

    @Override
    public void onPostAttachmentFailed(String attachmentId) {
        if (mCallback != null) {
            mCallback.onUploadFail(mReportModel);
        }
    }

    public interface ReportPicUploadCallback {
        void onUploadComplete(ReportModel model);
        void onUploadFail(ReportModel model);
    }

    public void upload() {
        if (mContext == null || mReportModel == null) {
            return;
        }
        final List<Item> uploadImages = mReportModel.getUploadImages();
        if (CollectionUtil.isEmpty(uploadImages)) {
            if (mCallback != null) {
                mCallback.onUploadComplete(mReportModel);
            }
        } else {
            Schedulers.newThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    for (Item uploadImage : uploadImages) {
                        try {
                            WrapSizeFile wrapSizeFile = Luban.with(mContext)
                                    .setTargetDir(WeLikeFileManager.getTempCacheDir().getAbsolutePath())
                                    .setCompressQuality(80)
                                    .ignoreBy(0).get(uploadImage.filePath);
                            DraftPicAttachment picAttachmentDraft = new DraftPicAttachment(wrapSizeFile.getAbsolutePath());
                            picAttachmentDraft.setUploadLocalFileName(wrapSizeFile.getAbsolutePath());
                            picAttachmentDraft.setWidth(wrapSizeFile.width);
                            picAttachmentDraft.setHeight(wrapSizeFile.height);
                            PostAttachmentTrans trans = new PostAttachmentTrans(picAttachmentDraft, UploadingManager.UPLOAD_TYPE_IMG,false, ReportPicUploadTask.this);
                            picCompressTransArr.put(trans.getAttachmentId(), trans);
                            trans.start();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
