package com.redefine.welike.business.publisher.management.cache;

import com.redefine.welike.business.publisher.management.bean.DraftBase;

import java.util.List;

public interface IDraftCacheCallback {
    void onAllDraftCallback(List<DraftBase> draftBases);
}

