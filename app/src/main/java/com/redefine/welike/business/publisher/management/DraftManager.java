package com.redefine.welike.business.publisher.management;

import android.arch.lifecycle.LiveData;

import com.redefine.welike.business.publisher.management.bean.DraftBase;
import com.redefine.welike.business.publisher.management.bean.DraftPicAttachment;
import com.redefine.welike.business.publisher.management.bean.DraftPost;
import com.redefine.welike.business.publisher.management.bean.DraftVideoAttachment;
import com.redefine.welike.business.publisher.management.cache.DraftCacheWrapper;
import com.redefine.welike.business.publisher.management.cache.IDraftCountCallback;
import com.redefine.welike.business.publisher.room.Draft;
import com.redefine.welike.business.publisher.room.DraftCount;

import java.util.Date;
import java.util.List;

/**
 * Created by liubin on 2018/3/15.
 */

public class DraftManager {

    private static class DraftManagerHolder {
        public static DraftManager instance = new DraftManager();
    }

    private DraftManager() {
    }

    public static DraftManager getInstance() {
        return DraftManagerHolder.instance;
    }

    public void init() {
//        draftCache.reset();
        DraftCacheWrapper.Companion.getInstance().init();
    }

    public void insertOrUpdate(DraftBase draft) {
        if (!draft.isSaveDB()) {
            return;
        }
        draft.setTime(new Date().getTime());
//        draftCache.insertOrUpdate(draft);

        DraftCacheWrapper.Companion.getInstance().insertOrUpdate(draft);
    }

    public void resetDraftUncompletedResource(DraftBase draft) {
        if (draft instanceof DraftPost) {
            DraftPost postDraft = (DraftPost) draft;
            List<DraftPicAttachment> picAttachmentDraftList = postDraft.getPicDraftList();
            if (picAttachmentDraftList != null && picAttachmentDraftList.size() > 0) {
                for (DraftPicAttachment pic : picAttachmentDraftList) {
                    pic.setObjectKey(null);
                }
            }
            DraftVideoAttachment videoAttachmentDraft = postDraft.getVideo();
            if (videoAttachmentDraft != null) {
                videoAttachmentDraft.setObjectKey(null);
            }
            DraftPicAttachment videoThumb = postDraft.getVideoThumb();
            if (videoThumb != null) {
                videoThumb.setObjectKey(null);
            }
        }
    }

    public void delete(DraftBase draft) {

//        draftCache.delete(draft);
        DraftCacheWrapper.Companion.getInstance().delete(draft);
    }

    public void clearAll() {
//        draftCache.deleteAll();
        DraftCacheWrapper.Companion.getInstance().clear();
    }

    public void clear() {
//        draftCache.clear();
        DraftCacheWrapper.Companion.getInstance().clear();
    }

    public LiveData<List<Draft>> listDrafts() {
//        draftCache.listAllShowable(cacheCallback);

        return DraftCacheWrapper.Companion.getInstance().getAllDrafts();
    }

    public void getDraftCount(IDraftCountCallback countCallback) {
//        draftCache.countAllShowable(countCallback);
        DraftCacheWrapper.Companion.getInstance().getDraftsCount(countCallback);
    }

    public LiveData<DraftCount> getDraftCount1() {
        return DraftCacheWrapper.Companion.getInstance().getDraftCount1();
    }

}
