package com.redefine.welike.base.uploading;

import android.text.TextUtils;

import com.redefine.welike.base.StorageDBStore;
import com.redefine.welike.base.dao.storage.DaoSession;
import com.redefine.welike.base.dao.storage.Upload;
import com.redefine.welike.base.dao.storage.UploadDao;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by liubin on 2018/1/22.
 */

public class UploadStatusStorage {
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private static class UploadStatusStorageHolder {
        public static UploadStatusStorage instance = new UploadStatusStorage();
    }

    private UploadStatusStorage() {}

    public static UploadStatusStorage getInstance() { return UploadStatusStorageHolder.instance; }

    public String getMultiPartStatus(String sign) {
        String uploadId = null;
        DaoSession daoSession = StorageDBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            UploadDao uploadDao = daoSession.getUploadDao();
            if (uploadDao != null) {
                Upload upload = null;
                readWriteLock.readLock().lock();
                try {
                    upload = uploadDao.queryBuilder().where(UploadDao.Properties.Sign.eq(sign)).build().unique();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    readWriteLock.readLock().unlock();
                }
                if (upload != null) {
                    uploadId = upload.getUploadid();
                }
            }
        }
        return uploadId;
    }

    public void putMultiPartStatus(String sign, String uploadId) {
        if (TextUtils.isEmpty(sign) || TextUtils.isEmpty(uploadId)) return;

        DaoSession daoSession = StorageDBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            UploadDao uploadDao = daoSession.getUploadDao();
            if (uploadDao != null) {
                Upload upload = new Upload();
                upload.setSign(sign);
                upload.setUploadid(uploadId);
                readWriteLock.writeLock().lock();
                try {
                    uploadDao.insert(upload);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        }
    }

    public void removeMultiPartStatus(String sign) {
        if (TextUtils.isEmpty(sign)) return;

        DaoSession daoSession = StorageDBStore.getInstance().getDaoSession();
        if (daoSession != null) {
            UploadDao uploadDao = daoSession.getUploadDao();
            if (uploadDao != null) {
                readWriteLock.writeLock().lock();
                try {
                    uploadDao.deleteByKey(sign);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        }
    }

}
